package com.stomas.evaluacionfinal2;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

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
                    intent = new Intent(this, CalculatorActivity.class);
                    break;
                default:
                    return false;
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }

            return true;
        });

        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
}