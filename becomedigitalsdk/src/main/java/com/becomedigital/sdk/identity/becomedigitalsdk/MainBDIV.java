package com.becomedigital.sdk.identity.becomedigitalsdk;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.becomedigital.sdk.identity.becomedigitalsdk.callback.AsynchronousTask;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeCallBackManager;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;
import com.becomedigital.sdk.identity.becomedigitalsdk.services.ValidateStatusRest;
import com.becomedigital.sdk.identity.becomedigitalsdk.utils.LoadCountries;
import com.becomedigital.sdk.identity.becomedigitalsdk.utils.SharedParameters;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static androidx.navigation.Navigation.findNavController;

public class MainBDIV extends AppCompatActivity implements AsynchronousTask {
    public static final String TAG = MainBDIV.class.getSimpleName();
    public static final String KEY_ERROR = "ErrorMessage";
    private static ValidateStatusRest autService;
    private static Context mContext;
    private static int progress;
    private BDIVConfig config;
    public Intent mData = new Intent();
    public androidx.appcompat.widget.Toolbar toolbar;
    private boolean isViewCloseAction;
    private final int USERRESPONSE = 0;
    private final int INITAUTHRESPONSE = 1;
    private final int ADDDATARESPONSE = 2;
    private final int USERRESPONSEINITIAL = 3;
    private final int SENDFACIALAUTH = 4;
    private final int GETCONTRACT = 5;
    private final int GETIMAGE = 6;

    private FrameLayout frameInit;
    private TextView textInfoServer;
    private String urlVGlobal;
    private boolean isHomeActivity = true;
    private CountDownTimer countdownToGetdata;
    private String access_token;
    private ResponseIV responseIVAuth;


    private ImageButton imgBtnCancel;
    private ImageButton imgBtnBack;

