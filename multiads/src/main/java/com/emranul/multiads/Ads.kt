package com.emranul.multiads

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import coil.load
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinSdk
import com.emranul.multiads.MultiAdsUtil.ADS_CUSTOM
import com.emranul.multiads.MultiAdsUtil.ADS_FACEBOOK
import com.emranul.multiads.MultiAdsUtil.ADS_GOOGLE
import com.emranul.multiads.MultiAdsUtil.ADS_MAX
import com.emranul.multiads.MultiAdsUtil.ADS_OFF
import com.emranul.multiads.MultiAdsUtil.ADS_STARTUP
import com.emranul.multiads.MultiAdsUtil.ADS_UNITY
import com.emranul.multiads.databinding.AdsCustomInterBinding
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.facebook.ads.AdView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.unity3d.ads.*
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import java.util.*

class Ads(private val activity: Activity) {

    private var facebookBannerAds: AdView? = null
    private var facebookInterstitialAd: InterstitialAd? = null

    private var googleBannerAd: com.google.android.gms.ads.AdView? = null
    private var googleInterstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd? = null

    private var startAppAd: StartAppAd? = null

    private var unityId: String = ""

    private var appLovinInter: MaxInterstitialAd? = null
    private var TEST_MODE = false


    init {

        LocalValue.init(activity)
        TEST_MODE = LocalValue.isTestMode()
        unityId = if (TEST_MODE) "491239" else LocalValue.publisherId()


    }

    companion object {
        private const val TAG = "Ads"


    }

    /**
     *  Google ads section ----------->
     */
    private fun googleBannerAdInit(addContainerView: LinearLayout) {


        MobileAds.initialize(activity) {}
        googleBannerAd = com.google.android.gms.ads.AdView(activity)
        googleBannerAd!!.setAdSize(AdSize.BANNER)
        googleBannerAd!!.adUnitId =
            if (TEST_MODE) "ca-app-pub-3940256099942544/6300978111" else LocalValue.bannerId()
        addContainerView.addView(googleBannerAd)
        val adRequest = AdRequest.Builder().build()
        googleBannerAd!!.loadAd(adRequest)

        googleInterstitialInit()


    }

