package com.becomedigital.sdk.identity.becomedigitalsdk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static androidx.navigation.Navigation.findNavController;


public class CancelFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_cancel, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        Button btnUndo = getActivity ( ).findViewById (R.id.btnUndo);
        Button btnCancel = getActivity ( ).findViewById (R.id.btnCancelProcess);
        btnUndo.setOnClickListener (view12 -> {
            ((MainBDIV) getActivity ( )).showImgBtnCancel ( );
            findNavController (view12).popBackStack ( );
        });
        btnCancel.setOnClickListener (view1 -> ((MainBDIV) getActivity ( )).setResulUserCanceled ( ));
    }
}
