package com.becomedigital.sdk.identity.becomedigitalsdk.models;

public class Countries_DB {
    public static final String TABLE_NAME = "Countries";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COUNTRY_NAME = "country_name";
    public static final String COLUMN_CO2 = "co_2";
    public static final String COLUMN_CO3 = "co_3";
    public static final String COLUMN_AIRPORT_CODE = "airport_code";

    private String countrie_name;
    private String airport_code;
    private String co_3;
    private String co_2;
    private Integer id;

    public String getAirport_code() {
        return airport_code;
    }

    public void setAirport_code(String airport_code) {
        this.airport_code = airport_code;
    }

    public String getCo_3() {
        return co_3;
    }

    public void setCo_3(String co_3) {
        this.co_3 = co_3;
    }

    public String getCo_2() {
        return co_2;
    }

    public void setCo_2(String co_2) {
        this.co_2 = co_2;
    }

    public String getcountrie_name() {
        return countrie_name;
    }

    public void setcountrie_name(String countrie_name) {
        this.countrie_name = countrie_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public static String getCreateTable() {
        return CREATE_TABLE;
    }

    public Countries_DB() {
    }

    public Countries_DB(Integer id, String countrie_name, String airport_code, String co_3, String co_2) {
        this.countrie_name = countrie_name;
        this.airport_code = airport_code;
        this.co_3 = co_3;
        this.co_2 = co_2;
        this.id = id;
    }

    public Countries_DB(Integer id, String countrie_name, String co_3) {
        this.id = id;
        this.countrie_name = countrie_name;
        this.co_3 = co_3;
    }

    public Countries_DB(String countrie_name, String co_3) {
        this.countrie_name = countrie_name;
        this.co_3 = co_3;
    }

    public Countries_DB(String countrie_name, String airport_code, String co_3, String co_2) {
        this.countrie_name = countrie_name;
        this.airport_code = airport_code;
        this.co_3 = co_3;
        this.co_2 = co_2;
        this.id = id;
    }

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER,"
                    + COLUMN_COUNTRY_NAME + " TEXT,"
                    + COLUMN_AIRPORT_CODE + " TEXT,"
                    + COLUMN_CO2 + " TEXT,"
                    + COLUMN_CO3 + " TEXT"
                    + ")";


}
