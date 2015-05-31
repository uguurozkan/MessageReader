/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Uğur Özkan on 5/29/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class CommandListenerService extends Service implements RecognitionListener {

    private SpeechRecognizer speechRecognizer;
    private Intent recognitionIntent;
    private String messageBody;
    private String address;

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
        messageBody = intent.getStringExtra("messageBody");
        address = intent.getStringExtra("address");
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

    private void listen() {
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
        Log.d(TAG, "onResults ");
        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            ArrayList<VoiceCommand> voiceCommands = parseCommands(heard);
            for (VoiceCommand command : voiceCommands) {
                executeCommand(command);
            }

            if (voiceCommands.isEmpty()) {
                listen();
            } else {
                stopSelf();
            }
        }
    }

    private ArrayList<VoiceCommand> parseCommands(List<String> heard) {
        Log.d(TAG, "parseCommands ");
        ArrayList<VoiceCommand> voiceCommands = new ArrayList<>();
        for (String said : heard) {
            Log.d(TAG, "parseCommands " + said);
            for (VoiceCommand command : VoiceCommand.values()) {
                if (said.toLowerCase().contains(command.name().toLowerCase())) {
                    voiceCommands.add(command.getCommand());
                }
            }
        }
        HashSet<VoiceCommand> tempHS = new HashSet<>();
        tempHS.addAll(voiceCommands);
        voiceCommands.clear();
        voiceCommands.addAll(tempHS);
        Collections.sort(voiceCommands);
        return voiceCommands;
    }

    private void executeCommand(VoiceCommand command) {
        Log.d(TAG, command.name());
        switch (command) {
            case IGNORE:
                markAsRead();
                break;
            case READ:
                readMessage();
            case REPLY:
                sendMessage();
                break;
            case DELETE:
                deleteMessage();
                break;
            default:
                Log.d(TAG, command.name());
                break;
        }
    }

    private void markAsRead() {
        try {
//            ContentValues values = new ContentValues();
//            values.put("read", true);
//            getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + messageId, null);
//
//            Log.d(TAG, "markAsREad.");
        } catch (Exception e) {
            Log.d(TAG, "no message found.");
        }
    }

    private void readMessage() {
        Intent messageNotifierService = new Intent(this, Reader.class);
        messageNotifierService.putExtra("speech", messageBody);
        this.startService(messageNotifierService);
    }

    private void sendMessage() {
    }

    private void deleteMessage() {
        Uri uriSms = Uri.parse("content://sms");
        String[] messageParams = findMessage();
        if (messageParams != null) {
            this.getContentResolver().delete(uriSms, "thread_id=? and _id=?", new String[]{messageParams[0], messageParams[1]});
        }
    }

    private String[] findMessage() {
        Uri uriSms = Uri.parse("content://sms");
        Cursor cursor = this.getContentResolver().query(uriSms, new String[]{"_id", "thread_id", "address", "person", "date", "body"}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (messageBody.equals(cursor.getString(5)) && address.equals(cursor.getString(2))) {
                    return new String[]{String.valueOf(cursor.getString(0)), String.valueOf(cursor.getString(1))};
                }
            } while (cursor.moveToNext());
        }
        return null;
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
