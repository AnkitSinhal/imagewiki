package com.microsoft.wiki.utils;

import android.content.Context;

import java.io.File;

public class FileUtils {
    /**
     * Instance of directory
     */
    private File mCacheDirectory;

    /**
     * Default constructor
     *
     * @param context       application context
     * @param directoryName name of the cache directory
     */
    public FileUtils(Context context, String directoryName) {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment
                .MEDIA_MOUNTED)) {
            mCacheDirectory = new File(android.os.Environment.getExternalStorageDirectory(),
                    directoryName);
        } else {
            mCacheDirectory = context.getCacheDir();
        }

        if (!mCacheDirectory.exists()) {
            mCacheDirectory.mkdirs();
        }
    }

    /**
     * Get the file with given name
     *
     * @param filename name of the file
     * @return Instance of file
     */
    public File getFile(String filename) {
        return new File(mCacheDirectory, filename);
    }

    /**
     * Delete all the files in directory
     */
    public void clear() {
        File[] files = mCacheDirectory.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

}