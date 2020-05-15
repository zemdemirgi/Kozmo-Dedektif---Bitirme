package com.example.kozmodedektifwithnavbar.ui.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kozmodedektifwithnavbar.R;
import com.example.kozmodedektifwithnavbar.adapters.AdapterProduct;
import com.example.kozmodedektifwithnavbar.base.BaseFragment;
import com.example.kozmodedektifwithnavbar.ui.content.ContentFragment;
import com.example.kozmodedektifwithnavbar.ui.products.ProductFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment implements  AdapterProduct.OnItemClickListener {

    private HomeViewModel homeViewModel;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<String> productNameFB;
    ArrayList<String> productBarcodeFB;
    ArrayList<String> productImageFB;
    AdapterProduct adapterProduct;
    ArrayList<String> ocrGelenName;
    ArrayList<List> ocrGelenContent;
    ArrayList<String> ocrGelenBarcode;
    @BindView(R.id.text_home)
    TextView textView;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.searchTextHome)
    EditText searchTextHome;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,@Nullable Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this,root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        TextView actionbar_title = (TextView)((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.fragmentTitle);
        actionbar_title.setText(R.string.title_home);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gilroy-medium.ttf");
        actionbar_title.setTypeface(typeface);

        textView.setTypeface(typeface);
        searchTextHome.setTypeface(typeface);

        searchTextHome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
        super.onActivityCreated(savedInstanceState);
        FirebaseApp.initializeApp(getContext());
        firebaseFirestore = FirebaseFirestore.getInstance();

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        init();
    }
    private void filter(String text) {
       ArrayList<String> filterdNames = new ArrayList<>();
        ArrayList<String> filterdId = new ArrayList<>();
        ArrayList<String> filterdImage = new ArrayList<>();
        Boolean ıd =false;
       int index=-1;
        for (String s : productNameFB) {
            if (s.toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
                ıd = true ;
            }
            index++;
            if (ıd==true){
                filterdId.add(productBarcodeFB.get(index));
                filterdImage.add(productImageFB.get(index));
            }
            ıd=false;
        }
        adapterProduct.filterList(filterdNames,filterdId,filterdImage);
    }
    private void init(){

    productNameFB = new ArrayList<>();
    productBarcodeFB = new ArrayList<>();
    productImageFB = new ArrayList<>();
    Bundle bundle = getArguments();
    firebaseFirestore = FirebaseFirestore.getInstance();
    getDataFromFirestore();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterProduct = new AdapterProduct(productNameFB,productImageFB,productBarcodeFB);

        recyclerView.setAdapter(adapterProduct);
        adapterProduct.setOnItemClickListener(this);
    }

    private void getDataFromFirestore() {

        CollectionReference collectionReference = firebaseFirestore.collection("Product");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null) {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                        Map<String,Object> data = snapshot.getData();

                        //Casting
                        String name = (String) data.get("name");
                        String imageUrl = (String) data.get("imageUrl");
                        String barcode = (String) data.get("barcodes");
                        System.out.println(name);
                        productNameFB.add(name);
                        productImageFB.add(imageUrl);
                        productBarcodeFB.add(barcode);
                        adapterProduct.notifyDataSetChanged();

                    }


                }

            }
        });
    }

    private void getDataFromFirestoreFilter(String name) {

        CollectionReference collectionReference = firebaseFirestore.collection("Product");

        collectionReference.whereArrayContains("name",name).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null) {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                        Map<String,Object> data = snapshot.getData();

                        //Casting
                        String name = (String) data.get("name");
                        String imageUrl = (String) data.get("imageUrl");
                        String barcode = (String) data.get("barcodes");
                        System.out.println(name);
                        productNameFB.add(name);
                        productImageFB.add(imageUrl);
                        productBarcodeFB.add(barcode);
                        adapterProduct.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        if (!productBarcodeFB.get(position).isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putString("barcodeString",productBarcodeFB.get(position));
            bundle.putString("nameString",productNameFB.get(position));
            pushFragment(new ProductFragment(),getFragmentManager(),bundle);
        }

    }


}
