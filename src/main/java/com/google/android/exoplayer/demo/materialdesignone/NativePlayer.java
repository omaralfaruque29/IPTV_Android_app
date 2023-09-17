package com.google.android.exoplayer.demo.materialdesignone;

import android.content.Context;
import android.content.Intent;

import com.google.android.exoplayer.demo.Model.ChannelLink;
import com.google.android.exoplayer.demo.StreamingPlayer;

/**
 * Created by raj on 6/13/16.
 */
public class NativePlayer {
    Context context;
    ChannelLink channelLink;

    public NativePlayer(Context context, ChannelLink channelLink){
        this.context = context;
        this.channelLink = channelLink;
        createPlayer();
    }

    private void createPlayer() {
        StreamingPlayer.channelLink = channelLink;
        Intent intent = new Intent(context, StreamingPlayer.class);
        context.startActivity(intent);
    }
}
