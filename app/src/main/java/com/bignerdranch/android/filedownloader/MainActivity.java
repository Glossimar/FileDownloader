package com.bignerdranch.android.filedownloader;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String DOWNLOAD_URL = "http://ws.stream.qqmusic.qq.com/201445492.m4a?fromtag=46";

    private MediaPlayer player;

    private Button start;
    private Button pause;
    private Button cancel;
    private Button playMusic;
    private Button pauseMusic;

    private File file;

    private FileDownloader fileDownloader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = new MediaPlayer();

        fileDownloader = new FileDownloader(DOWNLOAD_URL, "mp3");
        FileDownloader.setActivity(MainActivity.this);
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        bindService(intent,fileDownloader.getServiceConnection(),BIND_AUTO_CREATE);

        String fileName =DOWNLOAD_URL.substring(DOWNLOAD_URL.lastIndexOf("/"));
        String fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        file = new File(fileDirectory + fileName + ".mp3");

        start = (Button) findViewById(R.id.start_button);
        pause = (Button) findViewById(R.id.pause_button);
        cancel = (Button) findViewById(R.id.cancel_button);
        playMusic =(Button) findViewById(R.id.play_music);
        pauseMusic = (Button) findViewById(R.id.pause_music);

        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        cancel.setOnClickListener(this);
        playMusic.setOnClickListener(this);
        pauseMusic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                fileDownloader.beginDownload();
                break;
            case R.id.cancel_button:
                fileDownloader.cancelDownload();
                break;
            case R.id.pause_button:
                fileDownloader.pauseDownload();
                break;
            case R.id.play_music:
                try {
                    if (file.exists()) {
                        player.setDataSource(file.getPath());
                        player.prepare();
                        player.start();
                    } else {
                        Toast.makeText(MainActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pause_music:
                if (player.isPlaying()){
                    player.pause();
                }
                break;
            default:
                break;
        }
    }
}
