package com.xasync.mixbean.core;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

public class MixBeanRunnerTest extends BaseSpringContainerTest {

    @Test
    public void testRegisterInSpring() {
        //Map<String, BizFuncBeanMeta> metaMap = MixBeans.
        //System.out.println(JSON.toJSONString(metaMap));
    }

    @Test
    public void testRunner() throws InterruptedException {

        //design a workflow in MixBean's DSL
        String dsl = "{ " + /* the start of parallel block */
                "boil#cook(#input.container,#input.liquid)=>blanch#cook(#boil)=>cut#cook(#blanch)" +
                ";" + /* Parallel block partition identifier */
                "stir#cook(#input.eggs)" +
                "}" + /* the end of parallel block */
                "=>heat#cook^3000?(#input.container)~name" +
                "=>pour#cook(#heat,#input.oil)" +
                "=>putin#cook(#heat,#cut,#stir)" +
                "=>fry#cook(#heat,#putin)" +
                "=>putout#cook({name:'eggAndTomato',foods:{#fry.foods}})";
        MixBeanContext cxt = MixBeanContext.create("test");
        MixBeans.run(cxt, dsl);

        System.out.println(JSON.toJSONString(cxt));
    }


    @Test
    public void testSyntax() throws InterruptedException {

        //design a workflow in MixBean's DSL
        String dsl = "{ " + /* the start of parallel block */
                "boil#cook(#input.container,#input.liquid)=>blanch#cook(#boil)=>cut#cook(#blanch)" +
                ";" + /* Parallel block partition identifier */
                "stir#cook(#input.eggs)" +
                "}" + /* the end of parallel block */
                "=>heat#cook^3000?(#input.container)~name" +
                "=>pour#cook(#heat,#input.oil)" +
                "=>putin#cook(#heat,#cut,#stir)" +
                "=>fry#cook(#heat,#putin)" +
                "=>putout#cook({name:'eggAndTomato',foods:{#fry.foods}})";
        MixBeanContext cxt = MixBeanContext.create("test");
        MixBeans.run(cxt, dsl);

        System.out.println(JSON.toJSONString(cxt));
    }
}
