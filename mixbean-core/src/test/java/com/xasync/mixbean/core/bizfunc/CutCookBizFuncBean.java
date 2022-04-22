package com.xasync.mixbean.core.bizfunc;

import com.xasync.mixbean.core.BizFuncBeanDefinition;
import com.xasync.mixbean.core.BizFuncBeanParams;
import com.xasync.mixbean.core.MixBeanContext;
import com.xasync.mixbean.core.annotation.BizFuncBeanParam;
import com.xasync.mixbean.core.annotation.BizFuncBeanParamDefines;
import org.springframework.stereotype.Component;


@Component
public class CutCookBizFuncBean extends BizFuncBeanDefinition {
    public String ability() {
        return "cut";
    }

    public String provider() {
        return "cook";
    }

    public String desc() {
        return "cut foods into cubes";
    }

    @BizFuncBeanParamDefines({
            @BizFuncBeanParam("food")
    })
    public Object execute(MixBeanContext cxt, BizFuncBeanParams params) {
        return null;
    }
}
