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

    private ArrayList<VoiceCommand> voiceCommands;
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
        super.stopListening();
        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            parseCommands(heard);
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

    private void parseCommands(List<String> heard) {
        voiceCommands = new ArrayList<>();
        for (String said : heard) {
            for (VoiceCommand command : VoiceCommand.values()) {
                if (said.toLowerCase().contains(command.name().toLowerCase())) {
                    voiceCommands.add(command.getCommand());
                }
            }
        }
        organizeVoiceCommands();
    }

    private void organizeVoiceCommands() {
        HashSet<VoiceCommand> tempHS = new HashSet<>();
        tempHS.addAll(voiceCommands);
        voiceCommands.clear();
        voiceCommands.addAll(tempHS);
        Collections.sort(voiceCommands);
    }

    private void executeCommand(VoiceCommand command) {
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
                break;
        }
    }

    private void markMessageAsRead() {
        String[] messageParams = findMessage();

        if (messageParams != null) {
            ContentValues values = new ContentValues();
            values.put("read", true);
            getContentResolver().update(Uri.parse("content://sms"), values, "_id=? and thread_id=?", messageParams);
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
