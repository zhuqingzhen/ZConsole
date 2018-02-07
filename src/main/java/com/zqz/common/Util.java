package com.zqz.common;

/**
 * @author zqz
 */
public class Util {
	/**
	 * 将字节数转换为可识别的单位
	 * @param size
	 * @return
	 */
	public static String getPrintSize(long size) {
		// 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size < 1024) {
			return String.valueOf(size) + "B";
		} else {
			size = size / 1024;
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// 因为还没有到达要使用另一个单位的时候
		// 接下去以此类推
		if (size < 1024) {
			return String.valueOf(size) + "KB";
		} else {
			size = size / 1024;
		}
		if (size < 1024) {
			// 因为如果以MB为单位的话，要保留最后1位小数，
			// 因此，把此数乘以100之后再取余
			size = size * 100;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
		} else {
			// 否则如果要以GB为单位的，先除于1024再作同样的处理
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
		}
	}

	private static Integer ss = 1000;
	private static Integer mi = ss * 60;
	private static Integer hh = mi * 60;
	private static Integer dd = hh * 24;

	/**
	 * 格式化毫秒值
	 * @param ms
	 * @return
	 */
	public static String formatTime(long ms) {
		Long day = ms / dd;
		Long hour = (ms - day * dd) / hh;
		Long minute = (ms - day * dd - hour * hh) / mi;
		Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

		StringBuffer sb = new StringBuffer();
		if (day > 0) {
			sb.append(day + "天");
		}
		if (hour > 0) {
			sb.append(hour + "小时");
		}
		if (minute > 0) {
			sb.append(minute + "分");
		}
		if (second > 0) {
			sb.append(second + "秒");
		}
		if (milliSecond > 0) {
			sb.append(milliSecond + "毫秒");
		}
		return sb.toString();
	}
}
