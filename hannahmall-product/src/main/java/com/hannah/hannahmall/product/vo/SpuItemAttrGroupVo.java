package com.hannah.hannahmall.product.vo;

import com.hannah.hannahmall.common.model.es.Attr;
import lombok.Data;
import lombok.ToString;

import java.util.List;



@Data
@ToString
public class SpuItemAttrGroupVo {

    private String groupName;

    private List<Attr> attrs;

}
