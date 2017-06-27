package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.EmailMessageDAO;
import br.edu.utfpr.dv.siacoes.model.EmailConfig;
import br.edu.utfpr.dv.siacoes.model.EmailMessage;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
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
	
	public List<EmailMessage> listByModule(SystemModule module) throws Exception{
		try{
			EmailMessageDAO dao = new EmailMessageDAO();
			
			return dao.listByModule(module);
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
	
	public void sendEmail(int idUser, EmailMessage.MessageType type, List<EmailMessageEntry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(new String[] { bo.findEmail(idUser) }, type, keys);
	}
	
	public void sendEmail(int[] users, EmailMessage.MessageType type, List<EmailMessageEntry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(bo.findEmails(users), type, keys);
	}
	
	public void sendEmail(String[] to, EmailMessage.MessageType type, List<EmailMessageEntry<String, String>> keys) throws Exception {
		EmailMessage message = this.findByMessageType(type);
		
		this.sendEmail(to, message, keys);
	}
	
	public void sendEmail(int[] users, EmailMessage message, List<EmailMessageEntry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(bo.findEmails(users), message, keys);
	}
	
	public void sendEmail(String[] to, EmailMessage message, List<EmailMessageEntry<String, String>> keys) throws Exception {
		String msg = message.getMessage();
		
		if(keys != null){
			for(EmailMessageEntry<String, String> k : keys){
				msg = msg.replaceAll("\\{" + k.getKey() + "\\}", k.getValue());
			}
		}
		
		this.sendEmail(to, message.getSubject(), msg);
	}
	
	private void sendEmail(String[] to, String subject, String message) throws Exception{
		EmailConfigBO bo = new EmailConfigBO();
		EmailConfig config = bo.loadConfiguration();
		
		if(config == null){
			throw new Exception("É preciso configurar os dados para envio de e-mails.");
		}
		
		if((config.getSignature() != null) && !config.getSignature().trim().isEmpty()){
			message = message + "\n\n--------------\n" + config.getSignature();	
		}
		
		EmailUtils email = new EmailUtils(config.getHost(), config.getPort(), config.getUser(), config.getPassword(), config.isEnableSsl(), config.isAuthenticate());
		
		email.sendEmail(config.getUser(), to, null, null, subject, message);
	}

}
