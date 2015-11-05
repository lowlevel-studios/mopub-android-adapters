package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.notifymob.android.Notify;
import com.notifymob.android.NotifyMarker;
import com.notifymob.android.NotifyMarkerListener;

import java.util.Map;

public class NotifyInterstitial extends CustomEventInterstitial {
    public static final String DEV_ID_KEY = "developerId";

    private CustomEventInterstitialListener mInterstitialListener;
    private NotifyMarker mMarker;
    private Notify mNotify;

    @Override
    protected void loadInterstitial(Context context,
            CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mInterstitialListener = customEventInterstitialListener;

        String developerId;

        if (!(context instanceof Activity)) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            developerId = serverExtras.get(DEV_ID_KEY);
        } else {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mNotify = new Notify((Activity) context, developerId);
        mInterstitialListener.onInterstitialLoaded();
    }

    @Override
    protected void onInvalidate() {
        if (mNotify != null && mMarker != null) {
            mNotify.stopMarker(mMarker);
        }
    }

    @Override
    protected void showInterstitial() {
        mMarker = mNotify.startMarker("interstitial", NotifyMarker.FLAG_ALLOW_INTERSTITIAL_TYPE, mListener);
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(DEV_ID_KEY);
    }

    private final NotifyMarkerListener mListener = new NotifyMarkerListener() {
        @Override
        public void onReady() {
        }

        @Override
        public void onDisplayed() {
            mInterstitialListener.onInterstitialShown();
        }

        @Override
        public void onDismissed() {
            mInterstitialListener.onInterstitialDismissed();
        }

        @Override
        public void onLinkFollowed() {
            mInterstitialListener.onInterstitialClicked();
        }

        @Override
        public boolean onUnavailable() {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
            return false;
        }
    };
}