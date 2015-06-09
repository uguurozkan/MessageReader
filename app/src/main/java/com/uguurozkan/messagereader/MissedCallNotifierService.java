/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

/**
 * Created by Uğur Özkan on 6/1/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class MissedCallNotifierService extends ReaderService {

    private void startPendingActivity() {
        try {
            Intent intent = new Intent(this.getApplicationContext(), MissedCallNotifierReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1253, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stopSelf();
        }
    }
}
