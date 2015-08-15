package com.cryonicsinstitute;

import io.fabric.sdk.android.Fabric;
import util.AlarmSounds;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.cryonicsinstitute.prefs.Prefs;
import com.crashlytics.android.Crashlytics;

import java.util.GregorianCalendar;

public class CryonicsCheckinApp extends Application {
	public static CryonicsCheckinApp app;


	@Override
	public void onCreate() {
		super.onCreate();

		if(!BuildConfig.DEBUG) {
			Fabric.with(this, new Crashlytics());
		}

		app = this;
		Prefs.init(this);
		AlarmSounds.init(this);

        // set up alarm if it isn't already running
        AlarmActivity.setNextAlarm(this);
	}

    /**
     * Start the countdown to sending friends/family a help message
     * Defaults to 20 mins
     */
	public static void startCountdownTimer() {
		// The time at which the alarm will be scheduled. Here the alarm is scheduled for 1 day from the current time.
		// We fetch the current time in milliseconds and add 1 day's time
		// i.e. 24*60*60*1000 = 86,400,000 milliseconds in a day.
		Long time = new GregorianCalendar().getTimeInMillis() + 20*60*1000;

		// Create an Intent and set the class that will execute when the Alarm triggers. Here we have
		// specified SMSAlertReceiver in the Intent. The onReceive() method of this class will execute when the broadcast from your alarm is received.
		Intent intentAlarm = new Intent(CryonicsCheckinApp.app, SMSAlertReceiver.class);

		// Get the Alarm Service.
		AlarmManager alarmManager = (AlarmManager) CryonicsCheckinApp.app.getSystemService(Context.ALARM_SERVICE);

		// Set the alarm for a particular time.
		PendingIntent pendingIntent = PendingIntent.getBroadcast(CryonicsCheckinApp.app, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
	}

    /**
     * Cancel the countdown to sending friends/family a help message
     */
	public static void cancelCountdownTimer() {
        Prefs.setSentSMSCount(0);

		// Cancel alarm
		Intent intentAlarm = new Intent(CryonicsCheckinApp.app, SMSAlertReceiver.class);
		AlarmManager alarmManager = (AlarmManager) CryonicsCheckinApp.app.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(CryonicsCheckinApp.app, 1, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
	}
}
