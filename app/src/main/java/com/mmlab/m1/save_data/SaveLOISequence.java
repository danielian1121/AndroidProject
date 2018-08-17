package com.mmlab.m1.save_data;

import android.util.Log;


import com.mmlab.m1.model.LOISequenceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by waynewei on 2015/11/2.
 */
public class SaveLOISequence {

    private ArrayList<LOISequenceModel> loiSequenceList;

    public SaveLOISequence(String response,String from) {
        try {
            //JSON is the JSON code above
            JSONObject jsonResponse = new JSONObject(response);
            loiSequenceList = new ArrayList<>();
            loiSequenceList.clear();
            JSONArray results;
            if(from.equals("SOI")) {
                JSONArray j = jsonResponse.getJSONArray("results");
                JSONObject k = j.getJSONObject(0);
                results = k.getJSONArray("POI_set");
            }else
                results = jsonResponse.getJSONArray("POI_set");
            for (int i = 0; i < results.length(); i++) {

                JSONObject object = results.getJSONObject(i);
                String poiId = object.getString("id");
                String poiTitle = object.getString("title");
                Double poiLat = object.getDouble("latitude");
                Double poiLong = object.getDouble("longitude");
                //String poiDescription = object.getString("description");
                String open = object.getString("open");
                String contributor = object.getString("rights");
                String identifier = object.getString("identifier");
                //int mediaFormat = object.getInt("POI_media_fmt");
                loiSequenceList.add(new LOISequenceModel(poiId, poiTitle, poiLat, poiLong, open, identifier,contributor));
//                Log.d("LOISequence", poiTitle + " " + poiLat + " " + poiLong);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<LOISequenceModel> getLOISequenceList() {
        return loiSequenceList;
    }
}
