/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Uğur Özkan on 5/31/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class SenderService extends ListenerService {

    private String address;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        address = intent.getStringExtra("address");

        Intent listenIntent = new Intent(getApplicationContext(), super.getClass());
        return super.onStartCommand(listenIntent, flags, startId);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "SenderService onResults ");
        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            sendSms(heard.get(0));
        }
        super.onResults(results);
        stopSelf();
    }

    private void sendSms(String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(address, null, message, null, null);
    }
}
