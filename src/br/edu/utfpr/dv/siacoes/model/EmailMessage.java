package br.edu.utfpr.dv.siacoes.model;

public class EmailMessage {
	
	public enum MessageType{
		NONE(0), ACTIVITYSUBMITED(1), 
		ACTIVITYAPPROVED(2),
		ACTIVITYREPPROVED(3);
		
		private final int value; 
		MessageType(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static MessageType valueOf(int value){
			for(MessageType p : MessageType.values()){
				if(p.getValue() == value){
					return p;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case ACTIVITYSUBMITED:
					return "Comprovante de Atividade Complementar Enviado";
				case ACTIVITYAPPROVED:
					return "Comprovante de Atividade Complementar Aprovado";
				case ACTIVITYREPPROVED:
					return "Comprovante de Atividade Complementar Reprovado";
				default:
					return "";
			}
		}
	}
	
	private MessageType idEmailMessage;
	private String subject;
	private String message;
	private String dataFields;
	
	public EmailMessage(){
		this.setIdEmailMessage(MessageType.NONE);
		this.setSubject("");
		this.setMessage("");
		this.setDataFields("");
	}
	
	public MessageType getIdEmailMessage() {
		return idEmailMessage;
	}
	public void setIdEmailMessage(MessageType idEmailMessage) {
		this.idEmailMessage = idEmailMessage;
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

}
