@file:JvmName("PromoActivity")
package com.google.firebaseengage

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class PromoActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var btnConversion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)
        firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
        btnConversion = findViewById<Button>(R.id.btn_conversion).apply {
            setOnClickListener {
                // Report purchase using ParamBuilder from ktx
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                    param(FirebaseAnalytics.Param.CURRENCY, "USD")
                    param(FirebaseAnalytics.Param.AFFILIATION, "PromoActivity")
                    param(FirebaseAnalytics.Param.SHIPPING, 0.0)
                    param(FirebaseAnalytics.Param.VALUE, 10.0)
                    param(FirebaseAnalytics.Param.TRANSACTION_ID, "T12345")
                }
            }
        }
    }
}