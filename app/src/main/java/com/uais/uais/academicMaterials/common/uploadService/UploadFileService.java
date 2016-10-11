package com.uais.uais.academicMaterials.common.uploadService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.uais.uais.R;
import com.uais.uais.academicMaterials.AcademicActivity;
import com.uais.uais.academicMaterials.AcademicFragment;


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
            showNotification(intent,"File Upload","Upload in progress",100);
        }else if(intent.getIntExtra("progressing",0) == 0){ //upload complete
            showNotification(intent,"File Upload","Upload Complete",0);
        }else if(intent.getIntExtra("progressing",0) == 2){ //upload fail
            showNotification(intent,"File Upload","Upload Fail",0);
        }
    }
    public void showNotification(Intent intent,String title,String description,int max){

        Intent resultIntent = new Intent(getApplicationContext(), AcademicActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(AcademicActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_file_upload_white_18dp)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setContentText(description)
                .setProgress(max, intent.getIntExtra("progress_", 0), false);

        notificationBuilder.setContentIntent(resultPendingIntent); // opens activity contain this service if notification clicked
        notificationManager.notify(1, notificationBuilder.build());
    }
}
