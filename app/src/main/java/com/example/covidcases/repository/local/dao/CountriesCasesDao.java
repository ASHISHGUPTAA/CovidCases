package com.example.covidcases.repository.local.dao;

import com.example.covidcases.repository.local.model.CountriesCases;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CountriesCasesDao {

    @Insert
    void insertData(List<CountriesCases> countriesCasesList) throws Exception;

    @Query("DELETE FROM COUNTRY_CASES")
    void deleteAllData() throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 ORDER BY TOTAL_CONFIRMED DESC")
    List<CountriesCases> getCovidListByTotalConfirmDescOrder() throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 ORDER BY TOTAL_CONFIRMED ASC")
    List<CountriesCases> getCovidListByTotalConfirmAscOrder() throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 ORDER BY TOTAL_DEATHS DESC")
    List<CountriesCases> getCovidListByTotalDeathDescOrder() throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 ORDER BY TOTAL_DEATHS ASC")
    List<CountriesCases> getCovidListByTotalDeathAscOrder() throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 ORDER BY COUNTRY DESC")
    List<CountriesCases> getCovidListByCountryDescOrder() throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 ORDER BY COUNTRY ASC")
    List<CountriesCases> getCovidListByCountryAscOrder() throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 ORDER BY TOTAL_RECOVERED DESC")
    List<CountriesCases> getCovidListByTotalRecoverDescOrder() throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 ORDER BY TOTAL_RECOVERED ASC")
    List<CountriesCases> getCovidListByTotalRecoverAscOrder() throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 AND TOTAL_CONFIRMED >= :totalCaseGrt ORDER BY TOTAL_CONFIRMED ASC")
    List<CountriesCases> getCovidListByTotCaseGrt(Long totalCaseGrt) throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 AND TOTAL_CONFIRMED <= :totalCaseLess ORDER BY TOTAL_CONFIRMED ASC")
    List<CountriesCases> getCovidListByTotCaseLess(Long totalCaseLess) throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 AND TOTAL_DEATHS >= :deathGrt ORDER BY TOTAL_DEATHS ASC")
    List<CountriesCases> getCovidListByDeathGrt(Long deathGrt) throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 AND TOTAL_DEATHS <= :deathLess ORDER BY TOTAL_DEATHS ASC")
    List<CountriesCases> getCovidListByDeathLess(Long deathLess) throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 AND TOTAL_RECOVERED >= :totalRecGrt ORDER BY TOTAL_RECOVERED ASC")
    List<CountriesCases> getCovidListByTotRecoverGrt(Long totalRecGrt) throws Exception;

    @Query("SELECT NEW_CONFIRMED, COUNTRY, TOTAL_CONFIRMED, TOTAL_DEATHS, TOTAL_RECOVERED FROM COUNTRY_CASES WHERE NEW_CONFIRMED != 0 AND TOTAL_RECOVERED <= :totalRecLess ORDER BY TOTAL_RECOVERED ASC")
    List<CountriesCases> getCovidListByTotRecoverLess(Long totalRecLess) throws Exception;
}
