package com.example.playerdemo;

/*
 * Copyright (C) 2009 The Android Open Source Project
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

import java.io.File;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class MainActivity extends Activity implements
        OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
        OnVideoSizeChangedListener, SurfaceHolder.Callback {

    private static final String TAG = "MediaPlayerDemo";
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private String path;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    
    RelativeLayout mVideoLayout1;
    RelativeLayout mVideoLayout2;
    
    private VideoView video1;  
    MediaController  mediaco;  

    /**
     * 
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        
        mVideoLayout1 = (RelativeLayout) findViewById(R.id.video_layout1);
        mVideoLayout2 = (RelativeLayout) findViewById(R.id.video_layout2);
        
        mPreview = (SurfaceView) findViewById(R.id.surface);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        video1=(VideoView)findViewById(R.id.video1);  
        mediaco=new MediaController(this);  
        //Uri uri = Uri.parse("http://static.smartisanos.cn/common/video/smartisant1.mp4");   
       // video1.setVideoURI(uri);  
       
        File file=new File("/mnt/sdcard/smartisant1.mp4");  
        if(file.exists()){  
            //VideoView与MediaController进行关联  
            video1.setVideoPath(file.getAbsolutePath());  
            video1.setMediaController(mediaco);  
            mediaco.setMediaPlayer(video1);  
            //让VideiView获取焦点  
            video1.requestFocus(); 
        }   
    }

    private void playVideo() {
        doCleanUp();
        try {
            /*
             * Set path variable to progressive streamable mp4 or 3gpp
             * format URL. Http protocol should be used. Mediaplayer can only
             * play "progressive streamable contents" which basically means: 1.
             * the movie atom has to precede all the media data atoms. 2. The
             * clip has to be reasonably interleaved.
             */
            path = "http://static.smartisanos.cn/common/video/smartisant1.mp4";

            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource("/mnt/sdcard/smartisant1.mp4");
            //File file=new File();  
            //String mUrl = Uri.parse("/mnt/sdcard/smartisant2.mp4").getPath();
			//mMediaPlayer = MediaPlayer.create(this, Uri.parse(mUrl));
            
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.setVolume(0, 0);
            mMediaPlayer.prepare();
            
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        Log.d(TAG, "onBufferingUpdate percent:" + percent);

    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d(TAG, "onCompletion called");
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height
                    + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = 854/2;
        mVideoHeight = 480/2;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged called");

    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed called");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");
        playVideo();

    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback " + mVideoWidth + "-->" + mVideoHeight);
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mVideoLayout1.getLayoutParams();
        params.width = 854/2;
        params.height = 480/2;
        params.leftMargin = 0;
        mVideoLayout1.setLayoutParams(params);
        
        params = (LinearLayout.LayoutParams)mVideoLayout2.getLayoutParams();
        params.width = 854/2;
        params.height = 480/2;
        params.rightMargin = 0;
        mVideoLayout2.setLayoutParams(params);
        
        mMediaPlayer.start();
        video1.start();
    }
}