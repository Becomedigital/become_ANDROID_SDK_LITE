package com.becomedigital.sdk.identity.becomedigitalsdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BDIVConfig implements Parcelable, Serializable {

    private String clienId;
    private byte[] customerLogo;
    private  String clientSecret;
    private String contractId;
    private String validationTypes;
    private boolean allowLibraryLoading;
    private String userId;


    public BDIVConfig(String clienId, String clientSecret, String contractId, String validationTypes, boolean allowLibraryLoading, byte[] customerLogo, String userId) {
        this.clienId = clienId;
        this.customerLogo = customerLogo;
        this.clientSecret = clientSecret;
        this.contractId = contractId;
        this.validationTypes = validationTypes;
        this.allowLibraryLoading = allowLibraryLoading;
        this.userId = userId;
    }

    protected BDIVConfig(Parcel in) {
        clienId = in.readString ( );
        customerLogo = in.createByteArray ( );
        clientSecret = in.readString ( );
        contractId = in.readString ( );
        validationTypes = in.readString ( );
        allowLibraryLoading = in.readByte ( ) != 0;
        userId = in.readString ( );
    }

    public static final Creator<BDIVConfig> CREATOR = new Creator<BDIVConfig> ( ) {
        @Override
        public BDIVConfig createFromParcel(Parcel in) {
            return new BDIVConfig (in);
        }

        @Override
        public BDIVConfig[] newArray(int size) {
            return new BDIVConfig[size];
        }
    };

    public String getClienId() {
        return clienId;
    }

    public void setClienId(String clienId) {
        this.clienId = clienId;
    }

    public byte[] getCustomerLogo() {
        return customerLogo;
    }

    public void setCustomerLogo(byte[] customerLogo) {
        this.customerLogo = customerLogo;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getValidationTypes() {
        return validationTypes;
    }

    public void setValidationTypes(String validationTypes) {
        this.validationTypes = validationTypes;
    }

    public boolean isAllowLibraryLoading() {
        return allowLibraryLoading;
    }

    public void setAllowLibraryLoading(boolean allowLibraryLoading) {
        this.allowLibraryLoading = allowLibraryLoading;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString (clienId);
        parcel.writeByteArray (customerLogo);
        parcel.writeString (clientSecret);
        parcel.writeString (contractId);
        parcel.writeString (validationTypes);
        parcel.writeByte ((byte) (allowLibraryLoading ? 1 : 0));
        parcel.writeString (userId);
    }
}
