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
public class NotifierService extends Reader {

    private final String SPEECH = "You have a new message from ";
    private String fromWhom, messageBody;
    private String address;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fromWhom = intent.getStringExtra("fromWhom");
        messageBody = intent.getStringExtra("messageBody");
        address = intent.getStringExtra("address");
        Intent readIntent = new Intent(getApplicationContext(), super.getClass());
        readIntent.putExtra("speech", SPEECH + fromWhom);
        return super.onStartCommand(readIntent, flags, startId);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        startCommandListenerService();
        super.onUtteranceCompleted(utteranceId);
        stopSelf();
    }

    private void startCommandListenerService() {
        Intent commandListenerService = new Intent(getApplicationContext(), CommandListenerService.class);
        commandListenerService.putExtra("messageBody", messageBody);
        commandListenerService.putExtra("address", address);
        getApplicationContext().startService(commandListenerService);
    }
}
