package com.hannah.hannahmall.product.vo;

import lombok.Data;

@Data
public class SpuItemAttrGroupItem {
    private Long spuId;
    private Long attrGroupId;
    private String  attrGroupName;
    private Long attrId;
    private String attrName;
    private String attrValue;

}
