package com.google.android.exoplayer.demo.materialdesignone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.exoplayer.demo.Model.ChannelLink;
import com.google.android.exoplayer.demo.PlayerActivity;
import com.google.android.exoplayer.demo.service.Storage;
import com.google.android.exoplayer.util.Util;

/**
 * Created by raj on 6/27/16.
 */
public class Exo {
    Context context;
    ChannelLink channelLink;
    String[] bitRates = {"180p", "360p", "480p", "720p"};
    String path;

    public Exo(Context context, ChannelLink channelLink){
        this.context = context;
        this.channelLink = channelLink;
        createPlayer();
    }

    private void createPlayer() {
//        if(MainActivity.dialog.isShowing()){
//            MainActivity.dialog.dismiss();
//        }
        Intent intent = new Intent(context, PlayerActivity.class);
        selectLink();
        intent.setData(Uri.parse(path));
        //intent.setData(Uri.parse("https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"));
        intent.putExtra(PlayerActivity.CONTENT_ID_EXTRA, "Apple AAC media playlist");
        intent.putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, Util.TYPE_HLS);
        intent.putExtra(PlayerActivity.PROVIDER_EXTRA, "");
        context.startActivity(intent);
    }

    private void selectLink() {
        String[] channelLinks = {channelLink.getSboxLink180(), channelLink.getSboxLink360(), channelLink.getSboxLink480(), channelLink.getSboxLink720()};
        String bitRate = Storage.getInstance().getCurrentBitRate();
        int currBitNum = 0;
        for (int i = 0; i < bitRates.length; i++) {
            if (bitRate.equalsIgnoreCase(bitRates[i])) {
                path = channelLinks[i];
                currBitNum = i;
                break;
            }
        }

        if (!path.endsWith(".m3u8")) {
            for (int j = currBitNum - 1; j >= 0; j--) {
                path = channelLinks[j];
                if (!path.endsWith(".m3u8")) {
                    continue;
                } else {
                    break;
                }
            }
        }

    }
}
