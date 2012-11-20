package com.nsdb.univer.supporter.data;



import com.nsdb.univer.data.BoardData;
import com.nsdb.univer.data.BookData;
import com.nsdb.univer.data.ProfessorData;
import com.nsdb.univer.data.RangeData;

import android.app.Activity;
import android.content.SharedPreferences;

public class AppPref {
	
	private static RangeSet rangeSet;

	private static String id;
	private static String password;
	private static int user_id;
	private static String value;
	
	private static String cookieName;
	private static String cookieValue;
	private static String cookieDomain;
	private static String cookiePath;
	
	private static BookData lastBookData;
	private static ProfessorData lastProfessorData;
	private static BoardData lastBoardData;
	
	private static int startTab;

	public static void load(Activity activity) {
		System.out.println("AppPref Loaded");
		SharedPreferences pref=activity.getSharedPreferences("AppPref",Activity.MODE_PRIVATE);
		
		RangeData region=new RangeData( pref.getString("region_title",""),pref.getString("region_nick",""),pref.getInt("region_id",-1) );
		RangeData univ=new RangeData( pref.getString("univ_title",""),pref.getString("univ_nick",""),pref.getInt("univ_id",-1) );
		RangeData college=new RangeData( pref.getString("college_title",""),pref.getString("college_nick",""),pref.getInt("college_id",-1) );
		RangeData major=new RangeData( pref.getString("major_title",""),pref.getString("major_nick",""),pref.getInt("major_id",-1) );
		rangeSet=new RangeSet(region,univ,college,major);

		id=pref.getString("id","");
		password=pref.getString("password","");
		user_id=pref.getInt("user_id",-1);
		value=pref.getString("value","");

		cookieName="";
		cookieValue="";
		cookieDomain="";
		cookiePath="";
		
		lastBookData=null;
		lastProfessorData=null;
		lastBoardData=null;
		
		startTab=pref.getInt("startTab",0);
	}
	
	public static void save(Activity activity) {
		System.out.println("AppPref Saved");
		SharedPreferences pref=activity.getSharedPreferences("AppPref",Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit=pref.edit();
		edit.putString("region_title",rangeSet.get("region").title);
		edit.putString("region_nick",rangeSet.get("region").nick);
		edit.putInt("region_id",rangeSet.get("region").id);
		edit.putString("univ_title",rangeSet.get("univ").title);
		edit.putString("univ_nick",rangeSet.get("univ").nick);
		edit.putInt("univ_id",rangeSet.get("univ").id);
		edit.putString("college_title",rangeSet.get("college").title);
		edit.putString("college_nick",rangeSet.get("college").nick);
		edit.putInt("college_id",rangeSet.get("college").id);
		edit.putString("major_title",rangeSet.get("major").title);
		edit.putString("major_nick",rangeSet.get("major").nick);
		edit.putInt("major_id",rangeSet.get("major").id);
		edit.putString("id",id);
		edit.putString("password",password);
		edit.putInt("user_id",user_id);
		edit.putString("value",value);
		edit.putInt("startTab",startTab);
		edit.commit();
	}
	
	public static void setRangeSet(RangeSet set) {
		rangeSet=new RangeSet(set);
	}
	public static RangeSet getRangeSet() {
		return rangeSet;
	}
	
	public static void setString(String filter,String filtervalue) {
		if(filter.compareTo("id")==0) {
			id=filtervalue;
		} else if(filter.compareTo("password")==0) {
			password=filtervalue;
		} else if(filter.compareTo("value")==0) {
			value=filtervalue;
		} else if(filter.compareTo("cookieName")==0) {
			cookieName=filtervalue;
		} else if(filter.compareTo("cookieValue")==0) {
			cookieValue=filtervalue;
		} else if(filter.compareTo("cookieDomain")==0) {
			cookieDomain=filtervalue;
		} else if(filter.compareTo("cookiePath")==0) {
			cookiePath=filtervalue;
		}
	}

	public static String getString(String filter) {
		if(filter.compareTo("id")==0) {
			return id;
		} else if(filter.compareTo("password")==0) {
			return password;
		} else if(filter.compareTo("value")==0) {
			return value;
		} else if(filter.compareTo("cookieName")==0) {
			return cookieName;
		} else if(filter.compareTo("cookieValue")==0) {
			return cookieValue;
		} else if(filter.compareTo("cookieDomain")==0) {
			return cookieDomain;
		} else if(filter.compareTo("cookiePath")==0) {
			return cookiePath;
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

	public static void setLastProfessorData(ProfessorData data) {
		lastProfessorData=data;
	}
	public static ProfessorData getLastProfessorData() {
		return lastProfessorData;
	}
	
	public static void setLastBoardData(BoardData data) {
		lastBoardData=data;
	}
	public static BoardData getLastBoardData() {
		return lastBoardData;
	}

	public static void setInt(String filter,int filtervalue) {
		if(filter.compareTo("startTab")==0) {
			startTab=filtervalue;
		} else if(filter.compareTo("user_id")==0) {
			user_id=filtervalue;
		}
	}
	public static int getInt(String filter) {
		if(filter.compareTo("startTab")==0) {
			return startTab;
		} else if(filter.compareTo("user_id")==0) {
			return user_id;
		} else {
			return -1;
		}
	}
}
