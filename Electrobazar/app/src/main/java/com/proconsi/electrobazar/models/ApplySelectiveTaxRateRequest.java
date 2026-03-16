package com.proconsi.electrobazar.models;

import java.util.List;

public class ApplySelectiveTaxRateRequest {
    private Long taxRateId;
    private List<Long> productIds;
    private List<Long> categoryIds;

    public Long getTaxRateId() { return taxRateId; }
    public void setTaxRateId(Long taxRateId) { this.taxRateId = taxRateId; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public List<Long> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<Long> categoryIds) { this.categoryIds = categoryIds; }
}
