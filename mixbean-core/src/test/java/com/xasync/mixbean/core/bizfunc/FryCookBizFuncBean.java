package com.xasync.mixbean.core.bizfunc;

import com.xasync.mixbean.core.BizFuncBeanDefinition;
import com.xasync.mixbean.core.BizFuncBeanParams;
import com.xasync.mixbean.core.MixBeanContext;
import com.xasync.mixbean.core.annotation.BizFuncBeanParamDefines;
import org.springframework.stereotype.Component;


@Component
public class FryCookBizFuncBean extends BizFuncBeanDefinition {
    public String ability() {
        return "fry";
    }

    public String provider() {
        return "cook";
    }

    public String desc() {
        return "stir-fry";
    }

    @BizFuncBeanParamDefines({})
    public Object execute(MixBeanContext cxt, BizFuncBeanParams params) {
        return null;
    }
}
