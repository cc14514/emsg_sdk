package com.emsg.sdk.client.upload;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import com.google.gson.Gson;



public class TokenGenerator {
	static String ACCESS_KEY = "eau6doiUgYfcnZiqzWl_xs6amhn5VZ5jC4IPpDwd";
	static String SECRET_KEY = "QsSEMBIDMMv3SlVtIrZzLCIDQKjB5N71FdLX8Lwj";
	
	public static String uploadToken(String bucketName) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = SECRET_KEY.getBytes();           
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

        	PutPolicy putPolicy = new PutPolicy();
        	putPolicy.setScope(bucketName);
        	putPolicy.setDeadline(System.currentTimeMillis()/1000 + 24*60*60);
        	
        	byte[] encodePutpolicy = Base64.encode(new Gson().toJson(putPolicy).getBytes("utf-8"), Base64.URL_SAFE|Base64.NO_WRAP);
            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(encodePutpolicy);

            String encodedSign = Base64.encodeToString(rawHmac, Base64.URL_SAFE|Base64.NO_WRAP);
            
            return ACCESS_KEY + ":" + encodedSign + ":" + new String(encodePutpolicy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static String downloadToken(String url) {
		return "";
	}
	
}
