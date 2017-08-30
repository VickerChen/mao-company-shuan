package com.moscase.shouhuan.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.moscase.shouhuan.R;

/**
 * Created by 陈航 on 17/08/29.
 *
 * 少年一事能狂  敢骂天地不仁
 */
public class BottomDialog {

    private CustomDialog customDialog;

    public BottomDialog(Context context) {
        customDialog = new CustomDialog(context);
    }

    public void inflateMenu(View menu) {
        customDialog.inflateMenu(menu);
    }

    public void cancelable(boolean value) {
        customDialog.setCancelable(value);
    }

    public void canceledOnTouchOutside(boolean value) {
        customDialog.setCanceledOnTouchOutside(value);
    }


    public void show() {
        customDialog.show();
    }

    public void dismiss() {
        customDialog.dismiss();
    }

    public interface OnItemSelectedListener {
        boolean onItemSelected(int id);
    }

    private class CustomDialog extends Dialog implements View.OnClickListener {

        private final String TAG = CustomDialog.class.getName();

        private int padding;
        private LinearLayout container;

        public CustomDialog(Context context) {
            super(context);
            padding = getContext().getResources().getDimensionPixelSize(R.dimen.dimen_8_dp);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            container = new LinearLayout(getContext());
            container.setLayoutParams(params);
            container.setBackgroundColor(Color.WHITE);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(0, padding, 0, padding);
            ScrollView scrollView = new ScrollView(getContext());
            scrollView.addView(container);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(scrollView, params);
            setCancelable(true);
            setCanceledOnTouchOutside(true);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }

        public void inflateMenu(View view) {
            container.addView(view);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
