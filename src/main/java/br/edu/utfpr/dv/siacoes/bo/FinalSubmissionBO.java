package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.FinalSubmissionDAO;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionFooterReport;
import br.edu.utfpr.dv.siacoes.model.FinalSubmission;
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
			
			byte[] report = bo.getReport(idStudent, idDepartment).toByteArray();
			
			submission.getDepartment().setIdDepartment(idDepartment);
			submission.getStudent().setIdUser(idStudent);
			submission.getFeedbackUser().setIdUser(idFeedbackUser);
			submission.setFinalScore(finalScore);
			submission.setDate(DateUtils.getToday().getTime());
			submission.setReport(report);
			
			submission.setIdFinalSubmission(dao.save(idFeedbackUser, submission));
			
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

}
