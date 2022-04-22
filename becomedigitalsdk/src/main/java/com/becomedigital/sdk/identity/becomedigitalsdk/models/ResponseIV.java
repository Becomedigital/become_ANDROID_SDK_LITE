package com.becomedigital.sdk.identity.becomedigitalsdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;

public class ResponseIV implements Parcelable, Serializable {
    public static final int NOFOUND = 3;
    public static final int ERROR = 2;
    public static final int PENDING = 1;
    public static final int SUCCES = 0;
    private String id;
    private String created_at;
    private String company;
    private String fullname;
    private String dni_number;
    private String birth;
    private String document_type;
    private String document_number;
    private Boolean face_match;
    private Boolean template;
    private Boolean alteration;
    private Boolean watch_list;
    private String comply_advantage_result;
    private String comply_advantage_url;
    private String verification_status;
    private String device_model;
    private String os_version;
    private String browser_major;
    private String browser_version;
    private String ua;
    private String device_type;
    private String device_vendor;
    private String os_name;
    private String browser_name;
    private String issuePlace;
    private String emissionDate;
    private String ageRange;
    private Integer savingAccountsCount;
    private Integer financialIndustryDebtsCount;
    private Integer solidarityIndustryDebtsCount;
    private Integer serviceIndustryDebtsCount;
    private Integer commercialIndustryDebtsCount;
    private String ip;
    private String frontImgUrl;
    private String backImgUrl;
    private String selfiImageUrl;
    private String frontImgUrlLocal;
    private String backImgUrlLocal;
    private String selfiImageUrlLocal;
    private String message;
    private Integer responseStatus;

