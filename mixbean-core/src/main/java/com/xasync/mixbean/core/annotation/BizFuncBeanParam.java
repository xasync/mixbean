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
 * BizFuncBeanParam is used to describe the parameter of BizFuncBean, so you can define parameter's name,
 * parameter's description and so on. Of course, this annotation can make the registered beans more friendly
 * when you use the visual terminal for orchestrating the multiple BizFuncBean.
 * <p>
 * Ps: @BizFuncBeanParam cannot be applied to methods alone, but must be used with @BizFuncBeanParamDefines
 * <code>
 * // @BizFuncBeanParamDefines({
 * //       @BizFuncBeanParam(value="cityName", desc="the name of city", notNull=true)
 * // })
 * // public object execute(){
 * //     //...
 * // }
 * </code>
 *
 * @author xasync.com
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BizFuncBeanParam {

    /**
     * parameter's name
     *
     * @return name
     */
    String value();

    /**
     * parameter's description
     *
     * @return description
     */
    String desc() default "";

    /**
     * if the value of parameter allows to be not null
     *
     * @return true or false
     */
    boolean notNull() default false;
}
