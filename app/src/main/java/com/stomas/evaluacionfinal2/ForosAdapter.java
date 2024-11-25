package com.stomas.evaluacionfinal2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ForosAdapter extends RecyclerView.Adapter<ForosAdapter.ViewHolder> {

    private final ArrayList<Foro> forosList;
    private final Context context;

    public ForosAdapter(Context context, ArrayList<Foro> forosList) {
        this.context = context;
        this.forosList = forosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Foro foro = forosList.get(position);
        holder.tvTitulo.setText(foro.getTitulo());
        holder.tvContenido.setText(foro.getContenido());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ForoDetailActivity.class);
            intent.putExtra("foro_id", foro.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return forosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvContenido;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(android.R.id.text1);
            tvContenido = itemView.findViewById(android.R.id.text2);
        }
    }
}