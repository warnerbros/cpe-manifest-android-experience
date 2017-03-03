package com.wb.nextgenlibrary.network;

import android.content.Context;
import android.os.Environment;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.storage.ExternalStorage;
import com.wb.nextgenlibrary.util.HttpHelper;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;


public class NGECacheManager {
	
	public static final int STATE_INVALID = 0;
	public static final int STATE_WRITING = 1;
	public static final int STATE_VALID = 2;
	public static final long CACHE_TRIMPADDING = 5000; // When trimming cahce, trim an extra 5K each time
	public static long sCacheLimit = 0;
	
	public static final int POLICY_OFF = 0;
	public static final int POLICY_SMALL = 1;
	public static final int POLICY_MEDIUM = 2;
	public static final int POLICY_LARGE = 3;
	
	// public final String cachePath = "/sdcard/data/net.flixster/";
	private static HashMap<Integer, CacheItem> sCacheHash; // < key= urlHashCode, value = index in ArrayList
	
	private Context mContext;
	private static FileOutputStream sFileOutputStream;
	private static FileInputStream sFileInputStream;
	private static CacheItem sHead = null;
	private static CacheItem sTail = null;
	private static int sCacheItemSize = 0;
	private static long sCacheByteSize = 0;
	private static File sCacheDir;
	private static TimerTask sTimerTask;
	private static Timer sTimer;
	
	private static boolean sIsActive = false;
	
	public NGECacheManager(Context applicationContext) {
		mContext = applicationContext;
		sCacheHash = new LinkedHashMap<Integer, CacheItem>(100, (float) 0.5, true);
		
		// sTimer = new Timer();
		//
		// sTimerTask = new TimerTask() {
		// public void run() {
		NextGenLogger.v(F.TAG,
				"NGECacheManager MEDIA Mounted =" + Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED) + "");
		
