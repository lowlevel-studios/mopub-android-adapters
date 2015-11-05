package com.mopub.nativeads;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.NativeAd;
import com.heyzap.sdk.ads.NativeListener;

import java.util.Map;

public class HeyzapNative extends CustomEventNative {
    public static final String PUBLISHER_ID_KEY = "publisherId";

    private Context mContext;
    private CustomEventNativeListener mNativeListener;

    @Override
    protected void loadNativeAd(@NonNull Context context,
			@NonNull CustomEventNativeListener customEventNativeListener,
			@NonNull Map<String, Object> localExtras,
			@NonNull Map<String, String> serverExtras) {
        mNativeListener = customEventNativeListener;

        String publisherId;

        if (extrasAreValid(serverExtras)) {
            publisherId = serverExtras.get(PUBLISHER_ID_KEY);
        } else {
            mNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        Activity activity = (Activity) context;

        if (!HeyzapAds.hasStarted()) {
            HeyzapAds.start(publisherId, activity, HeyzapAds.DISABLE_AUTOMATIC_FETCH);
        }

        final HeyzapStaticNativeAd heyzapStaticNativeAd = new HeyzapStaticNativeAd(activity);
        heyzapStaticNativeAd.loadAd();
    }

    private boolean extrasAreValid(final Map<String, String> serverExtras) {
        return (serverExtras != null) && serverExtras.containsKey(PUBLISHER_ID_KEY);
    }

    class HeyzapStaticNativeAd extends StaticNativeAd implements NativeListener {
        private NativeAd mNativeAd;
        private ImpressionTracker mImpressionTracker;
        private NativeClickHandler mNativeClickHandler;

        public HeyzapStaticNativeAd(Activity activity) {
            mNativeAd = new NativeAd(activity);
            mNativeAd.setListener(this);

            mImpressionTracker = new ImpressionTracker(activity);
            mNativeClickHandler = new NativeClickHandler(activity);
        }

        void loadAd() {
            mNativeAd.load();
        }

        @Override
        public void onError(HeyzapAds.NativeError error) {
            mNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }

        @Override
        public void onAdLoaded(NativeAd ad) {
            setIconImageUrl(ad.getIcon().getUrl());
            setMainImageUrl(ad.getCoverImage().getUrl());
            setTitle(ad.getTitle());
            setText(ad.getBody());
            setCallToAction(ad.getCallToAction());

            mNativeListener.onNativeAdLoaded(this);
        }

        @Override
        public void onAdClicked(NativeAd ad) {
        }

        @Override
        public void onAdShown(NativeAd ad) {
        }

        @Override
        public void prepare(@NonNull View view) {
            mImpressionTracker.addView(view, this);
            mNativeClickHandler.setOnClickListener(view, this);
        }

        @Override
        public void handleClick(@NonNull View view) {
            notifyAdClicked();
            if (mNativeAd != null) {
                mNativeAd.doClick(view);
            }
        }

        @Override
        public void recordImpression(@NonNull View view) {
            notifyAdImpressed();
            if (mNativeAd != null) {
                mNativeAd.doImpression();
            }
        }
    }
}