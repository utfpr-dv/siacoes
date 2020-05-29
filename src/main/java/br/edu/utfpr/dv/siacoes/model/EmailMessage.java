package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class EmailMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum MessageType{
		NONE(0), ACTIVITYSUBMITTED(1), 
		ACTIVITYFEEDBACK(2),
		INTERNSHIPINCLUDEDSTUDENT(3),
		INTERNSHIPINCLUDEDSUPERVISOR(4),
		PROPOSALREGISTERSTUDENT(5),
		PROPOSALREGISTERSUPERVISOR(6),
		PROPOSALSUBMITEDSTUDENT(7),
		PROPOSALSUBMITEDSUPERVISOR(8),
		PROPOSALCHANGEDSTUDENT(9),
		PROPOSALCHANGEDSUPERVISOR(10),
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
		INTERNSHIPJURYREMOVEDAPPRAISER(22),
		FINALDOCUMENTSUBMITTED(23),
		FINALDOCUMENTVALIDATED(24),
		INTERNSHIPFINALDOCUMENTSUBMITTED(25),
		INTERNSHIPFINALDOCUMENTVALIDATED(26),
		PROPOSALAPPRAISERSUPERVISORINDICATION(27),
		PROPOSALAPPRAISERFEEDBACKSUPERVISOR(28),
		PROPOSALAPPRAISERFEEDBACKSTUDENT(29),
		USERREGISTRED(30),
		PROJECTORTHESISSUBMITEDSTUDENT(31),
		PROJECTORTHESISSUBMITEDSUPERVISOR(32),
		PROJECTORTHESISCHANGEDSTUDENT(33),
		PROJECTORTHESISCHANGEDSUPERVISOR(34),
		FINALDOCUMENTSUBMITTEDSTUDENT(35),
		FINALDOCUMENTVALIDATEDSTUDENT(36),
		INTERNSHIPFINALDOCUMENTSUBMITTEDSTUDENT(37),
		INTERNSHIPFINALDOCUMENTVALIDATEDSTUDENT(38),
		BUGREPORTED(39),
		BUGUPDATED(40),
		INTERNSHIPSTUDENTREPORTSUBMITTED(41),
		INTERNSHIPSUPERVISORREPORTSUBMITTED(42),
		SIGNEDSUPERVISORAGREEMENT(43),
		SIGNEDAPPRAISERFEEDBACK(44),
		SIGNEDJURYREQUEST(45),
		SIGNEDSUPERVISORCHANGE(46),
		SIGNEDATTENDANCE(47),
		SIGNEDJURYFORM(48),
		SIGNEDINTERNSHIPPOSTERREQUEST(49),
		SIGNEDINTERNSHIPJURYFORM(50),
		REQUESTSIGNJURYREQUEST(51),
		REQUESTSIGNATTENDANCE(52),
		REQUESTSIGNJURYFORM(53),
		REQUESTSIGNINTERNSHIPPOSTERREQUEST(54),
		REQUESTSIGNINTERNSHIPJURYFORM(55),
		REQUESTSUPERVISORSIGNJURYFORM(56),
		REQUESTSUPERVISORSIGNINTERNSHIPJURYFORM(57),
		INTERNSHIPCOMPANYSUPERVISORREPORTSUBMITTED(58);
		
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
				case ACTIVITYSUBMITTED:
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
				case PROPOSALCHANGEDSTUDENT:
					return "Ressubmissão de Proposta de TCC 1 (Estudante)";
				case PROPOSALCHANGEDSUPERVISOR:
					return "Ressubmissão de Proposta de TCC 1 (Orientador)";
				case PROPOSALAPPRAISERREGISTER:
					return "Indicação de Avaliador da Proposta de TCC 1 (Responsável TCC)";
				case PROPOSALAPPRAISERFEEDBACK:
					return "Parecer da Proposta de TCC 1";
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
				case FINALDOCUMENTSUBMITTED:
					return "Versão Final de TCC Submetida";
				case FINALDOCUMENTVALIDATED:
					return "Versão Final de TCC Validada";
				case INTERNSHIPFINALDOCUMENTSUBMITTED:
					return "Versão Final de Estágio Submetida";
				case INTERNSHIPFINALDOCUMENTVALIDATED:
					return "Versão Final de Estágio Validada";
				case PROPOSALAPPRAISERSUPERVISORINDICATION:
					return "Indicação de Avaliador da Proposta de TCC 1 (Orientador)";
				case PROPOSALAPPRAISERFEEDBACKSUPERVISOR:
					return "Parecer da Proposta de TCC 1 (Orientador)";
				case PROPOSALAPPRAISERFEEDBACKSTUDENT:
					return "Parecer da Proposta de TCC 1 (Estudante)";
				case USERREGISTRED:
					return "Usuário Cadastrado";
				case PROJECTORTHESISSUBMITEDSTUDENT:
					return "Submissão de Projeto ou Monografia (Estudante)";
				case PROJECTORTHESISSUBMITEDSUPERVISOR:
					return "Submissão de Projeto ou Monografia (Orientador)";
				case PROJECTORTHESISCHANGEDSTUDENT:
					return "Ressubmissão de Projeto ou Monografia (Estudante)";
				case PROJECTORTHESISCHANGEDSUPERVISOR:
					return "Ressubmissão de Projeto ou Monografia (Orientador)";
				case FINALDOCUMENTSUBMITTEDSTUDENT:
					return "Versão Final de TCC Submetida (Estudante)";
				case FINALDOCUMENTVALIDATEDSTUDENT:
					return "Versão Final de TCC Validada (Estudante)";
				case INTERNSHIPFINALDOCUMENTSUBMITTEDSTUDENT:
					return "Versão Final de Estágio Submetida (Estudante)";
				case INTERNSHIPFINALDOCUMENTVALIDATEDSTUDENT:
					return "Versão Final de Estágio Validada (Estudante)";
				case BUGREPORTED:
					return "Nova Sugestão/Problema Cadastrado";
				case BUGUPDATED:
					return "Sugestão/Problema Atualizado";
				case INTERNSHIPSTUDENTREPORTSUBMITTED:
					return "Relatório de Estágio Enviado (Estudante)";
				case INTERNSHIPSUPERVISORREPORTSUBMITTED:
					return "Relatório de Estágio Enviado (Orientador)";
				case SIGNEDSUPERVISORAGREEMENT:
					return "Termo de Concordância de Orientação de TCC Assinado";
				case SIGNEDAPPRAISERFEEDBACK:
					return "Parecer da Proposta de TCC 1 Assinado";
				case SIGNEDJURYREQUEST:
					return "Formulário de Solicitação de Banca de TCC Assinado";
				case SIGNEDSUPERVISORCHANGE:
					return "Solicitação de Alteração de Orientador de TCC Assinada";
				case SIGNEDATTENDANCE:
					return "Formulário de Reuniões de TCC Assinado";
				case SIGNEDJURYFORM:
					return "Formulário de Avaliação de Banca de TCC Assinado";
				case SIGNEDINTERNSHIPPOSTERREQUEST:
					return "Formulário de Solicitação de Banca de Estágio Assinado";
				case SIGNEDINTERNSHIPJURYFORM:
					return "Formulário de Avaliação de Banca de Estágio Assinado";
				case REQUESTSIGNJURYREQUEST:
					return "Assinatura Requisitada no Formulário de Solicitação de Banca de TCC";
				case REQUESTSIGNATTENDANCE:
					return "Assinatura Requisitada no Formulário de Reuniões de TCC";
				case REQUESTSIGNJURYFORM:
					return "Assinatura Requisitada no Formulário de Avaliação de Banca de TCC";
				case REQUESTSIGNINTERNSHIPPOSTERREQUEST:
					return "Assinatura Requisitada no Formulário de Solicitação de Banca de Estágio";
				case REQUESTSIGNINTERNSHIPJURYFORM:
					return "Assinatura Requisitada no Formulário de Avaliação de Banca de Estágio";
				case REQUESTSUPERVISORSIGNJURYFORM:
					return "Formulário de Avaliação de Banca de TCC Pronto para Assinatura";
				case REQUESTSUPERVISORSIGNINTERNSHIPJURYFORM:
					return "Formulário de Avaliação de Banca de Estágio Pronto para Assinatura";
				case INTERNSHIPCOMPANYSUPERVISORREPORTSUBMITTED:
					return "Relatório de Estágio Enviado (Supervisor)";
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
