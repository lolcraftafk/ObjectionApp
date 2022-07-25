package com.example.objection1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BackgroundService extends Service {
    private Intent main;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}