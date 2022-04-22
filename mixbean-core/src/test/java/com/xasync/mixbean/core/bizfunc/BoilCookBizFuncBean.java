package com.xasync.mixbean.core.bizfunc;

import com.xasync.mixbean.core.BizFuncBeanDefinition;
import com.xasync.mixbean.core.BizFuncBeanParams;
import com.xasync.mixbean.core.MixBeanContext;
import com.xasync.mixbean.core.annotation.BizFuncBeanParam;
import com.xasync.mixbean.core.annotation.BizFuncBeanParamDefines;
import org.springframework.stereotype.Component;


@Component
public class BoilCookBizFuncBean extends BizFuncBeanDefinition {

    public String ability() {
        return "boil";
    }

    public String provider() {
        return "cook";
    }

    public String desc() {
        return "boil some liquid by heating the container";
    }

    @BizFuncBeanParamDefines({
            @BizFuncBeanParam(value = "liquid", notNull = true, desc = "specific a liquid which will be boil"),
            @BizFuncBeanParam(value = "container", notNull = true, desc = "specific a container,likes pot")
    })
    public Object execute(MixBeanContext cxt, BizFuncBeanParams params) {
        return null;
    }
}
