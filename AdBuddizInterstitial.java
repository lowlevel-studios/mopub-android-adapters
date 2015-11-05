package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizError;

import java.util.Map;

import static com.mopub.mobileads.MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;

public class AdBuddizInterstitial extends CustomEventInterstitial implements AdBuddizDelegate {
    private static final String PUBLISHER_KEY = "publisherKey";

	private Activity mActivity;
    private CustomEventInterstitialListener mInterstitialListener;
    private boolean mLoaded = false;

    @Override
    protected void loadInterstitial(Context context,
            CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {        
        mInterstitialListener = customEventInterstitialListener;

        String publisherKey;

        if (!(context instanceof Activity)) {
        	mInterstitialListener.onInterstitialFailed(ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            publisherKey = serverExtras.get(PUBLISHER_KEY);
        } else {
            mInterstitialListener.onInterstitialFailed(ADAPTER_CONFIGURATION_ERROR);
            return;
        }

    	mActivity = (Activity) context;

        AdBuddiz.setPublisherKey(publisherKey);
        AdBuddiz.setDelegate(this);
        AdBuddiz.cacheAds(mActivity);

        if (AdBuddiz.isReadyToShowAd(mActivity)) {
            mInterstitialListener.onInterstitialLoaded();
            mLoaded = true;
        }
    }

    @Override
    protected void onInvalidate() {
    	AdBuddiz.onDestroy();
    }

    @Override
    protected void showInterstitial() {
        if (AdBuddiz.isReadyToShowAd(mActivity)) {
            AdBuddiz.showAd(mActivity);
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(PUBLISHER_KEY);
    }

    /**
     * AdBuddiz AdBuddizDelegate implementation
     */
	@Override
	public void didCacheAd() {
        if (!mLoaded) {
            mInterstitialListener.onInterstitialLoaded();
            mLoaded = true;
        }
	}
	
	@Override
	public void didClick() {
		mInterstitialListener.onInterstitialClicked();
	}

	@Override
	public void didFailToShowAd(AdBuddizError error) {
		switch (error) {
		case NO_MORE_AVAILABLE_ADS:
			mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);	break;
		case NO_NETWORK_AVAILABLE:
			mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR); break;
		default:
			mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
		}
	}

	@Override
	public void didHideAd() {
		mInterstitialListener.onInterstitialDismissed();
	}

	@Override
	public void didShowAd() {
		mInterstitialListener.onInterstitialShown();
	}
}