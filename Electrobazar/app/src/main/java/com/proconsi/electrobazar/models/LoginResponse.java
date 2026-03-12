package com.proconsi.electrobazar.models;

public class LoginResponse {
    private Worker worker;
    private String token;

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
