package com.perfection.newkeyboard.Helpers;

import com.perfection.newkeyboard.Model.UserModel;
import com.perfection.newkeyboard.Model.SettingModel;

/**
 * Created by mrhic on 8/16/2017.
 */

public class Singleton {

    static Singleton instance = null;

    public UserModel getUser() {
        return user;
    }

    public Singleton setUser(UserModel user) {
        this.user = user;
        return this;
    }

    public SettingModel getSettings() {
        return settings;
    }

    public Singleton setSettings(SettingModel settings) {
        this.settings = settings;
        return this;
    }

    public UserModel user;
    public SettingModel settings;

    public static Singleton GetInstance()
    {
        if (instance == null)
            instance = new Singleton();
        return instance;
    }

}
