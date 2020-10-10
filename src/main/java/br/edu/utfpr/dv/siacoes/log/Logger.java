package br.edu.utfpr.dv.siacoes.log;

import java.util.logging.Level;

public class Logger {
	
	public static void log(Level level, String msg, Throwable thrown) {
		thrown.printStackTrace();
	}

}
