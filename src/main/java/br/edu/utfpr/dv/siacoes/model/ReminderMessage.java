package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class ReminderMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum ReminderType {
		NONE(0), INTERNSHIPSTUDENTREPORT(1), INTERNSHIPSUPERVISORREPORT(2);
		
		private final int value; 
		ReminderType(int value) { 
			this.value = value; 
		}
		
		public int getValue() { 
			return value;
		}
		
		public static ReminderType valueOf(int value) {
			for(ReminderType p : ReminderType.values()) {
				if(p.getValue() == value) {
					return p;
				}
			}
			
			return null;
		}
		
		public String toString() {
			switch(this) {
				case INTERNSHIPSTUDENTREPORT:
					return "Relatório parcial do acadêmico";
				case INTERNSHIPSUPERVISORREPORT:
					return "Relatório parcial do orientador";
				default:
					return "";
			}
		}
	}
	
	private ReminderType idReminderMessage;
	private String subject;
	private String message;
	private String dataFields;
	private SystemModule module;
	
	public ReminderMessage() {
		this.setIdReminderMessage(ReminderType.NONE);
		this.setSubject("");
		this.setMessage("");
		this.setDataFields("");
		this.setModule(SystemModule.GENERAL);
	}
	
	public ReminderType getIdReminderMessage() {
		return idReminderMessage;
	}
	public void setIdReminderMessage(ReminderType idReminderMessage) {
		this.idReminderMessage = idReminderMessage;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDataFields() {
		return dataFields;
	}
	public void setDataFields(String dataFields) {
		this.dataFields = dataFields;
	}
	public SystemModule getModule() {
		return module;
	}
	public void setModule(SystemModule module) {
		this.module = module;
	}

}