    private BecomeCallBackManager mCallbackManager = BecomeCallBackManager.createNew();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main_bdiv);
        frameInit = findViewById(R.id.frameLoaderInit);
        textInfoServer = findViewById(R.id.text_info_server);
        mContext = getApplicationContext();
        autService = new ValidateStatusRest();
        ImageView imgLoader = findViewById(R.id.imgLoader);// loader inicial
        Glide.with(this)
                .load(R.drawable.load_init)
                .into(imgLoader);
        getExtrasAndValidateConfig(); // get data and validate input config parceable
        // carga el logo del usuario
        toolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ImageView imgToolbar = toolbar.findViewById(R.id.imgUser);
        imgBtnCancel = toolbar.findViewById(R.id.btnCancel);
        imgBtnCancel.setOnClickListener(view -> {
            findNavController(MainBDIV.this, R.id.nav_host_fragment).navigate(R.id.cancelAction);
            imgBtnCancel.setVisibility(View.INVISIBLE);
            isViewCloseAction = true;
        });
        if (this.config.getCustomerLogo() != null) {
            imgToolbar.setImageBitmap(BitmapFactory.decodeByteArray(config.getCustomerLogo(), 0, config.getCustomerLogo().length));
        }

        imgBtnBack = toolbar.findViewById(R.id.btnBack);
        imgBtnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                findNavController(MainBDIV.this, R.id.nav_host_fragment).popBackStack();
                if (isViewCloseAction)
                    imgBtnCancel.setVisibility(View.VISIBLE);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public BDIVConfig getConfig() {
        return config;
    }

    public void setConfig(BDIVConfig config) {
        this.config = config;
    }


    public void changeColorToolbar(Boolean isDark) {
        if (isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black));
            imgBtnCancel.setImageDrawable(getResources().getDrawable(R.drawable.close__icon_w));
            imgBtnBack.setImageDrawable(getResources().getDrawable(R.drawable.back_icon_w));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.white));
            imgBtnCancel.setImageDrawable(getResources().getDrawable(R.drawable.icon_cancel_black));
            imgBtnBack.setImageDrawable(getResources().getDrawable(R.drawable.back_icon_dark34x28_2x));
        }
    }

    public void showImgBtnCancel() {
        imgBtnCancel.setVisibility(View.VISIBLE);
    }

    private void getExtrasAndValidateConfig() { // valida data input user
        if (getIntent().getExtras() != null) {
            setConfig((BDIVConfig) getIntent().getSerializableExtra("BDIVConfig"));
            if (config != null) {
                String split = getString(R.string.splitValidationTypes);
                String[] validationTypesSubs = config.getValidationTypes().split(split);
                if (this.config.getClienId().isEmpty()) {
                    setResulLoginError("ClienId parameters cannot be empty");
                    return;
                } else if (this.config.getClientSecret().isEmpty()) {
                    setResulLoginError("ClientSecret parameters cannot be empty");
                    return;
                } else if (this.config.getContractId().isEmpty()) {
                    setResulLoginError("ContractId parameters cannot be empty");
                    return;
                } else if (this.config.getUserId().isEmpty()) {
                    setResulLoginError("UserId parameters cannot be empty");
                    return;
                } else if (validationTypesSubs.length == 0) {
                    setResulLoginError("The validationTypes parameter cannot be empty");
                    return;
                } else if (validationTypesSubs.length == 1) {
                    if (validationTypesSubs[0].equals("VIDEO")) {
                        setResulLoginError("The process cannot be initialized with video only");
                        return;
                    } else if (!validationTypesSubs[0].equals("VIDEO")
                            && !validationTypesSubs[0].equals("DNI")
                            && !validationTypesSubs[0].equals("PASSPORT")
                            && !validationTypesSubs[0].equals("LICENSE")) {
                        setResulLoginError("Input parameters are wrong");
                        return;
                    }
                }
                // autenticarse
                startAutentication();
            }

        }
    }

    private void startAutentication() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        LoadCountries.loadCountries(this);
        autService.getAuth(this, this.config.getClienId(), this.config.getClientSecret(), this);
    }

    private void getContract() {
        autService.getContract(this.config.getContractId(), access_token, this, this);
    }

    //region server transactions

    private boolean isOkResponse = false;

    @Override
    public void onReceiveResultsTransaction(ResponseIV responseIV, int transactionId) {
        runOnUiThread(() -> {

            if (transactionId == INITAUTHRESPONSE) {
                access_token = responseIV.getMessage();
                autService.getDataAutentication(true, SharedParameters.url_add_data + "/" + this.config.getUserId(), access_token, MainBDIV.this, MainBDIV.this);
            }

            if (transactionId == USERRESPONSE) {
                if (responseIV.getResponseStatus() == ResponseIV.SUCCES) {
                    isOkResponse = true;
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    countdownToGetdata.cancel();
                    responseIVAuth = responseIV;
                    getImages();
                } else if (responseIV.getResponseStatus() == ResponseIV.ERROR) {
                    countdownToGetdata.cancel();
                    setResultError(responseIV.getMessage());
                }
            }

            if (transactionId == USERRESPONSEINITIAL) {

                switch (responseIV.getResponseStatus()) {
                    case ResponseIV.SUCCES:
                        responseIVAuth = responseIV;
                        getContract();
                        break;
                    case ResponseIV.NOFOUND:
                        enableAppRemoveSpinner();
                        break;
                    case ResponseIV.PENDING:
                        enableAppRemoveSpinner();
                        returnResultSucces(responseIV);
                        break;
                    case ResponseIV.ERROR:
                        enableAppRemoveSpinner();
                        setResultError(responseIV.getMessage());
                        break;
                }
            }

            if (transactionId == ADDDATARESPONSE) {
                if (responseIV.getResponseStatus() == ResponseIV.SUCCES) {
                    textInfoServer.setText(getResources().getString(R.string.text_progress_inteligence));
                    urlVGlobal = responseIV.getMessage();
                    initCounDownGetData(responseIV.getMessage());
                } else {
                    setResultError(responseIV.getMessage());
                }
            }

            if (transactionId == SENDFACIALAUTH) {
                if (responseIV.getResponseStatus() == ResponseIV.SUCCES) {
                    getImages();
                } else {
                    setResultError(responseIV.getMessage());
                }
            }
        });
    }

    private void enableAppRemoveSpinner() {
        runOnUiThread(() -> {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Animation animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            frameInit.startAnimation(animFadeOut);
            frameInit.setVisibility(View.GONE);
        });
    }

    @Override
    public void onReceiveResultsTransactionDictionary(Map<String, Object> map, int responseStatus, int transactionId) {
        if (transactionId == GETCONTRACT) {
            if (responseStatus == ResponseIV.ERROR) {
                setResultError((String) map.get("mensaje"));
            } else {
                boolean canUseOnexOne = (boolean) map.get("canUseOnexOne");
                int countIsOnexOne = (int) map.get("countIsOnexOne");
                int maxIsOnexOne = (int) map.get("maxIsOnexOne");
                if (countIsOnexOne <= maxIsOnexOne) {
                    if (canUseOnexOne) {
                        enableAppRemoveSpinner();
                        goToTakeSelfie();
                    } else {
                        getImages();
                    }
                } else {
                    setResultError(getString(R.string.text_empty_balance));
                }
            }
        }

        if (transactionId == GETIMAGE) {
            if (responseStatus == ResponseIV.ERROR) {
                setResultError((String) map.get("mensaje"));
            } else {
                byte[] imageData = (byte[]) map.get("dataResponse");
                String name = (String) map.get("name");

                switch (name) {
                    case "backImg":
                        responseIVAuth.setBackImgUrlLocal(saveFile(name, imageData));
                        if (!responseIVAuth.getFrontImgUrl().isEmpty()) {
                            autService.getImage(responseIVAuth.getFrontImgUrl(), access_token, "frontImg", this, MainBDIV.this);

                        } else if (!responseIVAuth.getSelfiImageUrl().isEmpty()) {
                            autService.getImage(responseIVAuth.getSelfiImageUrl(), access_token, "selfieImage", this, MainBDIV.this);

                        } else {
                            returnResultSucces(responseIVAuth);
                        }
                        break;
                    case "frontImg":
                        responseIVAuth.setFrontImgUrlLocal(saveFile(name, imageData));
                        if (!responseIVAuth.getSelfiImageUrl().isEmpty()) {
                            autService.getImage(responseIVAuth.getSelfiImageUrl(), access_token, "selfieImage", this, MainBDIV.this);
                        } else {
                            returnResultSucces(responseIVAuth);
                        }
                        break;
                    case "selfieImage":
                        responseIVAuth.setSelfiImageUrlLocal(saveFile(name, imageData));
                        returnResultSucces(responseIVAuth);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onErrorTransaction(String errorMsn) {
        runOnUiThread(() -> {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (countdownToGetdata != null)
                countdownToGetdata.cancel();
            setResultError(errorMsn);
        });
    }

    private void goToTakeSelfie() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFront", true);
        bundle.putBoolean("isSelfie", true);
        bundle.putBoolean("isVideoCapture", false);// salta a la captura
        bundle.putSerializable("config", config);
        findNavController(this, R.id.nav_host_fragment).navigate(R.id.goToTakeSelfie, bundle);
    }

    private void getImages() {
        if (!responseIVAuth.getBackImgUrl().isEmpty()) {
            autService.getImage(responseIVAuth.getBackImgUrl(), access_token, "backImg", this, MainBDIV.this);
        } else if (!responseIVAuth.getFrontImgUrl().isEmpty()) {
            autService.getImage(responseIVAuth.getFrontImgUrl(), access_token, "frontImg", this, MainBDIV.this);
        } else if (!responseIVAuth.getSelfiImageUrl().isEmpty()) {
            autService.getImage(responseIVAuth.getSelfiImageUrl(), access_token, "selfieImage", this, MainBDIV.this);
        } else {
            returnResultSucces(responseIVAuth);
        }
    }

    private String saveFile(String name, byte[] dataImage){
        String child = name + ".jpg";
        File mFile = new File(getExternalFilesDir (null), child);
        if (mFile.exists ( )) {
            mFile.delete ( );
        }
        FileOutputStream output = null;
        try {
            output = new FileOutputStream (mFile);
            output.write (dataImage);
        } catch (IOException e) {
            setResultError (e.getLocalizedMessage ( ));
        } finally {
            if (null != output) {
                try {
                    output.close ( );
                } catch (IOException e) {
                    setResultError (e.getLocalizedMessage ( ));
                }
            }
        }
        return mFile.getPath ( );
    }


    public void displayLoader(Boolean isSelfieLoader) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        frameInit.setVisibility(View.VISIBLE);
        textInfoServer.setText(isSelfieLoader ? getString(R.string.text_loader_selfie) : getString(R.string.text_info_upload));
    }

    public void addDataServer(BDIVConfig config,
                              SharedParameters.typeDocument typeDocument,
                              String urlDocFront,
                              String selectedCountyCo2,
                              String urlDocBack,
                              String urlVideo) {
        textInfoServer.setText(getString(R.string.text_info_upload));
        // Carga el user agent
        String ua = new WebView(this).getSettings().getUserAgentString();
        autService.addDataServer(this, config, ua, typeDocument, urlDocFront, selectedCountyCo2.toUpperCase(), urlDocBack, urlVideo, access_token, this);
    }

    public void facialAuth(BDIVConfig config,
                           String urlSelfie) {
        autService.facialAuth(this, config, urlSelfie, access_token, this);
    }

    private void initCounDownGetData(final String urlGetData) {
        final int[] countTime = {0};
        final int[] countBefore = {10};
        countdownToGetdata = new CountDownTimer(190000, 1000) {
            public void onTick(long millisUntilFinished) {
//                //Log.d(TAG, "time of the process: " + millisUntilFinished);
                countTime[0]++;
                if (!isOkResponse && countTime[0] > countBefore[0]) {
                    countBefore[0] = countTime[0] + 10;
                    autService.getDataAutentication(false, urlGetData, access_token, MainBDIV.this, MainBDIV.this);
                }
            }

            public void onFinish() {
                runOnUiThread(() -> {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    textInfoServer.setText(getString(R.string.text_time_out_msn));
                    Button btnClose = findViewById(R.id.btnCloseTO);
                    Button btnRetry = findViewById(R.id.btnRetryTO);
                    btnRetry.setVisibility(View.VISIBLE);
                    btnRetry.setOnClickListener(view -> {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        countTime[0] = 0;
                        countBefore[0] = 10;
                        btnRetry.setVisibility(View.GONE);
                        btnClose.setVisibility(View.GONE);
                        initCounDownGetData(urlVGlobal);
                        textInfoServer.setText(getString(R.string.retry_send_text));
                    });


                    btnClose.setVisibility(View.VISIBLE);
                    btnClose.setOnClickListener(view -> setResultError("Time out"));
//                    //Log.d(TAG, "time: done");
                });
            }
        }.start();
    }

    //endregion

    //region app response

    private void returnResultSucces(ResponseIV responseIV) {
        enableAppRemoveSpinner();
        mData.putExtra("ResponseIV", (Parcelable) responseIV);
        setResult(RESULT_OK, mData);
        finish();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        this.mCallbackManager.onActivityResult(i, i2, intent);
        super.onActivityResult(i, i2, intent);
    }

    private void setResulLoginError(String msnErr) {
        mData.putExtra(KEY_ERROR, msnErr);
        setResult(RESULT_FIRST_USER, mData);
        finish();
    }

    public void setResultError(String msnErr) {
        mData.putExtra(KEY_ERROR, msnErr);
        setResult(RESULT_FIRST_USER, mData);
        finish();
    }

    public void setResulUserCanceled() {
        setResult(RESULT_CANCELED, mData);
        finish();
    }

    public void setIsHomeActivity(boolean isHomeActivity) {
        this.isHomeActivity = isHomeActivity;
        imgBtnBack.setVisibility(isHomeActivity ? View.GONE : View.VISIBLE);
    }

    //endregion


    @Override
    public void onBackPressed() {
        if (!isHomeActivity)
            super.onBackPressed();
    }

}
