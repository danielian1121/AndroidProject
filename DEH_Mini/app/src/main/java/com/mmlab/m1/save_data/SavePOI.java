package com.mmlab.m1.save_data;

import android.util.Log;

import com.mmlab.m1.MyApplication;
import com.mmlab.m1.model.POIModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by waynewei on 2015/11/2.
 */
public class SavePOI {

	private ArrayList<POIModel> poiList;
    private String type;
    private static String language;
//	public SavePOI(String response) {
//		try {
//			//JSON is the JSON code above
//			JSONObject jsonResponse = new JSONObject(response);
//			poiList = new ArrayList<>();
//			poiList.clear();
//			JSONArray results = jsonResponse.getJSONArray("results");
//			for (int i = 0; i < results.length(); i++) {
//				JSONObject object = results.getJSONObject(i);
//				String poiID = object.getString("POI_id");
//				String poiName = object.getString("POI_title");
//				String poiInfo = object.getString("POI_description");
//				Double distance = object.getDouble("distance")/1000;
//				String poiDistance = new DecimalFormat("#0.0").format(distance) + " 公里";
//				String poiAddress = object.getString("POI_address");
//				String poiSubject = object.getString("subject");
//				String poiType1 = object.getString("type1");
//
//				ArrayList<String> poiKeywords = new ArrayList<>();
//
//				String keyword1 = object.getString("keyword1");
//				if (!keyword1.isEmpty()) poiKeywords.add(keyword1);
//				String keyword2 = object.getString("keyword2");
//				if (!keyword2.isEmpty()) poiKeywords.add(keyword2);
//				String keyword3 = object.getString("keyword3");
//				if (!keyword3.isEmpty()) poiKeywords.add(keyword3);
//				String keyword4 = object.getString("keyword4");
//				if (!keyword4.isEmpty()) poiKeywords.add(keyword4);
//				String keyword5 = object.getString("keyword5");
//				if (!keyword5.isEmpty()) poiKeywords.add(keyword5);
//
//				String poiLatitude = object.getString("latitude");
//				String poiLongitude = object.getString("longitude");
//
//				String poiSource = object.getString("POI_source");
//
//				JSONArray pic = object.getJSONObject("PICs").getJSONArray("pic");
//				pics = new ArrayList<>();
//				audios = new ArrayList<>();
//				movies = new ArrayList<>();
//
//				for (int j = 0; j < pic.length(); j++) {
//					JSONObject obj = pic.getJSONObject(j);
//					poiPicUrl = obj.getString("url");
//					if (poiPicUrl.matches("^(.*\\.((jpg)$))?[^.]*$")) {
////						Log.d("pic", poiPicUrl);
//						pics.add(poiPicUrl);
//					} else if (poiPicUrl.matches("^(.*\\.((aac)$))?[^.]*$")) {
//						Log.d("aac", poiPicUrl);
//						audios.add(poiPicUrl);
//					} else if (poiPicUrl.matches("^(.*\\.((mp4)$))?[^.]*$")) {
//						Log.d("mp4", poiPicUrl);
//						movies.add(poiPicUrl);
//					}
//
//				}
//
//				poiList.add(new POIModel(poiID, poiName, poiDistance, poiSubject, poiType1, poiAddress, poiInfo, poiLatitude, poiLongitude, poiSource, poiKeywords, pics, audios, movies));
//				Log.d("POI", poiName + " " + pics + " " + audios + " " + poiSource);
//
//			}
//
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
    public void setType(String t){
        this.type = t;
    }

