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

import com.xasync.mixbean.core.enhance.BizFuncBeanMeta;
import com.xasync.mixbean.core.util.LogUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * MixBeanRegister
 *
 * @author xasync.com
 */
class MixBeanRegister {
    private final Map<String, Pair<BizFuncBeanDefinition, BizFuncBeanMeta>> BEAN_REG_POOL = new ConcurrentHashMap<>();


    public void register(BizFuncBeanDefinition bizFuncBean) {
        String clazzName = null;
        try {
            if (Objects.isNull(bizFuncBean)) {
                LogUtils.warn("Abort to register 'null'");
                return;
            }
            clazzName = bizFuncBean.getClass().getCanonicalName();
            BizFuncBeanMeta meta = BizFuncBeanMeta.from(bizFuncBean);
            String bizFuncName = meta.getName();
            //BizFuncBean's instance
            if (BEAN_REG_POOL.containsKey(bizFuncName)) {
                LogUtils.warn("Duplicate to register '{}' as '{}'", clazzName, bizFuncName);
                return;
            }
            BEAN_REG_POOL.put(bizFuncName, Pair.of(bizFuncBean, meta));
            LogUtils.info("success to register '{}' as '{}'", clazzName, bizFuncName);
        } catch (Throwable ex) {
            LogUtils.error("fail to register '{}'", ex, clazzName);
        }
    }

    public Pair<BizFuncBeanDefinition, BizFuncBeanMeta> findBizFuncBean(String bizFuncName) {
        return BEAN_REG_POOL.get(bizFuncName);
    }


    public List<BizFuncBeanMeta> getBizFuncBeanMetaList() {
        return BEAN_REG_POOL.values()
                .stream()
                .map(Pair::getValue)
                .collect(Collectors.toList());
    }
}
