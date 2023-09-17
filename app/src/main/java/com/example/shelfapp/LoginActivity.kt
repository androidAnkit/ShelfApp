package com.example.shelfapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.shelfapp.Util.Constants.Companion.INTERNET_BROADCAST_INTENT_FILTER
import com.example.shelfapp.Util.Logger
import com.example.shelfapp.databinding.ActivityLoginBinding
import com.example.shelfapp.storage.SharedPref
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPref: SharedPref
    private var networkChangeReceiver: NetworkChangeReceiver = NetworkChangeReceiver()

    override fun onStart() {
        super.onStart()
        Logger("On Start Login")
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
        val filter = IntentFilter(INTERNET_BROADCAST_INTENT_FILTER)
        registerReceiver(internetCheckReceiver, filter)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger("On Create Login")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        sharedPref = SharedPref(this)
        // Login Button clicked
        binding.loginLayout.loginBtn.setOnClickListener {
            Logger("${sharedPref.name}, ${sharedPref.password}")
            if (binding.loginLayout.editTextName.text.toString() == sharedPref.name &&
                binding.loginLayout.editTextPassword.text.toString() == sharedPref.password
            ) {
                val openBookList = Intent(this, BookListActivity::class.java)
                startActivity(openBookList)
                finish()

            } else {
                Toast.makeText(this, "Wrong name or password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginLayout.signUp.setOnClickListener {
            val openSignUpActivity = Intent(this, SignUpActivity::class.java)
            startActivity(openSignUpActivity)
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Logger("On Destroy Login")
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
            if (intent?.action == INTERNET_BROADCAST_INTENT_FILTER) {
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
                binding.loginLayout.fragmentContainer.visibility = View.VISIBLE
                binding.loginLayout.mainLayout.visibility = View.GONE
                bundle.putString("activityName", "LoginActivity")
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