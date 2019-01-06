package com.moscase.shouhuan.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentVideoRecordTextAdapter;
import com.github.florent37.camerafragment.widgets.CameraSettingsView;
import com.github.florent37.camerafragment.widgets.CameraSwitchView;
import com.github.florent37.camerafragment.widgets.FlashSwitchView;
import com.github.florent37.camerafragment.widgets.MediaActionSwitchView;
import com.github.florent37.camerafragment.widgets.RecordButton;
import com.moscase.shouhuan.R;
import com.moscase.shouhuan.service.MyBleService;
import com.moscase.shouhuan.utils.MessageEvent;
import com.moscase.shouhuan.utils.MyApplication;
import com.moscase.shouhuan.utils.PermissionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.Environment.getExternalStorageDirectory;
import static com.moscase.shouhuan.utils.MyApplication.hexStringToBytes;
import static com.moscase.shouhuan.utils.MyApplication.isEnterPhotoActivity;


/**
 * Created by 陈航 on 2017/8/25.
 * <p>
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public class PhotoActivity extends AppCompatActivity {

    public static final String FRAGMENT_TAG = "camera";
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;

    @BindView(R.id.settings_view)
    CameraSettingsView settingsView;
    @BindView(R.id.flash_switch_view)
    FlashSwitchView flashSwitchView;
    @BindView(R.id.front_back_camera_switcher)
    CameraSwitchView cameraSwitchView;
    @BindView(R.id.record_button)
    RecordButton recordButton;
    @BindView(R.id.photo_video_camera_switcher)
    MediaActionSwitchView mediaActionSwitchView;

    @BindView(R.id.record_duration_text)
    TextView recordDurationText;
    @BindView(R.id.record_size_mb_text)
    TextView recordSizeText;

    @BindView(R.id.cameraLayout)
    View cameraLayout;
    private String mDate;

    private String[] permissionCamera = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private boolean isConnect = true;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    private SharedPreferences mSharedPreferences;

    private MyBleService mMyBleService;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mSharedPreferences = getSharedPreferences("ToggleButton", MODE_PRIVATE);
        isConnect = mSharedPreferences.getBoolean("isconnected", false);
        Intent intent1 = new Intent(this, MyBleService.class);
        bindService(intent1, conn, BIND_AUTO_CREATE);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        mDate = sDateFormat.format(new java.util.Date());
        if (Build.VERSION.SDK_INT >= 23) {
//            6.0以上系统动态申请权限
            showCameranPermission();
        } else {
            addCamera();
        }


        mRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
                if (MyApplication.isBleConnect)
                    mMyBleService.write(hexStringToBytes("6f6b"));
                mHandler.postDelayed(this, 10000);
            }
        };
        mHandler.postDelayed(mRunnable, 10);


    }


    @OnClick(R.id.flash_switch_view)
    public void onFlashSwitcClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }

    @OnClick(R.id.front_back_camera_switcher)
    public void onSwitchCameraClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchCameraTypeFrontBack();
        }
    }

    @OnClick(R.id.record_button)
    public void onRecordButtonClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();

        if (cameraFragment != null) {
            cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                @Override
                public void onVideoRecorded(String filePath) {
                    Intent intent = PreviewActivity
                            .newIntentVideo(PhotoActivity
                                    .this, filePath);

                    startActivityForResult(intent,
                            REQUEST_PREVIEW_CODE);

                }

                @Override
                public void onPhotoTaken(byte[] bytes,
                                         String filePath) {
                    File file = new File(getExternalStorageDirectory() + "/蓝牙手表图片");
                    if (!file.exists())
                        file.mkdirs();
                    Intent intent = PreviewActivity
                            .newIntentPhoto(PhotoActivity
                                    .this, filePath);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent,
                            REQUEST_PREVIEW_CODE);
                }
            }, getExternalStorageDirectory() + "/蓝牙手表图片", "IMG_" + mDate);
        }
    }

    @OnClick(R.id.settings_view)
    public void onSettingsClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.openSettingDialog();
        }
    }

    @OnClick(R.id.photo_video_camera_switcher)
    public void onMediaActionSwitchClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchActionPhotoVideo();
        }
    }


    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {
        @SuppressLint("MissingPermission") final CameraFragment cameraFragment = CameraFragment
                .newInstance(new Configuration.Builder()
                        .setCamera(Configuration.CAMERA_FACE_REAR).build());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commit();

        if (cameraFragment != null) {

            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {

                @Override
                public void onCurrentCameraBack() {
                    cameraSwitchView.displayBackCamera();
                }

                @Override
                public void onCurrentCameraFront() {
                    cameraSwitchView.displayFrontCamera();
                }

                @Override
                public void onFlashAuto() {
                    flashSwitchView.displayFlashAuto();
                }

                @Override
                public void onFlashOn() {
                    flashSwitchView.displayFlashOn();
                }

                @Override
                public void onFlashOff() {
                    flashSwitchView.displayFlashOff();
                }

                @Override
                public void onCameraSetupForPhoto() {
                    mediaActionSwitchView.displayActionWillSwitchVideo();

                    recordButton.displayPhotoState();
                    flashSwitchView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCameraSetupForVideo() {
                    mediaActionSwitchView.displayActionWillSwitchPhoto();

                    recordButton.displayVideoRecordStateReady();
                    flashSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void shouldRotateControls(int degrees) {
                    ViewCompat.setRotation(cameraSwitchView, degrees);
                    ViewCompat.setRotation(mediaActionSwitchView, degrees);
                    ViewCompat.setRotation(flashSwitchView, degrees);
                    ViewCompat.setRotation(recordDurationText, degrees);
                    ViewCompat.setRotation(recordSizeText, degrees);
                }

                @Override
                public void onRecordStateVideoReadyForRecord() {
                    recordButton.displayVideoRecordStateReady();
                }

                @Override
                public void onRecordStateVideoInProgress() {
                    recordButton.displayVideoRecordStateInProgress();
                }

                @Override
                public void onRecordStatePhoto() {
                    recordButton.displayPhotoState();
                }

                @Override
                public void onStopVideoRecord() {
                    recordSizeText.setVisibility(View.GONE);
                    //cameraSwitchView.setVisibility(View.VISIBLE);
                    settingsView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStartVideoRecord(File outputFile) {
                }
            });

            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    cameraSwitchView.setEnabled(false);
                    recordButton.setEnabled(false);
                    settingsView.setEnabled(false);
                    flashSwitchView.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    cameraSwitchView.setEnabled(true);
                    recordButton.setEnabled(true);
                    settingsView.setEnabled(true);
                    flashSwitchView.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                    cameraSwitchView.setVisibility(allow ? View.VISIBLE : View.GONE);
                }

                @Override
                public void allowRecord(boolean allow) {
                    recordButton.setEnabled(allow);
                }

                @Override
                public void setMediaActionSwitchVisible(boolean visible) {
                    mediaActionSwitchView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                }
            });

            cameraFragment.setTextListener(new CameraFragmentVideoRecordTextAdapter() {
                @Override
                public void setRecordSizeText(long size, String text) {
                    recordSizeText.setText(text);
                }

                @Override
                public void setRecordSizeTextVisible(boolean visible) {
                    recordSizeText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }

                @Override
                public void setRecordDurationText(String text) {
                    recordDurationText.setText(text);
                }

                @Override
                public void setRecordDurationTextVisible(boolean visible) {
                    recordDurationText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    private void showCameranPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                .CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission
                .RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission
                .READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissions();
        } else {
            addCamera();
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
        } else {
            ActivityCompat.requestPermissions(this, permissionCamera, 123);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 123) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                addCamera();
            } else {
                Toast.makeText(this, "请授予权限", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    //    这里如果不做延迟处理的话，在刚刚进入这个Activity的时候趁相机还没初始化就立即返回的话会崩溃
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 600);
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        //我懒得重新写全局的常量了
        if (event.getMsg() == 73) {
            if (isEnterPhotoActivity) {
                recordButton.performClick();
                isEnterPhotoActivity = false;
                //拍照成功后发OK
                if (MyApplication.isBleConnect)
                mMyBleService.write(hexStringToBytes("6f6b"));
            }
        }
    }

    @Override
    protected void onResume() {
        isEnterPhotoActivity = true;
        super.onResume();
        if (MyApplication.isBleConnect)
            mMyBleService.write(hexStringToBytes("70686f746f7f"));
    }

    @Override
    protected void onPause() {
        isEnterPhotoActivity = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        isEnterPhotoActivity = false;
        mHandler.removeCallbacks(mRunnable);
        //退出界面发exit
        if (MyApplication.isBleConnect)
        mMyBleService.write(hexStringToBytes("65786974"));
        super.onDestroy();

    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mMyBleService = ((MyBleService.LocalBinder) iBinder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
