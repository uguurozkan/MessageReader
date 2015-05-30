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

import java.util.List;

/**
 * Created by Uğur Özkan on 5/29/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class CommandListenerService extends Service implements RecognitionListener {

    private SpeechRecognizer speechRecognizer;
    private Intent recognitionIntent;

    String TAG = "TAGAT";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        init();
        super.onCreate();
    }

    private void init() {
        Log.d(TAG, "init");
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
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
        }
        speechRecognizer = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onstartCommand");
        recognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechRecognizer.startListening(recognitionIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
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
        Log.d(TAG, "onEndofSpeech");
        //stopSelf();
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "error " + error);

        if ((error == SpeechRecognizer.ERROR_NO_MATCH) || (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
            Log.d(TAG, "didn't recognize anything");
            // keep going
            getSpeechRecognizer().startListening(recognitionIntent);
        }
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults ");
        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (String said : heard) {
                if (said.contains("test")){
                    Log.d(TAG, "heard test");
                }
            }

            for (int i = 0; i<heard.size(); i++) {
                Log.d(TAG, " onResults     " +  heard.get(i));
            }
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent " + eventType);
    }

}
