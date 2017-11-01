package com.moscase.shouhuan.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.elbbbird.android.socialsdk.SocialSDK;
import com.elbbbird.android.socialsdk.model.SocialToken;
import com.elbbbird.android.socialsdk.model.SocialUser;
import com.elbbbird.android.socialsdk.otto.BusProvider;
import com.elbbbird.android.socialsdk.otto.SSOBusEvent;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.utils.PermissionUtil;
import com.squareup.otto.Subscribe;
import com.tencent.connect.common.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.os.Environment.getExternalStorageDirectory;

public class LoginQQActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private String[] permission = {

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_qq);
        BusProvider.getInstance().register(this);


        mSharedPreferences = getSharedPreferences("myInfo", MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= 23) {
//            6.0以上系统动态申请权限
            showPermission();
        } else {
            initView();
        }


    }

    void initView() {
        //判断是否有APP图片目录
        File file = new File(getExternalStorageDirectory() + "/蓝牙手表图片");
        if (!file.exists())
            file.mkdirs();


        SocialSDK.initQQ("1106501598");
        SocialSDK.oauthQQ(this);
    }


    @Subscribe
    public void onOauthResult(SSOBusEvent event) {
        switch (event.getType()) {
            case SSOBusEvent.TYPE_GET_TOKEN:
                SocialToken token = event.getToken();
                Log.i("koma---QQ", "onOauthResult#BusEvent.TYPE_GET_TOKEN " + token.toString());
                break;
            case SSOBusEvent.TYPE_GET_USER:

                SocialUser user = event.getUser();
                mSharedPreferences.edit().putString("userName", user.getName()).commit();
                if (user.getGender() == 1) {
                    mSharedPreferences.edit().putBoolean("isMale", true).commit();

                } else if (user.getGender() == 2) {
                    mSharedPreferences.edit().putBoolean("isMale", false).commit();
                } else {
                    mSharedPreferences.edit().putBoolean("isMale", true).commit();
                }

                new MyTask(user.getAvatar()).execute();


                break;
            case SSOBusEvent.TYPE_FAILURE:
                Exception e = event.getException();
                Log.i("koma---QQ", "授权失败 " + e.toString());
                finish();
                break;
            case SSOBusEvent.TYPE_CANCEL:
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                Log.i("koma---QQ", "用户取消授权");
                finish();
                break;
        }
    }


    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void saveMyBitmap(String path, Bitmap mBitmap) {
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.d("koma---保存图片出错", e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (Exception e) {
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private class MyTask extends AsyncTask<Void, Void, Void> {

        Bitmap bitmap;
        String murl;

        public MyTask(String url) {
            murl = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("koma", "开始下载图片");
            bitmap = returnBitMap(murl);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("koma", "开始保存图片");
            saveMyBitmap(getExternalStorageDirectory() +
                    "/蓝牙手表图片/UserPhoto.jpg", bitmap);
            sendBroadcast(new Intent("com.chenhang.finish"));
        }
    }


    private void showPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission
                .READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissions();
        } else {
            initView();
        }
    }

    private void requestCameraPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
            ActivityCompat
                    .requestPermissions(this, permission,
                            123);
        } else {
            ActivityCompat.requestPermissions(this, permission, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 123) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                initView();
            } else {
                Toast.makeText(this, "请授予权限", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            SocialSDK.oauthQQCallback(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
//        finish();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }


}
