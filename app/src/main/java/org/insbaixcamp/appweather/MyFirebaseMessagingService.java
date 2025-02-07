package org.insbaixcamp.appweather;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "WeatherChannel";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Token FCM recibido: " + token);

        // Enviar el token al servidor (si lo deseas) junto con la ciudad del usuario
        // para notificaciones segmentadas (opcional).
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implementar lógica de envío del token al servidor,
        // junto con la ciudad preferida/ubicación del usuario
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Si el mensaje contiene datos
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Mensaje de datos: " + remoteMessage.getData());
            // Aquí puedes gestionar datos específicos
        }

        // Si el mensaje contiene notificación
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notificación: " + title + " - " + body);

            // Mostrar la notificación en el dispositivo
            showNotification(title, body);
        }
    }

    // Método para crear el canal de notificación y mostrarla
    private void showNotification(String title, String body) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Creación del canal (solo necesario para Android O y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Weather Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para alertas meteorológicas");
            notificationManager.createNotificationChannel(channel);
        }

        // Construcción de la notificación
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.ic_menu_compass) // Cambia el ícono si lo deseas
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // Publicación de la notificación
        notificationManager.notify(1, notification);
    }
}
