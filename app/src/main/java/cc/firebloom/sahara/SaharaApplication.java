package cc.firebloom.sahara;

import android.app.Application;
import android.content.Context;

/**
 * Created by snowhs on 20141220.
 */
public class SaharaApplication extends Application {
    private static SaharaApplication sInstance;

    public static Context getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }
}