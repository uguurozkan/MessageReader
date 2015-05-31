/*
 * Copyright (c) 2015.
 * This code belongs to Uğur Özkan
 * ugur.ozkan@ozu.edu.tr
 */

package com.uguurozkan.messagereader;

/**
 * Created by Uğur Özkan on 5/30/2015.
 * <p/>
 * ugur.ozkan@ozu.edu.tr
 */
public enum VoiceCommand {
    IGNORE(100), SALLA(101, IGNORE),
    READ(200), OKU(201, READ),
    REPLY(300), CEVAPLA(301, REPLY),
    DELETE(400), REMOVE(401, DELETE), SIL(402, DELETE),
    SEND(500);

    private int priority;
    private VoiceCommand[] keywords;

    VoiceCommand(int priority, VoiceCommand... keywords) {
        this.priority = priority;
        this.keywords = keywords;
    }

    public VoiceCommand getCommand() {
        if (this.keywords.length == 0) {
            return this;
        } else {
            return this.keywords[0];
        }
    }

    public int getPriority() {
        return this.priority;
    }

}
