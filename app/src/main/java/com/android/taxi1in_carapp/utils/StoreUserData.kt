package com.taxi1.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class StoreUserData {
    private var pref: SharedPreferences? = null
    private lateinit var parentActivity: Context
    var APP_KEY: String? = null
    private val TAG = javaClass.simpleName


    constructor(context: Context) {
        parentActivity = context
        APP_KEY = context.packageName.replace("\\.".toRegex(), "_").toLowerCase()
        Log.d(TAG, "StoreUserData: $APP_KEY")
    }

    fun setString(key: String, value: String) {
        pref = parentActivity.getSharedPreferences(
            APP_KEY,
            Context.MODE_PRIVATE
        )
        val editor = pref!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String {
        pref = parentActivity.getSharedPreferences(
            APP_KEY,
            Context.MODE_PRIVATE
        )
        return pref!!.getString(key, "")!!
    }

    fun setDouble(key: String?, value: Double) {
        pref = parentActivity.getSharedPreferences(
            APP_KEY,
            Context.MODE_PRIVATE
        )
        val editor = pref!!.edit()
        editor.putString(key, value.toString() + "")
        editor.apply()
    }

    fun getDouble(key: String?): Double? {
        pref = parentActivity.getSharedPreferences(
            APP_KEY,
            Context.MODE_PRIVATE
        )
        return if (pref!!.getString(key, "")!!.length > 0) {
            pref!!.getString(key, "")!!.toDouble()
        } else {
            null
        }
    }

    fun setBoolean(key: String?, value: Boolean) {
        pref = parentActivity.getSharedPreferences(
            APP_KEY,
            Context.MODE_PRIVATE
        )
        val editor = pref!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String?): Boolean {
        pref = parentActivity.getSharedPreferences(
            APP_KEY,
            Context.MODE_PRIVATE
        )
        return pref!!.getBoolean(key, false)
    }


    fun setInt(key: String?, value: Int?) {
        pref = parentActivity.getSharedPreferences(
            APP_KEY,
            Context.MODE_PRIVATE
        )
        val editor = pref!!.edit()
        editor.putInt(key, value!!)
        editor.apply()
    }

    fun getInt(key: String?): Int {
        pref = parentActivity.getSharedPreferences(
            APP_KEY,
            Context.MODE_PRIVATE
        )
        return pref!!.getInt(key, -1)
    }


    fun is_exist(key: String?): Boolean {
        pref = parentActivity.getSharedPreferences(
            APP_KEY,
            Context.MODE_PRIVATE
        )
        return pref!!.contains(key)
    }

    fun clearData(context: Context) {
        val settings = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE)
        settings.edit().clear().apply()

    }
}