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
public class Reader extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    private TextToSpeech incomingMessageTTS;
    private String speech;

    @Override
    public void onCreate() {
        super.onCreate();
        incomingMessageTTS = getTextToSpeech();
    }

    // Lazy initialization
    private TextToSpeech getTextToSpeech() {
        if (incomingMessageTTS == null) {
            incomingMessageTTS = new TextToSpeech(this, this);
        }
        return incomingMessageTTS;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        speech = intent.getStringExtra("speech");
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
        incomingMessageTTS.setLanguage(Locale.US);
        incomingMessageTTS.setSpeechRate(0.8f);
        incomingMessageTTS.setOnUtteranceCompletedListener(this);
    }

    private void speak() {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "uniqueId");
        incomingMessageTTS.speak(speech, TextToSpeech.QUEUE_ADD, map);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (incomingMessageTTS != null) {
            incomingMessageTTS.stop();
            incomingMessageTTS.shutdown();
        }
        incomingMessageTTS = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
