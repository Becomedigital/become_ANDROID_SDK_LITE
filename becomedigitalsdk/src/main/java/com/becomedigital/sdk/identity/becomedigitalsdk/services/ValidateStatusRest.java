package com.becomedigital.sdk.identity.becomedigitalsdk.services;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.becomedigital.sdk.identity.becomedigitalsdk.R;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.AsynchronousTask;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;
import com.becomedigital.sdk.identity.becomedigitalsdk.utils.SharedParameters;
import com.becomedigital.sdk.identity.becomedigitalsdk.utils.UserAgentInterceptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ValidateStatusRest {
    /* access modifiers changed from: private */
    public static final String TAG = ValidateStatusRest.class.getSimpleName();
    private final int USERRESPONSE = 0;
    private final int INITAUTHRESPONSE = 1;
    private final int ADDDATARESPONSE = 2;
    private final int USERRESPONSEINITIAL = 3;
    private final int SENDFACIALAUTH = 4;
    private final int GETCONTRACT = 5;
    private final int GETIMAGE = 6;


    public void getAuth(final Activity activity, String clientID, String clientSecret, final AsynchronousTask asynchronousTask) {

        AsyncTask.execute(() -> {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                String serverUrl = preferences.getString(SharedParameters.URL_AUTH, SharedParameters.url_auth);

                MediaType JSON = MediaType.parse("application/json");
                JSONObject json = new JSONObject();
                json.put("client_id", clientID);
                json.put("client_secret", clientSecret);

                String jsonString = json.toString();
//                //Log.d("JSON SEND:", jsonString);
                RequestBody body = RequestBody.create(JSON, jsonString);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .readTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .writeTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS).build();

                Request request = new Request.Builder()
                        .url(serverUrl)
                        .header("Content-Type", "application/json")
                        .post(body)
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                        asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        // // e.printStackTrace();();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONObject Jobject = new JSONObject(jsonData);
                            if (!Jobject.has("msg")) {
                                asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.SUCCES, Jobject.getString("access_token")), INITAUTHRESPONSE);

                            } else {
                                asynchronousTask.onErrorTransaction(Jobject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            // // e.printStackTrace();();
                            asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        }
                    }
                });


            } catch (Exception e) {
                asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                // // e.printStackTrace();();
            }
        });
    }

    private File video = null;
    private File document1 = null;
    private File document2 = null;

    public void addDataServer(final Activity activity,
                              BDIVConfig config,
                              String ua,
                              SharedParameters.typeDocument typeDocument,
                              String urlDocFront,
                              String selectedCountyCo2,
                              String urlDocBack,
                              String urlVideo,
                              String accesToken,
                              final AsynchronousTask asynchronousTask) {
        AsyncTask.execute(() -> {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity); // get url
                String serverUrl = preferences.getString(SharedParameters.URL_ADD_DATA, SharedParameters.url_add_data);
                String split = activity.getString(R.string.splitValidationTypes);
                String[] validationTypesSubs = config.getValidationTypes().split(split);
                boolean containsVideo = false;
                boolean isPassport = false;

                for (String validationTypesSub : validationTypesSubs) {
                    if (validationTypesSub.equals("VIDEO")) {
                        containsVideo = true;
                    }
                }
                if (typeDocument == SharedParameters.typeDocument.PASSPORT) {
                    isPassport = true;
                }
                RequestBody requestBody = null;
                if (containsVideo) {
                    requestBody = addDocumentsAndVideo(isPassport, urlDocFront, config, selectedCountyCo2, urlDocBack, typeDocument, urlVideo);
                } else {
                    requestBody = addDocuments(isPassport, urlDocFront, config, selectedCountyCo2, urlDocBack, typeDocument);
                }


                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(new UserAgentInterceptor(ua))
                        .connectTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .readTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .writeTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + accesToken)
                        .url(serverUrl)
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                        asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        // // e.printStackTrace();();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONObject Jobject = new JSONObject(jsonData);
                            if (Jobject.has("message")) {
                                if (Jobject.getString("message").equals("El recurso fue creado")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.SUCCES, Jobject.getString("url_resource")), ADDDATARESPONSE);
                                } else {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("message")), ADDDATARESPONSE);
                                }
                            } else {
                                if (Jobject.has("msg")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("msg")), ADDDATARESPONSE);
                                }
                                if (Jobject.has("error")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("error")), ADDDATARESPONSE);
                                }
                            }

                        } catch (JSONException e) {
                            // // e.printStackTrace();();
                            asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        }

