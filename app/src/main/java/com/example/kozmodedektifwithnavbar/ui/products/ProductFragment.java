package com.example.kozmodedektifwithnavbar.ui.products;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.speech.tts.TextToSpeech;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kozmodedektifwithnavbar.R;
import com.example.kozmodedektifwithnavbar.adapters.AdapterProductDetail;
import com.example.kozmodedektifwithnavbar.base.BaseFragment;
import com.example.kozmodedektifwithnavbar.ui.content.ContentFragment;
import com.example.kozmodedektifwithnavbar.ui.search.NotFoundFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductFragment extends BaseFragment implements AdapterProductDetail.OnItemClickListener {

    private ProductViewModel mViewModel;

    private TextToSpeech textToSpeech;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @BindView(R.id.productImageView)
    ImageView productImageView;
    @BindView(R.id.listenButton)
    ImageView listenButton;
    @BindView(R.id.contentView)
    RecyclerView contentView;

    @BindView(R.id.likeButton)
    ImageButton likeButton;

    ArrayList<String> contentName;
    ArrayList<String> contentComment;
    ArrayList<Boolean> contentHarmful;
    ArrayAdapter arrayAdapter;
    AdapterProductDetail adapterProductDetail;

    Boolean ControlLike=false ;
    String docId;
    Boolean  isExists=false;
    public static ProductFragment newInstance() {
        return new ProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        View root = inflater.inflate(R.layout.fragment_product, container, false);

        ButterKnife.bind(this, root);
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        Bundle bundle = getArguments();
        TextView actionbar_title = (TextView)((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.fragmentTitle);
        actionbar_title.setText(bundle.getString("nameString"));
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gilroy-medium.ttf");
        actionbar_title.setTypeface(typeface);
        super.onActivityCreated(savedInstanceState);
        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                likeButton.setOnClickListener(v -> {
                    Bundle bundle = getArguments();
                colorControl(bundle.getString("barcodeString"));

            });
            }
        });
        textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String data = contentName.toString();
                Log.i("TTS", "button clicked: " + data);
                int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);

                if (speechStatus == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
            }

        });

        likeButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View arg0) {

                Bundle bundle = getArguments();
                colorControl(bundle.getString("barcodeString"));
                if(ControlLike==true){
                    unlikeProduct(bundle.getString("barcodeString"));
                }
                if(ControlLike==false){
                    likeProduct(bundle.getString("barcodeString"));
                }
            }
        });

             init();

    }

    public void  init(){

        FirebaseApp.initializeApp(getContext());
        firebaseFirestore = FirebaseFirestore.getInstance();
        Bundle bundle = getArguments();

        if(bundle.getString("barcodeString")!=null ){

            getProductInformation(bundle.getString("barcodeString"));
            contentName = new ArrayList<String>();
            contentComment = new ArrayList<String>();
            contentHarmful = new ArrayList<Boolean>();
            contentView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapterProductDetail = new AdapterProductDetail(contentName, contentComment, contentHarmful);
            DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(contentView.getContext(),DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.border_recycler));
            contentView.addItemDecoration(dividerItemDecoration);
            contentView.setAdapter(adapterProductDetail);
            adapterProductDetail.setOnItemClickListener(ProductFragment.this);
        }
        else
        {
            getProductInformation(bundle.getString("scanResult"));
            contentName = new ArrayList<String>();
            contentComment = new ArrayList<String>();
            contentHarmful = new ArrayList<Boolean>();
            contentView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapterProductDetail = new AdapterProductDetail(contentName, contentComment, contentHarmful);
            contentView.setAdapter(adapterProductDetail);
            adapterProductDetail.setOnItemClickListener(ProductFragment.this);
        }
}

    private void getProductInformation(String barcode) {
        colorControl(barcode);

        CollectionReference collectionReference = firebaseFirestore.collection("Product");
        collectionReference.whereEqualTo("barcodes", barcode).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null) {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                        Map<String, Object> data = snapshot.getData();
                        if (!data.isEmpty()) {
                            isExists = true;
                        }

                        String imageUrl = (String) data.get("imageUrl");
                        Picasso.get().load(imageUrl).into(productImageView);

                        /* productContentTextView.setText(data.get("barcode").toString());*/
                        ArrayList<Long> ctId = (ArrayList<Long>) data.get("content");
                        getContentInformation(ctId);
                    }
                }
                if (!isExists){
                    pushFragment(new NotFoundFragment(),getFragmentManager());
                }
            }
        });

    }

    private void getContentInformation(ArrayList<Long> contentIdList) {
        for (long id : contentIdList) {
            CollectionReference collectionReference = firebaseFirestore.collection("Content");
            collectionReference.whereEqualTo("id", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> data = snapshot.getData();
                            contentComment.add((String) data.get("comment"));

                            contentName.add((String) data.get("name"));
                            contentHarmful.add((Boolean) data.get("is_harmful"));
                        }
                        adapterProductDetail.notifyDataSetChanged();
                    }
                }
            });
        }
    }


    private void likeProduct(String Barcode) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String userUid = firebaseUser.getUid();
        Map<String, Object> docData = new HashMap<>();
        docData.put("barcode",Barcode );
        docData.put("userId",userUid);
        firebaseFirestore.collection("fav").add(docData);
        colorControl(Barcode);
    }
    private void unlikeProduct(String Barcode) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        findDocumentId(Barcode);
        colorControl(Barcode);
        if(docId!=null){
            firebaseFirestore.collection("fav").document(docId).delete();
            colorControl(Barcode);
        }
        colorControl(Barcode);

    }
    private void colorControl(String Barcode) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String userUid = firebaseUser.getUid();
        CollectionReference collectionReference = firebaseFirestore.collection("fav");
        collectionReference.whereEqualTo("barcode", Barcode).whereEqualTo("userId",userUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                       if( !data.isEmpty()){
                           likeButton.setColorFilter(Color.parseColor("#f5291d"));
                           /*likeButton.setBackgroundResource(R.drawable.favoritered);*/
                           ControlLike = true;
                       }else {
                           likeButton.setColorFilter(Color.BLACK);
                         /*  likeButton.setBackgroundResource(R.drawable.favorite);*/
                           ControlLike = false;
                       }
                    }
                }

            }
        });

    }


    private void findDocumentId(String Barcode) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String userUid = firebaseUser.getUid();
        CollectionReference collectionReference = firebaseFirestore.collection("fav");
        collectionReference.whereEqualTo("barcode", Barcode).whereEqualTo("userId",userUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                         docId = snapshot.getId();
                    }
                }
            }
        });

    }
    @Override
    public void onItemClick(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("commentString",contentComment.get(position));
            bundle.putString("commentName",contentName.get(position));
            pushFragment(new ContentFragment(),getFragmentManager(),bundle);

    }
}
