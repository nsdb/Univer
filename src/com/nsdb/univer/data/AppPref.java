package com.nsdb.univer.data;

public class AppPref {
	
	private static RangeData region;
	private static RangeData univ;
	private static RangeData college;
	private static RangeData major;
	private static String id;
	private static String password;

	public static void init() {
		region=new RangeData();
		univ=new RangeData();
		college=new RangeData();
		major=new RangeData();
		id="";
		password="";
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
}
