package com.stomas.evaluacionfinal2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventosDashboardAdapter extends RecyclerView.Adapter<EventosDashboardAdapter.ViewHolder> {

    private final List<String> eventosList;

    public EventosDashboardAdapter(List<String> eventosList) {
        this.eventosList = eventosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvEvento.setText(eventosList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEvento;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEvento = itemView.findViewById(android.R.id.text1);
        }
    }
}