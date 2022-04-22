package com.xasync.mixbean.core.bizfunc;

import com.xasync.mixbean.core.BizFuncBeanDefinition;
import com.xasync.mixbean.core.BizFuncBeanParams;
import com.xasync.mixbean.core.MixBeanContext;
import com.xasync.mixbean.core.annotation.BizFuncBeanParam;
import com.xasync.mixbean.core.annotation.BizFuncBeanParamDefines;
import org.springframework.stereotype.Component;

@Component
public class BlanchCookBizFuncBean extends BizFuncBeanDefinition {
    public String ability() {
        return "blanch";
    }

    public String provider() {
        return "cook";
    }

    public String desc() {
        return "put foods into the boiling liquid a little while";
    }

    @BizFuncBeanParamDefines({
            @BizFuncBeanParam(value = "food", notNull = true, desc = "foods"),
            @BizFuncBeanParam("liquid")
    })
    public Object execute(MixBeanContext cxt, BizFuncBeanParams params) {
        return null;
    }
}
