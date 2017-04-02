package com.example.myijkplayerup;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.myijkplayerup.databinding.ActivityVideoBinding;
import com.example.myijkplayerup.media.IjkVideoView;
import com.example.myijkplayerup.media.MediaController;
import com.example.myijkplayerup.util.LogUtil;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends AppCompatActivity implements MediaController.FullsreenController {

    private static final String TAG = "VideoActivity";

    private String mVideoPath;
    private Uri mVideoUri;

    private MediaController mMediaController;
    private IjkVideoView mVideoView;
//    private TextView mToastTextView;
//    private TableLayout mHudView;
//    private DrawerLayout mDrawerLayout;
//    private ViewGroup mRightDrawer;

    private boolean mBackPressed;
    private ActivityVideoBinding mDataBinding;
    private Toolbar mToolbar;
    private ActionBar mActionBar;

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_video);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_video);
        // handle arguments
        mVideoPath = getIntent().getStringExtra("videoPath");

        Intent intent = getIntent();
        String intentAction = intent.getAction();
        Log.i(TAG, "intentAction===" + intentAction);
        if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction.equals(Intent.ACTION_VIEW)) {
                mVideoPath = intent.getDataString();
            } else if (intentAction.equals(Intent.ACTION_SEND)) {
                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    String scheme = mVideoUri.getScheme();//获得协议如http
                    if (TextUtils.isEmpty(scheme)) {
                        Log.e(TAG, "Null unknown scheme\n");
                        finish();
                        return;
                    }
                    if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                        mVideoPath = mVideoUri.getPath();
                    } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                        Log.e(TAG, "Can not resolve content below Android-ICS\n");
                        finish();
                        return;
                    } else {
                        Log.e(TAG, "Unknown scheme " + scheme + "\n");
                        finish();
                        return;
                    }
                }
            }
        }

//        if (!TextUtils.isEmpty(mVideoPath)) {
//            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
//        }

        // init UI
        mToolbar = mDataBinding.toolbar;
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_60dp);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mMediaController = new MediaController(this);
        mMediaController.setOnFullScreenListener(this);

//        mToastTextView = (TextView) findViewById(R.id.toast_text_view);
//        mHudView = (TableLayout) findViewById(R.id.hud_view);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mRightDrawer = (ViewGroup) findViewById(R.id.right_drawer);
//
//        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = (IjkVideoView) findViewById(R.id.ijkVideoView);
        mVideoView.setMediaController(mMediaController);
//        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
//        if (mVideoPath != null)
//            mVideoView.setVideoPath(mVideoPath);
//        else if (mVideoUri != null)
//            mVideoView.setVideoURI(mVideoUri);
//        else {
//            Log.e(TAG, "Null Data Source\n");
//            finish();
//            return;
//        }
        mVideoView.setVideoPath("http://zv.3gv.ifeng.com/live/zhongwen800k.m3u8");

        mVideoView.start();


        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                switch (i) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        mDataBinding.infoNet.setVisibility(View.VISIBLE);
                        LogUtil.i("infoNewt===" + mDataBinding.infoNet.getText());
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mDataBinding.infoNet.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mBackPressed = true;
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onStop() {
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mVideoView.setFullScreen();
            mActionBar.setDisplayShowTitleEnabled(true);
            mToolbar.setTitle("title");
            mToolbar.setTitleTextColor(Color.WHITE);
            mMediaController.setSupportActionBar(mActionBar);
        } else {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mVideoView.setFitScreen();
            mActionBar.setDisplayShowTitleEnabled(false);
            mMediaController.setSupportActionBar(null);
        }

    }

    @Override
    public void fullscreen() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
           onBackPressed();
        }
//        if (id == R.id.action_toggle_ratio) {
//            int aspectRatio = mVideoView.toggleAspectRatio();
//            String aspectRatioText = MeasureHelper.getAspectRatioText(this, aspectRatio);
//            mToastTextView.setText(aspectRatioText);
//            mMediaController.showOnce(mToastTextView);
//            return true;
//        } else if (id == R.id.action_toggle_player) {
//            int player = mVideoView.togglePlayer();
//            String playerText = IjkVideoView.getPlayerText(this, player);
//            mToastTextView.setText(playerText);
//            mMediaController.showOnce(mToastTextView);
//            return true;
//        } else if (id == R.id.action_toggle_render) {
//            int render = mVideoView.toggleRender();
//            String renderText = IjkVideoView.getRenderText(this, render);
//            mToastTextView.setText(renderText);
//            mMediaController.showOnce(mToastTextView);
//            return true;
//        } else if (id == R.id.action_show_info) {
//            mVideoView.showMediaInfo();
//        } else if (id == R.id.action_show_tracks) {
//            if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
//                Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
//                if (f != null) {
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.remove(f);
//                    transaction.commit();
//                }
//                mDrawerLayout.closeDrawer(mRightDrawer);
//            } else {
//                Fragment f = TracksFragment.newInstance();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.right_drawer, f);
//                transaction.commit();
//                mDrawerLayout.openDrawer(mRightDrawer);
//            }
//        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public ITrackInfo[] getTrackInfo() {
//        if (mVideoView == null)
//            return null;
//
//        return mVideoView.getTrackInfo();
//    }
//
//    @Override
//    public void selectTrack(int stream) {
//        mVideoView.selectTrack(stream);
//    }
//
//    @Override
//    public void deselectTrack(int stream) {
//        mVideoView.deselectTrack(stream);
//    }
//
//    @Override
//    public int getSelectedTrack(int trackType) {
//        if (mVideoView == null)
//            return -1;
//
//        return mVideoView.getSelectedTrack(trackType);
//    }
}