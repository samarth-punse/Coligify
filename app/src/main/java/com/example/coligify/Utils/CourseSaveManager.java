package com.example.coligify.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.coligify.Model.CourseModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CourseSaveManager {

    private static final String PREF_NAME = "course_pref";
    private static final String KEY_FAV = "fav_courses";
    private static final String KEY_WATCH = "watch_courses";

    private static List<CourseModel> favouriteList = new ArrayList<>();
    private static List<CourseModel> watchLaterList = new ArrayList<>();

    // Call ONCE when app starts
    public static void init(Context context) {
        favouriteList = load(context, KEY_FAV);
        watchLaterList = load(context, KEY_WATCH);
    }

    public static List<CourseModel> getFavouriteList() {
        return favouriteList;
    }

    public static List<CourseModel> getWatchLaterList() {
        return watchLaterList;
    }

    public static void addToFavourite(Context context, CourseModel course) {
        if (!favouriteList.contains(course)) {
            favouriteList.add(course);
            save(context, KEY_FAV, favouriteList);
        }
    }

    public static void removeFromFavourite(Context context, CourseModel course) {
        favouriteList.remove(course);
        save(context, KEY_FAV, favouriteList);
    }

    public static void addToWatchLater(Context context, CourseModel course) {
        if (!watchLaterList.contains(course)) {
            watchLaterList.add(course);
            save(context, KEY_WATCH, watchLaterList);
        }
    }

    public static void removeFromWatchLater(Context context, CourseModel course) {
        watchLaterList.remove(course);
        save(context, KEY_WATCH, watchLaterList);
    }

    private static void save(Context context, String key, List<CourseModel> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(key, new Gson().toJson(list)).apply();
    }

    private static List<CourseModel> load(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(key, null);

        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<CourseModel>>() {}.getType();
        return new Gson().fromJson(json, type);
    }
}
