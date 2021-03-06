/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deso.welcome;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.os.Handler;
import android.util.Log;

public class WBReceiver extends BroadcastReceiver {
    private static final String TAG = "WelcomeBackBootReceiver";
    private static final String WB_TOGGLE = "wb_toggle";
    private static final String WB_DURATION = "wb_duration";

    private boolean mShowWB;
    private int mWBDuration;

    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedPreferences pref = context.getSharedPreferences("WBPrefs", Context.MODE_PRIVATE);
        ContentResolver res = context.getContentResolver();
        mShowWB = pref.getBoolean(WB_TOGGLE, true);
        mWBDuration = Integer.parseInt(pref.getString(WB_DURATION, "12000"));
        if (mShowWB){
            FirstBootNotify(context, mWBDuration);
            Log.i(TAG, "Notified boot: Duration set for "+mWBDuration);
        } else {
            Log.i(TAG, "Notification Disabled");
        }
    }

    public void FirstBootNotify(Context context, int duration) {
        long durval = (long) duration;
        Handler h = new Handler();
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_status_bar_deso_logo)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText("")
		.setStyle(new Notification.InboxStyle()
		.setBigContentTitle(context.getString(R.string.notification_title))
		.addLine("Build status: "+SystemProperties.get("ro.deso.buildtype"))
		.addLine("Build date: "+SystemProperties.get("ro.build.date"))
		.addLine("Device: "+SystemProperties.get("ro.product.device")));
		final NotificationManager mNotificationManager =
			(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
		h.postDelayed(new Runnable() {
			public void run() {
				mNotificationManager.cancel(1);
			}
		}, durval);
    }
}
