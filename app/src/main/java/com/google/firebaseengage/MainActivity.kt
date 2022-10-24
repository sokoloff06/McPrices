package com.google.firebaseengage


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebaseengage.cart.CartAdapter
import com.google.firebaseengage.cart.CartHandler
import com.google.firebaseengage.entities.Cart

class MainActivity : AppCompatActivity(), CartHandler {
    private lateinit var navigationView: NavigationView
    private var cart = Cart()
    private lateinit var cartAdapter: CartAdapter
    private lateinit var navDrawer: DrawerLayout
    private lateinit var viewPager: ViewPager
    var remoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Firebase Remote Config
        setUpAndApplyRemoteConfig()
        navDrawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            navDrawer.closeDrawers()
            when (menuItem.itemId) {
                R.id.nav_promo -> {
                    val i = Intent(applicationContext, PromoActivity::class.java)
                    startActivity(i)
                }
                R.id.nav_util -> {
                    val i = Intent(applicationContext, UtilActivity::class.java)
                    startActivity(i)
                }
                R.id.nav_main -> {
                    val i = Intent(applicationContext, MainActivity::class.java)
                    startActivity(i)
                }
                else -> {}
            }
            true
        }
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
        cartAdapter = CartAdapter(this)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        val pagerAdapter = MainPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        /**
         * The [ViewPager] that will host the section contents.
         */
        viewPager = findViewById(R.id.container)
        viewPager.setAdapter(pagerAdapter)
        val tabLayout = findViewById<TabLayout>(R.id.tabs_selector)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun setUpAndApplyRemoteConfig() {
        // RC Demo 1: set up remote config
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(5)
            .build()
        remoteConfig!!.setConfigSettingsAsync(configSettings)
        remoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig!!.fetchAndActivate()
            .addOnCompleteListener {
                Log.d(
                    "ENGAGE-DEBUG",
                    "Remote Control fetched and active"
                )
            }
            .addOnFailureListener { exception: Exception ->
                Log.d(
                    "ENGAGE-DEBUG",
                    "Remote Control FAILED to be fetched: " + exception.localizedMessage
                )
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navDrawer.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(LOG_TAG, "Setting new intent: $intent \nReplacing old: ${getIntent()}")
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        navigationView.setCheckedItem(R.id.nav_main)
        // RC Demo 3: Background color on refresh (A/B testing)
        val color = remoteConfig!!.getString(BG_COLOR_KEY)
        Log.d(LOG_TAG, "Using bg_color of $color from Remote Config")
        viewPager.setBackgroundColor(Color.parseColor(color))

        Log.d(LOG_TAG, "onResume: $intent")
        intent.extras?.let {
            if (intent.extras!!.getString("redirect") == "promo" && !intent.hasExtra("consumed")) {
                intent.putExtra("consumed", true)
                val intent = Intent(applicationContext, PromoActivity::class.java)
                startActivity(intent)
            }
            /*    val keys = intent.extras!!.keySet()
                for (key in keys) {
                    intent.extras!!.getString(key).let {
                        Log.d(LOG_TAG, "$key value: $it")
                    }
                }*/
        }
    }

    override fun getCart(): Cart {
        return cart
    }

    override fun onDataHasChanged() {
        val sumTextView = findViewById<TextView>(R.id.sum_text_view)
        sumTextView.text = cart.sum.toString() + "€"
    }

    override fun getCartAdapter(): CartAdapter {
        return cartAdapter
    }

    companion object {
        const val LOG_TAG = "ENGAGE-DEBUG"
        const val BG_COLOR_KEY = "bg_color"
    }
}