package com.mks.net.service;

public class OtpGenrator {
	
	public static String generateOTP()  
    {  
        int randomPin   =(int) (Math.random()*9000)+1000; 
        String otp  = String.valueOf(randomPin); 
        return otp;
    } 

}
