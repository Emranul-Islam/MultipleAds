package com.emranul.multiads

import android.content.Context

object MultiAdsUtil {

    const val ADS_GOOGLE = "google"
    const val ADS_FACEBOOK = "facebook"
    const val ADS_STARTUP = "startapp"
    const val ADS_UNITY = "unity"
    const val ADS_MAX = "applovin_max"
    const val ADS_CUSTOM = "custom"
    const val ADS_OFF = "none"

    fun offAds(context: Context){
        LocalValue.init(context)
        LocalValue.write("multi_ads_type", ADS_OFF)
    }

    fun initValue(
        context: Context,
        isTestMode:Boolean,
        adsType:String?,
        bannerId:String?,
        bannerLink:String?,
        bannerImage:String?,
        interstitialId:String?,
        interstitialLink:String?,
        interstitialImage:String?,
        interstitialClick:String?,
        nativeId:String?,
        nativeLink:String?,
        nativeImage:String?,
        nativePerItem:String?,
        publisherId:String?, //it's used for StartApp ads and also unity gameId
        appOpenId:String,
        appOpenImage:String,
        appOpenLink:String
    ){

        LocalValue.init(context)
        LocalValue.setTestMode(isTestMode)
        LocalValue.write("multi_ads_type",adsType)
        LocalValue.write("multi_ads_banner_id",bannerId)
        LocalValue.write("multi_ads_banner_link",bannerLink)
        LocalValue.write("multi_ads_banner_image",bannerImage)
        LocalValue.write("multi_ads_interstitial_id",interstitialId)
        LocalValue.write("multi_ads_interstitial_link",interstitialLink)
        LocalValue.write("multi_ads_interstitial_image",interstitialImage)
        LocalValue.write("multi_ads_interstitial_click",interstitialClick)
        LocalValue.write("multi_ads_native_id",nativeId)
        LocalValue.write("multi_ads_native_link",nativeLink)
        LocalValue.write("multi_ads_native_image",nativeImage)
        /**
         * Eta korar karon holo unity and ads max e ekhon kono native dekhabo na
         * */
        if (adsType == ADS_MAX || adsType == ADS_UNITY) {
            LocalValue.write("multi_ads_native_per_item","-10")
        }else{
            LocalValue.write("multi_ads_native_per_item",nativePerItem)
        }
        LocalValue.write("multi_ads_native_per_item",nativePerItem)
        LocalValue.write("multi_ads_publisher_id",publisherId)
        LocalValue.write("multi_ads_app_open_id",appOpenId)
        LocalValue.write("multi_ads_app_open_image",appOpenImage)
        LocalValue.write("multi_ads_app_open_link",appOpenLink)

    }


}