		// create cache directory
		NextGenLogger.d(F.TAG,
				"NGECacheManager Environment.getExternalStorageState():" + Environment.getExternalStorageState() + " sCacheLimit:"
						+ sCacheLimit);
		boolean activeState = false;
		if (Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED)) {
			File sdDir = ExternalStorage.getExternalFilesDir(F.CACHE_DIR);
			NextGenLogger.d(F.TAG, "NGECacheManager sdDir:" + sdDir);
			sCacheDir = new File(sdDir.toString());
			if (sCacheDir.exists() && sCacheDir.isDirectory() && sCacheDir.canWrite()) {
				NextGenLogger.v(F.TAG, "NGECacheManager  mCacheDir exists == true");
				buildCacheIndex();
				activeState = true;
			} else {
				NextGenLogger.v(F.TAG, "NGECacheManager mCacheDir mkdirs()");
				if (sCacheDir.mkdirs()) {
					activeState = true;
				}
			}
			
			setCacheLimit();
			// TrimCache(0L);
			sIsActive = activeState;
			NextGenLogger.v(F.TAG, "NGECacheManager active: " + sIsActive);
			
		} else {
			NextGenLogger.e(F.TAG, "NGECacheManager SDCARD not avalable!");
		}
		// }
		// };
		
		// sTimer.schedule(sTimerTask, 10);
		
	}
	
	public static void setCacheLimit() {
		NextGenLogger.v(F.TAG, "NGECacheManager SetCacheLimit() before sCacheLimit:" + sCacheLimit + " getCachePolicy:"
				+ NextGenExperience.getCachePolicy());
		switch (NextGenExperience.getCachePolicy()) {
		case POLICY_OFF:
			sCacheLimit = 0;
			break;
		case POLICY_SMALL:
			sCacheLimit = 2000000;
			break;
		case POLICY_MEDIUM:
			sCacheLimit = 4000000;
			break;
		case POLICY_LARGE:
			sCacheLimit = 80000000;
			break;
		default:
			sCacheLimit = 0;
		}
		NextGenLogger.v(F.TAG, "NGECacheManager SetCacheLimit() after sCacheLimit:" + sCacheLimit);
	}
	
	public void buildCacheIndex() {
		NextGenLogger.v(F.TAG, "NGECacheManager.buildCacheIndex start");
		
		// sCacheDir = mContext.getFilesDir();
		sCacheDir = ExternalStorage.getExternalFilesDir(F.CACHE_DIR);
		File fileObj;
		String files[] = sCacheDir.list();
		
		NextGenLogger.v(F.TAG, "NGECacheManager.buildCacheIndex files:" + files);
		if (files != null) {
			CacheItem tempItem;
			for (String file : files) {
				// Loggerflx.v(F.TAG, "NGECacheManager.buildCacheIndex file:" + file +
				// " mCacheByteSize:"
				// + sCacheByteSize + " mCacheItemSize:" + sCacheItemSize +" sCacheLimit:"+sCacheLimit);
				if (file.startsWith("cache")) {
					fileObj = new File(sCacheDir, file);
					// fileObj = mContext.getFileStreamPath(file);
					tempItem = new CacheItem();
					tempItem.size = fileObj.length();
					tempItem.state = STATE_VALID;
					tempItem.key = FilenameToHash(file);
					AppendCacheItem(tempItem);
				}
			}
		}
		
		NextGenLogger.v(F.TAG, "NGECacheManager.buildCacheIndex mCacheByteSize:" + sCacheByteSize + " mCacheItemSize:"
				+ sCacheItemSize + " sCacheLimit:" + sCacheLimit);
		
	}
	
	public byte[] get(int urlHashCode) {
		String filename = "cache" + Integer.toString(urlHashCode).replace('-', '_');
		// Loggerflx.v(F.TAG, "NGECacheManager get filename:" + filename + " mCacheByteSize:" +
		// sCacheByteSize
		// + " mCacheItemSize:" + sCacheItemSize+" sCacheLimit:"+sCacheLimit);
		if (!sIsActive) {
			NextGenLogger.e(F.TAG, "NGECacheManager.get NOT ACTIVE YET");
			return null;
		}
		try {
			if (sCacheHash.containsKey(urlHashCode)) {
				// Loggerflx.v(F.TAG, "NGECacheManager.get hit urlHashCode:" + urlHashCode);
				// Loggerflx.v(F.TAG, "NGECacheManager filename:" + filename);
				File fileObj = new File(sCacheDir, filename);
				if (!fileObj.exists()) {
					fileObj.createNewFile();
				}
				sFileInputStream = new FileInputStream(fileObj);
				byte[] results = HttpHelper.streamToByteArray(sFileInputStream);
				sFileInputStream.close();
				
				// ADD the LRU part
				// SendToTail(mCacheHash.get(urlHashCode));
				
				return results;
			}
		} catch (FileNotFoundException e) {
		 	//NextGenLogger.e(F.TAG, "NGECacheManager.get", e);
			
		} catch (IOException e) {
			//NextGenLogger.e(F.TAG, "NGECacheManager.get", e);
		} catch (Exception ex){
			//NextGenLogger.e(F.TAG, "NGECacheManager.get", ex);

		}
		return null;
	}
	
	public synchronized static void TrimCache(long trimSize) {
		// /////////////////////////////////
		// Trim Cache on disk if needed....
		
		if (trimSize > sCacheLimit) {
			NextGenLogger.w(F.TAG, "trimSize size exceeds cache size!!");
			return;
		}
		
		File fileObj;
		
		if (sCacheByteSize + trimSize + CACHE_TRIMPADDING > sCacheLimit) {
			while (sCacheByteSize + trimSize + CACHE_TRIMPADDING > sCacheLimit && sHead != null) {
				// attempt file op 1st!
				fileObj = new File(sCacheDir, HashToFilename(sHead.key));
				fileObj.delete();
				
				// if successful, mange queue part...
				DecapCacheItem(sHead);
			}
			NextGenLogger.v(F.TAG, "NGECacheManager.put TRIM, mCacheByteSize:" + sCacheByteSize + " mCacheItemSize:"
					+ sCacheItemSize + " sCacheLimit:" + sCacheLimit);
		}
	}
	
	public synchronized static void clearCache() {
		String[] children = ExternalStorage.getExternalFilesDir(F.CACHE_DIR).list();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
					new File(ExternalStorage.getExternalFilesDir(F.CACHE_DIR) + "/" + children[i]).delete();
			}
		}
	}
	
	public synchronized void put(int urlHashCode, byte[] byteArray) {
		String filename = "cache" + Integer.toString(urlHashCode).replace('-', '_');
		// Loggerflx.v(F.TAG, "NGECacheManager put filename:" + filename + " byteArray.length:" +
		// byteArray.length
		// + " sCacheLimit:" + sCacheLimit);
		
		if (!sIsActive) {
			NextGenLogger.v(F.TAG, "NGECacheManager.put NOT ACTIVE YET byteArray.length:" + byteArray.length);
			return;
		}
		
		if (sCacheLimit == 0) { // special case
			return;
		}
		// TrimCache(byteArray.length + CACHE_TRIMPADDING);
		
		// Write Cache file, and insert into index
		CacheItem tempItem;
		File fileObj;
		
		try {
			// first write to disk...
			fileObj = new File(sCacheDir, filename);
			sFileOutputStream = new FileOutputStream(fileObj);
			
			// sFileOutputStream = mContext.openFileOutput(filename, Context.MODE_WORLD_READABLE);
			sFileOutputStream.write(byteArray, 0, byteArray.length);
			sFileOutputStream.flush();
			
			tempItem = new CacheItem();
			tempItem.key = urlHashCode;
			tempItem.state = STATE_VALID;
			tempItem.size = byteArray.length;
			AppendCacheItem(tempItem);
			
			// Loggerflx.v(F.TAG, "NGECacheManager put success. filename:" + filename);
		} catch (FileNotFoundException e) {
			NextGenLogger.v(F.TAG, "NGECacheManager put FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			NextGenLogger.v(F.TAG, "NGECacheManager put IOException");
			e.printStackTrace();
		} finally {
			if (sFileOutputStream != null) {
				try {
					sFileOutputStream.close();
				} catch (IOException e) {
					/* Ignored */
				}
			}
		}
	}
	
	private void AppendCacheItem(CacheItem item) {
		// Loggerflx.v(F.TAG, "NGECacheManager.AppendCacheItem item.size:" + item.size);
		
		if (sHead == null) {
			sHead = item;
			sTail = sHead;
		} else {
			item.next = null;
			item.prev = sTail;
			sTail.next = item;
			sTail = item;
		}
		sCacheHash.put(item.key, sTail);
		sCacheByteSize = sCacheByteSize + item.size;
		sCacheItemSize++;
	}
	
	private static void DecapCacheItem(CacheItem topItem) {
		// Loggerflx.v(F.TAG,
		// "NGECacheManager.DecapCacheItem mHead:"+mHead+" mTail:"+mTail+"mCacheHash.size:"+mCacheHash.size());
		if (topItem == null) {
			NextGenLogger.e(F.TAG, "NGECacheManager.DecapCacheItem topItem is null");
			return;
		} else if (sHead == sTail) { // special case of only one item
			NextGenLogger.v(F.TAG, "NGECacheManager.DecapCacheItem empty? mHead:" + sHead + " tail:" + sTail);
			sHead = null;
			sTail = null;
		} else {
			sHead = topItem.next;
			sHead.prev = null;
		}
		sCacheHash.remove(topItem);
		sCacheByteSize = sCacheByteSize - topItem.size;
		sCacheItemSize--;
	}
	
	// private void SendToTail(CacheItem item) {
	//
	// }
	
	public static String HashToFilename(int hashCode) {
		return "cache" + Integer.toString(hashCode).replace('-', '_');
	}
	
	public static int FilenameToHash(String filename) {
		String numberString = filename.substring(5);
		return Integer.valueOf(numberString.replace("_", "-"));
	}
	
	class CacheItem {
		int key;
		// String key;
		long size = 0;
		int state = STATE_INVALID;
		CacheItem next = null;
		CacheItem prev = null;
	}
	
}