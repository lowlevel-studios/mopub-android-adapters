package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.appnext.appnextinterstitial.InterstitialManager;
import com.appnext.appnextinterstitial.OnAdClicked;
import com.appnext.appnextinterstitial.OnAdClosed;
import com.appnext.appnextinterstitial.OnAdError;
import com.appnext.appnextinterstitial.OnAdLoaded;

import java.util.Map;

public class AppnextInterstitial extends CustomEventInterstitial implements OnAdClicked, OnAdClosed, OnAdError, OnAdLoaded {
    public static final String PLACEMENT_ID_KEY = "placementId";
    public static final String TYPE_KEY = "type";

    private Context mContext;
    private CustomEventInterstitialListener mInterstitialListener;
    private String mPlacement;
    private int mType;

    @Override
    protected void loadInterstitial(Context context,
            CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mContext = context;
        mInterstitialListener = customEventInterstitialListener;
        mType = InterstitialManager.INTERSTITIAL_VIDEO;

        if (!(context instanceof Activity)) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            mPlacement = serverExtras.get(PLACEMENT_ID_KEY);
            mType = getType(serverExtras);
        } else {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        InterstitialManager.setOnAdClickedCallback(this);
        InterstitialManager.setOnAdClosedCallback(this);
        InterstitialManager.setOnAdErrorCallback(this);
        InterstitialManager.setOnAdLoadedCallback(this);

        InterstitialManager.cacheInterstitial(mContext, mPlacement, mType);
    }

    @Override
    protected void onInvalidate() {
    }

    @Override
    protected void showInterstitial() {
        InterstitialManager.showInterstitial(mContext, mPlacement, mType);
		mInterstitialListener.onInterstitialShown();
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(PLACEMENT_ID_KEY);
    }

    private int getType(Map<String, String> serverExtras) {
        String type = serverExtras.get(TYPE_KEY);

        if (type == null) {
            return InterstitialManager.INTERSTITIAL_VIDEO;
        }

        switch (type) {
        case "FULL_SCREEN_VIDEO":
            return InterstitialManager.FULL_SCREEN_VIDEO;
        default:
            return InterstitialManager.INTERSTITIAL_VIDEO;
        }
    }

    /**
     * Appnext listeners implementation
     */
    @Override
    public void adClicked() {
        mInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onAdClosed() {
        mInterstitialListener.onInterstitialDismissed();
    }

    @Override
    public void adError(String s) {
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void adLoaded() {
        mInterstitialListener.onInterstitialLoaded();
    }
}