package com.brightcove.player.samples.ima.basic;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.VideoFields;
import com.brightcove.player.util.StringUtil;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import nl.ngti.beem.sdk.BMClient;
import nl.ngti.beem.sdk.BMClientDelegate;
import nl.ngti.beem.sdk.BMLogger;
import nl.ngti.beem.sdk.BMScreen;
import nl.ngti.beem.sdk.BMStyle;
import org.jetbrains.annotations.NotNull;

/**
 * This app illustrates how to use the Google IMA plugin with the
 * Brightcove Player for Android.
 *
 * @author Paul Matthew Reilly (original code)
 * @author Paul Michael Reilly (added explanatory comments)
 */
public class MainActivity extends BrightcovePlayerAppCompat {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private GoogleIMAComponent googleIMAComponent;
    private BrightcoveMediaController mediaController;

    //    private Soundpays soundpays;
    private BMClient bmClient;

    private Handler handler = new Handler(Looper.getMainLooper());
    private BMClientDelegate bmClientDelegate;
    private BMLogger bmLogger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.ima_activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        mediaController = new BrightcoveMediaController(brightcoveVideoView);
        brightcoveVideoView.setMediaController(mediaController);
        super.onCreate(savedInstanceState);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin and establish
        // a playlist listener object for our sample video: the Potter Puppet show.
        setupGoogleIMA();

        // Remove the HLS_URL field from the catalog request to allow
        // midrolls to work.  Midrolls don't work with HLS due to
        // seeking bugs in the Android OS.
        Map<String, String> options = new HashMap<String, String>();
        List<String> values = new ArrayList<String>(Arrays.asList(VideoFields.DEFAULT_FIELDS));
        values.remove(VideoFields.HLS_URL);
        options.put("video_fields", StringUtil.join(values, ","));

        Catalog catalog = new Catalog(eventEmitter, getString(R.string.account_id), getString(R.string.policy_key));
        catalog.findPlaylistByReferenceID("play_2017_4_videos", new PlaylistListener() {
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
            }

            public void onError(String error) {
                Log.e(TAG, error);
            }
        });

