package com.project.focus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SilenceState {

    private Context __context;

    public SilenceState(Context context){
        __context = context;
    }

    public boolean getLockedState(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(__context);
        String lockedState = preferences.getString("LockedState", "unlocked");

        assert lockedState != null;
        return !lockedState.equals("unlocked");
    }

    public void setLockedState(boolean lockedState){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(__context);
        SharedPreferences.Editor editor = preferences.edit();
        String lockedString;
        if(!lockedState) lockedString = "unlocked";
        else lockedString = "locked";
        editor.putString("LockedState",lockedString);
        editor.apply();
    }

}
