package com.xasync.mixbean.core;

import com.alibaba.fastjson.JSON;
import com.xasync.island.spring.SpringContexts;
import com.xasync.mixbean.core.support.BaseSpringTest;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MixBeansTest extends BaseSpringTest {

    @Test
    public void testCookEggAndTomato() {
        String projectName = SpringContexts.value("${project.name}");
        System.out.println("=== " + projectName + " ===");
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
        //context's properties
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("container", "Non-stick Iron Pot");
        inputMap.put("liquid", "water");
        inputMap.put("eggs", "hen's egg");
        inputMap.put("oil", "olive oil");
        MixBeanContext cxt = MixBeanContext.create("cookEggAndTomato")
                .chainAdd(inputMap);
        //execution
        MixBeans.run(cxt, dsl);
        //obtain the result of last BizFuncBean
        ChineseDishes dishes = cxt.extractLastBeanReturn(ChineseDishes.class);
        System.out.println(JSON.toJSONString(dishes));
        //AssertExt.assertNotNull(dishes);

        //MixBeanRunner runner = new MixBeanRunner();
        //runner.interpret(dsl);
    }


    @Data
    public static class ChineseDishes {

        /**
         * The name of dishes
         */
        private String name;

        /**
         * Dish composition
         */
        private List<String> foods;
    }
}
