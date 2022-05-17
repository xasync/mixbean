/*
Copyright 2022~Forever xasync.com under one or more contributor authorized.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.xasync.mixbean.core.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.xasync.island.log.logback.FocusClassOfCallerConverter;
import com.xasync.island.log.logback.LogbackConverters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * LogUtils
 *
 * @author xasync.com
 */
public class LogUtils {
    public final static String COL_SP = "^^^";
    public final static String KV_SP = "=>";

    private final static String LOG_VAR_MARK = "{}";
    private final static String MIX_BEAN_LOGGER_NAME = "__MIX_BEAN_LOGGER__";
    private final static String LOG_FILE_NAME = "mixbean.log";

    private static Logger ROOT_LOGGER;

    private static Logger MIX_BEAN_LOGGER;


    public static void debug(String msg, Object... arguments) {
        MIX_BEAN_LOGGER.debug(msg, arguments);
        ROOT_LOGGER.debug(msg, arguments);
    }

    public static void debug(String msg, Throwable throwable, Object... arguments) {
        try {
            String tpl = StringUtils.replace(msg, LOG_VAR_MARK, "%s", arguments.length);
            String line = String.format(tpl, arguments);
            MIX_BEAN_LOGGER.debug(line, throwable);
            ROOT_LOGGER.debug(line, throwable);
        } catch (Throwable ex) {
            //pass
        }
    }


    public static void info(String msg, Object... arguments) {
        MIX_BEAN_LOGGER.info(msg, arguments);
    }


    public static void warn(String msg, Object... arguments) {
        MIX_BEAN_LOGGER.warn(msg, arguments);
    }


    public static void warn(String msg, Throwable throwable, Object... arguments) {
        try {
            String tpl = StringUtils.replace(msg, LOG_VAR_MARK, "%s", arguments.length);
            String line = String.format(tpl, arguments);
            MIX_BEAN_LOGGER.warn(line, throwable);
        } catch (Throwable ex) {
            //pass
        }
    }


    public static void error(String msg, Object... arguments) {
        MIX_BEAN_LOGGER.error(msg, arguments);
    }

    public static void error(String msg, Throwable throwable, Object... arguments) {
        try {
            String tpl = StringUtils.replace(msg, LOG_VAR_MARK, "%s", arguments.length);
            String line = String.format(tpl, arguments);
            MIX_BEAN_LOGGER.error(line, throwable);
        } catch (Throwable ex) {
            //pass
        }
    }

    static {
        try {
            ROOT_LOGGER = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            //MixBean Private Logger
            ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
            boolean provideByLogback = (loggerFactory instanceof LoggerContext);
            if (provideByLogback) {
                LoggerContext loggerContext = (LoggerContext) loggerFactory;
                //appender
                RollingFileAppender<ILoggingEvent> appender = newRollingFileAppender(loggerContext);
                //create a custom logger if not exists
                MIX_BEAN_LOGGER = loggerFactory.getLogger(MIX_BEAN_LOGGER_NAME);
                ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) MIX_BEAN_LOGGER;
                logbackLogger.setLevel(Level.INFO);
                logbackLogger.setAdditive(false);
                logbackLogger.addAppender(appender);
                System.out.println("success to initialize the logger provided by logback: " + MIX_BEAN_LOGGER.getName() + " > " + currentLogFilePath());
            } else {
                System.out.println("Don't find any logback-binder for slf4j, " + "and we recommend that you provide the logback dependency for MixBean " + "to obtain better logging support.");
            }
        } catch (Throwable ex) {
            MIX_BEAN_LOGGER = LoggerFactory.getLogger(LogUtils.class);
            System.out.println("fail to initialize logger, and default to " + MIX_BEAN_LOGGER.getName());
            ex.printStackTrace();
        }
    }

    public static Path currentLogFilePath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, LOG_FILE_NAME);
    }

    private static RollingFileAppender<ILoggingEvent> newRollingFileAppender(LoggerContext loggerContext) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(loggerContext);
        //logger name
        appender.setName(MIX_BEAN_LOGGER_NAME + "APPENDER");
        //logger file
        appender.setFile(currentLogFilePath().toString());
        //rolling policy
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<>();
        policy.setContext(loggerContext);
        policy.setParent(appender);
        policy.setMaxFileSize(FileSize.valueOf("50MB"));
        policy.setTotalSizeCap(FileSize.valueOf("5GB"));
        policy.setMaxHistory(3);
        policy.setFileNamePattern(currentLogFilePath() + ".%d{yyyy-MM-dd}.%i");
        policy.start();
        appender.setRollingPolicy(policy);
        //encoder
        LogbackConverters.register(FocusClassOfCallerConverter.class, FocusClassOfCallerConverter.SHORT_NAME);
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setCharset(StandardCharsets.UTF_8);
        //define the data format in log
        encoder.setPattern(String.join(COL_SP, "%date{yyyy-MM-dd'T'HH:mm:ss.SSS}", "%level", "%thread", "%fc{15,1}", "%message%n"));
        encoder.start();
        appender.setEncoder(encoder);
        //start
        appender.start();
        return appender;
    }

}
