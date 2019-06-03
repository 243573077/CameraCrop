package com.crop.camera;

import android.app.Application;

/**
 * Created by wangbin on 2019/6/3.
 */

public class App extends Application {

    public static App instance;


    private App() {
    }

    public static  App getInstance(){
        if(instance == null){
            instance = new App();
        }
        return instance;
    }
}
