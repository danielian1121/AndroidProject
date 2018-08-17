package com.mmlab.m1.model;



import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by waynewei on 2015/10/25.
 */
public class MyFavorite extends RealmObject {

	private String title;
	private String id;
	private String pic;
	private String info;
	private String address;
	private Double favlat ;
	private Double favlong ;

	public MyFavorite(){
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}




	public Double getFavlat() {
		return favlat;
	}

	public void setFavlat(Double mfavlat) {
		this.favlat = mfavlat;
	}

	public Double getFavlong() {
		return favlong;
	}

	public void setFavlong(Double mfavlong) {
		this.favlong = mfavlong;
	}



	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}



	public void setId(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setPic(String pic){
		this.pic = pic;
	}

	public String getPic() {
		return pic;
	}
}
