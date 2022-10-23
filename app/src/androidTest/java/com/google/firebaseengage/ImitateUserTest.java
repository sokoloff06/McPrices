package com.google.firebaseengage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class ImitateUserTest {

    @Test
    public void main() throws InterruptedException {
        // Given
        Context ctx = ApplicationProvider.getApplicationContext();
        System.out.println("package name = " + ctx.getPackageName());

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(ctx);
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        CountDownLatch rcFetchLatch = new CountDownLatch(1);
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    Log.d("ENGAGE-DEBUG", "Remote Control fetched and active");
                    rcFetchLatch.countDown();
                })
                .addOnFailureListener(exception -> Log.d("ENGAGE-DEBUG",
                        "Remote Control FAILED to be fetched: " + exception.getLocalizedMessage()));

        rcFetchLatch.await(5000, TimeUnit.MILLISECONDS);
        final CountDownLatch appIdLatch = new CountDownLatch(1);
        analytics.getAppInstanceId().addOnCompleteListener(task -> {
            System.out.println("App Instance ID = " + task.getResult());
            appIdLatch.countDown();
        });
        String bgColor = remoteConfig.getString(MainActivity.BG_COLOR_KEY).toUpperCase();
        String fId = analytics.getFirebaseInstanceId();
        System.out.println("Firebase Instance ID = " + fId);
        System.out.println("bgColor = " + bgColor);
        if (bgColor.equals("#E0F7FA")) {
            logEvent(fId, bgColor, analytics);
        } else if (bgColor.equals("#FFEBEE") || bgColor.equals("#FFFFFF")) {
            double random = Math.random();
            if (random <= 0.5) {
                logEvent(fId, bgColor, analytics);
            }
        }
        appIdLatch.await(5000, TimeUnit.MILLISECONDS);
        //10-22 13:58:40.150 32633 32664 I System.out: bgColor = #ffffff
        //10-22 13:58:40.152 32633 32664 I System.out: Firebase Instance ID = dRa1TvjHRzqQrO8EEhZedP
        //10-22 13:58:40.156 32633 32633 I System.out: App Instance ID = 86d96a633eb26b514c3d5514e5ea8c16
    }

    private void logEvent(String transactionId, String color, FirebaseAnalytics analytics) {
        System.out.println("Logging event");
        Bundle eventParams = new Bundle();
        eventParams.putString(FirebaseAnalytics.Param.CURRENCY, "EUR");
        eventParams.putDouble(FirebaseAnalytics.Param.VALUE, 10);
        eventParams.putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId);
        eventParams.putString(FirebaseAnalytics.Param.AFFILIATION, color);
        analytics.logEvent(FirebaseAnalytics.Event.PURCHASE, eventParams);
    }
}
