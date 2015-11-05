package com.mopub.mobileads;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdSize;
import com.mobusi.adsmobusi.MobusiAd;
import com.mobusi.adsmobusi.MobusiAdListener;
import com.mobusi.adsmobusi.MobusiAdMediation;
import com.mobusi.adsmobusi.MobusiAdType;

import java.util.Map;

public class MobusiBanner extends CustomEventBanner implements MobusiAdListener {
    private static final String ID_ZONE_KEY = "idZone";

    private MobusiAd mAd;
    private CustomEventBannerListener mBannerListener;

    @Override
    protected void loadBanner(Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        String idZone;

        if (extrasAreValid(serverExtras)) {
            idZone = serverExtras.get(ID_ZONE_KEY);
        } else {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mAd = new MobusiAd(context);
        mAd.load(idZone, this);
    }

    @Override
    protected void onInvalidate() {
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(ID_ZONE_KEY);
    }

    /**
     * MobusiAdListener implementation
     */
    @Override
    public void onAdsLoaded(MobusiAdType adType, String s) {
        if (adType == MobusiAdType.BANNER) {
            mBannerListener.onBannerLoaded(mAd);
        }
    }

    @Override
    public void onAdReceiveFail(String s) {
        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdBannerShowed() {
    }

    @Override
    public void onAdBannerClicked() {
        mBannerListener.onBannerClicked();
    }

    @Override
    public void onAdInterstitialClicked() {
    }

    @Override
    public void onAdScreenClosed() {
    }

    @Override
    public void onAdScreenOpened() {
    }
}