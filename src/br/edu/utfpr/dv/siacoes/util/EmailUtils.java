package br.edu.utfpr.dv.siacoes.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtils {
	
	private String host;
	private boolean enableSsl;
	private String user;
	private String password;
	private int port;
	private boolean authenticate;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public boolean isEnableSsl() {
		return enableSsl;
	}

	public void setEnableSsl(boolean enableSsl) {
		this.enableSsl = enableSsl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isAuthenticate() {
		return authenticate;
	}

	public void setAuthenticate(boolean authenticate) {
		this.authenticate = authenticate;
	}
	
	public EmailUtils(String host, int port, String user, String password, boolean enableSsl, boolean authenticate){
		this.setHost(host);
		this.setPort(port);
		this.setUser(user);
		this.setPassword(password);
		this.setEnableSsl(enableSsl);
		this.setAuthenticate(authenticate);
	}

	public void sendEmail(String from, String[] to, String[] cc, String[] bcc, String subject, String body) {
        Properties props = System.getProperties();
        
        //props.put("mail.smtp.starttls.enable", (this.isEnableTls() ? "true" : "false"));
        props.put("mail.smtp.host", this.getHost());
        props.put("mail.smtp.user", this.getUser());
        props.put("mail.smtp.password", this.getPassword());
        props.put("mail.smtp.port", String.valueOf(this.getPort()));
        props.put("mail.smtp.auth", (this.isAuthenticate() ? "true" : "false"));
        
        if(this.isEnableSsl()){
            props.put("mail.smtp.socketFactory.port", String.valueOf(this.getPort()));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            
            if(to != null){
	            for(int i = 0; i < to.length; i++) {
	                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
	            }
            }
            
            if(cc != null){
	            for(int i = 0; i < cc.length; i++) {
	                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
	            }
            }
            
            if(bcc != null){
	            for(int i = 0; i < bcc.length; i++) {
	                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc[i]));
	            }
            }

            message.setSubject(subject);
            message.setText(body);
            Transport.send(message, this.getUser(), this.getPassword());
        } catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        }
    }
	
}
