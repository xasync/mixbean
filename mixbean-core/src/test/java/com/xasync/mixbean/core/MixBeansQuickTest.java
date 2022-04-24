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

import com.xasync.mixbean.core.bizfunc.case_reg.EmptyBizFuncBean;
import org.junit.Test;

/**
 * MixBeansQuickTest
 *
 * @author xasync.com
 */
public class MixBeansQuickTest {

    @Test
    public void testRegister() {
        MixBeans.register(null);
        MixBeans.register(new EmptyBizFuncBean());
    }
}
