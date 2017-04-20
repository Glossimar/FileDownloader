package com.bignerdranch.android.filedownloader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LENOVO on 2017/4/21.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer>{
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCLED=3;

    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    private String downloadUrl;
    private String fileType;

    public DownloadTask(DownloadListener listener, String fileType) {
        this.listener = listener;
        this.fileType = fileType;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        HttpURLConnection connection = null;
        RandomAccessFile saveFile = null;
        File file = null;
        try {
            long downloadLength = 0;
            downloadUrl = params[0];
            URL url = new URL(downloadUrl);
            String fileName =downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            if (fileType != null) {
                file = new File(fileDirectory + fileName + "." + fileType);
            }else {
                file = new File(fileDirectory + fileName);
            }
            if (file.exists()) {
                downloadLength = file.length();
            }
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0){
                return TYPE_FAILED;
            } else if (contentLength == downloadLength) {
                return TYPE_SUCCESS;
            }
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            connection.setRequestProperty("RANGE", "bytes=" + downloadLength + "-" + contentLength);
            is = connection.getInputStream();
            if (is != null) {
                saveFile = new RandomAccessFile(file, "rw");
                saveFile.seek(downloadLength);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        return TYPE_FAILED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        saveFile.write(b, 0, len);
                        int progress = (int) ((total + downloadLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                return  TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (saveFile != null) {
                    saveFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCLED:
                listener.onCanceled();
                break;
            default:
                break;
        }

    }

    public void pausedDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(downloadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            if (connection.getInputStream() != null) {
                return connection.getContentLength();
            }else {
                return 0;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
