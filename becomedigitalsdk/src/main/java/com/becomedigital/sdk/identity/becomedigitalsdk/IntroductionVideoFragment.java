package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
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

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroductionVideoFragment extends Fragment {
    private final int REQUEST_PERMISSIONS = 34;
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private BDIVConfig config;

    public IntroductionVideoFragment() {
        // Required empty public constructor
    }


    private Boolean arePermissionsGranted() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (checkSelfPermission (getActivity ( ), PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionsGranted ( )) {
                ((MainBDIV) getActivity ( )).setResultError ("denied permits for the camera");
            } else {
                getActivity ( ).runOnUiThread (this::goToVideoRecording);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_fragment_introduction_video, container, false);
    }

    @Override
    public void onResume() {
        super.onResume ( );
        ((MainBDIV) getActivity ( )).setIsHomeActivity (false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        ImageView imageView = getActivity ( ).findViewById (R.id.imgGifIntroVideo);
        ((MainBDIV) getActivity ( )).changeColorToolbar (false);
        Glide.with (this)
                .load (R.drawable.liveness)
                .into (imageView);
        Button btnContinue = getActivity ( ).findViewById (R.id.btnRecordinVideo);
        Bundle arguments = getArguments();
        if (arguments != null) {
            config = (BDIVConfig) arguments.getSerializable("config");
        }
        btnContinue.setOnClickListener (view1 -> {
            if (checkCameraHardware (getActivity ( ))) {
                if (arePermissionsGranted ( )) {
                    requestPermissions (PERMISSIONS, REQUEST_PERMISSIONS);
                } else {
                    goToVideoRecording ( );
                }
            } else {
                ((MainBDIV) getActivity ( )).setResultError ("no camera on this device");
            }
        });
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager ( ).hasSystemFeature (PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void goToVideoRecording() {
        Bundle bundle = new Bundle ( );
        bundle.putSerializable("config", config);
        findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.fragRecordVideo, bundle);
    }
}
