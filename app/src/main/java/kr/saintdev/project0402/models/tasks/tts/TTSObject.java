package kr.saintdev.project0402.models.tasks.tts;

import java.io.File;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-15
 */

public class TTSObject {
    private String requestSentence = null;
    private File mp3File = null;

    public TTSObject(String requestSentence, File mp3File) {
        this.requestSentence = requestSentence;
        this.mp3File = mp3File;
    }

    public String getRequestSentence() {
        return requestSentence;
    }

    public File getMp3File() {
        return mp3File;
    }
}
