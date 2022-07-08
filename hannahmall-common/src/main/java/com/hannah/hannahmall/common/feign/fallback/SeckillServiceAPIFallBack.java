package com.hannah.hannahmall.common.feign.fallback;

import com.hannah.hannahmall.common.exception.HannahmallExceptinCodeEnum;
import com.hannah.hannahmall.common.feign.SeckillServiceAPI;
import com.hannah.hannahmall.common.feign.vo.SecSessionSkuVO;
import com.hannah.hannahmall.common.utils.ResultBody;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 此接口实现需要放在接口调用方的spring容器
 * 中
 */
@Component
public class SeckillServiceAPIFallBack implements SeckillServiceAPI {
    @Override
    public ResultBody<SecSessionSkuVO> getSecSessionSkuVOBySkuId(Long skuId) {
        return new ResultBody<>(HannahmallExceptinCodeEnum.TOOMANG_REQUEST_EXCEPTION);
    }

    @Override
    public ResultBody<List<SecSessionSkuVO>> getCurrentSecSessionSkuVOS() {
        return new ResultBody<>(HannahmallExceptinCodeEnum.TOOMANG_REQUEST_EXCEPTION);
    }
}
