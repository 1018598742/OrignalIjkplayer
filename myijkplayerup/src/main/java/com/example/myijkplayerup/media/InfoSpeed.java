package com.example.myijkplayerup.media;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

import static java.lang.String.format;

/**
 * Created by Administrator on 2017/3/31.
 */

public class InfoSpeed {
    private IMediaPlayer mMediaPlayer;
    private TextView mTextView;
    public InfoSpeed(IMediaPlayer mMediaPlayer,TextView mTextView){
        this.mTextView = mTextView;
        this.mMediaPlayer = mMediaPlayer;
    }

    private static final int MSG_UPDATE_HUD = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_HUD: {
                    IjkMediaPlayer mp = null;
                    if (mMediaPlayer == null)
                        break;
                    if (mMediaPlayer instanceof IjkMediaPlayer) {
                        mp = (IjkMediaPlayer) mMediaPlayer;
                    } else if (mMediaPlayer instanceof MediaPlayerProxy) {
                        MediaPlayerProxy proxy = (MediaPlayerProxy) mMediaPlayer;
                        IMediaPlayer internal = proxy.getInternalMediaPlayer();
                        if (internal != null && internal instanceof IjkMediaPlayer)
                            mp = (IjkMediaPlayer) internal;
                    }
                    if (mp == null)
                        break;
                    long tcpSpeed = mp.getTcpSpeed();
                    String speed = String.format(Locale.US, "%s", formatedSpeed(tcpSpeed, 1000));
                    mTextView.setText(speed);
                    mHandler.removeMessages(MSG_UPDATE_HUD);
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);
                }
            }
        }
    };

    private static String formatedSpeed(long bytes, long elapsed_milli) {
        if (elapsed_milli <= 0) {
            return "0 B/s";
        }

        if (bytes <= 0) {
            return "0 B/s";
        }

        float bytes_per_sec = ((float) bytes) * 1000.f / elapsed_milli;
        if (bytes_per_sec >= 1000 * 1000) {
            return format(Locale.US, "%.2f MB/s", ((float) bytes_per_sec) / 1000 / 1000);
        } else if (bytes_per_sec >= 1000) {
            return format(Locale.US, "%.1f KB/s", ((float) bytes_per_sec) / 1000);
        } else {
            return format(Locale.US, "%d B/s", (long) bytes_per_sec);
        }
    }
}
