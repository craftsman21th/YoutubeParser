package com.moder.compass.stats.upload;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghelong on 2018/10/26.<br/>
 * Email: wanghelong
 */
public class StatsCache {

    private String key;
    private int count;
    private List<CountTimePair> countTimePairs = new ArrayList<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public void addCount(int count) {
        this.count += count;
        CountTimePair countTimePair = new CountTimePair();
        countTimePair.count = count;
        countTimePair.time = System.currentTimeMillis();
        countTimePairs.add(countTimePair);
    }

    public String getCountTimePairs() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (CountTimePair countTimePair : countTimePairs) {
            jsonObject = new JSONObject();
            try {
                jsonObject.put(String.valueOf(countTimePair.count), countTimePair.time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    /**
     * 数量-时间对，某次行为可能得count值存在大于1的情况，这视为同一个操作，此处为此次操作的对应时间
     */
    private class CountTimePair {
        private int count;
        private long time;
    }

}
