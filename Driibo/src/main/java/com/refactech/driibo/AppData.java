
package com.refactech.driibo;

import android.app.Application;
import android.content.Context;

/**
 * Created by Issac on 7/18/13.
 */
public class AppData extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

}
