package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.aerserv.sdk.AerServBanner;
import com.aerserv.sdk.AerServConfig;
import com.aerserv.sdk.AerServEvent;
import com.aerserv.sdk.AerServEventListener;
import com.mopub.common.util.Views;

import java.util.List;
import java.util.Map;

public class AerServCustomEventBanner extends CustomEventBanner implements AerServEventListener {
    private static final String LOG_TAG = AerServCustomEventBanner.class.getSimpleName();

    public static final String KEYWORDS = "keywords";
    public static final String PLACEMENT = "placement";
    public static final String TIMEOUT_MILLIS = "timeoutMillis";

    private AerServBanner mBanner;
    private CustomEventBannerListener mBannerListener;

    @Override
    protected void loadBanner(Context context,
            CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras,
            Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;

        List<String> keywords;
        String placement;
        Integer timeout;

        if (!(context instanceof Activity)) {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (extrasAreValid(serverExtras)) {
            keywords = AerServPluginUtil.getStringList(KEYWORDS, serverExtras);
            placement = serverExtras.get(PLACEMENT);
            timeout = AerServPluginUtil.getInteger(TIMEOUT_MILLIS, serverExtras);
        } else {
            Log.w(LOG_TAG, "Cannot load AerServ ad because placement is missing");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mBanner = new AerServBanner(context);

        AerServConfig config = new AerServConfig(context, placement);

        if (timeout != null) {
            Log.d(LOG_TAG, "Timeout is: " + timeout + " millis");
            config.setTimeout(timeout);
        }

        if (keywords != null) {
            Log.d(LOG_TAG, "Keywords are: " + keywords);
            config.setKeywords(keywords);
        }

        config.setEventListener(this);

        mBanner.configure(config);
        Log.d(LOG_TAG, "Loading banner ad");
        mBanner.show();
    }

    @Override
    protected void onInvalidate() {
        if (mBanner != null) {
            Views.removeFromParent(mBanner);
            mBanner.kill();
            mBanner = null;
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
        case AD_LOADED:
            Log.d(LOG_TAG, "Banner ad loaded");
            mBannerListener.onBannerLoaded(mBanner);
            break;

        case AD_FAILED:
            Log.d(LOG_TAG, "Failed to load banner ad");
            mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
            break;

        case AD_CLICKED:
            Log.d(LOG_TAG, "Banner ad clicked");
            mBannerListener.onBannerClicked();
            break;

        default:
            Log.d(LOG_TAG, "The following AerServ banner ad event cannot be mapped and will be ignored: " + event.name());
        }
    }
}
