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

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // Variables globales
    private FusedLocationProviderClient fusedLocationClient; // Cliente para obtener la ubicación
    private final String API_KEY = "effd8c6d8cf06ebdebacda1bf50a6b0f"; // Sustituye por tu API key de OpenWeatherMap

    // TextViews para mostrar datos
    private TextView tvLatitude, tvLongitude, tvWeatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar los TextViews
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvWeatherInfo = findViewById(R.id.tvWeatherInfo);

        // Inicializar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Solicitar permisos de ubicación
        solicitarPermisosUbicacion();
    }

    /**
     * Solicitar permisos de ubicación
     */
    private void solicitarPermisosUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            obtenerUltimaUbicacion();
        }
    }

    /**
     * Manejar respuesta del usuario al solicitar permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUltimaUbicacion();
        } else {
            Log.e("Permisos", "Permiso de ubicación denegado.");
        }
    }

    /**
     * Obtener la última ubicación conocida del dispositivo
     */
    private void obtenerUltimaUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();

                    // Actualizar los TextViews con la ubicación
                    tvLatitude.setText("Latitud: " + latitud);
                    tvLongitude.setText("Longitud: " + longitud);

                    // Consultar API con la ubicación obtenida
                    consultarAPIMeteorologica(latitud, longitud);
                } else {
                    Log.e("Ubicacion", "No se pudo obtener la ubicación.");
                }
            });
        }
    }

    /**
     * Consultar la API de OpenWeatherMap con idioma y unidades configuradas automáticamente
     */
    private void consultarAPIMeteorologica(double latitud, double longitud) {
        // Detectar idioma y unidades según la configuración del dispositivo
        String idioma = Locale.getDefault().getLanguage(); // Idioma del sistema
        String pais = Locale.getDefault().getCountry(); // País del sistema
        String unidades = pais.equals("US") ? "imperial" : "metric"; // Métricas: imperial para EE.UU., metric para el resto

        // Construir la URL de la API
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitud +
                "&lon=" + longitud +
                "&appid=" + API_KEY +
                "&units=" + unidades +
                "&lang=" + idioma;

        // Configurar cliente OkHttp
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        // Enviar solicitud a la API
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API", "Error en la consulta: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String respuesta = response.body().string();

                    // Procesar y mostrar la respuesta
                    runOnUiThread(() -> {
                        try {
                            // Mostrar datos crudos por ahora
                            tvWeatherInfo.setText("Respuesta de la API:\n" + respuesta);
                        } catch (Exception e) {
                            tvWeatherInfo.setText("Error al procesar la respuesta.");
                            Log.e("JSON", "Error al procesar JSON", e);
                        }
                    });
                } else {
                    runOnUiThread(() -> tvWeatherInfo.setText("Error: Código de respuesta " + response.code()));
                }
            }
        });
    }
}
