package com.nsdb.univer.supporter;

// Source From hoyanet.pe.kr/2015, Thanks!

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {

	public static void addNotification(Context context, Intent intent,
			int noticeId, int iconId, String ticker, String title,
			String message) {

		// 통지 객체를 생성
		// (표시 아이콘, 티커메시지, 현재 시간)
		Notification noti = new Notification(iconId, ticker,
				System.currentTimeMillis());

		// App을 구동하기 위한 PendingIntent의 생성
		PendingIntent pintent = PendingIntent
				.getActivity(context, 0, intent, 0);

		// 통지의 상세 정보 지정
		// (출처Aactivity, 제목, 내용, 선택시 구동할 App정보)
		noti.setLatestEventInfo(context, title, message, pintent);

		// 시스템에 통지 정보를 등록
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(noticeId);
		nm.notify(noticeId, noti);
	}
	
	public static void removeNotification(Context context, int noticeId) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(noticeId);
	}	
}
