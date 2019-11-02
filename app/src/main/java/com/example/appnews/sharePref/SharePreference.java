package com.example.appnews.sharePref;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.appnews.model.Channel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharePreference {
        public static final String PREFS_NAME = "NEWS_APP";
        public static final String FAVORITES = "News_Favorite";

        public SharePreference() {
            super();
        }
        // This four methods are used for maintaining favorites.
        public void saveFavorites(Context context, List<Channel> favorites) {
            SharedPreferences settings;
            SharedPreferences.Editor editor;

            settings = context.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
            editor = settings.edit();

            Gson gson = new Gson();
            String jsonFavorites = gson.toJson(favorites);

            editor.putString(FAVORITES, jsonFavorites);

            editor.commit();
        }

        public void addFavorite(Context context, Channel channel) {
            List<Channel> favorites = getFavorites(context);
            if (favorites == null)
                favorites = new ArrayList<Channel>();
            favorites.add(channel);
            saveFavorites(context, favorites);
        }

        public void removeFavorite(Context context, Channel channel) {
            ArrayList<Channel> favorites = getFavorites(context);
            if (favorites != null) {
                favorites.remove(channel);
                saveFavorites(context, favorites);
            }
        }
        public void ClearAll(Context context,List<Channel> channels)
        {
            List<Channel> favorites = getFavorites(context);
            if(favorites != null) {
                favorites.clear();
                saveFavorites(context, favorites);
            }
        }
        public ArrayList<Channel> getFavorites(Context context) {
            SharedPreferences settings;
            List<Channel> favorites;

            settings = context.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);

            if (settings.contains(FAVORITES)) {
                String jsonFavorites = settings.getString(FAVORITES, null);
                Gson gson = new Gson();
                Channel[] favoriteItems = gson.fromJson(jsonFavorites,
                        Channel[].class);

                favorites = Arrays.asList(favoriteItems);
                favorites = new ArrayList<Channel>(favorites);
            } else
                return null;

            return (ArrayList<Channel>) favorites;
        }
}
