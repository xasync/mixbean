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
package com.xasync.mixbean.core.enhance;

import com.xasync.mixbean.core.BizFuncBeanDefinition;
import com.xasync.mixbean.core.BizFuncBeanDslSpec;
import com.xasync.mixbean.core.MixBeanTrack;
import com.xasync.mixbean.core.annotation.BizFuncBeanParam;
import com.xasync.mixbean.core.annotation.BizFuncBeanParamDefines;
import com.xasync.mixbean.core.exception.MixBeanParamException;
import com.xasync.mixbean.core.exception.MixBeanRuntimeException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * BizFuncBeanMeta is used to structure BizFuncBean's instance registered in MixBean's pool.
 *
 * @author xasync.com
 */
@Data
public class BizFuncBeanMeta {

    /**
     * the name of BizFuncBean's instance
     */
    private String name;

    /**
     * the description of BizFuncBean's instance
     */
    private String desc;

    /**
     * the parameters of BizFuncBean's instance
     */
    private List<ParameterMeta> paramList;

    /**
     * Create a new instance base on the inputs which is instance of BizFuncBeanDefinition
     *
     * @param definition BizFuncBeanDefinition
     * @return BizFuncBeanMeta
     */
    public static BizFuncBeanMeta from(BizFuncBeanDefinition definition) {
        if (Objects.isNull(definition)) {
            throw new MixBeanParamException(MixBeanTrack.current(), "BizFuncBeanMeta#from expect a non-null value");
        }
        Method method = definition.obtainEntranceMethod();
        BizFuncBeanParamDefines paramDefines = method.getAnnotation(BizFuncBeanParamDefines.class);
        if (Objects.isNull(paramDefines)) {
            throw new MixBeanRuntimeException(MixBeanTrack.current(),
                    String.format("miss '@BizFuncBeanParamDefines' on '%s'", definition.getClass().getCanonicalName()));
        }
        List<ParameterMeta> paramList = new ArrayList<>();
        for (BizFuncBeanParam paramDefine : paramDefines.value()) {
            ParameterMeta pMeta = new ParameterMeta();
            pMeta.setName(StringUtils.trimToEmpty(paramDefine.value()));
            pMeta.setDesc(paramDefine.desc());
            pMeta.setNotNull(paramDefine.notNull());
            //add to the result list
            paramList.add(pMeta);
        }

        BizFuncBeanMeta funcMeta = new BizFuncBeanMeta();
        funcMeta.setName(BizFuncBeanDslSpec.genBizFuncBeanName(definition));
        funcMeta.setDesc(definition.desc());
        funcMeta.setParamList(paramList);
        return funcMeta;
    }

    /**
     * ParameterMeta
     */
    @Data
    public static class ParameterMeta implements Serializable {

        /**
         * Parameter's name
         */
        private String name;

        /**
         * Parameter's description
         */
        private String desc;

        /**
         * If parameter's value allows to be not null
         */
        private Boolean notNull;
    }
}
