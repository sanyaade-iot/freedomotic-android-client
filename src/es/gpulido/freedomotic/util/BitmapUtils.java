/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.gpulido.freedomotic.util;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import es.gpulido.freedomotic.ui.preferences.Preferences;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Helper class for fetching and disk-caching images from the web.
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";
    //TODO: Allow user to clean cachedObject to update data
	public static HashMap<String, Bitmap> cachedObjBitmap = new HashMap<String, Bitmap>();
    
	//Helper class that downloads bitmap file from a url
	public static Bitmap downloadFile(String fileUrl){
		Bitmap bmImg=null;  
		URL myFileUrl =null;          
		try {
			myFileUrl= new URL(fileUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bmImg = BitmapFactory.decodeStream(is);		               
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				 
	    return bmImg;
		
	}
    
    public static Bitmap getImage(String icon, int width, int height) {
        String resizedImageKey = icon + "-" + width + "-" + height;
        Bitmap img;
        if (cachedObjBitmap.containsKey(resizedImageKey))
        {
        	img = cachedObjBitmap.get(resizedImageKey);
        }
        else
        {                	            	            
            img = BitmapUtils.downloadFile(Preferences.getServerString()+"/v1/resources/"+icon);
            if (img != null) {            //img just loaded from disk, cache it resized
                if ((width <= 0) || (height <= 0)) {//resizing not needed
                	cachedObjBitmap.put(resizedImageKey, img);
                } else {
                	cachedObjBitmap.put(resizedImageKey, Bitmap.createScaledBitmap(img, width, height, true));	                    	
                }
                return img;
            }
            else //The image was not found on the server, mark it so it is not searched again.
            {
            	cachedObjBitmap.put(resizedImageKey, null);
            	
            }
        }
        return img;
    }
	
	
	
	//TODO: Figure out how to download and store in the disk the files and do it async   
	//The fecthImage
     
                  
    // TODO: for concurrent connections, DefaultHttpClient isn't great, consider other options
    // that still allow for sharing resources across bitmap fetches.

    public static interface OnFetchCompleteListener {
        public void onFetchComplete(Object cookie, Bitmap result);
    }

    /**
     * Only call this method from the main (UI) thread. The {@link OnFetchCompleteListener} callback
     * be invoked on the UI thread, but image fetching will be done in an {@link AsyncTask}.
     */
    public static void fetchImage(final Context context, final String url,
            final OnFetchCompleteListener callback) {
        fetchImage(context, url, null, null, callback);
    }

    /**
     * Only call this method from the main (UI) thread. The {@link OnFetchCompleteListener} callback
     * be invoked on the UI thread, but image fetching will be done in an {@link AsyncTask}.
     *
     * @param cookie An arbitrary object that will be passed to the callback.
     */
    public static void fetchImage(final Context context, final String url,
            final BitmapFactory.Options decodeOptions,
            final Object cookie, final OnFetchCompleteListener callback) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                final String url = params[0];
                if (TextUtils.isEmpty(url)) {
                    return null;
                }

                // First compute the cache key and cache file path for this URL
                File cacheFile = null;
                try {
                    MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
                    mDigest.update(url.getBytes());
                    final String cacheKey = bytesToHexString(mDigest.digest());
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        cacheFile = new File(
                                Environment.getExternalStorageDirectory()
                                        + File.separator + "Android"
                                        + File.separator + "data"
                                        + File.separator + context.getPackageName()
                                        + File.separator + "cache"
                                        + File.separator + "bitmap_" + cacheKey + ".tmp");
                    }
                } catch (NoSuchAlgorithmException e) {
                    // Oh well, SHA-1 not available (weird), don't cache bitmaps.
                }

                if (cacheFile != null && cacheFile.exists()) {
                    Bitmap cachedBitmap = BitmapFactory.decodeFile(
                            cacheFile.toString(), decodeOptions);
                    if (cachedBitmap != null) {
                        return cachedBitmap;
                    }
                }

                try {
                    // TODO: check for HTTP caching headers
                    final HttpClient httpClient =getHttpClient(
                            context.getApplicationContext());
                    final HttpResponse resp = httpClient.execute(new HttpGet(url));
                    final HttpEntity entity = resp.getEntity();

                    final int statusCode = resp.getStatusLine().getStatusCode();
                    if (statusCode != HttpStatus.SC_OK || entity == null) {
                        return null;
                    }

                    final byte[] respBytes = EntityUtils.toByteArray(entity);

                    // Write response bytes to cache.
                    if (cacheFile != null) {
                        try {
                            cacheFile.getParentFile().mkdirs();
                            cacheFile.createNewFile();
                            FileOutputStream fos = new FileOutputStream(cacheFile);
                            fos.write(respBytes);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.w(TAG, "Error writing to bitmap cache: " + cacheFile.toString(), e);
                        } catch (IOException e) {
                            Log.w(TAG, "Error writing to bitmap cache: " + cacheFile.toString(), e);
                        }
                    }

                    // Decode the bytes and return the bitmap.
                    return BitmapFactory.decodeByteArray(respBytes, 0, respBytes.length,
                            decodeOptions);
                } catch (Exception e) {
                    Log.w(TAG, "Problem while loading image: " + e.toString(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                callback.onFetchComplete(cookie, result);
            }
        }.execute(url);
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    
    
    //TODO: This is from SyncService from iosched
    private static final int SECOND_IN_MILLIS = (int) DateUtils.SECOND_IN_MILLIS;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
    
    /**
     * Generate and return a {@link HttpClient} configured for general use,
     * including setting an application-specific user-agent string.
     */
    public static HttpClient getHttpClient(Context context) {
        final HttpParams params = new BasicHttpParams();

        // Use generous timeouts for slow mobile networks
        HttpConnectionParams.setConnectionTimeout(params, 20 * SECOND_IN_MILLIS);
        HttpConnectionParams.setSoTimeout(params, 20 * SECOND_IN_MILLIS);

        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpProtocolParams.setUserAgent(params, buildUserAgent(context));

        final DefaultHttpClient client = new DefaultHttpClient(params);

        client.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                // Add header to accept gzip content
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                // Inflate any responses compressed with gzip
                final HttpEntity entity = response.getEntity();
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        return client;
    }
    
    /**
     * Build and return a user-agent string that can identify this application
     * to remote servers. Contains the package name and version code.
     */
    private static String buildUserAgent(Context context) {
        try {
            final PackageManager manager = context.getPackageManager();
            final PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            // Some APIs require "(gzip)" in the user-agent string.
            return info.packageName + "/" + info.versionName
                    + " (" + info.versionCode + ") (gzip)";
        } catch (NameNotFoundException e) {
            return null;
        }
    }
    /**
     * Simple {@link HttpEntityWrapper} that inflates the wrapped
     * {@link HttpEntity} by passing it through {@link GZIPInputStream}.
     */
    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
    
}
