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

/**
 * Defines all symbols in MixBean's DSL
 *
 * @author xasync.com
 */
public class MixBeanDslSymbols {
    public final static String ABILITY_PROVIDER_DELIMITER = "#";
    public final static String PARALLEL_BLOCK_START = "{";
    public final static String PARALLEL_BLOCK_END = "}";
    public final static String PARALLEL_BEAN_DELIMITER = ";";
    public final static String SERIAL_BEAN_DELIMITER = "=>";
    public final static String BEAN_PARAM_LIST_START = "(";
    public final static String BEAN_PARAM_LIST_END = ")";
    public final static String BEAN_ALIAS_MARK = "~";
    public final static String BEAN_TIMEOUT_MARK = "^";
    public final static String BEAN_SOFT_DEPEND_MARK = "?";
}
