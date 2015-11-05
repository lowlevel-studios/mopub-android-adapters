package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

public class AirpushInterstitial extends CustomEventInterstitial implements AdListener {
    private CustomEventInterstitialListener mInterstitialListener;
    private Main mMain;

    @Override
    protected void loadInterstitial(Context context,
            CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mInterstitialListener = customEventInterstitialListener;

        if (!(context instanceof Activity)) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        Activity activity = (Activity) context;

        AdConfig.setAdListener(this);
        AdConfig.setCachingEnabled(true);

        mMain = new Main(activity);
        mMain.startInterstitialAd(AdConfig.AdType.smartwall);
    }

    @Override
    protected void onInvalidate() {
    }

    @Override
    protected void showInterstitial() {
        try {
            mMain.showCachedAd(AdConfig.AdType.smartwall);
        } catch (Exception e) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        }
    }

    /**
     * Airpush AdListener implementation
     */
    @Override
    public void noAdListener() {
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdCached(AdConfig.AdType adType) {
        if (adType == AdConfig.AdType.smartwall) {
            mInterstitialListener.onInterstitialLoaded();
        }
    }

    @Override
    public void onAdClickedListener() {
        mInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onAdClosed() {
        mInterstitialListener.onInterstitialDismissed();
    }

    @Override
    public void onAdError(String s) {
        if (s.contains("available in cache")) {
            mInterstitialListener.onInterstitialLoaded();
            return;
        }
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
    }

    @Override
    public void onAdExpandedListner() {
    }

    @Override
    public void onAdLoadedListener() {
    }

    @Override
    public void onAdLoadingListener() {
    }

    @Override
    public void onAdShowing() {
        mInterstitialListener.onInterstitialShown();
    }

    @Override
    public void onCloseListener() {
    }

    @Override
    public void onIntegrationError(String s) {
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
    }
}