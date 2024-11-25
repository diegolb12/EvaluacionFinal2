package com.stomas.evaluacionfinal2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    private AppDatabase dbHelper;
    private String selectedDate = "";
    private ArrayList<String> eventosList = new ArrayList<>();
    private EventosAdapter eventosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbHelper = new AppDatabase(this);

        CalendarView calendarView = findViewById(R.id.calendar_view);
        EditText etTitulo = findViewById(R.id.et_titulo);
        EditText etDescripcion = findViewById(R.id.et_descripcion);
        Button btnGuardarEvento = findViewById(R.id.btn_guardar_evento);
        RecyclerView rvEventos = findViewById(R.id.rv_eventos);

        eventosAdapter = new EventosAdapter(eventosList);
        rvEventos.setLayoutManager(new LinearLayoutManager(this));
        rvEventos.setAdapter(eventosAdapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            cargarEventosParaFecha();
        });

        btnGuardarEvento.setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            if (!titulo.isEmpty() && !descripcion.isEmpty() && !selectedDate.isEmpty()) {
                guardarEvento(titulo, descripcion, selectedDate);
                etTitulo.setText("");
                etDescripcion.setText("");
                cargarEventosParaFecha();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_calendar);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;

            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    intent = new Intent(this, DashboardActivity.class);
                    break;
                case R.id.nav_calendar:
                    return true;
                case R.id.nav_community:
                    intent = new Intent(this, CommunityActivity.class);
                    break;
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

    private void guardarEvento(String titulo, String descripcion, String fecha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO eventos (titulo, fecha, descripcion) VALUES (?, ?, ?)", new String[]{titulo, fecha, descripcion});
        db.close();
    }

    private void cargarEventosParaFecha() {
        eventosList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT titulo, descripcion FROM eventos WHERE fecha = ?", new String[]{selectedDate});

        while (cursor.moveToNext()) {
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            eventosList.add(titulo + ": " + descripcion);
        }
        cursor.close();
        db.close();

        eventosAdapter.notifyDataSetChanged();
    }
}