//                        if (video.exists ( ))
//                            video.delete ( );
//                        if (document1.exists ( ))
//                            document1.delete ( );
//                        if (document2.exists ( ))
//                            document2.delete ( );
                    }
                });


            } catch (Exception e) {
                asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                // // e.printStackTrace();();
            }
        });
    }


    private RequestBody addDocuments(Boolean isPassport,
                                     String urlDocFront,
                                     BDIVConfig config,
                                     String selectedCountyCo2,
                                     String urlDocBack,
                                     SharedParameters.typeDocument typeDocument) {
        RequestBody requestBody;
        document1 = new File(urlDocFront);
        if (isPassport) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("contract_id", config.getContractId())
                    .addFormDataPart("user_id", config.getUserId())
                    .addFormDataPart("country", selectedCountyCo2)
                    .addFormDataPart("file_type", "passport")
                    .addFormDataPart("document1", "document1.jpg", RequestBody.create(MediaType.parse("image/jpg"), document1))
                    .build();
        } else {
            document2 = new File(urlDocBack);
            String fileType = "driving-license";
            if (typeDocument == SharedParameters.typeDocument.DNI) {
                fileType = "national-id";
            }
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("contract_id", config.getContractId())
                    .addFormDataPart("user_id", config.getUserId())
                    .addFormDataPart("country", selectedCountyCo2)
                    .addFormDataPart("file_type", fileType)
                    .addFormDataPart("document1",
                            "document1.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document1))
                    .addFormDataPart("document2",
                            "document2.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document2))
                    .build();
        }
        return requestBody;
    }


    private RequestBody addDocumentsAndVideo(Boolean isPassport,
                                             String urlDocFront,
                                             BDIVConfig config,
                                             String selectedCountyCo2,
                                             String urlDocBack,
                                             SharedParameters.typeDocument typeDocument,
                                             String urlVideo) {
        RequestBody requestBody;
        video = new File(urlVideo);
        document1 = new File(urlDocFront);
        if (isPassport) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("contract_id", config.getContractId())
                    .addFormDataPart("user_id", config.getUserId())
                    .addFormDataPart("country", selectedCountyCo2)
                    .addFormDataPart("file_type", "passport")
                    .addFormDataPart("video",
                            "video.mp4",
                            RequestBody.create(MediaType.parse("video/mp4"), video))
                    .addFormDataPart("document1",
                            "document1.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document1))
                    .build();
        } else {

            document2 = new File(urlDocBack);
            String fileType = "driving-license";
            if (typeDocument == SharedParameters.typeDocument.DNI) {
                fileType = "national-id";
            }
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("contract_id", config.getContractId())
                    .addFormDataPart("user_id", config.getUserId())
                    .addFormDataPart("country", selectedCountyCo2)
                    .addFormDataPart("file_type", fileType)
                    .addFormDataPart("video",
                            "video.mp4",
                            RequestBody.create(MediaType.parse("video/mp4"), video))
                    .addFormDataPart("document1",
                            "document1.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document1))
                    .addFormDataPart("document2",
                            "document2.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document2))
                    .build();
        }
        return requestBody;
    }

    public void getDataAutentication(Boolean isInitialValidation, String urlGetResponse, String access_token, final Activity activity, final AsynchronousTask asynchronousTask) {
        AsyncTask.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .readTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .writeTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS).build();

                Request request = new Request.Builder()
                        .url(urlGetResponse)
                        .header("Authorization", "Bearer " + access_token)
                        .get()
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                        asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        // // e.printStackTrace();();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONObject Jobject = new JSONObject(jsonData);

                            if (response.code() == 404) {
                                asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.NOFOUND, Jobject.getString("apimsg")), isInitialValidation ? USERRESPONSEINITIAL : USERRESPONSE);
                            } else if (response.code() == 200 || response.code() == 202 ) {
                                if (Jobject.has("verification") &&
                                        Jobject.has("media")) {
                                    JSONObject JobjectV = new JSONObject(Jobject.getString("verification"));
                                    JSONObject JobjectUA = new JSONObject();
                                    JSONObject JobjectR = new JSONObject();
                                    JSONObject JobjectM = new JSONObject(Jobject.getString("media"));
                                    if (Jobject.has("userAgent")) {
                                        JobjectUA = new JSONObject(Jobject.getString("userAgent"));
                                    }
                                    if (Jobject.has("registry")) {
                                        JobjectR = new JSONObject(Jobject.getString("registry"));
                                    }
                                    if (JobjectV.has("verification_status")) {
                                        if (JobjectV.getString("verification_status").equals("La verificacion tuvo un error")) {
                                            asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, JobjectV.getString("verification_status")), isInitialValidation ? USERRESPONSEINITIAL : USERRESPONSE);
                                        } else {
                                            if (JobjectV.getString("verification_status").equals("completed")) {
                                                boolean face_match = false,
                                                        template = false,
                                                        alteration = false,
                                                        watch_list = false;

                                                if (JobjectV.has("face_match")) {
                                                    if (JobjectV.getBoolean("face_match"))
                                                        face_match = true;
                                                }
                                                if (JobjectV.has("template")) {
                                                    if (JobjectV.getBoolean("template"))
                                                        template = true;
                                                }
                                                if (JobjectV.has("alteration")) {
                                                    if (JobjectV.getBoolean("alteration"))
                                                        alteration = true;
                                                }
                                                if (JobjectV.has("watch_list")) {
                                                    if (JobjectV.getBoolean("watch_list"))
                                                        watch_list = true ;
                                                }

                                                JSONObject JComplyAdvantage = new JSONObject();
                                                if (Jobject.has("usercomply_advantageAgent")) {
                                                    JComplyAdvantage = new JSONObject(Jobject.getString("comply_advantage"));
                                                }

                                                ResponseIV responseIV = new ResponseIV(
                                                        (Jobject.has("id") ? Jobject.getString("id") : ""),
                                                        (Jobject.has("created_at") ? Jobject.getString("created_at") : ""),
                                                        (Jobject.has("company") ? Jobject.getString("company") : ""),
                                                        (Jobject.has("fullname") ? Jobject.getString("fullname") : ""),
                                                        (Jobject.has("dni_number") ? Jobject.getString("dni_number") : ""),
                                                        (Jobject.has("birth") ? Jobject.getString("birth") : ""),
                                                        (Jobject.has("document_type") ? Jobject.getString("document_type") : ""),
                                                        (Jobject.has("document_number") ? Jobject.getString("document_number") : ""),
                                                        face_match,
                                                        template,
                                                        alteration,
                                                        watch_list,
                                                        (JComplyAdvantage.has("comply_advantage_result") ? JComplyAdvantage.getString("comply_advantage_result") : ""),
                                                        (JComplyAdvantage.has("comply_advantage_url") ? JComplyAdvantage.getString("comply_advantage_url") : ""),
                                                        JobjectV.getString("verification_status"),
                                                        (JobjectUA.has("device_model") ? JobjectUA.getString("device_model") : ""),
                                                        (JobjectUA.has("os_version") ? JobjectUA.getString("os_version") : ""),
                                                        (JobjectUA.has("browser_major") ? JobjectUA.getString("browser_major") : ""),
                                                        (JobjectUA.has("browser_version") ? JobjectUA.getString("browser_version") : ""),
                                                        (JobjectUA.has("ua") ? JobjectUA.getString("ua") : ""),
                                                        (JobjectUA.has("device_type") ? JobjectUA.getString("device_type") : ""),
                                                        (JobjectUA.has("device_vendor") ? JobjectUA.getString("device_vendor") : ""),
                                                        (JobjectUA.has("os_name") ? JobjectUA.getString("os_name") : ""),
                                                        (JobjectUA.has("browser_name") ? JobjectUA.getString("browser_name") : ""),
                                                        (JobjectR.has("issuePlace") ? JobjectR.getString("issuePlace") : ""),
                                                        (JobjectR.has("emissionDate") ? JobjectR.getString("emissionDate") : ""),
                                                        (JobjectR.has("ageRange") ? JobjectR.getString("ageRange") : ""),
                                                        (JobjectR.has("savingAccountsCount") ? JobjectR.getInt("savingAccountsCount") : 0),
                                                        (JobjectR.has("financialIndustryDebtsCount") ? JobjectR.getInt("financialIndustryDebtsCount") : 0),
                                                        (JobjectR.has("solidarityIndustryDebtsCount") ? JobjectR.getInt("solidarityIndustryDebtsCount") : 0),
                                                        (JobjectR.has("serviceIndustryDebtsCount") ? JobjectR.getInt("serviceIndustryDebtsCount") : 0),
                                                        (JobjectR.has("commercialIndustryDebtsCount") ? JobjectR.getInt("commercialIndustryDebtsCount") : 0),
                                                        (Jobject.has("ip") ? Jobject.getString("ip") : ""),
                                                        (JobjectM.has("frontImgUrl") ? JobjectM.getString("frontImgUrl") : ""),
                                                        (JobjectM.has("backImgUrl") ? JobjectM.getString("backImgUrl") : ""),
                                                        (JobjectM.has("selfiImageUrl") ? JobjectM.getString("selfiImageUrl") : ""),
                                                        "",
                                                        "",
                                                        "",
                                                        "verification complete",
                                                        ResponseIV.SUCCES
                                                );
                                                int responseType = isInitialValidation ? USERRESPONSEINITIAL : USERRESPONSE;
                                                asynchronousTask.onReceiveResultsTransaction(responseIV, responseType);
                                            } else if (isInitialValidation && JobjectV.getString("verification_status").equals("pending")) {
                                                ResponseIV responseIV = new ResponseIV();
                                                responseIV.setMessage(JobjectV.getString("verification_status"));
                                                responseIV.setResponseStatus(ResponseIV.PENDING);
                                                asynchronousTask.onReceiveResultsTransaction(responseIV, USERRESPONSEINITIAL);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            // // e.printStackTrace();();
                            asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        }
                    }
                });


            } catch (Exception e) {
                asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                // // e.printStackTrace();();
            }
        });
    }

    private RequestBody addSelfie(String urlSelfie,
                                  BDIVConfig config) {
        RequestBody requestBody;
        document1 = new File(urlSelfie);
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", config.getUserId())
                .addFormDataPart("image", "image.jpg", RequestBody.create(MediaType.parse("image/jpg"), document1))
                .build();

        return requestBody;
    }

    public void getContract(String contractId, String access_token, final Activity activity, final AsynchronousTask asynchronousTask) {
        AsyncTask.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .readTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .writeTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS).build();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity); // get url
                String serverUrl = preferences.getString(SharedParameters.URL_GET_CONTRACT, SharedParameters.url_get_contract);
                String urlContractId = serverUrl + contractId;
                Request request = new Request.Builder()
                        .url(urlContractId)
                        .header("Authorization", "Bearer " + access_token)
                        .get()
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                        asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        // // e.printStackTrace();();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONObject Jobject = new JSONObject(jsonData);
                            Map<String, Object> map = new HashMap<String, Object>();

                            if (Jobject.has("msg")) {
                                map.put("mensaje", Jobject.getString("verification_status"));
                                asynchronousTask.onReceiveResultsTransactionDictionary(map, ResponseIV.ERROR, GETCONTRACT);
                            } else if (Jobject.has("canUseOnexOne") &&
                                    Jobject.has("countIsOnexOne") &&
                                    Jobject.has("maxIsOnexOne")) {
                                map.put("canUseOnexOne", Jobject.getBoolean("canUseOnexOne"));
                                map.put("countIsOnexOne", Jobject.getInt("countIsOnexOne"));
                                map.put("maxIsOnexOne", Jobject.getInt("maxIsOnexOne"));
                                asynchronousTask.onReceiveResultsTransactionDictionary(map, ResponseIV.SUCCES, GETCONTRACT);
                            } else {
                                map.put("mensaje", activity.getResources().getString(R.string.general_error));
                                asynchronousTask.onReceiveResultsTransactionDictionary(map, ResponseIV.ERROR, GETCONTRACT);
                            }
                        } catch (JSONException e) {
                            // // e.printStackTrace();();
                            asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        }
                    }
                });


            } catch (Exception e) {
                asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                // // e.printStackTrace();();
            }
        });
    }


    public void getImage(String urlImage, String access_token, String name, final Activity activity, final AsynchronousTask asynchronousTask) {
        AsyncTask.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .readTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .writeTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS).build();
                Request request = new Request.Builder()
                        .url(urlImage)
                        .header("Authorization", "Bearer " + access_token)
                        .get()
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                        asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        // // e.printStackTrace();();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        byte[] imageData = response.body().bytes();
                        Map<String, Object> map = new HashMap<String, Object>();
                        if (imageData != null && imageData.length > 0) {
                            map.put("dataResponse", imageData);
                            map.put("name", name);
                            asynchronousTask.onReceiveResultsTransactionDictionary(map, ResponseIV.SUCCES, GETIMAGE);
                        } else {
                            map.put("mensaje", activity.getResources().getString(R.string.general_error));
                            asynchronousTask.onReceiveResultsTransactionDictionary(map, ResponseIV.ERROR, GETIMAGE);
                        }
                    }
                });


            } catch (Exception e) {
                asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                // // e.printStackTrace();();
            }
        });
    }


    public void facialAuth(final Activity activity,
                           BDIVConfig config,
                           String urlSelfie,
                           String accesToken,
                           final AsynchronousTask asynchronousTask) {
        AsyncTask.execute(() -> {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity); // get url
                String serverUrl = preferences.getString(SharedParameters.URL_RE_VALIDATION, SharedParameters.url_re_validation);

                RequestBody requestBody = addSelfie(urlSelfie, config);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .readTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .writeTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS).build();

                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + accesToken)
                        .url(serverUrl)
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                        asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        // // e.printStackTrace();();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONObject Jobject = new JSONObject(jsonData);
                            if (response.code() == 404) {
                                if (Jobject.has("error")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("error")), SENDFACIALAUTH);
                                } else if (Jobject.has("detail")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("detail")), SENDFACIALAUTH);
                                } else if (Jobject.has("description")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("description")), SENDFACIALAUTH);
                                } else {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.toString()), SENDFACIALAUTH);
                                }
                            } else if (response.code() == 200) {
                                if (Jobject.has("detail")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("detail")), SENDFACIALAUTH);

                                } else if (Jobject.has("result") &&
                                        Jobject.has("confidence")) {

                                    if (Jobject.getBoolean("result")) {
                                        asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.SUCCES, Jobject.getString("result")), SENDFACIALAUTH);

                                    } else {
                                        asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, "Resultado fallido, confidence: " + Jobject.getString("confidence")), SENDFACIALAUTH);
                                    }
                                } else {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, activity.getResources().getString(R.string.general_error)), SENDFACIALAUTH);
                                }
                            } else {
                                asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, activity.getResources().getString(R.string.general_error)), SENDFACIALAUTH);
                            }

                        } catch (JSONException e) {
                            // // e.printStackTrace();();
                            asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        }
                    }
                });


            } catch (Exception e) {
                asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                // // e.printStackTrace();();
            }
        });
    }


}
