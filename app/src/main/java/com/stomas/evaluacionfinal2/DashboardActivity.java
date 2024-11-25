package com.stomas.evaluacionfinal2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private AppDatabase dbHelper;
    private EventosDashboardAdapter eventosAdapter;
    private List<String> eventosList = new ArrayList<>();
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new AppDatabase(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos", null);
        if (cursor.getCount() == 0) {
            db.execSQL("INSERT INTO gastos (categoria, monto) VALUES ('Alimentos', 200)");
            db.execSQL("INSERT INTO gastos (categoria, monto) VALUES ('Transporte', 100)");
            db.execSQL("INSERT INTO gastos (categoria, monto) VALUES ('Entretenimiento', 150)");
            db.execSQL("INSERT INTO gastos (categoria, monto) VALUES ('Educación', 300)");
        }
        cursor.close();
        db.close();

        pieChart = findViewById(R.id.pie_chart);
        actualizarGrafico(pieChart);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, com.github.mikephil.charting.highlight.Highlight h) {
                PieEntry entry = (PieEntry) e;
                String categoriaSeleccionada = entry.getLabel();
                float montoActual = entry.getValue();

                mostrarDialogoActualizarEliminar(categoriaSeleccionada, montoActual);
            }

            @Override
            public void onNothingSelected() {
            }
        });

        RecyclerView rvEventos = findViewById(R.id.rv_eventos);
        eventosAdapter = new EventosDashboardAdapter(eventosList);
        rvEventos.setLayoutManager(new LinearLayoutManager(this));
        rvEventos.setAdapter(eventosAdapter);

        cargarEventos();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;

            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    return true; // Ya estás en esta ventana
                case R.id.nav_calendar:
                    intent = new Intent(this, CalendarActivity.class);
                    break;
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "DashboardActivity resumed.");
        actualizarGrafico(pieChart);
        cargarEventos();
    }

    private void actualizarGrafico(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT categoria, SUM(monto) AS total FROM gastos GROUP BY categoria", null);
            while (cursor.moveToNext()) {
                String categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"));
                float total = cursor.getFloat(cursor.getColumnIndexOrThrow("total"));
                entries.add(new PieEntry(total, categoria));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en la consulta de base de datos: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        if (entries.isEmpty()) {
            Log.w(TAG, "No hay datos para mostrar en el gráfico.");
        }

        PieDataSet dataSet = new PieDataSet(entries, "Gastos por Categoría");
        dataSet.setColors(new int[]{android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_green_light}, this);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void mostrarDialogoActualizarEliminar(String categoria, float montoActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones de Gasto");
        builder.setMessage("Categoría: " + categoria + "\nMonto Actual: $" + montoActual);

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf(montoActual));
        builder.setView(input);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String nuevoMontoTexto = input.getText().toString().trim();
            if (!nuevoMontoTexto.isEmpty()) {
                try {
                    float nuevoMonto = Float.parseFloat(nuevoMontoTexto);
                    actualizarGastoEnBaseDeDatos(categoria, nuevoMonto);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Eliminar", (dialog, which) -> eliminarGastoEnBaseDeDatos(categoria));

        builder.setNeutralButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void actualizarGastoEnBaseDeDatos(String categoria, float nuevoMonto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("monto", nuevoMonto);

        int filasAfectadas = db.update("gastos", valores, "categoria = ?", new String[]{categoria});

        if (filasAfectadas > 0) {
            Toast.makeText(this, "Gasto actualizado con éxito", Toast.LENGTH_SHORT).show();
            actualizarGrafico(pieChart);
        } else {
            Toast.makeText(this, "Error al actualizar el gasto", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void eliminarGastoEnBaseDeDatos(String categoria) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasEliminadas = db.delete("gastos", "categoria = ?", new String[]{categoria});

        if (filasEliminadas > 0) {
            Toast.makeText(this, "Gasto eliminado con éxito", Toast.LENGTH_SHORT).show();
            actualizarGrafico(pieChart);
        } else {
            Toast.makeText(this, "Error al eliminar el gasto", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void cargarEventos() {
        eventosList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT titulo, fecha FROM eventos ORDER BY fecha ASC LIMIT 5", null);

        while (cursor.moveToNext()) {
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
            eventosList.add(fecha + " - " + titulo);
        }

        cursor.close();
        db.close();

        eventosAdapter.notifyDataSetChanged();
    }
}