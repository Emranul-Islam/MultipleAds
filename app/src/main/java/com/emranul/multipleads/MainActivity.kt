package com.emranul.multipleads

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.emranul.multiads.MultiAdsUtil
import com.emranul.multipleads.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    val ads = binding.ads.initMultiBanner(this)


        binding.dsdsf.setOnClickListener {
            ads.showInterstitialAds()
        }


    }
}