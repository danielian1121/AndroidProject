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
public class SaveAOISequence {

	private ArrayList<LOISequenceModel> aoiSequenceList;

	public SaveAOISequence(String response,String from) {
		try {
			//JSON is the JSON code above
			JSONObject jsonResponse = new JSONObject(response);
			Log.d("response",response);
			aoiSequenceList = new ArrayList<>();
			aoiSequenceList.clear();
			JSONArray results;
			if(from.equals("SOI")) {
				JSONArray ja = jsonResponse.getJSONArray("results");
				JSONObject jb = ja.getJSONObject(0);
				results = jb.getJSONArray("POI_set");
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
				aoiSequenceList.add(new LOISequenceModel(poiId, poiTitle, poiLat, poiLong, open,  identifier,contributor));
				Log.d("LOISequence", poiId + " " + poiTitle);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<LOISequenceModel> getAOISequenceList() {
		return aoiSequenceList;
	}
}
