package com.project.focus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

public class Category {

    private Context __context;
    private HashMap<String, Integer> __defaultCategoryList = new HashMap<>();

    public Category(Context context){
        __context = context;
        this.setInitialCatList();
    }

    private void setInitialCatList(){
        __defaultCategoryList.put("Study", 0x1F4DA);
        __defaultCategoryList.put("Sport", 0x1F3C0);
        __defaultCategoryList.put("Relax", 0x23F3);
    }

    public String getEmoji(int unicode){
        return new String(Character.toChars(unicode));
    }

    public Integer getEmojiUnicodeFromCategoryName(String catName){
        return __defaultCategoryList.get(catName);
    }

    public String getEmojiFromCategoryName(String catName){
        return getEmoji(__defaultCategoryList.get(catName));
    }

    public HashMap<String, Integer> getCurrentCategory(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(__context);
        String catName = preferences.getString("Category", "Study");
        HashMap<String, Integer> toReturn = new HashMap<>();
        toReturn.put(catName, getEmojiUnicodeFromCategoryName(catName));
        return toReturn;
    }

    public String currentName(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(__context);
        return preferences.getString("Category", "Study");
    }

    public HashMap<String, Integer> getAllCategories(){
        return this.__defaultCategoryList;
    }

    public void setCategory(String newCategory){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(__context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Category", newCategory);
        editor.apply();
    }


}
