package com.mmlab.m1.save_data;

import android.util.Log;

import com.mmlab.m1.model.LOIModel;
import com.mmlab.m1.model.LOISequenceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 2017/2/13.
 */

public class SaveSOI {
    private ArrayList<LOIModel> soiList;

    public SaveSOI(String response) {
        try {
            //JSON is the JSON code above
            JSONObject jsonResponse = new JSONObject(response);
            soiList = new ArrayList<>();
            soiList.clear();
            JSONArray results = jsonResponse.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject object = results.getJSONObject(i);
                String loiID = object.getString("SOI_id");
                String loiName = object.getString("SOI_title");
                String loiInfo = object.getString("SOI_description");
                String con = null;
                if (object.has("rights")){
                    con = object.getString("rights");
                }
                String identifier = object.getString("identifier");
                SaveSOISequence  sl = new SaveSOISequence(object.toString());
                ArrayList<LOISequenceModel> loiSequenceList = sl.getSOISequenceList();
                soiList.add(new LOIModel(loiID, loiName,loiInfo, con, identifier,loiSequenceList));
                Log.d("Route", loiID + " " + loiName + " "+loiInfo);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<LOIModel> getSOIList() {
        return soiList;
    }

}
