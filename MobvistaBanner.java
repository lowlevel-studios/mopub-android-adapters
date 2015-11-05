package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.mobvista.cloud.sdk.AdListener;
import com.mobvista.cloud.sdk.MobvistaAd;
import com.mopub.common.util.Views;

import java.util.Map;

public class MobvistaBanner extends CustomEventBanner implements AdListener {
    private static final String API_KEY = "apiKey";
    private static final String APP_ID_KEY = "appId";

    private MobvistaAd mAd;
    private View mAdView;
    private CustomEventBannerListener mBannerListener;

    @Override
    protected void loadBanner(Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        String apiKey;
        String appId;

        if (extrasAreValid(serverExtras)) {
            apiKey = serverExtras.get(API_KEY);
            appId = serverExtras.get(APP_ID_KEY);
        } else {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        Activity activity = (Activity) context;

        mAd = new MobvistaAd(activity, appId, apiKey);
        mAdView = mAd.getBannerAdView(activity, this);
    }

    @Override
    protected void onInvalidate() {
        Views.removeFromParent(mAdView);
        if (mAd != null) {
            mAd.onDestory();
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(API_KEY) && serverExtras.containsKey(APP_ID_KEY);
    }

    /**
     * Mobvista AdListener implementation
     */
    @Override
    public void onAdLoaded() {
        mBannerListener.onBannerLoaded(mAdView);
    }

    @Override
    public void onAdFailToLoad() {
        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdShow() {
    }

    @Override
    public void onAdClick() {
        mBannerListener.onBannerClicked();
    }

    @Override
    public void onAdClose() {
    }
}