package com.example.kozmodedektifwithnavbar.ui.search;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.kozmodedektifwithnavbar.R;
import com.example.kozmodedektifwithnavbar.base.BaseFragment;
import com.example.kozmodedektifwithnavbar.ui.ocr.ThanksViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotFoundFragment extends BaseFragment {

    private NotFoundViewModel notFoundViewModel;

    @BindView(R.id.thanksText)
    TextView notFoundText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notFoundViewModel = ViewModelProviders.of(this).get(NotFoundViewModel.class);
        View root = inflater.inflate(R.layout.fragment_thanks, container, false);
        ButterKnife.bind(this,root);
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        TextView actionbar_title = (TextView)((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.fragmentTitle);
        actionbar_title.setText(R.string.title_notFoundProduct);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gilroy-medium.ttf");
        actionbar_title.setTypeface(typeface);
        notFoundText.setTypeface(typeface);
        notFoundViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        notFoundText.setText("Ürün ne yazık ki bulunamadı.");
    }
}
