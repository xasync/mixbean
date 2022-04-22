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

import java.util.List;

/**
 * MixBeanDslCompiler
 *
 * @author xasync.com
 */
public interface MixBeanDslCompiler {
    /**
     * Specifies a concise name for MixBean's DSL compiler.
     *
     * @return the name of compiler
     */
    String name();

    /**
     * Compile a MixBean's DSL to the new structure which makes sure to execute faster. In this new structure,
     * the elements in the list represent the need for sequential execution,
     * the elements in the map represent the need for parallel execution, and nested maps are allowed in the list.
     *
     * @param dsl MixBean's DSL
     * @return the compiled MixBean's DSL
     */
    List<Object> compile(String dsl);
}
