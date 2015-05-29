/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Uğur Özkan on 5/29/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class CommandListenerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
