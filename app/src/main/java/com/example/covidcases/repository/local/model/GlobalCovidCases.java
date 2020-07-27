package com.example.covidcases.repository.local.model;

public class GlobalCovidCases {

    private Long NewConfirmed;
    private Long TotalConfirmed;
    private Long NewDeaths;
    private Long TotalDeaths;
    private Long NewRecovered;
    private Long TotalRecovered;

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
}
