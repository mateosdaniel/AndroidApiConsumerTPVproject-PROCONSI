package com.proconsi.electrobazar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static final String PREF_NAME = "ElectrobazarPrefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_WORKER_ID = "worker_id";
    private static final String KEY_ROLE = "worker_role";
    private static final String KEY_USERNAME = "username";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public void saveWorkerDetails(Long workerId, String role, String username) {
        editor.putLong(KEY_WORKER_ID, workerId != null ? workerId : -1L);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public Long getWorkerId() {
        long id = pref.getLong(KEY_WORKER_ID, -1L);
        return id == -1L ? null : id;
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        String token = getToken();
        if (token == null || token.split("\\.").length < 2) {
            return permissions;
        }

        try {
            String payloadBase64 = token.split("\\.")[1];
            byte[] decodedBytes = Base64.decode(payloadBase64, Base64.URL_SAFE);
            String payloadJson = new String(decodedBytes, "UTF-8");
            JSONObject jsonObject = new JSONObject(payloadJson);
            JSONArray permsArray = jsonObject.optJSONArray("permissions");
            if (permsArray != null) {
                for (int i = 0; i < permsArray.length(); i++) {
                    permissions.add(permsArray.getString(i));
                }
            }
            Log.d("SessionManager", "Extracted permissions: " + permissions);
        } catch (Exception e) {
            Log.e("SessionManager", "Error decoding JWT token for permissions", e);
        }
        return permissions;
    }

    public boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }
}
