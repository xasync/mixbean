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
package com.xasync.mixbean.core.util.logback;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Objects;

/**
 * MixBeanCaller
 *
 * @author xasync.com
 */
public class MixBeanClassOfCallerConverter extends NamedConverter {

    private final static int SECOND_LAYER_OF_STACK = 2;
    public final static String NAME = "mbCaller";

    @Override
    protected String getFullyQualifiedName(ILoggingEvent event) {
        try {
            StackTraceElement[] cda = event.getCallerData();
            if (Objects.isNull(cda) || cda.length < SECOND_LAYER_OF_STACK) {
                return "?";
            }
            StackTraceElement stackTopElement = cda[1];
            return stackTopElement.getClassName() + ":" + stackTopElement.getLineNumber();
        } catch (Throwable ex) {
            return "?";
        }
    }
}
