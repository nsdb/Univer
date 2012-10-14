package com.nsdb.univer.data;

import android.app.Activity;
import android.content.SharedPreferences;

public class AppPref {
	
	private static RangeData region;
	private static RangeData univ;
	private static RangeData college;
	private static RangeData major;
	private static String id;
	private static String password;
	private static BookData lastBookData;

	public static void load(Activity activity) {
		System.out.println("AppPref Loaded");
		SharedPreferences pref=activity.getSharedPreferences("AppPref",Activity.MODE_PRIVATE);
		region=new RangeData( pref.getString("region_title",""),pref.getString("region_nick",""),pref.getInt("region_id",-1) );
		univ=new RangeData( pref.getString("univ_title",""),pref.getString("univ_nick",""),pref.getInt("univ_id",-1) );
		college=new RangeData( pref.getString("college_title",""),pref.getString("college_nick",""),pref.getInt("college_id",-1) );
		major=new RangeData( pref.getString("major_title",""),pref.getString("major_nick",""),pref.getInt("major_id",-1) );
		id=pref.getString("id","");
		password="";
		lastBookData=null;
	}
	
	public static void save(Activity activity) {
		System.out.println("AppPref Saved");
		SharedPreferences pref=activity.getSharedPreferences("AppPref",Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit=pref.edit();
		edit.putString("region_title",region.title);
		edit.putString("region_nick",region.nick);
		edit.putInt("region_id",region.id);
		edit.putString("univ_title",univ.title);
		edit.putString("univ_nick",univ.nick);
		edit.putInt("univ_id",univ.id);
		edit.putString("college_title",college.title);
		edit.putString("college_nick",college.nick);
		edit.putInt("college_id",college.id);
		edit.putString("major_title",major.title);
		edit.putString("major_nick",major.nick);
		edit.putInt("major_id",major.id);
		edit.putString("id",id);
		edit.commit();
	}

	public static void setRangeData(String filter,RangeData data) {
		if(filter.compareTo("region")==0) {
			region=data;
		} else if(filter.compareTo("univ")==0) {
			univ=data;
		} else if(filter.compareTo("college")==0) {
			college=data;
		} else if(filter.compareTo("major")==0) {
			major=data;
		}
	}
	
	public static RangeData getRangeData(String filter) {
		if(filter.compareTo("region")==0) {
			return region;
		} else if(filter.compareTo("univ")==0) {
			return univ;
		} else if(filter.compareTo("college")==0) {
			return college;
		} else if(filter.compareTo("major")==0) {
			return major;
		} else {
			return null;
		}
	}
	
	public static void setString(String filter,String value) {
		if(filter.compareTo("id")==0) {
			id=value;
		} else if(filter.compareTo("password")==0) {
			password=value;
		}
	}

	public static String getString(String filter) {
		if(filter.compareTo("id")==0) {
			return id;
		} else if(filter.compareTo("password")==0) {
			return password;
		} else {
			return null;
		}
	}
	
	public static void setLastBookData(BookData data) {
		lastBookData=data;
	}
	public static BookData getLastBookData() {
		return lastBookData;
	}
}
