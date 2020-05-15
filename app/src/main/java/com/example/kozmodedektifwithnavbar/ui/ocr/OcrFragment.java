package com.example.kozmodedektifwithnavbar.ui.ocr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.example.kozmodedektifwithnavbar.MainActivity;
import com.example.kozmodedektifwithnavbar.R;
import com.example.kozmodedektifwithnavbar.activities.OcrCaptureActivity;
import com.example.kozmodedektifwithnavbar.activities.ScannerActivity;
import com.example.kozmodedektifwithnavbar.activities.SignInActivity;
import com.example.kozmodedektifwithnavbar.base.BaseFragment;
import com.example.kozmodedektifwithnavbar.ui.home.HomeFragment;
import com.example.kozmodedektifwithnavbar.ui.products.ProductFragment;
import com.example.kozmodedektifwithnavbar.ui.search.SearchViewModel;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OcrFragment extends BaseFragment {

    private OcrViewModel ocrViewModel;
    private static final int RC_OCR_CAPTURE = 9003;

    private static final int SCANNER_REQUEST_CODE = 69;
    private static final int ZBAR_CAMERA_PERMISSION = 1;

    @BindView(R.id.use_flash)
    CompoundButton useFlash;

    @BindView(R.id.text_value)
    EditText textValue;

    @BindView(R.id.copy_text_button)
    Button copyButton;

    @BindView(R.id.mail_text_button)
    Button mailTextButton;

    @BindView(R.id.read_text_button)
    Button readTextButton;

    @BindView(R.id.save_firebase)
    Button saveTextButton;

    @BindView(R.id.button3)
    Button addBarcode;

    @BindView(R.id.productName)
    EditText productName;

    @BindView(R.id.barcodeText)
    TextView barcodeText;

    private FirebaseFirestore firebaseFirestore;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ocrViewModel =
                ViewModelProviders.of(this).get(OcrViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ocr, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        TextView actionbar_title = (TextView)((AppCompatActivity)getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.fragmentTitle);
        actionbar_title.setText(R.string.title_ocr);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gilroy-medium.ttf");
        actionbar_title.setTypeface(typeface);
        useFlash.setTypeface(typeface);
        textValue.setTypeface(typeface);
        copyButton.setTypeface(typeface);
        mailTextButton.setTypeface(typeface);
        readTextButton.setTypeface(typeface);
        saveTextButton.setTypeface(typeface);
        addBarcode.setTypeface(typeface);
        productName.setTypeface(typeface);
        barcodeText.setTypeface(typeface);;
        FirebaseApp.initializeApp(getContext());
        ocrViewModel.getText().observe(getViewLifecycleOwner(), s -> {
        });
        init();
    }

    private void init() {

        addBarcode.setOnClickListener( v -> {

            barcodeReadClick();

        });
        readTextButton.setOnClickListener(v -> {
            // launch Ocr capture activity.
            Intent intent = new Intent(getContext(), OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, true);
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_OCR_CAPTURE);
        });

        copyButton.setOnClickListener(v -> {
            android.content.ClipboardManager clipboard =
                    (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", textValue.getText().toString());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getContext(), R.string.clipboard_copy_successful_message, Toast.LENGTH_SHORT).show();
        });
        mailTextButton.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_SUBJECT, "Text Read");
            i.putExtra(Intent.EXTRA_TEXT, textValue.getText().toString());
            try {
                startActivity(Intent.createChooser(i, getString(R.string.mail_intent_chooser_text)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getContext(),
                        R.string.no_email_client_error, Toast.LENGTH_SHORT).show();
            }
        });
        saveTextButton.setOnClickListener(v -> {
             firebaseFirestore = FirebaseFirestore.getInstance();
            if (textValue.getText().toString() != null) {
                String[] currencies = textValue.getText().toString().split("\\n");
                ArrayList<String> theList = new ArrayList<String>(Arrays.asList(currencies));
                HashMap<String, Object> Data = new HashMap<>();
                Data.put("content",theList);
                Data.put("name",productName.getText().toString());
                Data.put("barcode",barcodeText.getText().toString());
                firebaseFirestore.collection("Ocr").add(Data);

            }
            pushFragment(new ThanksFragment(),getFragmentManager());
        });
    }

 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == SCANNER_REQUEST_CODE){

         barcodeText.setText(""+data.getStringExtra("scanResult"));
          }

        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    textValue.setText(text);
                    Log.d(TAG, "Text read: " + text);
                } else {
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            }
        }


        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void barcodeReadClick() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(new Intent(getActivity(), ScannerActivity.class),SCANNER_REQUEST_CODE);

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        if (textValue.getText().toString().isEmpty()) {
            copyButton.setVisibility(View.GONE);
            mailTextButton.setVisibility(View.GONE);
            saveTextButton.setVisibility(View.GONE);

        } else {
            useFlash.setVisibility(View.GONE);
            readTextButton.setVisibility(View.GONE);
            addBarcode.setVisibility(View.GONE);
            copyButton.setVisibility(View.VISIBLE);
            mailTextButton.setVisibility(View.VISIBLE);
            saveTextButton.setVisibility(View.VISIBLE);
        }

    }


}
