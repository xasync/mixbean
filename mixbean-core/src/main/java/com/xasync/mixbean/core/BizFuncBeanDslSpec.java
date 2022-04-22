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

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import static com.xasync.mixbean.core.MixBeanDslSymbols.*;
import static com.xasync.mixbean.core.MixBeanDslSymbols.BEAN_PARAM_LIST_END;
import static com.xasync.mixbean.core.MixBeanDslSymbols.BEAN_PARAM_LIST_START;

/**
 * BizFuncBeanDslSpec is used to structure a syntax segment which describes BizFuncBean in MixBean's DSL.
 *
 * @author xasync.com
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BizFuncBeanDslSpec {
    /**
     * the start symbol of inline-lists in SpEL
     * Ps: <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions">SpEL</a>
     */
    private final static String SPRING_EL_LIST_START = "{";

    /**
     * the end symbol of inline-lists in SpEL
     * Ps: <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions">SpEL</a>
     */
    private final static String SPRING_EL_LIST_END = "}";

    /**
     * Generate the name of BizFuncBean's instance.
     *
     * @param bizFuncBean BizFuncBeanDefinition
     * @return name
     */
    public static String genBizFuncBeanName(BizFuncBeanDefinition bizFuncBean) {
        return genBizFuncBeanName(bizFuncBean.ability(), bizFuncBean.provider());
    }

    /**
     * Generate the BizFuncBean's name according to the ability and provider.
     *
     * @param ability  the ability name
     * @param provider the provider name
     * @return name
     */
    private static String genBizFuncBeanName(String ability, String provider) {
        return ability + ABILITY_PROVIDER_DELIMITER + provider;
    }

    /**
     * the raw dsl about BizFuncBean
     */
    @Getter
    private final String rawDsl;


    /**
     * the name of BizFuncBean's ability
     */
    @Getter
    private final String ability;


    /**
     * the alias is used to index the result from MixBeanContext
     */
    @Getter
    private final String alias;


    /**
     * the name of BizFuncBean's provider
     */
    @Getter
    private final String provider;

    /**
     * The maximum time to wait for completable execution,in milliseconds
     */
    @Getter
    private final Long timeout;

    /**
     * it means that the exception will not interrupt when the value is true
     */
    @Getter
    private final Boolean softDepend;

    /**
     * a string line about BizFuncBean's parameter
     */
    @Getter
    private final String paramLine;

    /**
     * Get the name of BizFuncBean's instance.
     *
     * @return the name of BizFuncBean
     */
    public String obtainName() {
        return genBizFuncBeanName(ability, provider);
    }

    /**
     * Obtain the map's key for indexing the result
     *
     * @return index-key
     */
    public String obtainResultIndexKey() {
        return StringUtils.isNotBlank(alias) ? alias : ability;
    }

    /**
     * Convert the parameter line to inline-lists in spring expression
     *
     * @return inline-lists
     */
    public String getParamLineAsSpEl() {
        return SPRING_EL_LIST_START + this.paramLine + SPRING_EL_LIST_END;
    }

    public String getRawParams() {
        return BEAN_PARAM_LIST_START + this.paramLine + BEAN_PARAM_LIST_END;
    }

}
