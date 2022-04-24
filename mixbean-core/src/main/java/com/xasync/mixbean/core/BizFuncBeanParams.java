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
import com.alibaba.fastjson.TypeReference;
import com.xasync.mixbean.core.enhance.BizFuncBeanMeta;
import com.xasync.mixbean.core.exception.MixBeanParamException;
import com.xasync.mixbean.core.exception.MixBeanRuntimeException;
import com.xasync.mixbean.core.exception.MixBeanSyntaxException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

public class BizFuncBeanParams {
    private final static String CAST_TEMP_KEY = "__cast_temp_key__";

    @Getter
    private final MixBeanTrack trace;
    private final List<Object> paramValueList;

    private final BizFuncBeanMeta bizFuncBeanMeta;

    private BizFuncBeanParams(MixBeanTrack trace, BizFuncBeanMeta bizFuncBeanMeta, List<Object> paramValueList) {
        this.trace = trace;
        this.bizFuncBeanMeta = bizFuncBeanMeta;
        this.paramValueList = paramValueList;
    }

    public static BizFuncBeanParams from(MixBeanTrack trace, BizFuncBeanMeta bizFuncBeanMeta, List<Object> paramValueList) {
        BizFuncBeanParams params = new BizFuncBeanParams(trace, bizFuncBeanMeta, paramValueList);
        int len = Objects.nonNull(paramValueList) ? paramValueList.size() : 0;
        int pLen = Objects.nonNull(bizFuncBeanMeta.getParamList()) ? bizFuncBeanMeta.getParamList().size() : 0;
        if (len > 0 && pLen < len) {
            throw new MixBeanRuntimeException(trace, "");
        }
        return params;
    }

    public <T> T get(String name, TypeReference<T> type) {
        name = StringUtils.trimToEmpty(name);
        if (name.isEmpty()) {
            throw new MixBeanParamException(trace, "BizFuncBeanParams#get 'name' is empty");
        }
        Pair<Integer, BizFuncBeanMeta.ParameterMeta> pair = findParamDefineByName(name);
        int index = pair.getLeft();
        //don't define the parameter
        if (index < 0) {
            throw new MixBeanRuntimeException(trace, String.format("miss the parameter definition about '%s'", name));
        }
        BizFuncBeanMeta.ParameterMeta param = pair.getRight();
        Object obj = index < paramValueList.size() ? paramValueList.get(index) : null;
        T paramValue = null;
        if (Objects.nonNull(obj)) {
            JSONObject json = new JSONObject();
            json.put(CAST_TEMP_KEY, obj);
            paramValue = json.getObject(CAST_TEMP_KEY, type);
        }
        if (param.getNotNull() && Objects.isNull(paramValue)) {
            throw new MixBeanParamException(trace, "'%s' is require");
        }
        return paramValue;
    }

    private Pair<Integer, BizFuncBeanMeta.ParameterMeta> findParamDefineByName(String name) {
        List<BizFuncBeanMeta.ParameterMeta> list = bizFuncBeanMeta.getParamList();
        if (Objects.isNull(list)) {
            return Pair.of(-1, null);
        }
        for (int i = 0; i < list.size(); i++) {
            BizFuncBeanMeta.ParameterMeta pMeta = list.get(i);
            if (Objects.equals(name, pMeta.getName())) {
                return Pair.of(i, pMeta);
            }
        }
        return Pair.of(-1, null);
    }
}
