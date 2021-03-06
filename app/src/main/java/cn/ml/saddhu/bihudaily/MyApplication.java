package cn.ml.saddhu.bihudaily;

import android.app.Application;
import android.os.Environment;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;

import cn.ml.saddhu.bihudaily.engine.db.DBHelper;
import cn.ml.saddhu.bihudaily.engine.imageloader.ImageLoader;
import cn.ml.saddhu.bihudaily.engine.imageloader.ImageLoaderConfiguration;
import cn.ml.saddhu.bihudaily.engine.util.ConfigurationManager;
import cn.sadhu.share_library.constant.Constants;

/**
 * Created by sadhu on 2016/11/7.
 * Email static.sadhu@gmail.com
 * Describe:
 */
public class MyApplication extends Application {
    public static final String TAG = "MyApplication";
    public static Application mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MyApplication onCreate");
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        mContext = this;
        LeakCanary.install(this);
        WbSdk.install(this, new AuthInfo(this, getString(R.string.weibo_app_id), Constants.REDIRECT_URL, Constants.SCOPE));
        if (ConfigurationManager.isLight(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                //.logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("cai")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        File directory;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            directory = getExternalCacheDir().getAbsoluteFile();
        } else {
            directory = getCacheDir().getAbsoluteFile();
        }

        DiskCacheConfig smallImageDiskCacheConfig = DiskCacheConfig
                .newBuilder(this)
                .setMaxCacheSize(100 * 1024 * 1024)
                .setBaseDirectoryPath(directory)
                .setBaseDirectoryName("zhihuImageCache")
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setSmallImageDiskCacheConfig(smallImageDiskCacheConfig)
                .build();
        Fresco.initialize(this, config);
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
        DBHelper.getInstance().init(this);

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int memoryCache = maxMemory / 8;
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .setMaxMemoryCache(memoryCache)
                .setMaxDiskCache(30 * 1024 * 1024)
                .setDiskCacheDirName("image")
                .build();
        ImageLoader.getInstance().init(configuration);
        //EventBus.builder().addIndex(new MyEventBusIndex()).build();
    }
}
