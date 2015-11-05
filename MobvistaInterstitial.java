package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.mobvista.cloud.sdk.AdListener;
import com.mobvista.cloud.sdk.MobvistaAd;

import java.util.Map;

public class MobvistaInterstitial extends CustomEventInterstitial implements AdListener {
    private static final String API_KEY = "apiKey";
    private static final String APP_ID_KEY = "appId";
    private static final String TYPE_KEY = "type";

    private MobvistaAd mAd;
    private CustomEventInterstitialListener mInterstitialListener;
    private String mType;

    @Override
    protected void loadInterstitial(Context context,
            CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mInterstitialListener = customEventInterstitialListener;

        String apiKey;
        String appId;
        String type;

        if (extrasAreValid(serverExtras)) {
            apiKey = serverExtras.get(API_KEY);
            appId = serverExtras.get(APP_ID_KEY);
            type = serverExtras.get(TYPE_KEY);
        } else {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (type == null) {
            type = "interstitial";
        }

        Activity activity = (Activity) context;

        switch (type) {
        case "exit":
            mType = MobvistaAd.SCENARIO_EXIT;
            break;
        case "splash":
            mType = MobvistaAd.SCENARIO_SPLASH;
            break;
        default:
            mType = MobvistaAd.SCENARIO_INTERSTITIAL;
        }

        mAd = new MobvistaAd(activity, appId, apiKey);
        mAd.loadAd(activity, this);
    }

    @Override
    protected void onInvalidate() {
        if (mAd != null) {
            mAd.onDestory();
        }
    }

    @Override
    protected void showInterstitial() {
        mAd.showAd(mType);
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(API_KEY) && serverExtras.containsKey(APP_ID_KEY);
    }

    /**
     * Mobvista AdListener implementation
     */
    @Override
    public void onAdLoaded() {
        mInterstitialListener.onInterstitialLoaded();
    }

    @Override
    public void onAdFailToLoad() {
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdShow() {
        mInterstitialListener.onInterstitialShown();
    }

    @Override
    public void onAdClick() {
        mInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onAdClose() {
        mInterstitialListener.onInterstitialDismissed();
    }
}