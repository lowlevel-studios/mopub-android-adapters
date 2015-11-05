package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.VideoAd;

import java.util.Map;

public class HeyzapVideo extends CustomEventInterstitial implements HeyzapAds.OnStatusListener {
    public static final String PUBLISHER_ID_KEY = "publisherId";

    private Activity mActivity;
    private CustomEventInterstitialListener mInterstitialListener;

    @Override
    protected void loadInterstitial(Context context,
			CustomEventInterstitialListener customEventInterstitialListener,
			Map<String, Object> localExtras,
			Map<String, String> serverExtras) {
        mInterstitialListener = customEventInterstitialListener;

        String publisherId;

        if (!(context instanceof Activity)) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            publisherId = serverExtras.get(PUBLISHER_ID_KEY);
        } else {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mActivity = (Activity) context;

        if (!HeyzapAds.hasStarted()) {
            HeyzapAds.start(publisherId, mActivity, HeyzapAds.DISABLE_AUTOMATIC_FETCH);
        }

        VideoAd.setOnStatusListener(this);
        VideoAd.fetch();
    }

    @Override
    protected void onInvalidate() {
    }

    @Override
    protected void showInterstitial() {
        if (VideoAd.isAvailable()) {
            VideoAd.display(mActivity);
        } else {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(PUBLISHER_ID_KEY);
    }

    /**
     * Heyzap OnStatusListener implementation
     */
    @Override
    public void onShow(String s) {
        mInterstitialListener.onInterstitialShown();
    }

    @Override
    public void onClick(String s) {
        mInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onHide(String s) {
        mInterstitialListener.onInterstitialDismissed();
    }

    @Override
    public void onFailedToShow(String s) {
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
    }

    @Override
    public void onAvailable(String s) {
        mInterstitialListener.onInterstitialLoaded();
    }

    @Override
    public void onFailedToFetch(String s) {
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAudioStarted() {
    }

    @Override
    public void onAudioFinished() {
    }
}