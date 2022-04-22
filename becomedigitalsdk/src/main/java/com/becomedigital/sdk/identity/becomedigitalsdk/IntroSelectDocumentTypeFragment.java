package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;

import static androidx.navigation.Navigation.findNavController;


public class IntroSelectDocumentTypeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_intro_select_document_type, container, false);
    }

    private BDIVConfig config;
    private String urlVideoFile = "";

    @Override
    public void onResume() {
        super.onResume ( );
        ((MainBDIV) getActivity ( )).setIsHomeActivity (false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            config = (BDIVConfig) arguments.getSerializable("config");
            urlVideoFile = arguments.getString("urlVideoFile");
        }
        Button btnContinue = getActivity ( ).findViewById (R.id.btnContinueInfo);
        Bundle bundle = new Bundle ( );
        bundle.putSerializable("config", config);
        bundle.putString("urlVideoFile", urlVideoFile);
        btnContinue.setOnClickListener (view1 -> findNavController (view1).navigate (R.id.SelectCountryAction, bundle));
        ((MainBDIV) getActivity ( )).changeColorToolbar (false);
        String split = getString (R.string.splitValidationTypes);
        String[] validationTypesSubs = config.getValidationTypes ( ).split (split);
        LinearLayout lPassport = getActivity ( ).findViewById (R.id.lPassportInfo);
        LinearLayout lDNI = getActivity ( ).findViewById (R.id.lDNIInfo);
        LinearLayout lLicense = getActivity ( ).findViewById (R.id.lLicenseInfo);
        for (String validationTypesSub : validationTypesSubs) {
            switch (validationTypesSub) {
                case "DNI":
                    lDNI.setVisibility (View.VISIBLE);
                    break;
                case "PASSPORT":
                    lPassport.setVisibility (View.VISIBLE);
                    break;
                case "LICENSE":
                    lLicense.setVisibility (View.VISIBLE);
                    break;
            }
        }
    }
}
