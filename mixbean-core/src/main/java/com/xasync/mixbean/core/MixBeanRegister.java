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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MixBeanRegister
 *
 * @author xasync.com
 */
class MixBeanRegister {
    private final Map<String, BizFuncBeanMeta> META_REG_POOL = new ConcurrentHashMap<>();
    private final Map<String, BizFuncBeanDefinition> BEAN_REG_POOL = new ConcurrentHashMap<>();


    public void register(BizFuncBeanDefinition bizFuncBean) {
        try {
            String bizFuncName = BizFuncBeanDslSpec.genBizFuncBeanName(bizFuncBean);
            BEAN_REG_POOL.putIfAbsent(bizFuncName, bizFuncBean);
            META_REG_POOL.putIfAbsent(bizFuncName, BizFuncBeanMeta.from(bizFuncBean));
            System.out.println("[MixBean] success to register '" + bizFuncName + "'");
        } catch (Throwable ex) {
            System.out.println("[MixBean] fail to register '" +
                    (bizFuncBean != null ? bizFuncBean.getClass().getCanonicalName() : null) + "', cause:" +
                    ExceptionUtils.getMessage(ex));
        }
    }

    public Pair<BizFuncBeanDefinition, BizFuncBeanMeta> findBizFuncBean(String bizFuncName) {
        BizFuncBeanDefinition bizFuncBean = BEAN_REG_POOL.get(bizFuncName);
        if (Objects.isNull(bizFuncBean)) {
            throw new RuntimeException();
        }
        BizFuncBeanMeta meta = META_REG_POOL.get(bizFuncName);
        if (Objects.isNull(meta)) {
            throw new RuntimeException();
        }
        return Pair.of(bizFuncBean, meta);
    }


    public Map<String, BizFuncBeanMeta> getRegBizFuncBeanMetaMap() {
        return META_REG_POOL;
    }
}
