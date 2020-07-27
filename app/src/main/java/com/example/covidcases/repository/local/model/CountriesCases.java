package com.example.covidcases.repository.local.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "COUNTRY_CASES")
public class CountriesCases {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "PK")
    private Long pk;

    @ColumnInfo(name = "COUNTRY")
    private String Country;

    @ColumnInfo(name = "COUNTRY_CODE")
    private String CountryCode;

    @ColumnInfo(name = "SLUG")
    private String Slug;

    @ColumnInfo(name = "NEW_CONFIRMED")
    private Long NewConfirmed;

    @ColumnInfo(name = "TOTAL_CONFIRMED")
    private Long TotalConfirmed;

    @ColumnInfo(name = "NEW_DEATHS")
    private Long NewDeaths;

    @ColumnInfo(name = "TOTAL_DEATHS")
    private Long TotalDeaths;

    @ColumnInfo(name = "NEW_RECOVERED")
    private Long NewRecovered;

    @ColumnInfo(name = "TOTAL_RECOVERED")
    private Long TotalRecovered;

    @ColumnInfo(name = "DATE")
    private String Date;

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getSlug() {
        return Slug;
    }

    public void setSlug(String slug) {
        Slug = slug;
    }

    public Long getNewConfirmed() {
        return NewConfirmed;
    }

    public void setNewConfirmed(Long newConfirmed) {
        NewConfirmed = newConfirmed;
    }

    public Long getTotalConfirmed() {
        return TotalConfirmed;
    }

    public void setTotalConfirmed(Long totalConfirmed) {
        TotalConfirmed = totalConfirmed;
    }

    public Long getNewDeaths() {
        return NewDeaths;
    }

    public void setNewDeaths(Long newDeaths) {
        NewDeaths = newDeaths;
    }

    public Long getTotalDeaths() {
        return TotalDeaths;
    }

    public void setTotalDeaths(Long totalDeaths) {
        TotalDeaths = totalDeaths;
    }

    public Long getNewRecovered() {
        return NewRecovered;
    }

    public void setNewRecovered(Long newRecovered) {
        NewRecovered = newRecovered;
    }

    public Long getTotalRecovered() {
        return TotalRecovered;
    }

    public void setTotalRecovered(Long totalRecovered) {
        TotalRecovered = totalRecovered;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
