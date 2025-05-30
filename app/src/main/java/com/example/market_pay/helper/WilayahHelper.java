package com.example.market_pay.helper;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market_pay.R;
import com.example.market_pay.adapter.WilayahAdapter;
import com.example.market_pay.model.KelurahanModel;
import com.example.market_pay.view.customer.DaftarMerchantActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WilayahHelper {

    private static final String BASE_URL = "https://www.emsifa.com/api-wilayah-indonesia/api/";
    private final DaftarMerchantActivity activity;
    private List<KelurahanModel> kelurahanModelList = new ArrayList<>();

    private AutoCompleteTextView autoKel;
    private RecyclerView recyclerViewKel;
    private TextInputLayout textInputKel;

    public WilayahHelper(DaftarMerchantActivity activity) {
        this.activity = activity;
        initializeViews();
        setupListeners();
        // Langsung load kelurahan Kecamatan Jalaksana
        new GetKelurahanTask().execute("3208160");
    }

    private void initializeViews() {
        autoKel = activity.findViewById(R.id.txtDesa);
        recyclerViewKel = activity.findViewById(R.id.recyclerViewKel);
        textInputKel = activity.findViewById(R.id.txtLayoutDesa);

        recyclerViewKel.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewKel.setHasFixedSize(true);

        recyclerViewKel.setVisibility(View.GONE);
    }

    private void setupListeners() {
        autoKel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterKelurahan(s.toString());
                recyclerViewKel.setVisibility(s.length() > 0 && !kelurahanModelList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterKelurahan(String text) {
        List<KelurahanModel> filteredList = new ArrayList<>();
        for (KelurahanModel kelurahanModel : kelurahanModelList) {
            if (kelurahanModel.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(kelurahanModel);
            }
        }
        WilayahAdapter<KelurahanModel> adapter = new WilayahAdapter<>(filteredList, item -> {
            KelurahanModel kel = (KelurahanModel) item;
            autoKel.setText(kel.getName());
            recyclerViewKel.setVisibility(View.GONE);
        });
        recyclerViewKel.setAdapter(adapter);
    }

    private class GetKelurahanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String districtId = strings[0];
            return getDataFromApi(BASE_URL + "villages/" + districtId + ".json");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Type listType = new TypeToken<ArrayList<KelurahanModel>>() {}.getType();
                kelurahanModelList = new Gson().fromJson(result, listType);

                WilayahAdapter<KelurahanModel> adapter = new WilayahAdapter<>(kelurahanModelList, item -> {
                    KelurahanModel kel = (KelurahanModel) item;
                    autoKel.setText(kel.getName());
                    recyclerViewKel.setVisibility(View.GONE);
                });
                recyclerViewKel.setAdapter(adapter);

                textInputKel.setEnabled(true);
                autoKel.setText("");
            } else {
                Toast.makeText(activity, "Gagal memuat data kelurahan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDataFromApi(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonData = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            jsonData = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonData;
    }
}
