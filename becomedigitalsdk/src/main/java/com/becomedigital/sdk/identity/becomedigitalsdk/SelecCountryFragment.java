package com.becomedigital.sdk.identity.becomedigitalsdk;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.CountriesView;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.Countries_DB;
import com.becomedigital.sdk.identity.becomedigitalsdk.utils.SharedParameters;

import java.util.ArrayList;
import java.util.List;

import static androidx.navigation.Navigation.findNavController;


public class SelecCountryFragment extends Fragment {


    private Button btnPassport, btnLicense, btnDNI;
    private BDIVConfig config;
    private String selectedCountry,
            selectedCountyCo2,
            urlVideoFile = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_selec_country, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            config = (BDIVConfig) arguments.getSerializable("config");
            if(arguments.containsKey("urlVideoFile"))
            urlVideoFile = arguments.getString("urlVideoFile");
        }
        LinearLayout lPassport = getActivity ( ).findViewById (R.id.lPasaporteC);
        LinearLayout lDNI = getActivity ( ).findViewById (R.id.lDNIC);
        LinearLayout lLicense = getActivity ( ).findViewById (R.id.lLicenseC);
        btnDNI = getActivity ( ).findViewById (R.id.btnDNI);
        btnLicense = getActivity ( ).findViewById (R.id.btnLicense);
        btnPassport = getActivity ( ).findViewById (R.id.btnPassport);

        btnDNI.setOnClickListener (view1 -> goToIntroCaptureDoc(view1, SharedParameters.typeDocument.DNI));
        btnLicense.setOnClickListener (view1 -> goToIntroCaptureDoc(view1, SharedParameters.typeDocument.LICENSE));
        btnPassport.setOnClickListener (view1 -> goToIntroCaptureDoc(view1, SharedParameters.typeDocument.PASSPORT));

        String split = getString (R.string.splitValidationTypes);
        String[] validationTypesSubs = config.getValidationTypes ( ).split (split);
        for (String validationTypesSub : validationTypesSubs) {
            switch (validationTypesSub) {
                case "DNI":
                    lDNI.setVisibility (View.VISIBLE);
                    break;
                case "PASSPORT":
                    lPassport.setVisibility (View.VISIBLE);
                    break;
                case "LICENSE":
                    lLicense.setVisibility (View.VISIBLE);
                    break;
            }
        }
        loadCountriesSpinner ( );
    }

    private void goToIntroCaptureDoc(View view, SharedParameters.typeDocument typeDocument) {
        Bundle bundle = new Bundle ( );
        bundle.putBoolean ("isFront", true);
        bundle.putString ("selectedCountry", selectedCountry);
        bundle.putString ("selectedCountyCo2", selectedCountyCo2);
        bundle.putSerializable ("typeDocument", typeDocument);
        bundle.putSerializable ("config", config);
        bundle.putString ("urlVideoFile", urlVideoFile);
        findNavController (view).navigate (R.id.actionIntroDocCapture, bundle);
    }

    private final static String TAG = SelecCountryFragment.class.getSimpleName ( );

    private void loadCountriesSpinner() {

        // carga los paises y el adapter de la lista
        Spinner spinner = getActivity ( ).findViewById (R.id.spinnerCountries);
        List<String> list = new ArrayList<> ( );
        CountriesView countriesView = new CountriesView ( );
        List<Countries_DB> CountriesList = countriesView.getHallCountries (getActivity ( ));
        list.add (getString (R.string.select_text));
        for (Countries_DB countries_db : CountriesList) {
            list.add (countries_db.getcountrie_name ( ));
        }
        spinner.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener ( ) {  // evento seleccion de pais
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition (pos);
                selectedCountry = item.toString ( );
//                //Log.d (TAG, "selected item: " + item);
                Integer idInt = (int) (long) id;
                Countries_DB countries_db = countriesView.getCountryCO2ById (String.valueOf (idInt), getActivity ());
                selectedCountyCo2 = countries_db.getCo_3 ( );
                //Log.d (TAG, "selected item: " + item);
                if (!item.equals (getString (R.string.select_text))) {
                    btnDNI.setEnabled (true);
                    btnLicense.setEnabled (true);
                    btnPassport.setEnabled (true);
                    btnDNI.setTextColor (getResources ( ).getColor (R.color.black));
                    btnLicense.setTextColor (getResources ( ).getColor (R.color.black));
                    btnPassport.setTextColor (getResources ( ).getColor (R.color.black));
                    btnPassport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.passport_icon_btn, 0, R.drawable.arrownex, 0);
                    btnDNI.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dni_icon_btn, 0, R.drawable.arrownex, 0);
                    btnLicense.setCompoundDrawablesWithIntrinsicBounds(R.drawable.driver_icon_btn, 0, R.drawable.arrownex, 0);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        CustomArrayAdapter<String> dataAdapter = new CustomArrayAdapter<> (getActivity ( ), list);
        dataAdapter.setDropDownViewResource (R.layout.simple_spinner_dropdown);
        spinner.setAdapter (dataAdapter);
        dataAdapter.notifyDataSetChanged ( );
    }

    class CustomArrayAdapter<T> extends ArrayAdapter<T> {

        public CustomArrayAdapter(Context ctx, List<String> objects) {
            super (ctx, R.layout.simple_spinner_dropdown, (List<T>) objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getView (position, convertView, parent);
            final float scale = getResources ( ).getDisplayMetrics ( ).density;
            int pixels = (int) (50 * scale + 0.5f);
            TextView text = view.findViewById (R.id.textDropdwon);
            text.setHeight (pixels);
            text.setGravity (Gravity.CENTER | Gravity.START);
            text.setTextColor (Color.BLACK);
            ImageView imgArrow = getActivity ( ).findViewById (R.id.imgArrow);
            imgArrow.setVisibility (View.VISIBLE);
            return view;
        }
    }
}
