package com.becomedigital.sdk.identity.becomedigitalsdk.models;

import android.content.Context;
import java.util.ArrayList;

public class CountriesView {

    public void createTableCountries(Context context) {
        DataBaseHelper db = new DataBaseHelper (context);
        db.createTableCountries ();
    }

    public long createCountry(Countries_DB profile, Context context) {
        DataBaseHelper db = new DataBaseHelper (context);
        return db.insertCountries_DB (profile);
    }

    public ArrayList<Countries_DB> getHallCountries(Context context) {
        DataBaseHelper db = new DataBaseHelper (context);
        return db.getCountries ();
    }

    public boolean isNullEntity(Context context) {
        DataBaseHelper db = new DataBaseHelper (context);
        return db.checkIfTableCountries ( );
    }

    public Countries_DB getCountryIdByCo3(String co3, Context context){
        DataBaseHelper db = new DataBaseHelper (context);
        return db.getcountrieIdByCo3 (co3);
    }



    public Countries_DB getCountryNameByCo3(String co3, Context context){
        DataBaseHelper db = new DataBaseHelper (context);
        return db.getcountriesNameByCo3 (co3);
    }

    public Countries_DB getCountryCO3ById(String id, Context context){
        DataBaseHelper db = new DataBaseHelper (context);
        return db.getcountrieCO3ById (id);
    }

    public Countries_DB getCountryCO2ById(String id, Context context){
        DataBaseHelper db = new DataBaseHelper (context);
        return db.getcountrieCO2ById (id);
    }


    public boolean deleteCountries(Context context) {
        DataBaseHelper db = new DataBaseHelper (context);
        return db.deleteCountries ();
    }
}
