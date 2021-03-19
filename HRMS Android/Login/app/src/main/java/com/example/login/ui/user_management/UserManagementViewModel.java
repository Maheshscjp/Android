package com.example.login.ui.user_management;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserManagementViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UserManagementViewModel() {

        mText = new MutableLiveData<>();
        mText.setValue("This is user management fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
