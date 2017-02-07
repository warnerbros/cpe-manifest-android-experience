package com.wb.nextgenlibrary.util.utils;

import android.content.Context;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

/**
 * Created by gzcheng on 12/20/16.
 */

public class NextGenGlide{
	public static DrawableTypeRequest<GlideUrl> load(Context context, String url){
		if (StringHelper.isEmpty(url)){
			GlideUrl glideUrl = new GlideUrl("http://www.wb.com/pink.jpg", new LazyHeaders.Builder().addHeader("package_name", context.getPackageName()).build());
			return Glide.with(context).load(glideUrl);

		} else {

			GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder().addHeader("package_name", context.getPackageName()).build());
			return Glide.with(context).load(glideUrl);
		}
	}
}
