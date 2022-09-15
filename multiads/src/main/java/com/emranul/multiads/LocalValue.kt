package com.emranul.multiads

import android.content.Context
import android.content.SharedPreferences

object LocalValue {
    private var mSharedPref: SharedPreferences? = null
    private const val NAME = "com.emranul.multiads"

    fun init(context: Context) {
        if (mSharedPref == null) {
            mSharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        }
    }

    fun read(key: String?, defValue: String?): String? {
        return mSharedPref!!.getString(key, defValue)
    }
    fun write(key: String?, value: String?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun setTestMode(value: Boolean) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putBoolean("multi_ads_test_mode", value)
        prefsEditor.apply()
    }

    fun isTestMode():Boolean {
        return mSharedPref!!.getBoolean("multi_ads_test_mode",false)
    }




    fun adType(): String? {
        return mSharedPref!!.getString("multi_ads_type", "google")
    }

    fun bannerId(): String {
        return mSharedPref!!.getString("multi_ads_banner_id", null)?:""
    }

    fun bannerLink(): String {
        return mSharedPref!!.getString("multi_ads_banner_link", null)?:""
    }

    fun bannerImage(): String? {
        return mSharedPref!!.getString("multi_ads_banner_image", null)
    }

    fun interstitialId(): String{
        return mSharedPref!!.getString("multi_ads_interstitial_id", null)?:""
    }

    fun interstitialImage(): String {
        return mSharedPref!!.getString("multi_ads_interstitial_image", null)?:""
    }

    fun interstitialLink(): String {
        return mSharedPref!!.getString("multi_ads_interstitial_link", null)?:""
    }

    fun interstitialClick(): Int {
        return mSharedPref!!.getString("multi_ads_interstitial_click", "0")?.toInt()?:0
    }



    fun appOpenId(): String{
        return mSharedPref!!.getString("multi_ads_app_open_id", null)?:""
    }

    fun appOpenImage(): String{
        return mSharedPref!!.getString("multi_ads_app_open_image", null)?:""
    }

    fun appOpenLink(): String {
        return mSharedPref!!.getString("multi_ads_app_open_link", null)?:""
    }

    fun nativeId(): String? {
        return mSharedPref!!.getString("multi_ads_native_id", null)
    }

    fun nativeLink(): String? {
        return mSharedPref!!.getString("multi_ads_native_link", null)
    }

    fun nativeImage(): String? {
        return mSharedPref!!.getString("multi_ads_native_image", null)
    }

    fun nativePerItem(): Int {
        return mSharedPref!!.getInt("multi_ads_native_per_item", 4)
    }


    fun publisherId(): String {
        return mSharedPref!!.getString("multi_ads_publisher_id", "")?:""
    }

    /**
     * For interstitial ads we have to count user click that's
     * why we saved click count
     * */
    fun clickCount(makeZero: Boolean = false) {
        val previewsValue = getClickCount() + 1
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putInt("multi_ads_click_count", if (makeZero) 0 else previewsValue)
        prefsEditor.apply()
    }

    fun getClickCount(): Int {
        return mSharedPref!!.getInt("multi_ads_click_count", 0)
    }


}