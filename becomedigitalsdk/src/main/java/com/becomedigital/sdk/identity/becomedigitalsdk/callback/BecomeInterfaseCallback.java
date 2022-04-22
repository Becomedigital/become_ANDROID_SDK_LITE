package com.becomedigital.sdk.identity.becomedigitalsdk.callback;


import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;

public interface BecomeInterfaseCallback {
    void onSuccess(ResponseIV responseIV);
    void onCancel();
    void onError(LoginError pLoginError);
}
