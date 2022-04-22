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

package com.xasync.mixbean.core;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * MixBeanTrack is used to carry execution tracking information
 *
 * @author xasync.com
 */
public class MixBeanTrack implements Serializable {

    private final static TransmittableThreadLocal<MixBeanTrack> TRACK = new TransmittableThreadLocal<>();


    private MixBeanTrack(String mixBeanName, String serialId, Boolean test) {
        this.name = mixBeanName;
        this.serialId = serialId;
        this.test = test;
    }

    public static void start(String mixBeanName) {
        start(mixBeanName, null, false);
    }

    public static void start(String mixBeanName, String serialId, Boolean test) {
        if (StringUtils.isBlank(serialId)) {
            serialId = createSerialId();
        }
        TRACK.set(new MixBeanTrack(mixBeanName, serialId, test));
    }

    public static MixBeanTrack current() {
        return TRACK.get();
    }

    private static String createSerialId() {
        return String.format("%s^%s", System.currentTimeMillis(), RandomStringUtils.randomNumeric(3));
    }

    /**
     * A unique number for tracing each execution
     */
    @Getter
    private final String serialId;

    /**
     * Provides a human-readable identify
     */
    @Getter
    private final String name;

    /**
     * Mark as a testing execution,default false
     */
    private final Boolean test;

    public Boolean getTest() {
        return Objects.nonNull(test) ? test : false;
    }

    @Override
    public String toString() {
        return String.format("<|serialId=%s|name=%s|test=%s|>", getSerialId(), getName(), getTest());
    }
}
