package com.wb.nextgenlibrary.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;

import android.content.Context;
import android.content.res.AssetManager;


final public class ResourceUtils {
	
	private ResourceUtils() {
	};
	
	public static String getStringFromAssets(Context context, String fileName) {
		AssetManager am = context.getAssets();
		StringBuilder sb = new StringBuilder();
		InputStream in = null;
		try {
			in = am.open(fileName);
			InputStreamReader is = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();
			
			while (read != null) {
				sb.append(read);
				read = br.readLine();
			}
		} catch (IOException e) {
			NextGenLogger.e(F.TAG, "ResourceUtils.getStringFromAssets: " + e.getMessage(), e);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				NextGenLogger.e(F.TAG, "ResourceUtils.getStringFromAssets: " + e.getMessage(), e);
			}
		}
		return sb.toString();
		
	}
}
