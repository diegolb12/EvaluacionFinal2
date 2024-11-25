package com.stomas.evaluacionfinal2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ViewHolder> {

    private final ArrayList<String> comentariosList;

    public ComentariosAdapter(ArrayList<String> comentariosList) {
        this.comentariosList = comentariosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String comentario = comentariosList.get(position);
        holder.tvComentario.setText(comentario);
    }

    @Override
    public int getItemCount() {
        return comentariosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComentario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComentario = itemView.findViewById(android.R.id.text1);
        }
    }
}