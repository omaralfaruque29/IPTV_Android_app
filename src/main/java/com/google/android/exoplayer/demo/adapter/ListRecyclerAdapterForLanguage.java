package com.google.android.exoplayer.demo.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer.demo.Model.Category;
import com.google.android.exoplayer.demo.Model.Channel;
import com.google.android.exoplayer.demo.Model.Language;
import com.google.android.exoplayer.demo.R;
import com.google.android.exoplayer.demo.materialdesignone.MainActivity;
import com.google.android.exoplayer.demo.materialdesignone.NavigationDrawerFrag;
import com.google.android.exoplayer.demo.materialdesignone.categoryDetail;
import com.google.android.exoplayer.demo.service.ChannelParseByCategory;
import com.google.android.exoplayer.demo.service.ChannelParseByCountry;
import com.google.android.exoplayer.demo.service.ChannelParseByLanguage;
import com.google.android.exoplayer.demo.service.Storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shams on 6/14/16.
 */
public class ListRecyclerAdapterForLanguage extends RecyclerView.Adapter<ListRecyclerAdapterForLanguage.myViewHolder>  {

    private LayoutInflater inflater;
    public List<categoryDetail> data = new ArrayList<>();
    private Context context;
    private String currentLanguageName = null;
    private ProgressDialog dialog;

    public ListRecyclerAdapterForLanguage(Context context , List<categoryDetail> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View customRootView = inflater.inflate(R.layout.custom_row,parent,false);
        myViewHolder viewHolder = new myViewHolder(customRootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {

        categoryDetail current = data.get(position);
        holder.title.setText(current.categoryName);
        holder.icon.setImageResource(current.icon);
    }

    @Override
    public int getItemCount() {
        if (data != null){
            return data.size();
        }
        return 0;
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView icon;

        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.customRowtitle);
            icon = (ImageView) itemView.findViewById(R.id.customRowImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = ProgressDialog.show(MainActivity.getAppContex(), "Loading..", "Please wait...", true);
                    for(final Language lg : NavigationDrawerFrag.allLanguage){
                        if(lg.getName().equalsIgnoreCase((String) title.getText())){
                            String url = "http://www.livestreamer.com/api/live/2/" + lg.getId();
                            currentLanguageName = lg.getName();
                            new ListRecyclerAdapterForLanguageAsync().execute(url);

//                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    try {
//                                        Storage.getInstance().setCurrentLanguage(lg.getName());
//                                        ChannelParseByLanguage channelParseByLanguage = new ChannelParseByLanguage(context, lg.getName());
//                                        List<Channel> channels = channelParseByLanguage.getData(response);
//                                        //MainActivity.populateNewData(channels);
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    error.printStackTrace();
//                                    Log.i("ERROR..", error.toString());
//                                }
//                            });
//                            Volley.newRequestQueue(context).add(stringRequest);
                        }
                    }
                }
            });
        }
    }

    public class ListRecyclerAdapterForLanguageAsync extends AsyncTask<String, Void, Void> {

        StringBuffer buffer = null;
        @Override
        protected Void doInBackground(String... params) {

            HttpURLConnection httpURLConnection;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                buffer = new StringBuffer("");
                String line = "";
                while ((line = reader.readLine()) !=null){
                    buffer.append(line);
                }
                System.out.println("buffer.toString****************" + buffer.toString());

            } catch (Exception ex){
                ex.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Storage.getInstance().setCurrentCountry(currentLanguageName);
                ChannelParseByLanguage channelParseByLanguage = new ChannelParseByLanguage(context, currentLanguageName);
                channelParseByLanguage.getData(buffer.toString());
                dialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
