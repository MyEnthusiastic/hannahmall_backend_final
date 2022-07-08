package com.hannah.hannahmall.search.controller;


import com.hannah.hannahmall.common.exception.HannahmallExceptinCodeEnum;
import com.hannah.hannahmall.common.utils.ResultBody;
import com.hannah.hannahmall.search.service.MallSearchService;
import com.hannah.hannahmall.search.vo.SearchParam;

import com.hannah.hannahmall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    /**
     * catalog3Id=167&keyword=手机&sort=saleCount_desc&brandId=1 可以直接用对象接收
     *
     * @param paramEo
     * @return
     */
    @GetMapping({"/", "list.html"})
    public String list(SearchParam paramEo, Model model, HttpServletRequest request) throws IOException {
        paramEo.set_queryString(request.getQueryString());
        SearchResult searchResult = mallSearchService.search(paramEo);
        model.addAttribute("result",searchResult);
        return "list";
    }


}
