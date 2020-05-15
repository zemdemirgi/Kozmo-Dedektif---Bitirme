package com.example.kozmodedektifwithnavbar.ui.search;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kozmodedektifwithnavbar.R;
import com.example.kozmodedektifwithnavbar.activities.ScannerActivity;
import com.example.kozmodedektifwithnavbar.adapters.AdapterProduct;
import com.example.kozmodedektifwithnavbar.base.BaseFragment;
import com.example.kozmodedektifwithnavbar.ui.home.HomeFragment;
import com.example.kozmodedektifwithnavbar.ui.products.ProductFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends BaseFragment {

    private static final int SCANNER_REQUEST_CODE = 69;
    private SearchViewModel searchViewModel;
    private static final int ZBAR_CAMERA_PERMISSION = 1;

    @BindView(R.id.scan_barcode)
    ImageButton scanBarcode;

    @BindView(R.id.scan_text)
    ImageButton scanText;

    ArrayList<String> productNameFB;
    ArrayList<String> productImageFB;
    ArrayList<String> productBarcodeFB;
    AdapterProduct adapterProduct;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

/*    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this,root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        TextView actionbar_title = (TextView)((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.fragmentTitle);
        actionbar_title.setText(R.string.title_search);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gilroy-medium.ttf");
        actionbar_title.setTypeface(typeface);
        searchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                scanText.setOnClickListener(v -> {
                    pushFragment(new ProductFragment(),getFragmentManager());
                });

                scanBarcode.setOnClickListener( v -> {

                    barcodeReadClick();
                });

                scanText.setOnClickListener( v -> {
                   textReadClick();
                });
            }
        });
        init();
    }

    private void barcodeReadClick() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(new Intent(getActivity(), ScannerActivity.class),SCANNER_REQUEST_CODE);

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        }

    }

    private void textReadClick() {
        Bundle bundle = new Bundle();

        pushFragment(new HomeFragment(),getFragmentManager(),bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SCANNER_REQUEST_CODE){
            Bundle bundle = data.getExtras();
            ProductFragment productFragment = new ProductFragment();
            productFragment.setArguments(bundle);
            pushFragment(productFragment,getParentFragmentManager());
        }
    }

    private void init() {
        productNameFB = new ArrayList<>();
        productImageFB = new ArrayList<>();
        productBarcodeFB = new ArrayList<>();
    /*    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterProduct = new AdapterProduct(productNameFB,productImageFB,productBarcodeFB);
        recyclerView.setAdapter(adapterProduct);
*/
    }

}
