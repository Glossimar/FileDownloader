package com.bignerdranch.android.filedownloader;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by LENOVO on 2017/4/21.
 */

public class FileDownloader {

    public static Activity activityStatic;

    private String downloadUrl;
    private String fileType;

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public FileDownloader(String downloadUrl, String fileType) {
        this.downloadUrl = downloadUrl;
        this.fileType = fileType;
    }
    public void beginDownload(){
        Log.d("findNull", "beginDownload: " + downloadBinder);
        downloadBinder.startDownload(downloadUrl, fileType);
    }

    public static int getDownLoadProgress(){
        return DownloadTask.downloadProgress;
    }

    public void pauseDownload() {
        downloadBinder.pauseDownload();
    }

    public void cancelDownload() {
        downloadBinder.canaelDownload();
    }

    public ServiceConnection  getServiceConnection() {
        return connection;
    }
    public static void setActivity(Activity activity){
        activityStatic = activity;
    }
}
