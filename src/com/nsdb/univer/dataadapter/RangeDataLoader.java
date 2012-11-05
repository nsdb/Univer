package com.nsdb.univer.dataadapter;

import java.util.ArrayList;
import org.jdom2.Element;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nsdb.univer.common.AppPref;
import com.nsdb.univer.common.RangeData;
import com.nsdb.univer.R;
import com.woozzu.android.util.StringMatcher;

public class RangeDataLoader extends BaseDataLoader<RangeData> {

	private String filterRange;
	private String filterTitle;

	public RangeDataLoader(Activity activity, ListView view, boolean inScrollView) {
		super(activity, R.layout.stringdata, view, inScrollView);
		filterRange="";
		filterTitle="";
	}
	
	@Override
	protected BaseListAdapter<RangeData> createListAdapter(Activity activity,
			int dataResourceId, ArrayList<RangeData> dataVisible, ListView view) {
		return new RangeListAdapter(activity,dataResourceId,dataVisible,view);
	}	


	public void updateData(String filterRange) {
		this.filterRange=filterRange;
		super.updateData();
	}

	@Override
	protected String getXmlUrl() {

		// region : 1.234.23.142/~ypunval/feeds/region/
		// univ : 1.234.23.142/~ypunval/feeds/univ/(region_id)/
		// college:1.234.23.142/~ypunval/feeds/collage/(univ_id)/
		// major  :1.234.23.142/~ypunval/feeds/major/(college_id)/
		String url=getActivity().getResources().getString(R.string.base_url)+'/'
				+getActivity().getResources().getString(R.string.get_url)+'/'
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

	public void updateView(String filterTitle) {
		this.filterTitle=filterTitle;
		super.updateView(RESULT_SUCCESS);
	}

	@Override
	protected void updateView(int result) {
		super.updateView(result);		
		
		if(result!=RESULT_SUCCESS) return;

		// filter data
		if(filterTitle.compareTo("")!=0) {
			for(int i=0;i<getDataList().size();i++) {
				if(StringMatcher.match( getDataList().get(i).title, filterTitle )==false)
					getDataList().remove(i--);
			}		
		}

		// sort data
		RangeData[] data=new RangeData[getDataList().size()];
		data=getDataList().toArray(data);
		int c=0;
		RangeData temp;
		while(c<data.length-1) {
			if(data[c].title.compareTo( data[c+1].title )>0) {
				temp=data[c];
				data[c]=data[c+1];
				data[c+1]=temp;
				if(c!=0) c--;
				else c++;
			} else c++;
		}
		getDataList().clear();
		for(int i=0;i<data.length;i++)
			getDataList().add(data[i]);
		
	}

	// for SectionIndexer (IndexableListView)
	private class RangeListAdapter extends BaseListAdapter<RangeData> implements SectionIndexer {

		private String sections = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";

		public RangeListAdapter(Activity activity, int dataResourceId,
				ArrayList<RangeData> objects, ListView view) {
			super(activity, dataResourceId, objects, view);
		}

		@Override
		protected void setView(int position, View v) {
			TextView t=(TextView)v.findViewById(R.id.text);
			t.setText( get(position).title );
		}

		public int getPositionForSection(int section) {
			// If there is no item for current section, previous section will be selected
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getDataList().size(); j++) {
					if (StringMatcher.match(
							String.valueOf(getDataList().get(j).title.charAt(0)),
							String.valueOf(sections.charAt(i)) ) ) {
						return j;
					}
				}
			}
			return 0;
		}

		public int getSectionForPosition(int position) {
			return 0;
		}

		public Object[] getSections() {
			String[] temp = new String[sections.length()];
			for (int i = 0; i < sections.length(); i++)
				temp[i] = String.valueOf(sections.charAt(i));
			System.out.println("getSections : "+temp);
			return temp;
		}

	}


}
