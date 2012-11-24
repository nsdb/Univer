package com.nsdb.univer.supporter;

import com.google.android.gcm.GCMRegistrar;
import android.content.Context;

public class GCMRegIdGetter {
	
	// same as GCMIntentService
    private static final String PROJECT_ID = "543457066261";
	
	public static String get(Context c) throws InterruptedException {
        GCMRegistrar.checkDevice(c);
        GCMRegistrar.checkManifest(c);
        String regId = GCMRegistrar.getRegistrationId(c);
        if(regId.compareTo("")==0) {
            GCMRegistrar.register(c, PROJECT_ID);
            while(regId.compareTo("")==0) {
            	Thread.sleep(1000);
            	regId = GCMRegistrar.getRegistrationId(c);		            	
            }
            System.out.println("GCM Register Success!");
    	} else {
        	System.out.println("GCM is Already Registered");
        }
        System.out.println("regId : "+regId);
        return regId;
		
	}

}
