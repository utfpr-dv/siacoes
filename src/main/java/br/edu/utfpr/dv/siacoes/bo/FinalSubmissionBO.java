package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.FinalSubmissionDAO;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionFooterReport;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionReport;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.FinalSubmission;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class FinalSubmissionBO {
	
	public FinalSubmission registerFinalSubmission(int idStudent, int idDepartment, int idFeedbackUser) throws Exception{
		try{
			if(this.studentHasSubmission(idStudent, idDepartment)) {
				throw new Exception("O acadêmico já concluiu as Atividades Complementares.");
			}
			
			FinalSubmissionDAO dao = new FinalSubmissionDAO();
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			FinalSubmission submission = new FinalSubmission();
			SigacConfig config = new SigacConfigBO().findByDepartment(idDepartment);
			double finalScore = 0;
			
			List<ActivitySubmissionFooterReport> footer = bo.getFooterReport(idStudent, idDepartment);
			
			for(ActivitySubmissionFooterReport item : footer) {
				if(item.getTotal() < item.getMinimum()) {
					throw new Exception("O acadêmico ainda não atintiu a pontuação mínima no grupo " + item.getSequence() + " (" + item.getGroup() + ")");
				}
				
				finalScore = finalScore + item.getTotal();
			}
			
			if(finalScore < new SigacConfigBO().findByDepartment(idDepartment).getMinimumScore()) {
				throw new Exception("O acadêmico ainda não atintiu a pontuação mínima exigida para aprovação.");
			}
			
			byte[] pdf = bo.getPdfReport(idStudent, idDepartment, true);
			
			submission.getDepartment().setIdDepartment(idDepartment);
			submission.getStudent().setIdUser(idStudent);
			submission.getFeedbackUser().setIdUser(idFeedbackUser);
			submission.setFinalScore(finalScore);
			submission.setDate(DateUtils.getToday().getTime());
			submission.setReport(pdf);
			
			submission.setIdFinalSubmission(dao.save(idFeedbackUser, submission));
			
			if(config.isUseDigitalSignature()) {
				ActivitySubmissionReport report = bo.getReportData(idStudent, idDepartment);
				
				int idDocument = Document.insert(idDepartment, DocumentType.ACTIVITYFINALSUBMISSION, submission.getIdFinalSubmission(), SignDatasetBuilder.build(report), SignDatasetBuilder.getSignaturesList(report));
			}
			
			return submission;
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<FinalSubmission> listAll() throws Exception{
		try{
			FinalSubmissionDAO dao = new FinalSubmissionDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<FinalSubmission> listByDepartment(int idDepartment) throws Exception{
		try{
			FinalSubmissionDAO dao = new FinalSubmissionDAO();
			
			return dao.listByDepartment(idDepartment);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<FinalSubmission> listByFeedbackUser(int idDepartment, int idUser) throws Exception{
		try{
			FinalSubmissionDAO dao = new FinalSubmissionDAO();
			
			return dao.listByFeedbackUser(idDepartment, idUser);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public FinalSubmission findById(int id) throws Exception{
		try{
			FinalSubmissionDAO dao = new FinalSubmissionDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public FinalSubmission findByStudent(int idStudent, int idDepartment) throws Exception{
		try{
			FinalSubmissionDAO dao = new FinalSubmissionDAO();
			
			return dao.findByStudent(idStudent, idDepartment);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public boolean studentHasSubmission(int idStudent, int idDepartment) throws Exception{
		try{
			FinalSubmissionDAO dao = new FinalSubmissionDAO();
			
			return dao.studentHasSubmission(idStudent, idDepartment);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public void sendRequestSignFinalSubmissionMessage(int idFinalSubmission, List<User> users) throws Exception {
		for(User user : users) {
			List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
			User u = new UserBO().findById(user.getIdUser());

			keys.add(new EmailMessageEntry<String, String>("name", u.getName()));
			
			new EmailMessageBO().sendEmail(user.getIdUser(), MessageType.REQUESTSIGNACTIVITYFINALSUBMISSION, keys, false);
		}
	}
	
	public void sendFinalSubmissionSignedMessage(int idFinalSubmission) throws Exception {
		FinalSubmission submission = this.findById(idFinalSubmission);
		User manager = new UserBO().findManager(submission.getDepartment().getIdDepartment(), SystemModule.SIGAC);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
		keys.add(new EmailMessageEntry<String, String>("student", submission.getStudent().getName()));
		
		new EmailMessageBO().sendEmail(manager.getIdUser(), MessageType.SIGNEDACTIVITYFINALSUBMISSION, keys);
	}

}
