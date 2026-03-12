package com.proconsi.electrobazar.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.proconsi.electrobazar.models.LoginResponse;
import com.proconsi.electrobazar.models.Worker;
import com.proconsi.electrobazar.repositories.AuthRepository;
import com.proconsi.electrobazar.utils.SessionManager;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final SessionManager sessionManager;
    private final MutableLiveData<String> loginTrigger = new MutableLiveData<>();
    private final LiveData<AuthRepository.LoginResult> loginResult;

    private String currentUsername;
    private String currentPassword;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
        sessionManager = new SessionManager(application);
        
        loginResult = Transformations.switchMap(loginTrigger, input -> 
            authRepository.login(currentUsername, currentPassword)
        );
    }

    public void login(String username, String password) {
        this.currentUsername = username;
        this.currentPassword = password;
        loginTrigger.setValue("trigger");
    }

    public LiveData<AuthRepository.LoginResult> getLoginResult() {
        return loginResult;
    }

    public void saveSession(LoginResponse response) {
        sessionManager.saveToken(response.getToken());
        Worker worker = response.getWorker();
        String roleName = (worker.getRole() != null) ? worker.getRole().getName() : "WORKER";
        sessionManager.saveWorkerDetails(worker.getId(), roleName, worker.getUsername());
    }
}
