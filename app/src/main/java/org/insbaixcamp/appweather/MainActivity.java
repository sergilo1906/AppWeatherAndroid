package org.insbaixcamp.appweather;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

        // Carregar el fragment inicial només si és la primera càrrega
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new IniciFragment())
                .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();
        
        if (id == R.id.nav_inici) {
            fragment = new IniciFragment();
        } else if (id == R.id.nav_notificacions) {
            fragment = new NotificacionsFragment();
        } else if (id == R.id.nav_configuracio) {
            fragment = new ConfiguracioFragment();
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
            return true;
        }
        return false;
    }
}