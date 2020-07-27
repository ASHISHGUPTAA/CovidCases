package com.example.covidcases.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covidcases.repository.local.model.CountriesCases;
import com.example.covidcases.repository.remote.api.CovidApi;
import com.example.covidcases.repositoryconfig.CovidDatabase;
import com.example.covidcases.ui.adapter.CountryCaseAdapter;
import com.example.covidcases.util.ApplicationConstants;
import com.example.covidcases.util.RetrofitUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModel extends androidx.lifecycle.ViewModel implements ApplicationConstants {

    private CovidApi covidApi;
    private CovidDatabase covidDatabase;

    public ViewModel(Context context) {
        this.covidApi = CovidApi.instance;
        this.covidDatabase = RetrofitUtil.getDatabase(context);
    }

    public Long getSumOfTotalCasesByCountry(List<CountriesCases> countriesCasesList, String Cases) throws Exception {
        Long totalCases = 0L;
            if (TOTAL_CASES.equalsIgnoreCase(Cases)) {
                for (CountriesCases cc : countriesCasesList) {
                    if (cc.getTotalConfirmed() != null) {
                        totalCases = totalCases + cc.getTotalConfirmed();
                    }
                }
            } else if (DEATHS.equalsIgnoreCase(Cases)) {
            for (CountriesCases cc : countriesCasesList) {
                if (cc.getTotalDeaths() != null) {
                    totalCases = totalCases + cc.getTotalDeaths();
                }
            }
            } else if (RECOVERED.equalsIgnoreCase(Cases)) {
            for (CountriesCases cc : countriesCasesList) {
                if (cc.getTotalRecovered() != null) {
                    totalCases = totalCases + cc.getTotalRecovered();
                }
            }
            }
        return totalCases;
    }

    public List<CountriesCases> getCovidListByTotalConfirmDescOrder() throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotalConfirmDescOrder();
    }

    public List<CountriesCases> getCovidListByTotalConfirmAscOrder() throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotalConfirmAscOrder();
    }

    public List<CountriesCases> getCovidListByTotalDeathDescOrder() throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotalDeathDescOrder();
    }

    public List<CountriesCases> getCovidListByTotalDeathAscOrder() throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotalDeathAscOrder();
    }

    public List<CountriesCases> getCovidListByCountryDescOrder() throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByCountryDescOrder();
    }

    public List<CountriesCases> getCovidListByCountryAscOrder() throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByCountryAscOrder();
    }

    public List<CountriesCases> getCovidListByTotalRecoverDescOrder() throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotalRecoverDescOrder();
    }

    public List<CountriesCases> getCovidListByTotalRecoverAscOrder() throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotalRecoverAscOrder();
    }

    public List<CountriesCases> getCovidListByTotCaseGrt(Long totalCaseGrtNo) throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotCaseGrt(totalCaseGrtNo);
    }

    public List<CountriesCases> getCovidListByTotCaseLess(Long totalCaseLessNo) throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotCaseLess(totalCaseLessNo);
    }

    public List<CountriesCases> getCovidListByDeathGrt(Long deathGrtNo) throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByDeathGrt(deathGrtNo);
    }

    public List<CountriesCases> getCovidListByDeathLess(Long deathLessNo) throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByDeathLess(deathLessNo);
    }

    public List<CountriesCases> getCovidListByTotRecoverGrt(Long totalRecGrtNo) throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotRecoverGrt(totalRecGrtNo);
    }

    public List<CountriesCases> getCovidListByTotRecoverLess(Long totalRecLessNo) throws Exception {
        return covidDatabase.countriesCasesDao().getCovidListByTotRecoverLess(totalRecLessNo);
    }

    public void displayCovidListByCountry(RecyclerView recyclerView, Context context) throws Exception {
        List<CountriesCases> countriesCasesList = covidDatabase.countriesCasesDao().getCovidListByTotalConfirmDescOrder();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        CountryCaseAdapter adapter = new CountryCaseAdapter(countriesCasesList);
        recyclerView.setAdapter(adapter);
    }

    public void displayCovidListByOrder(RecyclerView recyclerView, Context context, List<CountriesCases> countriesCasesList) throws Exception {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        CountryCaseAdapter adapter = new CountryCaseAdapter(countriesCasesList);
        recyclerView.setAdapter(adapter);
    }

    public void getTotalCasesOfCovid(final TextView totalCases, final TextView deaths, final TextView recovered, final RecyclerView recyclerView, final Context context, String refresh) throws Exception {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        if (F.equalsIgnoreCase(refresh)) {
            progressDialog.setTitle(LOADING_DATA);
            progressDialog.setMessage(PLEASE_WAIT);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        Call<JsonObject> jsonData = this.covidApi.getCovidCases(COVID_19_URL);
        jsonData.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    if (jsonObject.has("Countries")) {
                        JSONArray jsonArray = (JSONArray) jsonObject.get("Countries");
                        List<CountriesCases> countriesCasesList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CountriesCases>>() {
                        }.getType());
                        if (F.equalsIgnoreCase(refresh)) {
                            covidDatabase.countriesCasesDao().insertData(countriesCasesList);
                        } else if (M.equalsIgnoreCase(refresh)) {
                            covidDatabase.countriesCasesDao().deleteAllData();
                            covidDatabase.countriesCasesDao().insertData(countriesCasesList);
                        }
                        //print covid cases
                        totalCases.setText(String.valueOf(getSumOfTotalCasesByCountry(countriesCasesList, TOTAL_CASES)));
                        deaths.setText(String.valueOf(getSumOfTotalCasesByCountry(countriesCasesList, DEATHS)));
                        recovered.setText(String.valueOf(getSumOfTotalCasesByCountry(countriesCasesList, RECOVERED)));
                        displayCovidListByCountry(recyclerView, context);
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, NETWORK_CALL_ERROR, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        });

    }
}
