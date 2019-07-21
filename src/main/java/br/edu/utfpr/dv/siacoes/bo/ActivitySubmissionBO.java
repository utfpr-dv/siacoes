package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.catalina.tribes.util.Arrays;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import br.edu.utfpr.dv.siacoes.dao.ActivityGroupDAO;
import br.edu.utfpr.dv.siacoes.dao.ActivitySubmissionDAO;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.model.ActivityGroupStatus;
import br.edu.utfpr.dv.siacoes.model.ActivityScore;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionDetailReport;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionFooterReport;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionReport;
import br.edu.utfpr.dv.siacoes.model.ActivityValidationReport;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
import br.edu.utfpr.dv.siacoes.model.StudentActivityStatusReport;
import br.edu.utfpr.dv.siacoes.model.StudentActivityStatusReport.StudentStage;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

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
	
	public List<ActivitySubmission> listByStudent(int idStudent, int idDepartment, int feedback, boolean loadFiles) throws Exception{
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.listByStudent(idStudent, idDepartment, feedback, loadFiles);
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
	
	public List<ActivitySubmission> listWithNoFeedback2(int idDepartment) throws Exception{
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.listWithNoFeedback2(idDepartment);
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
	
	public int save(int idUser, ActivitySubmission submission) throws Exception{
		int ret = 0;
		boolean isInsert = (submission.getIdActivitySubmission() == 0);
		ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
		
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
		if(submission.getDescription().trim().isEmpty()){
			throw new Exception("Informe a descrição da atividade.");
		}
		if((submission.getFeedback() == ActivityFeedback.DISAPPROVED) && ((submission.getFeedbackReason() == null) || submission.getFeedbackReason().trim().isEmpty())) {
			throw new Exception("Informe o motivo para a recusa da atividade (observações do parecerista).");
		}
		boolean fillAmount = new ActivityBO().needsFillAmount(submission.getActivity().getIdActivity());
		if((submission.getAmount() <= 0) && fillAmount) {
			throw new Exception("Informe a quantidade a ser validada para a atividade (horas, etc.).");
		}
		if(!fillAmount) {
			submission.setAmount(1);
		}
		if((submission.getFeedback() == ActivityFeedback.APPROVED) && (submission.getValidatedAmount() <= 0)) {
			if(!fillAmount) {
				submission.setValidatedAmount(1);
			} else {
				submission.setValidatedAmount(submission.getAmount());
			}
		} else if(submission.getFeedback() != ActivityFeedback.APPROVED) {
			submission.setValidatedAmount(0);
		}
		SigacConfig config = new SigacConfigBO().findByDepartment(submission.getDepartment().getIdDepartment());
		if((config.getMaxFileSize() > 0) && ((submission.getIdActivitySubmission() == 0) || !Arrays.equals(submission.getFile(), dao.getFile(submission.getIdActivitySubmission()))) && (submission.getFile().length > config.getMaxFileSize())) {
			throw new Exception("O comprovante deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
		}
		if(new FinalSubmissionBO().studentHasSubmission(submission.getStudent().getIdUser(), submission.getDepartment().getIdDepartment())) {
			throw new Exception("Não é possível incluir ou editar a atividade pois o acadêmico já foi sinalizado como Aprovado.");
		}
		
		ActivityFeedback feedback = dao.getFeedback(submission.getIdActivitySubmission());
		
		try{
			ret = dao.save(idUser, submission);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
		
		try{
			EmailMessageBO bo = new EmailMessageBO();
			List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
			
			keys.add(new EmailMessageEntry<String, String>("student", submission.getStudent().getName()));
			keys.add(new EmailMessageEntry<String, String>("group", submission.getActivity().getGroup().getDescription()));
			keys.add(new EmailMessageEntry<String, String>("activity", submission.getActivity().getDescription()));
			keys.add(new EmailMessageEntry<String, String>("semester", String.valueOf(submission.getSemester())));
			keys.add(new EmailMessageEntry<String, String>("year", String.valueOf(submission.getYear())));
			keys.add(new EmailMessageEntry<String, String>("comments", submission.getComments()));
			keys.add(new EmailMessageEntry<String, String>("description", submission.getDescription()));
			keys.add(new EmailMessageEntry<String, String>("feedbackUser", submission.getFeedbackUser().getName()));
			
			if(submission.getFeedback() == ActivityFeedback.APPROVED){
				keys.add(new EmailMessageEntry<String, String>("feedback", "APROVADA"));
			}else if(submission.getFeedback() == ActivityFeedback.DISAPPROVED){
				keys.add(new EmailMessageEntry<String, String>("feedback", "REPROVADA"));
			}
			
			if(isInsert) {
				bo.sendEmail(submission.getStudent().getIdUser(), MessageType.ACTIVITYSUBMITTED, keys);
			} else if(feedback != submission.getFeedback()){
				bo.sendEmail(submission.getStudent().getIdUser(), MessageType.ACTIVITYFEEDBACK, keys);
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		return ret;
	}
	
	public boolean delete(int idUser, int id) throws Exception{
		ActivitySubmission submission = this.findById(id);
		
		return this.delete(idUser, submission);
	}
	
	public boolean delete(int idUser, ActivitySubmission submission) throws Exception{
		if(submission.getFeedback() != ActivityFeedback.NONE){
			throw new Exception("Não é possível excluir a atividade pois o parecer para ela já foi emitido.");
		}
		if(new FinalSubmissionBO().studentHasSubmission(submission.getStudent().getIdUser(), submission.getDepartment().getIdDepartment())) {
			throw new Exception("Não é possível excluir a atividade pois o acadêmico já foi sinalizado como Aprovado.");
		}
		
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.delete(idUser, submission.getIdActivitySubmission());
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<ActivitySubmissionFooterReport> getFooterReport(int idStudent, int idDepartment) throws Exception{
		List<ActivitySubmission> list = this.listByStudent(idStudent, idDepartment, ActivityFeedback.APPROVED.getValue(), false);
		
		return this.getFooterReport(list);
	}
	
	public List<ActivitySubmissionFooterReport> getFooterReport(List<ActivitySubmission> list) throws Exception{
		List<ReportActivity> activities = new ArrayList<ReportActivity>();
		
		activities = this.loadActivities(list);
		
		return this.buildFooter(activities);
	}
	
	public ByteArrayOutputStream getReport(int idStudent, int idDepartment) throws Exception{
		List<ActivitySubmission> list = this.listByStudent(idStudent, idDepartment, ActivityFeedback.APPROVED.getValue(), true);
		UserBO bo = new UserBO();
		User student = bo.findById(idStudent);
		
		return this.getReport(list, student, idDepartment);
	}
	
	public ByteArrayOutputStream getReport(List<ActivitySubmission> list, User student, int idDepartment) throws Exception{
		ActivitySubmissionReport report = new ActivitySubmissionReport();
		List<ReportActivity> activities = new ArrayList<ReportActivity>();
		List<byte[]> documents = new ArrayList<byte[]>();
		UserDepartment department = new UserDepartmentBO().find(student.getIdUser(), UserProfile.STUDENT, idDepartment);
		
		if(department == null) {
			department = new UserDepartment();
		}
		
		report.setStudent(student.getName());
		report.setRegisterSemester(department.getRegisterSemester());
		report.setRegisterYear(department.getRegisterYear());
		report.setStudentCode(student.getStudentCode());
		
		report.setDetails(this.loadSubmissionDetailReport(list));
		
		documents = this.loadDocuments(list);
		
		activities = this.loadActivities(list);
		
		report.setFooter(this.buildFooter(activities));
		
		for(ActivitySubmissionFooterReport footer : report.getFooter()){
			report.setTotalScore(report.getTotalScore() + footer.getTotal());
		}
		
		SigacConfigBO dbo = new SigacConfigBO();
		SigacConfig config = dbo.findByDepartment(idDepartment);
		
		if((report.getTotalScore() >= config.getMinimumScore()) && this.hasMinimalScores(report.getFooter())){
			report.setSituation("Pontuação atingida");	
		}else{
			report.setSituation("Pontuação insuficiente");
		}
		
		List<ActivitySubmissionReport> list2 = new ArrayList<ActivitySubmissionReport>();
		list2.add(report);
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list2, "ActivitySubmission", idDepartment);
		
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
	
	private boolean hasMinimalScores(List<ActivitySubmissionFooterReport> list){
		for(ActivitySubmissionFooterReport footer : list){
			if(footer.getTotal() < footer.getMinimum()){
				return false;
			}
		}
		
		return true;
	}
	
	private List<byte[]> loadDocuments(List<ActivitySubmission> submissions){
		List<byte[]> documents = new ArrayList<byte[]>();
		
		for(ActivitySubmission submission : submissions){
			if(submission.getFeedback() == ActivityFeedback.APPROVED){
				documents.add(submission.getFile());
			}
		}
		
		return documents;
	}
	
	private List<ActivitySubmissionDetailReport> loadSubmissionDetailReport(List<ActivitySubmission> submissions){
		List<ActivitySubmissionDetailReport> detail = new ArrayList<ActivitySubmissionDetailReport>();
		
		for(ActivitySubmission submission : submissions){
			if(submission.getFeedback() == ActivityFeedback.APPROVED){
				ActivitySubmissionDetailReport detailReport = new ActivitySubmissionDetailReport();
				
				detailReport.setActivity(submission.getActivity().getDescription());
				detailReport.setGroup(submission.getActivity().getGroup().getSequence());
				detailReport.setScore(this.round(submission.getActivity().getScore()));
				detailReport.setUnit(submission.getActivity().getUnit().getDescription());
				detailReport.setSemester(submission.getSemester());
				detailReport.setYear(submission.getYear());
				
				if(submission.getActivity().getUnit().isFillAmount()){
					detailReport.setAmount(this.round(submission.getValidatedAmount()));
				}else{
					detailReport.setAmount(1);
				}
				detailReport.setTotal(this.round(submission.getScore()));
				
				detail.add(detailReport);
			}
		}
		
		return detail;
	}
	
	private List<ReportActivity> loadActivities(List<ActivitySubmission> submissions){
		List<ReportActivity> activities = new ArrayList<ReportActivity>();
		
		for(ActivitySubmission submission : submissions){
			if(submission.getFeedback() == ActivityFeedback.APPROVED){
				boolean find = false;
				for(ReportActivity ra : activities){
					if((ra.getIdActivity() == submission.getActivity().getIdActivity()) && (ra.getSemester() == submission.getSemester()) && (ra.getYear() == submission.getYear())){
						ra.setScore(ra.getScore() + this.round(submission.getScore()));
						find = true;
						break;
					}
				}
				if(!find){
					ReportActivity ra = new ReportActivity();
					ra.setIdActivity(submission.getActivity().getIdActivity());
					ra.setIdActivityGroup(submission.getActivity().getGroup().getIdActivityGroup());
					ra.setGroup(submission.getActivity().getGroup().getSequence());
					ra.setSemester(submission.getSemester());
					ra.setYear(submission.getYear());
					ra.setScore(this.round(submission.getScore()));
					ra.setMaximumScore(submission.getActivity().getMaximumInSemester());
					activities.add(ra);
				}
			}
		}
		
		return activities;
	}
	
	private List<ActivitySubmissionFooterReport> buildFooter(List<ReportActivity> activities) throws SQLException{
		List<ActivitySubmissionFooterReport> ret = new ArrayList<ActivitySubmissionFooterReport>();
		ActivityGroupDAO groupDao = new ActivityGroupDAO();
		List<ActivityGroup> listGroup = groupDao.listAll();
		
		for(ActivityGroup group : listGroup){
			ActivitySubmissionFooterReport footer = new ActivitySubmissionFooterReport();
			
			footer.setIdActivityGroup(group.getIdActivityGroup());
			footer.setSequence(group.getSequence());
			footer.setGroup(group.getDescription());
			footer.setMinimum(group.getMinimumScore());
			footer.setMaximum(group.getMaximumScore());
			
			for(ReportActivity ra : activities){
				if(ra.getIdActivityGroup() == group.getIdActivityGroup()){
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
			
			if(footer.getTotal() > footer.getMaximum()){
				footer.setTotal(footer.getMaximum());
			}
			
			ret.add(footer);
		}
		
		return ret;
	}
	
	private double round(double value){
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(1, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public List<User> listFeedbackUsers(int idDepartment) throws Exception{
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.listFeedbackUsers(idDepartment);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public byte[] getActivityValidationReport(int idDepartment, int idFeedbackUser) throws Exception {
		try{
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			List<ActivityValidationReport> list = dao.getActivityValidationReport(idDepartment, idFeedbackUser);
			
			ByteArrayOutputStream report = new ReportUtils().createPdfStream(list, "ActivityValidation", idDepartment);
			
			return report.toByteArray();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<StudentActivityStatusReport> getStudentActivityStatus(int idDepartment, StudentStage stage, boolean withoutPoints) throws Exception {
		try {
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			List<ActivitySubmissionFooterReport> scores = new ArrayList<ActivitySubmissionFooterReport>();
			List<StudentActivityStatusReport> students = dao.getStudentActivityStatusReport(idDepartment);
			List<StudentActivityStatusReport> report = new ArrayList<StudentActivityStatusReport>();
			SigacConfig config = new SigacConfigBO().findByDepartment(idDepartment);
			
			for(StudentActivityStatusReport user : students) {
				if((user.getStage() >= stage.getValue()) && (user.getStage() != StudentStage.GRADUATED.getValue())) {
					list = this.listByStudent(user.getIdUser(), idDepartment, ActivityFeedback.APPROVED.getValue(), false);
					scores = this.getFooterReport(list);
					
					user.setScores(scores);
					
					for(ActivitySubmissionFooterReport s : scores) {
						user.setTotalScore(user.getTotalScore() + s.getTotal());
					}
					
					if((user.getTotalScore() >= config.getMinimumScore()) && this.hasMinimalScores(scores)) {
						user.setSituation("Pontuação atingida");
						
						if(!withoutPoints) {
							report.add(user);
						}
					} else {
						user.setSituation("Pontuação insuficiente");
						
						report.add(user);
					}
				}
			}
			
			return report;
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public byte[] getStudentActivityStatusReport(int idDepartment, StudentStage stage, boolean withoutPoints) throws Exception {
		try{
			List<StudentActivityStatusReport> report = this.getStudentActivityStatus(idDepartment, stage, withoutPoints);
			
			ByteArrayOutputStream pdfReport = new ReportUtils().createPdfStream(report, "StudentActivityStatus", idDepartment);
			
			return pdfReport.toByteArray();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<ActivityGroupStatus> getStudentActivityGroupStatus(int idDepartment, StudentStage stage, boolean withoutPoints) throws Exception {
		try{
			List<ActivityGroup> groups = new ActivityGroupBO().listAll();
			List<StudentActivityStatusReport> report = this.getStudentActivityStatus(idDepartment, stage, withoutPoints);
			List<ActivityGroupStatus> list = new ArrayList<ActivityGroupStatus>();
			
			for(ActivityGroup group : groups) {
				ActivityGroupStatus status = new ActivityGroupStatus();
				double sum = 0;
				int count = 0;
				
				status.setGroup(group);
				for(StudentActivityStatusReport student : report) {
					for(ActivitySubmissionFooterReport score : student.getScores()) {
						if(score.getIdActivityGroup() == group.getIdActivityGroup()) {
							sum = sum + score.getTotal();
							count++;
						}
					}
				}
				
				if(count > 0) {
					sum = sum / count;
				}
				
				status.setAverageScore(sum);
				
				list.add(status);
			}
			
			return list;
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<ActivityScore> getActivityScore(int idDepartment, int initialYear, int finalYear, int maxActivities) throws Exception {
		try{
			List<ActivityScore> list = new ActivitySubmissionDAO().getActivityScore(idDepartment, initialYear, finalYear);
			
			if(list.size() <= maxActivities) {
				return list;
			} else {
				List<ActivityScore> ret = new ArrayList<ActivityScore>();
				
				for(int i = 0; i < maxActivities; i++) {
					ret.add(list.get(i));
				}
				
				double score = 0;
				for(int i = maxActivities; i < list.size(); i++) {
					score = score + list.get(i).getScore();
				}
				
				ActivityScore a = new ActivityScore();
				a.setIdActivity(0);
				a.setActivity("Outras Atividades");
				a.setScore(score);
				ret.add(a);
				
				return ret;
			}
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public long getTotalSubmissions() throws Exception{
		try {
			ActivitySubmissionDAO dao = new ActivitySubmissionDAO();
			
			return dao.getTotalSubmissions();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	private class ReportActivity{
		
		private int idActivity;
		private int idActivityGroup;
		private int group;
		private int year;
		private int semester;
		private double score;
		private double maximumScore;
		
		public ReportActivity(){
			this.setIdActivity(0);
			this.setIdActivityGroup(0);
			this.setGroup(0);
			this.setYear(0);
			this.setSemester(0);
			this.setScore(0);
			this.setMaximumScore(0);
		}
		
		public int getIdActivityGroup() {
			return idActivityGroup;
		}
		public void setIdActivityGroup(int idActivityGroup) {
			this.idActivityGroup = idActivityGroup;
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
