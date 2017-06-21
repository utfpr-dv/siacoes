package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.EmailMessageDAO;
import br.edu.utfpr.dv.siacoes.model.EmailConfig;
import br.edu.utfpr.dv.siacoes.model.EmailMessage;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.util.EmailUtils;

public class EmailMessageBO {
	
	public List<EmailMessage> listAll() throws Exception{
		try{
			EmailMessageDAO dao = new EmailMessageDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public EmailMessage findByMessageType(MessageType type) throws Exception{
		try{
			EmailMessageDAO dao = new EmailMessageDAO();
			
			return dao.findByMessageType(type);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public MessageType save(EmailMessage message) throws Exception{
		if(message.getSubject().trim().isEmpty()){
			throw new Exception("Informe o assunto da mensagem.");
		}
		if(message.getMessage().trim().isEmpty()){
			throw new Exception("Informe o conteúdo da mensagem.");
		}
		
		try{
			EmailMessageDAO dao = new EmailMessageDAO();
			
			return dao.save(message);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void sendEmail(int[] users, EmailMessage.MessageType type, List<Map.Entry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(bo.findEmails(users), type, keys);
	}
	
	public void sendEmail(String[] to, EmailMessage.MessageType type, List<Map.Entry<String, String>> keys) throws Exception {
		EmailMessage message = this.findByMessageType(type);
		
		this.sendEmail(to, message, keys);
	}
	
	public void sendEmail(int[] users, EmailMessage message, List<Map.Entry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(bo.findEmails(users), message, keys);
	}
	
	public void sendEmail(String[] to, EmailMessage message, List<Map.Entry<String, String>> keys) throws Exception {
		String msg = message.getMessage();
		
		for(Map.Entry<String, String> k : keys){
			msg.replaceAll(k.getKey(), k.getValue());
		}
		
		this.sendEmail(to, message.getSubject(), msg);
	}
	
	private void sendEmail(String[] to, String subject, String message) throws Exception{
		EmailConfigBO bo = new EmailConfigBO();
		EmailConfig config = bo.loadConfiguration();
		
		if(config == null){
			throw new Exception("É preciso configurar os dados para envio de e-mails.");
		}
		
		EmailUtils email = new EmailUtils(config.getHost(), config.getPort(), config.getUser(), config.getPassword(), config.isEnableSsl(), config.isAuthenticate());
		
		email.sendEmail(config.getUser(), to, null, null, subject, message);
	}

}
