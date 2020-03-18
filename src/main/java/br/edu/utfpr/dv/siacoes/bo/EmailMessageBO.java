package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.EmailMessageDAO;
import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.EmailConfig;
import br.edu.utfpr.dv.siacoes.model.EmailMessage;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.service.LoginService;
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
	
	public MessageType save(int idUser, EmailMessage message) throws Exception{
		if(message.getSubject().trim().isEmpty()){
			throw new Exception("Informe o assunto da mensagem.");
		}
		if(message.getMessage().trim().isEmpty()){
			throw new Exception("Informe o conteúdo da mensagem.");
		}
		
		try{
			EmailMessageDAO dao = new EmailMessageDAO();
			
			return dao.save(idUser, message);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void sendForgotPasswordEmail(User user) throws Exception {
		String token = new LoginService().generateToken(user.getLogin());
		String link = AppConfig.getInstance().getHost() + "/#!password/" + token;
		String message = "Você solicitou a recuperação de senha do sistema SIACOES<br/><br/>" +
				"Para redefinir a sua senha, acesse o link:<br/>" +
				"<a href=\"" + link + "\">" + link + "</a>";
		
		this.sendEmail(null, new String[] {user.getEmail()}, "Recuperação de Senha", message, SystemModule.GENERAL, true, true, false);
	}
	
	public void sendEmail(int idUser, EmailMessage.MessageType type, List<EmailMessageEntry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(new int[] { idUser }, new String[] { bo.findEmail(idUser) }, type, keys, true);
	}
	
	public void sendEmail(int idUser, EmailMessage.MessageType type, List<EmailMessageEntry<String, String>> keys, boolean writeMessage) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(new int[] { idUser }, new String[] { bo.findEmail(idUser) }, type, keys, writeMessage);
	}
	
	public void sendEmail(int[] users, EmailMessage.MessageType type, List<EmailMessageEntry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(users, bo.findEmails(users), type, keys, true);
	}
	
	public void sendEmail(int users[], String[] to, EmailMessage.MessageType type, List<EmailMessageEntry<String, String>> keys, boolean writeMessage) throws Exception {
		EmailMessage message = this.findByMessageType(type);
		
		this.sendEmail(users, to, message, keys, true);
	}
	
	public void sendEmail(int[] users, EmailMessage message, List<EmailMessageEntry<String, String>> keys) throws Exception {
		UserBO bo = new UserBO();
		
		this.sendEmail(users, bo.findEmails(users), message, keys, true);
	}
	
	public void sendEmail(int[] users, String[] to, EmailMessage message, List<EmailMessageEntry<String, String>> keys, boolean writeMessage) throws Exception {
		String msg = this.getMessage(message, keys);
		
		this.sendEmail(users, to, message.getSubject(), msg, message.getModule(), true, false, writeMessage);
	}
	
	public void sendEmailNoThread(int[] users, String[] to, EmailMessage message, List<EmailMessageEntry<String, String>> keys, boolean writeMessage) throws Exception {
		String msg = this.getMessage(message, keys);
		
		this.sendEmail(users, to, message.getSubject(), msg, message.getModule(), false, false, writeMessage);
	}
	
	public String getMessage(EmailMessage message, List<EmailMessageEntry<String, String>> keys) {
		String msg = message.getMessage();
		
		if(keys != null){
			for(EmailMessageEntry<String, String> k : keys){
				msg = msg.replaceAll("\\{" + k.getKey() + "\\}", k.getValue());
			}
		}
		
		return msg;
	}
	
	private void sendEmail(int users[], String[] to, String subject, String message, SystemModule module, boolean useThread, boolean html, boolean writeMessage) throws Exception{
		EmailConfigBO bo = new EmailConfigBO();
		EmailConfig config = bo.loadConfiguration();
		
		if(writeMessage) {
			for(int idUser : users) {
				new MessageBO().sendMessage(idUser, subject, message, module);
			}
		}
		
		if(config == null){
			throw new Exception("É preciso configurar os dados para envio de e-mails.");
		}
		
		if((config.getSignature() != null) && !config.getSignature().trim().isEmpty()) {
			if(html) {
				message = message + "<br/><br/>--------------<br/>" + config.getSignature().replace("\n", "<br/>");
			} else {
				message = message + "\n\n--------------\n" + config.getSignature();	
			}
		}
		
		EmailUtils email = new EmailUtils(config.getHost(), config.getPort(), config.getUser(), config.getPassword(), config.isEnableSsl(), config.isAuthenticate());
		
		if(useThread)
			email.sendEmail(config.getUser(), to, null, null, subject, message, html);
		else
			email.sendEmailNoThread(config.getUser(), to, null, null, subject, message, html);
	}

}
