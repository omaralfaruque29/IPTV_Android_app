package com.google.android.exoplayer.demo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.demo.Model.Channel;
import com.google.android.exoplayer.demo.R;
import com.google.android.exoplayer.demo.StreamingPlayer;
import com.google.android.exoplayer.demo.materialdesignone.NavigationDrawerFrag;
import com.google.android.exoplayer.demo.service.ChannelLinkParse;
import com.google.android.exoplayer.demo.service.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shams on 6/18/16.
 */
public class AdapterForCustomChannelsList extends RecyclerView.Adapter<AdapterForCustomChannelsList.AdapterForCustomChannelsListHolders> {

    private List<Channel> channelList;
    private Context context;

    public AdapterForCustomChannelsList(Context context, List<Channel> channelList) {
        this.channelList = channelList;
        this.context = context;
    }

    @Override
    public AdapterForCustomChannelsListHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_channels_row, null);
        AdapterForCustomChannelsListHolders rcv = new AdapterForCustomChannelsListHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(AdapterForCustomChannelsListHolders holder, int position) {

        holder.channelName.setText(channelList.get(position).getChannelName());
        holder.channelIcon.setImageBitmap(channelList.get(position).getChannelBitMap());
    }

    @Override
    public int getItemCount() {
        return this.channelList.size();
    }


    public class AdapterForCustomChannelsListHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView channelName;
        public ImageView channelIcon;

        public AdapterForCustomChannelsListHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            channelName = (TextView)itemView.findViewById(R.id.customRowtitleForCustomChannelList);
            channelIcon = (ImageView)itemView.findViewById(R.id.customImageForCustomChannelList);
        }

        @Override
        public void onClick(View view) {
            List<Channel> channels = null;
            channels = new ArrayList<>();

            if(NavigationDrawerFrag.byCountry){
                channels = Storage.getInstance().getChannelsByCountry().get(Storage.getInstance().getCurrentCountry());
                Log.d("TAG", "ChannelList Size : " + channels.size());

                RecyclerViewAdapter. channelIds = null;
                RecyclerViewAdapter.channelIds = new ArrayList<>();
                try{
                    for(Channel channel : channels){
                        RecyclerViewAdapter.channelIds.add(channel.getChannelId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String s ="";
                for (Integer i : RecyclerViewAdapter.channelIds){
                    s=s+"   "+i;
                }

                Log.d("TAG","Channel ids : "+s);
            }else if(NavigationDrawerFrag.byCategory){
                channels = Storage.getInstance().getChannelsByCategory().get(Storage.getInstance().getCurrentCategory());
                Log.d("TAG","ChannelList Size : "+channels.size());

                RecyclerViewAdapter.channelIds = null;
                RecyclerViewAdapter.channelIds = new ArrayList<>();
                try{
                    for(Channel channel : channels){
                        RecyclerViewAdapter.channelIds.add(channel.getChannelId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String s ="";
                for (Integer i : RecyclerViewAdapter.channelIds){
                    s=s+"   "+i;
                }

                Log.d("TAG","Channel ids : "+s);
            }else if(NavigationDrawerFrag.byLanguage){
                channels = Storage.getInstance().getChannelsByLanguage().get(Storage.getInstance().getCurrentLanguage());
                Log.d("TAG","ChannelList Size : "+channels.size());

                RecyclerViewAdapter.channelIds = null;
                RecyclerViewAdapter.channelIds = new ArrayList<>();
                try{
                    for(Channel channel : channels){
                        RecyclerViewAdapter.channelIds.add(channel.getChannelId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String s ="";
                for (Integer i : RecyclerViewAdapter.channelIds){
                    s=s+"   "+i;
                }

                Log.d("TAG","Channel ids : "+s);
            }


            try{
                Channel channel = channels.get(getPosition());
                RecyclerViewAdapter.index = RecyclerViewAdapter.channelIds.indexOf(channel.getChannelId());
                Toast.makeText(view.getContext(), "channel.getChannelId() = " + channel.getChannelId(), Toast.LENGTH_SHORT).show();
                StreamingPlayer.playerInstance.finish();
                ChannelLinkParse channelLinkParse = new ChannelLinkParse(context, channel.getChannelLink());
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(view.getContext(), "Exception occured..", Toast.LENGTH_SHORT).show();
            }



        }
    }
}
