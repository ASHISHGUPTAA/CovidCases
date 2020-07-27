package com.example.covidcases.repositoryconfig;

import com.example.covidcases.repository.local.dao.CountriesCasesDao;
import com.example.covidcases.repository.local.model.CountriesCases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
        CountriesCases.class
}, version = 1)

public abstract class CovidDatabase extends RoomDatabase {

    public abstract CountriesCasesDao countriesCasesDao();
}
