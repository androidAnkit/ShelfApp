package com.example.shelfapp.Util

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import com.example.shelfapp.InternetCheck
import com.example.shelfapp.LoginActivity
import com.example.shelfapp.NetworkChangeReceiver
import com.example.shelfapp.model.BookListData
import com.example.shelfapp.model.BookListDataItem
import com.example.shelfapp.model.CountryData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class Utility(private val context: Context) {
    fun getBookList(): BookListData {
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("BookList.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            Logger(" The ioException occurred in bookList: ${ioException.message}")
        }

        return Gson().fromJson(jsonString, BookListData::class.java)
    }

    private fun getCountryList(): CountryData {

        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("CountryList.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            Logger(" The ioException occurred in CountryList: ${ioException.message}")
        }
        return Gson().fromJson(jsonString, CountryData::class.java)
    }

    fun getCountryName(): Array<String?> {
        val countryData: CountryData = getCountryList()
        val countries = countryData.data
        val countriesName = arrayOfNulls<String>(countries.size)
        var i = 0
        for ((countryCode, country) in countries) {
            Logger("$countryCode: ${country.country}, Region: ${country.region}")
            countriesName[i] = country.country
            i++
        }
        return countriesName
    }

    fun isNetworkAvailable(): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        return result
    }


}