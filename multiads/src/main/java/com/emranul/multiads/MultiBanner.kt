package com.emranul.multiads

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.Nullable
import coil.load
import com.emranul.multiads.databinding.MultiBannerBinding

class MultiBanner(context: Context, @Nullable attrs: AttributeSet) :
    LinearLayout(context, attrs) {

    private var binding: MultiBannerBinding
    private lateinit var ads: Ads
    private var drawable: Drawable? = null

    init {
        binding = MultiBannerBinding.inflate(LayoutInflater.from(context), this, true)
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.MultiBanner, 0, 0)

        try {
            drawable = typedArray.getDrawable(R.styleable.MultiBanner_setPlaceHolderDrawable)
            binding.customAdBanner.load(drawable)
        } finally {
            typedArray.recycle()
        }

    }

    fun initMultiBanner(activity: Activity): Ads {
        ads = Ads(activity)
        ads.allBannerAds(
            binding.addContainer,
            binding.customAdBanner,
            binding.startAppBanner
        )
        return ads
    }
}