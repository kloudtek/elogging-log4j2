package com.kloudtek.elogging.log4j2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.message.MapMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

class ELJsonLayoutTest {
    private static final Marker SQL_MARKER = MarkerManager.getMarker("SQL");
    private static final Marker UPDATE_MARKER = MarkerManager.getMarker("SQL_UPDATE").setParents(SQL_MARKER);
    private static LoggerContext logCtx;
    private static ELMemoryAppender appender;

    @BeforeAll
    private static void init() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setPackages(ELJsonLayout.class.getPackage().getName());
        builder.setStatusLevel(Level.ERROR);
        builder.setConfigurationName("BuilderTest");
        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL)
                .addAttribute("level", Level.DEBUG));
        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "ELMemoryAppender");
        appenderBuilder.add(builder.newLayout("ELJsonLayout"));
        appenderBuilder.add(builder.newFilter("MarkerFilter", Filter.Result.DENY, Filter.Result.NEUTRAL)
                .addAttribute("marker", "FLOW"));
        builder.add(appenderBuilder);
        builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG)
                .add(builder.newAppenderRef("Stdout")).addAttribute("additivity", false));
        builder.add(builder.newRootLogger(Level.ERROR).add(builder.newAppenderRef("Stdout")));
        logCtx = Configurator.initialize(builder.build());
        appender = (ELMemoryAppender) logCtx.getRootLogger().getAppenders().values().iterator().next();
    }

    @BeforeEach
    public void clear() {
        appender.clear();
    }

    @Test
    public void testLog() throws Exception {
        logCtx.getLogger("Hello").info("WORLD");
        String logEntry = getSingleLogEntry();
        Assertions.assertEquals("{\"loggerName\":\"Hello\",\"loggerFqcn\":\"org.apache.logging.log4j.spi.AbstractLogger\",\"threadName\":\"main\",\"level\":\"INFO\",\"message\":\"WORLD\",\"timestamp\":\"[TIMESTAMP]\"}\n",logEntry);
    }

    @Test
    public void testJonLogMessage() throws Exception {
        HashMap<String,String> stuff = new HashMap<>();
        stuff.put("foo","bar");
        stuff.put("a.b.c","2345\n\"--~");
        MapMessage mapMessage = new MapMessage(stuff);
        logCtx.getLogger("Hello").log(Level.INFO, mapMessage);
        String logEntry = getSingleLogEntry();
        Assertions.assertEquals("{\"loggerName\":\"Hello\",\"loggerFqcn\":\"org.apache.logging.log4j.spi.AbstractLogger\",\"threadName\":\"main\",\"level\":\"INFO\",\"a.b.c\":\"2345\\n\\\"--~\",\"foo\":\"bar\",\"timestamp\":\"[TIMESTAMP]\"}\n",logEntry);
    }

    private String getSingleLogEntry() {
        ArrayList<String> events = appender.getEvents();
        Assertions.assertEquals(1,events.size());
        return events.iterator().next().replaceAll("\"timestamp\":\".*?\"","\"timestamp\":\"[TIMESTAMP]\"");
    }
}