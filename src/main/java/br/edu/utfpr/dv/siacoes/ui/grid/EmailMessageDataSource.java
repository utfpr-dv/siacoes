package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.EmailMessage;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage;

public class EmailMessageDataSource extends BasicDataSource {

	private String type;
	private String title;
	private String message;
	
	public EmailMessageDataSource(EmailMessage message) {
		this.setId(message.getIdEmailMessage().getValue());
		this.setType(message.getIdEmailMessage().toString());
		this.setTitle(message.getSubject());
		this.setMessage(message.getMessage());
	}
	
	public EmailMessageDataSource(ReminderMessage message) {
		this.setId(message.getIdReminderMessage().getValue());
		this.setType(message.getIdReminderMessage().toString());
		this.setTitle(message.getSubject());
		this.setMessage(message.getMessage());
	}
	
	public static List<EmailMessageDataSource> loadMessages(List<EmailMessage> list) {
		List<EmailMessageDataSource> ret = new ArrayList<EmailMessageDataSource>();
		
		for(EmailMessage message : list) {
			ret.add(new EmailMessageDataSource(message));
		}
		
		return ret;
	}
	
	public static List<EmailMessageDataSource> loadReminders(List<ReminderMessage> list) {
		List<EmailMessageDataSource> ret = new ArrayList<EmailMessageDataSource>();
		
		for(ReminderMessage message : list) {
			ret.add(new EmailMessageDataSource(message));
		}
		
		return ret;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
