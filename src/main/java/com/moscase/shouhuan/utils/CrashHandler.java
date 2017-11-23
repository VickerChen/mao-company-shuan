package com.moscase.shouhuan.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.UncaughtExceptionHandler;

/**
 * 异常信息上报
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private static final String PATH =
            Environment.getExternalStorageDirectory() + "/Crash/log/";

    private static final String FILE_NAME = "_crash";

    private static final String FILE_NAME_SUFFIX = ".log";

    private static CrashHandler sInstance = new CrashHandler();

    private UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;


    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        // 导出异常信息到 SD 卡中
        dumpExceptionToSDCard(throwable);

        throwable.printStackTrace();

        // 如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, throwable);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private void dumpExceptionToSDCard(Throwable ex) {
        // 如果 SD 卡不存在或无法使用，则无法把异常信息写入 SD 卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.w(TAG, "sdcard unmounted, skip dump exception");
            }
            return;
        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager
                .GET_ACTIVITIES);
        // App 版本号
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print("_");
        pw.println(pi.versionCode);

        // Android 系统版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        // 手机制造商
        pw.print("Model: ");
        pw.println(Build.MODEL);

        // CPU 架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }

    public void uploadFile() {
        final File f = new File(PATH);
        // 判断路径是否存在
        if (!f.exists()) {
            return;
        }


        final List<File> files = Arrays.asList(f.listFiles());
        if (files.size() == 0) {
            return;
        }
        OkGo.<String>post("https://app.moscase8.com/apps/8001D/LogForAndroid")
                .tag(this)
                .isSpliceUrl(true)
                .addFileParams("file", files)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.d("koma",response.body());
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response.body());
                            int retCode = jsonObject.optInt("ret");
                            if (retCode == 0) {
                                Log.d("koma","上传成功");
                                // 删除成功后，删除本地 crash 文件
                                for (File file : files) {
                                    boolean result = file.delete();
                                    Log.d("koma","文件删除"+result);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Log.d("koma","上传失败"+response);
                    }
                });
    }
}
