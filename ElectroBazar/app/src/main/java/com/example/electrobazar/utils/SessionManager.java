package com.example.electrobazar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.electrobazar.models.Worker;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "ElectroBazarPrefs";
    private static final String KEY_WORKER = "logged_worker";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void saveWorker(Worker worker) {
        String workerJson = gson.toJson(worker);
        editor.putString(KEY_WORKER, workerJson);
        editor.apply();
    }

    public Worker getWorker() {
        String workerJson = pref.getString(KEY_WORKER, null);
        if (workerJson == null)
            return null;
        return gson.fromJson(workerJson, Worker.class);
    }

    public void logout() {
        editor.remove(KEY_WORKER);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getWorker() != null;
    }

    public boolean hasPermission(String permission) {
        Worker worker = getWorker();
        if (worker == null || worker.getPermissions() == null)
            return false;
        return worker.getPermissions().contains(permission);
    }
}
