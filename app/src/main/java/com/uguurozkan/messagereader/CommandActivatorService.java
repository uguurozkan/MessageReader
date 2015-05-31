/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Uğur Özkan on 5/31/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class CommandActivatorService extends ListenerService {

    private String messageBody, address;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        messageBody = intent.getStringExtra("messageBody");
        address = intent.getStringExtra("address");

        Intent listenIntent = new Intent(getApplicationContext(), super.getClass());
        return super.onStartCommand(listenIntent, flags, startId);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "Command activator onResults ");
        super.stopListening();
        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            ArrayList<VoiceCommand> voiceCommands = parseCommands(heard);
            for (VoiceCommand command : voiceCommands) {
                executeCommand(command);
            }

            if (voiceCommands.isEmpty()) {
                super.listen();
            } else {
                super.onResults(results);
                stopSelf();
            }
        }
    }

    private ArrayList<VoiceCommand> parseCommands(List<String> heard) {
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
                markMessageAsRead();
                break;
            case READ:
                readMessage();
                break;
            case REPLY:
                sendNewMessage();
                break;
            case DELETE:
                deleteMessage();
                break;
            default:
                Log.d(TAG, command.name());
                break;
        }
    }

    private void markMessageAsRead() {
        String[] messageParams = findMessage();

        if (messageParams != null) {
            ContentValues values = new ContentValues();
            values.put("read", true);
            getContentResolver().update(Uri.parse("content://sms"), values, "_id=? and thread_id=?", messageParams);
            Log.d(TAG, "marked As Read.");
        }
    }

    private void readMessage() {
        Intent messageReaderService = new Intent(this, ReaderService.class);
        messageReaderService.putExtra("speech", messageBody);
        startService(messageReaderService);
    }

    private void sendNewMessage() {
        startSenderService();
    }

    private void startSenderService() {
        Intent messageSenderService = new Intent(getApplicationContext(), SenderService.class);
        messageSenderService.putExtra("address", address);
        startService(messageSenderService);
    }

    private void deleteMessage() {
        String[] messageParams = findMessage();
        if (messageParams != null) {
            getContentResolver().delete(Uri.parse("content://sms"), "_id=? and thread_id=?", messageParams);
            Log.d(TAG, "message deleted");
        }
    }

    private String[] findMessage() {
        Cursor cursor = this.getContentResolver().query(Uri.parse("content://sms"),
                new String[]{"_id", "thread_id", "address", "person", "date", "body"}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (messageBody.equals(cursor.getString(5)) && address.equals(cursor.getString(2))) {
                    return new String[]{String.valueOf(cursor.getString(0)), String.valueOf(cursor.getString(1))};
                }
            } while (cursor.moveToNext());
        }
        return null;
    }

}
