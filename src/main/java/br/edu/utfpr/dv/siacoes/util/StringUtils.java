package br.edu.utfpr.dv.siacoes.util;

import java.util.UUID;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class StringUtils {
	
	public static String generateSHA3Hash(String s){
		SHA3.DigestSHA3 digestSHA3 = new SHA3.DigestSHA3(512);
	    byte[] digest = digestSHA3.digest(s.getBytes());

	    return Hex.toHexString(digest);
	}
	
	public static String generateSalt() {
		String s = UUID.randomUUID().toString();
		
		return s.substring(s.lastIndexOf("-") + 1);
	}
	
	public static String getFormattedBytes(int bytes) {
		String[] units = {"bytes", "KB", "MB", "GB", "TB", "PB", "YB"};
		int i = 0;
		float bytes2 = bytes;
		
		while((i < (units.length - 1)) && (bytes2 > 1024)) {
			bytes2 = bytes2 / 1024;
			i++;
		}
		
		return String.format("%.2f %s", bytes2, units[i]);
	}
	
}
