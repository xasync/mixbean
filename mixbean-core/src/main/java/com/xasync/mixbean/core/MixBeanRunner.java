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

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.xasync.mixbean.core.enhance.BizFuncBeanMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.*;

/**
 * MixBeanRunner
 *
 * @author xasync.com
 */
@Slf4j
class MixBeanRunner {
    private final static ExecutorService THREAD_POOL = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() + 1,
            1024, 3, TimeUnit.SECONDS,
            new SynchronousQueue<>(), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()));

    private final Map<String, List<Object>> DSL_COMPILE_CACHE = new ConcurrentHashMap<>();
    private final MixBeanRegister register;

    private final MixBeanDslCompiler compiler;

    public MixBeanRunner(MixBeanRegister register, MixBeanDslCompiler compiler) {
        this.register = register;
        this.compiler = compiler;
    }

    public void execute(final MixBeanContext cxt, final String dsl) {
        if (StringUtils.isBlank(dsl)) {
            log.debug("{} Your DSL '{}' is blank and nothing to do", MixBeanTrack.current(), dsl);
            return;
        }
        //compile dsl before execution, but it will use the cache if exists.
        List<Object> blocks;
        if (DSL_COMPILE_CACHE.containsKey(dsl)) {
            blocks = DSL_COMPILE_CACHE.get(dsl);
        } else {
            blocks = compiler.compile(dsl);
            DSL_COMPILE_CACHE.put(dsl, blocks);
        }
        /* MixBean stipulates that the main process of service orchestration must be serial.
        Of course, some sub nodes in the main process can be parallel,
        so the whole process starts from the execution of serial nodes. */
        execSerialBlocks(cxt, blocks);
    }

    private void invokeBizFuncBean(final MixBeanContext cxt, final BizFuncBeanDslSpec spec) {
        System.out.println(spec.obtainName());
        Pair<BizFuncBeanDefinition, BizFuncBeanMeta> pair = register.findBizFuncBean(spec.obtainName());
        BizFuncBeanDefinition bizFunc = pair.getLeft();
        BizFuncBeanMeta bizFuncMeta = pair.getRight();
        BizFuncBeanParams params = BizFuncBeanParams.from(null, bizFuncMeta, Collections.emptyList());
        Object result = bizFunc.execute(cxt, params);
        cxt.put(spec.obtainResultIndexKey(), result);
    }

    private void execSerialBlocks(final MixBeanContext cxt, List<Object> serialBlocks) {
        if (Objects.isNull(serialBlocks)) {
            throw new RuntimeException();
        }
        for (Object block : serialBlocks) {
            if (block instanceof Map) {
                Map<String, Object> parallelBlocks = (Map<String, Object>) block;
                execParallelBlocks(cxt, parallelBlocks);
            } else if (block instanceof BizFuncBeanDslSpec) {
                //exec
                BizFuncBeanDslSpec spec = (BizFuncBeanDslSpec) block;
                invokeBizFuncBean(cxt, spec);
            } else {
                throw new RuntimeException();
            }
        }
    }

    private void execParallelBlocks(final MixBeanContext cxt, Map<String, Object> parallelBlocks) {
        if (Objects.isNull(parallelBlocks)) {
            throw new RuntimeException();
        }
        List<Future<?>> futureList = new ArrayList<>();
        for (Object block : parallelBlocks.values()) {
            if (block instanceof Map) {
                Future<?> future = THREAD_POOL.submit(() -> execParallelBlocks(cxt, (Map<String, Object>) block));
                futureList.add(future);
            } else if (block instanceof List) {
                Future<?> future = THREAD_POOL.submit(() -> execSerialBlocks(cxt, (List<Object>) block));
                futureList.add(future);
            } else {
                throw new RuntimeException();
            }
        }
        for (Future<?> future : futureList) {
            try {
                //check if finish and wait forever
                future.get();
            } catch (Throwable ex) {
                //
                Throwable rootEx = ExceptionUtils.getRootCause(ex);
                throw new RuntimeException(rootEx.getMessage(), rootEx);
            }
        }
    }


}
