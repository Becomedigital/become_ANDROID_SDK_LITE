package com.becomedigital.sdk.identity.becomedigitalsdk.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private final static String TAG = DataBaseHelper.class.getSimpleName ( );
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "user_db";

    public DataBaseHelper(Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // region countries
    public void createTableCountries() {
        SQLiteDatabase db = this.getWritableDatabase ( );
        // crea la tabla user en SQLite
        try {

            db.execSQL ("DROP TABLE IF EXISTS " + Countries_DB.TABLE_NAME);
            // create notes table
            db.execSQL (Countries_DB.CREATE_TABLE);
            //Log.d (TAG, "tabla countries creada con exito");
        } catch (SQLException e) {
            //Log.d (TAG + "tb_SelectedCountries", e.getMessage ( ));
        } finally {
            // close db connection
            db.close ( );
        }

    }

    public long insertCountries_DB(Countries_DB Countries) {
        long id = 0;
        SQLiteDatabase db = this.getWritableDatabase ( );

        try {
            // inserta el usuario en la base

            ContentValues values = new ContentValues ( );
            // no need to add them
            values.put (Countries_DB.COLUMN_ID, Countries.getId ( ));
            values.put (Countries_DB.COLUMN_COUNTRY_NAME, Countries.getcountrie_name ( ));
            values.put (Countries_DB.COLUMN_AIRPORT_CODE, Countries.getAirport_code ( ));
            values.put (Countries_DB.COLUMN_CO2, Countries.getCo_2 ( ));
            values.put (Countries_DB.COLUMN_CO3, Countries.getCo_3 ( ));


            // insert row
            try {
                id = db.insert (Countries_DB.TABLE_NAME, null, values);
            } catch (Exception e) {
                //Log.d ("Error in entity: ", e.getMessage ( ));
            }

            //Log.d (TAG, "pais " + Countries.getcountrie_name ( ) + " insertada con exito");
            // return newly inserted row id
        } catch (Exception e) {
            //Log.d (TAG, e.getMessage ( ));
        } finally {
            // close db connection
            db.close ( );
        }

        return id;
    }

    public ArrayList<Countries_DB> getCountries() {
        //obtiene el usuario de la base de datos local SQLite
        Cursor cursor = null;
        ArrayList<Countries_DB> selectedCountriesList = new ArrayList<> ( );
        // get readable database as we are not inserting anything
        try {


            SQLiteDatabase db = this.getReadableDatabase ( );
            cursor = db.query (Countries_DB.TABLE_NAME,
                    new String[]{Countries_DB.COLUMN_ID,
                            Countries_DB.COLUMN_COUNTRY_NAME,
                            Countries_DB.COLUMN_AIRPORT_CODE,
                            Countries_DB.COLUMN_CO2,
                            Countries_DB.COLUMN_CO3
                    },
                    null,
                    null,
                    null,
                    null,
                    null);

            while (cursor.moveToNext ( )) {

                try {
                    selectedCountriesList.add (new Countries_DB (cursor.getInt (cursor.getColumnIndex (Countries_DB.COLUMN_ID)),
                            cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_COUNTRY_NAME)),
                            cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_AIRPORT_CODE)),
                            cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_CO2)),
                            cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_CO3))));
                } catch (Exception e) {
                    //Log.e (TAG, "Error " + e.toString ( ));
                }

            }
            //Log.d (TAG, "paises obtenidas con exito");
            // close the db connection
        } catch (Exception e) {
            //Log.d (TAG + "Error select table:", e.getMessage ( ));
            if (cursor != null)
                cursor.close ( );
        } finally {
            // close db connection
            if (cursor != null)
                cursor.close ( );
        }

        return selectedCountriesList;
    }

    public boolean deleteCountries() {
        try {
            SQLiteDatabase db = this.getWritableDatabase ( );
            db.delete (Countries_DB.TABLE_NAME, null, null);
            db.close ( );
            return true;
        } catch (SQLException e) {
            //Log.d (TAG, e.getMessage ( ));
            return false;
        }
    }

    public boolean checkIfTableCountries() {
        // valida si existen registros

        Cursor cursor = null;
        try {
            String countQuery = "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + Countries_DB.TABLE_NAME + "'";
            SQLiteDatabase db = this.getReadableDatabase ( );
            cursor = db.rawQuery (countQuery, null);
            if (cursor != null) {
                if (cursor.getCount ( ) > 0) {
                    cursor.close ( );
                    return false;
                }

            }
            //Log.d (TAG, "chequeo exitoso");
        } catch (SQLException e) {
            //Log.d ("Error en el contador:", e.getMessage ( ));
            if (cursor != null)
                cursor.close ( );
        } finally {
            // close db connection
            if (cursor != null)
                cursor.close ( );
        }
        // return count
        return true;
    }

    public Countries_DB getcountrieIdByCo3(String co3) {
        //obtiene el usuario de la base de datos local SQLite
        Cursor cursor = null;
        Countries_DB countries_db = new Countries_DB ( );
        // get readable database as we are not inserting anything
        try {


            String whereClause = Countries_DB.COLUMN_CO3 + " = ?";
            String[] whereArgs = new String[]{
                    co3.toLowerCase ( )
            };
            SQLiteDatabase db = this.getReadableDatabase ( );

            cursor = db.query (Countries_DB.TABLE_NAME,
                    new String[]{Countries_DB.COLUMN_ID,
                            Countries_DB.COLUMN_COUNTRY_NAME,
                            Countries_DB.COLUMN_CO3
                    },
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    null);

            if (cursor != null) {
                cursor.moveToFirst ( );
                countries_db = new Countries_DB (cursor.getInt (cursor.getColumnIndex (Countries_DB.COLUMN_ID)),
                        cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_COUNTRY_NAME)),
                        cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_CO3)));
            }

            //Log.d (TAG, "pais obtenido con exito");
            // close the db connection
        } catch (Exception e) {
            //Log.d (TAG + "Error select table:", e.getMessage ( ));
            if (cursor != null)
                cursor.close ( );
        } finally {
            // close db connection
            if (cursor != null)
                cursor.close ( );
        }

        return countries_db;
    }

    public Countries_DB getcountriesNameByCo3(String co3) {
        //obtiene el usuario de la base de datos local SQLite
        Cursor cursor = null;
        Countries_DB countries_db = new Countries_DB ( );
        // get readable database as we are not inserting anything
        try {


            String whereClause = Countries_DB.COLUMN_CO3 + " = ?";
            String[] whereArgs = new String[]{
                    co3.toLowerCase ( )
            };
            SQLiteDatabase db = this.getReadableDatabase ( );

            cursor = db.query (Countries_DB.TABLE_NAME,
                    new String[]{Countries_DB.COLUMN_COUNTRY_NAME,
                            Countries_DB.COLUMN_CO3
                    },
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    null);

            if (cursor != null)
                cursor.moveToFirst ( );

            countries_db = new Countries_DB (cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_COUNTRY_NAME)),
                    cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_CO3)));

            //Log.d (TAG, "pais obtenido con exito");
            // close the db connection
        } catch (Exception e) {
            //Log.d (TAG + "Error select table:", e.getMessage ( ));
            if (cursor != null)
                cursor.close ( );
        } finally {
            // close db connection
            if (cursor != null)
                cursor.close ( );
        }

        return countries_db;
    }

    public Countries_DB getcountrieCO3ById(String id) {
        //obtiene el usuario de la base de datos local SQLite
        Cursor cursor = null;
        Countries_DB countries_db = new Countries_DB ( );
        // get readable database as we are not inserting anything
        try {


            String whereClause = Countries_DB.COLUMN_ID + " = ?";
            String[] whereArgs = new String[]{
                    id.toLowerCase ( )
            };
            SQLiteDatabase db = this.getReadableDatabase ( );

            cursor = db.query (Countries_DB.TABLE_NAME,
                    new String[]{Countries_DB.COLUMN_COUNTRY_NAME,
                            Countries_DB.COLUMN_CO3
                    },
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    null);

            if (cursor != null)
                cursor.moveToFirst ( );

            countries_db = new Countries_DB (cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_COUNTRY_NAME)),
                    cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_CO3)));

            //Log.d (TAG, "pais obtenido con exito");
            // close the db connection
        } catch (Exception e) {
            //Log.d (TAG + "Error select table:", e.getMessage ( ));
            if (cursor != null)
                cursor.close ( );
        } finally {
            // close db connection
            if (cursor != null)
                cursor.close ( );
        }

        return countries_db;
    }


    public Countries_DB getcountrieCO2ById(String id) {
        //obtiene el usuario de la base de datos local SQLite
        Cursor cursor = null;
        Countries_DB countries_db = new Countries_DB ( );
        // get readable database as we are not inserting anything
        try {


            String whereClause = Countries_DB.COLUMN_ID + " = ?";
            String[] whereArgs = new String[]{
                    id.toLowerCase ( )
            };
            SQLiteDatabase db = this.getReadableDatabase ( );

            cursor = db.query (Countries_DB.TABLE_NAME,
                    new String[]{Countries_DB.COLUMN_COUNTRY_NAME,
                            Countries_DB.COLUMN_CO2
                    },
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    null);

            if (cursor != null)
                cursor.moveToFirst ( );

            countries_db = new Countries_DB (cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_COUNTRY_NAME)),
                    cursor.getString (cursor.getColumnIndex (Countries_DB.COLUMN_CO2)));

            //Log.d (TAG, "pais obtenido con exito");
            // close the db connection
        } catch (Exception e) {
            //Log.d (TAG + "Error select table:", e.getMessage ( ));
            if (cursor != null)
                cursor.close ( );
        } finally {
            // close db connection
            if (cursor != null)
                cursor.close ( );
        }

        return countries_db;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL ("DROP TABLE IF EXISTS " + Countries_DB.TABLE_NAME);

        // Create tables again
        onCreate (db);
    }
    //endregion


}


