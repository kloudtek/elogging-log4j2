package com.kloudtek.elogging.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;

@Plugin(name = "ELMemoryAppender", category = "Core", elementType = "layout")
public class ELMemoryAppender extends AbstractAppender {
    private ArrayList<String> events = new ArrayList<>();

    public ELMemoryAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        events.add(new String(getLayout().toByteArray(event)));
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public void clear() {
        events.clear();
    }

    @PluginFactory
    public static ELMemoryAppender createLayout(@PluginElement("Layout") Layout<? extends Serializable> layout,
                                                @PluginElement("Filter") final Filter filter,
                                                @PluginAttribute("name") final String name,
                                                @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) final boolean ignoreExceptions) {
        return new ELMemoryAppender(name, filter, layout, ignoreExceptions);
    }
}
