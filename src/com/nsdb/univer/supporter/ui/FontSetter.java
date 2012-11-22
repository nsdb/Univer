package com.nsdb.univer.supporter.ui;

import com.nsdb.univer.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontSetter {

	public static void setDefault(Activity activity) {
        set(activity.getResources().getString(R.string.default_font_url),activity);		
	}
	public static void set(String fontUrl,Activity activity) {
		set(fontUrl,activity,activity.findViewById(android.R.id.content));
	}
	
	public static void setDefault(Context c,View v) {
        set(c.getResources().getString(R.string.default_font_url),c,v);
	}
	public static void set(String fontUrl,Context c,View v) {
		setFontsForAll(fontUrl,c.getAssets(),v);
	}
	
	
	// Thanks for ASCRO!  <http://www.androidpub.com/2371561>
	protected static void setFontsForAll(String fontDirectory,AssetManager manager, View v)
	{
		Typeface font=Typeface.createFromAsset(manager,fontDirectory);
		setFontsForAll(font, v);
	}
	protected static void setFontsForAll(Typeface font, View v)
	{
		if(ViewGroup.class.isInstance(v))
		{
			ViewGroup g = (ViewGroup) v;
			int n = g.getChildCount();
			for(int i=0;i<n;i++)
			{
				setFontsForAll(font, g.getChildAt(i));
			}
		}
		else if(TextView.class.isInstance(v))
		{
			TextView t = (TextView)v;
			t.setTypeface(font);
		}
		return;
	}
}
