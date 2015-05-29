/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by Uğur Özkan on 5/28/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class NewMessageNotifierService extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    private final String SPEECH = "You have a new message from ";
    private TextToSpeech incomingMessageSpeech;

    @Override
    public void onCreate() {
        incomingMessageSpeech = new TextToSpeech(this, this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (incomingMessageSpeech != null) {
            incomingMessageSpeech.stop();
            incomingMessageSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //System.out.println(incomingMessageSpeech.isLanguageAvailable(Locale.UK));
            incomingMessageSpeech.setLanguage(Locale.US);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String senderNum = intent.getStringExtra("senderNum");
        incomingMessageSpeech.speak(SPEECH + senderNum, TextToSpeech.QUEUE_ADD, null);
        return super.onStartCommand(intent, flags, startId);
        //return START_NOT_STICKY;
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        stopSelf();
    }
}
