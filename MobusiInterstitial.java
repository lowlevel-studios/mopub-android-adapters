package com.mopub.mobileads;

import android.content.Context;

import com.mobusi.adsmobusi.MobusiAd;
import com.mobusi.adsmobusi.MobusiAdListener;
import com.mobusi.adsmobusi.MobusiAdType;

import java.util.Map;

public class MobusiInterstitial extends CustomEventInterstitial implements MobusiAdListener {
    private static final String ID_ZONE_KEY = "idZone";

    private MobusiAd mAd;
    private CustomEventInterstitialListener mInterstitialListener;

    @Override
    protected void loadInterstitial(Context context,
            CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mInterstitialListener = customEventInterstitialListener;

        String idZone;

        if (extrasAreValid(serverExtras)) {
            idZone = serverExtras.get(ID_ZONE_KEY);
        } else {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mAd = new MobusiAd(context);
        mAd.load(idZone, this);
    }

    @Override
    protected void onInvalidate() {
    }

    @Override
    protected void showInterstitial() {
        if (mAd != null) {
            mAd.show();
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(ID_ZONE_KEY);
    }

    /**
     * MobusiAdListener implementation
     */
    @Override
    public void onAdsLoaded(MobusiAdType adType, String s) {
        if (adType == MobusiAdType.INTERSTITIAL || adType == MobusiAdType.VIDEO) {
            mInterstitialListener.onInterstitialLoaded();
        }
    }

    @Override
    public void onAdReceiveFail(String s) {
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdBannerShowed() {
    }

    @Override
    public void onAdBannerClicked() {
    }

    @Override
    public void onAdInterstitialClicked() {
        mInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onAdScreenClosed() {
        mInterstitialListener.onInterstitialDismissed();
    }

    @Override
    public void onAdScreenOpened() {
        mInterstitialListener.onInterstitialShown();
    }
}