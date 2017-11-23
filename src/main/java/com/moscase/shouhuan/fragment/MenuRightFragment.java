package com.moscase.shouhuan.fragment;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.activity.AboutActivity;
import com.moscase.shouhuan.activity.ClockActivity;
import com.moscase.shouhuan.activity.MyInfoActivity;
import com.moscase.shouhuan.activity.TargetActivity;
import com.moscase.shouhuan.utils.PermissionUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.getExternalStorageDirectory;
import static com.tencent.open.utils.Global.getSharedPreferences;

/**
 * Created by 陈航 on 2017/8/3.
 * <p>
 * 少年一事能狂  敢骂天地不仁
 */
public class MenuRightFragment extends Fragment {
    private LinearLayout mMyInfo;
    private LinearLayout mTarget;
    private LinearLayout mClock;
    private LinearLayout mAbout;
    private CircleImageView mUserPhoto;
    private Button mLoginButton;
    private Button mExitButton;

<<<<<<< HEAD
    protected SharedPreferences mMyInfoShared;

    private SharedPreferences mSharedPreferences;
=======
    private SharedPreferences mMyInfoShared;
>>>>>>> 0ce1ed17b9c863cc06e2b2396eb75373c912243b

    private Tencent mTencent; //qq主操作对象
    private IUiListener loginListener; //授权登录监听器
    private IUiListener userInfoListener; //获取用户信息监听器
    private String scope; //获取信息的范围参数
    private UserInfo userInfo; //qq用户信息

    private String[] permission = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};


    /**
     * QQ登录
     */

    private QQLoginListener mListener;
    private UserInfo mInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.menu_layout_right, container);

<<<<<<< HEAD
        mMyInfoShared = getActivity().getSharedPreferences("myInfo", getActivity().MODE_PRIVATE);


=======
>>>>>>> 0ce1ed17b9c863cc06e2b2396eb75373c912243b
        mMyInfo = (LinearLayout) view.findViewById(R.id.myInfo);
        mMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(intent);
            }
        });

        mTarget = (LinearLayout) view.findViewById(R.id.target);
        mTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TargetActivity.class);
                startActivity(intent);
            }
        });

        mClock = (LinearLayout) view.findViewById(R.id.clock);
        mClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ClockActivity.class);
                startActivity(intent);
            }
        });

        mAbout = (LinearLayout) view.findViewById(R.id.about);
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });


        mListener = new QQLoginListener();
        // 实例化Tencent
        if (mTencent == null) {
            mTencent = Tencent.createInstance("1106501598", getActivity());
        }
        mUserPhoto = (CircleImageView) view.findViewById(R.id.yonghu);
//        mUserPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= 23) {
////            6.0以上系统动态申请权限
//                    showPermission();
//                } else {
//                    initLogin();
//                }
////                Intent intent = new Intent(getActivity(), LoginQQActivity.class);
////                startActivity(intent);
//            }
//        });



        mLoginButton = (Button) view.findViewById(R.id.login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
//            6.0以上系统动态申请权限
                    showPermission();
                } else {
                    initLogin();
                }
<<<<<<< HEAD
=======
//                Intent intent = new Intent(getActivity(), LoginQQActivity.class);
//                startActivity(intent);
            }
        });

        mMyInfoShared = getSharedPreferences("myInfo", MODE_PRIVATE);

        mLoginButton = (Button) view.findViewById(R.id.login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
//            6.0以上系统动态申请权限
                    showPermission();
                } else {
                    initLogin();
                }
>>>>>>> 0ce1ed17b9c863cc06e2b2396eb75373c912243b
            }
        });

        mExitButton = (Button) view.findViewById(R.id.exit);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTencent != null) {
                    //注销登录
                    mTencent.logout(getActivity());
<<<<<<< HEAD
                    mMyInfoShared.edit().putBoolean("islogin",false).commit();
                }
                if (deleteFile(getExternalStorageDirectory() + "/蓝牙手表图片"+"/UserPhoto.jpg"))
                    Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();

//                mUserPhoto.setImageDrawable(getDrawable(R.drawable.human));
                mUserPhoto.setImageResource(R.drawable.human);
=======
                }
