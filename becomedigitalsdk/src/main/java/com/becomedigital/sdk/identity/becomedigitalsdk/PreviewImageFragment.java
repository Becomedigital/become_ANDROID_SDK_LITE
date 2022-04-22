package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.utils.SharedParameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class PreviewImageFragment extends Fragment {

    private SharedParameters.typeDocument typeDocument;
    private BDIVConfig config;
    private String selectedCountry,
            selectedCountyCo2 = "",
            urlVideoFile = "",
            urlDocFront = "",
            urlDocBack = "";
    private boolean isFront;
    private boolean isSelfie;
    public PreviewImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate (R.layout.fragment_preview_image, container, false);
    }

    Button btnIsOkImage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        ((MainBDIV) getActivity ( )).changeColorToolbar (true);
        ImageView imgToPreview = getActivity ( ).findViewById (R.id.imgToPreview);
        ImageView imgProgresssbar = getActivity().findViewById(R.id.imgProgressBarDoc);
        Bundle arguments = getArguments();
        if (arguments != null) {
            config = (BDIVConfig) arguments.getSerializable("config");
            if (arguments.containsKey("typeDocument"))
                typeDocument = (SharedParameters.typeDocument) arguments.getSerializable("typeDocument");
            if (arguments.containsKey("urlVideoFile"))
                urlVideoFile = arguments.getString("urlVideoFile");
            if (arguments.containsKey("selectedCountyCo2"))
                selectedCountyCo2 = arguments.getString("selectedCountyCo2");
            if (arguments.containsKey("selectedCountry"))
                selectedCountry = arguments.getString("selectedCountry");
            if (arguments.containsKey("urlDocBack"))
                urlDocBack = arguments.getString("urlDocBack");
            if (arguments.containsKey("urlDocFront"))
                urlDocFront = arguments.getString("urlDocFront");
            if (arguments.containsKey("isFront"))
                isFront = getArguments().getBoolean("isFront");
            if (arguments.containsKey("isSelfie"))
                isSelfie = getArguments().getBoolean("isSelfie");

        }
        if(isSelfie){
            imgProgresssbar.setVisibility(View.INVISIBLE);
        }
        btnIsOkImage = getActivity ( ).findViewById (R.id.btnOkImage);
        Button btnRetry = getActivity ( ).findViewById (R.id.btnRetry);

            String pathToFile = getArguments ( ).getString ("pathImage");
            File imgFile = new File (pathToFile);

            if (imgFile.exists ( )) {
                Bitmap myBitmap = BitmapFactory.decodeFile (imgFile.getAbsolutePath ( ));
                imgToPreview.setImageBitmap (myBitmap);
            }

            btnRetry.setOnClickListener (view1 -> {
                    if (imgFile.exists ( )) {
                        imgFile.delete ( );
                    }
                findNavController (view).popBackStack ( );

            });
            btnIsOkImage.setOnClickListener (view12 -> {
                if(isSelfie){
                    ((MainBDIV) getActivity ( )).displayLoader (true );
                    ((MainBDIV) getActivity ( )).facialAuth(config, pathToFile);
                }else{
                    if (getArguments ( ).getBoolean ("isFront")) {
                        urlDocFront = pathToFile;
                    } else {
                        urlDocBack = pathToFile;
                    }
                    navigate ( );
                }
            });

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle
        outState.clear();
    }


    private void navigate() {
        Bundle bundle = new Bundle ( );
        bundle.putString ("urlDocFront", urlDocFront);
        bundle.putString ("urlDocBack", urlDocBack);
        bundle.putString("selectedCountry",selectedCountry);
        bundle.putString("selectedCountyCo2",selectedCountyCo2);
        bundle.putSerializable("typeDocument",typeDocument);
        bundle.putSerializable("config",config);
        bundle.putString ("urlVideoFile", urlVideoFile);
        if (typeDocument == SharedParameters.typeDocument.PASSPORT) {
            findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionFinish, bundle);
        } else {

            if (isFront) {
                bundle.putBoolean ("isFront", false);
                findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionCaptureBackDocument, bundle);
            } else {
                findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionFinish, bundle);
            }
        }
    }

}
