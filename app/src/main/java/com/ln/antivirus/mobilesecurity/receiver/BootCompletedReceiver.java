package com.ln.antivirus.mobilesecurity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ln.antivirus.mobilesecurity.service.MonitorShieldService;
import com.ln.antivirus.mobilesecurity.util.Utils;

public class BootCompletedReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (!Utils.isServiceRunning(context, MonitorShieldService.class)) {
            context.startService(new Intent(context, MonitorShieldService.class));
        }
    }
}
