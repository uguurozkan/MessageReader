/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

/**
 * Created by Uğur Özkan on 5/29/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class ListenerService extends Service implements RecognitionListener {

    private SpeechRecognizer speechRecognizer;
    private Intent recognitionIntent;

    String TAG = "TAGAT";

    @Override
    public void onCreate() {
        super.onCreate();
        speechRecognizer = getSpeechRecognizer();
    }

    // Lazy initialization
    private SpeechRecognizer getSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(this);
        }
        return speechRecognizer;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        initRecognitionIntent();
        listen();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initRecognitionIntent() {
        recognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
    }

    public void listen() {
        getSpeechRecognizer().startListening(recognitionIntent);
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "error " + error);

        if ((error == SpeechRecognizer.ERROR_NO_MATCH) ||
                (error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT) ||
                (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
            listen();
        }
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "Listener service onResults ");
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
        }
        speechRecognizer = null;
        super.onDestroy();
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
