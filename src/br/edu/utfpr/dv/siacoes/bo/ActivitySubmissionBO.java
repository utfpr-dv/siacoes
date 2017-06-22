package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import br.edu.utfpr.dv.siacoes.dao.ActivityGroupDAO;
import br.edu.utfpr.dv.siacoes.dao.ActivitySubmissionDAO;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionDetailReport;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionFooterReport;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionReport;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

public class ActivitySubmissionBO {

	public List<ActivitySubmission> listAll() throws Exception{
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<ActivitySubmission> listByStudent(int idStudent, int idDepartment) throws Exception{
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.listByStudent(idStudent, idDepartment);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<ActivitySubmission> listWithNoFeedback(int idDepartment) throws Exception{
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.listWithNoFeedback(idDepartment);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public ActivitySubmission findById(int id) throws Exception{
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(ActivitySubmission submission) throws Exception{
		if((submission.getStudent() == null) || (submission.getStudent().getIdUser() == 0)){
			throw new Exception("Informe o acadêmico.");
		}
		if((submission.getDepartment() == null) || (submission.getDepartment().getIdDepartment() == 0)){
			throw new Exception("Informe o departamento/coordenação.");
		}
		if((submission.getActivity() == null) || (submission.getActivity().getIdActivity() == 0)){
			throw new Exception("Informe a atividade.");
		}
		if(submission.getFile() == null){
			throw new Exception("É necessário enviar o comprovante.");
		}
		if((submission.getFeedback() == ActivityFeedback.APPROVED) && (submission.getValidatedAmount() <= 0)){
			submission.setValidatedAmount(submission.getAmount());
		}
		
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.save(submission);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public boolean delete(int id) throws Exception{
		ActivitySubmission submission = this.findById(id);
		
		return this.delete(submission);
	}
	
	public boolean delete(ActivitySubmission submission) throws Exception{
		if(submission.getFeedback() != ActivityFeedback.NONE){
			throw new Exception("Não é possível excluir a atividade pois o parecer para ela já foi emitido.");
		}
		
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.delete(submission.getIdActivitySubmission());
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public ByteArrayOutputStream getReport(int idStudent, int idDepartment) throws Exception{
		ActivitySubmissionReport report = new ActivitySubmissionReport();
		List<ActivitySubmission> list = this.listByStudent(idStudent, idDepartment);
		List<ReportActivity> activities = new ArrayList<ReportActivity>();
		List<byte[]> documents = new ArrayList<byte[]>();
		
		UserBO bo = new UserBO();
		User student = bo.findById(idStudent);
		
		report.setStudent(student.getName());
		report.setRegisterSemester(student.getRegisterSemester());
		report.setRegisterYear(student.getRegisterYear());
		report.setStudentCode(student.getStudentCode());
		
		for(ActivitySubmission submission : list){
			if(submission.getFeedback() == ActivityFeedback.APPROVED){
				ActivitySubmissionDetailReport detailReport = new ActivitySubmissionDetailReport();
				
				detailReport.setActivity(submission.getActivity().getDescription());
				detailReport.setGroup(submission.getActivity().getGroup().getSequence());
				detailReport.setScore(submission.getActivity().getScore());
				detailReport.setUnit(submission.getActivity().getUnit().getDescription());
				detailReport.setSemester(submission.getSemester());
				detailReport.setYear(submission.getYear());

				documents.add(submission.getFile());
				
				if(submission.getActivity().getUnit().isFillAmount()){
					detailReport.setAmount(submission.getValidatedAmount());
					detailReport.setTotal(submission.getValidatedAmount() * submission.getScore());
				}else{
					detailReport.setAmount(1);
					detailReport.setTotal(submission.getScore());
				}
				
				report.getDetails().add(detailReport);
				
				boolean find = false;
				for(ReportActivity ra : activities){
					if((ra.getIdActivity() == submission.getActivity().getIdActivity()) && (ra.getSemester() == submission.getSemester()) && (ra.getYear() == submission.getYear())){
						ra.setScore(ra.getScore() + detailReport.getTotal());
						find = true;
						break;
					}
				}
				if(!find){
					ReportActivity ra = new ReportActivity();
					ra.setIdActivity(submission.getActivity().getIdActivity());
					ra.setGroup(submission.getActivity().getGroup().getSequence());
					ra.setSemester(submission.getSemester());
					ra.setYear(submission.getYear());
					ra.setScore(detailReport.getTotal());
					ra.setMaximumScore(submission.getActivity().getMaximumInSemester());
					activities.add(ra);
				}
			}
		}
		
		ActivityGroupDAO groupDao = new ActivityGroupDAO();
		List<ActivityGroup> listGroup = groupDao.listAll();
		
		for(ActivityGroup group : listGroup){
			ActivitySubmissionFooterReport footer = new ActivitySubmissionFooterReport();
			
			footer.setGroup(group.getDescription());
			footer.setMinimum(group.getMinimumScore());
			footer.setMaximum(group.getMaximumScore());
			
			for(ReportActivity ra : activities){
				if(ra.getGroup() == group.getSequence()){
					if((ra.getMaximumScore() > 0) && (ra.getScore() > ra.getMaximumScore())){
						footer.setTotal(footer.getTotal() + ra.getMaximumScore());
					}else{
						footer.setTotal(footer.getTotal() + ra.getScore());
					}
				}
			}
			
			if(footer.getTotal() < footer.getMinimum()){
				footer.setSituation("Não atingiu a pontuação mínima");
			}else{
				footer.setSituation("Pontuação mínima atingida");
			}
			
			report.getFooter().add(footer);
		}
		
		for(ActivitySubmissionFooterReport footer : report.getFooter()){
			if(footer.getTotal() > footer.getMaximum()){
				footer.setTotal(footer.getMaximum());
				report.setTotalScore(report.getTotalScore() + footer.getMaximum());
			}else{
				report.setTotalScore(report.getTotalScore() + footer.getTotal());
			}
		}
		
		SigacConfigBO dbo = new SigacConfigBO();
		SigacConfig config = dbo.findByDepartment(idDepartment);
		
		if(report.getTotalScore() >= config.getMinimumScore()){
			report.setSituation("Pontuação atingida");	
		}else{
			report.setSituation("Pontuação insuficiente");
		}
		
		List<ActivitySubmissionReport> list2 = new ArrayList<ActivitySubmissionReport>();
		list2.add(report);
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list2, "ActivitySubmission");
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PDFMergerUtility pdfMerge = new PDFMergerUtility();
		
		pdfMerge.setDestinationStream(output);
		
		pdfMerge.addSource(new ByteArrayInputStream(rep.toByteArray()));
		for(byte[] d : documents){
			pdfMerge.addSource(new ByteArrayInputStream(d));
		}
		
		pdfMerge.mergeDocuments(null);
		
		return output;
	}
	
	private class ReportActivity{
		
		private int idActivity;
		private int group;
		private int year;
		private int semester;
		private double score;
		private double maximumScore;
		
		public ReportActivity(){
			this.setIdActivity(0);
			this.setGroup(0);
			this.setYear(0);
			this.setSemester(0);
			this.setScore(0);
			this.setMaximumScore(0);
		}
		
		public int getIdActivity() {
			return idActivity;
		}
		public void setIdActivity(int idActivity) {
			this.idActivity = idActivity;
		}
		public int getGroup() {
			return group;
		}
		public void setGroup(int group) {
			this.group = group;
		}
		public int getYear() {
			return year;
		}
		public void setYear(int year) {
			this.year = year;
		}
		public int getSemester() {
			return semester;
		}
		public void setSemester(int semester) {
			this.semester = semester;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}
		public double getMaximumScore() {
			return maximumScore;
		}
		public void setMaximumScore(double maximumScore) {
			this.maximumScore = maximumScore;
		}
		
	}

}
