package com.nsdb.univer.supporter.ui;

import java.net.URL;

import com.nsdb.univer.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageSetterNoCache extends AsyncTask<Void,Void,Bitmap> {
	
	private String url;
	private ImageView view;
	
	public ImageSetterNoCache(String url,ImageView view) {
		this.url=url;
		this.view=view;
		view.setImageResource(R.drawable.com_image);
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		
		try {
			URL url = new URL(this.url);
			BitmapFactory.Options option=new BitmapFactory.Options();
			Rect rect=new Rect(-1,-1,-1,-1);
			
			// get width, height
			option.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(url.openStream(),rect,option);
			int width=option.outWidth;
			int height=option.outHeight;
			
			// check sample size
			if(width*height > 2048*2048) {
				option.inSampleSize=8;				
			} else if(width*height > 1024*1024) {
				option.inSampleSize=4;
			} else if(width*height > 512*512) {
				option.inSampleSize=2;
			} else {
				option.inSampleSize=1;
			}
			
			// get Bitmap		
			option.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeStream(url.openStream(),rect,option);

			return bitmap;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		view.setImageBitmap(result);
	}
	
	

}
