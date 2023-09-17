package com.google.android.exoplayer.demo.Model;

/**
 * Created by raj on 5/25/16.
 */
public class ChannelLink {
    private int channelId;
    private String sboxLink180;
    private String sboxLink360;
    private String sboxLink480;
    private String sboxLink720;

    private String liveStreamerLink180;
    private String liveStreamerLink360;
    private String liveStreamerLink480;
    private String liveStreamerLink720;

    public String getLiveStreamerLink720() {
        return liveStreamerLink720;
    }

    public void setLiveStreamerLink720(String liveStreamerLink720) {
        this.liveStreamerLink720 = liveStreamerLink720;
    }

    public String getLiveStreamerLink480() {
        return liveStreamerLink480;
    }

    public void setLiveStreamerLink480(String liveStreamerLink480) {
        this.liveStreamerLink480 = liveStreamerLink480;
    }

    public String getLiveStreamerLink360() {
        return liveStreamerLink360;
    }

    public void setLiveStreamerLink360(String liveStreamerLink360) {
        this.liveStreamerLink360 = liveStreamerLink360;
    }

    public String getLiveStreamerLink180() {
        return liveStreamerLink180;
    }

    public void setLiveStreamerLink180(String liveStreamerLink180) {
        this.liveStreamerLink180 = liveStreamerLink180;
    }

    public String getSboxLink720() {
        return sboxLink720;
    }

    public void setSboxLink720(String sboxLink720) {
        this.sboxLink720 = sboxLink720;
    }

    public String getSboxLink480() {
        return sboxLink480;
    }

    public void setSboxLink480(String sboxLink480) {
        this.sboxLink480 = sboxLink480;
    }

    public String getSboxLink360() {
        return sboxLink360;
    }

    public void setSboxLink360(String sboxLink360) {
        this.sboxLink360 = sboxLink360;
    }

    public String getSboxLink180() {
        return sboxLink180;
    }

    public void setSboxLink180(String sboxLink180) {
        this.sboxLink180 = sboxLink180;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }
}
