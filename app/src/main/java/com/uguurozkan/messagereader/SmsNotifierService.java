/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

import android.content.Intent;

/**
 * Created by Uğur Özkan on 5/28/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class SmsNotifierService extends ReaderService {

    private final String SPEECH = "You have a new message from ";
    private String address, fromWhom, messageBody;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        messageBody = intent.getStringExtra("messageBody");
        address = intent.getStringExtra("address");
        fromWhom = intent.getStringExtra("fromWhom");

        Intent readIntent = new Intent(getApplicationContext(), super.getClass());
        readIntent.putExtra("speech", SPEECH + fromWhom);
        return super.onStartCommand(readIntent, flags, startId);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        startCommandActivatorService();
        super.onUtteranceCompleted(utteranceId);
        stopSelf();
    }

    private void startCommandActivatorService() {
        Intent commandActivatorService = new Intent(getApplicationContext(), CommandActivatorService.class);
        commandActivatorService.putExtra("messageBody", messageBody);
        commandActivatorService.putExtra("address", address);
        startService(commandActivatorService);
    }

}
