package com.google.android.exoplayer.demo.service;

import com.google.android.exoplayer.demo.Model.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raj on 5/25/16.
 */
public class Storage {
    private static Storage storage;
    private static String currentCountry;
    private static String currentCategory;
    private static String currentLanguage;
    private static Map<String, List<Channel>> channelsByCountry = new HashMap<>();
    private static Map<String, List<Channel>> channelsByCategory = new HashMap<>();
    private static Map<String, List<Channel>> channelsByLanguage = new HashMap<>();
    private List<Channel> currentChannelList = new ArrayList<Channel>();
    private String currentBitRate;
    private int currentGrid = 0;
    private int currentDrawerItem = 0;
    private int currentChannelItem = 0;
    private String currentPlayer;
    private int lastClickedDrawerItem;

    public static Storage getInstance(){
        if (storage == null){
            storage = new Storage();
        }
        return storage;
    }

    public static Storage getStorage() {
        return storage;
    }

    public static void setStorage(Storage storage) {
        Storage.storage = storage;
    }

    public static String getCurrentCountry() {
        return currentCountry;
    }

    public static void setCurrentCountry(String currentCountry) {
        Storage.currentCountry = currentCountry;
    }

    public static String getCurrentCategory() {
        return currentCategory;
    }

    public static void setCurrentCategory(String currentCategory) {
        Storage.currentCategory = currentCategory;
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    public static void setCurrentLanguage(String currentLanguage) {
        Storage.currentLanguage = currentLanguage;
    }

    public static Map<String, List<Channel>> getChannelsByCountry() {
        return channelsByCountry;
    }

    public static void setChannelsByCountry(Map<String, List<Channel>> channelsByCountry) {
        Storage.channelsByCountry = channelsByCountry;
    }

    public static Map<String, List<Channel>> getChannelsByCategory() {
        return channelsByCategory;
    }

    public static void setChannelsByCategory(Map<String, List<Channel>> channelsByCategory) {
        Storage.channelsByCategory = channelsByCategory;
    }

    public static Map<String, List<Channel>> getChannelsByLanguage() {
        return channelsByLanguage;
    }

    public static void setChannelsByLanguage(Map<String, List<Channel>> channelsByLanguage) {
        Storage.channelsByLanguage = channelsByLanguage;
    }

    public String getCurrentBitRate() {
        return currentBitRate;
    }

    public void setCurrentBitRate(String currentBitRate) {
        this.currentBitRate = currentBitRate;
    }

    public int getCurrentGrid() {
        return currentGrid;
    }

    public void setCurrentGrid(int currentGrid) {
        this.currentGrid = currentGrid;
    }

    public int getCurrentDrawerItem() {
        return currentDrawerItem;
    }

    public void setCurrentDrawerItem(int currentDrawerItem) {
        this.currentDrawerItem = currentDrawerItem;
    }

    public int getCurrentChannelItem() {
        return currentChannelItem;
    }

    public void setCurrentChannelItem(int currentChannelItem) {
        this.currentChannelItem = currentChannelItem;
    }

    public List<Channel> getCurrentChannelList() {
        return currentChannelList;
    }

    public void setCurrentChannelList(List<Channel> currentChannelList) {
        this.currentChannelList = currentChannelList;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getLastClickedDrawerItem() {
        return lastClickedDrawerItem;
    }

    public void setLastClickedDrawerItem(int lastClickedDrawerItem) {
        this.lastClickedDrawerItem = lastClickedDrawerItem;
    }
}