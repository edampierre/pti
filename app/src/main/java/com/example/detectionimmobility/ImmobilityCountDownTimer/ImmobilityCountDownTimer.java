package com.example.detectionimmobility.ImmobilityCountDownTimer;

import android.os.CountDownTimer;

public class ImmobilityCountDownTimer extends CountDownTimer {

    private ImmobilityCountDownTimerCallback immobilityCountDownTimerCallback;

    public ImmobilityCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public void registerOnFinishCallback(ImmobilityCountDownTimerCallback immobilityCountDownTimerCallback) {
        this.immobilityCountDownTimerCallback = immobilityCountDownTimerCallback;
    }

    @Override
    public void onTick(long l) {

    }

    @Override
    public void onFinish() {
        if(this.immobilityCountDownTimerCallback != null) this.immobilityCountDownTimerCallback.onCountDownFinish();
    }
}
