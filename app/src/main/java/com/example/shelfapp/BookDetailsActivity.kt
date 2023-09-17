package com.example.shelfapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.shelfapp.Util.Constants
import com.example.shelfapp.Util.Logger
import com.example.shelfapp.Util.Utility
import com.example.shelfapp.databinding.ActivityBookDetailsBinding
import com.example.shelfapp.storage.SharedPref
import java.lang.Exception


class BookDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityBookDetailsBinding
    lateinit var sharedPref: SharedPref
    private var doubleBackToExitPressedOnce = false
    private var networkChangeReceiver = NetworkChangeReceiver()

    override fun onStart() {
        super.onStart()
        Logger("On Start details")
        try {
            if (networkChangeReceiver != null)
                unregisterReceiver(networkChangeReceiver)
        } catch (e: Exception) {
            Logger("Exception unregistering network in on Start: ${e.message}")
        }
        networkChangeReceiver = NetworkChangeReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        intentFilter.priority = 100
        registerReceiver(networkChangeReceiver, intentFilter)

        try {
            if (internetCheckReceiver != null)
                unregisterReceiver(internetCheckReceiver)
        } catch (e: Exception) {
            Logger("Exception unregistering network in on Start: ${e.message}")
        }
        val filter = IntentFilter(Constants.INTERNET_BROADCAST_INTENT_FILTER)
        registerReceiver(internetCheckReceiver, filter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_details)
        sharedPref = SharedPref(this)

//        val details = intent?.getStringArrayExtra("BOOK_DETAILED")
        val details = sharedPref.getDetails()
        if (details != null) {
            for (s in details) {
                Logger("$s")
            }
        }

        binding.close.setOnClickListener {
            val openBookList = Intent(this, BookListActivity::class.java)
            startActivity(openBookList)
            finish()
        }

        Glide.with(this)
            .load(details?.get(3))
            .into(binding.detailsLayout.bookImage)

        binding.detailsLayout.bookAlias.text = "Alias: " + details?.get(1)

        binding.detailsLayout.bookHits.text = "Hits: " + details?.get(2)

        binding.bookTitle.text = details?.get(4)

    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger("on Destroy Book Details Activity")
        try {
            unregisterReceiver(networkChangeReceiver)
        } catch (e: Exception) {
            Logger("Exception unregistering network: ${e.message}")
        }
        try {
            unregisterReceiver(internetCheckReceiver)
        } catch (e: Exception) {
            Logger("Exception unregistering internet: ${e.message}")
        }
    }

    private val internetCheckReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Constants.INTERNET_BROADCAST_INTENT_FILTER) {
                val data = intent?.getBooleanExtra("DATA", false)
                onBroadcastReceived(data)
            }
        }
    }

    private fun onBroadcastReceived(status: Boolean?) {
        try {
            if (status == false) {
                Logger("Internet is not available")
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val bundle = Bundle()
                bundle.putString("activityName", "BookDetailsActivity")
                binding.detailsLayout.fragmentContainer.visibility = View.VISIBLE
                binding.bookTitle.text = resources.getString(R.string.app_name)
                binding.detailsLayout.mainLayout.visibility = View.GONE
                val fragment = NoInternetFragment()
                fragment.arguments = bundle
                fragmentTransaction.replace(R.id.fragment_container, fragment)
                fragmentTransaction.commit()
            }
        } catch (e: Exception) {
            Logger("Exception from login: ${e.message}")
        }
    }
}