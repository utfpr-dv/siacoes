package br.edu.utfpr.dv.siacoes.model;

import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class EmailMessage {
	
	public enum MessageType{
		NONE(0), ACTIVITYSUBMITED(1), 
		ACTIVITYFEEDBACK(2),
		INTERNSHIPINCLUDEDSTUDENT(3),
		INTERNSHIPINCLUDEDSUPERVISOR(4),
		PROPOSALREGISTERSTUDENT(5),
		PROPOSALREGISTERSUPERVISOR(6),
		PROPOSALSUBMITEDSTUDENT(7),
		PROPOSALSUBMITEDSUPERVISOR(8),
		PROPOSALCHANGESTUDENT(9),
		PROPOSALCHANGESUPERVISOR(10),
		PROPOSALAPPRAISERREGISTER(11),
		PROPOSALAPPRAISERFEEDBACK(12),
		JURYINCLUDEDSTUDENT(13),
		JURYINCLUDEDAPPRAISER(14),
		JURYCHANGEDSTUDENT(15),
		JURYCHANGEDAPPRAISER(16),
		JURYREMOVEDAPPRAISER(17),
		INTERNSHIPJURYINCLUDEDSTUDENT(18),
		INTERNSHIPJURYINCLUDEDAPPRAISER(19),
		INTERNSHIPJURYCHANGEDSTUDENT(20),
		INTERNSHIPJURYCHANGEDAPPRAISER(21),
		INTERNSHIPJURYREMOVEDAPPRAISER(22);
		
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
				case ACTIVITYFEEDBACK:
					return "Comprovante de Atividade Complementar Apreciado";
				case INTERNSHIPINCLUDEDSTUDENT:
					return "Estágio Cadastrado (Estudante)";
				case INTERNSHIPINCLUDEDSUPERVISOR:
					return "Estágio Cadastrado (Orientador)";
				case PROPOSALREGISTERSTUDENT:
					return "Registro de Orientação (Estudante)";
				case PROPOSALREGISTERSUPERVISOR:
					return "Registro de Orientação (Orientador)";
				case PROPOSALSUBMITEDSTUDENT:
					return "Submissão de Proposta de TCC 1 (Estudante)";
				case PROPOSALSUBMITEDSUPERVISOR:
					return "Submissão de Proposta de TCC 1 (Orientador)";
				case PROPOSALCHANGESTUDENT:
					return "Ressubmissão de Proposta de TCC 1 (Estudante)";
				case PROPOSALCHANGESUPERVISOR:
					return "Ressubmissão de Proposta de TCC 1 (Orientador)";
				case PROPOSALAPPRAISERREGISTER:
					return "Cadastro de Avaliador da Proposta de TCC 1";
				case PROPOSALAPPRAISERFEEDBACK:
					return "Feedback da Proposta de TCC 1";
				case JURYINCLUDEDSTUDENT:
					return "Banca de TCC Marcada (Estudante)";
				case JURYINCLUDEDAPPRAISER:
					return "Banca de TCC Marcada (Avaliador)";
				case JURYCHANGEDSTUDENT:
					return "Banca de TCC Alterada (Estudante)";
				case JURYCHANGEDAPPRAISER:
					return "Banca de TCC Alterada (Avaliador)";
				case JURYREMOVEDAPPRAISER:
					return "Banca de TCC (Avaliador Removido)";
				case INTERNSHIPJURYINCLUDEDSTUDENT:
					return "Banca de Estágio Marcada (Estudante)";
				case INTERNSHIPJURYINCLUDEDAPPRAISER:
					return "Banca de Estágio Marcada (Avaliador)";
				case INTERNSHIPJURYCHANGEDSTUDENT:
					return "Banca de Estágio Alterada (Estudante)";
				case INTERNSHIPJURYCHANGEDAPPRAISER:
					return "Banca de Estágio Alterada (Avaliador)";
				case INTERNSHIPJURYREMOVEDAPPRAISER:
					return "Banca de Estágio (Avaliador Removido)";
				default:
					return "";
			}
		}
	}
	
	private MessageType idEmailMessage;
	private String subject;
	private String message;
	private String dataFields;
	private SystemModule module;
	
	public EmailMessage(){
		this.setIdEmailMessage(MessageType.NONE);
		this.setSubject("");
		this.setMessage("");
		this.setDataFields("");
		this.setModule(SystemModule.GENERAL);
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
	public SystemModule getModule() {
		return module;
	}
	public void setModule(SystemModule module) {
		this.module = module;
	}

}
