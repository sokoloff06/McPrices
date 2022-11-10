//package com.google.firebaseengage
//
//import android.content.ComponentName
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import androidx.browser.customtabs.*
//import androidx.test.core.app.ActivityScenario
//import com.google.firebaseengage.MainActivity.Companion.LOG_TAG
//import org.junit.Test
//import java.util.concurrent.CountDownLatch
//import java.util.concurrent.TimeUnit
//
//
//class WebVisitor {
//    @Test
//    fun run() {
//        val pageLoaded = CountDownLatch(1)
//        ActivityScenario.launch(MainActivity::class.java).onActivity {
//            var session : CustomTabsSession?
//            val navigationCallback = object : CustomTabsCallback() {
//                @Override
//                override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
//                    super.onNavigationEvent(navigationEvent, extras)
//                    Log.d(LOG_TAG, "Navigation event: $navigationEvent")
//                    if (navigationEvent == NAVIGATION_FINISHED) {
//                        pageLoaded.countDown()
//                    }
//                }
//            }
//            val ctsConnection = object : CustomTabsServiceConnection() {
//                override fun onServiceDisconnected(name: ComponentName?) {}
//                override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
//                    session = client.newSession(navigationCallback)
//                    client.warmup(0)
//
//                    val url = "https://sokolovv-3c318.web.app/"
//                    val builder = CustomTabsIntent.Builder(session)
//                    val customTabsIntent = builder.build()
//                    customTabsIntent.launchUrl(it, Uri.parse(url))
//                }
//            }
//            val customTabsProvider = CustomTabsClient.getPackageName(it, null)
//            CustomTabsClient.bindCustomTabsService(it, customTabsProvider, ctsConnection);
//            Log.d(LOG_TAG, "Finish")
//        }
//        pageLoaded.await(20000, TimeUnit.MILLISECONDS)
//    }
//}