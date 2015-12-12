package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdSize;
import com.amazon.device.ads.AdTargetingOptions;
import com.mopub.common.util.Dips;

import java.util.Map;

public class AmazonBanner extends CustomEventBanner implements AdListener {
    private static final String APP_ID_KEY = "appId";

    private AdLayout mAdView;
    private CustomEventBannerListener mBannerListener;

    @Override
    protected void loadBanner(
            Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        String appId;

        if (!(context instanceof Activity)) {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            appId = serverExtras.get(APP_ID_KEY);
        } else {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        final AdSize adSize = calculateAdSize(context);
        final AdLayout.LayoutParams params = new AdLayout.LayoutParams(
                AdLayout.LayoutParams.MATCH_PARENT, Dips.asIntPixels(adSize.getHeight(), context));

        AdRegistration.setAppKey(appId);

        mAdView = new AdLayout((Activity) context, adSize);
        mAdView.setLayoutParams(params);
        mAdView.setListener(this);
        mAdView.loadAd(new AdTargetingOptions());
    }

    @Override
    public void onInvalidate() {
        if (mAdView != null) {
            mAdView.setListener(null);
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(APP_ID_KEY);
    }

    private AdSize calculateAdSize(Context context) {
        final DisplayMetrics dp = context.getResources().getDisplayMetrics();
        final float width = dp.widthPixels / dp.density;

        if (width == 600) {
            return AdSize.SIZE_600x90;
        } else if (width == 800) {
            return AdSize.SIZE_600x90;
        } else if (width == 728) {
            return AdSize.SIZE_728x90;
        } else if (width >= 1024) {
            return AdSize.SIZE_1024x50;
        } else {
            return AdSize.SIZE_320x50;
        }
    }

    /**
     * Amazon AdListener implementation
     */
    @Override
    public void onAdLoaded(Ad ad, AdProperties adProperties) {
        if (mAdView != null && mBannerListener != null) {
            Log.d("MoPub", "Amazon banner ad loaded successfully. Showing ad...");
            mBannerListener.onBannerLoaded(mAdView);
        } else if (mBannerListener != null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.UNSPECIFIED);
        }
    }

    @Override
    public void onAdFailedToLoad(Ad ad, AdError adError) {
        Log.d("MoPub", "Amazon banner ad failed to load.");
        Log.d("MoPub", adError.getCode().toString() + ": " + adError.getMessage());

        if (mBannerListener != null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        }
    }

    @Override
    public void onAdExpanded(Ad ad) {
        Log.d("MoPub", "Amazon banner ad clicked.");
        if (mBannerListener != null) {
            mBannerListener.onBannerClicked();
        }
    }

    @Override
    public void onAdCollapsed(Ad ad) {
        Log.d("MoPub", "Amazon banner ad modal dismissed.");
    }

    @Override
    public void onAdDismissed(Ad ad) {
    }
}