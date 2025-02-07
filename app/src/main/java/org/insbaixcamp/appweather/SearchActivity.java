package org.insbaixcamp.appweather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    // Variables globals
    private final String API_KEY = "effd8c6d8cf06ebdebacda1bf50a6b0f"; // Sustituye por tu API Key de OpenWeatherMap

    // Elementos de la interfaz
    private EditText etCityName;
    private TextView tvCityName, tvTemperature, tvWeatherDescription;
    private ImageView imgWeatherIcon;
    private Button btnSearch, btnBack;  // <-- AÑADIMOS btnBack

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Inicializar los elementos de la interfaz
        etCityName = findViewById(R.id.etCityName);
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription);
        imgWeatherIcon = findViewById(R.id.imgWeatherIcon);
        btnSearch = findViewById(R.id.btnSearch);

        // Referenciamos el botón "Tornar"
        btnBack = findViewById(R.id.btnBack);

        // Configurar el botón de búsqueda
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = etCityName.getText().toString().trim();
                if (!cityName.isEmpty()) {
                    consultarAPIMeteorologica(cityName);
                }
            }
        });

        // Configurar el botón para volver a MainActivity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Opción 1: si se lanzó SearchActivity desde MainActivity,
                // simplemente volvemos atrás cerrando esta actividad:
                finish();

                // Opción 2 (más explícita): lanzar de nuevo MainActivity:
                // Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                // startActivity(intent);
                // finish();
            }
        });
    }

    /**
     * Consultar la API de OpenWeatherMap según el nombre de la ciudad
     */
    private void consultarAPIMeteorologica(String cityName) {
        // Detectar idioma y unidades según la configuración del dispositivo
        String idioma = Locale.getDefault().getLanguage(); // Idioma del sistema
        String pais = Locale.getDefault().getCountry();    // País del sistema
        String unitats = pais.equalsIgnoreCase("US") ? "imperial" : "metric";

        // Construir la URL de la API
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName +
                "&appid=" + API_KEY +
                "&units=" + unitats +
                "&lang=" + idioma;

        // Configurar cliente OkHttp
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        // Enviar consulta a la API
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API", "Error en la consulta: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resposta = response.body().string();

                    // Procesar y mostrar la respuesta
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(resposta);

                            // Obtener el nombre de la ciudad
                            String cityName = jsonObject.getString("name");

                            // Obtener información del clima (weather)
                            JSONArray weatherArray = jsonObject.getJSONArray("weather");
                            JSONObject weatherObject = weatherArray.getJSONObject(0);
                            String weatherDescription = weatherObject.getString("description");
                            String iconCode = weatherObject.getString("icon");

                            // Obtener la temperatura
                            JSONObject mainObject = jsonObject.getJSONObject("main");
                            double temperature = mainObject.getDouble("temp");

                            // Actualizar los TextViews con la información obtenida
                            tvCityName.setText(cityName);
                            String unit = unitats.equals("imperial") ? "°F" : "°C";
                            tvTemperature.setText(
                                    String.format(Locale.getDefault(), "%.0f%s", temperature, unit)
                            );
                            tvWeatherDescription.setText(weatherDescription);

                            // Cargar el icono del clima
                            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                            cargarIconoClima(iconUrl);

                        } catch (Exception e) {
                            Log.e("JSON", "Error al procesar el JSON", e);
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
     * Método para cargar y mostrar la icona del clima
     */
    private void cargarIconoClima(String iconUrl) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(iconUrl);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                runOnUiThread(() -> imgWeatherIcon.setImageBitmap(bitmap));
            } catch (Exception e) {
                Log.e("Icona", "Error al cargar la icona: " + e.getMessage());
            }
        }).start();
    }
}
