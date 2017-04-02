/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
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

package com.example.myijkplayerup.media;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myijkplayerup.R;
import com.example.myijkplayerup.util.LogUtil;

import java.util.ArrayList;
import java.util.Formatter;

public class AndroidMediaController extends MediaController implements IMediaController {
    private ActionBar mActionBar;
    private View mRoot;
    private ImageButton mPauseButton;
    private static final int sDefaultTimeout = 3000;
    private MediaPlayerControl mPlayer;
    private ProgressBar mProgress;
    private boolean mDragging;
    private boolean mShowing;
    private TextView mEndTime, mCurrentTime;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;

    public AndroidMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AndroidMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
        initView(context);
    }

    public AndroidMediaController(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {

    }

    public void setSupportActionBar(@Nullable ActionBar actionBar) {
        mActionBar = actionBar;
        if (isShowing()) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }

    @Override
    public void show() {
        super.show();
        mShowing = true;
        if (mActionBar != null)
            mActionBar.show();
    }

    @Override
    public void hide() {
        super.hide();
        if (mActionBar != null)
            mActionBar.hide();
        for (View view : mShowOnceArray)
            view.setVisibility(View.GONE);
        mShowOnceArray.clear();
    }

    @Override
    public void setMediaPlayer(com.example.myijkplayerup.media.MediaController.MediaPlayerControl player) {

    }


//    @Override
//    public void setMediaPlayer(MediaPlayerControl player) {
//        LogUtil.i("player:1");
//        this.mPlayer = player;
//        updatePausePlay();
//        super.setMediaPlayer(player);
//        LogUtil.i("player:2"+mPlayer.isPlaying());
//    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.mymedia_controller, null);

        initControllerView(mRoot);

        return mRoot;
    }

    private void initControllerView(View v) {

        mPauseButton = (ImageButton) v.findViewById(R.id.pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mProgress = (ProgressBar) v.findViewById(R.id.mediacontroller_progress);
//        if (mProgress != null) {
//            if (mProgress instanceof SeekBar) {
//                SeekBar seeker = (SeekBar) mProgress;
//                seeker.setOnSeekBarChangeListener(mSeekListener);
//            }
//            mProgress.setMax(1000);
//        }
//        mEndTime = (TextView) v.findViewById(R.id.time);
//        mCurrentTime = (TextView) v.findViewById(R.id.time_current);
//        mFormatBuilder = new StringBuilder();
//        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }
//
    private final View.OnClickListener mPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };
//
    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    private void updatePausePlay() {
        LogUtil.i("updatePausePlay:"+mPlayer.isPlaying());
        if (mRoot == null || mPauseButton == null)
            return;

        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.mipmap.ic_media_pause);
        } else {
            mPauseButton.setImageResource(R.mipmap.ic_media_play);
        }
    }

//    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onStartTrackingTouch(SeekBar bar) {
//            show(3600000);
//
//            mDragging = true;
//
//            // By removing these pending progress messages we make sure
//            // that a) we won't update the progress while the user adjusts
//            // the seekbar and b) once the user is done dragging the thumb
//            // we will post one of these messages to the queue again and
//            // this ensures that there will be exactly one message queued up.
//            removeCallbacks(mShowProgress);
//        }
//
//        @Override
//        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
//            if (!fromuser) {
//                // We're not interested in programmatically generated changes to
//                // the progress bar's position.
//                return;
//            }
//
//            long duration = mPlayer.getDuration();
//            long newposition = (duration * progress) / 1000L;
//            mPlayer.seekTo((int) newposition);
//            if (mCurrentTime != null)
//                mCurrentTime.setText(stringForTime((int) newposition));
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar bar) {
//            mDragging = false;
//            setProgress();
//            updatePausePlay();
//            show(sDefaultTimeout);
//
//            // Ensure that progress is properly updated in the future,
//            // the call to show() does not guarantee this because it is a
//            // no-op if we are already showing.
//            post(mShowProgress);
//        }
//    };
//    private final Runnable mShowProgress = new Runnable() {
//        @Override
//        public void run() {
//            int pos = setProgress();
//            if (!mDragging && mShowing && mPlayer.isPlaying()) {
//                postDelayed(mShowProgress, 1000 - (pos % 1000));
//            }
//        }
//    };
//
//    private int setProgress() {
//        if (mPlayer == null || mDragging) {
//            return 0;
//        }
//        int position = mPlayer.getCurrentPosition();
//        int duration = mPlayer.getDuration();
//        if (mProgress != null) {
//            if (duration > 0) {
//                // use long to avoid overflow
//                long pos = 1000L * position / duration;
//                mProgress.setProgress((int) pos);
//            }
//            int percent = mPlayer.getBufferPercentage();
//            mProgress.setSecondaryProgress(percent * 10);
//        }
//
//        if (mEndTime != null)
//            mEndTime.setText(stringForTime(duration));
//        if (mCurrentTime != null)
//            mCurrentTime.setText(stringForTime(position));
//
//        return position;
//    }
//
//    private String stringForTime(int timeMs) {
//        int totalSeconds = timeMs / 1000;
//
//        int seconds = totalSeconds % 60;
//        int minutes = (totalSeconds / 60) % 60;
//        int hours = totalSeconds / 3600;
//
//        mFormatBuilder.setLength(0);
//        if (hours > 0) {
//            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
//        } else {
//            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
//        }
//    }

    //----------
    // Extends
    //----------
    private ArrayList<View> mShowOnceArray = new ArrayList<View>();

    public void showOnce(@NonNull View view) {
        mShowOnceArray.add(view);
        view.setVisibility(View.VISIBLE);
        show();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
