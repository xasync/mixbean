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

import com.xasync.mixbean.core.annotation.BizFuncBeanParamDefines;
import com.xasync.mixbean.core.exception.MixBeanRuntimeException;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * BizFuncBeanDefinition
 *
 * @author xasync.com
 */
public abstract class BizFuncBeanDefinition implements InitializingBean {

    /**
     * The main execution method name
     */
    private final static String ENTRANCE_METHOD_NAME = "execute";

    /**
     * Provides the name of BizFuncBean's ability
     *
     * @return the name of BizFuncBean's ability
     */
    public abstract String ability();

    /**
     * Provides the name of BizFuncBean's provider
     *
     * @return the name of BizFuncBean's provider
     */
    public abstract String provider();

    /**
     * Describe in detail the functions of the current BizFuncBean
     *
     * @return description
     */
    public abstract String desc();

    /**
     * BizFuncBean's execution-entrance
     *
     * @param cxt    MixBeanContext
     * @param params BizFuncBeanParams
     * @return result
     */
    public abstract Object execute(MixBeanContext cxt, BizFuncBeanParams params);

    /**
     * Obtain the execution-entrance method of the current BizFuncBean
     *
     * @return A method reference
     */
    public Method obtainEntranceMethod() {
        Class<? extends BizFuncBeanDefinition> clazz = this.getClass();
        Optional<Method> opt = Arrays.stream(clazz.getDeclaredMethods())
                .filter(x -> Objects.equals(x.getName(), ENTRANCE_METHOD_NAME))
                .findFirst();
        if (!opt.isPresent()) {
            throw new MixBeanRuntimeException(MixBeanTrack.current(),
                    String.format("miss %s to decorate the method '%s' of '%s'",
                            BizFuncBeanParamDefines.class.getSimpleName(),
                            ENTRANCE_METHOD_NAME, clazz.getSimpleName()));
        }
        return opt.get();
    }

    public void afterPropertiesSet() throws Exception {
        MixBeans.register(this);
    }
}
