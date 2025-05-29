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
import com.example.market_pay.model.KabupatenModel;
import com.example.market_pay.model.KecamatanModel;
import com.example.market_pay.model.KelurahanModel;
import com.example.market_pay.model.ProvinsiModel;
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
    private List<ProvinsiModel> provinsiModelList = new ArrayList<>();
    private List<KabupatenModel> kabupatenModelList = new ArrayList<>();
    private List<KecamatanModel> kecamatanModelList = new ArrayList<>();
    private List<KelurahanModel> kelurahanModelList = new ArrayList<>();

    private AutoCompleteTextView autoProv, autoKab, autoKec, autoKel;
    private RecyclerView recyclerViewProv, recyclerViewKab, recyclerViewKec, recyclerViewKel;
    private TextInputLayout textInputKab, textInputKec, textInputKel;

    public WilayahHelper(DaftarMerchantActivity activity) {
        this.activity = activity;
        initializeViews();
        setupListeners();
        new GetProvinsiTask().execute();
    }

    private void initializeViews() {
        autoProv = activity.findViewById(R.id.autoProv);
        autoKab = activity.findViewById(R.id.autoKab);
        autoKec = activity.findViewById(R.id.autoKec);
        autoKel = activity.findViewById(R.id.autoKel);

        recyclerViewProv = activity.findViewById(R.id.recyclerViewProv);
        recyclerViewKab = activity.findViewById(R.id.recyclerViewKab);
        recyclerViewKec = activity.findViewById(R.id.recyclerViewKec);
        recyclerViewKel = activity.findViewById(R.id.recyclerViewKel);

        textInputKab = activity.findViewById(R.id.textInputKab);
        textInputKec = activity.findViewById(R.id.textInputKec);
        textInputKel = activity.findViewById(R.id.textInputKel);

        setupRecyclerView(recyclerViewProv);
        setupRecyclerView(recyclerViewKab);
        setupRecyclerView(recyclerViewKec);
        setupRecyclerView(recyclerViewKel);

        recyclerViewProv.setVisibility(View.GONE);
        recyclerViewKab.setVisibility(View.GONE);
        recyclerViewKec.setVisibility(View.GONE);
        recyclerViewKel.setVisibility(View.GONE);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);
    }

    private void setupListeners() {
        autoProv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProvinsi(s.toString());
                recyclerViewProv.setVisibility(s.length() > 0 && !provinsiModelList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoKab.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterKabupaten(s.toString());
                recyclerViewKab.setVisibility(s.length() > 0 && !kabupatenModelList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoKec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterKecamatan(s.toString());
                recyclerViewKec.setVisibility(s.length() > 0 && !kecamatanModelList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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

    private void filterProvinsi(String text) {
        List<ProvinsiModel> filteredList = new ArrayList<>();
        for (ProvinsiModel provinsiModel : provinsiModelList) {
            if (provinsiModel.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(provinsiModel);
            }
        }
        WilayahAdapter<ProvinsiModel> adapter = new WilayahAdapter<>(filteredList, item -> {
            ProvinsiModel prov = (ProvinsiModel) item;
            autoProv.setText(prov.getName());
            textInputKab.setEnabled(true);
            autoKab.setText("");
            kabupatenModelList.clear();
            new GetKabupatenTask().execute(prov.getId());
            recyclerViewProv.setVisibility(View.GONE);
            textInputKec.setEnabled(false);
            autoKec.setText("");
            kecamatanModelList.clear();
            textInputKel.setEnabled(false);
            autoKel.setText("");
            kelurahanModelList.clear();
        });
        recyclerViewProv.setAdapter(adapter);
    }

    private void filterKabupaten(String text) {
        List<KabupatenModel> filteredList = new ArrayList<>();
        for (KabupatenModel kabupatenModel : kabupatenModelList) {
            if (kabupatenModel.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(kabupatenModel);
            }
        }
        WilayahAdapter<KabupatenModel> adapter = new WilayahAdapter<>(filteredList, item -> {
            KabupatenModel kab = (KabupatenModel) item;
            autoKab.setText(kab.getName());
            textInputKec.setEnabled(true);
            autoKec.setText("");
            kecamatanModelList.clear();
            new GetKecamatanTask().execute(kab.getId());
            recyclerViewKab.setVisibility(View.GONE);
            textInputKel.setEnabled(false);
            autoKel.setText("");
            kelurahanModelList.clear();
        });
        recyclerViewKab.setAdapter(adapter);
    }

    private void filterKecamatan(String text) {
        List<KecamatanModel> filteredList = new ArrayList<>();
        for (KecamatanModel kecamatanModel : kecamatanModelList) {
            if (kecamatanModel.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(kecamatanModel);
            }
        }
        WilayahAdapter<KecamatanModel> adapter = new WilayahAdapter<>(filteredList, item -> {
            KecamatanModel kec = (KecamatanModel) item;
            autoKec.setText(kec.getName());
            textInputKel.setEnabled(true);
            autoKel.setText("");
            kelurahanModelList.clear();
            new GetKelurahanTask().execute(kec.getId());
            recyclerViewKec.setVisibility(View.GONE);
        });
        recyclerViewKec.setAdapter(adapter);
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

    private class GetProvinsiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return getDataFromApi(BASE_URL + "provinces.json");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Type listType = new TypeToken<ArrayList<ProvinsiModel>>() {}.getType();
                provinsiModelList = new Gson().fromJson(result, listType);

                WilayahAdapter<ProvinsiModel> adapter = new WilayahAdapter<>(provinsiModelList, item -> {
                    ProvinsiModel prov = (ProvinsiModel) item;
                    autoProv.setText(prov.getName());
                    textInputKab.setEnabled(true);
                    autoKab.setText("");
                    kabupatenModelList.clear();
                    new GetKabupatenTask().execute(prov.getId());
                    recyclerViewProv.setVisibility(View.GONE);
                    textInputKec.setEnabled(false);
                    autoKec.setText("");
                    kecamatanModelList.clear();
                    textInputKel.setEnabled(false);
                    autoKel.setText("");
                    kelurahanModelList.clear();
                });
                recyclerViewProv.setAdapter(adapter);

            } else {
                Toast.makeText(activity, "Gagal memuat data provinsi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetKabupatenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String provinceId = strings[0];
            return getDataFromApi(BASE_URL + "regencies/" + provinceId + ".json");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Type listType = new TypeToken<ArrayList<KabupatenModel>>() {}.getType();
                kabupatenModelList = new Gson().fromJson(result, listType);

                // Set adapter ke RecyclerView
                WilayahAdapter<KabupatenModel> adapter = new WilayahAdapter<>(kabupatenModelList, item -> {
                    KabupatenModel kab = (KabupatenModel) item;
                    autoKab.setText(kab.getName());
                    textInputKec.setEnabled(true);
                    autoKec.setText("");
                    kecamatanModelList.clear();
                    new GetKecamatanTask().execute(kab.getId());
                    recyclerViewKab.setVisibility(View.GONE);
                    textInputKel.setEnabled(false);
                    autoKel.setText("");
                    kelurahanModelList.clear();
                });
                recyclerViewKab.setAdapter(adapter);

                textInputKab.setEnabled(true);
                autoKab.setText("");

            } else {
                Toast.makeText(activity, "Gagal memuat data kabupaten", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetKecamatanTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String regencyId = strings[0];
            return getDataFromApi(BASE_URL + "districts/" + regencyId + ".json");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Type listType = new TypeToken<ArrayList<KecamatanModel>>() {}.getType();
                kecamatanModelList = new Gson().fromJson(result, listType);

                WilayahAdapter<KecamatanModel> adapter = new WilayahAdapter<>(kecamatanModelList, item -> {
                    KecamatanModel kec = (KecamatanModel) item;
                    autoKec.setText(kec.getName());
                    textInputKel.setEnabled(true);
                    autoKel.setText("");
                    kelurahanModelList.clear();
                    new GetKelurahanTask().execute(kec.getId());
                    recyclerViewKec.setVisibility(View.GONE);
                });
                recyclerViewKec.setAdapter(adapter);

                textInputKec.setEnabled(true);
                autoKec.setText("");

            } else {
                Toast.makeText(activity, "Gagal memuat data kecamatan", Toast.LENGTH_SHORT).show();
            }
        }
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