package org.insbaixcamp.appweather;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView navegacioInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        navegacioInferior = findViewById(R.id.navegacioInferior);
        navegacioInferior.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_inici) {
            // Gestionar acció d'inici
            return true;
        } else if (id == R.id.nav_notificacions) {
            // Gestionar acció de notificacions
            return true;
        } else if (id == R.id.nav_configuracio) {
            // Gestionar acció de configuració
            return true;
        }
        return false;
    }
}