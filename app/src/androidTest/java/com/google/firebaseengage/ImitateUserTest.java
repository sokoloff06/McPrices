package com.google.firebaseengage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebaseengage.cart.CartFragment;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class ImitateUserTest {

    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    @Test
    public void generatePurchasesBasedOnRcValues() throws InterruptedException {
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
        String firebaseInstanceId = analytics.getFirebaseInstanceId();
        System.out.println("Firebase Instance ID = " + firebaseInstanceId);
        //TODO: create function logIf(param_name, param_values)
        // A/B Testing (bgColor) results logic
        String bgColor = remoteConfig.getString(MainActivity.BG_COLOR_KEY).toUpperCase();
        System.out.println("bgColor = " + bgColor);
        if (bgColor.equals("#E0F7FA")) {
            logEvent(bgColor, analytics);
        } else {
            double random = Math.random();
            if (random <= 0.5) {
                logEvent(bgColor, analytics);
            }
        }

        // Personalization (btn_buy_color) results logic
        String btnColor = remoteConfig.getString(CartFragment.PURCHASE_BTN_COLOR).toUpperCase();
        System.out.println(CartFragment.PURCHASE_BTN_COLOR + " = " + btnColor);
         if (btnColor.equals("#2D77E3") || btnColor.equals("#633BAD")) {
            double random = Math.random();
            if (random <= 0.5) {
                logEvent(btnColor, analytics);
            }
        }
        appIdLatch.await(5000, TimeUnit.MILLISECONDS);
    }

    private void logEvent(String bgColor, FirebaseAnalytics analytics) {
        String btnColor = remoteConfig.getString(CartFragment.PURCHASE_BTN_COLOR).toUpperCase();

        Bundle eventParams = new Bundle();
        eventParams.putString(FirebaseAnalytics.Param.CURRENCY, "EUR");
        eventParams.putDouble(FirebaseAnalytics.Param.VALUE, 10);
        eventParams.putString(FirebaseAnalytics.Param.TRANSACTION_ID, analytics.getFirebaseInstanceId());
        eventParams.putString(FirebaseAnalytics.Param.AFFILIATION, bgColor);
        eventParams.putString(FirebaseAnalytics.Param.COUPON, btnColor);
        System.out.println("Logging event");
        analytics.logEvent(FirebaseAnalytics.Event.PURCHASE, eventParams);
    }
}
