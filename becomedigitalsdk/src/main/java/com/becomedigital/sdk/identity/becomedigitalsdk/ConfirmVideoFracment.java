package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.bumptech.glide.Glide;

import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmVideoFracment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_confirm_video_fracment, container, false);
    }

    private BDIVConfig config;
    private String urlVideoFile;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        ImageView imageView = getActivity ( ).findViewById (R.id.imgGifConfirmVideo);
        ((MainBDIV) getActivity ( )).changeColorToolbar (false);
        Glide.with (this)
                .load (R.drawable.liveness)
                .into (imageView);
        Bundle arguments = getArguments();
        if (arguments != null) {
            config = (BDIVConfig) arguments.getSerializable("config");
            urlVideoFile = arguments.getString("urlVideoFile");
        }
        Button btnContinue = getActivity ( ).findViewById (R.id.btnConfirmVideo);
        btnContinue.setOnClickListener (view1 -> {
            Bundle bundle = new Bundle ( );
            bundle.putSerializable("config", config);
            bundle.putString("urlVideoFile", urlVideoFile);
            findNavController (view).navigate (R.id.actionConfirm, bundle);
        });
        Button btnRetry = getActivity ( ).findViewById (R.id.btnRetry);
        btnRetry.setOnClickListener (view1 -> {
            findNavController (view).popBackStack ();
        });
    }
}