    private fun googleInterstitialInit() {


        try {


            MobileAds.initialize(activity) {}
            val adRequest = AdRequest.Builder().build()
            val adUnitId =
                if (TEST_MODE) "ca-app-pub-3940256099942544/8691691433" else LocalValue.interstitialId()

            if (TEST_MODE) {
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf("7EF29D1DDC36ED837C8CE027B8AB122D"))
            }


            Log.d(TAG, "googleInterstitialInit: $adUnitId")

            com.google.android.gms.ads.interstitial.InterstitialAd.load(
                activity,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, "google interstitial ads error ${adError.message}")
                        googleInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                        Log.d(TAG, "Ad was loaded.")
                        googleInterstitialAd = interstitialAd
                        Log.d(TAG, "Ad was loaded. Pore $googleInterstitialAd")

                    }
                })


        } catch (e: Exception) {
            Log.d(TAG, "googleInterstitialInit: ${e.message}")
        }

    }

    private fun googleInterShow() {


        Log.d(TAG, "googleInterShow: $googleInterstitialAd")

        if (googleInterstitialAd != null) {
            googleInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                        Log.d(TAG, "Ad failed to show.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        LocalValue.clickCount(true)
                        googleInterstitialAd = null
                    }
                }

            googleInterstitialAd?.show(activity)
        }

    }


    /**
     *  Facebook ads section ----------->
     */
    private fun facebookBannerAdInit(addContainerView: LinearLayout) {
        AudienceNetworkAds.initialize(activity)

        if (TEST_MODE) {
            AdSettings.addTestDevice("97919c6c-ec27-42a8-bac0-98addc506a46")
        }


        val placement = LocalValue.bannerId()
        facebookBannerAds = AdView(activity, placement, com.facebook.ads.AdSize.BANNER_HEIGHT_50)
        addContainerView.addView(facebookBannerAds)
        facebookBannerAds!!.loadAd()

        facebookInterstitialAd()
    }

    private fun facebookInterstitialAd() {

        AudienceNetworkAds.initialize(activity)

        if (TEST_MODE) {
            AdSettings.addTestDevice("97919c6c-ec27-42a8-bac0-98addc506a46")
        }

        facebookInterstitialAd =
            InterstitialAd(
                activity,
                LocalValue.interstitialId()
            )
        val interstitialAdListener = object : InterstitialAdListener {
            override fun onError(p0: Ad?, p1: AdError?) {
                Log.d(TAG, "onError: ${p1?.errorMessage}")
            }

            override fun onAdLoaded(p0: Ad?) {
                Log.d(TAG, "onAdLoaded: ")
            }

            override fun onAdClicked(p0: Ad?) {
            }

            override fun onLoggingImpression(p0: Ad?) {

            }

            override fun onInterstitialDisplayed(p0: Ad?) {
                Log.d(TAG, "onInterstitialDisplayed")
                LocalValue.clickCount(true)
            }

            override fun onInterstitialDismissed(p0: Ad?) {


            }
        }

        facebookInterstitialAd?.loadAd(
            facebookInterstitialAd?.buildLoadAdConfig()
                ?.withAdListener(interstitialAdListener)
                ?.build()
        )
    }

    private fun facebookInterstitialAdShow() {

        facebookInterstitialAd?.let {

            Log.d(TAG, "facebookInterstitialAdShow: Request For Ads")
            if (it.isAdLoaded) {
                Log.d(TAG, "facebookInterstitialAdShow: Ad Showed")
                it.show()
            } else {
                it.loadAd()
            }
        }

    }


    /**
     *  StartApp ads section ----------->
     */
    private fun startAppInit() {
        val startAppId = if (TEST_MODE) "thisistestmode" else LocalValue.publisherId()
        StartAppSDK.init(activity, startAppId, true)
        startAppAd = StartAppAd(activity)

    }

    /**
     * Unity ads section
     * */
    private fun unityInit() {

        UnityAds.initialize(activity, unityId, TEST_MODE, object : IUnityAdsInitializationListener {
            override fun onInitializationComplete() {
                Log.d(TAG, "onInitializationComplete: ")
                unityLoadAds()
            }

            override fun onInitializationFailed(
                p0: UnityAds.UnityAdsInitializationError?,
                p1: String?
            ) {
                Log.d(TAG, "onInitializationFailed: $p0")
            }
        })


    }

    private fun unityLoadAds() {
        UnityAds.load(
            LocalValue.interstitialId(),
            object : IUnityAdsLoadListener {
                override fun onUnityAdsAdLoaded(p0: String?) {
                    Log.d(TAG, "onUnityAdsAdLoaded: $p0")
                }

                override fun onUnityAdsFailedToLoad(
                    p0: String?,
                    p1: UnityAds.UnityAdsLoadError?,
                    p2: String?
                ) {
                    Log.d(TAG, "onUnityAdsFailedToLoad: $p0 $p1 $p2")
                }
            })
    }


    private fun unityBanner(adsContainer: LinearLayout) {
        val bottomBanner = BannerView(
            activity,
            LocalValue.bannerId(),
            UnityBannerSize(320, 50)
        )
        bottomBanner.listener = object : BannerView.IListener {
            override fun onBannerLoaded(bannerAdView: BannerView) {
                Log.v(TAG, "onBannerLoaded: " + bannerAdView.placementId)
            }

            override fun onBannerFailedToLoad(
                bannerAdView: BannerView,
                errorInfo: BannerErrorInfo
            ) {
                adsContainer.isVisible = false
                Log.e(
                    TAG,
                    "Unity Ads failed to load banner for " + bannerAdView.placementId + " with error: [" + errorInfo.errorCode + "] " + errorInfo.errorMessage
                )
            }

            override fun onBannerClick(bannerAdView: BannerView) {
                Log.v(TAG, "onBannerClick: " + bannerAdView.placementId)
            }

            override fun onBannerLeftApplication(bannerAdView: BannerView) {
                Log.v(TAG, "onBannerLeftApplication: " + bannerAdView.placementId)
            }

        }
        bottomBanner.load()
        adsContainer.addView(bottomBanner)
    }


    private fun unityShowInterstitialAds() {
        LocalValue.clickCount(true)
        UnityAds.show(
            activity,
            LocalValue.interstitialId(),
            UnityAdsShowOptions(),
            object : IUnityAdsShowListener {
                override fun onUnityAdsShowFailure(
                    placementId: String,
                    error: UnityAds.UnityAdsShowError,
                    message: String
                ) {
                    Log.e(TAG, "onUnityAdsShowFailure: $error - $message")
                }

                override fun onUnityAdsShowStart(placementId: String) {
                    Log.v(TAG, "onUnityAdsShowStart: $placementId")
                    LocalValue.clickCount(true)
                }

                override fun onUnityAdsShowClick(placementId: String) {
                    Log.v(TAG, "onUnityAdsShowClick: $placementId")
                }

                override fun onUnityAdsShowComplete(
                    placementId: String,
                    state: UnityAds.UnityAdsShowCompletionState
                ) {
                    Log.v(TAG, "onUnityAdsShowComplete: $placementId")
                    unityLoadAds()

                }
            })
    }


    /**
     *
     *      AppLovin MAX section ------------------>
     *
     * */

    private fun applovinMaxInitWithBanner(addContainer: LinearLayout) {


        AppLovinSdk.getInstance(activity).mediationProvider = "max"
        AppLovinSdk.getInstance(activity).initializeSdk {
            // AppLovin SDK is initialized, start loading ads
            Log.d(TAG, "onCreate: ")

            val adsView =
                MaxAdView(
                    LocalValue.bannerId(),
                    activity.applicationContext
                )
            adsView.setListener(object : MaxAdViewAdListener {
                override fun onAdLoaded(ad: MaxAd?) {
                    Log.d(TAG, "onAdLoaded: ")
                }

                override fun onAdDisplayed(ad: MaxAd?) {
                    Log.d(TAG, "onAdDisplayed: $ad")
                }

                override fun onAdHidden(ad: MaxAd?) {
                    Log.d(TAG, "onAdHidden: $ad")
                }

                override fun onAdClicked(ad: MaxAd?) {
                    Log.d(TAG, "onAdClicked: $ad")
                }

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                    Log.d(TAG, "onAdLoadFailed: $adUnitId $error")
                    addContainer.isVisible = false
                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    Log.d(TAG, "onAdDisplayFailed: $ad $error")
                }

                override fun onAdExpanded(ad: MaxAd?) {
                    Log.d(TAG, "onAdExpanded: $ad")
                }

                override fun onAdCollapsed(ad: MaxAd?) {
                    Log.d(TAG, "onAdCollapsed: $ad")
                }
            })

            // Stretch to the width of the screen for banners to be fully functional
            val width = ViewGroup.LayoutParams.MATCH_PARENT

            // Banner height on phones and tablets is 50 and 90, respectively
//        val heightPx = applicationContext.resources.getDimensionPixelSize(R.dimen.banner_height)

            adsView.layoutParams = FrameLayout.LayoutParams(width, 60)

            // Set background or background color for banners to be fully functional
//        adsView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white))

            addContainer.addView(adsView)

            // Load the ad
            adsView.loadAd()
        }

    }

    private fun applovinInitInterstitial() {

        appLovinInter = MaxInterstitialAd(
            LocalValue.interstitialId(), activity
        )
        appLovinInter?.setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                Log.d(TAG, "onAdLoaded: ")
            }

            override fun onAdDisplayed(ad: MaxAd?) {
                Log.d(TAG, "onAdDisplayed: ")
                LocalValue.clickCount(true)
            }

            override fun onAdHidden(ad: MaxAd?) {
                Log.d(TAG, "onAdHidden: ")
                appLovinInter?.loadAd()
            }

            override fun onAdClicked(ad: MaxAd?) {
                Log.d(TAG, "onAdClicked: ")
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                Log.d(TAG, "onAdLoadFailed: $adUnitId ${error?.message}")
                appLovinInter?.loadAd()
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                Log.d(TAG, "onAdDisplayFailed: ${error?.message}")
                appLovinInter?.loadAd()
            }
        })
        appLovinInter?.loadAd()


    }


    private fun applovinInterShow() {
        if (appLovinInter?.isReady == true) {
            appLovinInter!!.showAd()
        } else {
            Log.d(TAG, "applovinInterShow: Add Not loaded")
        }
    }


    /**
     *  Custom ads section ----------->
     */
    private fun customBannerAds(imageView: ImageView) {
        val url = LocalValue.bannerImage()
        imageView.isVisible = true
        imageView.load(url)
        imageView.setOnClickListener {
            openWebsite(LocalValue.bannerLink())
        }

    }

    private fun openWebsite(url: String?) {
        /**
         *  It will send to any type of browser with this link
         */
        try {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        url ?: ""
                    )
                )
            )
        } catch (e: Exception) {
            Log.d(TAG, "open url failed: ${e.message}")
        }
    }

    private fun showCustomInterstitialAds(image: String, link: String) {

        val binding =
            AdsCustomInterBinding.inflate(LayoutInflater.from(activity.applicationContext))

        val dialog = Dialog(activity).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(binding.root)
        }

        binding.image.load(image) {
            placeholder(R.drawable.place_holder_image)
            placeholder(R.drawable.place_holder_image)
        }

        binding.btnClose.setOnClickListener {
            dialog.dismiss()
        }
        binding.title.setOnClickListener {
            openWebsite(link)
        }

        dialog.setOnDismissListener {
            LocalValue.clickCount(true)
        }

        dialog.show()


    }


    /**
     * Open ads section ------------>
     * */
    private fun showGoogleOpenAds() {
        if (LocalValue.adType() == ADS_GOOGLE && LocalValue.appOpenId().isNotEmpty()
        ) {
            Log.d(TAG, "onCreate: ${LocalValue.appOpenId()}")
            try {
                val application = activity.application
                // Show the app open ad.
                (application as AppOpenManager)
                    .showAdIfAvailable(
                        activity,
                        object : OnShowAdCompleteListener {
                            override fun onShowAdComplete() {
                                Log.e(TAG, "onAdShowedFullScreenContent.")
                            }
                        })
            } catch (e: Exception) {
                Log.i(TAG, e.message.toString())
            }
        }
    }

    private fun showCustomOpenAds() {
        if (
            LocalValue.adType() == ADS_CUSTOM && LocalValue.appOpenImage().isNotEmpty()
        ) {
            showCustomInterstitialAds(
                LocalValue.appOpenImage(),
                LocalValue.appOpenLink()
            )
        }
    }


    /**
     *
     * From Here all of the public classes >>>>>>>>>>>>>>>>>>>>
     *
     * */

    fun showOpenAds() {
        showGoogleOpenAds()
        showCustomOpenAds()
    }

    fun showInterstitialAds() {

        if (LocalValue.adType() != ADS_OFF) {
            LocalValue.clickCount()
            try {
                if (LocalValue.getClickCount() > LocalValue.interstitialClick()) {
                    when (LocalValue.adType()) {
                        ADS_GOOGLE -> {
                            googleInterShow()
                        }
                        ADS_FACEBOOK -> {
                            facebookInterstitialAdShow()
                        }
                        ADS_STARTUP -> {
                            startAppAd?.showAd(object : AdDisplayListener {
                                override fun adDisplayed(p0: com.startapp.sdk.adsbase.Ad?) {
                                    LocalValue.clickCount(true)
                                }

                                override fun adHidden(p0: com.startapp.sdk.adsbase.Ad?) {

                                }

                                override fun adClicked(p0: com.startapp.sdk.adsbase.Ad?) {

                                }

                                override fun adNotDisplayed(p0: com.startapp.sdk.adsbase.Ad?) {

                                }
                            })
                        }
                        ADS_UNITY -> {
                            unityShowInterstitialAds()
                        }

                        ADS_MAX -> {
                            applovinInterShow()
                        }

                        ADS_CUSTOM -> {

                            showCustomInterstitialAds(
                                LocalValue.interstitialImage(),
                                LocalValue.interstitialLink()
                            )

                        }


                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "showInterstitialAds: ${e.message}")
            }
        }
    }


    fun allBannerAds(
        addContainer: LinearLayout,
        customAdBanner: ImageView,
        startAppBanner: Banner
    ) {
        LocalValue.adType()?.let {

            when (it) {
                ADS_GOOGLE -> {

                    Log.d(TAG, "handleAdSystem: Ads Google")
                    googleBannerAdInit(addContainer)

                }
                ADS_FACEBOOK -> {


                    Log.d(TAG, "handleAdSystem: Ads Facebook")
                    facebookBannerAdInit(addContainer)

                }
                ADS_CUSTOM -> {


                    Log.d(TAG, "handleAdSystem: Ads Facebook")

                    customBannerAds(customAdBanner)


                }
                ADS_STARTUP -> {

                    startAppBanner.isVisible = true
                    Log.d(TAG, "handleAdSystem: Ads StartApp/Startup")
                    startAppInit()

                }
                ADS_UNITY -> {
                    Log.d(TAG, "handleAdSystem: Unity: ")
                    unityInit()
                    unityBanner(addContainer)
                }

                ADS_MAX -> {
                    applovinMaxInitWithBanner(addContainer)
                    applovinInitInterstitial()
                }

                else -> {
                    addContainer.isVisible = false
                    Log.d(TAG, "handleAdSystem: Don't have ads")
                }
            }

        }


    }

    fun onPauseAd() {
        googleBannerAd?.pause()
    }

    fun onResumeAd() {

        googleBannerAd?.resume()
    }

    fun onDestroyAd() {
        googleBannerAd?.destroy()
        facebookInterstitialAd?.destroy()
        facebookBannerAds?.destroy()

    }

    fun onBackPressed() {
        startAppAd?.onBackPressed()
    }


}