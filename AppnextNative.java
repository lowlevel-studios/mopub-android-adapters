package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.appnext.appnextsdk.API.AppnextAPI;
import com.appnext.appnextsdk.API.AppnextAd;
import com.appnext.appnextsdk.API.AppnextAdRequest;

import java.util.ArrayList;
import java.util.Map;

public class AppnextNative extends CustomEventNative {
    public static final String PLACEMENT_ID_KEY = "placementId";

    private Context mContext;
    private CustomEventNativeListener mNativeListener;

    @Override
    protected void loadNativeAd(@NonNull Context context,
			@NonNull CustomEventNativeListener customEventNativeListener,
			@NonNull Map<String, Object> localExtras,
			@NonNull Map<String, String> serverExtras) {
        mNativeListener = customEventNativeListener;
        mContext = context;

        String placementId;

        if (extrasAreValid(serverExtras)) {
            placementId = serverExtras.get(PLACEMENT_ID_KEY);
        } else {
            mNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        final AppnextStaticNativeAd appnextStaticNativeAd =
                new AppnextStaticNativeAd(context, new AppnextAPI(context, placementId));
        appnextStaticNativeAd.loadAd();
    }

    private boolean extrasAreValid(final Map<String, String> serverExtras) {
        return (serverExtras != null) && serverExtras.containsKey(PLACEMENT_ID_KEY);
    }

    class AppnextStaticNativeAd extends StaticNativeAd implements AppnextAPI.AppnextAdListener {
        private AppnextAd mAd;
        private AppnextAPI mApi;
        private ImpressionTracker mImpressionTracker;
        private NativeClickHandler mNativeClickHandler;

        public AppnextStaticNativeAd(Context context, AppnextAPI api) {
            mApi = api;
            mImpressionTracker = new ImpressionTracker(context);
            mNativeClickHandler = new NativeClickHandler(context);
        }

        void loadAd() {
            mApi.setAdListener(this);
            mApi.setTrackImpression(true);
            mApi.loadAds(new AppnextAdRequest());
        }

        @Override
        public void onAdsLoaded(ArrayList<AppnextAd> list) {
            if (list.isEmpty()) {
                mNativeListener.onNativeAdFailed(NativeErrorCode.EMPTY_AD_RESPONSE);
                return;
            }

            mAd = list.get(0);

            setIconImageUrl(mAd.getImageURL());
            setMainImageUrl(mAd.getImageURL());
            setTitle(mAd.getAdTitle());
            setText(mAd.getAdDescription());

            mNativeListener.onNativeAdLoaded(this);
        }

        @Override
        public void onError(String s) {
            mNativeListener.onNativeAdFailed(NativeErrorCode.EMPTY_AD_RESPONSE);
        }

        @Override
        public void prepare(@NonNull View view) {
            mImpressionTracker.addView(view, this);
            mNativeClickHandler.setOnClickListener(view, this);
        }

        @Override
        public void handleClick(@NonNull View view) {
            notifyAdClicked();
            if (mApi != null && mAd != null) {
                mApi.adClicked(mAd);
            }
        }

        @Override
        public void recordImpression(@NonNull View view) {
            notifyAdImpressed();
            if (mApi != null && mAd != null) {
                mApi.adImpression(mAd);
            }
        }
    }
}