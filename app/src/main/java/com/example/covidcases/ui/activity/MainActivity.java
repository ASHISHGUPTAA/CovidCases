package com.example.covidcases.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covidcases.R;
import com.example.covidcases.repository.local.model.CountriesCases;
import com.example.covidcases.repository.remote.api.CovidApi;
import com.example.covidcases.ui.ViewModel;
import com.example.covidcases.util.ActivityUtil;
import com.example.covidcases.util.ApplicationConstants;
import com.example.covidcases.util.ApplicationSharedPreferences;
import com.example.covidcases.util.locationService.LocationService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements ApplicationConstants {

    private CovidApi covidApi;
    private  ViewModel viewModel;
    private TextView totalCases, filterName;
    private TextView deaths;
    private TextView recovered;
    private RecyclerView recyclerView;
    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
    private ApplicationSharedPreferences applicationSharedPreferences;
    private ImageView clearFilter;
    private LinearLayout filterLayout;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private List<Address> addresses;
    private Geocoder geocoder;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            geocoder = new Geocoder(context, Locale.getDefault());
            totalCases = findViewById(R.id.confirm);
            deaths = findViewById(R.id.death);
            recovered = findViewById(R.id.recover);
            recyclerView = findViewById(R.id.country_list);
            TextView sortData = findViewById(R.id.sort);
            LinearLayout filterData = findViewById(R.id.select_filter);
            filterLayout = findViewById(R.id.filter_layout);
            filterName = findViewById(R.id.filter_name);
            clearFilter = findViewById(R.id.clear_filter);
            covidApi = CovidApi.instance;
            viewModel = new ViewModel(MainActivity.this);
            startService(new Intent(this, LocationService.class));
            applicationSharedPreferences = new ApplicationSharedPreferences(MainActivity.this);
            List<CountriesCases> countriesCasesList = viewModel.getCovidListByTotalConfirmDescOrder();
            if (countriesCasesList == null || countriesCasesList.isEmpty()) {
                viewModel.getTotalCasesOfCovid(totalCases, deaths, recovered, recyclerView, MainActivity.this, F);
            } else {
                totalCases.setText(String.valueOf(viewModel.getSumOfTotalCasesByCountry(countriesCasesList, TOTAL_CASES)));
                deaths.setText(String.valueOf(viewModel.getSumOfTotalCasesByCountry(countriesCasesList, DEATHS)));
                recovered.setText(String.valueOf(viewModel.getSumOfTotalCasesByCountry(countriesCasesList, RECOVERED)));
                viewModel.displayCovidListByCountry(recyclerView, MainActivity.this);
            }

            sortData.setOnClickListener(view -> showSortOptions());  //show sort data
            filterData.setOnClickListener(view -> showFilterOptions());  //show filter data

            //Refreshed API after 2 minutes
            Handler handler = new Handler();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        viewModel.getTotalCasesOfCovid(totalCases, deaths, recovered, recyclerView, MainActivity.this, M);
                        ActivityUtil.removeAllCases(applicationSharedPreferences);
                        filterLayout.setVisibility(View.GONE);

                        boolean isNetworkConnected = ActivityUtil.isNetworkConnected(MainActivity.this);
                        if (isNetworkConnected) {
                            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(MainActivity.this));
                            Job job = dispatcher.newJobBuilder()
                                    .setTag("testing job")
                                    .setReplaceCurrent(false)
                                    .setRecurring(true)
                                    .setTrigger(Trigger.executionWindow(10, 20))
                                    .setConstraints(Constraint.ON_ANY_NETWORK)
                                    .setLifetime(Lifetime.FOREVER)
                                    .build();

                            dispatcher.mustSchedule(job);
                        } else {
                            AlertDialog alertDialog = ActivityUtil.createAlertDialog(INTERNET_ERROR, MainActivity.this);
                            alertDialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, PAGE_MESSAGE, Toast.LENGTH_LONG).show();
                    handler.postDelayed(this, INTERVAL);
                }
            }, INTERVAL);

            ActivityUtil.checkFilter(applicationSharedPreferences, filterLayout, filterName);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                }
            }

            clearFilter.setOnClickListener(view -> {
                try {
                    filterLayout.setVisibility(View.GONE);
                    ActivityUtil.removeAllCases(applicationSharedPreferences);
                    viewModel.displayCovidListByCountry(recyclerView, MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFilterOptions() {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            View layoutView = getLayoutInflater().inflate(R.layout.filter_layout, null);
            ImageView imageView = layoutView.findViewById(R.id.cancel);
            Button clearText = layoutView.findViewById(R.id.reset);
            Button done = layoutView.findViewById(R.id.done);
            EditText totalCaseGrt = layoutView.findViewById(R.id.tot_case_grt);
            EditText totalCaseLess = layoutView.findViewById(R.id.tot_case_less);
            EditText deathGrt = layoutView.findViewById(R.id.death_grt);
            EditText deathLess = layoutView.findViewById(R.id.death_less);
            EditText totalRecoverGrt = layoutView.findViewById(R.id.tot_rec_grt);
            EditText totalRecoverLess = layoutView.findViewById(R.id.tot_rec_less);
            dialogBuilder.setView(layoutView);
            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            if (applicationSharedPreferences.contains(SP_TOTAL_CONFIRM_GREATER_THAN)) {
                totalCaseGrt.setText((String)applicationSharedPreferences.get(SP_TOTAL_CONFIRM_GREATER_THAN));
            } if (applicationSharedPreferences.contains(SP_TOTAL_CONFIRM_LESS_THAN)) {
                totalCaseLess.setText((String) applicationSharedPreferences.get(SP_TOTAL_CONFIRM_LESS_THAN));
            } if (applicationSharedPreferences.contains(SP_TOTAL_DEATH_GREATER_THAN)) {
                deathGrt.setText((String) applicationSharedPreferences.get(SP_TOTAL_DEATH_GREATER_THAN));
            } if (applicationSharedPreferences.contains(SP_TOTAL_DEATH_LESS_THAN)) {
                deathLess.setText((String) applicationSharedPreferences.get(SP_TOTAL_DEATH_LESS_THAN));
            } if (applicationSharedPreferences.contains(SP_TOTAL_RECOVERED_GREATER_THAN)) {
                totalRecoverGrt.setText((String) applicationSharedPreferences.get(SP_TOTAL_RECOVERED_GREATER_THAN));
            } if (applicationSharedPreferences.contains(SP_TOTAL_RECOVERED_LESS_THAN)) {
                totalRecoverLess.setText((String) applicationSharedPreferences.get(SP_TOTAL_RECOVERED_LESS_THAN));
            }
            imageView.setOnClickListener(view -> alertDialog.dismiss());
            clearText.setOnClickListener(view -> {
                try {
                    ActivityUtil.removeAllCases(applicationSharedPreferences);
                    filterLayout.setVisibility(View.GONE);
                    viewModel.displayCovidListByCountry(recyclerView, MainActivity.this);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            done.setOnClickListener(view -> {
                try {
                    if (totalCaseGrt.getText().toString().equalsIgnoreCase("")) {
                        totalCaseGrt.setError("Please set value"); }
                    else {
                        viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotCaseGrt(Long.valueOf(totalCaseGrt.getText().toString())));
                        applicationSharedPreferences.put(SP_TOTAL_CONFIRM_GREATER_THAN, totalCaseGrt.getText().toString());
                        applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_LESS_THAN);
                        applicationSharedPreferences.remove(SP_TOTAL_DEATH_GREATER_THAN);
                        applicationSharedPreferences.remove(SP_TOTAL_DEATH_LESS_THAN);
                        applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_GREATER_THAN);
                        applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_LESS_THAN);
                        applicationSharedPreferences.put(SP_DYNAMIC_NAME, getString(R.string.total_cases_grt));
                        alertDialog.dismiss();
                        ActivityUtil.checkFilter(applicationSharedPreferences, filterLayout, filterName);
                    }
                 if (totalCaseLess.getText().toString().equalsIgnoreCase("")) {
                        totalCaseLess.setError("Please set value"); }
                 else {
                     viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotCaseLess(Long.valueOf(totalCaseLess.getText().toString())));
                     applicationSharedPreferences.put(SP_TOTAL_CONFIRM_LESS_THAN, totalCaseLess.getText().toString());
                     applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_DEATH_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_DEATH_LESS_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_LESS_THAN);
                     applicationSharedPreferences.put(SP_DYNAMIC_NAME, getString(R.string.total_cases_less));
                     alertDialog.dismiss();
                     ActivityUtil.checkFilter(applicationSharedPreferences, filterLayout, filterName);
                 }
                 if (deathGrt.getText().toString().equalsIgnoreCase("")) {
                        deathGrt.setError("Please set value"); }
                 else {
                     viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByDeathGrt(Long.valueOf(deathGrt.getText().toString())));
                     applicationSharedPreferences.put(SP_TOTAL_DEATH_GREATER_THAN, deathGrt.getText().toString());
                     applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_LESS_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_DEATH_LESS_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_LESS_THAN);
                     applicationSharedPreferences.put(SP_DYNAMIC_NAME, getString(R.string.total_death_grt));
                     alertDialog.dismiss();
                     ActivityUtil.checkFilter(applicationSharedPreferences, filterLayout, filterName);
                 }
                 if (deathLess.getText().toString().equalsIgnoreCase("")) {
                        deathLess.setError("Please set value"); }
                 else {
                     viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByDeathLess(Long.valueOf(deathLess.getText().toString())));
                     applicationSharedPreferences.put(SP_TOTAL_DEATH_LESS_THAN, deathLess.getText().toString());
                     applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_LESS_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_DEATH_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_LESS_THAN);
                     applicationSharedPreferences.put(SP_DYNAMIC_NAME, getString(R.string.total_death_less));
                     alertDialog.dismiss();
                     ActivityUtil.checkFilter(applicationSharedPreferences, filterLayout, filterName);
                 }
                 if (totalRecoverGrt.getText().toString().equalsIgnoreCase("")) {
                     totalRecoverGrt.setError("Please set value"); }
                 else {
                     viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotRecoverGrt(Long.valueOf(totalRecoverGrt.getText().toString())));
                     applicationSharedPreferences.put(SP_TOTAL_RECOVERED_GREATER_THAN, totalRecoverGrt.getText().toString());
                     applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_LESS_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_DEATH_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_DEATH_LESS_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_LESS_THAN);
                     applicationSharedPreferences.put(SP_DYNAMIC_NAME, getString(R.string.total_recovered_grt));
                     alertDialog.dismiss();
                     ActivityUtil.checkFilter(applicationSharedPreferences, filterLayout, filterName);
                 }
                 if (totalRecoverLess.getText().toString().equalsIgnoreCase("")) {
                        totalRecoverLess.setError("Please set value"); }
                 else {
                     viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotRecoverLess(Long.valueOf(totalRecoverLess.getText().toString())));
                     applicationSharedPreferences.put(SP_TOTAL_RECOVERED_LESS_THAN, totalRecoverLess.getText().toString());
                     applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_LESS_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_DEATH_GREATER_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_DEATH_LESS_THAN);
                     applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_GREATER_THAN);
                     applicationSharedPreferences.put(SP_DYNAMIC_NAME, getString(R.string.total_recovered_less));
                     alertDialog.dismiss();
                     ActivityUtil.checkFilter(applicationSharedPreferences, filterLayout, filterName);
                 }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSortOptions() {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            View layoutView = getLayoutInflater().inflate(R.layout.sort_layout, null);
            Button dialogButton = layoutView.findViewById(R.id.clear);
            CardView countryByAscOrder = layoutView.findViewById(R.id.country_asc);
            CardView countryByDescOrder = layoutView.findViewById(R.id.country_desc);
            CardView totalCaseByAscOrder = layoutView.findViewById(R.id.total_cases_asc);
            CardView totalCaseByDescOrder = layoutView.findViewById(R.id.total_cases_desc);
            CardView deathByAscOrder = layoutView.findViewById(R.id.death_asc);
            CardView deathByDescOrder = layoutView.findViewById(R.id.death_desc);
            CardView totalRecoverByAscOrder = layoutView.findViewById(R.id.total_recover_asc);
            CardView totalRecoverByDescOrder = layoutView.findViewById(R.id.total_recover_desc);
            TextView countryAscText = layoutView.findViewById(R.id.ca_txt);
            TextView countryDscText = layoutView.findViewById(R.id.cd_txt);
            TextView totCaseAscText = layoutView.findViewById(R.id.tca_txt);
            TextView totCaseDescText = layoutView.findViewById(R.id.tcd_txt);
            TextView deathAscText = layoutView.findViewById(R.id.da_txt);
            TextView deathDescText = layoutView.findViewById(R.id.dd_txt);
            TextView totRecoverAscText = layoutView.findViewById(R.id.tra_txt);
            TextView totRecoverDescText = layoutView.findViewById(R.id.trd_txt);
            dialogBuilder.setView(layoutView);
            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            if (applicationSharedPreferences.contains(SP_COUNTRY_BY_ASC)) {
                countryByAscOrder.setCardBackgroundColor(getResources().getColor(R.color.Red));
                countryAscText.setTextColor(getResources().getColor(R.color.white));}
            else if (applicationSharedPreferences.contains(SP_COUNTRY_BY_DESC)) {countryByDescOrder.setCardBackgroundColor(getResources().getColor(R.color.Red));
                countryDscText.setTextColor(getResources().getColor(R.color.white));}
            if (applicationSharedPreferences.contains(SP_TOTAL_CASE_BY_ASC)) {totalCaseByAscOrder.setCardBackgroundColor(getResources().getColor(R.color.Red));
                totCaseAscText.setTextColor(getResources().getColor(R.color.white));}
            else if (applicationSharedPreferences.contains(SP_TOTAL_CASE_BY_DESC)) {totalCaseByDescOrder.setCardBackgroundColor(getResources().getColor(R.color.Red));
                totCaseDescText.setTextColor(getResources().getColor(R.color.white));}
            if (applicationSharedPreferences.contains(SP_DEATH_BY_ASC)) {deathByAscOrder.setCardBackgroundColor(getResources().getColor(R.color.Red));
                deathAscText.setTextColor(getResources().getColor(R.color.white));}
            else if (applicationSharedPreferences.contains(SP_DEATH_BY_DESC)) {deathByDescOrder.setCardBackgroundColor(getResources().getColor(R.color.Red));
                deathDescText.setTextColor(getResources().getColor(R.color.white));}
            if (applicationSharedPreferences.contains(SP_TOTAL_RECOVER_BY_ASC)) {totalRecoverByAscOrder.setCardBackgroundColor(getResources().getColor(R.color.Red));
                totRecoverAscText.setTextColor(getResources().getColor(R.color.white));}
            else if (applicationSharedPreferences.contains(SP_TOTAL_RECOVER_BY_DESC)) {totalRecoverByDescOrder.setCardBackgroundColor(getResources().getColor(R.color.Red));
                totRecoverDescText.setTextColor(getResources().getColor(R.color.white));}
            countryByAscOrder.setOnClickListener(view -> {
                try {
                    viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByCountryAscOrder());
                    applicationSharedPreferences.put(SP_COUNTRY_BY_ASC, 1L);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_DESC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_ASC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_DESC);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            countryByDescOrder.setOnClickListener(view -> {
                try {
                    viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByCountryDescOrder());
                    applicationSharedPreferences.put(SP_COUNTRY_BY_DESC, 2L);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_ASC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_ASC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_DESC);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            totalCaseByAscOrder.setOnClickListener(view -> {
                try {
                    viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotalConfirmAscOrder());
                    applicationSharedPreferences.put(SP_TOTAL_CASE_BY_ASC, 3L);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_ASC);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_DESC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_ASC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_DESC);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            totalCaseByDescOrder.setOnClickListener(view -> {
                try {
                    viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotalConfirmDescOrder());
                    applicationSharedPreferences.put(SP_TOTAL_CASE_BY_DESC, 4L);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_ASC);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_DESC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_ASC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_DESC);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            deathByAscOrder.setOnClickListener(view -> {
                try {
                    viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotalDeathAscOrder());
                    applicationSharedPreferences.put(SP_DEATH_BY_ASC, 5L);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_ASC);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_DESC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_DESC);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            deathByDescOrder.setOnClickListener(view -> {
                try {
                    viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotalDeathDescOrder());
                    applicationSharedPreferences.put(SP_DEATH_BY_DESC, 6L);
                    applicationSharedPreferences.remove(SP_DEATH_BY_ASC);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_ASC);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_DESC);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            totalRecoverByAscOrder.setOnClickListener(view -> {
                try {
                    viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotalRecoverAscOrder());
                    applicationSharedPreferences.put(SP_TOTAL_RECOVER_BY_ASC, 7L);
                    applicationSharedPreferences.remove(SP_DEATH_BY_ASC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_DESC);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_ASC);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_DESC);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            totalRecoverByDescOrder.setOnClickListener(view -> {
                try {
                    viewModel.displayCovidListByOrder(recyclerView, MainActivity.this, viewModel.getCovidListByTotalRecoverDescOrder());
                    applicationSharedPreferences.put(SP_TOTAL_RECOVER_BY_DESC, 8L);
                    applicationSharedPreferences.remove(SP_DEATH_BY_ASC);
                    applicationSharedPreferences.remove(SP_DEATH_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_ASC);
                    applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_DESC);
                    applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_ASC);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_ASC);
                    applicationSharedPreferences.remove(SP_COUNTRY_BY_DESC);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            dialogButton.setOnClickListener(view -> {
                try {
                    viewModel.displayCovidListByCountry(recyclerView, MainActivity.this);
                    ActivityUtil.removeAllColumnData(applicationSharedPreferences);
                    alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Please Allow Permission!")
                        .setMessage("Please Allow location to continue the application")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                                    }
                                }
                            }
                        });
                dialog.show();
            }
        }
    }

    GoogleApiClient googleApiClient;

    private void displayLocationSettingsRequest(final Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public boolean statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean check = true;
        if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return check;
    }

    private void buildAlertMessageNoGps() {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            displayLocationSettingsRequest(MainActivity.this);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            AlertDialog.Builder dialogLocation = new AlertDialog.Builder(MainActivity.this);
                            dialogLocation.setTitle("Please Allow Permission!")
                                    .setMessage("Please Allow location to continue the application")
                                    .setCancelable(false)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            statusCheck();
                                        }
                                    });
                            dialogLocation.show();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        trackLocation(MainActivity.this);
                        break;
                    case Activity.RESULT_CANCELED:
                        AlertDialog.Builder dialogLocation = new AlertDialog.Builder(MainActivity.this);
                        dialogLocation.setTitle("Please Allow Permission!")
                                .setMessage("Please Allow location to continue the application")
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        statusCheck();
                                    }
                                });
                        dialogLocation.show();
                        break;
                }
                break;
        }
    }

    private void trackLocation(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ((Activity) context).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                return;
            }
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    if (location != null) {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        applicationSharedPreferences.put(SP_USER_COUNTRY_NAME, addresses.get(0).getCountryName());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60 * 60 * 2, 100, locationListener);
    }

}
