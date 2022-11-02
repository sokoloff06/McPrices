package com.google.firebaseengage.firebase;

import static com.google.firebaseengage.MainActivity.LOG_TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tagmanager.CustomTagProvider;

import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

// Personalization assignment happens when we read value of the personalized property from Remote Config, not when RC is fetched
public class PersonalizationAssignmentObserver implements CustomTagProvider {
    public static CountDownLatch latch = new CountDownLatch(1);
    public static Runnable listener = null;

    @Override
    public void execute(@NonNull Map<String, Object> map) {
        Log.d(LOG_TAG, new JSONObject(map).toString());
        if (Objects.equals(map.get("group"), "P13N")) {
            Log.d(LOG_TAG, "PERSONALIZED_USER");
            if (listener != null) {
                listener.run();
            }
            latch.countDown();
        } else {
            Log.d(LOG_TAG, "BASELINE_USER");
        }
    }
}
