package br.edu.utfpr.dv.siacoes.bo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipJuryDAO;
import br.edu.utfpr.dv.siacoes.model.CalendarReport;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserScore;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserScoreReport;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

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
			
			return dao.listByStudent(idUser, semester, year);
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
			
			return dao.findByInternship(idInternship);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(InternshipJury jury) throws Exception{
		try {
			boolean insert = (jury.getIdInternshipJury() == 0);
			InternshipJury oldJury = null;
			
			if((jury.getInternship() == null) || (jury.getInternship().getIdInternship() == 0)){
				throw new Exception("Informe o estágio a que a banca pertence.");
			}
			if(jury.getLocal().isEmpty()){
				throw new Exception("Informe o local da banca.");
			}
			if(jury.getAppraisers() != null){
				InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
				
				User supervisor = jury.getSupervisor();
				boolean find = false;
				
				for(InternshipJuryAppraiser appraiser : jury.getAppraisers()){
					if(bo.appraiserHasJury(jury.getIdInternshipJury(), appraiser.getAppraiser().getIdUser(), jury.getDate())){
						throw new Exception("O membro " + appraiser.getAppraiser().getName() + " já tem uma banca marcada que conflita com este horário.");
					}
					
					if(appraiser.getAppraiser().getIdUser() == supervisor.getIdUser()){
						find = true;
					}
				}
				
				if(!find){
					throw new Exception("O orientador deve participar da banca.");
				}
			}
			
			InternshipJuryDAO dao = new InternshipJuryDAO();
			
			if(!insert){
				oldJury = dao.findById(jury.getIdInternshipJury());
				oldJury.setAppraisers(new InternshipJuryAppraiserBO().listAppraisers(jury.getIdInternshipJury()));
			}
			
			int id = dao.save(jury);
			
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
			
			return id;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
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
		User supervisor = jury.getSupervisor();
		
		if(appraiser.getIdUser() == supervisor.getIdUser()){
			throw new Exception("O orientador deve estar na banca.");
		}
		
		if(jury.getIdInternshipJury() != 0){
			InternshipJuryAppraiserScoreBO bo = new InternshipJuryAppraiserScoreBO();
			if(bo.hasScore(jury.getIdInternshipJury(), appraiser.getIdUser())){
				throw new Exception("O membro já lançou as notas para esta banca e não pode ser removido.");
			}	
		}
		
		return true;
	}
	
	public InternshipJuryFormReport getFormReport(int idJury) throws Exception{
		try{
			InternshipJuryFormReport report = new InternshipJuryFormReport();
			InternshipJury jury = this.findById(idJury);
			
			report.setDate(jury.getDate());
			report.setComments(jury.getComments());
			report.setSupervisorPonderosity(jury.getSupervisorPonderosity());
			report.setCompanySupervisorPonderosity(jury.getCompanySupervisorPonderosity());
			report.setAppraisersPonderosity(10 - jury.getSupervisorPonderosity() - jury.getCompanySupervisorPonderosity());
			report.setCompanySupervisorScore(jury.getCompanySupervisorScore());
			report.setResult(jury.getResult());
			
			InternshipBO ibo = new InternshipBO();
			Internship internship = ibo.findById(jury.getInternship().getIdInternship());
			
			report.setTitle(internship.getReportTitle());
			report.setStudent(internship.getStudent().getName());
			report.setCompany(internship.getCompany().getName());
			
			User supervisor = jury.getSupervisor();
			
			InternshipJuryAppraiserBO appraiserBO = new InternshipJuryAppraiserBO();
			List<InternshipJuryAppraiser> appraisers = appraiserBO.listAppraisers(idJury);
			int member = 1;
			
			for(InternshipJuryAppraiser appraiser : appraisers){
				InternshipJuryAppraiserScoreBO bo = new InternshipJuryAppraiserScoreBO();
				List<InternshipJuryAppraiserScore> list = bo.listScores(appraiser.getIdInternshipJuryAppraiser());
				JuryFormAppraiserReport appraiserReport = new JuryFormAppraiserReport();
				JuryFormAppraiserScoreReport scoreReport = new JuryFormAppraiserScoreReport();
				double scoreSum = 0, writingPonderosity = 0, oralPonderosity = 0, argumentationPonderosity = 0;
				
				appraiserReport.setName(appraiser.getAppraiser().getName());
				appraiserReport.setComments(appraiser.getComments());
				appraiserReport.setDate(report.getDate());
				appraiserReport.setStudent(report.getStudent());
				appraiserReport.setTitle(report.getTitle());
				appraiserReport.setCompany(report.getCompany());
				
				for(InternshipJuryAppraiserScore score : list){
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
				
				if(appraiser.getAppraiser().getIdUser() != supervisor.getIdUser()){
					appraiserReport.setDescription("Aval. " + String.valueOf(member));
					
					if(member == 1){
						report.setAppraiser1Score(appraiserReport.getScore());
					}else if(member == 2){
						report.setAppraiser2Score(appraiserReport.getScore());
					}
					
					member = member + 1;
				}else{
					appraiserReport.setDescription("Orientador");
					report.setSupervisorScore(appraiserReport.getScore());
				}
				
				scoreReport.setName(appraiserReport.getName());
				scoreReport.setDescription(appraiserReport.getDescription());
				
				report.getAppraisers().add(appraiserReport);
			}
			
			if((report.getAppraiser1Score() > 0) && (report.getAppraiser2Score() > 0) && (report.getSupervisorScore() > 0) && (report.getCompanySupervisorScore() > 0)){
				report.setFinalScore(((((report.getAppraiser1Score() + report.getAppraiser2Score()) / 2.0) * report.getAppraisersPonderosity()) + (report.getSupervisorScore() * report.getSupervisorPonderosity()) + (report.getCompanySupervisorScore() * report.getCompanySupervisorPonderosity())) / (report.getAppraisersPonderosity() + report.getSupervisorPonderosity() + report.getCompanySupervisorPonderosity()));	
			}else{
				report.setFinalScore(0);
			}
			
			return report;
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	private double round(double value){
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public List<CalendarReport> getCalendarReport(int idDepartment, int idUser, int semester, int year) throws Exception{
		List<InternshipJury> list;
		List<CalendarReport> report = new ArrayList<CalendarReport>();
		
		if(idUser == 0){
			list = this.listBySemester(idDepartment, semester, year);
		}else{
			UserBO bo = new UserBO();
			User user = bo.findById(idUser);
			
			if((user.getProfile() == UserProfile.PROFESSOR) || (user.getProfile() == UserProfile.ADMINISTRATOR)){
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
					c.setAppraisers(appraiser.getAppraiser().getName());
				}else{
					c.setAppraisers(c.getAppraisers() + "\n" + appraiser.getAppraiser().getName());	
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

}
