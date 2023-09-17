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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shelfapp.Util.Constants
import com.example.shelfapp.Util.Logger
import com.example.shelfapp.Util.Utility
import com.example.shelfapp.adapters.BookListAdapter
import com.example.shelfapp.databinding.ActivityBookListBinding
import com.example.shelfapp.storage.SharedPref
import java.lang.Exception

class BookListActivity : AppCompatActivity() {

    lateinit var binding: ActivityBookListBinding
    private var doubleBackToExitPressedOnce = false
    private var networkChangeReceiver = NetworkChangeReceiver()
    lateinit var sharedPref: SharedPref

    override fun onStart() {
        super.onStart()
        Logger("On Start List")
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_list)
        sharedPref = SharedPref(this)
        binding.heading.text = "BOOK LIST"
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val bookListAdapter = BookListAdapter(Utility(this).getBookList()) { bookListDataItem ->
            val details = arrayListOf<String?>(
                bookListDataItem.id.toString(),
                bookListDataItem.alias.toString(),
                bookListDataItem.hits.toString(),
                bookListDataItem.image.toString(),
                bookListDataItem.title.toString(),
                bookListDataItem.lastChapterDate.toString(),
            )
            sharedPref.saveDetails(details)
            val openBookDetailsActivity = Intent(this, BookDetailsActivity::class.java)
//            openBookDetailsActivity.putExtra("BOOK_DETAILED", details)
            startActivity(openBookDetailsActivity)
            finish()
        }

        binding.logout.setOnClickListener {
            val openLoginActivity = Intent(this, LoginActivity::class.java)
            startActivity(openLoginActivity)
            finish()
        }

        binding.bookListRv.adapter = bookListAdapter
        binding.bookListRv.layoutManager = LinearLayoutManager(this)

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
        Logger("onDestroy book list activity")
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
                bundle.putString("activityName", "BookListActivity")
                binding.fragmentContainer.visibility = View.VISIBLE
                binding.heading.text = resources.getString(R.string.app_name)
                binding.bookListRv.visibility = View.GONE
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