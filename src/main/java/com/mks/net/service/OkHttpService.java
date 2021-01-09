package com.mks.net.service;

import java.io.IOException;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class OkHttpService {
	
	public static String sendToFactor(String api, String mobileNumber, String otp) throws IOException{
		   OkHttpClient client = new OkHttpClient();
		   String myUrl = "http://2factor.in/API/V1/"+api+"/SMS/"+mobileNumber+"/"+otp;
           Request request = new Request.Builder()
          .url(myUrl)
		  .get()
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();
           Response response = client.newCall(request).execute();
           String result =  response.body().string();
           return result;
		}

}
