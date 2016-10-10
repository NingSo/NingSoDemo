package com.ningso.ningsodemo.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by NingSo on 16/3/4.上午10:26
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class DownLoadHelper {

    public static void executeDownLoad(final String urlStr, final String destFileDir, final String destFileName, final FileCallback fileCallback) {
        new AsyncTask<Void, Void, File>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (fileCallback != null) {
                    fileCallback.onStart();
                }
            }

            @Override
            protected File doInBackground(Void... params) {
                if (fileCallback != null) {
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        URL url = new URL(urlStr);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setConnectTimeout(7 * 1000);
                        urlConnection.setReadTimeout(10 * 1000);
                        urlConnection.setRequestMethod("GET");
                        final long total = urlConnection.getContentLength();
                        Log.e(DownLoadHelper.class.getSimpleName(), "total: " + total);
                        long sum = 0;
                        File dir = new File(destFileDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, destFileName);
                        if (file.exists() && file.length() == total) {
                            Log.d(DownLoadHelper.class.getSimpleName(), "The file which we want to download was already here.");
                            //  fileCallback.onResponse(file);
                            urlConnection.disconnect();
                            return file;
                        } else {
                            file.createNewFile();
                        }
                        is = urlConnection.getInputStream();
                        fos = new FileOutputStream(file);
                        int len;
                        byte[] buf = new byte[2048];
                        while ((len = is.read(buf)) != -1) {
                            sum += len;
                            fos.write(buf, 0, len);
                            final long finalSum = sum;
                            fileCallback.inProgress(finalSum * 1.0f / total);
                        }
                        urlConnection.disconnect();
                        fos.flush();
                        return file;
                    } catch (IOException e) {
                        fileCallback.onError(e);
                    } finally {
                        try {
                            if (is != null) is.close();
                            if (fos != null) fos.close();
                        } catch (IOException e) {
                            fileCallback.onError(e);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);
                if (fileCallback != null) {
                    if (file != null) {
                        fileCallback.onResponse(file);
                    } else {
                        fileCallback.onError(new RuntimeException("file is null"));
                    }
                }
            }
        }.execute();
    }

    public interface FileCallback {
        void onStart();

        void inProgress(float progress);

        void onError(Exception e);

        void onResponse(File response);
    }
}
