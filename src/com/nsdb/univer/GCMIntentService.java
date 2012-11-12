package com.nsdb.univer;

import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
    private static final String tag = "GCMIntentService";
    private static final String PROJECT_ID = "543457066261";
   
    public GCMIntentService(){ this(PROJECT_ID); }
   
    public GCMIntentService(String project_id) { super(project_id); }
    
    public static String getProjectId() { return PROJECT_ID; }
 
    /** 푸시로 받은 메시지 */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle b = intent.getExtras();

        Iterator<String> iterator = b.keySet().iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            String value = b.get(key).toString();
            Log.d(tag, "onMessage. "+key+" : "+value);
        }
    }

    /**에러 발생시*/
    @Override
    protected void onError(Context context, String errorId) {
        Log.d(tag, "onError. errorId : "+errorId);
    }
 
    /**단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다*/
    @Override
    protected void onRegistered(Context context, String regId) {
        Log.d(tag, "onRegistered. regId : "+regId);
    }

    /**단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다*/
    @Override
    protected void onUnregistered(Context context, String regId) {
        Log.d(tag, "onUnregistered. regId : "+regId);
    }
}