package com.wb.nextgen.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;

import android.content.res.AssetManager;


final public class ResourceUtils {
	
	private ResourceUtils() {
	};
	
	public static String getStringFromAssets(String fileName) {
		AssetManager am = NextGenApplication.getContext().getAssets();
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
