package com.nsdb.univer.dataadapter;

import org.jdom2.Element;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.RangeData;
import com.nsdb.univer.ui.R;

public class RangeDataAdapter extends BaseDataAdapter<RangeData> {

	private String filterRange;
	private String filterTitle;

	public RangeDataAdapter(Activity activity, ListView view, boolean inScrollView) {
		super(activity, R.layout.stringdata, view, inScrollView);
		filterRange="";
		filterTitle="";
	}

	public void updateData(String filterRange) {
		this.filterRange=filterRange;
		super.updateData();
	}

	public void updateView(String filterTitle) {
		this.filterTitle=filterTitle;
		super.updateView();
	}

	@Override
	protected String getXmlUrl() {

		// region : 1.234.23.142/~ypunval/feeds/region/
		// univ : 1.234.23.142/~ypunval/feeds/univ/(region_id)/
		// college:1.234.23.142/~ypunval/feeds/collage/(univ_id)/
		// major  :1.234.23.142/~ypunval/feeds/major/(college_id)/
		String url=activity.getResources().getString(R.string.base_url)+'/'
				+activity.getResources().getString(R.string.get_url)+'/'
				+filterRange+"/";
		if(filterRange.compareTo("univ")==0 && AppPref.getRangeSet().get("region").id != -1)
			url=url+AppPref.getRangeSet().get("region").id+"/";
		else if(filterRange.compareTo("college")==0 && AppPref.getRangeSet().get("univ").id != -1)
			url=url+AppPref.getRangeSet().get("univ").id+"/";
		else if(filterRange.compareTo("major")==0 && AppPref.getRangeSet().get("college").id != -1)
			url=url+AppPref.getRangeSet().get("college").id+"/";

		System.out.println("XML URL : "+url);
		return url;
	}

	@Override
	protected RangeData convertElement(Element item) {
		
		return new RangeData( item.getChild("title").getText(),
							item.getChild("nick").getText(),
							Integer.parseInt(item.getChild("id").getText()) );
	}

	@Override
	protected boolean filterOriginalData(RangeData data) {
		
		if(data.title.contains(filterTitle))
			return true;
		else
			return false;
		
	}

	@Override
	protected void dataViewSetting(int position, View v) {
		
		TextView t=(TextView)v.findViewById(R.id.text);
		t.setText( get(position).title );
	}

}
