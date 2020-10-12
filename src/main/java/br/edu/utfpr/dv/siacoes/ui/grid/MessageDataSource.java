package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Message;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class MessageDataSource extends BasicDataSource {

	private String sender;
	private String subject;
	private boolean read;
	private LocalDateTime date;
	
	public MessageDataSource(Message message) {
		this.setId(message.getIdMessage());
		this.setSender(message.getModule().getShortDescription());
		this.setSubject(message.getTitle());
		this.setRead(message.isRead());
		this.setDate(DateUtils.convertToLocalDateTime(message.getDate()));
	}
	
	public static List<MessageDataSource> load(List<Message> list) {
		List<MessageDataSource> ret = new ArrayList<MessageDataSource>();
		
		for(Message message : list) {
			ret.add(new MessageDataSource(message));
		}
		
		return ret;
	}
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
}
