package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class SuspendedSaleResponse {
    private Long id;
    private String label;
    private String status; // SUSPENDED, RESUMED, or CANCELLED
    private String createdAt;
    private String workerUsername;
    private List<SuspendedSaleLineResponse> lines;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getWorkerUsername() { return workerUsername; }
    public void setWorkerUsername(String workerUsername) { this.workerUsername = workerUsername; }

    public List<SuspendedSaleLineResponse> getLines() { return lines; }
    public void setLines(List<SuspendedSaleLineResponse> lines) { this.lines = lines; }

    public static class SuspendedSaleLineResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    }
}
