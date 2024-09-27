package com.example.detectionimmobility;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;

public class BeepHelper
{
    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

    public void beep(int duration) {
        toneG.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE, duration);
    }
}
