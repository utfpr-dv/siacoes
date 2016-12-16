package br.edu.utfpr.dv.siacoes.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

	public static String generateMD5Hash(String s){
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			
			m.update(s.getBytes(),0,s.length());
		    
			return new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			
			return "";
		}
	    
	}
	
}
