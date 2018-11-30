# ELogging log4j library

This library contains the following plugins:

## ELJsonLayout

### Overview

This is a Yet Another log4j2 json layout which has a couple advantages over the other ones out there:

First, it doesn't has any dependencies which avoid library conflicts (ie all the other json libraries I tried wouldn't
work with mule due to conflicts w/ jackson libraries)

Second, it supports MapMessage to generate a json message with multiples attributes. Additionally when using a MapMessage,
you can specify that a value in the message map should be included as "raw json" by adding another key/value with the
same key appended with "_$_rawjson_$_", with the value of "true"

so for example the following key/values:

```
foo=bar
obj={}
```

would generate:

```
{
    ...
    "foo": "bar",
    "obj": "{}"
}
```

but if instead you specify

```
foo=bar
obj={}
obj_$_rawjson_$_=true
```

it would generate:

```
{
    ...
    "foo": "bar",
    "obj": {}
}
```

### Example configuration

```
<?xml version="1.0" encoding="utf-8"?>
<Configuration>
    <Appenders>
        <RollingFile name="file" fileName="mytest.log" 
                 filePattern="mytest-%i.log">
            <ELJsonLayout/>
            <SizeBasedTriggeringPolicy size="10 MB" />
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <AsyncRoot level="INFO">
            <AppenderRef ref="file" />
        </AsyncRoot>
    </Loggers>
</Configuration>
```

In some cases with complex classloading (ie. mule applications) you might have to specify the package

```xml
<Configuration packages="com.kloudtek.elogging.log4j2">
```

## ELMemoryAppender

This appender just store each formatted log entry in memory