package com.proconsi.electrobazar;

import android.app.Application;
import com.proconsi.electrobazar.network.RetrofitClient;

public class ElectrobazarApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
    }
}
