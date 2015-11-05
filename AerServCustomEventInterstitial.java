package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.aerserv.sdk.AerServConfig;
import com.aerserv.sdk.AerServEvent;
import com.aerserv.sdk.AerServEventListener;
import com.aerserv.sdk.AerServInterstitial;

import java.util.List;
import java.util.Map;

public class AerServCustomEventInterstitial extends CustomEventInterstitial implements AerServEventListener {
    private static final String LOG_TAG = AerServCustomEventInterstitial.class.getSimpleName();

    public static final String KEYWORDS = "keywords";
    public static final String PLACEMENT = "placement";
    public static final String TIMEOUT_MILLIS = "timeoutMillis";

    private AerServInterstitial mInterstitial;
    private CustomEventInterstitialListener mInterstitialListener;

    @Override
    protected void loadInterstitial(Context context,
                 CustomEventInterstitialListener customEventInterstitialListener,
                 Map<String, Object> localExtras,
                 Map<String, String> serverExtras) {
        mInterstitialListener = customEventInterstitialListener;

        List<String> keywords;
        String placement;
        Integer timeout;

        if (!(context instanceof Activity)) {
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            keywords = AerServPluginUtil.getStringList(KEYWORDS, serverExtras);
            placement = serverExtras.get(PLACEMENT);
            timeout = AerServPluginUtil.getInteger(TIMEOUT_MILLIS, serverExtras);
        } else {
            Log.w(LOG_TAG, "Cannot load AerServ ad because placement is missing");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        AerServConfig config = new AerServConfig(context, placement);

        config.setEventListener(this);
        config.setPreload(true);

        if (timeout != null) {
            Log.d(LOG_TAG, "Timeout is: " + timeout + " millis");
            config.setTimeout(timeout);
        }

        if (keywords != null) {
            Log.d(LOG_TAG, "Keywords are: " + keywords);
            config.setKeywords(keywords);
        }

        Log.d(LOG_TAG, "Loading interstitial ad");
        mInterstitial = new AerServInterstitial(config);
    }

    @Override
    protected void onInvalidate() {
        if (mInterstitial != null) {
            mInterstitial.kill();
            mInterstitial = null;
        }
    }

    @Override
    protected void showInterstitial() {
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(PLACEMENT);
    }

    /**
     * AerServ AerServEventListener implementation
     */
    @Override
    public void onAerServEvent(AerServEvent event, List<Object> list) {
        switch (event) {
        case PRELOAD_READY:
            Log.d(LOG_TAG, "Interstitial ad loaded");
            mInterstitialListener.onInterstitialLoaded();
            break;

        case AD_FAILED:
            Log.d(LOG_TAG, "Failed to load interstitial ad");
            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
            break;

        case AD_LOADED:
            Log.d(LOG_TAG, "Interstitial ad shown");
            mInterstitialListener.onInterstitialShown();
            break;

        case AD_CLICKED:
            Log.d(LOG_TAG, "Interstitial ad clicked");
            mInterstitialListener.onInterstitialClicked();
            break;

        case AD_DISMISSED:
            Log.d(LOG_TAG, "Interstitial ad dismissed");
            mInterstitialListener.onInterstitialDismissed();
            break;

        case AD_COMPLETED:
            Log.d(LOG_TAG, "Interstitial ad completed");
            mInterstitialListener.onInterstitialDismissed();
            break;

        default:
            Log.d(LOG_TAG, "The following AerServ interstitial ad event cannot be mapped and will be ignored: " + event.name());
        }
    }
}
