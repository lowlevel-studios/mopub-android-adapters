package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.InterstitialAd;

import java.util.Map;

public class AmazonInterstitial extends CustomEventInterstitial implements AdListener {
    private static final String APP_ID_KEY = "appId";

    private InterstitialAd mInterstitial;
    private CustomEventInterstitialListener mInterstitialListener;

    @Override
    protected void loadInterstitial(
            Context context,
            CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mInterstitialListener = customEventInterstitialListener;

        String appId;

        if (!(context instanceof Activity)) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            appId = serverExtras.get(APP_ID_KEY);
        } else {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        AdRegistration.setAppKey(appId);

        mInterstitial = new InterstitialAd((Activity) context);
        mInterstitial.setListener(this);
        mInterstitial.loadAd();
    }

    @Override
    public void onInvalidate() {
        if (mInterstitial != null) {
            mInterstitial.setListener(null);
        }
    }

    @Override
    protected void showInterstitial() {
        if (mInterstitial != null && mInterstitial.isReady() && mInterstitialListener != null) {
            mInterstitial.showAd();
            mInterstitialListener.onInterstitialShown();
        } else if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(APP_ID_KEY);
    }

    /**
     * Amazon AdListener implementation
     */
    @Override
    public void onAdLoaded(Ad ad, AdProperties adProperties) {
        if (mInterstitial != null && mInterstitialListener != null) {
            Log.d("MoPub", "Amazon interstitial ad loaded successfully.");
            mInterstitialListener.onInterstitialLoaded();
        } else if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
        }
    }

    @Override
    public void onAdFailedToLoad(Ad ad, AdError adError) {
        Log.d("MoPub", "Amazon interstitial ad failed to load.");
        Log.d("MoPub", adError.getCode().toString() + ": " + adError.getMessage());

        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        }
    }

    @Override
    public void onAdExpanded(Ad ad) {
        Log.d("MoPub", "Amazon interstitial clicked?");
    }

    @Override
    public void onAdCollapsed(Ad ad) {
    }

    @Override
    public void onAdDismissed(Ad ad) {
        if (mInterstitialListener != null) {
            mInterstitialListener.onInterstitialDismissed();
        }
    }
}