package com.xasync.mixbean.core.bizfunc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xasync.mixbean.core.BizFuncBeanDefinition;
import com.xasync.mixbean.core.BizFuncBeanParams;
import com.xasync.mixbean.core.MixBeanContext;
import com.xasync.mixbean.core.annotation.BizFuncBeanParam;
import com.xasync.mixbean.core.annotation.BizFuncBeanParamDefines;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.xasync.mixbean.core.MixBeansTest.*;

@Component
public class PutoutCookBizFuncBean extends BizFuncBeanDefinition {
    public String ability() {
        return "putout";
    }

    public String provider() {
        return "cook";
    }

    public String desc() {
        return "put some foods out from pot";
    }

    @BizFuncBeanParamDefines({
            @BizFuncBeanParam("things")
    })
    public Object execute(MixBeanContext cxt, BizFuncBeanParams params) {
        Map<String, Object> things = params.get("things", new TypeReference<Map<String, Object>>() {
        });
        if (Objects.isNull(things)) {
            return null;
        }
        ChineseDishes dishes = new ChineseDishes();
        dishes.setName(String.valueOf(things.get("name")));
        List<String> foods = JSON.parseArray(JSON.toJSONString(things.get("foods")), String.class);
        dishes.setFoods(foods);
        return dishes;
    }
}
