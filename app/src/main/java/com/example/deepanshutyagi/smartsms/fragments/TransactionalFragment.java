package com.example.deepanshutyagi.smartsms.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.deepanshutyagi.smartsms.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionalFragment extends Fragment {


    public TransactionalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getContext(), "Transactional Fragment", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_transactional, container, false);
    }

}
