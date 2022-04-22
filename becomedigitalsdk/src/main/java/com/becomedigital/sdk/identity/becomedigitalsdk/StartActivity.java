package com.becomedigital.sdk.identity.becomedigitalsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeInterfaseCallback;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.LoginError;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeResponseManager;

public class StartActivity extends AppCompatActivity {

    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String KEY_RESPONSE = "ResponseIV";
    public static final String KEY_ERROR = "ErrorMessage";
    private static final int REQUEST_CODE = 7778;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_start);
        if (getIntent().getExtras() != null) {
            BecomeResponseManager.getInstance().login(this, (BDIVConfig) getIntent().getSerializableExtra("BDIVConfig"));
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            BecomeInterfaseCallback callback = BecomeResponseManager.getCallback();
            if (callback != null) {

                if (resultCode == RESULT_OK) {
                    callback.onSuccess((ResponseIV) data.getSerializableExtra("ResponseIV"));
                } else if (resultCode == RESULT_CANCELED) {
                    callback.onCancel();
                } else {
                    LoginError loginError = new LoginError();
                    if (data.hasExtra(KEY_ERROR))
                        loginError.setMessage(data.getStringExtra(KEY_ERROR));
                    callback.onError(loginError);
                }
            }
            finish();
        }
    }
}
