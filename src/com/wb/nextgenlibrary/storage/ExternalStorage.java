package com.wb.nextgenlibrary.storage;

import android.os.Environment;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.util.HttpHelper;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Access methods to shared external storage
 */
public class ExternalStorage {
    private static final String EXTERNAL_STORAGE_PATH = "/Android/data/" + NextGenExperience.getApplicationContext().getPackageName() + "/files/";

    /** @since API Level 1 */
    public static File getExternalFilesDir(String subDir) {
        // return new File(FlixsterApplication.getContext().getExternalFilesDir(null), subDir); // Requires API Level 8
        return new File(Environment.getExternalStorageDirectory(), EXTERNAL_STORAGE_PATH + subDir);
    }

    /** @since API Level 1 */
    public static boolean writeFile(String content, String subDir, String fileName) {

        // Check if available and not read only
        if (!isAvailable() || isReadOnly()) {
            NextGenLogger.w(F.TAG, "ExternalStorage.writeFile storage unavailable or read-only");
            return false;
        }

        // Create a path where we will place our List of objects on external storage
        File dir = new File(Environment.getExternalStorageDirectory(), EXTERNAL_STORAGE_PATH + subDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        boolean success = false;
        OutputStream os = null;

        try {
            os = new FileOutputStream(file);
            os.write(content.getBytes());
            os.flush();
            success = true;
        } catch (IOException e) {
            NextGenLogger.w(F.TAG, "ExternalStorage.writeFile " + file, e);
        } catch (Exception e) {
            NextGenLogger.w(F.TAG, "ExternalStorage.writeFile " + file, e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
            }
        }

        NextGenLogger.d(F.TAG, "ExternalStorage.writeFile " + file + (success ? " successful" : " failed"));
        return success;
    }

    /** @since API Level 1 */
    public static String readFile(String subDir, String fileName) {
        return readFile(Environment.getExternalStorageDirectory() + EXTERNAL_STORAGE_PATH + subDir + fileName);
    }

    /** @since API Level 1 */
    public static String readFile(String localUri) {
        String result = null;

        // Check if available and not read only
        if (!isAvailable() || isReadOnly()) {
            NextGenLogger.w(F.TAG, "ExternalStorage.readFile storage unavailable or read-only");
            return null;
        }

        localUri = localUri.startsWith("file://") ? localUri.substring(7) : localUri;
        File file = new File(localUri);
        if (!file.exists()) {
            NextGenLogger.d(F.TAG, "ExternalStorage.readFile " + file + " not found");
            return null;
        }

        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            result = new String(HttpHelper.streamToByteArray(in));
        } catch (FileNotFoundException e) {
            NextGenLogger.w(F.TAG, "ExternalStorage.readFile " + file, e);
        } catch (IOException e) {
            NextGenLogger.w(F.TAG, "ExternalStorage.readFile " + file, e);
        } catch (Exception e) {
            NextGenLogger.w(F.TAG, "ExternalStorage.readFile " + file, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    /** @since API Level 1 */
    public static boolean findFile(String localUri) {
        // Check if available
        if (!isAvailable()) {
            NextGenLogger.w(F.TAG, "ExternalStorage.findFile storage unavailable");
            return false;
        }
        localUri = localUri.startsWith("file://") ? localUri.substring(7) : localUri;
        File file = new File(localUri);
        return file.exists();
    }

    /**
     * Find all files with the extension in the directory
     * 
     * @since API Level 1
     */
    public static Collection<String> findFiles(String subDir, String fileExtension) {
        // Check if available
        if (!isAvailable()) {
            NextGenLogger.w(F.TAG, "ExternalStorage.findFiles storage unavailable");
            return null;
        }

        // List directory files
        File externalStorageDir = Environment.getExternalStorageDirectory();
        File dir = new File(externalStorageDir, EXTERNAL_STORAGE_PATH + subDir);
        if (!dir.exists()) {
            return null;
        } else {
            String[] fileNames = dir.list();
            ArrayList<String> localUris = new ArrayList<String>(fileNames.length);
            for (String fileName : fileNames) {
                if (fileName.endsWith(fileExtension)) {
                    localUris.add(externalStorageDir + EXTERNAL_STORAGE_PATH + subDir + fileName);
                }
            }
            return localUris;
        }
    }

    /** @since API Level 1
    public static boolean renameFile(String oldLocalUri, String newSubDir, String newFileName) {
        // Check if available and not read only
        if (!isAvailable() || isReadOnly()) {
            NextGenLogger.w(F.TAG, "ExternalStorage.readFile storage unavailable or read-only");
            return false;
        }

        // Rename
        oldLocalUri = oldLocalUri.startsWith("file://") ? oldLocalUri.substring(7) : oldLocalUri;
        File file = new File(oldLocalUri);
        if (file.exists()) {
            String newPath = Environment.getExternalStorageDirectory() + EXTERNAL_STORAGE_PATH + newSubDir
                    + newFileName;
            return file.renameTo(new File(newPath));
        } else {
            return false;
        }
    }*/

    /** @since API Level 1 */
    public static boolean deleteFile(String subDir, String fileName) {
        return deleteFile(Environment.getExternalStorageDirectory() + EXTERNAL_STORAGE_PATH + subDir + fileName);
    }
    
    public static boolean deleteFileByName(String name) {
    	return deleteFile(Environment.getExternalStorageDirectory() + EXTERNAL_STORAGE_PATH + "/wv/" + name + ".wvm");
    }

    /** @since API Level 1 */
    public static boolean deleteFile(String localUri) {

        // Check if available and not read only
        if (!isAvailable() || isReadOnly()) {
            NextGenLogger.w(F.TAG, "ExternalStorage.deleteFile storage unavailable or read-only");
            return false;
        }

        localUri = localUri.startsWith("file://") ? localUri.substring(7) : localUri;
        File file = new File(localUri);
        if (file.exists()) {
            if (file.delete()) {
                NextGenLogger.d(F.TAG, "ExternalStorage.deleteFile removed " + file);
                return true;
            }
        }
        return false;
    }

    public static boolean isReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    public static boolean isWriteable() {
        return isAvailable() && !isReadOnly();
    }
}
