package com.moscase.shouhuan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moscase.shouhuan.R;
import com.moscase.shouhuan.view.BottomDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.NumberPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by 陈航 on 2017/8/27.
 *
 * 少年一事能狂  敢骂天地不仁
 */

public class MyInfoActivity extends FragmentActivity {
    public static final int TAKE_PHOTO = 1;
    private static final int SELECT_PHOTO = 2;

    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.man)
    ImageView mMan;
    @BindView(R.id.woman)
    ImageView mWoman;
    @BindView(R.id.sex)
    TextView mSex;
    @BindView(R.id.bodyHeight)
    TextView mBodyHeight;
    @BindView(R.id.bodyWeight)
    TextView mBodyWeight;
    @BindView(R.id.yaowei)
    TextView mYaowei;
    @BindView(R.id.tunwei)
    TextView mTunwei;
    @BindView(R.id.birthday)
    TextView mBirthday;
    @BindView(R.id.height)
    RelativeLayout mHeight;
    @BindView(R.id.weight)
    RelativeLayout mWeight;
    @BindView(R.id.yaoweiLayout)
    RelativeLayout mYaoweiLayout;
    @BindView(R.id.tunweiLayout)
    RelativeLayout mTunweiLayout;


    private CircleImageView mCircleImageView;

    private BottomDialog mBottomDialog;

    private Button mTakePhoto;
    private Button mSelectFromPics;
    private Button mDismiss;
    private String mDate;
    private Uri imageUri;
    private SharedPreferences mSharePreferences;
    private boolean isMale = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        ButterKnife.bind(this);

        mSharePreferences = getSharedPreferences("myInfo", MODE_PRIVATE);
        mUserName.setText(mSharePreferences.getString("userName", "用户名"));
        mBodyHeight.setText(mSharePreferences.getInt("height", 170) + "");
        mBodyWeight.setText(mSharePreferences.getInt("weight", 60) + "");
        mYaowei.setText(mSharePreferences.getInt("yaowei", 70) + "");
        mTunwei.setText(mSharePreferences.getInt("tunwei", 100) + "");
        mBirthday.setText(mSharePreferences.getString("birthday", "1989/02/14"));
        isMale = mSharePreferences.getBoolean("isMale", true);
        if (isMale) {
            mMan.setImageResource(R.drawable.man1);
            mWoman.setImageResource(R.drawable.women2);
            mSex.setText( "男");
        } else {
            mMan.setImageResource(R.drawable.man2);
            mWoman.setImageResource(R.drawable.women1);
            mSex.setText("女");
        }

        //判断是否有APP图片目录
        File file = new File(getExternalStorageDirectory() + "/蓝牙手表图片");
        if (!file.exists())
            file.mkdirs();
        //格式化时间
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        mDate = sDateFormat.format(new Date());
        mCircleImageView = (CircleImageView) findViewById(R.id.photo);
        //从APP图片目录获取用户头像
        Bitmap bitmap = BitmapFactory.decodeFile(getExternalStorageDirectory() +
                "/蓝牙手表图片/UserPhoto.jpg");
        if (bitmap != null)
            mCircleImageView.setImageBitmap(bitmap);
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //显示bottomDialog
                showDialig();
            }
        });
    }

    private void showDialig() {
        mBottomDialog = new BottomDialog(this);
        mBottomDialog.cancelable(true);
        mBottomDialog.canceledOnTouchOutside(true);
        View view = View.inflate(this, R.layout.dialog_layout, null);
        mBottomDialog.inflateMenu(view);
        mBottomDialog.show();

        mTakePhoto = (Button) view.findViewById(R.id.btn1);
        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                mBottomDialog.dismiss();
            }
        });

        mSelectFromPics = (Button) view.findViewById(R.id.btn2);
        mSelectFromPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                startActivityForResult(intent, SELECT_PHOTO);
                mBottomDialog.dismiss();
            }
        });

        mDismiss = (Button) view.findViewById(R.id.btn_cancel);
        mDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomDialog.dismiss();
            }
        });

    }

    private void takePhoto() {
//      创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalStorageDirectory() + "/蓝牙手表图片",
                "IMG_" + mDate + "" + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageUri = Uri.fromFile(outputImage);

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        final Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUri));
                        mCircleImageView.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                File f = new File(getExternalStorageDirectory() + "/蓝牙手表图片",
                                        "UserPhoto.jpg");
                                try {
                                    FileOutputStream out = new FileOutputStream(f);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                    out.flush();
                                    out.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        mCircleImageView.setImageURI(uri);
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                    uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final Bitmap finalBitmap = bitmap;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                File f = new File(getExternalStorageDirectory() + "/蓝牙手表图片",
                                        "UserPhoto.jpg");
                                if (f.exists()) {
                                    f.delete();
                                }
                                try {
                                    FileOutputStream out = new FileOutputStream(f);
                                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                    out.flush();
                                    out.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.userName, R.id.man, R.id.woman, R.id.sex, R.id.bodyHeight, R.id.bodyWeight, R
            .id.yaowei, R.id.tunwei, R.id.birthday, R.id.height, R.id.weight, R.id.yaoweiLayout,
            R.id.tunweiLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userName:
                final EditText inputServer = new EditText(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请输入用户名").setView(inputServer)
                        .setNegativeButton("Cancel", null);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        mUserName.setText(inputServer.getText().toString());
                        SharedPreferences.Editor mEditor = mSharePreferences.edit();
                        mEditor.putString("userName", inputServer.getText().toString());
                        mEditor.commit();
                    }
                });
                builder.show();
                break;
            case R.id.man:
                mMan.setImageResource(R.drawable.man1);
                mWoman.setImageResource(R.drawable.women2);
                isMale = true;
                SharedPreferences.Editor mEditorMale = mSharePreferences.edit();
                mEditorMale.putBoolean("isMale", isMale).commit();
                mSex.setText("男");
                break;
            case R.id.woman:
                mWoman.setImageResource(R.drawable.women1);
                mMan.setImageResource(R.drawable.man2);
                isMale = false;
                SharedPreferences.Editor mEditorFemale = mSharePreferences.edit();
                mEditorFemale.putBoolean("isMale", isMale).commit();
                mSex.setText("女");
                break;
            case R.id.birthday:
                final DatePicker picker = new DatePicker(this);
                picker.setCanceledOnTouchOutside(true);
                picker.setUseWeight(true);
                picker.setTopPadding(ConvertUtils.toPx(this, 10));
                picker.setRangeEnd(2111, 01, 11);
                picker.setRangeStart(1900, 01, 01);
                picker.setSelectedItem(1995, 02, 21);
                picker.setResetWhileWheel(false);
                picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        mBirthday.setText(year + "/" + month + "/" + day);
                        SharedPreferences.Editor mEditor = mSharePreferences.edit();
                        mEditor.putString("birthday", year + "/" + month + "/" + day);
                        mEditor.commit();
                    }
                });
                picker.show();
                break;
            case R.id.height:
                NumberPicker pickerHeight = new NumberPicker(this);
                pickerHeight.setWidth(pickerHeight.getScreenWidthPixels());
                pickerHeight.setCycleDisable(false);
                pickerHeight.setDividerVisible(false);
                pickerHeight.setOffset(2);//偏移量
                pickerHeight.setRange(145, 200, 1);//数字范围
                pickerHeight.setSelectedItem(Integer.parseInt(mBodyHeight.getText().toString()));
                pickerHeight.setLabel("厘米");
                pickerHeight.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
                    @Override
                    public void onNumberPicked(int index, Number item) {
                        mBodyHeight.setText(item + "");
                        SharedPreferences.Editor mEditor = mSharePreferences.edit();
                        mEditor.putInt("height", (Integer) item).commit();
                    }
                });
                pickerHeight.show();
                break;
            case R.id.weight:
                NumberPicker pickerWeight = new NumberPicker(this);
                pickerWeight.setWidth(pickerWeight.getScreenWidthPixels());
                pickerWeight.setCycleDisable(false);
                pickerWeight.setDividerVisible(false);
                pickerWeight.setOffset(2);//偏移量
                pickerWeight.setRange(40, 100, 1);//数字范围
                pickerWeight.setSelectedItem(60);
                pickerWeight.setLabel("kg");
                pickerWeight.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
                    @Override
                    public void onNumberPicked(int index, Number item) {
                        mBodyWeight.setText(item + "");
                        SharedPreferences.Editor mEditor = mSharePreferences.edit();
                        mEditor.putInt("weight", (Integer) item).commit();
                    }
                });
                pickerWeight.show();
                break;
            case R.id.yaoweiLayout:
                NumberPicker pickerYaowei = new NumberPicker(this);
                pickerYaowei.setWidth(pickerYaowei.getScreenWidthPixels());
                pickerYaowei.setCycleDisable(false);
                pickerYaowei.setDividerVisible(false);
                pickerYaowei.setOffset(2);//偏移量
                pickerYaowei.setRange(40, 120, 1);//数字范围
                pickerYaowei.setSelectedItem(70);
                pickerYaowei.setLabel("cm");
                pickerYaowei.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
                    @Override
                    public void onNumberPicked(int index, Number item) {
                        mYaowei.setText(item + "");
                        SharedPreferences.Editor mEditor = mSharePreferences.edit();
                        mEditor.putInt("yaowei", (Integer) item).commit();
                    }
                });
                pickerYaowei.show();
                break;
            case R.id.tunweiLayout:
                NumberPicker pickerTunwei = new NumberPicker(this);
                pickerTunwei.setWidth(pickerTunwei.getScreenWidthPixels());
                pickerTunwei.setCycleDisable(false);
                pickerTunwei.setDividerVisible(false);
                pickerTunwei.setOffset(2);//偏移量
                pickerTunwei.setRange(40, 120, 1);//数字范围
                pickerTunwei.setSelectedItem(70);
                pickerTunwei.setLabel("cm");
                pickerTunwei.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
                    @Override
                    public void onNumberPicked(int index, Number item) {
                        mTunwei.setText(item + "");
                        SharedPreferences.Editor mEditor = mSharePreferences.edit();
                        mEditor.putInt("tunwei", (Integer) item).commit();
                    }
                });
                pickerTunwei.show();
                break;
        }
    }
}
