package com.xasync.mixbean.core.bizfunc;

import com.xasync.mixbean.core.BizFuncBeanDefinition;
import com.xasync.mixbean.core.BizFuncBeanParams;
import com.xasync.mixbean.core.MixBeanContext;
import com.xasync.mixbean.core.annotation.BizFuncBeanParam;
import com.xasync.mixbean.core.annotation.BizFuncBeanParamDefines;
import org.springframework.stereotype.Component;


@Component
public class StirCookBizFuncBean extends BizFuncBeanDefinition {
    public String ability() {
        return "stir";
    }

    public String provider() {
        return "cook";
    }

    public String desc() {
        return "make foods to be well-mixed";
    }

    @BizFuncBeanParamDefines({
            @BizFuncBeanParam("food")
    })
    public Object execute(MixBeanContext cxt, BizFuncBeanParams params) {
        return null;
    }
}
