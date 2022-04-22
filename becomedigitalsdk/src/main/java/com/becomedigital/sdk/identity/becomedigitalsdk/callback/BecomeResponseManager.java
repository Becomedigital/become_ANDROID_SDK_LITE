package com.becomedigital.sdk.identity.becomedigitalsdk.callback;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;


import com.becomedigital.sdk.identity.becomedigitalsdk.MainBDIV;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;
import com.becomedigital.sdk.identity.becomedigitalsdk.R;
import com.becomedigital.sdk.identity.becomedigitalsdk.StartActivity;

import java.lang.ref.WeakReference;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class BecomeResponseManager {
    public static final String KEY_RESPONSE = "ResponseIV";
    private static final int REQUEST_CODE = 7778;
    private static WeakReference<BecomeCallBackManager> mCallbackManagerWeakReference = new WeakReference<>(null);
    private static BecomeResponseManager sBecomeManager;
    private WeakReference<Activity> mActivityWeakReference = new WeakReference<>(null);

    public static BecomeInterfaseCallback getCallback() {
        BecomeCallBackManager becomeCallBackManager = (BecomeCallBackManager) mCallbackManagerWeakReference.get();
        if (becomeCallBackManager == null) {
            return null;
        }
        return becomeCallBackManager.getCallback();
    }

    public void startAutentication(Activity activity, BDIVConfig bDIVConfig) {
        Intent intent = new Intent(activity, StartActivity.class);
        intent.putExtra("BDIVConfig", (Parcelable) bDIVConfig);
        activity.startActivity(intent);
    }

    public void login(Activity activity, BDIVConfig bDIVConfig) {
        this.mActivityWeakReference = new WeakReference<>(activity);
        startActivity(MainBDIV.class, bDIVConfig);
    }

    public void registerCallback(BecomeCallBackManager becomeCallBackManager, BecomeInterfaseCallback becomeInterfaseCallback) {
        becomeCallBackManager.setCallback(becomeInterfaseCallback);
        mCallbackManagerWeakReference = new WeakReference<>(becomeCallBackManager);
    }

    public static synchronized BecomeResponseManager getInstance() {
        BecomeResponseManager becomeResponseManager;
        synchronized (BecomeResponseManager.class) {
            if (sBecomeManager == null) {
                sBecomeManager = new BecomeResponseManager();
            }
            becomeResponseManager = sBecomeManager;
        }
        return becomeResponseManager;
    }

    /* access modifiers changed from: 0000 */
    public boolean onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            BecomeInterfaseCallback callback = getCallback();
            if (callback != null) {

                if (resultCode == RESULT_OK) {
                    callback.onSuccess((ResponseIV) data.getSerializableExtra("ResponseIV"));
                } else if (resultCode == RESULT_CANCELED) {
                    callback.onCancel();
                } else {
                    LoginError loginError = new LoginError();
                    if (data.hasExtra(LoginError.class.getSimpleName()))
                        loginError.setMessage(data.getStringExtra(LoginError.class.getSimpleName()));
                    callback.onError(loginError);
                }
                return true;
            }
        }
        return false;
    }

    private <T extends BaseActivityBecome> void startActivity(Class<MainBDIV> pClass, BDIVConfig bDIVConfig) {
        Activity activity = (Activity) this.mActivityWeakReference.get();
        if (activity != null) {
            Intent intent = new Intent(activity, pClass);
            intent.putExtra("BDIVConfig", (Parcelable) bDIVConfig);
            activity.startActivityForResult(intent, REQUEST_CODE);
            activity.overridePendingTransition(R.anim.slide_in_from_bottom, 0);
        }
    }
}
