package com.google.android.exoplayer.demo.materialdesignone;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.exoplayer.demo.Model.Category;
import com.google.android.exoplayer.demo.Model.Channel;
import com.google.android.exoplayer.demo.Model.Country;
import com.google.android.exoplayer.demo.Model.Language;
import com.google.android.exoplayer.demo.PlayerActivity;
import com.google.android.exoplayer.demo.PrefActivity;
import com.google.android.exoplayer.demo.R;
import com.google.android.exoplayer.demo.SpeedTest.CallbackInterface;
import com.google.android.exoplayer.demo.SpeedTest.SpeedTest;
import com.google.android.exoplayer.demo.adapter.RecyclerViewAdapter;
import com.google.android.exoplayer.demo.service.ChannelLinkParse;
import com.google.android.exoplayer.demo.service.ChannelParseByCategory;
import com.google.android.exoplayer.demo.service.ChannelParseByCountry;
import com.google.android.exoplayer.demo.service.ChannelParseByLanguage;
import com.google.android.exoplayer.demo.service.Storage;

import com.google.android.exoplayer.demo.sqLiteDatabase.ChannelModel;
import com.google.android.exoplayer.demo.sqLiteDatabase.DBHandler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends ActionBarActivity {
    /*Toolbar toolbar;
    public static FirstTabFrag firstTabFrag;
    public static DrawerLayout drawerLayout;
    NavigationDrawerFrag drawerFrag;
    int currentFocusView = 0;*/ //before

    Toolbar toolbar;
    public static FirstTabFrag firstTabFrag;
    public static LinearLayout drawerLayout;
    LinearLayout drawerFrag;
    public static int currentFocusView = 1;
    static boolean firstGrid = true;

    static boolean favButtonClick = false;



    View mainContent;
    View drawerView;
    private ListFragment mDrawerList;

    private static Context con;

    //For double back press
    private static final long delay = 2000L;
    private boolean mRecentlyBackPressed = false;
    private Handler mExitHandler = new Handler();
    private Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mRecentlyBackPressed=false;
        }
    };

    public static ProgressDialog dialog;
    private Country tempCountry;
    private Category tempCategory;
    private Language tempLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = MainActivity.this;
        setContentView(R.layout.activity_main);

//        toolbar = (Toolbar) findViewById(R.id.app_bar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        mainContent = findViewById(R.id.fragment_place);
//        drawerFrag = (LinearLayout)findViewById(R.id.listView_Drawer);
//        drawerLayout = (LinearLayout) findViewById(R.id.drawerLayout);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        firstTabFrag = new FirstTabFrag();
        fragmentTransaction.add(R.id.fragment_place, firstTabFrag, "firstTabFrag");
        fragmentTransaction.commit();

        Storage.getInstance().setCurrentBitRate("180p");
        Storage.getInstance().setCurrentPlayer("Exo");


        NavigationDrawerFrag.selectionSpinner.setFocusable(true);
        NavigationDrawerFrag.selectionSpinner.requestFocus();

