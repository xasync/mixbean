package com.xasync.mixbean.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xasync.mixbean.core.compiler.StandardMixBeanDslCompiler;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class MixBeanDslCompilerTest {

    private static final MixBeanDslCompiler STD_COMPILER = new StandardMixBeanDslCompiler();

    @Test
    public void testStdCompilerForPureSerial() {
        List<Triple<String, Boolean, String>> caseList = Arrays.asList(Triple.of("null", false, null), Triple.of("empty", false, ""), Triple.of("blank", false, " "), Triple.of("oneBizFunc", true, "cry#baby^100?('tom',3)~me"), Triple.of("twoBizFunc", true, "cry#baby()=>care#mom(#cry,'lucy')"), Triple.of("nonProvider", true, "mergeList({1,2,3},{4,5,6})"));
        for (Triple<String, Boolean, String> triple : caseList) {
            String caseName = triple.getLeft();
            Boolean expect = triple.getMiddle();
            String dsl = triple.getRight();
            List<Object> bizFuncList = null;
            try {
                bizFuncList = STD_COMPILER.compile(dsl);
                Optional<Object> opt = Optional.ofNullable(bizFuncList).orElse(Collections.emptyList()).stream().filter(x -> !(x instanceof BizFuncBeanDslSpec)).findFirst();
                if (opt.isPresent()) {
                    throw new RuntimeException(caseName);
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            boolean actual = Objects.nonNull(bizFuncList) && !bizFuncList.isEmpty();
            System.out.printf("[%s] = %s \n", caseName, bizFuncList);
            Assert.assertEquals(expect, actual);
        }
    }

    @Test
    public void testStdCompilerForMix() {
        String dsl = "f1()=>{f21()=>f22()=>f23();f3()}=>f4()=>f5()=>{f6();f7();f8()}" +
                "=>f9#sys^300?('hi',{1,2,3})~mixTest" +
                "=>f10()=>f11()=>f12()=>f13()";
        long start = System.currentTimeMillis();
        List<Object> blocks = STD_COMPILER.compile(dsl);
        System.out.println("rt=" + (System.currentTimeMillis() - start));
        String jsonStr = JSON.toJSONString(blocks, SerializerFeature.PrettyFormat);
        System.out.println(jsonStr);
        JSONArray json = JSON.parseArray(jsonStr);
        Assert.assertEquals(10, blocks.size());
        Assert.assertEquals(3, json.getJSONObject(1).getJSONArray("block0").size());

        JSONObject jsonSpec = json.getJSONObject(5);
        BizFuncBeanDslSpec spec = new BizFuncBeanDslSpec(
                jsonSpec.getString("rawDsl"),
                jsonSpec.getString("ability"),
                jsonSpec.getString("alias"),
                jsonSpec.getString("provider"),
                jsonSpec.getLong("timeout"),
                jsonSpec.getBoolean("softDepend"),
                jsonSpec.getString("params")
        );
        Assert.assertEquals("mixTest", spec.obtainResultIndexKey());
        Assert.assertEquals("f9", spec.getAbility());
        Assert.assertEquals("sys", spec.getProvider());
        Assert.assertEquals("f9#sys", spec.obtainName());
        Assert.assertEquals("{'hi',{1,2,3}}", spec.getParamLineAsSpEl());
    }
}
