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
import com.google.android.exoplayer.demo.Model.Country;
import com.google.android.exoplayer.demo.R;
import com.google.android.exoplayer.demo.materialdesignone.MainActivity;
import com.google.android.exoplayer.demo.materialdesignone.NavigationDrawerFrag;
import com.google.android.exoplayer.demo.materialdesignone.categoryDetail;
import com.google.android.exoplayer.demo.service.ChannelParseByCategory;
import com.google.android.exoplayer.demo.service.ChannelParseByCountry;
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
 * Created by shams on 6/13/16.
 */
public class ListRecyclerAdapterForCategory   extends RecyclerView.Adapter<ListRecyclerAdapterForCategory.myViewHolder> {

    private LayoutInflater inflater;
    public List<categoryDetail> data = new ArrayList<>();
    private Context context;
    private String currentCategoryName = null;
    private ProgressDialog dialog;


    public ListRecyclerAdapterForCategory(Context context , List<categoryDetail> data){
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
                    for(final Category ct : NavigationDrawerFrag.allCategory){
                        if(ct.getName().equalsIgnoreCase((String) title.getText())){
                            String url = "http://www.livestreamer.com/api/live/1/" + ct.getId();
                            currentCategoryName = ct.getName();
                            new ListRecyclerAdapterForCategoryAsync().execute(url);
//                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    try {
//                                        Storage.getInstance().setCurrentCategory(ct.getName());
//                                        ChannelParseByCategory channelParseByCategory = new ChannelParseByCategory(context, ct.getName());
//                                        List<Channel> channels = channelParseByCategory.getData(response);
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

    public class ListRecyclerAdapterForCategoryAsync extends AsyncTask<String, Void, Void> {

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
                Storage.getInstance().setCurrentCategory(currentCategoryName);
                ChannelParseByCategory channelParseByCategory = new ChannelParseByCategory(context, currentCategoryName);
                List<Channel> channels = channelParseByCategory.getData(buffer.toString());
                //MainActivity.populateNewData(channels);
                dialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
