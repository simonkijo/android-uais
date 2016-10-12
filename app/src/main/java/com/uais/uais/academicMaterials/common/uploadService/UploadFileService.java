package com.uais.uais.academicMaterials.common.uploadService;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.uais.uais.R;
import com.uais.uais.academicMaterials.AcademicActivity;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;


public class UploadFileService extends IntentService {

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;

    public static final String ACTION_COMPLETE = "com.uais.uais.academicMaterials.common.uploadService.action.COMPLETE";

    public UploadFileService(){
        super("UploadFileService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.getIntExtra("progressing",0) == 1) { //in progress
            showNotification(intent,"Upload in progress : "+intent.getIntExtra("progress_", 0)+"%",100,true,1);
        }else if(intent.getIntExtra("progressing",0) == 0){ //upload complete
            showNotification(intent,"Upload Complete : 100%",0,false,0);
        }else if(intent.getIntExtra("progressing",0) == 2){ //upload fail
            showNotification(intent,"Upload Fail",0,false,0);
        }
    }
    public void showNotification(Intent intent,String description,int max, Boolean ongoingflag, int resID){

        Intent resultIntent = new Intent(getApplicationContext(), AcademicActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(AcademicActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        // Set Vibrate
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_VIBRATE;

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(intent.getStringExtra("filename"))
                .setSmallIcon((resID == 1 ? android.R.drawable.stat_sys_upload : R.mipmap.ic_file_upload_white_18dp))
                .setTicker(getString(R.string.notification_ticker))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setContentText(description)
                .setProgress(max, intent.getIntExtra("progress_", 0), false) //set progressbar
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)  //set long running on background
                .setOnlyAlertOnce(true)  //set vibrate once
                .setOngoing(ongoingflag)  //ensure not canceled by close button
                .setAutoCancel(true)  //when clicked notification is removed in notification bar
                .setDefaults(defaults);  //set vibration

        notificationBuilder.setContentIntent(resultPendingIntent); // opens activity contain this service if notification clicked
        notificationManager.notify(1, notificationBuilder.build());

    }
}
