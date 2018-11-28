package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.MessageDAO;
import br.edu.utfpr.dv.siacoes.model.Message;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class MessageBO {
	
	public Message findById(int idMessage) throws Exception {
		try {
			MessageDAO dao = new MessageDAO();
			
			return dao.findById(idMessage);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Message> listAll() throws Exception {
		try {
			MessageDAO dao = new MessageDAO();
			
			return dao.listAll();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int getUnreadMessages(int idUser)  throws Exception {
		try {
			MessageDAO dao = new MessageDAO();
			
			return dao.getUnreadMessages(idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Message> listByUser(int idUser) throws Exception {
		try {
			MessageDAO dao = new MessageDAO();
			
			return dao.listByUser(idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Message> listRead(int idUser, boolean read) throws Exception {
		try {
			MessageDAO dao = new MessageDAO();
			
			return dao.listRead(idUser, read);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean markAsRead(int idMessage, boolean read) throws Exception {
		try {
			MessageDAO dao = new MessageDAO();
			
			return dao.markAsRead(idMessage, read);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(Message message) throws Exception {
		if((message.getUser() == null) || (message.getUser().getIdUser() == 0)) {
			throw new Exception("Informe o destinatário.");
		}
		if(message.getTitle().trim().isEmpty()) {
			throw new Exception("Informe o título.");
		}
		if(message.getMessage().trim().isEmpty()) {
			throw new Exception("Informe a mensagem.");
		}
		
		try {
			MessageDAO dao = new MessageDAO();
			
			return dao.save(message);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int sendMessage(int idUser, String title, String message, SystemModule module) throws Exception {
		Message msg = new Message();
		
		msg.getUser().setIdUser(idUser);
		msg.setTitle(title);
		msg.setMessage(message);
		msg.setDate(DateUtils.getNow().getTime());
		msg.setModule(module);
		msg.setRead(false);
		
		return this.save(msg);
	}

}
