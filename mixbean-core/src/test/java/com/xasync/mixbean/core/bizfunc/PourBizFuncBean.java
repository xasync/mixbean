package com.xasync.mixbean.core.bizfunc;

import com.xasync.mixbean.core.BizFuncBeanDefinition;
import com.xasync.mixbean.core.BizFuncBeanParams;
import com.xasync.mixbean.core.MixBeanContext;
import com.xasync.mixbean.core.annotation.BizFuncBeanParamDefines;
import org.springframework.stereotype.Component;

@Component
public class PourBizFuncBean extends BizFuncBeanDefinition {
    public String ability() {
        return "pour";
    }

    public String provider() {
        return "cook";
    }

    public String desc() {
        return "pour some liquid";
    }


    @BizFuncBeanParamDefines({})
    public Object execute(MixBeanContext cxt, BizFuncBeanParams params) {
        return "pour";
    }
}
