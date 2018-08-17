package com.mmlab.m1.save_data;

import android.util.Log;


import com.mmlab.m1.model.LOIModel;
import com.mmlab.m1.model.LOISequenceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by waynewei on 2015/11/2.
 */
public class SaveLOI {
	private ArrayList<LOIModel> loiList;

	public SaveLOI(String response) {
		try {
			//JSON is the JSON code above
			JSONObject jsonResponse = new JSONObject(response);
			loiList = new ArrayList<>();

			JSONArray results = jsonResponse.getJSONArray("results");
			for (int i = 0; i < results.length(); i++) {
				JSONObject object = results.getJSONObject(i);
				String loiID = object.getString("LOI_id");
				String loiName = object.getString("LOI_title");
				//Double time = object.getDouble("duration") / 60;
				//String loiDuration = new DecimalFormat("#0.0").format(time) + " 小時";
				String loiInfo = object.getString("LOI_description");
				String con = null;
				if (object.has("rights")){
					con = object.getString("rights");
				}
				String identifier = object.getString("identifier");
				SaveLOISequence  sl = new SaveLOISequence(object.toString(),"LOI");
				ArrayList<LOISequenceModel> loiSequenceList = sl.getLOISequenceList();
				loiList.add(new LOIModel(loiID, loiName,loiInfo, con, identifier,loiSequenceList));

				Log.d("Route", loiID + " " + loiName + " " + identifier);

			}


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<LOIModel> getLOIList() {
		return loiList;
	}
}
