package com.google.android.exoplayer.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.exoplayer.demo.Model.Channel;
import com.google.android.exoplayer.demo.Model.ChannelLink;
import com.google.android.exoplayer.demo.adapter.AdapterForCustomChannelsList;
import com.google.android.exoplayer.demo.adapter.RecyclerViewAdapter;
import com.google.android.exoplayer.demo.interfaces.CallbackHandler;
import com.google.android.exoplayer.demo.materialdesignone.MainActivity;
import com.google.android.exoplayer.demo.service.ChannelLinkParse;
import com.google.android.exoplayer.demo.service.ChannelLinkParseAnother;
import com.google.android.exoplayer.demo.service.Storage;

/**
 * Created by raj on 5/31/16.
 */
public class StreamingPlayer extends Activity implements
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback{

    private static final String TAG = StreamingPlayer.class.getSimpleName();
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private  String path;
    public static ChannelLink channelLink;
    private int replayCounter = 0;
    public RecyclerView customChannelListView;

    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private AdapterForCustomChannelsList adapterForCustomChannelsList;
    public static Activity playerInstance;
    public int count = 0;
    public static int channelId;
    boolean colored = false;

    public static ProgressDialog dialog;
    private String[] bitRates = {"180p", "360p", "480p", "720p"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        setContentView(R.layout.mediaplayer_2);
        mPreview = (SurfaceView) findViewById(R.id.surface);
        customChannelListView = (RecyclerView) findViewById(R.id.customChannleList);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        playerInstance = StreamingPlayer.this;


        Log.d("Log", "Data Size : " + MainActivity.firstTabFrag.getData().size());
        for(Channel channel : MainActivity.firstTabFrag.getData()){
            Log.d("TAG","CHANEL NAME : "+channel.getChannelName());
        }
        adapterForCustomChannelsList = new AdapterForCustomChannelsList(this, MainActivity.firstTabFrag.getData());
        customChannelListView.setAdapter(adapterForCustomChannelsList);
        customChannelListView.setLayoutManager(new LinearLayoutManager(this));
        customChannelListView.setVisibility(View.GONE);
    }

    private void playVideo() {
        doCleanUp();
        try {

            if (path == "") {
                Toast.makeText(this, "Please edit MediaPlayerDemo_Video Activity," + " and set the path variable to your media file URL.", Toast.LENGTH_LONG).show();
            }

            Log.e("PATH", "Path = " + path);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            //replay();
            Log.d(TAG, "error*********************: can not play video: " + e.getMessage(), e);
            this.finish();
        }
    }

//    private void replay() {
//        try {
//            replayCounter++;
//            if(replayCounter <= 3){
//                Thread.sleep(1000);
//                playVideo();
//            } else {
//                replayCounter = 0;
//                onDestroy();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        Log.d(TAG, "onBufferingUpdate percent:" + percent);

    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d(TAG, "onCompletion called");
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
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
        selectLink();
        playVideo();
    }


    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
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
        if(MainActivity.dialog != null){
            MainActivity.dialog.dismiss();
        }
        if(StreamingPlayer.dialog != null){
            StreamingPlayer.dialog.dismiss();
        }
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

        Log.v(TAG, "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();

        if(MainActivity.dialog != null){
            MainActivity.dialog.dismiss();
        }
        if(StreamingPlayer.dialog != null){
            StreamingPlayer.dialog.dismiss();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keycode = event.getKeyCode();
        Log.d(TAG,""+keycode);
        if(event.getAction() == KeyEvent.ACTION_UP && keycode == KeyEvent.KEYCODE_DPAD_UP){
            Log.d("TAG", "From Up : " +Storage.getInstance().getCurrentChannelItem());
            if(customChannelListView.isFocused()){

                if(Storage.getInstance().getCurrentChannelItem() > 1){
                    RecyclerView.LayoutManager lm = customChannelListView.getLayoutManager();
                    AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders curviewHolder = (AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders) customChannelListView.findViewHolderForAdapterPosition(Storage.getInstance().getCurrentChannelItem() - 1);
                    curviewHolder.channelName.setBackgroundColor(Color.parseColor("#80000000"));
                    curviewHolder.channelIcon.setBackgroundColor(Color.parseColor("#80000000"));
                    //currView.setAlpha((float) 0.5);
                    AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders prevviewHolder = (AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders) customChannelListView.findViewHolderForAdapterPosition(Storage.getInstance().getCurrentChannelItem() - 2);
                    prevviewHolder.channelName.setBackgroundColor(Color.parseColor("#f9a6a6"));
                    prevviewHolder.channelIcon.setBackgroundColor(Color.parseColor("#f9a6a6"));
                    lm.scrollToPosition(Storage.getInstance().getCurrentChannelItem()-3);
                    Storage.getInstance().setCurrentChannelItem(Storage.getInstance().getCurrentChannelItem() - 1);

                }

            } else {
                dialog = ProgressDialog.show(StreamingPlayer.this, "Loading..", "Please wait...", true);
                RecyclerViewAdapter.index++;
                if(RecyclerViewAdapter.index > (RecyclerViewAdapter.channelIds.size()-1)){
                    RecyclerViewAdapter.index = 0;
                }
                int id = RecyclerViewAdapter.channelIds.get(RecyclerViewAdapter.index);
                Log.d(TAG, "Id is : " + id);
//                    StreamingPlayer.this.finish();
//                    new ChannelLinkParse(StreamingPlayer.this,id);
                new ChannelLinkParseAnother(StreamingPlayer.this, id, new CallbackHandler() {
                    @Override
                    public void notyfy() {
                        onPause();
                        channelLink = ChannelLinkParseAnother.AnotherChannelLink;
                        selectLink();
                        playVideo();
                    }
                });
            }

        }else if(event.getAction() == KeyEvent.ACTION_UP && keycode == KeyEvent.KEYCODE_MENU){
            if(count == 0){
                customChannelListView.setVisibility(View.VISIBLE);
                count=1;
            }else if(count == 1){
                customChannelListView.setVisibility(View.GONE);
                count = 0;
            }


        } else if (event.getAction() == KeyEvent.ACTION_UP && keycode == KeyEvent.KEYCODE_DPAD_DOWN){
            Log.d("TAG", "From Up : " +Storage.getInstance().getCurrentChannelItem());
            if(customChannelListView.isFocused()){
                if(!colored){
                    for(int i = 0; i < customChannelListView.getChildCount(); i++){
                        customChannelListView.getChildAt(i).setBackgroundColor(Color.parseColor("#80000000"));
                    }
                    colored = true;
                }

                RecyclerView.Adapter adapter = customChannelListView.getAdapter();
                int totalChild = adapter.getItemCount();

                Log.d("TAG","TOTAL ITEM : "+totalChild);

                if(Storage.getInstance().getCurrentChannelItem() <  totalChild){
                    RecyclerView.LayoutManager lm = customChannelListView.getLayoutManager();
                    if(Storage.getInstance().getCurrentChannelItem() == 0){
                        lm.scrollToPosition(0);
                        AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders viewHolder = (AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders) customChannelListView.findViewHolderForAdapterPosition(0);
                        viewHolder.channelName.setBackgroundColor(Color.parseColor("#f9a6a6"));
                        viewHolder.channelIcon.setBackgroundColor(Color.parseColor("#f9a6a6"));
                        //View firstView = customChannelListView.getChildAt(0);
                        //firstView.setBackgroundColor(Color.parseColor("#f9a6a6"));

                        Storage.getInstance().setCurrentChannelItem(Storage.getInstance().getCurrentChannelItem() + 1);
                    } else {
                        Log.d("TAG", "Current : " + Storage.getInstance().getCurrentChannelItem());
                        lm.scrollToPosition(Storage.getInstance().getCurrentChannelItem() + 1);
                        //View currView = customChannelListView.getChildAt(Storage.getInstance().getCurrentChannelItem());
                        AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders curviewHolder = (AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders) customChannelListView.findViewHolderForAdapterPosition(Storage.getInstance().getCurrentChannelItem());
                        //currView.setBackgroundColor(Color.parseColor("#f9a6a6"));
                        curviewHolder.channelName.setBackgroundColor(Color.parseColor("#f9a6a6"));
                        curviewHolder.channelIcon.setBackgroundColor(Color.parseColor("#f9a6a6"));
                        //View prevView = customChannelListView.getChildAt(Storage.getInstance().getCurrentChannelItem() - 1);
                        AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders prevviewHolder = (AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders) customChannelListView.findViewHolderForAdapterPosition(Storage.getInstance().getCurrentChannelItem()-1);
                        prevviewHolder.channelName.setBackgroundColor(Color.parseColor("#80000000"));
                        prevviewHolder.channelIcon.setBackgroundColor(Color.parseColor("#80000000"));
                        // prevView.setAlpha((float) 0.1);
                        Storage.getInstance().setCurrentChannelItem(Storage.getInstance().getCurrentChannelItem() + 1);

                    }
                }


            } else {
                dialog = ProgressDialog.show(StreamingPlayer.this, "Loading..", "Please wait...", true);
                RecyclerViewAdapter.index--;
                if(RecyclerViewAdapter.index < 0){
                    RecyclerViewAdapter.index = RecyclerViewAdapter.channelIds.size()-1;
                }
                int id = RecyclerViewAdapter.channelIds.get(RecyclerViewAdapter.index);
                Log.d(TAG, "Id is : " + id);
//                    StreamingPlayer.this.finish();
//                    new ChannelLinkParse(StreamingPlayer.this,id);
                new ChannelLinkParseAnother(StreamingPlayer.this, id, new CallbackHandler() {
                    @Override
                    public void notyfy() {
                        onPause();
                        channelLink = ChannelLinkParseAnother.AnotherChannelLink;
                        selectLink();
                        playVideo();
                    }
                });

            }

        } else if (event.getAction() == KeyEvent.ACTION_UP && keycode == KeyEvent.KEYCODE_DPAD_CENTER){
            if(customChannelListView.isFocused()){
                dialog = ProgressDialog.show(StreamingPlayer.this, "Loading..", "Please wait...", true);
                Channel channel = Storage.getInstance().getCurrentChannelList().get(Storage.getInstance().getCurrentChannelItem() - 1);
                RecyclerViewAdapter.index = RecyclerViewAdapter.channelIds.indexOf(channel.getChannelId());
                Log.d("CHANNEL_NAME","CHANNEL NAME : "+channel.getChannelName());
                new ChannelLinkParseAnother(StreamingPlayer.this, channel.getChannelId(), new CallbackHandler() {
                    @Override
                    public void notyfy() {
                        onPause();
                        channelLink = ChannelLinkParseAnother.AnotherChannelLink;
                        selectLink();
                        playVideo();
                    }
                });
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public void selectLink(){
        String[] channelLinks = {channelLink.getSboxLink180(), channelLink.getSboxLink360(), channelLink.getSboxLink480(), channelLink.getSboxLink720()};
        String bitRate = Storage.getInstance().getCurrentBitRate();
        for (int i = 0; i < bitRates.length; i++) {
            if (bitRate.equalsIgnoreCase(bitRates[i])) {
                path = channelLinks[i];
                if (!path.endsWith(".m3u8")) {
                    for (int j = i - 1; j >= 0; j--) {
                        path = channelLinks[j];
                        if (!path.endsWith(".m3u8")) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

}