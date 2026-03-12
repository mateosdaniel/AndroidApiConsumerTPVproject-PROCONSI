package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class IneIpcResponse {
    private List<IneDataPoint> data;

    public List<IneDataPoint> getData() { return data; }
    public void setData(List<IneDataPoint> data) { this.data = data; }

    public static class IneDataPoint {
        private BigDecimal valor;
        private Integer anyo;
        private Integer mes;

        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }

        public Integer getAnyo() { return anyo; }
        public void setAnyo(Integer anyo) { this.anyo = anyo; }

        public Integer getMes() { return mes; }
        public void setMes(Integer mes) { this.mes = mes; }
    }
}
