package com.hannah.hannahmall.product.web;

import com.hannah.hannahmall.product.service.SkuInfoService;
import com.hannah.hannahmall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;
@Slf4j
@Controller
public class ItemController {



    @Autowired
    SkuInfoService skuInfoService;
    /**
     * 商品详情
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String getSkuItem(@PathVariable("skuId")Long skuId,Model model) throws ExecutionException, InterruptedException {
        StopWatch sw=new StopWatch();
        sw.start();
        SkuItemVo  skuItemVo=skuInfoService.getSkuItem(skuId);
        model.addAttribute("skuItemVo",skuItemVo);
        sw.stop();
        log.info("****商品详情页耗时:{}毫秒*****",sw.getLastTaskTimeMillis());
        return "item";
    }

}
