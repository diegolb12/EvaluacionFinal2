package com.stomas.evaluacionfinal2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    private AppDatabase dbHelper;
    private ArrayList<Foro> forosList = new ArrayList<>();
    private ForosAdapter forosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        dbHelper = new AppDatabase(this);

        EditText etTituloForo = findViewById(R.id.et_titulo_foro);
        EditText etContenidoForo = findViewById(R.id.et_contenido_foro);
        Button btnAgregarForo = findViewById(R.id.btn_agregar_foro);
        RecyclerView rvForos = findViewById(R.id.rv_foros);

        forosAdapter = new ForosAdapter(this, forosList);
        rvForos.setLayoutManager(new LinearLayoutManager(this));
        rvForos.setAdapter(forosAdapter);

        cargarForos();

        btnAgregarForo.setOnClickListener(v -> {
            String titulo = etTituloForo.getText().toString().trim();
            String contenido = etContenidoForo.getText().toString().trim();
            if (!titulo.isEmpty() && !contenido.isEmpty()) {
                agregarForo(titulo, contenido);
                etTituloForo.setText("");
                etContenidoForo.setText("");
                cargarForos();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_community);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;

            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    intent = new Intent(this, DashboardActivity.class);
                    break;
                case R.id.nav_calendar:
                    intent = new Intent(this, CalendarActivity.class);
                    break;
                case R.id.nav_community:
                    return true;
                case R.id.nav_calculator:
                    intent = new Intent(this, CalculatorActivity.class);
                    break;
            }
            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });
    }

    private void agregarForo(String titulo, String contenido) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO comunidad (titulo, contenido) VALUES (?, ?)", new String[]{titulo, contenido});
        db.close();
    }

    private void cargarForos() {
        forosList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, titulo, contenido FROM comunidad", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String contenido = cursor.getString(cursor.getColumnIndexOrThrow("contenido"));

            forosList.add(new Foro(id, titulo, contenido));
        }
        cursor.close();
        db.close();

        forosAdapter.notifyDataSetChanged();
    }
}