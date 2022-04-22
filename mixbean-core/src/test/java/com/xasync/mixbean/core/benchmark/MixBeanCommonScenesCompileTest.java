package com.xasync.mixbean.core.benchmark;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xasync.mixbean.core.MixBeanDslCompiler;
import com.xasync.mixbean.core.compiler.StandardMixBeanDslCompiler;
import org.junit.Test;

import java.util.List;

public class MixBeanCommonScenesCompileTest {

    private static final MixBeanDslCompiler STD_COMPILER = new StandardMixBeanDslCompiler();

    @Test
    public void testPureSerial() {
        String dsl = "f1#sys^1000?('a')~af1" +
                "f2#sys^1000?('b',2)~af2" +
                "f3#sys^1000?('c',{31,32,33})~af3" +
                "f4#sys^1000?('d',{name:'hello',desc:'test',time:'1991-09-18 12:00:00',feature:{tag:'1,2'}})~af4" +
                "f5#sys^1000?('e')~af5" +
                "f6#sys^1000?('f')~af6" +
                "f7#sys^1000?('g')~af7" +
                "f8#sys^1000?('h')~af8" +
                "f9#sys^1000?('i')~af9" +
                "f10#sys^1000?('j')~af10";
        double times = 10000;
        long totalRt = 0;
        for (int count = 1; count <= times; count++) {
            long start = System.currentTimeMillis();
            List<Object> blocks = STD_COMPILER.compile(dsl);
            long rt = System.currentTimeMillis() - start;
            System.out.println(count + "=" + rt);
            totalRt += rt;
        }
        System.out.println("PureSerial RT AVG:" + (totalRt / times));
    }

    @Test
    public void testSerialNestParallel() {

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
        double times = 10000;
        long totalRt = 0;
        for (int count = 1; count <= times; count++) {
            long start = System.currentTimeMillis();
            List<Object> blocks = STD_COMPILER.compile(dsl);
            long rt = System.currentTimeMillis() - start;
            System.out.println(count + "=" + rt);
            totalRt += rt;
        }
        System.out.println("Nesting RT AVG:" + (totalRt / times));
    }
}
