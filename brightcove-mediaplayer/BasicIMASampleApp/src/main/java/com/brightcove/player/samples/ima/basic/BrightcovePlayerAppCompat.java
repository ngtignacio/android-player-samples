package com.brightcove.player.samples.ima.basic;

import android.app.PictureInPictureParams;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventLogger;
import com.brightcove.player.pictureinpicture.PictureInPictureManager;
import com.brightcove.player.util.LifecycleUtil;
import com.brightcove.player.view.BaseVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

class BrightcovePlayerAppCompat extends AppCompatActivity {
    public static final String TAG = BrightcovePlayer.class.getSimpleName();
    protected BaseVideoView brightcoveVideoView;
    private LifecycleUtil lifecycleUtil;
    private EventLogger eventLogger;
    private Bundle savedInstanceState;
    private boolean pictureInPictureEnabled;

    public BrightcovePlayerAppCompat() {
    }

    public BrightcoveVideoView getBrightcoveVideoView() {
        BrightcoveVideoView result = null;
        if (this.brightcoveVideoView instanceof BrightcoveVideoView) {
            result = (BrightcoveVideoView) this.brightcoveVideoView;
        }

        return result;
    }

    public BaseVideoView getBaseVideoView() {
        return this.brightcoveVideoView;
    }

    public void showClosedCaptioningDialog() {
        this.brightcoveVideoView.getClosedCaptioningController().showCaptionsDialog();
    }

    public void fullScreen() {
        if (!this.brightcoveVideoView.isFullScreen()) {
            this.brightcoveVideoView.getEventEmitter().emit("enterFullScreen");
        } else {
            Log.e(TAG, "The video view is already in full screen mode.");
        }

    }

    public void normalScreen() {
        if (this.brightcoveVideoView.isFullScreen()) {
            this.brightcoveVideoView.getEventEmitter().emit("exitFullScreen");
        } else {
            Log.e(TAG, "The video view is not in full screen mode!");
        }

    }

    public EventLogger getEventLogger() {
        return this.eventLogger;
    }

    public void onConfigurationChanged(Configuration configuration) {
        this.lifecycleUtil.onConfigurationChanged(configuration);
        super.onConfigurationChanged(configuration);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.brightcoveVideoView == null || this.lifecycleUtil != null && this.lifecycleUtil.baseVideoView == this.brightcoveVideoView) {
            this.savedInstanceState = savedInstanceState;
        } else {
            this.lifecycleUtil = new LifecycleUtil(this.brightcoveVideoView);
            this.attemptToRegisterPiPActivity();
            this.lifecycleUtil.onCreate(savedInstanceState, this);
            this.eventLogger = new EventLogger(this.brightcoveVideoView.getEventEmitter(), true, this.getClass().getSimpleName());
        }

    }

    private void initializeLifecycleUtil(View view) {
        if (this.brightcoveVideoView == null) {
            this.findBaseVideoView(view);
            if (this.brightcoveVideoView == null) {
                throw new IllegalStateException("A BaseVideoView must be wired up to the layout.");
            }

            this.lifecycleUtil = new LifecycleUtil(this.brightcoveVideoView);
            this.attemptToRegisterPiPActivity();
            this.lifecycleUtil.onCreate(this.savedInstanceState, this);
            this.eventLogger = new EventLogger(this.brightcoveVideoView.getEventEmitter(), true, this.getClass().getSimpleName());
        }

        this.savedInstanceState = null;
    }

    private void attemptToRegisterPiPActivity() {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                this.setPictureInPictureParams((new PictureInPictureParams.Builder()).build());
                PictureInPictureManager.getInstance().registerActivity(this, this.brightcoveVideoView);
                this.pictureInPictureEnabled = true;
            } catch (IllegalStateException var2) {
                this.pictureInPictureEnabled = false;
                Log.w(TAG, "This activity was not set to use Picture-in-Picture.");
            }
        }

    }

    private void findBaseVideoView(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();

            for (int i = 0; i < childCount; ++i) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof BaseVideoView) {
                    this.brightcoveVideoView = (BaseVideoView) child;
                    break;
                }

                this.findBaseVideoView(child);
            }
        }

    }

    public void setContentView(View view) {
        super.setContentView(view);
        this.initializeLifecycleUtil(view);
    }

    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        View contentView = this.findViewById(16908290);
        this.initializeLifecycleUtil(contentView);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        this.initializeLifecycleUtil(view);
    }

    protected void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
        this.lifecycleUtil.activityOnStart();
    }

    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
        this.lifecycleUtil.activityOnPause();
    }

    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        this.brightcoveVideoView.getEventEmitter().on("changeOrientation", new EventListener() {
            public void processEvent(Event event) {
                int orientation = event.getIntegerProperty("requestedOrientation");
                setRequestedOrientation(orientation);
            }
        });
        this.lifecycleUtil.activityOnResume();
    }

    protected void onRestart() {
        Log.v(TAG, "onRestart");
        super.onRestart();
        this.lifecycleUtil.onRestart();
    }

    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        this.brightcoveVideoView.getEventEmitter().on("activityDestroyed", new EventListener() {
            public void processEvent(Event event) {
                if (eventLogger != null) {
                    eventLogger.stop();
                }

            }
        });
        this.lifecycleUtil.activityOnDestroy();
        PictureInPictureManager.getInstance().unregisterActivity(this);
    }

    protected void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
        this.lifecycleUtil.activityOnStop();
    }

//    protected void onSaveInstanceState(final Bundle bundle) {
//        this.brightcoveVideoView.getEventEmitter().on("activitySaveInstanceState", new EventListener() {
//            @Default
//            public void processEvent(Event event) {
//                super.onSaveInstanceState(bundle);
//            }
//        });
//        this.lifecycleUtil.activityOnSaveInstanceState(bundle);
//    }

    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (this.pictureInPictureEnabled) {
            PictureInPictureManager.getInstance().onUserLeaveHint();
        }

    }

    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        PictureInPictureManager.getInstance().onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
    }
}
