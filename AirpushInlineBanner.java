package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.mopub.common.util.Views;

import java.util.Map;

public class AirpushInlineBanner extends CustomEventBanner implements AdListener {
    private AdView mAdView;
    private CustomEventBannerListener mBannerListener;

    @Override
    protected void loadBanner(Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        if (!(context instanceof Activity)) {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        Activity activity = (Activity) context;

        AdConfig.setCachingEnabled(true);

        mAdView = new AdView(activity);
        mAdView.setBannerType(AdView.BANNER_TYPE_IN_APP_AD);
        mAdView.setBannerAnimation(AdView.ANIMATION_TYPE_FADE);
        mAdView.setNewAdListener(this);
        mAdView.loadAd();

        mBannerListener.onBannerLoaded(mAdView);
    }

    @Override
    protected void onInvalidate() {
        Views.removeFromParent(mAdView);
    }

    /**
     * Airpush AdListener implementation
     */
    @Override
    public void noAdListener() {
        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdCached(AdConfig.AdType adType) {
    }

    @Override
    public void onAdClickedListener() {
        mBannerListener.onBannerClicked();
    }

    @Override
    public void onAdClosed() {
    }

    @Override
    public void onAdError(String s) {
        mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
    }

    @Override
    public void onAdExpandedListner() {
        mBannerListener.onBannerExpanded();
    }

    @Override
    public void onAdLoadedListener() {
    }

    @Override
    public void onAdLoadingListener() {
    }

    @Override
    public void onAdShowing() {
    }

    @Override
    public void onCloseListener() {
    }

    @Override
    public void onIntegrationError(String s) {
        mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
    }
}