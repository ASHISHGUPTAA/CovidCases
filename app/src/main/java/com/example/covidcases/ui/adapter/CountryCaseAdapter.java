package com.example.covidcases.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.covidcases.R;
import com.example.covidcases.repository.local.model.CountriesCases;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

public class CountryCaseAdapter extends RecyclerView.Adapter<CountryCaseAdapter.ViewHolder> {

    private List<CountriesCases> countriesCasesList;
    private SortedList<CountriesCases> list;

    public CountryCaseAdapter(List<CountriesCases> countriesCases) {
        this.countriesCasesList = countriesCases;

        list = new SortedList<CountriesCases>(CountriesCases.class, new SortedList.Callback<CountriesCases>() {
            @Override
            public int compare(CountriesCases o1, CountriesCases o2) {
                return o1.getTotalConfirmed().compareTo(o2.getTotalConfirmed());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(CountriesCases oldItem, CountriesCases newItem) {
                return oldItem.getTotalConfirmed().equals(newItem.getTotalConfirmed());
            }

            @Override
            public boolean areItemsTheSame(CountriesCases item1, CountriesCases item2) {
                return item1.getTotalConfirmed().equals(item2.getTotalConfirmed());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    public void addAll(List<CountriesCases> countries) {
        list.beginBatchedUpdates();
        for (int i = 0; i < countries.size(); i++) {
            list.add(countries.get(i));
        }
        list.endBatchedUpdates();
    }

    public CountriesCases get(int position) {
        return list.get(position);
    }

    public void clear() {
        list.beginBatchedUpdates();
        while (list.size() > 0) {
            list.removeItemAt(list.size() - 1);
        }
        list.endBatchedUpdates();
    }

    @NonNull
    @Override
    public CountryCaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_list_recycler, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.countryName.setText(countriesCasesList.get(position).getCountry().trim());
            holder.totalCases.setText(String.valueOf(countriesCasesList.get(position).getTotalConfirmed()));
            holder.deaths.setText(String.valueOf(countriesCasesList.get(position).getTotalDeaths()));
            holder.recovered.setText(String.valueOf(countriesCasesList.get(position).getTotalRecovered()));
    }

    @Override
    public int getItemCount() {
        return countriesCasesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView countryName, totalCases, deaths, recovered;

        ViewHolder(View itemView) {
            super(itemView);
            countryName = itemView.findViewById(R.id.country_name);
            totalCases = itemView.findViewById(R.id.total_cases);
            deaths = itemView.findViewById(R.id.deaths);
            recovered = itemView.findViewById(R.id.recovered);
        }
    }
}

