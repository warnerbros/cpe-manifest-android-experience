package net.flixster.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import net.flixster.android.storage.ExternalStorage;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.util.HttpHelper;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.FlixsterLogger;
import android.content.Context;
import android.os.Environment;


public class FlixsterCacheManager {
	
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
	
	public FlixsterCacheManager(Context applicationContext) {
		mContext = applicationContext;
		sCacheHash = new LinkedHashMap<Integer, CacheItem>(100, (float) 0.5, true);
		
		// sTimer = new Timer();
		//
		// sTimerTask = new TimerTask() {
		// public void run() {
		FlixsterLogger.v(F.TAG,
				"FlixsterCacheManager MEDIA Mounted =" + Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED) + "");
		
		// create cache directory
		FlixsterLogger.d(F.TAG,
				"FlixsterCacheManager Environment.getExternalStorageState():" + Environment.getExternalStorageState() + " sCacheLimit:"
						+ sCacheLimit);
		boolean activeState = false;
		if (Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED)) {
			File sdDir = ExternalStorage.getExternalFilesDir(F.CACHE_DIR);
			FlixsterLogger.d(F.TAG, "FlixsterCacheManager sdDir:" + sdDir);
			sCacheDir = new File(sdDir.toString());
			if (sCacheDir.exists() && sCacheDir.isDirectory() && sCacheDir.canWrite()) {
				FlixsterLogger.v(F.TAG, "FlixsterCacheManager  mCacheDir exists == true");
				buildCacheIndex();
				activeState = true;
			} else {
				FlixsterLogger.v(F.TAG, "FlixsterCacheManager mCacheDir mkdirs()");
				if (sCacheDir.mkdirs()) {
					activeState = true;
				}
			}
			
			setCacheLimit();
			// TrimCache(0L);
			sIsActive = activeState;
			FlixsterLogger.v(F.TAG, "FlixsterCacheManager active: " + sIsActive);
			
		} else {
			FlixsterLogger.e(F.TAG, "FlixsterCacheManager SDCARD not avalable!");
		}
		// }
		// };
		
		// sTimer.schedule(sTimerTask, 10);
		
	}
	
	public static void setCacheLimit() {
		FlixsterLogger.v(F.TAG, "FlixsterCacheManager SetCacheLimit() before sCacheLimit:" + sCacheLimit + " getCachePolicy:"
				+ NextGenApplication.getCachePolicy());
		switch (NextGenApplication.getCachePolicy()) {
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
		FlixsterLogger.v(F.TAG, "FlixsterCacheManager SetCacheLimit() after sCacheLimit:" + sCacheLimit);
	}
	
	public void buildCacheIndex() {
		FlixsterLogger.v(F.TAG, "FlixsterCacheManager.buildCacheIndex start");
		
		// sCacheDir = mContext.getFilesDir();
		sCacheDir = ExternalStorage.getExternalFilesDir(F.CACHE_DIR);
		File fileObj;
		String files[] = sCacheDir.list();
		
		FlixsterLogger.v(F.TAG, "FlixsterCacheManager.buildCacheIndex files:" + files);
		if (files != null) {
			CacheItem tempItem;
			for (String file : files) {
				// Loggerflx.v(F.TAG, "FlixsterCacheManager.buildCacheIndex file:" + file +
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
		
		FlixsterLogger.v(F.TAG, "FlixsterCacheManager.buildCacheIndex mCacheByteSize:" + sCacheByteSize + " mCacheItemSize:"
				+ sCacheItemSize + " sCacheLimit:" + sCacheLimit);
		
	}
	
	public byte[] get(int urlHashCode) {
		String filename = "cache" + Integer.toString(urlHashCode).replace('-', '_');
		// Loggerflx.v(F.TAG, "FlixsterCacheManager get filename:" + filename + " mCacheByteSize:" +
		// sCacheByteSize
		// + " mCacheItemSize:" + sCacheItemSize+" sCacheLimit:"+sCacheLimit);
		if (!sIsActive) {
			FlixsterLogger.e(F.TAG, "FlixsterCacheManager.get NOT ACTIVE YET");
			return null;
		}
		try {
			if (sCacheHash.containsKey(urlHashCode)) {
				// Loggerflx.v(F.TAG, "FlixsterCacheManager.get hit urlHashCode:" + urlHashCode);
				// Loggerflx.v(F.TAG, "FlixsterCacheManager filename:" + filename);
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
			FlixsterLogger.e(F.TAG, "FlixsterCacheManager.get", e);
			
		} catch (IOException e) {
			FlixsterLogger.e(F.TAG, "FlixsterCacheManager.get", e);
		}
		return null;
	}
	
	public synchronized static void TrimCache(long trimSize) {
		// /////////////////////////////////
		// Trim Cache on disk if needed....
		
		if (trimSize > sCacheLimit) {
			FlixsterLogger.w(F.TAG, "trimSize size exceeds cache size!!");
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
			FlixsterLogger.v(F.TAG, "FlixsterCacheManager.put TRIM, mCacheByteSize:" + sCacheByteSize + " mCacheItemSize:"
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
		// Loggerflx.v(F.TAG, "FlixsterCacheManager put filename:" + filename + " byteArray.length:" +
		// byteArray.length
		// + " sCacheLimit:" + sCacheLimit);
		
		if (!sIsActive) {
			FlixsterLogger.v(F.TAG, "FlixsterCacheManager.put NOT ACTIVE YET byteArray.length:" + byteArray.length);
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
			
			// Loggerflx.v(F.TAG, "FlixsterCacheManager put success. filename:" + filename);
		} catch (FileNotFoundException e) {
			FlixsterLogger.v(F.TAG, "FlixsterCacheManager put FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			FlixsterLogger.v(F.TAG, "FlixsterCacheManager put IOException");
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
		// Loggerflx.v(F.TAG, "FlixsterCacheManager.AppendCacheItem item.size:" + item.size);
		
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
		// "FlixsterCacheManager.DecapCacheItem mHead:"+mHead+" mTail:"+mTail+"mCacheHash.size:"+mCacheHash.size());
		if (topItem == null) {
			FlixsterLogger.e(F.TAG, "FlixsterCacheManager.DecapCacheItem topItem is null");
			return;
		} else if (sHead == sTail) { // special case of only one item
			FlixsterLogger.v(F.TAG, "FlixsterCacheManager.DecapCacheItem empty? mHead:" + sHead + " tail:" + sTail);
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