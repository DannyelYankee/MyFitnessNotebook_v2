package com.example.myfitnessnotebook_v1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {
    public ServicioFirebase() {
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String titulo = remoteMessage.getNotification().getTitle();
            String texto = remoteMessage.getNotification().getBody();
            final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "HEADS_UP_NOTIFICATION", NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notificacion = new Notification.Builder(this, CHANNEL_ID);
            notificacion.setContentTitle(titulo);
            notificacion.setContentText(texto);
            notificacion.setSmallIcon(R.drawable.ic_launcher_foreground);
            notificacion.setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(1, notificacion.build());
            System.out.println("mensaje recibido");
        }
    }

    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
    }
}
