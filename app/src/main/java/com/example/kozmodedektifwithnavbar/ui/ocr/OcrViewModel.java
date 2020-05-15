package com.example.kozmodedektifwithnavbar.ui.ocr;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OcrViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OcrViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ocr fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
