package com.xasync.mixbean.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xasync.mixbean.core.compiler.StandardMixBeanDslCompiler;
import com.xasync.mixbean.core.exception.MixBeanSyntaxErrorEnum;
import com.xasync.mixbean.core.exception.MixBeanSyntaxException;
import com.xasync.mixbean.core.support.AssertExt;
import org.junit.Test;

import java.util.*;

import static com.xasync.mixbean.core.exception.MixBeanSyntaxErrorEnum.*;

public class MixBeanDslCompilerTest {

    private static final MixBeanDslCompiler STD_COMPILER = new StandardMixBeanDslCompiler();

    /**
     * Mock some cases for parsing BizFuncBean's DSL
     */
    @Test
    public void testOneBizFuncBeanDsl() {
        //region blank dsl
        AssertExt.assertEquals(0, STD_COMPILER.compile(null).size());
        AssertExt.assertEquals(0, STD_COMPILER.compile("").size());
        AssertExt.assertEquals(0, STD_COMPILER.compile("   ").size());
        //endregion

        //region param syntax error
        AssertExt.assertEquals(MixBeanSyntaxErrorEnum.PARAM_LIST_SYNTAX_ERROR, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("name")).getErrorEnum());
        AssertExt.assertEquals(MixBeanSyntaxErrorEnum.PARAM_LIST_SYNTAX_ERROR, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("name#")).getErrorEnum());
        AssertExt.assertEquals(MixBeanSyntaxErrorEnum.PARAM_LIST_SYNTAX_ERROR, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("name#sys")).getErrorEnum());
        AssertExt.assertEquals(MixBeanSyntaxErrorEnum.PARAM_LIST_SYNTAX_ERROR, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("name#sys(")).getErrorEnum());
        AssertExt.assertEquals(MixBeanSyntaxErrorEnum.PARAM_LIST_SYNTAX_ERROR, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("name#sys(#req.title")).getErrorEnum());
        //pass
        AssertExt.on(() -> STD_COMPILER.compile("name#sys(#req.title)"))
                .assertEquals(1, (x) -> Objects.nonNull(x) ? x.size() : 0)
                .map(x -> (BizFuncBeanDslSpec) x.get(0))
                .assertEquals("name", BizFuncBeanDslSpec::getAbility)
                .assertEquals("sys", BizFuncBeanDslSpec::getProvider)
                .assertEquals("(#req.title)", BizFuncBeanDslSpec::getRawParams);
        //endregion

        //region alias syntax
        AssertExt.assertEquals(MixBeanSyntaxErrorEnum.BIZ_FUNC_BEAN_ALIAS_MISS_SYMBOL, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("name#sys(#req.title,'mock')nick")).getErrorEnum());

        AssertExt.on(() -> STD_COMPILER.compile("name#sys(#req.title,'mock')~"))
                .assertEquals(1, (x) -> Objects.nonNull(x) ? x.size() : 0)
                .map(x -> (BizFuncBeanDslSpec) x.get(0))
                .assertEquals("name", BizFuncBeanDslSpec::getAbility)
                .assertEquals("sys", BizFuncBeanDslSpec::getProvider)
                .assertEquals("(#req.title,'mock')", BizFuncBeanDslSpec::getRawParams)
                .assertEquals("name", BizFuncBeanDslSpec::obtainResultIndexKey);

        AssertExt.on(() -> STD_COMPILER.compile("name#sys(#req.title,'mock')~nick"))
                .assertEquals(1, (x) -> Objects.nonNull(x) ? x.size() : 0)
                .map(x -> (BizFuncBeanDslSpec) x.get(0))
                .assertEquals("name", BizFuncBeanDslSpec::getAbility)
                .assertEquals("sys", BizFuncBeanDslSpec::getProvider)
                .assertEquals("name#sys", BizFuncBeanDslSpec::obtainName)
                .assertEquals("(#req.title,'mock')", BizFuncBeanDslSpec::getRawParams)
                .assertEquals("nick", BizFuncBeanDslSpec::obtainResultIndexKey);
        //endregion

        //region soft depend
        AssertExt.on(() -> STD_COMPILER.compile("name#sys(#req.title,'mock')"))
                .assertEquals(1, (x) -> Objects.nonNull(x) ? x.size() : 0)
                .map(x -> (BizFuncBeanDslSpec) x.get(0))
                .assertEquals("name", BizFuncBeanDslSpec::getAbility)
                .assertEquals("sys", BizFuncBeanDslSpec::getProvider)
                .assertEquals("(#req.title,'mock')", BizFuncBeanDslSpec::getRawParams)
                .assertEquals(false, BizFuncBeanDslSpec::getSoftDepend);
        AssertExt.on(() -> STD_COMPILER.compile("name#sys?(#req.title,'mock')"))
                .assertEquals(1, (x) -> Objects.nonNull(x) ? x.size() : 0)
                .map(x -> (BizFuncBeanDslSpec) x.get(0))
                .assertEquals("name", BizFuncBeanDslSpec::getAbility)
                .assertEquals("sys", BizFuncBeanDslSpec::getProvider)
                .assertEquals("(#req.title,'mock')", BizFuncBeanDslSpec::getRawParams)
                .assertEquals(true, BizFuncBeanDslSpec::getSoftDepend);
        //endregion

        //region timeout
        AssertExt.on(() -> STD_COMPILER.compile("name#sys^300?(#req.title,'mock')"))
                .assertEquals(1, (x) -> Objects.nonNull(x) ? x.size() : 0)
                .map(x -> (BizFuncBeanDslSpec) x.get(0))
                .assertEquals("name", BizFuncBeanDslSpec::getAbility)
                .assertEquals("sys", BizFuncBeanDslSpec::getProvider)
                .assertEquals("(#req.title,'mock')", BizFuncBeanDslSpec::getRawParams)
                .assertEquals(true, BizFuncBeanDslSpec::getSoftDepend)
                .assertEquals(300L, BizFuncBeanDslSpec::getTimeout);
        AssertExt.assertEquals(BIZ_FUNC_BEAN_TIMEOUT_PARSE_FAIL, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("name#sys^?()")).getErrorEnum());
        AssertExt.assertEquals(BIZ_FUNC_BEAN_TIMEOUT_PARSE_FAIL, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("name#sys^abc?()")).getErrorEnum());
        //endregion

        //region bizFuncName
        AssertExt.assertEquals(BIZ_FUNC_BEAN_MISS_ABILITY, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("#sys()")).getErrorEnum());
        AssertExt.assertEquals(BIZ_FUNC_BEAN_MISS_PROVIDER, AssertExt.assertThrows(
                MixBeanSyntaxException.class, () -> STD_COMPILER.compile("name#()")).getErrorEnum());
        AssertExt.on(() -> STD_COMPILER.compile("name()"))
                .assertEquals(1, (x) -> Objects.nonNull(x) ? x.size() : 0)
                .map(x -> (BizFuncBeanDslSpec) x.get(0))
                .assertEquals("name", BizFuncBeanDslSpec::getAbility)
                .assertEquals(null, BizFuncBeanDslSpec::getProvider)
                .assertEquals("name", BizFuncBeanDslSpec::obtainName)
                .assertEquals("name", BizFuncBeanDslSpec::obtainResultIndexKey);
        //endregion
    }


    /**
     * Mock some pure serial DSL
     */
    @Test
    public void testPureSerialBizFuncBeanDsl() {
        AssertExt.on(() -> STD_COMPILER.compile("f1()=>"))
                .assertEquals(1, List::size)
                .map(x -> (BizFuncBeanDslSpec) x.get(0))
                .assertEquals("f1", BizFuncBeanDslSpec::getAbility);

        AssertExt.on(() -> STD_COMPILER.compile("f1()=>f2()"))
                .assertEquals(2, List::size)
                .atIndex(0, BizFuncBeanDslSpec.class, "f1", BizFuncBeanDslSpec::getAbility)
                .atIndex(1, BizFuncBeanDslSpec.class, "f2", BizFuncBeanDslSpec::getAbility);

        AssertExt.on(() -> STD_COMPILER.compile("f1()=>f2()=>f3#sys^300?()~fsys"))
                .assertEquals(3, List::size)
                .atIndex(2, BizFuncBeanDslSpec.class, "fsys", BizFuncBeanDslSpec::obtainResultIndexKey);
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
        AssertExt.assertEquals(10, blocks.size());
        AssertExt.assertEquals(3, json.getJSONObject(1).getJSONArray("block0").size());

        JSONObject jsonSpec = json.getJSONObject(5);
        BizFuncBeanDslSpec spec = new BizFuncBeanDslSpec(
                jsonSpec.getString("rawDsl"),
                jsonSpec.getString("ability"),
                jsonSpec.getString("alias"),
                jsonSpec.getString("provider"),
                jsonSpec.getLong("timeout"),
                jsonSpec.getBoolean("softDepend"),
                jsonSpec.getString("paramLine")
        );
        AssertExt.assertEquals("mixTest", spec.obtainResultIndexKey());
        AssertExt.assertEquals("f9", spec.getAbility());
        AssertExt.assertEquals("sys", spec.getProvider());
        AssertExt.assertEquals("f9#sys", spec.obtainName());
        AssertExt.assertEquals("{'hi',{1,2,3}}", spec.getParamLineAsSpEl());
    }


}
