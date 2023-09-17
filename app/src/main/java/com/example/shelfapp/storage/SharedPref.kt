package com.example.shelfapp.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.shelfapp.Util.Constants.Companion.PREF_DETAILS
import com.example.shelfapp.Util.Constants.Companion.PREF_LOGIN_NAME
import com.example.shelfapp.Util.Constants.Companion.PREF_LOGIN_PASSWORD
import com.example.shelfapp.Util.Constants.Companion.PREF_NAME
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


private var sharedPreferencesEnc: SharedPreferences? = null
private var editorEnc: SharedPreferences.Editor? = null

class SharedPref(private val context: Context) {
    init {
        sharedPreferencesEnc = context?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editorEnc = sharedPreferencesEnc?.edit()
    }

    var name:String?

        get() = sharedPreferencesEnc?.getString(PREF_LOGIN_NAME, "000000")
        set(value){
            value?.let { editorEnc?.putString(PREF_LOGIN_NAME, it)?.commit() }
        }

    var password: String?
        get() = sharedPreferencesEnc?.getString(PREF_LOGIN_PASSWORD, "S000000")
        set(value){
            value?.let { editorEnc?.putString(PREF_LOGIN_PASSWORD, it)?.commit() }
        }

    fun saveDetails(list: ArrayList<String?>?) {
        val gson = Gson()
        val json = gson.toJson(list)
        editorEnc?.putString(PREF_DETAILS, json)?.commit()
    }

    fun getDetails(): ArrayList<String?>? {
        val gson = Gson()
        val json = sharedPreferencesEnc?.getString(PREF_DETAILS, null)
        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson(json, type)
    }

}