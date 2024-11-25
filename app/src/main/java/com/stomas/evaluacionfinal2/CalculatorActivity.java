package com.stomas.evaluacionfinal2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CalculatorActivity extends AppCompatActivity {

    private AppDatabase dbHelper;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        dbHelper = new AppDatabase(this);

        EditText etCategoria = findViewById(R.id.et_categoria);
        EditText etMonto = findViewById(R.id.et_monto);
        CalendarView calendarGasto = findViewById(R.id.calendar_gasto);
        Button btnGuardarGasto = findViewById(R.id.btn_guardar_gasto);

        calendarGasto.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
        });

        btnGuardarGasto.setOnClickListener(v -> {
            String categoria = etCategoria.getText().toString().trim();
            String montoTexto = etMonto.getText().toString().trim();

            if (categoria.isEmpty() || montoTexto.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float monto = Float.parseFloat(montoTexto);
                guardarGasto(categoria, monto, selectedDate);
                Toast.makeText(this, "Gasto guardado con éxito", Toast.LENGTH_SHORT).show();
                etCategoria.setText("");
                etMonto.setText("");
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_calculator);

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
                    intent = new Intent(this, CommunityActivity.class);
                    break;
                case R.id.nav_calculator:
                    return true;
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }

            return true;
        });
    }
    private void guardarGasto(String categoria, float monto, String fecha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO gastos (categoria, monto, fecha) VALUES (?, ?, ?)", new Object[]{categoria, monto, fecha});
        db.close();
    }
}