package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.mopub.common.util.Views;
import com.notifymob.android.Notify;
import com.notifymob.android.NotifyBannerView;
import com.notifymob.android.NotifyMarker;
import com.notifymob.android.NotifyMarkerListener;

import java.util.Map;

public class NotifyBanner extends CustomEventBanner {
    public static final String DEV_ID_KEY = "developerId";

    private NotifyBannerView mAdView;
    private CustomEventBannerListener mBannerListener;
    private NotifyMarker mMarker;
    private Notify mNotify;

    @Override
    protected void loadBanner(Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        String developerId;

        if (!(context instanceof Activity)) {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            developerId = serverExtras.get(DEV_ID_KEY);
        } else {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mNotify = new Notify((Activity) context, developerId);
        mAdView = new NotifyBannerView(context);

        mMarker = mNotify.startMarker("banner", mAdView, mListener);
    }

    @Override
    protected void onInvalidate() {
        Views.removeFromParent(mAdView);
        if (mNotify != null && mMarker != null) {
            mNotify.stopMarker(mMarker);
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(DEV_ID_KEY);
    }

    private final NotifyMarkerListener mListener = new NotifyMarkerListener() {
        @Override
        public void onReady() {
            mBannerListener.onBannerLoaded(mAdView);
        }

        @Override
        public void onDisplayed() {
        }

        @Override
        public void onDismissed() {
        }

        @Override
        public void onLinkFollowed() {
            mBannerListener.onBannerClicked();
        }

        @Override
        public boolean onUnavailable() {
            mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
            return false;
        }
    };
}