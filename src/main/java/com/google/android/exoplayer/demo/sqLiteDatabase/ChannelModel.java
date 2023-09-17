package com.google.android.exoplayer.demo.sqLiteDatabase;

/**
 * Developed by rubayet on 6/22/16.
 */
public class ChannelModel {
    private Integer id;
    private Integer channelId;
    private String channelName;
    private String channelLogo;

    public ChannelModel (){

    }

    public ChannelModel(Integer channelId, String channelName, String channelLogo){
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelLogo = channelLogo;
    }

    public ChannelModel(Integer id, Integer channelId, String channelName, String channelLogo){
        this(channelId,channelName,channelLogo);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelLogo() {
        return channelLogo;
    }

    public void setChannelLogo(String channelLogo) {
        this.channelLogo = channelLogo;
    }
}
