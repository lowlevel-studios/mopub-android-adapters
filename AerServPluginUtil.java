package com.mopub.mobileads;

import com.mopub.common.util.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AerServPluginUtil {
    public static Integer getInteger(String key, Map<String, String> serverExtras) {
        try {
            String s = serverExtras.get(key);
            return (s != null) ? Integer.parseInt(s) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> getStringList(String key, Map<String, String> serverExtras) {
        String s = serverExtras.get(key);
        if (s == null)
            return null;

        String[] list = Json.jsonArrayToStringArray(s);
        return new ArrayList<>(Arrays.asList(list));
    }
}