//        soundpays = Soundpays.getInstance(getApplication());
        bmClient = BMClient.getInstance(getApplication(), "bluewin");
        bmClient.onActivityCreated(this, BMStyle.LIGHT);
        bmClientDelegate = new BMClientDelegate() {
            @Override
            public void introduction(@NotNull Function1<? super Boolean, Unit> function1) {
                function1.invoke(false);
            }

            @Override
            public void willShowBeemView(@NotNull BMScreen bmScreen) {

            }

            @Override
            public void willHideBeemView() {

            }
        };
        bmClient.setDelegate(bmClientDelegate);
        bmLogger = new BMLogger() {
            @Override
            public void v(@NotNull String s) {
                Log.v("bmclient", s);
            }

            @Override
            public void d(@NotNull String s) {
                Log.d("bmclient", s);
            }

            @Override
            public void i(@NotNull String s) {
                Log.i("bmclient", s);
            }

            @Override
            public void w(@NotNull String s) {
                Log.w("bmclient", s);
            }

            @Override
            public void e(@NotNull String s) {
                Log.e("bmclient", s);
            }

            @Override
            public void e(@NotNull Throwable throwable) {
                Log.e("bmclient", "error", throwable);
            }
        };
        bmClient.setLogger(bmLogger);
        startSoundScan();
    }

    private void startSoundScan() {
        soundScan();
    }

    private void soundScan() {
        System.out.println("lalala start sound scan");
//        soundpays.stopAudioScan(new SoundpaysCancelAudioCallback() {
//            @Override
//            public void onCancelComplete() {
//
//            }
//        });
//        soundpays.beginAudioScan(new SoundpaysAudioCallback() {
//            @Override
//            public void onSuccess() {
//                System.out.println("lalala success "+ getCode());
//                handler.postDelayed(() -> soundScan(), 2000);
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                System.out.println("lalala "+ e);
//                e.printStackTrace();
//                handler.postDelayed(() -> soundScan(), 2000);
//            }
//        });
    }

    /**
     * Provide a sample illustrative ad.
     */
    private String[] googleAds = {
        // Honda Pilot
        "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fhanna_MA_group%2Fvideo_comp_app&ciu_szs=&impl=s&gdfp_req=1&env=vp"
            + "&output=xml_vast2&unviewed_position_start=1&m_ast=vast&url=[referrer_url]&correlator=[timestamp]"
    };

    /**
     * Specify where the ad should interrupt the main video.  This code provides a procedural
     * abastraction for the Google IMA Plugin setup code.
     */
    private void setupCuePoints(Source source) {
        String cuePointType = "ad";
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> details = new HashMap<String, Object>();

        // preroll
        CuePoint cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);

        // midroll at 10 seconds.
        // Due HLS bugs in the Android MediaPlayer, midrolls are not supported.
        if (!source.getDeliveryType().equals(DeliveryType.HLS)) {
            int cuepointTime = 10 * (int) DateUtils.SECOND_IN_MILLIS;
            cuePoint = new CuePoint(cuepointTime, cuePointType, properties);
            details.put(Event.CUE_POINT, cuePoint);
            eventEmitter.emit(EventType.SET_CUE_POINT, details);
            // Add a marker where the ad will be.
            mediaController.getBrightcoveSeekBar().addMarker(cuepointTime);
        }

        // postroll
        cuePoint = new CuePoint(CuePoint.PositionType.AFTER, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);
    }

    /**
     * Setup the Brightcove IMA Plugin: add some cue points; establish a factory object to
     * obtain the Google IMA SDK instance.
     */
    private void setupGoogleIMA() {

        // Defer adding cue points until the set video event is triggered.
        eventEmitter.on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                setupCuePoints((Source) event.properties.get(Event.SOURCE));
            }
        });

        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging of ad starts
        eventEmitter.on(EventType.AD_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable logging of any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable logging of ad completions.
        eventEmitter.on(EventType.AD_COMPLETED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Set up a listener for initializing AdsRequests. The Google IMA plugin emits an ad
        // request event in response to each cue point event.  The event processor (handler)
        // illustrates how to play ads back to back.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Create a container object for the ads to be presented.
                AdDisplayContainer container = sdkFactory.createAdDisplayContainer();
                container.setPlayer(googleIMAComponent.getVideoAdPlayer());
                container.setAdContainer(brightcoveVideoView);

                // Populate the container with the companion ad slots.
                ArrayList<CompanionAdSlot> companionAdSlots = new ArrayList<CompanionAdSlot>();
                CompanionAdSlot companionAdSlot = sdkFactory.createCompanionAdSlot();
                ViewGroup adFrame = (ViewGroup) findViewById(R.id.ad_frame);
                companionAdSlot.setContainer(adFrame);
                companionAdSlot.setSize(adFrame.getWidth(), adFrame.getHeight());
                companionAdSlots.add(companionAdSlot);
                container.setCompanionSlots(companionAdSlots);

                // Build the list of ads request objects, one per ad
                // URL, and point each to the ad display container
                // created above.
                ArrayList<AdsRequest> adsRequests = new ArrayList<AdsRequest>(googleAds.length);
                AdsLoader loader = sdkFactory.createAdsLoader(MainActivity.this);
                for (String adURL : googleAds) {
                    AdsRequest adsRequest = sdkFactory.createAdsRequest();
                    adsRequest.setAdTagUrl(adURL);
//                    adsRequest.setAdDisplayContainer(container);
                    loader.requestAds(adsRequest);
                    adsRequests.add(adsRequest);
                }

                // Respond to the event with the new ad requests.
                event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
                eventEmitter.respond(event);
            }
        });

        // Create the Brightcove IMA Plugin and register the event emitter so that the plugin
        // can deal with video events.
        googleIMAComponent = new GoogleIMAComponent(brightcoveVideoView, eventEmitter);
    }
}
