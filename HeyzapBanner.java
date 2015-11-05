package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.heyzap.sdk.ads.BannerAdView;
import com.heyzap.sdk.ads.HeyzapAds;
import com.mopub.common.util.Views;

import java.util.Map;

public class HeyzapBanner extends CustomEventBanner implements HeyzapAds.BannerListener {
    public static final String PUBLISHER_ID_KEY = "publisherId";

    private BannerAdView mAdView;
    private CustomEventBannerListener mBannerListener;

    @Override
    protected void loadBanner(
            Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        String publisherId;

        if (!(context instanceof Activity)) {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            publisherId = serverExtras.get(PUBLISHER_ID_KEY);
        } else {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        Activity activity = (Activity) context;

        if (!HeyzapAds.hasStarted()) {
            HeyzapAds.start(publisherId, activity, HeyzapAds.DISABLE_AUTOMATIC_FETCH);
        }

        mAdView = new BannerAdView(activity);
        mAdView.setBannerListener(this);
        mAdView.load(publisherId);
    }

    @Override
    protected void onInvalidate() {
        Views.removeFromParent(mAdView);
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(PUBLISHER_ID_KEY);
    }

    /**
     * Heyzap BannerListener implementation
     */
    @Override
    public void onAdError(BannerAdView adView, HeyzapAds.BannerError error) {
        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdLoaded(BannerAdView adView) {
        mBannerListener.onBannerLoaded(adView);
    }

    @Override
    public void onAdClicked(BannerAdView adView) {
        mBannerListener.onBannerClicked();
    }
}