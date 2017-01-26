package com.brightcove.player.samples.exoplayer.video360;

import android.os.Bundle;

import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

public class MainActivity extends BrightcovePlayer {
    // Settings for HLS test video
    private static final String VIDEO_URL = "https://secure.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId=5123538633001&pubId=5028486670001&secure=true";
    private static final DeliveryType VIDEO_TYPE = DeliveryType.HLS;

    // Settings for MP4 test video
    //private static final String VIDEO_URL = "https://brightcove.hs.llnwd.net/e1/uds/pd/5028486670001/5028486670001_5123574206001_5123538633001.mp4?pubId=5028486670001&videoId=5123538633001";
    //private static final DeliveryType VIDEO_TYPE = DeliveryType.MP4;

    private static final Video.ProjectionFormat PROJECTION_FORMAT = Video.ProjectionFormat.EQUIRECTANGULAR;

    @Override
    @SuppressWarnings("ResourceType")
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        Video video = Video.createVideo(VIDEO_URL, VIDEO_TYPE, PROJECTION_FORMAT);
        brightcoveVideoView.add(video);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            brightcoveVideoView.stopPlayback();
        } else {
            brightcoveVideoView.pause();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        brightcoveVideoView.start();
    }
}