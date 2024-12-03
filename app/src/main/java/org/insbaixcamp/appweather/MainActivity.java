package org.insbaixcamp.appweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView navegacioInferior;

    // Variables per gestionar la ubicació i l'API
    private FusedLocationProviderClient fusedLocationClient;
    private final String API_KEY = "d0bd38f37ceb34026fb1327ae0fbd7e7";

    // TextViews per mostrar informació d'ubicació i resposta
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvApiResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialitza els TextViews
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvApiResponse = findViewById(R.id.tvApiResponse);

        // Inicialitza el client d'ubicació
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inicialitza la navegació inferior
        navegacioInferior = findViewById(R.id.navegacioInferior);
        navegacioInferior.setOnItemSelectedListener(this);

        // Sol·licita permisos d'ubicació
        soliciarPermisosUbicacio();

        // Carrega el fragment inicial només si és la primera càrrega
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new IniciFragment())
                    .commit();
        }
    }

    /**
     * Sol·licita permisos d'ubicació a l'usuari
     */
    private void soliciarPermisosUbicacio() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            obtenirUltimaUbicacio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenirUltimaUbicacio();
        } else {
            Log.e("Permisos", "L'usuari ha denegat el permís d'ubicació.");
        }
    }

    /**
     * Obté l'última ubicació coneguda
     */
    private void obtenirUltimaUbicacio() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();

                    tvLatitude.setText("Latitud: " + latitud);
                    tvLongitude.setText("Longitud: " + longitud);

                    Log.d("Ubicació", "Latitud: " + latitud + ", Longitud: " + longitud);
                    consultarAPIMeteorologica(latitud, longitud);
                } else {
                    Log.e("Ubicació", "No s'ha detectat cap ubicació.");
                }
            });
        }
    }

    /**
     * Consulta l'API meteorològica
     */
    private void consultarAPIMeteorologica(double latitud, double longitud) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitud + "&lon=" + longitud + "&appid=" + API_KEY + "&units=metric";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API", "Error en la consulta: " + e.getMessage());
                runOnUiThread(() -> tvApiResponse.setText("Error en la consulta a l'API."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resposta = response.body().string();
                    Log.d("API", "Resposta de l'API: " + resposta);
                    runOnUiThread(() -> tvApiResponse.setText("Resposta de l'API: " + resposta));
                } else {
                    Log.e("API", "Resposta no vàlida. Codi: " + response.code());
                    runOnUiThread(() -> tvApiResponse.setText("Error: Codi de resposta " + response.code()));
                }
            }
        });
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
