package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;

import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralIntroFragment extends Fragment {


    public GeneralIntroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_fragment_gneral_intro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        if (getActivity().getIntent().getExtras() != null) {
            BDIVConfig config = ((MainBDIV) getActivity()).getConfig();

            String split = getString (R.string.splitValidationTypes);
            String[] validationTypesSubs = config.getValidationTypes ( ).split (split);
            TextView textAnd = getActivity ( ).findViewById (R.id.textAnd);
            // controls document
            TextView textDocument = getActivity ( ).findViewById (R.id.textDocument);
            ImageView imgDocument = getActivity ( ).findViewById (R.id.imgDocument);
            // controls video
            ImageView imgVideo = getActivity ( ).findViewById (R.id.imgVideo);
            TextView textVideo = getActivity ( ).findViewById (R.id.textVideo);
            boolean actionContainsVideo = false;
            for (String validationTypesSub : validationTypesSubs) {
                if (validationTypesSub.equals ("VIDEO")) {
                    imgVideo.setVisibility (View.VISIBLE);
                    textVideo.setVisibility (View.VISIBLE);
                    textAnd.setVisibility (View.VISIBLE);
                    actionContainsVideo = true;
                }
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("config", config);
            Button btnContinue = getActivity ( ).findViewById (R.id.btnContinue);
            if (actionContainsVideo) {
                btnContinue.setOnClickListener (view1 -> findNavController (view1).navigate (R.id.continueAction, bundle));
            } else {
                btnContinue.setOnClickListener (view1 -> findNavController (view1).navigate (R.id.initFrag, bundle));
            }
        }else{
            ((MainBDIV) getActivity ( )).setResultError (getString(R.string.general_error));
        }
    }



    @Override
    public void onResume() {
        super.onResume ( );
        ((MainBDIV) getActivity ( )).setIsHomeActivity (true);
    }
}
