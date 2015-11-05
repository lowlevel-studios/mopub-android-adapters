package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.FrameLayout;

import com.mopub.common.util.Dips;
import com.mopub.common.util.Views;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.banner.RevMobBanner;

import java.util.Map;

public class RevmobBanner extends CustomEventBanner implements RevMobAdsListener {
    private static final String APP_ID_KEY = "appID";

    private static final int HEIGHT = 50;
    private static final int WIDTH = 320;

    private FrameLayout mAdView;
    private CustomEventBannerListener mBannerListener;
    private Handler mHandler = new Handler();

    @Override
    protected void loadBanner(Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        String appId;

        if (extrasAreValid(serverExtras)) {
            appId = serverExtras.get(APP_ID_KEY);
        } else {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        Activity activity = (Activity) context;

        RevMob revmob = RevMob.start(activity, appId);
        RevMobBanner banner = revmob.createBanner(activity, this);

        int width = Dips.asIntPixels(WIDTH, context);
        int height = Dips.asIntPixels(HEIGHT, context);

        mAdView = new FrameLayout(context);
        mAdView.addView(banner, new FrameLayout.LayoutParams(width, height));
    }

    @Override
    protected void onInvalidate() {
        Views.removeFromParent(mAdView);
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
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBannerListener.onBannerLoaded(mAdView);
            }
        });
    }

    @Override
    public void onRevMobAdNotReceived(String s) {
        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onRevMobAdDisplayed() {
    }

    @Override
    public void onRevMobAdDismiss() {
    }

    @Override
    public void onRevMobAdClicked() {
        mBannerListener.onBannerClicked();
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