>>>>>>> 0ce1ed17b9c863cc06e2b2396eb75373c912243b
                mLoginButton.setVisibility(View.VISIBLE);
                mExitButton.setVisibility(View.GONE);

            }
        });

        if (mMyInfoShared.getBoolean("islogin",false)){
            mLoginButton.setVisibility(View.GONE);
            mExitButton.setVisibility(View.VISIBLE);
        }else {
            mLoginButton.setVisibility(View.VISIBLE);
            mExitButton.setVisibility(View.GONE);
        }

        return view;
    }


    private void initLogin() {
        //判断是否有APP图片目录
        File file = new File(getExternalStorageDirectory() + "/蓝牙手表图片");
        if (!file.exists())
            file.mkdirs();
//
//        initData();

        //如果session不可用，则登录，否则说明已经登录
        if (!mTencent.isSessionValid()) {
            mTencent.login(getActivity(), "all", mListener);
        }


    }

    // 实现登录成功与否的接口
    private class QQLoginListener implements IUiListener {

        @Override
        public void onComplete(Object object) { //登录成功
            //获取openid和token
            initOpenIdAndToken(object);
            //获取用户信息
            getUserInfo();
        }

        @Override
        public void onError(UiError uiError) {  //登录失败
        }

        @Override
        public void onCancel() {                //取消登录
        }
    }

    private void initOpenIdAndToken(Object object) {
        JSONObject jb = (JSONObject) object;
        try {
            String openID = jb.getString("openid");  //openid用户唯一标识
            String access_token = jb.getString("access_token");
            String expires = jb.getString("expires_in");

            mTencent.setOpenId(openID);
            mTencent.setAccessToken(access_token, expires);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() {
        QQToken token = mTencent.getQQToken();
        mInfo = new UserInfo(getActivity(), token);
        mInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                JSONObject jb = (JSONObject) object;
                try {
                    String name = jb.getString("nickname");
                    //头像图片的url
                    String figureurl = jb.getString("figureurl_qq_2");
                    String gender = jb.getString("gender");
                    new MyTask(figureurl).execute();

                    mMyInfoShared.edit().putString("userName", name).commit();
                    if (name.equals("男")) {
                        mMyInfoShared.edit().putBoolean("isMale", true).commit();

                    } else if (name.equals("女")) {
                        mMyInfoShared.edit().putBoolean("isMale", false).commit();
                    } else {
                        mMyInfoShared.edit().putBoolean("isMale", true).commit();
                    }

                    Log.d("koma", "性别设置完毕");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResultData(requestCode, resultCode, data, mListener);
    }

    private void showPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission
                .READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissions();
        } else {
            initLogin();
        }
    }

    private void requestCameraPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
            MenuRightFragment.this.requestPermissions(permission,
                    123);
        } else {
            MenuRightFragment.this.requestPermissions(permission, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("koma", "授予了权限");

        if (requestCode == 123) {
            if (PermissionUtil.verifyPermissions(grantResults)) {

                initLogin();
            } else {
                Toast.makeText(getActivity(), "请授予权限", Toast.LENGTH_LONG).show();
            }
        }

    }

    //从连接中获取bitmap
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

    //把bitmap保存到文件夹
    public void saveMyBitmap(String path, Bitmap mBitmap) {
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.e("koma---保存图片出错", e.toString());
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
            //从APP图片目录获取用户头像
            Bitmap bitmap = BitmapFactory.decodeFile(getExternalStorageDirectory() +
                    "/蓝牙手表图片/UserPhoto.jpg");
            if (bitmap != null)
                mUserPhoto.setImageBitmap(bitmap);

<<<<<<< HEAD
            mMyInfoShared.edit().putBoolean("islogin",true).commit();
=======
>>>>>>> 0ce1ed17b9c863cc06e2b2396eb75373c912243b
            mLoginButton.setVisibility(View.GONE);
            mExitButton.setVisibility(View.VISIBLE);

        }
    }

<<<<<<< HEAD
    //删除用户头像
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

=======
>>>>>>> 0ce1ed17b9c863cc06e2b2396eb75373c912243b
    @Override
    public void onResume() {
        //从APP图片目录获取用户头像
        Bitmap bitmap = BitmapFactory.decodeFile(getExternalStorageDirectory() +
                "/蓝牙手表图片/UserPhoto.jpg");
        if (bitmap != null)
            mUserPhoto.setImageBitmap(bitmap);
        super.onResume();
    }
//
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }

}
