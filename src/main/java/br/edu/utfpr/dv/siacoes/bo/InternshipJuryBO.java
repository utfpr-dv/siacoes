package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanComparator;

import br.edu.utfpr.dv.siacoes.dao.InternshipJuryDAO;
import br.edu.utfpr.dv.siacoes.model.CalendarReport;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.model.Jury.JuryResult;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserScore;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryStudent;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserScoreReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
import br.edu.utfpr.dv.siacoes.model.JuryGrade;
import br.edu.utfpr.dv.siacoes.model.JuryStudentReport;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigesConfig.JuryFormat;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

public class InternshipJuryBO {
	
	public List<InternshipJury> listBySemester(int idDepartment, int semester, int year) throws Exception{
		try {
			InternshipJuryDAO dao = new InternshipJuryDAO();
			
			return dao.listBySemester(idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipJury> listByAppraiser(int idUser, int semester, int year) throws Exception{
		try {
			InternshipJuryDAO dao = new InternshipJuryDAO();
			
			return dao.listByAppraiser(idUser, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipJury> listByStudent(int idUser, int semester, int year) throws Exception{
		try {
			InternshipJuryDAO dao = new InternshipJuryDAO();
			
			List<InternshipJury> list = dao.listByStudent(idUser, semester, year);
			
			for(InternshipJury jury : list) {
				jury.getInternship().setFinalReport(null);
				jury.getInternship().setInternshipPlan(null);
			}
			
			return list;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipJury findById(int id) throws Exception{
		try {
			InternshipJuryDAO dao = new InternshipJuryDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipJury findByInternship(int idInternship) throws Exception{
		try {
			InternshipJuryDAO dao = new InternshipJuryDAO();
			
			InternshipJury jury = dao.findByInternship(idInternship);
			
			if(jury == null) {
				SigesConfig config = new SigesConfigBO().findByDepartment(new InternshipBO().findIdDepartment(idInternship));
				
				if(config.getJuryFormat() == JuryFormat.SESSION) {
					InternshipPosterRequest request = new InternshipPosterRequestBO().findByInternship(idInternship);
					
					if((request != null) && (request.getIdInternshipPosterRequest() != 0)) {
						request.setAppraisers(new InternshipPosterAppraiserRequestBO().listAppraisers(request.getIdInternshipPosterRequest()));
						
						jury = new InternshipJury();
						
						jury.setInternship(new InternshipBO().findById(idInternship));
						
						jury.setSupervisorFillJuryForm(config.isSupervisorFillJuryForm());
						jury.setJuryFormat(config.getJuryFormat());
						jury.setPosterRequest(request);
						
						InternshipJuryAppraiser appraiser = new InternshipJuryAppraiser();
						appraiser.setChair(true);
						appraiser.setAppraiser(jury.getInternship().getSupervisor());
						
						jury.setAppraisers(new ArrayList<InternshipJuryAppraiser>());
						jury.getAppraisers().add(appraiser);
						
						for(InternshipPosterAppraiserRequest a : request.getAppraisers()) {
							InternshipJuryAppraiser app = new InternshipJuryAppraiser();
							app.setChair(false);
							app.setSubstitute(a.isSubstitute());
							app.setAppraiser(a.getAppraiser());
							jury.getAppraisers().add(app);
						}
						
						jury.setParticipants(new ArrayList<InternshipJuryStudent>());
					}
				}
				
				if(jury == null) {
					jury = new InternshipJury();
					
					jury.setInternship(new InternshipBO().findById(idInternship));
					
					jury.setSupervisorFillJuryForm(config.isSupervisorFillJuryForm());
					jury.setJuryFormat(config.getJuryFormat());
					
					InternshipJuryAppraiser appraiser = new InternshipJuryAppraiser();
					appraiser.setChair(true);
					appraiser.setAppraiser(jury.getInternship().getSupervisor());
					
					jury.setAppraisers(new ArrayList<InternshipJuryAppraiser>());
					jury.getAppraisers().add(appraiser);
					
					jury.setParticipants(new ArrayList<InternshipJuryStudent>());
				}
			}
			
			return jury;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdDepartment(int idJury) throws Exception{
		try {
			InternshipJuryDAO dao = new InternshipJuryDAO();
			
			return dao.findIdDepartment(idJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean saveSupervisorScore(int idUser, int idInternshipJury, double score) throws Exception {
		try {
			if(score < 0) {
				throw new Exception("A nota deve ser maior ou igual a zero.");
			}
			if(Document.hasSignature(DocumentType.INTERNSHIPJURY, idInternshipJury)) {
				throw new Exception("A banca não pode ser alterada pois a ficha de avaliação já foi assinada.");
			}
			
			boolean hasAllScores;
			try {
				hasAllScores = this.hasAllScores(idInternshipJury);
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				hasAllScores = false;
			}
			
			boolean ret = new InternshipJuryDAO().saveSupervisorScore(idUser, idInternshipJury, score);
			
			try {
				if(!hasAllScores && this.hasAllScores(idInternshipJury)) {
					this.sendRequestSupervisorSignJuryForm(idInternshipJury);
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
			
			return ret;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, InternshipJury jury) throws Exception{
		try {
			boolean insert = (jury.getIdInternshipJury() == 0);
			InternshipJury oldJury = null;
			boolean hasAllScores;
			
			if((jury.getInternship() == null) || (jury.getInternship().getIdInternship() == 0)){
				throw new Exception("Informe o estágio a que a banca pertence.");
			}
			if(jury.getLocal().isEmpty()){
				throw new Exception("Informe o local da banca.");
			}
			if(jury.getIdInternshipJury() == 0) {
				jury.setStartTime(jury.getDate());
				
				try {
					SigesConfig config = new SigesConfigBO().findByDepartment(new InternshipBO().findIdDepartment(jury.getInternship().getIdInternship()));
					jury.setEndTime(DateUtils.addMinute(jury.getStartTime(), config.getJuryTime()));
				} catch (Exception e) {
					jury.setEndTime(DateUtils.addMinute(jury.getStartTime(), 30));	
				}
			}
			if(jury.getAppraisers() != null){
				InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
				
				User supervisor = jury.getSupervisor();
				int countChair = 0;
				boolean findSupervisor = (jury.getJuryFormat() == JuryFormat.SESSION);
				
				for(InternshipJuryAppraiser appraiser : jury.getAppraisers()){
					if(jury.getJuryFormat() == JuryFormat.INDIVIDUAL) {
						if(bo.appraiserHasJury(jury.getIdInternshipJury(), appraiser.getAppraiser().getIdUser(), jury.getDate())){
							throw new Exception("O membro " + appraiser.getAppraiser().getName() + " já tem uma banca marcada que conflita com este horário.");
						}
					}
					
					if(appraiser.getAppraiser().getIdUser() == supervisor.getIdUser()) {
						findSupervisor = true;
					}
					
					if(appraiser.isSubstitute() || (jury.getJuryFormat() == JuryFormat.SESSION)) {
						appraiser.setChair(false);
					}
					
					if(appraiser.isChair()) {
						countChair++;
					}
				}
				
				if(findSupervisor) {
					jury.setSupervisorAbsenceReason("");
				} else if(jury.getSupervisorAbsenceReason().trim().isEmpty()) {
					throw new Exception("Informe o motivo do Professor Orientador não estar presidindo a banca.");
				}
				if(jury.getJuryFormat() == JuryFormat.INDIVIDUAL) {
					if(countChair == 0) {
						throw new Exception("É preciso indicar o presidente da banca.");
					} else if(countChair > 1) {
						throw new Exception("Apenas um membro pode ser presidente da banca.");
					}
				}
			}
			if(Document.hasSignature(DocumentType.INTERNSHIPJURY, jury.getIdInternshipJury())) {
				throw new Exception("A banca não pode ser alterada pois a ficha de avaliação já foi assinada.");
			}
			
			try {
				hasAllScores = this.hasAllScores(jury.getIdInternshipJury());
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				hasAllScores = false;
			}
			
			InternshipJuryDAO dao = new InternshipJuryDAO();
			
			if(!insert){
				oldJury = dao.findById(jury.getIdInternshipJury());
				oldJury.setAppraisers(new InternshipJuryAppraiserBO().listAppraisers(jury.getIdInternshipJury()));
			}
			
			int id = dao.save(idUser, jury);
			
			try{
				jury.setInternship(new InternshipBO().findById(jury.getInternship().getIdInternship()));
				
				EmailMessageBO bo = new EmailMessageBO();
				List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
				
				keys.add(new EmailMessageEntry<String, String>("student", jury.getInternship().getStudent().getName()));
				keys.add(new EmailMessageEntry<String, String>("title", jury.getInternship().getReportTitle()));
				keys.add(new EmailMessageEntry<String, String>("date", new SimpleDateFormat("dd/MM/yyyy").format(jury.getDate())));
				keys.add(new EmailMessageEntry<String, String>("time", new SimpleDateFormat("HH:mm").format(jury.getDate())));
				keys.add(new EmailMessageEntry<String, String>("local", jury.getLocal()));
				keys.add(new EmailMessageEntry<String, String>("company", jury.getInternship().getCompany().getName()));
				keys.add(new EmailMessageEntry<String, String>("appraiser", jury.getSupervisor().getName()));
				
				if(insert){
					bo.sendEmail(jury.getInternship().getStudent().getIdUser(), MessageType.INTERNSHIPJURYINCLUDEDSTUDENT, keys);
					
					for(InternshipJuryAppraiser appraiser : jury.getAppraisers()){
						keys.remove(keys.size() - 1);
						keys.add(new EmailMessageEntry<String, String>("appraiser", appraiser.getAppraiser().getName()));
						bo.sendEmail(appraiser.getAppraiser().getIdUser(), MessageType.INTERNSHIPJURYINCLUDEDAPPRAISER, keys);
					}
				}else{
					boolean juryChanged = ((!jury.getDate().equals(oldJury.getDate())) || (!jury.getLocal().equals(oldJury.getLocal())));
					
					if(juryChanged){
						bo.sendEmail(jury.getInternship().getStudent().getIdUser(), MessageType.INTERNSHIPJURYCHANGEDSTUDENT, keys);
					}
					
					//Membro removido da banca
					for(InternshipJuryAppraiser a : oldJury.getAppraisers()){
						boolean find = false;
						
						for(InternshipJuryAppraiser a2 : jury.getAppraisers()){
							if(a.getAppraiser().getIdUser() == a2.getAppraiser().getIdUser()){
								find = true;
							}
						}
						
						if(!find){
							keys.remove(keys.size() - 1);
							keys.add(new EmailMessageEntry<String, String>("appraiser", a.getAppraiser().getName()));
							bo.sendEmail(a.getAppraiser().getIdUser(), MessageType.INTERNSHIPJURYREMOVEDAPPRAISER, keys);
						}
					}
					
					//Alterações de membros da banca
					for(InternshipJuryAppraiser a : jury.getAppraisers()){
						boolean find = false;
						
						for(InternshipJuryAppraiser a2 : oldJury.getAppraisers()){
							if(a.getAppraiser().getIdUser() == a2.getAppraiser().getIdUser()){
								find = true;
							}
						}
						
						keys.remove(keys.size() - 1);
						keys.add(new EmailMessageEntry<String, String>("appraiser", a.getAppraiser().getName()));
						
						if(!find){
							bo.sendEmail(a.getAppraiser().getIdUser(), MessageType.INTERNSHIPJURYINCLUDEDAPPRAISER, keys);
						}else if(juryChanged){
							bo.sendEmail(a.getAppraiser().getIdUser(), MessageType.INTERNSHIPJURYCHANGEDAPPRAISER, keys);	
						}
					}
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
			
			try {
				if(!hasAllScores && this.hasAllScores(jury.getIdInternshipJury())) {
					this.sendRequestSupervisorSignJuryForm(jury.getIdInternshipJury());
				}
			} catch(Exception e) { 
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
			
			return id;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void sendJuryFormSignedMessage(int idInternshipJury) throws Exception {
		InternshipJury jury = this.findById(idInternshipJury);
		User manager = new UserBO().findManager(jury.getInternship().getDepartment().getIdDepartment(), SystemModule.SIGES);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
		keys.add(new EmailMessageEntry<String, String>("student", jury.getInternship().getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("supervisor", jury.getInternship().getSupervisor().getName()));
		keys.add(new EmailMessageEntry<String, String>("company", jury.getInternship().getCompany().getName()));
		
		new EmailMessageBO().sendEmail(manager.getIdUser(), MessageType.SIGNEDINTERNSHIPPOSTERREQUEST, keys);
	}
	
	public void sendRequestSignJuryFormMessage(int idInternshipJury, List<User> users) throws Exception {
		InternshipJury jury = this.findById(idInternshipJury);
		
		for(User user : users) {
			List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
			
			keys.add(new EmailMessageEntry<String, String>("name", user.getName()));
			keys.add(new EmailMessageEntry<String, String>("student", jury.getInternship().getStudent().getName()));
			keys.add(new EmailMessageEntry<String, String>("supervisor", jury.getInternship().getSupervisor().getName()));
			keys.add(new EmailMessageEntry<String, String>("company", jury.getInternship().getCompany().getName()));
			
			new EmailMessageBO().sendEmail(user.getIdUser(), MessageType.REQUESTSIGNINTERNSHIPJURYFORM, keys, false);
		}
	}
	
	public void sendRequestSupervisorSignJuryForm(int idInternshipJury) throws Exception {
		InternshipJury jury = this.findById(idInternshipJury);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("student", jury.getInternship().getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("supervisor", jury.getInternship().getSupervisor().getName()));
		keys.add(new EmailMessageEntry<String, String>("company", jury.getInternship().getCompany().getName()));
		
		new EmailMessageBO().sendEmail(jury.getInternship().getSupervisor().getIdUser(), MessageType.REQUESTSUPERVISORSIGNINTERNSHIPJURYFORM, keys);
	}
	
	public boolean canAddAppraiser(InternshipJury jury, User appraiser) throws Exception{
		if(jury.getAppraisers() != null){
			for(InternshipJuryAppraiser ja : jury.getAppraisers()){
				if(ja.getAppraiser().getIdUser() == appraiser.getIdUser()){
					throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
				}
			}
		}else if(jury.getIdInternshipJury() != 0){
			InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
			InternshipJuryAppraiser ja = bo.findByAppraiser(jury.getIdInternshipJury(), appraiser.getIdUser());
			
			if(ja != null){
				throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
			}
		}
		
		return true;
	}
	
	public boolean canRemoveAppraiser(InternshipJury jury, User appraiser) throws Exception{
		if(jury.getIdInternshipJury() != 0){
			InternshipJuryAppraiserScoreBO bo = new InternshipJuryAppraiserScoreBO();
			if(bo.hasScore(jury.getIdInternshipJury(), appraiser.getIdUser())){
				throw new Exception("O membro já lançou as notas para esta banca e não pode ser removido.");
			}	
		}
		
		return true;
	}
	
	public byte[] getJuryForm(int idJury) throws Exception {
		if(Document.hasSignature(DocumentType.INTERNSHIPJURY, idJury)) {
			return Document.getSignedDocument(DocumentType.INTERNSHIPJURY, idJury);
		} else {
			br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipJury dataset = SignDatasetBuilder.build(this.getJuryFormReport(idJury));
			
			List<br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipJury> list = new ArrayList<br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipJury>();
			list.add(dataset);
			
			return new ReportUtils().createPdfStream(list, "InternshipJuryForm", this.findIdDepartment(idJury)).toByteArray();
		}
	}
	
	public InternshipJuryFormReport getJuryFormReport(int idJury) throws Exception{
		try {
			InternshipJuryFormReport report = new InternshipJuryFormReport();
			InternshipJury jury = this.findById(idJury);
			
			report.setDate(jury.getDate());
			report.setLocal(jury.getLocal());
			report.setComments(jury.getComments());
			report.setSupervisorPonderosity(jury.getSupervisorPonderosity());
			report.setCompanySupervisorPonderosity(jury.getCompanySupervisorPonderosity());
			report.setAppraisersPonderosity(10 - jury.getSupervisorPonderosity() - jury.getCompanySupervisorPonderosity());
			report.setCompanySupervisorScore(jury.getCompanySupervisorScore());
			report.setResult(jury.getResult());
			
			InternshipBO ibo = new InternshipBO();
			Internship internship = ibo.findById(jury.getInternship().getIdInternship());
			
			report.setIdStudent(internship.getStudent().getIdUser());
			report.setIdSupervisor(internship.getSupervisor().getIdUser());
			report.setSupervisor(internship.getSupervisor().getName());
			report.setTitle(internship.getReportTitle());
			report.setStudent(internship.getStudent().getName());
			report.setCompany(internship.getCompany().getName());
			
			User supervisor = jury.getSupervisor();
			
			InternshipJuryAppraiserBO appraiserBO = new InternshipJuryAppraiserBO();
			List<InternshipJuryAppraiser> appraisers = appraiserBO.listAppraisers(idJury);
			int member = 1;
			
			for(InternshipJuryAppraiser appraiser : appraisers) {
				if(!appraiser.isSubstitute()) {
					InternshipJuryAppraiserScoreBO bo = new InternshipJuryAppraiserScoreBO();
					List<InternshipJuryAppraiserScore> list = bo.listScores(appraiser.getIdInternshipJuryAppraiser());
					JuryFormAppraiserReport appraiserReport = new JuryFormAppraiserReport();
					JuryFormAppraiserScoreReport scoreReport = new JuryFormAppraiserScoreReport();
					double scoreSum = 0, writingPonderosity = 0, oralPonderosity = 0, argumentationPonderosity = 0;
					boolean isSupervisor = false;
					
					appraiserReport.setIdUser(appraiser.getAppraiser().getIdUser());
					appraiserReport.setName(appraiser.getAppraiser().getName());
					appraiserReport.setComments(appraiser.getComments());
					appraiserReport.setDate(report.getDate());
					appraiserReport.setLocal(report.getLocal());
					appraiserReport.setStudent(report.getStudent());
					appraiserReport.setTitle(report.getTitle());
					appraiserReport.setCompany(report.getCompany());
					
					for(InternshipJuryAppraiserScore score : list) {
						JuryFormAppraiserDetailReport appraiserDetail = new JuryFormAppraiserDetailReport();
						
						appraiserDetail.setEvaluationItemType(score.getInternshipEvaluationItem().getType().toString());
						appraiserDetail.setEvaluationItem(score.getInternshipEvaluationItem().getDescription());
						appraiserDetail.setPonderosity(score.getInternshipEvaluationItem().getPonderosity());
						appraiserDetail.setScore(this.round(score.getScore()));
						appraiserDetail.setOrder(appraiserReport.getDetail().size() + 1);
						
						appraiserReport.getDetail().add(appraiserDetail);
						
						switch(score.getInternshipEvaluationItem().getType()){
							case WRITING:
								scoreReport.setScoreWriting(scoreReport.getScoreWriting() + score.getScore());
								writingPonderosity = writingPonderosity + score.getInternshipEvaluationItem().getPonderosity();
								break;
							case ORAL:
								scoreReport.setScoreOral(scoreReport.getScoreOral() + score.getScore());
								oralPonderosity = oralPonderosity + score.getInternshipEvaluationItem().getPonderosity();
								break;
							case ARGUMENTATION:
								scoreReport.setScoreArgumentation(scoreReport.getScoreArgumentation() + score.getScore());
								argumentationPonderosity = argumentationPonderosity + score.getInternshipEvaluationItem().getPonderosity();
								break;
						}
					}
					
					for(JuryFormAppraiserDetailReport appraiserDetail : appraiserReport.getDetail()){
						switch(EvaluationItemType.fromString(appraiserDetail.getEvaluationItemType())){
							case WRITING:
								appraiserDetail.setPonderositySum(writingPonderosity);
								appraiserDetail.setScoreSum(scoreReport.getScoreWriting());
								break;
							case ORAL:
								appraiserDetail.setPonderositySum(oralPonderosity);
								appraiserDetail.setScoreSum(scoreReport.getScoreOral());
								break;
							case ARGUMENTATION:
								appraiserDetail.setPonderositySum(argumentationPonderosity);
								appraiserDetail.setScoreSum(scoreReport.getScoreArgumentation());
								break;
						}
					}
					
					scoreSum = scoreReport.getScoreWriting() + scoreReport.getScoreOral() + scoreReport.getScoreArgumentation();
					
					appraiserReport.setScore(this.round(scoreSum));
					
					if(appraiser.getAppraiser().getIdUser() != supervisor.getIdUser()) {
						appraiserReport.setDescription("Aval. " + String.valueOf(member));
						
						if(member == 1){
							report.setAppraiser1Score(appraiserReport.getScore());
						}else if(member == 2){
							report.setAppraiser2Score(appraiserReport.getScore());
						}
						
						member = member + 1;
					} else {
						isSupervisor = true;
						appraiserReport.setDescription("Orientador");
						
						if(jury.isSupervisorFillJuryForm()) {
							report.setSupervisorScore(appraiserReport.getScore());
						} else {
							report.setSupervisorScore(jury.getSupervisorScore());
						}
					}
					
					scoreReport.setIdUser(appraiserReport.getIdUser());
					scoreReport.setName(appraiserReport.getName());
					scoreReport.setDescription(appraiserReport.getDescription());
					
					JuryFormAppraiserReport signature = new JuryFormAppraiserReport();
					signature.setName(appraiserReport.getName());
					signature.setDescription(appraiserReport.getDescription());
					
					if(isSupervisor) {
						if(jury.isSupervisorFillJuryForm()) {
							report.getAppraisers().add(0, appraiserReport);
						}
						
						report.getAppraisersSignatures().add(0, signature);
					} else {
						report.getAppraisers().add(appraiserReport);
						report.getAppraisersSignatures().add(signature);
					}
				}
			}
			
			if((report.getAppraiser1Score() > 0) && (report.getAppraiser2Score() > 0) && (report.getSupervisorScore() > 0) && (report.getCompanySupervisorScore() > 0)) {
				report.setFinalScore(((((report.getAppraiser1Score() + report.getAppraiser2Score()) / 2.0) * report.getAppraisersPonderosity()) + (report.getSupervisorScore() * report.getSupervisorPonderosity()) + (report.getCompanySupervisorScore() * report.getCompanySupervisorPonderosity())) / (report.getAppraisersPonderosity() + report.getSupervisorPonderosity() + report.getCompanySupervisorPonderosity()));	
			} else {
				report.setFinalScore(0);
			}
			
			return report;
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	private double round(double value){
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public byte[] getScheduleReport(int idDepartment, int idUser, int semester, int year) throws Exception {
		List<CalendarReport> report = this.getScheduleReportList(idDepartment, idUser, semester, year);
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(report, "InternshipCalendar", idDepartment);
		return rep.toByteArray();
	}
	
	public List<CalendarReport> getScheduleReportList(int idDepartment, int idUser, int semester, int year) throws Exception{
		List<InternshipJury> list;
		List<CalendarReport> report = new ArrayList<CalendarReport>();
		
		if(idUser == 0){
			list = this.listBySemester(idDepartment, semester, year);
		}else{
			UserBO bo = new UserBO();
			User user = bo.findById(idUser);
			
			if(user.hasProfile(UserProfile.PROFESSOR)){
				list = this.listByAppraiser(idUser, semester, year);	
			}else{
				list = this.listByStudent(idUser, semester, year);
			}
		}
		
		for(InternshipJury jury : list){
			CalendarReport c = new CalendarReport();
			
			c.setDate(jury.getDate());
			c.setLocal(jury.getLocal());
			
			InternshipBO ibo = new InternshipBO();
			Internship internship = ibo.findById(jury.getInternship().getIdInternship());
			
			c.setStudent(internship.getStudent().getName());
			c.setCompany(internship.getCompany().getName());
			
			InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
			List<InternshipJuryAppraiser> appraisers = bo.listAppraisers(jury.getIdInternshipJury());
			
			for(InternshipJuryAppraiser appraiser : appraisers){
				if(c.getAppraisers().isEmpty()){
					c.setAppraisers(appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")));
				}else{
					c.setAppraisers(c.getAppraisers() + "\n" + appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")));	
				}
			}
			
			report.add(c);
		}
		
		return report;
	}
	
	public boolean hasScores(int idInternshipJury) throws Exception{
		InternshipJuryDAO dao = new InternshipJuryDAO();
		
		return dao.hasScores(idInternshipJury);
	}
	
	public boolean hasAllScores(int idJury) throws Exception {
		try {
			return new InternshipJuryDAO().hasAllScores(idJury);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryStudentReport> listJuryStudentReport(int idDepartment, int idInternshipJury, Date startDate, Date endDate, boolean orderByDate) throws Exception {
		try {
			InternshipJuryDAO dao = new InternshipJuryDAO();
			
			if(idInternshipJury <= 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				startDate = cal.getTime();
				
				cal = Calendar.getInstance();
				cal.setTime(endDate);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				endDate = cal.getTime();
			}
			
			return dao.listJuryStudentReport(idDepartment, idInternshipJury, startDate, endDate, orderByDate);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public byte[] getJuryStudentReport(int idDepartment, int idInternshipJury, Date startDate, Date endDate, boolean groupByJury) throws Exception {
		List<JuryStudentReport> list = this.listJuryStudentReport(idDepartment, idInternshipJury, startDate, endDate, groupByJury);
		
		ByteArrayOutputStream report;
		
		if(groupByJury) {
			report = new ReportUtils().createPdfStream(list, "JuryParticipantsListGrouped", idDepartment);
		} else {
			report = new ReportUtils().createPdfStream(list, "JuryParticipantsList", idDepartment);
		}
		
		return report.toByteArray();
	}
	
	public byte[] getJuryParticipantsSignature(int idInternshipJury) throws Exception {
		InternshipJuryFormReport report = this.getJuryFormReport(idInternshipJury);
		
		JuryFormReport report2 = new JuryFormReport();
		
		report2.setTitle(report.getCompany());
		report2.setStage(0);
		report2.setStudent(report.getStudent());
		report2.setDate(report.getDate());
		
		List<JuryFormReport> list = new ArrayList<JuryFormReport>();
		list.add(report2);
		
		return new ReportUtils().createPdfStream(list, "JuryParticipants", this.findIdDepartment(idInternshipJury)).toByteArray();
	}
	
	public byte[] getJuryGradesReport(int idDepartment, int semester, int year, boolean listAll) throws Exception {
		List<JuryGrade> list = this.getJuryGradesReportList(idDepartment, semester, year, listAll);
		
		return new ReportUtils().createPdfStream(list, "InternshipJuryGrades", idDepartment).toByteArray();
	}
	
	public List<JuryGrade> getJuryGradesReportList(int idDepartment, int semester, int year, boolean listAll) throws Exception {
		List<JuryGrade> report = new ArrayList<JuryGrade>();
		
		if(listAll) {
			List<Internship> list = new InternshipBO().list(idDepartment, year - 1, 0, 0, 0, 1, -1, null, null, null, null);
			List<Internship> list2 = new InternshipBO().list(idDepartment, year, 0, 0, 0, 1, -1, null, null, null, null);
			
			for(Internship internship : list2) {
				boolean find = false;
				
				for(Internship i : list) {
					if(i.getIdInternship() == internship.getIdInternship()) {
						find = true;
					}
				}
				
				if(!find) {
					list.add(internship);
				}
			}
			
			for(Internship internship : list) {
				JuryGrade grade = new JuryGrade();
				
				grade.setStudent(internship.getStudent().getName());
				grade.setJuryDate(null);
				grade.setStage(0);
				
				InternshipJury jury = this.findByInternship(internship.getIdInternship());
				
				if((jury != null) && (jury.getIdInternshipJury() != 0)) {
					grade.setResult(jury.getResult());
					grade.setJuryDate(jury.getDate());
					
					InternshipJuryFormReport form = this.getJuryFormReport(jury.getIdInternshipJury());
					grade.setScore(form.getFinalScore());	
				}
				
				InternshipFinalDocument doc = new InternshipFinalDocumentBO().findByInternship(internship.getIdInternship());
				if((doc != null) && (doc.getIdInternshipFinalDocument() != 0)) {
					grade.setSupervisorFeedback(doc.getSupervisorFeedback());
				}
				
				if(grade.getSupervisorFeedback() == DocumentFeedback.DISAPPROVED) {
					grade.setResult(JuryResult.DISAPPROVED);
				} else if(grade.getSupervisorFeedback() == DocumentFeedback.NONE) {
					grade.setResult(JuryResult.NONE);
				}
				
				report.add(grade);
			}
		} else {
			List<InternshipJury> list = this.listBySemester(idDepartment, semester, year);
			
			for(InternshipJury jury : list) {
				JuryGrade grade = new JuryGrade();
				
				grade.setStudent(jury.getStudent().getName());
				grade.setJuryDate(jury.getDate());
				grade.setStage(0);
				grade.setResult(jury.getResult());
				
				InternshipJuryFormReport form = this.getJuryFormReport(jury.getIdInternshipJury());
				grade.setScore(form.getFinalScore());
				
				InternshipFinalDocument doc = new InternshipFinalDocumentBO().findByInternship(jury.getInternship().getIdInternship());
				if((doc != null) && (doc.getIdInternshipFinalDocument() != 0)) {
					grade.setSupervisorFeedback(doc.getSupervisorFeedback());
				}
				
				if(grade.getSupervisorFeedback() == DocumentFeedback.DISAPPROVED) {
					grade.setResult(JuryResult.DISAPPROVED);
				} else if(grade.getSupervisorFeedback() == DocumentFeedback.NONE) {
					grade.setResult(JuryResult.NONE);
				}
				
				report.add(grade);
			}
		}
		
		@SuppressWarnings("unchecked")
		Comparator<JuryGrade> comparator = new BeanComparator("student");
		Collections.sort(report, comparator);
		
		return report;
	}

}
