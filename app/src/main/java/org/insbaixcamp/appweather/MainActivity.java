package org.insbaixcamp.appweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private final String API_KEY = "effd8c6d8cf06ebdebacda1bf50a6b0f"; // Sustituye por tu API Key de OpenWeatherMap

    // Elementos de la interfaz
    private TextView tvCityName, tvTemperature, tvWeatherDescription;
    private ImageView imgWeatherIcon;
    private Button btnSearchCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar los elementos de la interfaz
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription);
        imgWeatherIcon = findViewById(R.id.imgWeatherIcon);
        btnSearchCity = findViewById(R.id.btnSearchCity);

        // Inicializar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Solicitar permisos de ubicación
        solicitarPermisosUbicacion();

        // Configurar el botón para navegar a SearchActivity
        btnSearchCity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Solicitar permisos de ubicación
     */
    private void solicitarPermisosUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            obtenerUltimaUbicacion();
        }
    }

    /**
     * Manejar respuesta del usuario al solicitar permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUltimaUbicacion();
        } else {
            Log.e("Permisos", "Permiso de ubicación denegado.");
        }
    }

    /**
     * Obtener la última ubicación conocida del dispositivo
     */
    private void obtenerUltimaUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();

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
        String pais = Locale.getDefault().getCountry();    // País del sistema
        String unidades = pais.equalsIgnoreCase("US") ? "imperial" : "metric";

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
                            JSONObject jsonObject = new JSONObject(respuesta);

                            // Obtener el nombre de la ciudad
                            String cityName = jsonObject.getString("name");

                            // Obtener información del clima (weather)
                            JSONArray weatherArray = jsonObject.getJSONArray("weather");
                            JSONObject weatherObject = weatherArray.getJSONObject(0);
                            String weatherDescription = weatherObject.getString("description");
                            String iconCode = weatherObject.getString("icon");

                            // Obtener la temperatura (main.temp)
                            JSONObject mainObject = jsonObject.getJSONObject("main");
                            double temperature = mainObject.getDouble("temp");

                            // Actualizar los TextView con la información obtenida
                            tvCityName.setText(cityName);

                            // Mostrar temperatura con unidades correctas
                            String unidadTemperatura = unidades.equals("imperial") ? "°F" : "°C";
                            tvTemperature.setText(
                                    String.format(Locale.getDefault(), "%.0f%s", temperature, unidadTemperatura)
                            );

                            // Primera letra en mayúscula
                            String descCap = weatherDescription.substring(0, 1).toUpperCase()
                                    + weatherDescription.substring(1);
                            tvWeatherDescription.setText(descCap);

                            // Cargar el icono del clima
                            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                            cargarIconoClima(iconUrl);

                            // >>> Suscribir al usuario a un "topic" basado en la ciudad <<<
                            String cityTopic = "city_" + cityName; // p. ej. city_Reus
                            FirebaseMessaging.getInstance().subscribeToTopic(cityTopic)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d("MainActivity", "Suscrito al topic: " + cityTopic);
                                        } else {
                                            Log.d("MainActivity", "Error al suscribirse al topic: " + cityTopic);
                                        }
                                    });

                        } catch (Exception e) {
                            Log.e("JSON", "Error al procesar JSON", e);
                        }
                    });
                } else {
                    runOnUiThread(() ->
                            Log.e("API", "Error: Código de respuesta " + response.code())
                    );
                }
            }
        });
    }

    /**
     * Método para cargar y mostrar el icono del clima
     */
    private void cargarIconoClima(String iconUrl) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(iconUrl);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                runOnUiThread(() -> imgWeatherIcon.setImageBitmap(bitmap));
            } catch (Exception e) {
                Log.e("Icono", "Error al cargar el icono: " + e.getMessage());
            }
        }).start();
    }
}
