package com.bignerdranch.android.filedownloader;

/**
 * Created by LENOVO on 2017/4/21.
 */

public interface DownloadListener {
    void onProgress(int progress);
    void onPaused();
    void onSuccess();
    void onFailed();
    void onCanceled();
}
