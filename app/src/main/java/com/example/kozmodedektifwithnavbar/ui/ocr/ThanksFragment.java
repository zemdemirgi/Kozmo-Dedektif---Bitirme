package com.example.kozmodedektifwithnavbar.ui.ocr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.kozmodedektifwithnavbar.R;
import com.example.kozmodedektifwithnavbar.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThanksFragment extends BaseFragment {
    private ThanksViewModel thanksViewModel;
    @BindView(R.id.thanksText)
    TextView thanksText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thanksViewModel = ViewModelProviders.of(this).get(ThanksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_thanks, container, false);
        ButterKnife.bind(this,root);
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        thanksViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        thanksText.setText("Yardımın için teşekkürler.En kısa sürede ürün eklenecektir.:)");
    }

}
