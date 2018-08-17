package com.mmlab.m1.save_data;

import android.util.Log;


import com.mmlab.m1.model.LOIModel;
import com.mmlab.m1.model.LOISequenceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by waynewei on 2015/11/2.
 */
public class SaveAOI {
	private ArrayList<LOIModel> aoiList;

	public SaveAOI(String response) {
		try {
			//JSON is the JSON code above
			JSONObject jsonResponse = new JSONObject(response);
			aoiList = new ArrayList<>();
			aoiList.clear();
			JSONArray results = jsonResponse.getJSONArray("results");

			for (int i = 0; i < results.length(); i++) {
				JSONObject object = results.getJSONObject(i);
				String loiID = object.getString("AOI_id");
				String loiName = object.getString("AOI_title");
				String loiInfo = object.getString("AOI_description");
				String con = null;
				if (object.has("rights")){
					con = object.getString("rights");
				}
				String identifier = object.getString("identifier");
				SaveAOISequence  sl = new SaveAOISequence(object.toString(),"AOI");
				ArrayList<LOISequenceModel> loiSequenceList = sl.getAOISequenceList();
				aoiList.add(new LOIModel(loiID, loiName,loiInfo, con, identifier,loiSequenceList));
				Log.d("Route", loiID + " " + loiName + " "+loiInfo+" con:"+con );

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<LOIModel> getAOIList() {
		return aoiList;
	}
}
