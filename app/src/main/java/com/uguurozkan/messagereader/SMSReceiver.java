/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;

/**
 * Created by Uğur Özkan on 5/27/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startMessageNotifierService(context, intent);
    }

    private void startMessageNotifierService(Context context, Intent intent) {
        Intent messageNotifierService = new Intent(context, SmsNotifierService.class);
        messageNotifierService.putExtra("address", getSenderNum(intent.getExtras()));
        messageNotifierService.putExtra("fromWhom", getSenderName(context, intent.getExtras()));
        messageNotifierService.putExtra("messageBody", getMessageBody(intent.getExtras()));
        context.startService(messageNotifierService);
        //abortBroadcast();
    }

    /**
     * Parse contact name from the number.
     *
     * @param context
     * @param intentExtras The Bundle that contains pdus.
     * @return contact name if there is or sender number otherwise.
     */
    private String getSenderName(Context context, Bundle intentExtras) {
        String contactNum = getSenderNum(intentExtras);
        String contact = contactNum; // just to be sure

        Uri lookupURI = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.decode(contactNum));
        Cursor cursor = context.getContentResolver().query(lookupURI, new String[]{ContactsContract.Data.DISPLAY_NAME}, null, null, null);

        try {
            cursor.moveToFirst();
            contact = cursor.getString(0);
        } catch (Exception e) {
            contact = contactNum;
        } finally {
            cursor.close();
        }

        return contact;
    }

    /**
     * Gets the originating address of the message. It can be e-mail addresses as well
     *
     * @param intentExtras The Bundle that contains pdus.
     * @return null or address.
     */
    private String getSenderNum(Bundle intentExtras) {
        if (intentExtras == null)
            return null;

        String number = "";
        final Object[] pdus = (Object[]) intentExtras.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            number += currentMessage.getDisplayOriginatingAddress();
        }
        return number;
    }

    /**
     * Gets the message.
     *
     * @param intentExtras The Bundle that contains pdus.
     * @return null or message.
     */
    private String getMessageBody(Bundle intentExtras) {
        if (intentExtras == null)
            return null;

        String message = "";
        final Object[] pdus = (Object[]) intentExtras.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            message += currentMessage.getDisplayMessageBody();
        }

        return message;
    }

}
