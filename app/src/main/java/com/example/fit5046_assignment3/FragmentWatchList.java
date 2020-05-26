package com.example.fit5046_assignment3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class FragmentWatchList extends Fragment {
    private View WatchList;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        WatchList = inflater.inflate(R.layout. ，container,false);
        return WatchList;
    }
}
