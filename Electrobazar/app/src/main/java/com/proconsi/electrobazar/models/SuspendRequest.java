package com.proconsi.electrobazar.models;

import java.util.List;

public class SuspendRequest {
    private List<SuspendedSaleLineRequest> lines;
    private String label;

    public SuspendRequest(List<SuspendedSaleLineRequest> lines, String label) {
        this.lines = lines;
        this.label = label;
    }

    public List<SuspendedSaleLineRequest> getLines() { return lines; }
    public void setLines(List<SuspendedSaleLineRequest> lines) { this.lines = lines; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
