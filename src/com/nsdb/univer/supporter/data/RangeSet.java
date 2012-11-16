package com.nsdb.univer.supporter.data;

import com.nsdb.univer.data.RangeData;

import android.widget.Button;
import android.widget.TextView;

public class RangeSet {
	private RangeData region;
	private RangeData univ;
	private RangeData college;
	private RangeData major;
	public RangeSet() {
		region=new RangeData();
		univ=new RangeData();
		college=new RangeData();
		major=new RangeData();
	}
	public RangeSet(RangeData region,RangeData univ,RangeData college,RangeData major) {
		this.region=new RangeData(region);
		this.univ=new RangeData(univ);
		this.college=new RangeData(college);
		this.major=new RangeData(major);
	}
	public RangeSet(RangeSet set) {
		this.region=new RangeData(set.get("region"));
		this.univ=new RangeData(set.get("univ"));
		this.college=new RangeData(set.get("college"));
		this.major=new RangeData(set.get("major"));		
	}
	
	public void set(String filter,RangeData data) {
		if(filter.compareTo("region")==0) {
			region=new RangeData(data);
			univ=new RangeData();
			college=new RangeData();
			major=new RangeData();
		} else if(filter.compareTo("univ")==0) {
			univ=new RangeData(data);
			college=new RangeData();
			major=new RangeData();
		} else if(filter.compareTo("college")==0) {
			college=new RangeData(data);
			major=new RangeData();
		} else if(filter.compareTo("major")==0) {
			major=new RangeData();
		} else
			System.out.println("RangeDataSet received Invalid filter");
	}
	public RangeData get(String filter) {
		if(filter.compareTo("region")==0) {
			return region;
		} else if(filter.compareTo("univ")==0) {
			return univ;
		} else if(filter.compareTo("college")==0) {
			return college;
		} else if(filter.compareTo("major")==0) {
			return major;
		} else {
			System.out.println("RangeDataSet received Invalid filter");
			return null;
		}
	}
	
	public void applyDataToView(Button r,Button u) {

    	if(region.nick.compareTo("")==0) {
        	r.setText("지역");
    		u.setEnabled(false);
    	} else {
        	r.setText(region.nick);
    		u.setEnabled(true);
    	}
    	if(univ.nick.compareTo("")==0) {
        	u.setText("대학교");
    	} else {
        	u.setText(univ.nick);
    	}
		
	}
	public void applyDataToView(Button r,Button u,Button c,Button m) {

    	if(region.nick.compareTo("")==0) {
        	r.setText("지역");
    		u.setEnabled(false);
    	} else {
        	r.setText(region.nick);
    		u.setEnabled(true);
    	}
    	if(univ.nick.compareTo("")==0) {
        	u.setText("대학교");
    		c.setEnabled(false);
    	} else {
        	u.setText(univ.nick);
    		c.setEnabled(true);
    	}
    	if(college.nick.compareTo("")==0) {
        	c.setText("단과대");
    		m.setEnabled(false);
    	} else {
        	c.setText(college.nick);
    		m.setEnabled(true);
    	}
    	if(major.nick.compareTo("")==0) {
        	m.setText("학과");
    	} else {
        	m.setText(major.nick);
    	}
    	////
    	m.setEnabled(false);
		
	}
	public void applyDataToView(TextView r,TextView u,TextView c,TextView m,Button rb,Button ub,Button cb,Button mb) {
		
    	if(region.title.compareTo("")==0) {
    		r.setText("없음");
    		ub.setEnabled(false);
    	} else {
        	r.setText(region.title);
    		ub.setEnabled(true);    		
    	}
    	if(univ.title.compareTo("")==0) {
    		u.setText("없음");
			cb.setEnabled(false);
		} else {
    		u.setText(univ.title);
			cb.setEnabled(true);		
		}
    	if(college.title.compareTo("")==0) {
    		c.setText("없음");
			mb.setEnabled(false);
		} else {
    		c.setText(college.title);
			mb.setEnabled(true);		
		}
    	if(major.title.compareTo("")==0) {
    		m.setText("없음");
		} else {
    		m.setText(major.title);
		}
    	////
    	mb.setEnabled(false);
		
	}
	
	public boolean isFilled() {
		return college.id!=-1; // major.id!=-1;
	}

}
