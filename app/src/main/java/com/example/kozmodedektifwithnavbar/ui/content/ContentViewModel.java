package com.example.kozmodedektifwithnavbar.ui.content;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContentViewModel extends ViewModel {
    private MutableLiveData<String> cText;

    public ContentViewModel() {
        cText = new MutableLiveData<>();
        /* mText.setValue("This is product fragment");*/
    }

    public LiveData<String> getText() {
        return cText;
    }
}
