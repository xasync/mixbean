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

package com.xasync.mixbean.core.annotation;


import java.lang.annotation.*;

/**
 * BizFuncBeanParamDefines is annotation in java for decoration the execution-entrance method of BizFuncBean,
 * and you must use it to define the parameters of BizFuncBean.
 *
 * @author xasync.com
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BizFuncBeanParamDefines {

    /**
     * Defines the parameters of BizFuncBean,which is implements the BizFuncBeanDefinition abstract class.
     * Ps: the annotation is necessary for humanizing orchestration in terminal.
     *
     * @return the parameters of BizFunBean
     */
    BizFuncBeanParam[] value();
}
