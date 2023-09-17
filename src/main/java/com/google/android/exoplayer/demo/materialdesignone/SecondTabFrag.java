package com.google.android.exoplayer.demo.materialdesignone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer.demo.R;


/**
 * Developed by root on 4/2/16.
 */
public class SecondTabFrag extends Fragment {

    public static SecondTabFrag getInstance(){
        SecondTabFrag  secondTabFragIns = new SecondTabFrag();
        return secondTabFragIns;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.secondtabcontent,container,false);
        return layout;
    }
}
