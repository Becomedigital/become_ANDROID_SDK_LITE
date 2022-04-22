package com.becomedigital.sdk.identity.becomedigitalsdkiv;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.becomedigital.sdk.identity.becomedigitalsdk.MainBDIV;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeCallBackManager;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeInterfaseCallback;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeResponseManager;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.LoginError;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private BecomeCallBackManager mCallbackManager = BecomeCallBackManager.createNew();

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.become_icon);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        decodeResource.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        TextView textResponse = findViewById(R.id.textReponse);
        EditText textClientSecret = findViewById(R.id.cliensecretText);
        EditText textClientId = findViewById(R.id.clienidText);
        EditText textContractId = findViewById(R.id.ContractIdText);
        EditText textVlidationType = findViewById(R.id.validationType);
        EditText textUserId = findViewById(R.id.textUserId);
        Button btnAut = findViewById(R.id.btnAuth);
        ImageView imgSelfie, imgFront, imgBack;
        imgSelfie = findViewById(R.id.imgSelfie);
        imgFront = findViewById(R.id.imgFront);
        imgBack = findViewById(R.id.imgBack);
        btnAut.setOnClickListener(view -> {
            String validatiopnTypes = textVlidationType.getText().toString().isEmpty() ? "VIDEO/PASSPORT/DNI/LICENSE" : textVlidationType.getText().toString();
            String clientSecret = textClientSecret.getText().toString().isEmpty() ? "FKLDM63GPH89TISBXNZ4YJUE57WRQA25" : textClientSecret.getText().toString();
            String clientId = textClientId.getText().toString().isEmpty() ? "acc_demo" : textClientId.getText().toString();
            String contractId = textContractId.getText().toString().isEmpty() ? "2" : textContractId.getText().toString();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            String inActiveDate = format1.format(currentTime);
            String userId = textUserId.getText().toString().isEmpty() ? inActiveDate : textUserId.getText().toString();


            BecomeResponseManager.getInstance().startAutentication(MainActivity.this,
                    new BDIVConfig(clientId,
                            clientSecret,
                            contractId,
                            validatiopnTypes,
                            true,
                            byteArray,
                            userId
                    ));
            
            BecomeResponseManager.getInstance().registerCallback(mCallbackManager, new BecomeInterfaseCallback() {
                @Override
                public void onSuccess(final ResponseIV responseIV) {
                    String pathToFileSelfie = responseIV.getSelfiImageUrlLocal();
                    File imgFileSelfie = new File (pathToFileSelfie);
                    if (imgFileSelfie.exists ( )) {
                        Bitmap myBitmap = BitmapFactory.decodeFile (imgFileSelfie.getAbsolutePath ( ));
                        imgSelfie.setImageBitmap (myBitmap);
                    }

                    String pathToFileFront = responseIV.getFrontImgUrlLocal();
                    File imgFileFront = new File (pathToFileFront);
                    if (imgFileFront.exists ( )) {
                        Bitmap myBitmap = BitmapFactory.decodeFile (imgFileFront.getAbsolutePath ( ));
                        imgFront.setImageBitmap (myBitmap);
                    }

                    String pathToFileBack = responseIV.getBackImgUrlLocal();
                    File imgFileBack = new File (pathToFileBack);
                    if (imgFileBack.exists ( )) {
                        Bitmap myBitmap = BitmapFactory.decodeFile (imgFileBack.getAbsolutePath ( ));
                        imgBack.setImageBitmap (myBitmap);
                    }

                    textResponse.setText(responseIV.toString());
//                    //Log.d("responseIV", textFinal);
                }

                @Override
                public void onCancel() {
                    textResponse.setText("Cancelado por el usuario ");

                }

                @Override
                public void onError(LoginError pLoginError) {
                    textResponse.setText(pLoginError.getMessage());
//                    //Log.d("Error", pLoginError.getMessage());
                }

            });
        });

    }
}
