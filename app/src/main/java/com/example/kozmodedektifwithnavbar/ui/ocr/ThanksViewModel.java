package com.example.kozmodedektifwithnavbar.ui.ocr;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ThanksViewModel extends ViewModel {
    private MutableLiveData<String> tText;

    public ThanksViewModel() {
        tText = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return tText;
    }
}
