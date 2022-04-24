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
package com.xasync.mixbean.core.support;

import org.junit.Assert;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * AssertExts
 *
 * @author xasync.com
 */
public class AssertExt extends Assert {

    public static <T> SilkyBuilder<T> on(Supplier<T> supplier) {
        return new SilkyBuilder<>(supplier);
    }

    public static class SilkyBuilder<T> {
        private T value = null;

        private final Supplier<T> originSupplier;

        private T obtainValue() {
            if (Objects.isNull(value)) {
                value = originSupplier.get();
            }
            return value;
        }

        public SilkyBuilder(Supplier<T> supplier) {
            this.originSupplier = supplier;
        }

        public SilkyBuilder<T> assertEquals(Object expect, Function<T, Object> actual) {
            AssertExt.assertEquals(expect, actual.apply(obtainValue()));
            return this;
        }


        public <E> SilkyBuilder<T> atIndex(Integer index, Class<E> clazz, Object expect, Function<E, Object> actual) {
            Object obj = obtainValue();
            if (!(obj instanceof List)) {
                throw new AssertionError("it is not a list '" + String.valueOf(obj) + "'");
            }
            List<?> list = (List<?>) obj;
            E value = clazz.cast(list.get(index));
            AssertExt.assertEquals(expect, actual.apply(value));
            return this;
        }

        public <M> SilkyBuilder<M> map(Function<T, M> function) {
            return new SilkyBuilder<>(() -> function.apply(originSupplier.get()));
        }

    }
}
