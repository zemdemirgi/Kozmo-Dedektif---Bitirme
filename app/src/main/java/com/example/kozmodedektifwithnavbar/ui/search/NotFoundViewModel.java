package com.example.kozmodedektifwithnavbar.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotFoundViewModel extends ViewModel {
    private MutableLiveData<String> nfText;

    public NotFoundViewModel() {
        nfText = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return nfText;
    }
}
