package com.google.android.exoplayer.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.demo.Model.Channel;
import com.google.android.exoplayer.demo.PlayerActivity;
import com.google.android.exoplayer.demo.R;
import com.google.android.exoplayer.demo.materialdesignone.MainActivity;
import com.google.android.exoplayer.demo.materialdesignone.NavigationDrawerFrag;
import com.google.android.exoplayer.demo.materialdesignone.categoryDetail;
import com.google.android.exoplayer.demo.service.ChannelLinkParse;
import com.google.android.exoplayer.demo.service.Storage;
import com.google.android.exoplayer.util.Util;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by raj on 5/18/16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolders> {

    private List<Channel> channelList;
    private Context context;
    public static List<Integer> channelIds = new ArrayList<Integer>();

    public static int index;

    public RecyclerViewAdapter(Context context, List<Channel> channelList) {
        this.channelList = channelList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_single, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.channelName.setText(channelList.get(position).getChannelName());
        holder.channelIcon.setImageBitmap(channelList.get(position).getChannelBitMap());

    }

    @Override
    public int getItemCount() {
        return this.channelList.size();
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView channelName;
        public ImageView channelIcon;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            channelName = (TextView)itemView.findViewById(R.id.channel_name);
            channelIcon = (ImageView)itemView.findViewById(R.id.channel_image);
        }

        @Override
        public void onClick(View view) {
            List<Channel> channels = null;
            channels = new ArrayList<>();

            if(NavigationDrawerFrag.byCountry){
                channels = Storage.getInstance().getChannelsByCountry().get(Storage.getInstance().getCurrentCountry());
                Log.d("TAG","ChannelList Size : "+channels.size());

                channelIds = null;
                channelIds = new ArrayList<>();
                try{
                    for(Channel channel : channels){
                        channelIds.add(channel.getChannelId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String s ="";
                for (Integer i : channelIds){
                    s=s+"   "+i;
                }

                Log.d("TAG","Channel ids : "+s);
            }else if(NavigationDrawerFrag.byCategory){
                channels = Storage.getInstance().getChannelsByCategory().get(Storage.getInstance().getCurrentCategory());
                Log.d("TAG","ChannelList Size : "+channels.size());

                channelIds = null;
                channelIds = new ArrayList<>();
                try{
                    for(Channel channel : channels){
                        channelIds.add(channel.getChannelId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String s ="";
                for (Integer i : channelIds){
                    s=s+"   "+i;
                }

                Log.d("TAG","Channel ids : "+s);
            }else if(NavigationDrawerFrag.byLanguage){
                channels = Storage.getInstance().getChannelsByLanguage().get(Storage.getInstance().getCurrentLanguage());
                Log.d("TAG","ChannelList Size : "+channels.size());

                channelIds = null;
                channelIds = new ArrayList<>();
                try{
                    for(Channel channel : channels){
                        channelIds.add(channel.getChannelId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String s ="";
                for (Integer i : channelIds){
                    s=s+"   "+i;
                }

                Log.d("TAG","Channel ids : "+s);
            }
            if (NavigationDrawerFrag.byFavourite){
                channels = Storage.getInstance().getCurrentChannelList();
                Log.d("TAG","ChannelList Size : "+channels.size());

                channelIds = null;
                channelIds = new ArrayList<>();
                try{
                    for(Channel channel : channels){
                        channelIds.add(channel.getChannelId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                String s ="";
                for (Integer i : channelIds){
                    s=s+"   "+i;
                }

                Log.d("TAG","Channel ids : "+s);
            }

            try{
                Channel channel = channels.get(getPosition());
                index = channelIds.indexOf(channel.getChannelId());
                Toast.makeText(view.getContext(), "c" +channel.getChannelId(), Toast.LENGTH_SHORT).show();
               // ChannelLinkParse channelLinkParse = new ChannelLinkParse(context, channel.getChannelId());
                ChannelLinkParse channelLinkParse = new ChannelLinkParse(context, channel.getChannelLink());
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(view.getContext(), "Exception occured..", Toast.LENGTH_SHORT).show();
            }

 //           Toast.makeText(view.getContext(), "Clicked Country Position = " + getPosition() + channel.getChannelName(), Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, PlayerActivity.class);
//            intent.setData(Uri.parse(channelLinkParse.sboxLink360));
//            //intent.setData(Uri.parse("https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"));
//            intent.putExtra(PlayerActivity.CONTENT_ID_EXTRA, "Apple AAC media playlist");
//            intent.putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, Util.TYPE_HLS);
//            intent.putExtra(PlayerActivity.PROVIDER_EXTRA, "");
//            context.startActivity(intent);


        }
    }
}
