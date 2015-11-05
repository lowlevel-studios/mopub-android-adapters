package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.BannerView;
import com.mopub.common.util.Views;

import java.util.Map;

public class AppodealBanner extends CustomEventBanner implements BannerCallbacks {
    private static final String APP_KEY = "appKey";

    private Activity mActivity;
    private BannerView mAdView;
    private CustomEventBannerListener mBannerListener;

    @Override
    protected void loadBanner(Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        String appKey;

        if (!(context instanceof Activity)) {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            appKey = serverExtras.get(APP_KEY);
        } else {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mActivity = (Activity) context;

        mAdView = Appodeal.getBannerView(mActivity);

        Appodeal.setBannerCallbacks(this);
        Appodeal.setAutoCache(Appodeal.BANNER_VIEW, false);
        Appodeal.initialize(mActivity, appKey, Appodeal.BANNER_VIEW);
        Appodeal.cache(mActivity, Appodeal.BANNER_VIEW);
    }

    @Override
    protected void onInvalidate() {
        Views.removeFromParent(mAdView);
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey("appKey");
    }

    /**
     * Appodeal BannerCallbacks implementation
     */
    @Override
    public void onBannerLoaded() {
        Appodeal.show(mActivity, Appodeal.BANNER_VIEW);
        mBannerListener.onBannerLoaded(mAdView);
    }

    @Override
    public void onBannerFailedToLoad() {
        mBannerListener.onBannerFailed(MoPubErrorCode.NO_FILL);
    }

    @Override
    public void onBannerShown() {
    }

    @Override
    public void onBannerClicked() {
        mBannerListener.onBannerClicked();
    }
}
