package com.proconsi.electrobazar.models;

public class CompanySettings {
    private Long id;
    private String appName;
    private String name;
    private String cif;
    private String address;
    private String city;
    private String postalCode;
    private String phone;
    private String email;
    private String website;
    private String registroMercantil;
    private String invoiceFooterText;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCif() { return cif; }
    public void setCif(String cif) { this.cif = cif; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getRegistroMercantil() { return registroMercantil; }
    public void setRegistroMercantil(String registroMercantil) { this.registroMercantil = registroMercantil; }

    public String getInvoiceFooterText() { return invoiceFooterText; }
    public void setInvoiceFooterText(String invoiceFooterText) { this.invoiceFooterText = invoiceFooterText; }
}
