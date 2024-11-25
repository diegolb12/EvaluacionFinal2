package com.stomas.evaluacionfinal2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForoDetailActivity extends AppCompatActivity {

    private AppDatabase dbHelper;
    private int foroId;
    private ArrayList<String> comentariosList = new ArrayList<>();
    private ComentariosAdapter comentariosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foro_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalles del Foro");
        }

        dbHelper = new AppDatabase(this);
        foroId = getIntent().getIntExtra("foro_id", -1);

        TextView tvTituloForo = findViewById(R.id.tv_titulo_foro);
        TextView tvContenidoForo = findViewById(R.id.tv_contenido_foro);
        EditText etComentario = findViewById(R.id.et_comentario);
        Button btnGuardarComentario = findViewById(R.id.btn_guardar_comentario);
        RecyclerView rvComentarios = findViewById(R.id.rv_comentarios);

        comentariosAdapter = new ComentariosAdapter(comentariosList);
        rvComentarios.setLayoutManager(new LinearLayoutManager(this));
        rvComentarios.setAdapter(comentariosAdapter);

        cargarForo(tvTituloForo, tvContenidoForo);
        cargarComentarios();

        btnGuardarComentario.setOnClickListener(v -> {
            String comentario = etComentario.getText().toString().trim();
            if (!comentario.isEmpty()) {
                agregarComentario(comentario);
                etComentario.setText("");
                cargarComentarios();
            }
        });
    }

    private void cargarForo(TextView titulo, TextView contenido) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT titulo, contenido FROM comunidad WHERE id = ?", new String[]{String.valueOf(foroId)});

        if (cursor.moveToFirst()) {
            titulo.setText(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
            contenido.setText(cursor.getString(cursor.getColumnIndexOrThrow("contenido")));
        }
        cursor.close();
        db.close();
    }

    private void agregarComentario(String comentario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO comentarios (foro_id, comentario) VALUES (?, ?)", new String[]{String.valueOf(foroId), comentario});
        db.close();
    }

    private void cargarComentarios() {
        comentariosList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT comentario FROM comentarios WHERE foro_id = ?", new String[]{String.valueOf(foroId)});

        while (cursor.moveToNext()) {
            comentariosList.add(cursor.getString(cursor.getColumnIndexOrThrow("comentario")));
        }
        cursor.close();
        db.close();

        comentariosAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}