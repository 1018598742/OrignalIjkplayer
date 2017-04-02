package com.example.myijkplayerup;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.myijkplayerup.databinding.FragmentVideoBinding;
import com.example.myijkplayerup.media.IjkVideoView;
import com.example.myijkplayerup.media.MediaController;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Administrator on 2017/3/28.
 */

public class VideoFragment extends Fragment implements MediaController.FullsreenController{
    private Button mButton;
    private IjkVideoView mVideoView;
    private FrameLayout mFreLayout;
    private TextView mTextView;
    private MediaController mController;
    private View btRestart;
    private FragmentVideoBinding mDataBinding;
    private boolean mBackPressed;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View fragmentVideo = inflater.inflate(R.layout.fragment_video, container, false);
        mDataBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.fragment_video,container,false);
        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVideoView = getView(R.id.ijkVideoView);
        mButton = getView(R.id.button);
        btRestart = getView(R.id.bt_restart);
        mFreLayout = getView(R.id.fl_ijk);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mController = new MediaController(getActivity());
        mController.setOnFullScreenListener(this);
        mVideoView.setMediaController(mController);
        mVideoView.setVideoURI(Uri.parse("http://zv.3gv.ifeng.com/live/zhongwen800k.m3u8"));
        mVideoView.setTcpView(mDataBinding.infoNet);
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mVideoView.start();
            }
        });
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
//                if (iMediaPlayer != null && ){
//
//                }
            }

        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                switch (i){
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        mDataBinding.infoNet.setVisibility(View.VISIBLE);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mDataBinding.infoNet.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mTextView.setText("bug测试");
            }
        });
//
        btRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    protected <T extends View> T getView(int id) {
        return (T) getView().findViewById(id);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.pause();
    }

//    @Override
//    public void onBackPressed() {
//        mBackPressed = true;
//
//        super.onBackPressed();
//    }


    @Override
    public void onStop() {
        super.onStop();
        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
//            mVideoView.stopPlayback();
//            mVideoView.release(true);
//            mVideoView.stopBackgroundPlay();
//        } else {
//            mVideoView.enterBackground();
//        }
//        IjkMediaPlayer.native_profileEnd();
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mVideoView.setFullScreen();
        }else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mVideoView.setFitScreen();
        }

    }



    @Override
    public void fullscreen() {
        if (this.getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
