package com.example.tsunami;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquackeViewHolder> {
    private List<Earthquake> earthquakes;

    public EarthquakeAdapter(List<Earthquake> earthquakes) {
        this.earthquakes = earthquakes;
    }

    @NonNull
    @Override
    public EarthquackeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_earthquake, parent, false);

        EarthquackeViewHolder holder = new EarthquackeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EarthquackeViewHolder holder, int position) {
        Earthquake earthquake = earthquakes.get(position);

        holder.textViewTitle.setText(earthquake.getTitle());
        holder.textViewDate.setText(getTimeString(earthquake.getTime()));
        holder.textViewTsunami.setText(getTsunamiString(earthquake.getTsunami()));
    }

    @Override
    public int getItemCount() {
        return earthquakes.size();
    }

    class EarthquackeViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDate;
        private TextView textViewTsunami;

        public EarthquackeViewHolder(@NonNull View itemView) {
            super(itemView);

            initViews();
        }

        private void initViews() {
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewTsunami = itemView.findViewById(R.id.text_view_tsunami);
        }
    }

    private String getTimeString(long time) {
        String pattern = "EEE, dd MMM YYYY 'at' hh:mm:ss z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(time));
    }

    private String getTsunamiString(int tsunami) {
        switch (tsunami) {
            case 1:
                return "yes";
            case 0:
                return "no";
            default:
                return "not available";
        }
    }
}
