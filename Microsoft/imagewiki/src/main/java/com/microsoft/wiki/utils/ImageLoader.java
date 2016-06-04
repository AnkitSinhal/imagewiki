package com.microsoft.wiki.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.microsoft.wiki.R;
import com.microsoft.wiki.http.HttpConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    /**
     * Initialize MemoryCache
     */
    private ImageCacheUtils mImageCacheUtils = new ImageCacheUtils();
    /**
     * Instance of FileCache
     */
    private FileUtils mFileUtils;
    /**
     * Create Map (collection) to store image and image url in key value pair
     */
    private Map<ImageView, String> mImageViewMap = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    /**
     * Instance of ExecutorService
     */
    private ExecutorService mExecutorService;
    /**
     * Handler to display images in UI thread
     */
    private Handler mHandler = new Handler();
    /**
     * Placeholder image to show in image view
     */
    private static final int mPlaceholderImageId = R.mipmap.placeholder;

    /**
     * Default constructor
     *
     * @param context application context
     */
    public ImageLoader(Context context) {
        mFileUtils = new FileUtils(context, Constants.CACHE_DIRECTORY_NAME);
        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.
        mExecutorService = Executors.newFixedThreadPool(5);
    }

    /**
     * Display the image to the view. If image is in the cache directory then take the image from
     * it otherwise download from server.
     *
     * @param url       image URL
     * @param imageView view which need to set the result image
     */
    public void displayImage(String url, ImageView imageView) {
        mImageViewMap.put(imageView, url);

        // Check image is stored in ImageCache Map or not
        Bitmap bitmap = mImageCacheUtils.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            // queue Photo to download from url
            queueImage(url, imageView);
            // Before downloading image show default image
            imageView.setImageResource(mPlaceholderImageId);
        }
    }

    public class ImageDetails {
        public String url;
        public ImageView imageView;
    }

    /**
     * Queue the photo to display on UI
     *
     * @param url       image URL
     * @param imageView imageView instance
     */
    private void queueImage(String url, ImageView imageView) {
        ImageDetails imageDetails = new ImageDetails();
        imageDetails.imageView = imageView;
        imageDetails.url = url;

        // Pass the PhotosLoader runnable to executor service to process
        mExecutorService.submit(new PhotosLoader(imageDetails));
    }

    class PhotosLoader implements Runnable {
        ImageDetails imageDetails;

        PhotosLoader(ImageDetails imageDetails) {
            this.imageDetails = imageDetails;
        }

        @Override
        public void run() {
            try {
                // Check if image already downloaded
                if (isImageViewReused(imageDetails))
                    return;
                // download image from web url
                Bitmap bmp = getBitmap(imageDetails.url);

                // set image data in Memory Cache
                mImageCacheUtils.put(imageDetails.url, bmp);

                if (isImageViewReused(imageDetails))
                    return;

                // Add the bitmap to display
                BitmapRunnable bd = new BitmapRunnable(bmp, imageDetails);
                mHandler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    /**
     * Get the image bitmap if exist in cache otherwise download it from server
     *
     * @param url image url
     * @return image bitmap
     */
    private Bitmap getBitmap(String url) {
        //Identify images by hashcode
        String filename = String.valueOf(url.hashCode());
        File f = mFileUtils.getFile(filename);
        Bitmap bitmapFromCache = getBitmapFromCache(f);
        if (bitmapFromCache != null) {
            return bitmapFromCache;
        }
        try {
            HttpConnection httpConnection = new HttpConnection();
            byte[] responseData = httpConnection.execute(url);

            //create file with response image bytes
            OutputStream os = new FileOutputStream(f);
            os.write(responseData);
            os.close();
            return BitmapFactory.decodeByteArray(responseData, 0, responseData.length);
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                mImageCacheUtils.clear();
            return null;
        }
    }

    /**
     * Get the bitmap from cache
     *
     * @param file name of file
     * @return image bitmap
     */
    private Bitmap getBitmapFromCache(File file) {
        try {
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            FileInputStream stream2 = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * Check the image url is already exist in imageViews MAP
     *
     * @param imageDetails PhotoToLoad instance
     * @return true if exist else return false
     */
    boolean isImageViewReused(ImageDetails imageDetails) {
        String tag = mImageViewMap.get(imageDetails.imageView);
        return tag == null || !tag.equals(imageDetails.url);
    }

    // Used to display bitmap in the UI thread
    class BitmapRunnable implements Runnable {
        Bitmap bitmap;
        ImageDetails imageDetails;

        public BitmapRunnable(Bitmap b, ImageDetails p) {
            bitmap = b;
            imageDetails = p;
        }

        public void run() {
            if (isImageViewReused(imageDetails))
                return;
            if (bitmap != null)
                imageDetails.imageView.setImageBitmap(bitmap);
            else
                imageDetails.imageView.setImageResource(mPlaceholderImageId);
        }
    }

    /**
     * Clear cache directory downloaded images and image data in maps
     */
    public void clearCache() {
        mImageCacheUtils.clear();
        mFileUtils.clear();
    }
}
