package com.example.kozmodedektifwithnavbar.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.kozmodedektifwithnavbar.R;
import com.example.kozmodedektifwithnavbar.adapters.AdapterProduct;
import com.example.kozmodedektifwithnavbar.interfaces.OnBackPressed;

public class BaseFragment extends Fragment implements OnBackPressed {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    protected void pushFragment(Fragment targetFragment, FragmentManager fragmentManager){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, targetFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    protected void pushFragment(Fragment targetFragment, FragmentManager fragmentManager, Bundle bundle){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        targetFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.nav_host_fragment, targetFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
