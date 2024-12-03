package org.insbaixcamp.appweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient; // Client per gestionar la ubicació
    private final String API_KEY = "d0bd38f37ceb34026fb1327ae0fbd7e7"; // Substitueix amb la teva clau real de l'API

    // TextViews per mostrar informació
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvApiResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialitza els TextView
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvApiResponse = findViewById(R.id.tvApiResponse);

        // Inicialitza el client d'ubicació
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Sol·licita permisos per obtenir la ubicació
        soliciarPermisosUbicacio();
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

    /**
     * Gestiona la resposta de l'usuari després de sol·licitar permisos
     */
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
     * Obté l'última ubicació coneguda del dispositiu
     */
    private void obtenirUltimaUbicacio() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();

                    // Actualitza els TextView
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
     * Consulta l'API meteorològica utilitzant OkHttp3
     */
    private void consultarAPIMeteorologica(double latitud, double longitud) {
        // Construeix l'URL per a la consulta
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitud + "&lon=" + longitud + "&appid=" + API_KEY + "&units=metric";

        // Crea el client OkHttp
        OkHttpClient client = new OkHttpClient();

        // Crea la sol·licitud HTTP
        Request request = new Request.Builder().url(url).build();

        // Envia la sol·licitud en segon pla
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

                    // Actualitza el TextView amb la resposta de l'API
                    runOnUiThread(() -> tvApiResponse.setText("Resposta de l'API: " + resposta));
                } else {
                    Log.e("API", "Resposta no vàlida. Codi: " + response.code());
                    runOnUiThread(() -> tvApiResponse.setText("Error: Codi de resposta " + response.code()));
                }
            }
        });
    }
}
