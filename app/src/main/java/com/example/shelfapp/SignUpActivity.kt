package com.example.shelfapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.shelfapp.Util.Constants
import com.example.shelfapp.Util.Logger
import com.example.shelfapp.Util.Utility
import com.example.shelfapp.databinding.ActivitySignUpBinding
import com.example.shelfapp.model.CountryData
import com.example.shelfapp.storage.SharedPref
import org.json.JSONObject
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var sharedPref: SharedPref
    private var networkChangeReceiver: NetworkChangeReceiver = NetworkChangeReceiver()

    override fun onStart() {
        super.onStart()
        Logger("On Start Sign Up")
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        sharedPref = SharedPref(this)
        val passwordPattern = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!])([A-Za-z\\d@#\$%^&+=!]){8,16}$"
        )

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                val matcher = passwordPattern.matcher(password)
                if (matcher.matches()) {
                    sharedPref.password = (binding.layoutSignUp.signUpPassword.text.toString())
                    binding.layoutSignUp.passwordMessage.text =
                        getString(R.string.success_pass_message)
                    binding.layoutSignUp.passwordMessage.setTextColor(
                        resources.getColor(
                            R.color.green,
                            theme
                        )
                    )
                } else {
                    binding.layoutSignUp.passwordMessage.text =
                        getString(R.string.wrong_pass_format)
                    binding.layoutSignUp.passwordMessage.setTextColor(
                        resources.getColor(
                            R.color.red,
                            theme
                        )
                    )
                }
            }
        }

        binding.layoutSignUp.signUpPassword.addTextChangedListener(textWatcher)

        binding.layoutSignUp.signUpBtn.setOnClickListener {
            if (binding.layoutSignUp.signUpName.text.toString().isNotEmpty() &&
                binding.layoutSignUp.signUpPassword.text.toString().isNotEmpty()
            ) {
                if (binding.layoutSignUp.passwordMessage.text.toString() !=
                    getString(R.string.wrong_pass_format)
                ) {
                    sharedPref.name = (binding.layoutSignUp.signUpName.text.toString())
                    val openBookList = Intent(this, BookListActivity::class.java)
                    startActivity(openBookList)
                    finish()
                } else {
                    Toast.makeText(this, "Enter password as per suggested", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Either name or password field is empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.layoutSignUp.login.setOnClickListener {
            val openLoginActivity = Intent(this, LoginActivity::class.java)
            startActivity(openLoginActivity)
            finish()
        }

        var cName = Utility(this).getCountryName()
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, cName)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.layoutSignUp.countryNames.adapter = aa

        binding.layoutSignUp.countryNames.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Logger("Country selected: ${cName[position]}")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger("On Destroy Sign up")
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
                binding.layoutSignUp.fragmentContainer.visibility = View.VISIBLE
                binding.layoutSignUp.mainLayout.visibility = View.GONE
                bundle.putString("activityName", "SignUpActivity")
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