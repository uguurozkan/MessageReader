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

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Uğur Özkan on 5/28/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class NewMessageNotifierService extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    private final String SPEECH = "You have a new message from ";
    private TextToSpeech incomingMessageSpeech;
    private String senderNum;

    @Override
    public void onCreate() {
        super.onCreate();
        incomingMessageSpeech = getTextToSpeech();
    }

    // Lazy initialization
    private TextToSpeech getTextToSpeech() {
        if (incomingMessageSpeech == null) {
            incomingMessageSpeech = new TextToSpeech(this, this);
        }
        return incomingMessageSpeech;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        senderNum = intent.getStringExtra("senderNum");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            setTtsParams();
            speak();
        }
    }

    private void setTtsParams() {
        incomingMessageSpeech.setLanguage(Locale.US);
        incomingMessageSpeech.setSpeechRate(0.8f);
        incomingMessageSpeech.setOnUtteranceCompletedListener(this);
    }

    private void speak() {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "uniqueId");
        incomingMessageSpeech.speak(SPEECH + senderNum, TextToSpeech.QUEUE_ADD, map);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (incomingMessageSpeech != null) {
            incomingMessageSpeech.stop();
            incomingMessageSpeech.shutdown();
        }
        incomingMessageSpeech = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