	public POIModel parsePoiJSONObject(String result){
		POIModel poiModel = new POIModel();
		try {
            JSONObject object = new JSONObject(result);
            //if (object.getString("language").equals(language)){
                poiModel.setJsonObject(object);
                poiModel.setPOIId(object.getString("POI_id"));
                poiModel.setPOIName(object.getString("POI_title"));
                poiModel.setPOIInfo(object.getString("POI_description"));
                Double distance;
                if (object.has("distance")) {
                    distance = object.getDouble("distance") * 1000;
                    poiModel.setPOIDistance(new DecimalFormat("#0.0").format(distance));
                }
                poiModel.setPOIAddress(object.getString("POI_address"));
                language = Locale.getDefault().getDisplayLanguage();
                if (language.equals("English")){
                    if(object.getString("subject").equals("體驗的")||object.getString("subject").equals("Experiential"))
                        poiModel.setPOISubject("Experiential");
                    else if(object.getString("subject").equals("活化再造的")||object.getString("subject").equals("Activation and Reconstructed"))
                        poiModel.setPOISubject("Activation and Reconstructed");
                    else
                        poiModel.setPOISubject("Disappeared");
                    if(object.getString("format").equals("古蹟、歷史建築、聚落")||object.getString("format").equals("Historical Site, Historical Buildings, Village"))
                        poiModel.setPOIType1("Historical Site, Historical Buildings, Village");
                    else if(object.getString("format").equals("遺址")||object.getString("format").equals("Ruins"))
                        poiModel.setPOIType1("Ruins");
                    else if(object.getString("format").equals("文化景觀")||object.getString("format").equals("Culture Landscape"))
                        poiModel.setPOIType1("Culture Landscape");
                    else if(object.getString("format").equals("傳統藝術")||object.getString("format").equals("Traditional Art"))
                        poiModel.setPOIType1("Traditional Art");
                    else if(object.getString("format").equals("民俗及相關文物")||object.getString("format").equals("Folk Customs and Relevant Cultural Artifacts"))
                        poiModel.setPOIType1("Folk Customs and Relevant Cultural Artifacts");
                    else if(object.getString("format").equals("古物")||object.getString("format").equals("Antique"))
                        poiModel.setPOIType1("Antique");
                    else if(object.getString("format").equals("食衣住行育樂")||object.getString("format").equals("Food & Drink, Lodging, Reansportation, Infotainment"))
                        poiModel.setPOIType1("Food & Drink, Lodging, Reansportation, Infotainment");
                    else
                        poiModel.setPOIType1("Others");
                }
                else if (language.equals("中文")){
                    poiModel.setPOISubject(object.getString("subject"));
                    poiModel.setPOIType1(object.getString("format"));
                }
                else if (language.equals("日本語")){
                    if(object.getString("subject").equals("體驗的")||object.getString("subject").equals("現存している"))
                        poiModel.setPOISubject("現存している");
                    else if(object.getString("subject").equals("活化再造的")||object.getString("subject").equals("再建と復建による他目的活用"))
                        poiModel.setPOISubject("再建と復建による他目的活用");
                    else
                        poiModel.setPOISubject("現存していない");
                    if(object.getString("format").equals("古蹟、歷史建築、聚落")||object.getString("format").equals("古跡、歴史建築、集落"))
                        poiModel.setPOIType1("古跡、歴史建築、集落");
                    else if(object.getString("format").equals("遺址")||object.getString("format").equals("遺跡"))
                        poiModel.setPOIType1("遺跡");
                    else if(object.getString("format").equals("文化景觀")||object.getString("format").equals("文化景観"))
                        poiModel.setPOIType1("文化景観");
                    else if(object.getString("format").equals("傳統藝術")||object.getString("format").equals("伝統的な工芸品"))
                        poiModel.setPOIType1("伝統的な工芸品");
                    else if(object.getString("format").equals("民俗及相關文物")||object.getString("format").equals("習俗等に関する文物"))
                        poiModel.setPOIType1("習俗等に関する文物");
                    else if(object.getString("format").equals("古物")||object.getString("format").equals("昔の物"))
                        poiModel.setPOIType1("Antique");
                    else if(object.getString("format").equals("食衣住行育樂")||object.getString("format").equals("衣食住および日々の楽しみなど"))
                        poiModel.setPOIType1("衣食住および日々の楽しみなど");
                    else
                        poiModel.setPOIType1("その他");
                }


//				Log.d("test", object.getString("type1")+"");
//				Log.d("test",result);
                if (object.getString("identifier").equals("docent") || object.getString("identifier").equals("partner"))
                    poiModel.setIdentifier("narrator");
                else
                    poiModel.setIdentifier(object.getString("identifier"));

                Log.d("format", object.getString("format"));

                //poiModel.setContributor(object.getString("contributor"));

                poiModel.setOpen(object.getString("open"));
                ArrayList<String> poiKeywords = new ArrayList<>();

                String keyword1 = object.getString("keyword1");
                if (!keyword1.isEmpty()) poiKeywords.add(keyword1);
				/*String keyword2 = object.getString("keyword2");
				if (!keyword2.isEmpty()) poiKeywords.add(keyword2);
				String keyword3 = object.getString("keyword3");
				if (!keyword3.isEmpty()) poiKeywords.add(keyword3);
				String keyword4 = object.getString("keyword4");
				if (!keyword4.isEmpty()) poiKeywords.add(keyword4);
				String keyword5 = object.getString("keyword5");
				if (!keyword5.isEmpty()) poiKeywords.add(keyword5);*/

                poiModel.setPOIKeywords(poiKeywords);

                poiModel.setPOILat(object.getDouble("latitude"));
                poiModel.setPOILong(object.getDouble("longitude"));


                ArrayList<String> urls = new ArrayList<>();
                ArrayList<String> pics = new ArrayList<>();
                ArrayList<String> audios = new ArrayList<>();
                ArrayList<String> movies = new ArrayList<>();
                ArrayList<String> audio_tours = new ArrayList<>();
                ArrayList<String> mediaFormat = new ArrayList<>();
                //poiModel.setPOISource(object.getString("POI_source"));
                if (object.has("media_set")) {
                    JSONArray pic = object.getJSONArray("media_set");


                for (int j = 0; j < pic.length(); j++) {
                    JSONObject obj = pic.getJSONObject(j);
                    String poiPicUrl = obj.getString("media_url");
                    int media_fmt = obj.getInt("media_format");
                    urls.add(poiPicUrl);
//					if (poiPicUrl.matches("^(.*\\.((jpg)$))?[^.]*$")) {
//						pics.add(poiPicUrl);
//						poiModel.setMedia("photo");
//					} else if (poiPicUrl.matches("^(.*\\.((aac)$))?[^.]*$")) {
//						audios.add(poiPicUrl);
//						poiModel.setMedia("audio");
//					} else if (poiPicUrl.matches("^(.*\\.((mp4)$))?[^.]*$")) {
//						movies.add(poiPicUrl);
//						poiModel.setMedia("movie");
//					}
                    if (media_fmt == 1) {
                        pics.add(poiPicUrl);
                        mediaFormat.add("photo");
//						poiModel.setMedia("photo");
                    } else if (media_fmt == 2) {
                        audios.add(poiPicUrl);
                        mediaFormat.add("audio");
//						poiModel.setMedia("audio");
                    } else if (media_fmt == 4) {
                        movies.add(poiPicUrl);
                        mediaFormat.add("movie");
//						poiModel.setMedia("movie");
                    } else if (media_fmt == 8) {
                        audio_tours.add(poiPicUrl);
                        mediaFormat.add("audio tour");
                    }
                }
            }
            poiModel.setMedia(mediaFormat);
            poiModel.setUrl(urls);
            poiModel.setPOIPics(pics);
            poiModel.setPOIAudios(audios);
            poiModel.setPOIMovies(movies);
            poiModel.setPOIAudioTours(audio_tours);

            Log.d("poi", poiModel.getPOIName() + poiModel.getMedia() + " " + poiModel.getIdentifier());
        //}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return poiModel;
	}

	public void parsePoiListJSONObject(String response) {
		try {
			//JSON is the JSON code above
			JSONObject jsonResponse = new JSONObject(response);
			poiList = new ArrayList<>();
			poiList.clear();
            type = "";
            String language = Locale.getDefault().getDisplayLanguage();
            if (language.equals("English"))
                language = "英文";
            else if (language.equals("中文"))
                language = "中文";
            else if (language.equals("日本語"))
                language = "日文";
			JSONArray results = jsonResponse.getJSONArray("results");
			for (int i = 0; i < results.length(); i++) {
				JSONObject poiObject = results.getJSONObject(i);
                if(poiObject.getString("language").equals(language)||this.type.equals("Fav"))
                //Log.d("lan:",poiObject.getString("language"));
                    poiList.add(parsePoiJSONObject(poiObject.toString()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<POIModel> getPOIList() {
		return poiList;
	}
}
