package com.suda.msgcenter.util;

import com.suda.msgcenter.main.SudaApplication;
import com.suda.msgcenter.ui.MaintainMgr;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;


public class URLImageParser implements ImageGetter {

	TextView tv_image;
	ImageWorker iWorker;
	String content;
	Handler mHandler;
	public URLImageParser(TextView t, ImageWorker c, String src, Handler handler) {
		this.tv_image = t;
		iWorker = c;
		content = src;
		mHandler = handler;
	}

	@Override
	public Drawable getDrawable(String source) {
		Bitmap bit =iWorker.loadBitmap(source,SudaApplication.SCREEN_WEIDTH, SudaApplication.SCREEN_WEIDTH);
		if (bit == null) {
			loadBitmapThread(source);
			return null;
		} else {
			Bitmap bm = bit;
			if (bit.getWidth() > SudaApplication.SCREEN_WEIDTH) {
				bm = Bitmap.createScaledBitmap(bit, SudaApplication.SCREEN_WEIDTH,
						SudaApplication.SCREEN_WEIDTH * bit.getHeight() / bit.getWidth(), true);
			}
			Drawable drawable = new BitmapDrawable(bm);
			int w = bm.getWidth();
			int h = bm.getHeight();
			drawable.setBounds(0, 0, w, h);
			return drawable;
		}
	}

	private void loadBitmapThread(final String source) {
		MaintainMgr.getInstance().runBackground(new Runnable() {
			@Override
			public void run() {
				final Bitmap bm = fetchDrawable(source);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (bm != null) {
							Spanned htmlSpan = Html.fromHtml(content, URLImageParser.this, null);
							tv_image.setText(htmlSpan);
						}
					}
				});
			}
		});
	}

	public Bitmap fetchDrawable(String urlString) {
		try {
			Bitmap bit =iWorker.loadBitmap(urlString,SudaApplication.SCREEN_WEIDTH, SudaApplication.SCREEN_WEIDTH);
			return bit;
		} catch (Exception e) {
			return null;
		}
	}
}
