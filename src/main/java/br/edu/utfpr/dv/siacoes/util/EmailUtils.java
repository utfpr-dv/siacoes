package br.edu.utfpr.dv.siacoes.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.GeneralSecurityException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import com.sun.mail.util.MailSSLSocketFactory;

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
	
	public void sendEmailNoThread(String from, String[] to, String[] cc, String[] bcc, String subject, String body, boolean html) throws Exception {
		Session session;
		Properties props = System.getProperties();
        
        props.put("mail.smtp.host", getHost());
        props.put("mail.smtp.user", getUser());
        props.put("mail.smtp.password", getPassword());
        props.put("mail.smtp.port", getPort());
        props.put("mail.smtp.auth", (isAuthenticate() ? "true" : "false"));

        if(isEnableSsl()){
        	props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", getPort());
            props.put("mail.smtp.ssl.checkserveridentity", "true");
            
			try {
				MailSSLSocketFactory sf = new MailSSLSocketFactory();
				
				sf.setTrustedHosts(new String[] { getHost() });
	            
	            props.put("mail.smtp.ssl.socketFactory", sf);
			} catch (GeneralSecurityException e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
        }
        
        if(isAuthenticate()){
        	session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        getUser(), getPassword());
                }
            });
        }else{
        	session = Session.getDefaultInstance(props);	
        }
        
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
            
            if(html) {
            	message.setContent(body, "text/html; charset=utf-8");
            } else {
            	message.setText(body);
            }
            
            Transport.send(message, getUser(), getPassword());
        } catch (Exception e) {
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	throw e;
        }
	}
	
	public void sendEmail(String from, String[] to, String[] cc, String[] bcc, String subject, String body, boolean html) {
		Thread t = new Thread() {
		    public void run() {
		    	try {
					sendEmailNoThread(from, to, cc, bcc, subject, body, html);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
		    }
		};
		
		t.start();
    }
	
}
