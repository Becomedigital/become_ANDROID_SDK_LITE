package com.becomedigital.sdk.identity.becomedigitalsdk.callback;

import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;

import java.util.Map;

public interface AsynchronousTask {
    void onReceiveResultsTransaction(ResponseIV responseIV, int transactionId);
    void onReceiveResultsTransactionDictionary(Map<String, Object> map,int responseStatus, int transactionId);
    void onErrorTransaction(String errorMsn);
}
