package com.google.android.exoplayer.demo.sqLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by rubayet on 6/22/16.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favoriteList";
    private static final String TABLE_FAVORITES = "favorites";

    private static final String KEY_ID = "id";
    private static final String KEY_CHANNEL_ID = "channelId";
    private static final String KEY_CHANNEL_NAME = "channelName";
    private static final String KEY_CHANNEL_LOGO = "channelLogo";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        String CREATE_FAVORITE_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CHANNEL_ID + " INTEGER, " + KEY_CHANNEL_NAME + " TEXT, "
                + KEY_CHANNEL_LOGO + " TEXT" + ")";

        db.execSQL(CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        // Creating tables again
        onCreate(db);
    }

    public void addCahnnelToFav(ChannelModel channelToAdd){
        SQLiteDatabase db = null;
        try{
            System.out.println("add channel");
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(KEY_CHANNEL_ID, channelToAdd.getChannelId());
            values.put(KEY_CHANNEL_NAME , channelToAdd.getChannelName());
            values.put(KEY_CHANNEL_LOGO , channelToAdd.getChannelLogo());

            db.insert(TABLE_FAVORITES , null , values);
        }catch (Exception e){
            e.printStackTrace();
            System.out.print("problem");
        }finally {
            db.close();
        }
    }

    public List<ChannelModel> getAllChannels(){
        List<ChannelModel> favChannels = new ArrayList<ChannelModel>();
        String selectQuery = "SELECT * FROM " + TABLE_FAVORITES ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                ChannelModel channelModel = new ChannelModel();
                channelModel.setId(Integer.parseInt(cursor.getString(0)));
                channelModel.setChannelId(Integer.parseInt(cursor.getString(1)));
                channelModel.setChannelName(cursor.getString(2));
                channelModel.setChannelLogo(cursor.getString(3));
                favChannels.add(channelModel);
            }while (cursor.moveToNext());
        }
        return favChannels;
    }

    public ChannelModel getOneChannel(Integer channelId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, new String[]{KEY_CHANNEL_ID, KEY_CHANNEL_NAME, KEY_CHANNEL_LOGO},
                KEY_CHANNEL_ID + "=?", new String[]{String.valueOf(channelId)}, null, null, null, null);

        if (cursor!=null){
            cursor.moveToFirst();
            ChannelModel channelModel = new ChannelModel(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));
            return channelModel;
        }else
            return null;
    }

    public void removeSingleChannel(Integer channelId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, KEY_CHANNEL_ID + " = ?", new String[] { String.valueOf(channelId) });
    }
}
