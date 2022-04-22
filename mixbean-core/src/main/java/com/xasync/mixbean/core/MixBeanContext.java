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

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

public class MixBeanContext extends JSONObject {

    @Getter
    private final String name;

    @Getter
    private final String uniqueId;

    @Setter
    private Boolean test;

    public Boolean getTest() {
        return Objects.nonNull(test) ? test : false;
    }

    private MixBeanContext(String name, String uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
    }

    public static MixBeanContext create(String name) {
        return create(name, null);
    }

    public static MixBeanContext create(String name, String uniqueId) {
        return new MixBeanContext(name, uniqueId);
    }


    public MixBeanContext chainAdd(String name, Object value) {
        this.put(name, value);
        return this;
    }

    public MixBeanContext chainAdd(Map<String, Object> variables) {
        if (Objects.nonNull(variables)) {
            this.putAll(variables);
        }
        return this;
    }

    public <T> T extractLastBeanReturn(Class<T> clazz) {
        return null;
    }


}