//        final Object[] quality = new Object[1];
//
//        SpeedTest speedTest = new SpeedTest(new CallbackInterface() {
//            @Override
//            public void run(Object result) {
//                quality[0] = result;
//                System.out.println("Quality in MainActivity......." + quality[0].toString());
//
//
//                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//                alertDialog.setTitle("Default Resolution Quality");
//                alertDialog.setMessage(quality[0].toString() + "p measured from Network");
//
//                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Write your code here to execute after dialog closed
//                        //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
//                        Storage.getInstance().setCurrentBitRate(quality[0].toString() + "p");
//                    }
//                });
//                alertDialog.show();
//            }
//        });
//        try {
//            speedTest.execute("");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public static Context getAppContex(){
        return MainActivity.con;
    }



    public static void populateNewData(List<Channel> channels){
        Log.d("channels.size()", "==========================" + channels.size());
        firstTabFrag.getData().clear();
        for (Channel ch : channels) {
            firstTabFrag.getData().add(ch);
        }
        firstTabFrag.getRcAdapter().notifyDataSetChanged();
        //setFirstGrid();
    }

    private static void setFirstGrid() {
        if(firstGrid && MainActivity.firstTabFrag.rView.getChildCount() > 1){
            firstTabFrag.rView.setFocusable(true);
            firstTabFrag.rView.requestFocus();
//            View firstView = MainActivity.firstTabFrag.rView.getChildAt(0);
//            firstView.setBackgroundColor(Color.parseColor("#000000"));
            currentFocusView = 2;
            firstGrid =  false;
            Storage.getInstance().setCurrentGrid(0);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent intent = new Intent(this, PrefActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            int keycode = event.getKeyCode();
            //System.out.println("Storage.getInstance().getCurrentGrid():" +Storage.getInstance().getCurrentGrid() );
            if (keycode == event.KEYCODE_DPAD_LEFT) {
                NavigationDrawerFrag.selectionSpinner.setFocusable(false);
                if (currentFocusView == 2) {
                    firstTabFrag.rView.setFocusable(true);
                    firstTabFrag.rView.requestFocus();
                    if (Storage.getInstance().getCurrentGrid() > 1) {
                       // System.out.println("Storage.getInstance().getCurrentGrid() in if currentFocus 2:" +Storage.getInstance().getCurrentGrid() );

                        View currView = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid() - 1);
                        currView.setBackgroundColor(Color.parseColor("#f9a6a6"));
                        View prevView = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid() - 2);
                        prevView.setBackgroundColor(Color.parseColor("#000000"));
                        Storage.getInstance().setCurrentGrid(Storage.getInstance().getCurrentGrid() - 1);
                    } else {
                        currentFocusView = 1;
                        View firstView = firstTabFrag.rView.getChildAt(0);
                        firstView.setBackgroundColor(Color.parseColor("#f9a6a6"));
                        if(favButtonClick){
                            NavigationDrawerFrag.favButton.setFocusable(true);
                            NavigationDrawerFrag.favButton.requestFocus();
                        }
                    }
                }

            } else if (keycode == event.KEYCODE_DPAD_RIGHT) {
                if(firstTabFrag.rView.getChildCount() > 0){
                    currentFocusView = 2;

                    /**
                     * favorite checking
                     * */
                    List<Channel> channels = Storage.getInstance().getCurrentChannelList();
                    DBHandler db = new DBHandler(MainActivity.getAppContex());
                    List<ChannelModel> favChannels = new ArrayList<ChannelModel>();
                    favChannels = db.getAllChannels();

                    if(!channels.isEmpty()) {
                        for (int i = 0; i < channels.size(); i++) {
                            for (int j = 0; j < favChannels.size(); j++) {
                                if (channels.get(i).getChannelId() == favChannels.get(j).getChannelId()) {
                                    View view = MainActivity.firstTabFrag.rView.getChildAt(i);
                                    view.setRotationX(30);
                                }
                            }
                        }
                    }
                    /**
                     * favorite checking
                     * */

                    if (Storage.getInstance().getCurrentGrid() < firstTabFrag.rView.getChildCount()) {
                        if (Storage.getInstance().getCurrentGrid() == 0) {
                            View firstView = firstTabFrag.rView.getChildAt(0);
                            firstView.setBackgroundColor(Color.parseColor("#000000"));
                            Storage.getInstance().setCurrentGrid(Storage.getInstance().getCurrentGrid() + 1);
                        } else {
                            View currView = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid() - 1);
                            currView.setBackgroundColor(Color.parseColor("#f9a6a6"));
                            View nextView = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid());
                            nextView.setBackgroundColor(Color.parseColor("#000000"));
                            Storage.getInstance().setCurrentGrid(Storage.getInstance().getCurrentGrid() + 1);
                        }
                    }
                }



            } else if (keycode == event.KEYCODE_DPAD_UP) {
                if(currentFocusView == 1){
                    Storage.getInstance().setCurrentGrid(0);
                    upButtonFunc();

                } else if (currentFocusView == 2){
                    NavigationDrawerFrag.selectionSpinner.setFocusable(false);
                    if(Storage.getInstance().getCurrentGrid() > 0){
                        if (Storage.getInstance().getCurrentGrid() > 7) {
                            View currView = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid() - 1);
                            currView.setBackgroundColor(Color.parseColor("#f9a6a6"));
                            View nextView = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid() - 9);
                            nextView.setBackgroundColor(Color.parseColor("#000000"));
                            Storage.getInstance().setCurrentGrid(Storage.getInstance().getCurrentGrid() - 8);
                        }
                    } else {
                        currentFocusView = 1;
                        upButtonFunc();
                    }
                }

            } else if (keycode == event.KEYCODE_DPAD_DOWN) {
                if(currentFocusView == 1){
                    Storage.getInstance().setCurrentGrid(0);
                    downButtonFunc();
                } else if (currentFocusView == 2) {
                    if(Storage.getInstance().getCurrentGrid() > 0){
                        if (Storage.getInstance().getCurrentGrid() + 7 < firstTabFrag.rView.getChildCount()) {
                            View currView = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid() - 1);
                            currView.setBackgroundColor(Color.parseColor("#f9a6a6"));
                            View nextView = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid() + 7);
                            nextView.setBackgroundColor(Color.parseColor("#000000"));
                            Storage.getInstance().setCurrentGrid(Storage.getInstance().getCurrentGrid() + 8);
                        }
                    } else {
                        currentFocusView = 1;
                        downButtonFunc();
                    }
                }

            } else if(keycode == event.KEYCODE_DPAD_CENTER || keycode == 66){
                if (currentFocusView == 1) {
                    if(!NavigationDrawerFrag.selectionSpinner.isFocused()){
                        if(firstTabFrag.rView.getChildCount() > 0){
                            firstTabFrag.getData().clear();
                            firstTabFrag.getRcAdapter().notifyDataSetChanged();
                        }
                        if(!NavigationDrawerFrag.favButton.isFocused()){
                            if(NavigationDrawerFrag.byCountry){
                                //final Country ct;
                                tempCountry = NavigationDrawerFrag.allCountry.get(Storage.getInstance().getCurrentDrawerItem() - 1);
                                System.out.println(tempCountry.getName());
                                String url = "http://www.livestreamer.com/api/live/0/" + tempCountry.getId();
                                dialog = ProgressDialog.show(MainActivity.getAppContex(), "Loading..", "Please wait...", true);

                                new CountryRequestAsync().execute(url);
//                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        try {
//
//                                            Storage.getInstance().setCurrentCountry(tempCategory.getName());
//                                            ChannelParseByCountry channelParseByCountry = new ChannelParseByCountry(MainActivity.this, ct.getName());
//                                            channelParseByCountry.getChannelsByCountry(response);
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                           dialog.dismiss();
//                                    }
//                                }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        error.printStackTrace();
//                                        Log.i("ERROR..", error.toString());
//                                        //Toast.makeText(MainActivity.this, "Exception occured..", Toast.LENGTH_SHORT).show();
//                                             dialog.dismiss();
//
//                                    }
//                                });
//                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                                Volley.newRequestQueue(MainActivity.this).add(stringRequest);
                            } else if(NavigationDrawerFrag.byCategory){

                               // final Category ct;
                                tempCategory = NavigationDrawerFrag.allCategory.get(Storage.getInstance().getCurrentDrawerItem() - 1);
                                System.out.println(tempCategory.getName());
                                String url = "http://www.livestreamer.com/api/live/1/" + tempCategory.getId();
                                    dialog = ProgressDialog.show(MainActivity.getAppContex(), "Loading..", "Please wait...", true);

                                new CategoryRequestAsync().execute(url);
//                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        try {
//                                            Storage.getInstance().setCurrentCategory(tempCategory.getName());
//                                            ChannelParseByCategory channelParseByCategory = new ChannelParseByCategory(MainActivity.this, tempCategory.getName());
//                                            channelParseByCategory.getData(response);
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        dialog.dismiss();
//                                    }
//                                }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        error.printStackTrace();
//                                        Log.i("ERROR..", error.toString());
//                                        //Toast.makeText(MainActivity.this, "Exception occured..", Toast.LENGTH_SHORT).show();
//                                                       dialog.dismiss();
//                                    }
//                                });
//                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                                        3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                                Volley.newRequestQueue(MainActivity.this).add(stringRequest);

                            } else if(NavigationDrawerFrag.byLanguage) {
                               // final Language ct;
                                tempLanguage = NavigationDrawerFrag.allLanguage.get(Storage.getInstance().getCurrentDrawerItem() - 1);
                                System.out.println(tempLanguage.getName());
                                String url = "http://www.livestreamer.com/api/live/2/" + tempLanguage.getId();
                                dialog = ProgressDialog.show(MainActivity.getAppContex(), "Loading..", "Please wait...", true);

                                new LanguageRequestAsync().execute(url);
//                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        try {
//                                            Storage.getInstance().setCurrentLanguage(tempLanguage.getName());
//                                            ChannelParseByLanguage channelParseByLanguage = new ChannelParseByLanguage(MainActivity.this, tempLanguage.getName());
//                                            channelParseByLanguage.getData(response);
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        dialog.dismiss();
//
//                                    }
//                                }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        error.printStackTrace();
//                                        Log.i("ERROR..", error.toString());
//                                        //Toast.makeText(MainActivity.this, "Exception occured..", Toast.LENGTH_SHORT).show();
//                                                   dialog.dismiss();
//
//
//                                    }
//                                });
//                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                                        3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                                Volley.newRequestQueue(MainActivity.this).add(stringRequest);
                            }
                        }

                    }

                } else if (currentFocusView == 2) {
                    dialog = ProgressDialog.show(this, "Loading..", "Please wait...", true);
                    List<Channel> channels = new ArrayList<>();

                    if (NavigationDrawerFrag.byCountry) {
                        channels = Storage.getInstance().getCurrentChannelList();
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
                    } else if (NavigationDrawerFrag.byCategory) {
                        channels = Storage.getInstance().getCurrentChannelList();
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
                    } else if (NavigationDrawerFrag.byLanguage) {
                        channels = Storage.getInstance().getCurrentChannelList();
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
                    try {
                        Channel channel = channels.get(Storage.getInstance().getCurrentGrid() - 1);
                        RecyclerViewAdapter.index = RecyclerViewAdapter.channelIds.indexOf(channel.getChannelId());
                        Log.d("LOG","CLicked channel id : "+channel.getChannelId());
                        ChannelLinkParse channelLinkParse = new ChannelLinkParse(this, channel.getChannelLink());
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Toast.makeText(view.getContext(), "Exception occured..", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if(keycode == event.KEYCODE_MENU){
                //drawerLayout.openDrawer(Gravity.LEFT);
                currentFocusView = 1;
                if(Storage.getInstance().getCurrentGrid() > 0){
                    View view = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid() - 1);
                    view.setBackgroundColor(Color.parseColor("#f9a6a6"));
                }

            } else if (keycode == event.KEYCODE_1){
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
            }

            /*
            * add a channel to favorite
            * **/
            else if(keycode == event.KEYCODE_2){
                if (currentFocusView==2){

                    Channel channelToAddFav = Storage.getInstance().getCurrentChannelList().get(Storage.getInstance().getCurrentGrid() - 1);
                    View currentView = firstTabFrag.rView.getChildAt(Storage.getInstance().getCurrentGrid() - 1);

                    DBHandler db = new DBHandler(this);
                    List<ChannelModel> favChannels = new ArrayList<ChannelModel>();
                    favChannels = db.getAllChannels();
                    boolean insert = true;
                    for (ChannelModel ch : favChannels){

                        if(channelToAddFav.getChannelId() == ch.getChannelId()){
                            Toast.makeText(this,"Removed from favorite list",Toast.LENGTH_SHORT).show();
                            currentView.setRotationX(00);
                            db.removeSingleChannel(channelToAddFav.getChannelId());
                            insert = false;
                            break;
                        }else {
                            System.out.println("fav channels primary id "+ ch.getId() + "ch id "+ ch.getChannelId() + "name "+ch.getChannelName()+ "logo"+ch.getChannelLogo());
                        }
                    }
                    if(insert){
                        db.addCahnnelToFav(new ChannelModel(channelToAddFav.getChannelId(), channelToAddFav.getChannelName(), channelToAddFav.getChannelLogoName()));
                        currentView.setRotationX(60);
                        Toast.makeText(this,"Added to favorite list",Toast.LENGTH_SHORT).show();
                    }

                }
            }

            /*
            * add a channel to favorite
            * **/

        }
        return super.dispatchKeyEvent(event);
    }

    private void upButtonFunc() {
        NavigationDrawerFrag.selectionSpinner.setFocusable(false);
        if(Storage.getInstance().getCurrentDrawerItem() == 0){
            NavigationDrawerFrag.favButton.setBackgroundColor(Color.parseColor("#FF8000"));
            NavigationDrawerFrag.favButton.setFocusable(true);
            NavigationDrawerFrag.favButton.requestFocus();

        } else if(Storage.getInstance().getCurrentDrawerItem() == 1){
            View firstView = NavigationDrawerFrag.recyclerView.getChildAt(0);
            firstView.setBackgroundColor(Color.parseColor("#37474f"));
            NavigationDrawerFrag.recyclerView.setFocusable(false);
            NavigationDrawerFrag.selectionSpinner.setFocusable(true);
            NavigationDrawerFrag.selectionSpinner.requestFocus();
            Storage.getInstance().setCurrentDrawerItem(0);
        } else if(Storage.getInstance().getCurrentDrawerItem() > 1){

            View currView = NavigationDrawerFrag.recyclerView.getChildAt(Storage.getInstance().getCurrentDrawerItem() - 1);
            currView.setBackgroundColor(Color.parseColor("#37474f"));
            View prevView = NavigationDrawerFrag.recyclerView.getChildAt(Storage.getInstance().getCurrentDrawerItem() - 2);
            prevView.setBackgroundColor(Color.parseColor("#c9c4c2"));
            Storage.getInstance().setCurrentDrawerItem(Storage.getInstance().getCurrentDrawerItem() - 1);
        }
    }

    private void downButtonFunc() {
            NavigationDrawerFrag.favButton.setFocusable(false);
            NavigationDrawerFrag.favButton.setBackgroundColor(Color.parseColor("#37474f"));
            NavigationDrawerFrag.byFavourite = false;
            favButtonClick = false;

        if(Storage.getInstance().getCurrentDrawerItem() <  NavigationDrawerFrag.recyclerView.getChildCount()){
            if(Storage.getInstance().getCurrentDrawerItem() == 0){
                if(NavigationDrawerFrag.selectionSpinner.isFocused()){
                    NavigationDrawerFrag.selectionSpinner.setFocusable(false);
                }
                View firstView = NavigationDrawerFrag.recyclerView.getChildAt(Storage.getInstance().getCurrentDrawerItem());
                firstView.setBackgroundColor(Color.parseColor("#c9c4c2"));
                Storage.getInstance().setCurrentDrawerItem(1);

            } else {
                View currView = NavigationDrawerFrag.recyclerView.getChildAt(Storage.getInstance().getCurrentDrawerItem());
                currView.setBackgroundColor(Color.parseColor("#c9c4c2"));
                View prevView = NavigationDrawerFrag.recyclerView.getChildAt(Storage.getInstance().getCurrentDrawerItem() - 1);
                prevView.setBackgroundColor(Color.parseColor("#37474f"));
                Storage.getInstance().setCurrentDrawerItem(Storage.getInstance().getCurrentDrawerItem() + 1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mRecentlyBackPressed) {
            try{
                mExitHandler.removeCallbacks(mExitRunnable);
                mExitHandler = null;
                super.onBackPressed();

            }catch(Exception e){
                e.printStackTrace();
            }

        }
        else
        {
            try{
                mRecentlyBackPressed = true;
                Toast.makeText(MainActivity.this, "press again to exit", Toast.LENGTH_SHORT).show();
                mExitHandler.postDelayed(mExitRunnable, delay);
                Storage.getInstance().setCurrentDrawerItem(0);
                Storage.getInstance().setCurrentGrid(0);
                Storage.getInstance().setCurrentChannelItem(0);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public class CountryRequestAsync extends AsyncTask<String, Void, Void> {

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
                Storage.getInstance().setCurrentCountry(tempCountry.getName());
                ChannelParseByCountry channelParseByCountry = new ChannelParseByCountry(MainActivity.this, tempCountry.getName());
                channelParseByCountry.getChannelsByCountry(buffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }

    public class LanguageRequestAsync extends AsyncTask<String, Void, Void> {

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
                Storage.getInstance().setCurrentLanguage(tempLanguage.getName());
                ChannelParseByLanguage channelParseByLanguage = new ChannelParseByLanguage(MainActivity.this, tempLanguage.getName());
                channelParseByLanguage.getData(buffer.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }


    public class CategoryRequestAsync extends AsyncTask<String, Void, Void> {

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
                Storage.getInstance().setCurrentCategory(tempCategory.getName());
                ChannelParseByCategory channelParseByCategory = new ChannelParseByCategory(MainActivity.this, tempCategory.getName());
                channelParseByCategory.getData(buffer.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }

}

