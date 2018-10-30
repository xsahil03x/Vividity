package com.developerdru.vividity.screens.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.developerdru.vividity.data.remote.OperationStatus;

public class LoginVM extends ViewModel {

    private LiveData<OperationStatus> statusLiveData;

    public LoginVM() {

    }
}
