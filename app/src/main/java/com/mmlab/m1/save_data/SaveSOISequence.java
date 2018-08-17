package com.mmlab.m1.save_data;

import android.util.Log;

import com.mmlab.m1.model.LOISequenceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 2017/2/14.
 */

public class SaveSOISequence {
        private ArrayList<LOISequenceModel> soiSequenceList;

        public SaveSOISequence(String response) {
            try {
                //JSON is the JSON code above
                JSONObject jsonResponse = new JSONObject(response);
                Log.d("response",response);
                soiSequenceList = new ArrayList<>();
                soiSequenceList.clear();

                JSONArray results = jsonResponse.getJSONArray("XOI_set");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject object = results.getJSONObject(i);
                    String poiId = object.getString("id");
                    String poiTitle = object.getString("title");
                    Double poiLat = object.getDouble("latitude");
                    Double poiLong = object.getDouble("longitude");
                    String type = object.getString("type");
                    //String poiDescription = object.getString("description");
                    //改API讓他送XOI的open 才可以拿到open
                    Boolean open;
                    if(object.getString("open").equals("1         "))
                        open = true;
                    else if(object.getString("open").equals("0         "))
                        open = false;
                    else
                        open = object.getBoolean("open");
                    String contributor;
                    if(object.has("rights"))
                        contributor = object.getString("rights");
                    else if(object.has("owner"))
                        contributor = object.getString("owner");
                    else
                        contributor = null;
                    String identifier = object.getString("identifier");
                    //int mediaFormat = object.getInt("POI_media_fmt");
                    soiSequenceList.add(new LOISequenceModel(poiId, poiTitle, poiLat, poiLong,open,identifier,type,contributor));
                    Log.d("SOISequence", poiId + " " + poiTitle);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public ArrayList<LOISequenceModel> getSOISequenceList() {
            return soiSequenceList;
        }
}
