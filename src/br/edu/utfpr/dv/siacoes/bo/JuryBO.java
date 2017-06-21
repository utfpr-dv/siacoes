package br.edu.utfpr.dv.siacoes.bo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.JuryDAO;
import br.edu.utfpr.dv.siacoes.model.CalendarReport;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserScore;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserScoreReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.TermOfApprovalReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class JuryBO {
	
	public List<Jury> listBySemester(int idDepartment, int semester, int year) throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.listBySemester(idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Jury> listByAppraiser(int idUser, int semester, int year) throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.listByAppraiser(idUser, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Jury> listByStudent(int idUser, int semester, int year) throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.listByStudent(idUser, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Jury findById(int id) throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Jury findByProject(int idProject) throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.findByProject(idProject);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Jury findByThesis(int idThesis) throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.findByThesis(idThesis);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(Jury jury) throws Exception{
		try {
			boolean insert = (jury.getIdJury() == 0);
			
			if(((jury.getProject() == null) || (jury.getProject().getIdProject() == 0)) && ((jury.getThesis() == null) || (jury.getThesis().getIdThesis() == 0))){
				throw new Exception("Informe o projeto ou monografia a que a banca pertence.");
			}
			if(jury.getLocal().isEmpty()){
				throw new Exception("Informe o local da banca.");
			}
			if(jury.getAppraisers() != null){
				JuryAppraiserBO bo = new JuryAppraiserBO();
				
				User supervisor = jury.getSupervisor();
				boolean find = false;
				
				for(JuryAppraiser appraiser : jury.getAppraisers()){
					if(bo.appraiserHasJury(jury.getIdJury(), appraiser.getAppraiser().getIdUser(), jury.getDate())){
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
			
			JuryDAO dao = new JuryDAO();
			
			if(insert){
				//Banca marcada
			}else{
				//Reagendamento de banca
			}
			
			return dao.save(jury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean canAddAppraiser(Jury jury, User appraiser) throws Exception{
		if(jury.getAppraisers() != null){
			for(JuryAppraiser ja : jury.getAppraisers()){
				if(ja.getAppraiser().getIdUser() == appraiser.getIdUser()){
					throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
				}
			}
		}else if(jury.getIdJury() != 0){
			JuryAppraiserBO bo = new JuryAppraiserBO();
			JuryAppraiser ja = bo.findByAppraiser(jury.getIdJury(), appraiser.getIdUser());
			
			if(ja != null){
				throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
			}
		}
		
		return true;
	}
	
	public boolean canRemoveAppraiser(Jury jury, User appraiser) throws Exception{
		User supervisor = jury.getSupervisor();
		
		if(appraiser.getIdUser() == supervisor.getIdUser()){
			throw new Exception("O orientador deve estar na banca.");
		}
		
		if(jury.getIdJury() != 0){
			JuryAppraiserScoreBO bo = new JuryAppraiserScoreBO();
			if(bo.hasScore(jury.getIdJury(), appraiser.getIdUser())){
				throw new Exception("O membro já lançou as notas para esta banca e não pode ser removido.");
			}	
		}
		
		return true;
	}
	
	public JuryFormReport getFormReport(int idJury) throws Exception{
		try{
			JuryFormReport report = new JuryFormReport();
			Jury jury = this.findById(idJury);
			
			report.setDate(jury.getDate());
			report.setComments(jury.getComments());
			
			if((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)){
				ThesisBO bo = new ThesisBO();
				Thesis thesis = bo.findById(jury.getThesis().getIdThesis());
				
				report.setTitle(thesis.getTitle());
				report.setStudent(thesis.getStudent().getName());
				report.setStage(2);
			}else{
				ProjectBO bo = new ProjectBO();
				Project project = bo.findById(jury.getProject().getIdProject());
				
				report.setTitle(project.getTitle());
				report.setStudent(project.getStudent().getName());
				report.setStage(1);
			}
			
			User supervisor = jury.getSupervisor();
			
			JuryAppraiserBO appraiserBO = new JuryAppraiserBO();
			List<JuryAppraiser> appraisers = appraiserBO.listAppraisers(idJury);
			int member = 1;
			
			for(JuryAppraiser appraiser : appraisers){
				JuryAppraiserScoreBO bo = new JuryAppraiserScoreBO();
				List<JuryAppraiserScore> list = bo.listScores(appraiser.getIdJuryAppraiser());
				JuryFormAppraiserReport appraiserReport = new JuryFormAppraiserReport();
				JuryFormAppraiserScoreReport scoreReport = new JuryFormAppraiserScoreReport();
				double scoreSum = 0, scorePonderosity = 0, writingPonderosity = 0, oralPonderosity = 0, argumentationPonderosity = 0;
				
				appraiserReport.setName(appraiser.getAppraiser().getName());
				appraiserReport.setComments(appraiser.getComments());
				appraiserReport.setDate(report.getDate());
				appraiserReport.setStage(report.getStage());
				appraiserReport.setStudent(report.getStudent());
				appraiserReport.setTitle(report.getTitle());
				
				for(JuryAppraiserScore score : list){
					JuryFormAppraiserDetailReport appraiserDetail = new JuryFormAppraiserDetailReport();
					
					appraiserDetail.setEvaluationItemType(score.getEvaluationItem().getType().toString());
					appraiserDetail.setEvaluationItem(score.getEvaluationItem().getDescription());
					appraiserDetail.setPonderosity(score.getEvaluationItem().getPonderosity());
					appraiserDetail.setScore(this.round(score.getScore()));
					appraiserDetail.setOrder(appraiserReport.getDetail().size() + 1);
					
					appraiserReport.getDetail().add(appraiserDetail);
					
					switch(score.getEvaluationItem().getType()){
						case WRITING:
							scoreReport.setScoreWriting(scoreReport.getScoreWriting() + (score.getScore() * score.getEvaluationItem().getPonderosity()));
							writingPonderosity = writingPonderosity + score.getEvaluationItem().getPonderosity();
							break;
						case ORAL:
							scoreReport.setScoreOral(scoreReport.getScoreOral() + (score.getScore() * score.getEvaluationItem().getPonderosity()));
							oralPonderosity = oralPonderosity + score.getEvaluationItem().getPonderosity();
							break;
						case ARGUMENTATION:
							scoreReport.setScoreArgumentation(scoreReport.getScoreArgumentation() + (score.getScore() * score.getEvaluationItem().getPonderosity()));
							argumentationPonderosity = argumentationPonderosity + score.getEvaluationItem().getPonderosity();
							break;
					}
				}
				
				if(writingPonderosity > 0){
					scoreReport.setScoreWriting(this.round(scoreReport.getScoreWriting() / writingPonderosity));
				}
				if(oralPonderosity > 0){
					scoreReport.setScoreOral(this.round(scoreReport.getScoreOral() / oralPonderosity));
				}
				if(argumentationPonderosity > 0){
					scoreReport.setScoreArgumentation(this.round(scoreReport.getScoreArgumentation() / argumentationPonderosity));
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
				
				scoreSum = (scoreReport.getScoreWriting() * writingPonderosity) + (scoreReport.getScoreOral() * oralPonderosity) + (scoreReport.getScoreArgumentation() * argumentationPonderosity);
				scorePonderosity = writingPonderosity + oralPonderosity + argumentationPonderosity;
				
				scoreReport.setScore(this.round(scoreSum / scorePonderosity));
					
				if(appraiser.getAppraiser().getIdUser() != supervisor.getIdUser()){
					appraiserReport.setDescription("Membro " + String.valueOf(member));
					member = member + 1;
				}else{
					appraiserReport.setDescription("Orientador");
				}
					
				scoreReport.setName(appraiserReport.getName());
				scoreReport.setDescription(appraiserReport.getDescription());
				
				if(appraiserReport.getDescription().equals("Orientador")){
					report.getAppraisers().add(0, appraiserReport);
					report.getScores().add(0, scoreReport);
				}else{
					report.getAppraisers().add(appraiserReport);
					report.getScores().add(scoreReport);
				}
			}
			
			report.setScore(0);
			if(report.getScores().size() > 0){
				for(JuryFormAppraiserScoreReport score : report.getScores()){
					report.setScore(report.getScore() + score.getScore());
				}
				report.setScore(this.round(report.getScore() / report.getScores().size()));
			}
			
			return report;
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	private double round(double value){
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(1, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public TermOfApprovalReport getTermOfApprovalReport(int idJury) throws Exception{
		try{
			Jury jury = this.findById(idJury);
			
			if((jury.getThesis() == null) || (jury.getThesis().getIdThesis() == 0)){
				throw new Exception("O Termo de Aprovação só é emitido para a defesa de TCC 2.");
			}
			
			JuryDAO dao = new JuryDAO();
			if(!dao.hasAllScores(idJury)){
				throw new Exception("Para gerar o Termo de Aprovação é necessário que todas as notas sejam lançadas.");
			}
			
			if(!dao.isApproved(idJury)){
				throw new Exception("Não é possível gerar o Termo de Aprovação pois o acadêmico não obteve a aprovação.");
			}
			
			ThesisBO bo = new ThesisBO();
			Thesis thesis = bo.findById(jury.getThesis().getIdThesis());
			
			TermOfApprovalReport report = new TermOfApprovalReport();
			
			report.setTitle(thesis.getTitle());
			report.setStudent(thesis.getStudent().getName());
			report.setSupervisor(thesis.getSupervisor().getName());
			report.setDate(jury.getDate());
			report.setLocal(jury.getLocal());
			report.setStartTime(jury.getStartTime());
			report.setEndTime(jury.getEndTime());
			
			JuryAppraiserBO bo2 = new JuryAppraiserBO();
			List<JuryAppraiser> list = bo2.listAppraisers(idJury);
			
			for(JuryAppraiser appraiser : list){
				if(appraiser.getAppraiser().getIdUser() != thesis.getSupervisor().getIdUser()){
					if(report.getMember1().isEmpty()){
						report.setMember1(appraiser.getAppraiser().getName());
					}else{
						report.setMember2(appraiser.getAppraiser().getName());
						break;
					}
				}
			}
			
			return report;
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<CalendarReport> getCalendarReport(int idDepartment, int idUser, int semester, int year) throws Exception{
		List<Jury> list;
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
		
		for(Jury jury : list){
			CalendarReport c = new CalendarReport();
			
			c.setDate(jury.getDate());
			c.setLocal(jury.getLocal());
			
			if((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)){
				ThesisBO bo = new ThesisBO();
				Thesis thesis = bo.findById(jury.getThesis().getIdThesis());
				
				c.setTitle(thesis.getTitle());
				c.setStudent(thesis.getStudent().getName());
				c.setStage(2);
			}else{
				ProjectBO bo = new ProjectBO();
				Project project = bo.findById(jury.getProject().getIdProject());
				
				c.setTitle(project.getTitle());
				c.setStudent(project.getStudent().getName());
				c.setStage(1);
			}
			
			JuryAppraiserBO bo = new JuryAppraiserBO();
			List<JuryAppraiser> appraisers = bo.listAppraisers(jury.getIdJury());
			
			for(JuryAppraiser appraiser : appraisers){
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
	
}
