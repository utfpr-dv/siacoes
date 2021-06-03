package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ReminderMessageDAO;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage.ReminderType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class ReminderMessageBO {
	
	public List<ReminderMessage> listAll() throws Exception {
		try {
			ReminderMessageDAO dao = new ReminderMessageDAO();
			
			return dao.listAll();
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<ReminderMessage> listByModule(SystemModule module) throws Exception {
		try {
			ReminderMessageDAO dao = new ReminderMessageDAO();
			
			return dao.listByModule(module);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public ReminderMessage findByReminderType(ReminderType type) throws Exception {
		try {
			ReminderMessageDAO dao = new ReminderMessageDAO();
			
			return dao.findByMessageType(type);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public ReminderType save(int idUser, ReminderMessage message) throws Exception {
		if(message.getSubject().trim().isEmpty()) {
			throw new Exception("Informe o assunto da mensagem.");
		}
		if(message.getMessage().trim().isEmpty()) {
			throw new Exception("Informe o conteúdo da mensagem.");
		}
		
		try {
			ReminderMessageDAO dao = new ReminderMessageDAO();
			
			return dao.save(idUser, message);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

	public void sendEmail(int idUser, ReminderMessage.ReminderType type, List<EmailMessageEntry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(new int[] { idUser }, new String[] { bo.findEmail(idUser) }, type, keys, true);
	}
	
	public void sendEmail(int idUser, ReminderMessage.ReminderType type, List<EmailMessageEntry<String, String>> keys, boolean writeMessage) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(new int[] { idUser }, new String[] { bo.findEmail(idUser) }, type, keys, writeMessage);
	}
	
	public void sendEmail(int[] users, ReminderMessage.ReminderType type, List<EmailMessageEntry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(users, bo.findEmails(users), type, keys, true);
	}
	
	public void sendEmail(int users[], String[] to, ReminderMessage.ReminderType type, List<EmailMessageEntry<String, String>> keys, boolean writeMessage) throws Exception {
		ReminderMessage message = this.findByReminderType(type);
		
		this.sendEmail(users, to, message, keys, true);
	}
	
	public void sendEmail(int[] users, ReminderMessage message, List<EmailMessageEntry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(users, bo.findEmails(users), message, keys, true);
	}
	
	public void sendEmail(int[] users, String[] to, ReminderMessage message, List<EmailMessageEntry<String, String>> keys, boolean writeMessage) throws Exception {
		String msg = this.getMessage(message, keys);
		
		new EmailMessageBO().sendEmail(users, to, message.getSubject(), msg, message.getModule(), true, false, writeMessage);
	}
	
	public void sendEmailNoThread(int[] users, String[] to, ReminderMessage message, List<EmailMessageEntry<String, String>> keys, boolean writeMessage) throws Exception {
		String msg = this.getMessage(message, keys);
		
		new EmailMessageBO().sendEmail(users, to, message.getSubject(), msg, message.getModule(), false, false, writeMessage);
	}
	
	public String getMessage(ReminderMessage message, List<EmailMessageEntry<String, String>> keys) {
		String msg = message.getMessage();
		
		if(keys != null){
			for(EmailMessageEntry<String, String> k : keys){
				msg = msg.replaceAll("\\{" + k.getKey() + "\\}", k.getValue());
			}
		}
		
		return msg;
	}
	
}
