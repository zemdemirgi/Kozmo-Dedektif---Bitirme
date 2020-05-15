package com.example.kozmodedektifwithnavbar.ui.favourites;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kozmodedektifwithnavbar.R;
import com.example.kozmodedektifwithnavbar.adapters.AdapterProduct;
import com.example.kozmodedektifwithnavbar.adapters.AdapterProductDetail;
import com.example.kozmodedektifwithnavbar.base.BaseFragment;
import com.example.kozmodedektifwithnavbar.ui.products.ProductFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteFragment extends BaseFragment implements AdapterProductDetail.OnItemClickListener{

    private FavouriteViewModel favouriteViewModel;

    @BindView(R.id.text_favourite)
    TextView textView;

    ArrayList<String> productNameFB;
    ArrayList<String> productImageFB;
    ArrayList<String> productBarcodeFB;
    AdapterProduct adapterProduct;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        favouriteViewModel = ViewModelProviders.of(this).get(FavouriteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favourite, container, false);
        ButterKnife.bind(this,root);
        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        TextView actionbar_title = (TextView)((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.fragmentTitle);
        actionbar_title.setText(R.string.title_favourite);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gilroy-medium.ttf");
        actionbar_title.setTypeface(typeface);
        favouriteViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        init();
}

    private void getFavourite(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String userUid = firebaseUser.getUid();
        CollectionReference collectionReference = firebaseFirestore.collection("fav");
        collectionReference.whereEqualTo("userId", userUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = snapshot.getData();
                            String bId = (String) data.get("barcode");
                            getLikeProduct(bId);
                            adapterProduct.notifyDataSetChanged();
                        }
                    }
                }
        });

    }
    private void getLikeProduct(String BarcodeId) {

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = firebaseFirestore.collection("Product");
            collectionReference.whereEqualTo("barcodes", BarcodeId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = snapshot.getData();
                            String name = (String) data.get("name");
                            String imageUrl = (String) data.get("imageUrl");
                            productNameFB.add(name);
                            productImageFB.add(imageUrl);
                            productBarcodeFB.add(BarcodeId);
                            adapterProduct.notifyDataSetChanged();
                        }
                        adapterProduct.notifyDataSetChanged();
                    }
                }
            });
        }


    private void init() {
        productNameFB = new ArrayList<>();
        productImageFB = new ArrayList<>();
        productBarcodeFB = new ArrayList<>();
        getFavourite();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterProduct = new AdapterProduct(productNameFB,productImageFB,productBarcodeFB);
        recyclerView.setAdapter(adapterProduct);

    }
    @Override
    public void onItemClick(int position) {
System.out.println("dnemeeeeee");
        if (!productBarcodeFB.get(position).isEmpty()){
            Bundle bundle = new Bundle();
            System.out.println(productBarcodeFB.get(position));
            bundle.putString("barcodeString",productBarcodeFB.get(position));
            pushFragment(new ProductFragment(),getFragmentManager(),bundle);
        }
    }


}
