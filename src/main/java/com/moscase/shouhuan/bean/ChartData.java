package com.moscase.shouhuan.bean;

/**
 * Created by 陈航 on 2017/8/15.
 *
 * 我挥舞着键盘和本子，发誓要把世界写个明明白白
 */
public class ChartData {

	/**
	 * 16进制ARGB色彩值
	 */
	private int color;
	/**
	 * 0.25(保留两位)
	 */
	private float progress;
	/**
	 * 16进制ARGB色彩值，文本颜色
	 */
	private int textColor;
	/**
	 * 是否突出{仅扇形支持突出，环形不支持}
	 */
	private boolean isFloat;
	public ChartData() {
	}
	public ChartData(int color, float progress, int textColor, boolean isFloat) {
		this.color = color;
		this.progress = progress;
		this.textColor = textColor;
		this.isFloat = isFloat;
	}

	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public float getProgress() {
		return progress;
	}
	public void setProgress(float progress) {
		this.progress = progress;
	}
	public int getTextColor() {
		return textColor;
	}
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	public boolean isFloat() {
		return isFloat;
	}
	public void setFloat(boolean isFloat) {
		this.isFloat = isFloat;
	}
	@Override
	public String toString() {
		return "ChartData [color=" + color + ", progress=" + progress + ", textColor=" + textColor + ", isFloat="
				+ isFloat + "]";
	}
	
}