    protected ResponseIV(Parcel in) {
        id = in.readString();
        created_at = in.readString();
        company = in.readString();
        fullname = in.readString();
        dni_number = in.readString();
        birth = in.readString();
        document_type = in.readString();
        document_number = in.readString();
        byte tmpFace_match = in.readByte();
        face_match = tmpFace_match == 0 ? null : tmpFace_match == 1;
        byte tmpTemplate = in.readByte();
        template = tmpTemplate == 0 ? null : tmpTemplate == 1;
        byte tmpAlteration = in.readByte();
        alteration = tmpAlteration == 0 ? null : tmpAlteration == 1;
        byte tmpWatch_list = in.readByte();
        watch_list = tmpWatch_list == 0 ? null : tmpWatch_list == 1;
        comply_advantage_result = in.readString();
        comply_advantage_url = in.readString();
        verification_status = in.readString();
        device_model = in.readString();
        os_version = in.readString();
        browser_major = in.readString();
        browser_version = in.readString();
        ua = in.readString();
        device_type = in.readString();
        device_vendor = in.readString();
        os_name = in.readString();
        browser_name = in.readString();
        issuePlace = in.readString();
        emissionDate = in.readString();
        ageRange = in.readString();
        if (in.readByte() == 0) {
            savingAccountsCount = null;
        } else {
            savingAccountsCount = in.readInt();
        }
        if (in.readByte() == 0) {
            financialIndustryDebtsCount = null;
        } else {
            financialIndustryDebtsCount = in.readInt();
        }
        if (in.readByte() == 0) {
            solidarityIndustryDebtsCount = null;
        } else {
            solidarityIndustryDebtsCount = in.readInt();
        }
        if (in.readByte() == 0) {
            serviceIndustryDebtsCount = null;
        } else {
            serviceIndustryDebtsCount = in.readInt();
        }
        if (in.readByte() == 0) {
            commercialIndustryDebtsCount = null;
        } else {
            commercialIndustryDebtsCount = in.readInt();
        }
        ip = in.readString();
        frontImgUrl = in.readString();
        backImgUrl = in.readString();
        selfiImageUrl = in.readString();
        frontImgUrlLocal = in.readString();
        backImgUrlLocal = in.readString();
        selfiImageUrlLocal = in.readString();
        message = in.readString();
        if (in.readByte() == 0) {
            responseStatus = null;
        } else {
            responseStatus = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(created_at);
        dest.writeString(company);
        dest.writeString(fullname);
        dest.writeString(dni_number);
        dest.writeString(birth);
        dest.writeString(document_type);
        dest.writeString(document_number);
        dest.writeByte((byte) (face_match == null ? 0 : face_match ? 1 : 2));
        dest.writeByte((byte) (template == null ? 0 : template ? 1 : 2));
        dest.writeByte((byte) (alteration == null ? 0 : alteration ? 1 : 2));
        dest.writeByte((byte) (watch_list == null ? 0 : watch_list ? 1 : 2));
        dest.writeString(comply_advantage_result);
        dest.writeString(comply_advantage_url);
        dest.writeString(verification_status);
        dest.writeString(device_model);
        dest.writeString(os_version);
        dest.writeString(browser_major);
        dest.writeString(browser_version);
        dest.writeString(ua);
        dest.writeString(device_type);
        dest.writeString(device_vendor);
        dest.writeString(os_name);
        dest.writeString(browser_name);
        dest.writeString(issuePlace);
        dest.writeString(emissionDate);
        dest.writeString(ageRange);
        if (savingAccountsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(savingAccountsCount);
        }
        if (financialIndustryDebtsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(financialIndustryDebtsCount);
        }
        if (solidarityIndustryDebtsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(solidarityIndustryDebtsCount);
        }
        if (serviceIndustryDebtsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(serviceIndustryDebtsCount);
        }
        if (commercialIndustryDebtsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(commercialIndustryDebtsCount);
        }
        dest.writeString(ip);
        dest.writeString(frontImgUrl);
        dest.writeString(backImgUrl);
        dest.writeString(selfiImageUrl);
        dest.writeString(frontImgUrlLocal);
        dest.writeString(backImgUrlLocal);
        dest.writeString(selfiImageUrlLocal);
        dest.writeString(message);
        if (responseStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(responseStatus);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseIV> CREATOR = new Creator<ResponseIV>() {
        @Override
        public ResponseIV createFromParcel(Parcel in) {
            return new ResponseIV(in);
        }

        @Override
        public ResponseIV[] newArray(int size) {
            return new ResponseIV[size];
        }
    };

    public ResponseIV(Integer responseStatus, String message) {
        this.responseStatus = responseStatus;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseIV{" +
                "id='" + id + '\'' +
                ", created_at='" + created_at + '\'' +
                ", company='" + company + '\'' +
                ", fullname='" + fullname + '\'' +
                ", dni_number='" + dni_number + '\'' +
                ", birth='" + birth + '\'' +
                ", document_type='" + document_type + '\'' +
                ", document_number='" + document_number + '\'' +
                ", face_match=" + face_match +
                ", template=" + template +
                ", alteration=" + alteration +
                ", watch_list=" + watch_list +
                ", comply_advantage_result='" + comply_advantage_result + '\'' +
                ", comply_advantage_url='" + comply_advantage_url + '\'' +
                ", verification_status='" + verification_status + '\'' +
                ", device_model='" + device_model + '\'' +
                ", os_version='" + os_version + '\'' +
                ", browser_major='" + browser_major + '\'' +
                ", browser_version='" + browser_version + '\'' +
                ", ua='" + ua + '\'' +
                ", device_type='" + device_type + '\'' +
                ", device_vendor='" + device_vendor + '\'' +
                ", os_name='" + os_name + '\'' +
                ", browser_name='" + browser_name + '\'' +
                ", issuePlace='" + issuePlace + '\'' +
                ", emissionDate='" + emissionDate + '\'' +
                ", ageRange='" + ageRange + '\'' +
                ", savingAccountsCount=" + savingAccountsCount +
                ", financialIndustryDebtsCount=" + financialIndustryDebtsCount +
                ", solidarityIndustryDebtsCount=" + solidarityIndustryDebtsCount +
                ", serviceIndustryDebtsCount=" + serviceIndustryDebtsCount +
                ", commercialIndustryDebtsCount=" + commercialIndustryDebtsCount +
                ", ip='" + ip + '\'' +
                ", frontImgUrl='" + frontImgUrl + '\'' +
                ", backImgUrl='" + backImgUrl + '\'' +
                ", selfiImageUrl='" + selfiImageUrl + '\'' +
                ", frontImgUrlLocal='" + frontImgUrlLocal + '\'' +
                ", backImgUrlLocal='" + backImgUrlLocal + '\'' +
                ", selfiImageUrlLocal='" + selfiImageUrlLocal + '\'' +
                ", message='" + message + '\'' +
                ", responseStatus=" + responseStatus +
                '}';
    }

    public ResponseIV(String id, String created_at, String company, String fullname, String dni_number, String birth, String document_type, String document_number, Boolean face_match, Boolean template, Boolean alteration, Boolean watch_list, String comply_advantage_result, String comply_advantage_url, String verification_status, String device_model, String os_version, String browser_major, String browser_version, String ua, String device_type, String device_vendor, String os_name, String browser_name, String issuePlace, String emissionDate, String ageRange, Integer savingAccountsCount, Integer financialIndustryDebtsCount, Integer solidarityIndustryDebtsCount, Integer serviceIndustryDebtsCount, Integer commercialIndustryDebtsCount, String ip, String frontImgUrl, String backImgUrl, String selfiImageUrl, String frontImgUrlLocal, String backImgUrlLocal, String selfiImageUrlLocal, String message, Integer responseStatus) {
        this.id = id;
        this.created_at = created_at;
        this.company = company;
        this.fullname = fullname;
        this.dni_number = dni_number;
        this.birth = birth;
        this.document_type = document_type;
        this.document_number = document_number;
        this.face_match = face_match;
        this.template = template;
        this.alteration = alteration;
        this.watch_list = watch_list;
        this.comply_advantage_result = comply_advantage_result;
        this.comply_advantage_url = comply_advantage_url;
        this.verification_status = verification_status;
        this.device_model = device_model;
        this.os_version = os_version;
        this.browser_major = browser_major;
        this.browser_version = browser_version;
        this.ua = ua;
        this.device_type = device_type;
        this.device_vendor = device_vendor;
        this.os_name = os_name;
        this.browser_name = browser_name;
        this.issuePlace = issuePlace;
        this.emissionDate = emissionDate;
        this.ageRange = ageRange;
        this.savingAccountsCount = savingAccountsCount;
        this.financialIndustryDebtsCount = financialIndustryDebtsCount;
        this.solidarityIndustryDebtsCount = solidarityIndustryDebtsCount;
        this.serviceIndustryDebtsCount = serviceIndustryDebtsCount;
        this.commercialIndustryDebtsCount = commercialIndustryDebtsCount;
        this.ip = ip;
        this.frontImgUrl = frontImgUrl;
        this.backImgUrl = backImgUrl;
        this.selfiImageUrl = selfiImageUrl;
        this.frontImgUrlLocal = frontImgUrlLocal;
        this.backImgUrlLocal = backImgUrlLocal;
        this.selfiImageUrlLocal = selfiImageUrlLocal;
        this.message = message;
        this.responseStatus = responseStatus;
    }

    public ResponseIV() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDni_number() {
        return dni_number;
    }

    public void setDni_number(String dni_number) {
        this.dni_number = dni_number;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public String getDocument_number() {
        return document_number;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }

    public Boolean getFace_match() {
        return face_match;
    }

    public void setFace_match(Boolean face_match) {
        this.face_match = face_match;
    }

    public Boolean getTemplate() {
        return template;
    }

    public void setTemplate(Boolean template) {
        this.template = template;
    }

    public Boolean getAlteration() {
        return alteration;
    }

    public void setAlteration(Boolean alteration) {
        this.alteration = alteration;
    }

    public Boolean getWatch_list() {
        return watch_list;
    }

    public void setWatch_list(Boolean watch_list) {
        this.watch_list = watch_list;
    }

    public String getComply_advantage_result() {
        return comply_advantage_result;
    }

    public void setComply_advantage_result(String comply_advantage_result) {
        this.comply_advantage_result = comply_advantage_result;
    }

    public String getComply_advantage_url() {
        return comply_advantage_url;
    }

    public void setComply_advantage_url(String comply_advantage_url) {
        this.comply_advantage_url = comply_advantage_url;
    }

    public String getVerification_status() {
        return verification_status;
    }

    public void setVerification_status(String verification_status) {
        this.verification_status = verification_status;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public String getBrowser_major() {
        return browser_major;
    }

    public void setBrowser_major(String browser_major) {
        this.browser_major = browser_major;
    }

    public String getBrowser_version() {
        return browser_version;
    }

    public void setBrowser_version(String browser_version) {
        this.browser_version = browser_version;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_vendor() {
        return device_vendor;
    }

    public void setDevice_vendor(String device_vendor) {
        this.device_vendor = device_vendor;
    }

    public String getOs_name() {
        return os_name;
    }

    public void setOs_name(String os_name) {
        this.os_name = os_name;
    }

    public String getBrowser_name() {
        return browser_name;
    }

    public void setBrowser_name(String browser_name) {
        this.browser_name = browser_name;
    }

    public String getIssuePlace() {
        return issuePlace;
    }

    public void setIssuePlace(String issuePlace) {
        this.issuePlace = issuePlace;
    }

    public String getEmissionDate() {
        return emissionDate;
    }

    public void setEmissionDate(String emissionDate) {
        this.emissionDate = emissionDate;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public Integer getSavingAccountsCount() {
        return savingAccountsCount;
    }

    public void setSavingAccountsCount(Integer savingAccountsCount) {
        this.savingAccountsCount = savingAccountsCount;
    }

    public Integer getFinancialIndustryDebtsCount() {
        return financialIndustryDebtsCount;
    }

    public void setFinancialIndustryDebtsCount(Integer financialIndustryDebtsCount) {
        this.financialIndustryDebtsCount = financialIndustryDebtsCount;
    }

    public Integer getSolidarityIndustryDebtsCount() {
        return solidarityIndustryDebtsCount;
    }

    public void setSolidarityIndustryDebtsCount(Integer solidarityIndustryDebtsCount) {
        this.solidarityIndustryDebtsCount = solidarityIndustryDebtsCount;
    }

    public Integer getServiceIndustryDebtsCount() {
        return serviceIndustryDebtsCount;
    }

    public void setServiceIndustryDebtsCount(Integer serviceIndustryDebtsCount) {
        this.serviceIndustryDebtsCount = serviceIndustryDebtsCount;
    }

    public Integer getCommercialIndustryDebtsCount() {
        return commercialIndustryDebtsCount;
    }

    public void setCommercialIndustryDebtsCount(Integer commercialIndustryDebtsCount) {
        this.commercialIndustryDebtsCount = commercialIndustryDebtsCount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFrontImgUrl() {
        return frontImgUrl;
    }

    public void setFrontImgUrl(String frontImgUrl) {
        this.frontImgUrl = frontImgUrl;
    }

    public String getBackImgUrl() {
        return backImgUrl;
    }

    public void setBackImgUrl(String backImgUrl) {
        this.backImgUrl = backImgUrl;
    }

    public String getSelfiImageUrl() {
        return selfiImageUrl;
    }

    public void setSelfiImageUrl(String selfiImageUrl) {
        this.selfiImageUrl = selfiImageUrl;
    }

    public String getFrontImgUrlLocal() {
        return frontImgUrlLocal;
    }

    public void setFrontImgUrlLocal(String frontImgUrlLocal) {
        this.frontImgUrlLocal = frontImgUrlLocal;
    }

    public String getBackImgUrlLocal() {
        return backImgUrlLocal;
    }

    public void setBackImgUrlLocal(String backImgUrlLocal) {
        this.backImgUrlLocal = backImgUrlLocal;
    }

    public String getSelfiImageUrlLocal() {
        return selfiImageUrlLocal;
    }

    public void setSelfiImageUrlLocal(String selfiImageUrlLocal) {
        this.selfiImageUrlLocal = selfiImageUrlLocal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }
}
