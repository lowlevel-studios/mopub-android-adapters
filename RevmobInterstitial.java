package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.fullscreen.RevMobFullscreen;

import java.util.Map;

public class RevmobInterstitial extends CustomEventInterstitial implements RevMobAdsListener {
    private static final String APP_ID_KEY = "appID";

    private RevMobFullscreen mFullscreen;
    private CustomEventInterstitialListener mInterstitialListener;

    @Override
    protected void loadInterstitial(Context context,
            CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mInterstitialListener = customEventInterstitialListener;

        String appId;

        if (extrasAreValid(serverExtras)) {
            appId = serverExtras.get(APP_ID_KEY);
        } else {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        Activity activity = (Activity) context;

        RevMob revmob = RevMob.start(activity, appId);
        mFullscreen = revmob.createFullscreen(activity, this);
    }

    @Override
    protected void onInvalidate() {
        if (mFullscreen != null) {
            mFullscreen.hide();
        }
    }

    @Override
    protected void showInterstitial() {
        if (mFullscreen != null) {
            mFullscreen.show();
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(APP_ID_KEY);
    }

    /**
     * RevMobAdsListener implementation
     */
    @Override
    public void onRevMobSessionIsStarted() {
    }

    @Override
    public void onRevMobSessionNotStarted(String s) {
    }

    @Override
    public void onRevMobAdReceived() {
        mInterstitialListener.onInterstitialLoaded();
    }

    @Override
    public void onRevMobAdNotReceived(String s) {
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onRevMobAdDisplayed() {
        mInterstitialListener.onInterstitialShown();
    }

    @Override
    public void onRevMobAdDismiss() {
        mInterstitialListener.onInterstitialDismissed();
    }

    @Override
    public void onRevMobAdClicked() {
        mInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onRevMobEulaIsShown() {
    }

    @Override
    public void onRevMobEulaWasAcceptedAndDismissed() {
    }

    @Override
    public void onRevMobEulaWasRejected() {
    }
}