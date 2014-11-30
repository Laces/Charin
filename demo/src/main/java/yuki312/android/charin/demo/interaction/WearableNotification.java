package yuki312.android.charin.demo.interaction;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import yuki312.android.charin.demo.R;


public class WearableNotification {

    public void notify(Context context) {
        int notificationId = 001;
        // Build intent for notification content
        Intent interactionIntent = new Intent(context, InteractionService.class);
        interactionIntent.setAction(InteractionService.ACTION_REMOTE_INPUT_PAID);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_call))
                        .setContentTitle("Test Title")
                        .setContentText("Content Text")
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setSubText("sub text");

        String[] replyChoices = new String[]{"100", "200", "300"};
        RemoteInput remoteInput = new RemoteInput.Builder(InteractionService.EXTRA_PAID_MONEY)
                .setLabel("Label")
                .setChoices(replyChoices)
                .build();

        PendingIntent remindPendingIntent =
                PendingIntent.getService(context, 0, interactionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_plusone_medium_off_client, "title", remindPendingIntent)
                        .addRemoteInput(remoteInput).build();

        notificationBuilder.extend(
                new NotificationCompat.WearableExtender()
                        .addAction(action)
                        .setContentIcon(android.R.drawable.ic_media_play)
                        .setHintHideIcon(true));

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

}
