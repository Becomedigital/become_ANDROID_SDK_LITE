package com.becomedigital.sdk.identity.becomedigitalsdk.callback;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivityBecome extends AppCompatActivity {

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }



    public void dismissProgressDialog() {
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }
}
