package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.utils.SharedParameters;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinishFragment extends Fragment {


    public FinishFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_finish, container, false);
    }
    private SharedParameters.typeDocument typeDocument;
    private BDIVConfig config;
    private String selectedCountry,
            selectedCountyCo2,
            urlVideoFile = "",
            urlDocFront = "",
            urlDocBack = "";
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            config = (BDIVConfig) arguments.getSerializable("config");
            typeDocument = (SharedParameters.typeDocument) arguments.getSerializable("typeDocument");
            if (arguments.containsKey("urlVideoFile"))
                urlVideoFile = arguments.getString("urlVideoFile");
            selectedCountyCo2 = arguments.getString("selectedCountyCo2");
            selectedCountry = arguments.getString("selectedCountry");
            if (arguments.containsKey("urlDocBack"))
                urlDocBack = arguments.getString("urlDocBack");
            if (arguments.containsKey("urlDocFront"))
                urlDocFront = arguments.getString("urlDocFront");
        }
        Button btnUpload = getActivity ().findViewById (R.id.btnUpload);
        btnUpload.setOnTouchListener ((v, event) -> {
            switch (event.getAction ( )) {
                case MotionEvent.ACTION_DOWN:
                    getActivity ( ).runOnUiThread (() -> {
                        ((MainBDIV) getActivity ( )).displayLoader ( false);
                    });
                    break;
                case MotionEvent.ACTION_UP:
                    ((MainBDIV) getActivity ( )).addDataServer(config,typeDocument,urlDocFront,selectedCountyCo2,urlDocBack, urlVideoFile);
                    break;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume ( );
        ((MainBDIV) getActivity ( )).setIsHomeActivity (true);
        ((MainBDIV) getActivity ( )).changeColorToolbar (false);
    }
}
