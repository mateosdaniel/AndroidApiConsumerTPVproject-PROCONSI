package com.proconsi.electrobazar.models;

public class CustomerRequest {
    private String name;
    private String taxId;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private String type;
    private Boolean active;
    private Boolean hasRecargoEquivalencia;
    private Long tariffId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getHasRecargoEquivalencia() { return hasRecargoEquivalencia; }
    public void setHasRecargoEquivalencia(Boolean hasRecargoEquivalencia) { this.hasRecargoEquivalencia = hasRecargoEquivalencia; }

    public Long getTariffId() { return tariffId; }
    public void setTariffId(Long tariffId) { this.tariffId = tariffId; }
}
