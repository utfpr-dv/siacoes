package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanComparator;

import br.edu.utfpr.dv.siacoes.dao.JuryDAO;
import br.edu.utfpr.dv.siacoes.model.CalendarReport;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.Jury.JuryResult;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserScore;
import br.edu.utfpr.dv.siacoes.model.JuryBySemester;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserScoreReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
import br.edu.utfpr.dv.siacoes.model.JuryGrade;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.JuryStudentReport;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.TermOfApprovalReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

public class JuryBO {
	
	public List<Jury> listBySemester(int idDepartment, int semester, int year, int stage) throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.listBySemester(idDepartment, semester, year, stage);
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
			
			List<Jury> list = dao.listByStudent(idUser, semester, year);
			
			for(Jury jury : list) {
				if(jury.getProject() != null) {
					jury.getProject().setFile(null);
				}
				if(jury.getThesis() != null) {
					jury.getThesis().setFile(null);
				}
			}
			
			return list;
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
	
	public int findIdDepartment(int idJury) throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.findIdDepartment(idJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Jury findByStage(int idProposal, int stage) throws Exception {
		if(stage == 2) {
			Thesis thesis = new ThesisBO().findByProposal(idProposal);
			
			if((thesis == null) || (thesis.getIdThesis() == 0)) {
				return null;
			} else {
				return this.findByThesis(thesis.getIdThesis());
			}
		} else {
			Project project = new ProjectBO().findByProposal(idProposal);
			
			if((project == null) || (project.getIdProject() == 0)) {
				return null;
			} else {
				return this.findByProject(project.getIdProject());
			}
		}
	}
	
	public Jury findByProject(int idProject) throws Exception {
		try {
			JuryDAO dao = new JuryDAO();
			Jury jury = dao.findByProject(idProject);
			
			if(jury == null) {
				SigetConfig config = new SigetConfigBO().findByDepartment(new ProjectBO().findIdDepartment(idProject));
				
				if(config.isSupervisorJuryRequest()) {
					JuryRequest request = new JuryRequestBO().findByProject(idProject);
					
					if((request != null) && (request.getIdJuryRequest() != 0)) {
						request.setAppraisers(new JuryAppraiserRequestBO().listAppraisers(request.getIdJuryRequest()));
						
						jury = new Jury();
						
						jury.setProject(new Project());
						jury.getProject().setIdProject(idProject);
						jury.setAppraisers(new ArrayList<JuryAppraiser>());
						jury.setParticipants(new ArrayList<JuryStudent>());
						jury.setDate(request.getDate());
						jury.setLocal(request.getLocal());
						jury.setSupervisorAbsenceReason(request.getSupervisorAbsenceReason());
						jury.setJuryRequest(request);
						
						for(JuryAppraiserRequest a : request.getAppraisers()) {
							JuryAppraiser appraiser = new JuryAppraiser();
							
							appraiser.setAppraiser(a.getAppraiser());
							appraiser.setChair(a.isChair());
							appraiser.setSubstitute(a.isSubstitute());
							
							jury.getAppraisers().add(appraiser);
						}
					}
				}
				
				if(jury == null) {
					jury = new Jury();
					
					jury.setProject(new Project());
					jury.getProject().setIdProject(idProject);
					jury.setAppraisers(new ArrayList<JuryAppraiser>());
					jury.setParticipants(new ArrayList<JuryStudent>());
					
					JuryAppraiser appraiser = new JuryAppraiser();
					appraiser.setAppraiser(jury.getSupervisor());
					appraiser.setChair(true);
					
					jury.getAppraisers().add(appraiser);
					
					List<ProposalAppraiser> appraisers = new ProposalAppraiserBO().listAppraisers(new ProjectBO().findIdProposal(idProject));
					for(ProposalAppraiser a : appraisers) {
						try {
							if(this.canAddAppraiser(jury, a.getAppraiser())) {
								JuryAppraiser ja = new JuryAppraiser();
								
								ja.setAppraiser(a.getAppraiser());
								
								jury.getAppraisers().add(ja);
							}
						} catch (Exception ex) { }
					}
				}
				
				if(jury.getIdJury() == 0) {
					jury.setSupervisorAssignsGrades(config.isSupervisorAssignsGrades());
				}
			}
			
			return jury;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Jury findByProject(int idUser, int idDepartment, int semester, int year) throws Exception {
		ProjectBO bo = new ProjectBO();
		Project p = bo.findCurrentProject(idUser, idDepartment, semester, year);
		
		if(p == null){
			p = bo.findLastProject(idUser, idDepartment, semester, year);
		}
		
		if(p == null){
			throw new Exception("Não foi localizada a submissão do Projeto.");
		}else{
			Jury jury = new JuryDAO().findByProject(p.getIdProject());
			
			if((jury == null) || (jury.getIdJury() == 0)) {
				throw new Exception("A banca examinadora do projeto ainda não foi agendada.");
			}
			
			return jury;
		}
	}
	
	public Jury findByThesis(int idThesis) throws Exception {
		try {
			JuryDAO dao = new JuryDAO();
			Jury jury = dao.findByThesis(idThesis);
			
			if(jury == null) {
				SigetConfig config = new SigetConfigBO().findByDepartment(new ThesisBO().findIdDepartment(idThesis));
				
				if(config.isSupervisorJuryRequest()) {
					JuryRequest request = new JuryRequestBO().findByThesis(idThesis);
					
					if((request != null) && (request.getIdJuryRequest() != 0)) {
						request.setAppraisers(new JuryAppraiserRequestBO().listAppraisers(request.getIdJuryRequest()));
						
						jury = new Jury();
						
						jury.setThesis(new Thesis());
						jury.getThesis().setIdThesis(idThesis);
						jury.setAppraisers(new ArrayList<JuryAppraiser>());
						jury.setParticipants(new ArrayList<JuryStudent>());
						jury.setDate(request.getDate());
						jury.setLocal(request.getLocal());
						jury.setSupervisorAbsenceReason(request.getSupervisorAbsenceReason());
						jury.setJuryRequest(request);
						
						for(JuryAppraiserRequest a : request.getAppraisers()) {
							JuryAppraiser appraiser = new JuryAppraiser();
							
							appraiser.setAppraiser(a.getAppraiser());
							appraiser.setChair(a.isChair());
							appraiser.setSubstitute(a.isSubstitute());
							
							jury.getAppraisers().add(appraiser);
						}
					}
				}
			
				if(jury == null) {
					jury = new Jury();
					
					jury.setThesis(new Thesis());
					jury.getThesis().setIdThesis(idThesis);
					jury.setAppraisers(new ArrayList<JuryAppraiser>());
					jury.setParticipants(new ArrayList<JuryStudent>());
					
					int idJury = new ProjectBO().findIdJury(new ThesisBO().findIdProject(idThesis));
					if(idJury > 0) {
						List<JuryAppraiser> appraisers = new JuryAppraiserBO().listAppraisers(idJury);
						for(JuryAppraiser a : appraisers) {
							try {
								if(this.canAddAppraiser(jury, a.getAppraiser())) {
									JuryAppraiser ja = new JuryAppraiser();
									
									ja.setAppraiser(a.getAppraiser());
									ja.setChair(a.isChair());
									ja.setSubstitute(a.isSubstitute());
									
									jury.getAppraisers().add(ja);
								}
							} catch (Exception ex) { }
						}
					}
				}
				
				if(jury.getIdJury() == 0) {
					jury.setSupervisorAssignsGrades(config.isSupervisorAssignsGrades());
				}
			}
			
			return jury;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Jury findByThesis(int idUser, int idDepartment, int semester, int year) throws Exception {
		ThesisBO bo = new ThesisBO();
		Thesis thesis = bo.findCurrentThesis(idUser, idDepartment, semester, year);
		
		if(thesis == null){
			thesis = bo.findLastThesis(idUser, idDepartment, semester, year);
		}
		
		if(thesis == null){
			throw new Exception("Não foi localizada a submissão da Monografia.");
		}else{
			Jury jury = new JuryDAO().findByThesis(thesis.getIdThesis());
			
			if((jury == null) || (jury.getIdJury() == 0)) {
				throw new Exception("A banca examinadora da monografia ainda não foi agendada.");
			}
			
			return jury;
		}
	}
	
	public int save(int idUser, Jury jury) throws Exception{
		try {
			boolean insert = (jury.getIdJury() == 0);
			Jury oldJury = null;
			
			if(((jury.getProject() == null) || (jury.getProject().getIdProject() == 0)) && ((jury.getThesis() == null) || (jury.getThesis().getIdThesis() == 0))){
				throw new Exception("Informe o projeto ou monografia a que a banca pertence.");
			}
			if(jury.getLocal().isEmpty()){
				throw new Exception("Informe o local da banca.");
			}
			if(Document.hasSignature(DocumentType.JURY, jury.getIdJury())) {
				throw new Exception("A banca não pode ser alterada pois a ficha de avaliação já foi assinada.");
			}
			if(jury.getIdJury() == 0) {
				jury.setStartTime(jury.getDate());
				
				try {
					SigetConfig config = new SigetConfigBO().findByDepartment(((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)) ? new ThesisBO().findIdDepartment(jury.getThesis().getIdThesis()) : new ProjectBO().findIdDepartment(jury.getProject().getIdProject()));
					
					if((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)) {
						jury.setEndTime(DateUtils.addMinute(jury.getStartTime(), config.getJuryTimeStage2()));
					} else {
						jury.setEndTime(DateUtils.addMinute(jury.getStartTime(), config.getJuryTimeStage1()));
					}
				} catch (Exception e) {
					jury.setEndTime(DateUtils.addMinute(jury.getStartTime(), 60));	
				}
			}
			if(jury.getAppraisers() != null){
				User supervisor = jury.getSupervisor();
				User cosupervisor = jury.getCosupervisor();
				boolean find = false, findSupervisor = false, findCosupervisor = false;
				int countChair = 0, countMembers = 0, countSubstitutes = 0;
				
				for(JuryAppraiser appraiser : jury.getAppraisers()){
					if(new JuryAppraiserBO().appraiserHasJury(jury.getIdJury(), appraiser.getAppraiser().getIdUser(), jury.getDate())){
						throw new Exception("O membro " + appraiser.getAppraiser().getName() + " já tem uma banca marcada que conflita com este horário.");
					}
					
					if(appraiser.getAppraiser().getIdUser() == supervisor.getIdUser()) {
						findSupervisor = true;
					}
					
					if((cosupervisor != null) && (appraiser.getAppraiser().getIdUser() == cosupervisor.getIdUser())) {
						findCosupervisor = true;
					}
					
					if(appraiser.isChair() && ((appraiser.getAppraiser().getIdUser() == supervisor.getIdUser()) || ((cosupervisor != null) && (appraiser.getAppraiser().getIdUser() == cosupervisor.getIdUser())))){
						find = true;
					}
					
					if(appraiser.isSubstitute()) {
						appraiser.setChair(false);
					}
					
					if(appraiser.isSubstitute()) {
						countSubstitutes++;
					} else if(!appraiser.isChair()) {
						countMembers++;
					} else {
						countChair++;
					}
				}
				
				if(find) {
					jury.setSupervisorAbsenceReason("");
				} else if(jury.getSupervisorAbsenceReason().trim().isEmpty()) {
					throw new Exception("Informe o motivo do Professor Orientador ou o Coorientador não estar presidindo a banca.");
				}
				if(findSupervisor && findCosupervisor) {
					throw new Exception("O Coorientador não pode fazer parte da banca quando o orientador já compõe a mesma");
				}
				if(countChair == 0) {
					throw new Exception("É preciso indicar o presidente da banca.");
				} else if(countChair > 1) {
					throw new Exception("Apenas um membro pode ser presidente da banca.");
				}
				
				SigetConfig config = new SigetConfigBO().findByDepartment(((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)) ? new ThesisBO().findIdDepartment(jury.getThesis().getIdThesis()) : new ProjectBO().findIdDepartment(jury.getProject().getIdProject()));
				if(countMembers < config.getMinimumJuryMembers()) {
					throw new Exception("A banca deverá ser composta por, no mínimo, " + String.valueOf(config.getMinimumJuryMembers()) + " membros titulares (sem contar o presidente).");
				}
				if(countSubstitutes < config.getMinimumJurySubstitutes()) {
					throw new Exception("A banca deverá ser conter, no mínimo, " + String.valueOf(config.getMinimumJurySubstitutes()) + " suplente(s).");
				}
			}
			
			JuryDAO dao = new JuryDAO();
			
			if(!insert){
				oldJury = dao.findById(jury.getIdJury());
				oldJury.setAppraisers(new JuryAppraiserBO().listAppraisers(jury.getIdJury()));
			}
			
			int id = dao.save(idUser, jury);
			
			try{
				if(jury.getStage() == 2){
					jury.setThesis(new ThesisBO().findById(jury.getThesis().getIdThesis()));
				}else{
					jury.setProject(new ProjectBO().findById(jury.getProject().getIdProject()));
				}
				
				EmailMessageBO bo = new EmailMessageBO();
				List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
				
				keys.add(new EmailMessageEntry<String, String>("student", (jury.getStage() == 2 ? jury.getThesis().getStudent().getName() : jury.getProject().getStudent().getName())));
				keys.add(new EmailMessageEntry<String, String>("title", (jury.getStage() == 2 ? jury.getThesis().getTitle() : jury.getProject().getTitle())));
				keys.add(new EmailMessageEntry<String, String>("date", new SimpleDateFormat("dd/MM/yyyy").format(jury.getDate())));
				keys.add(new EmailMessageEntry<String, String>("time", new SimpleDateFormat("HH:mm").format(jury.getDate())));
				keys.add(new EmailMessageEntry<String, String>("local", jury.getLocal()));
				keys.add(new EmailMessageEntry<String, String>("stage", "TCC " + String.valueOf(jury.getStage())));
				keys.add(new EmailMessageEntry<String, String>("appraiser", jury.getSupervisor().getName()));
				
				if(insert){
					bo.sendEmail((jury.getStage() == 2 ? jury.getThesis().getStudent().getIdUser() : jury.getProject().getStudent().getIdUser()), MessageType.JURYINCLUDEDSTUDENT, keys);
					
					for(JuryAppraiser appraiser : jury.getAppraisers()){
						keys.remove(keys.size() - 1);
						keys.add(new EmailMessageEntry<String, String>("appraiser", appraiser.getAppraiser().getName()));
						bo.sendEmail(appraiser.getAppraiser().getIdUser(), MessageType.JURYINCLUDEDAPPRAISER, keys);
					}
				}else{
					boolean juryChanged = ((!jury.getDate().equals(oldJury.getDate())) || (!jury.getLocal().equals(oldJury.getLocal())));
					
					if(juryChanged){
						bo.sendEmail((jury.getStage() == 2 ? jury.getThesis().getStudent().getIdUser() : jury.getProject().getStudent().getIdUser()), MessageType.JURYCHANGEDSTUDENT, keys);
					}
					
					//Membro removido da banca
					for(JuryAppraiser a : oldJury.getAppraisers()){
						boolean find = false;
						
						for(JuryAppraiser a2 : jury.getAppraisers()){
							if(a.getAppraiser().getIdUser() == a2.getAppraiser().getIdUser()){
								find = true;
							}
						}
						
						if(!find){
							keys.remove(keys.size() - 1);
							keys.add(new EmailMessageEntry<String, String>("appraiser", a.getAppraiser().getName()));
							bo.sendEmail(a.getAppraiser().getIdUser(), MessageType.JURYREMOVEDAPPRAISER, keys);
						}
					}
					
					//Alterações de membros da banca
					for(JuryAppraiser a : jury.getAppraisers()){
						boolean find = false;
						
						for(JuryAppraiser a2 : oldJury.getAppraisers()){
							if(a.getAppraiser().getIdUser() == a2.getAppraiser().getIdUser()){
								find = true;
							}
						}
						
						keys.remove(keys.size() - 1);
						keys.add(new EmailMessageEntry<String, String>("appraiser", a.getAppraiser().getName()));
						
						if(!find){
							bo.sendEmail(a.getAppraiser().getIdUser(), MessageType.JURYINCLUDEDAPPRAISER, keys);
						}else if(juryChanged){
							bo.sendEmail(a.getAppraiser().getIdUser(), MessageType.JURYCHANGEDAPPRAISER, keys);	
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
	
	public void sendJuryFormSignedMessage(int idJury) throws Exception {
		Jury jury = this.findById(idJury);
		User manager = new UserBO().findManager(this.findIdDepartment(idJury), SystemModule.SIGET);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
		keys.add(new EmailMessageEntry<String, String>("student", jury.getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("supervisor", jury.getSupervisor().getName()));
		keys.add(new EmailMessageEntry<String, String>("title", jury.getTitle()));
		keys.add(new EmailMessageEntry<String, String>("stage", String.valueOf(jury.getStage())));
		
		new EmailMessageBO().sendEmail(manager.getIdUser(), MessageType.SIGNEDJURYREQUEST, keys);
	}
	
	public void sendRequestSignJuryFormMessage(int idJury, List<User> users) throws Exception {
		Jury jury = this.findById(idJury);
		
		for(User user : users) {
			List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
			
			keys.add(new EmailMessageEntry<String, String>("name", user.getName()));
			keys.add(new EmailMessageEntry<String, String>("student", jury.getStudent().getName()));
			keys.add(new EmailMessageEntry<String, String>("supervisor", jury.getSupervisor().getName()));
			keys.add(new EmailMessageEntry<String, String>("title", jury.getTitle()));
			keys.add(new EmailMessageEntry<String, String>("stage", String.valueOf(jury.getStage())));
			
			new EmailMessageBO().sendEmail(user.getIdUser(), MessageType.REQUESTSIGNJURYFORM, keys, false);
		}
	}
	
	public void sendRequestSupervisorSignJuryForm(int idJury) throws Exception {
		Jury jury = this.findById(idJury);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("student", jury.getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("supervisor", jury.getSupervisor().getName()));
		keys.add(new EmailMessageEntry<String, String>("title", jury.getTitle()));
		keys.add(new EmailMessageEntry<String, String>("stage", String.valueOf(jury.getStage())));
		
		new EmailMessageBO().sendEmail(jury.getSupervisor().getIdUser(), MessageType.REQUESTSUPERVISORSIGNJURYFORM, keys);
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
		if(jury.getIdJury() != 0){
			JuryAppraiserScoreBO bo = new JuryAppraiserScoreBO();
			if(bo.hasScore(jury.getIdJury(), appraiser.getIdUser())){
				throw new Exception("O membro já lançou as notas para esta banca e não pode ser removido.");
			}	
		}
		
		return true;
	}
	
	public byte[] getJuryForm(int idJury) throws Exception {
		if(Document.hasSignature(DocumentType.JURY, idJury)) {
			return Document.getSignedDocument(DocumentType.JURY, idJury);
		} else {
			br.edu.utfpr.dv.siacoes.report.dataset.v1.Jury dataset = SignDatasetBuilder.buildJury(this.getJuryFormReport(idJury));
			
			List<br.edu.utfpr.dv.siacoes.report.dataset.v1.Jury> list = new ArrayList<br.edu.utfpr.dv.siacoes.report.dataset.v1.Jury>();
			list.add(dataset);
			
			return new ReportUtils().createPdfStream(list, "JuryForm", this.findIdDepartment(idJury)).toByteArray();
		}
	}
	
	public JuryFormReport getJuryFormReport(int idJury) throws Exception{
		try{
			JuryFormReport report = new JuryFormReport();
			Jury jury = this.findById(idJury);
			Proposal proposal;
			
			report.setDate(jury.getDate());
			report.setLocal(jury.getLocal());
			
			if(jury.getSupervisorAbsenceReason().trim().isEmpty()) {
				report.setComments(jury.getComments());	
			} else {
				report.setComments("Motivo da ausência do Professor Orientador: " + jury.getSupervisorAbsenceReason() + "\n\n" + jury.getComments());
			}
			
			if((jury.getThesis() != null) && (jury.getThesis().getIdThesis() != 0)) {
				ThesisBO bo = new ThesisBO();
				Thesis thesis = bo.findById(jury.getThesis().getIdThesis());
				
				report.setTitle(thesis.getTitle());
				report.setIdStudent(thesis.getStudent().getIdUser());
				report.setStudent(thesis.getStudent().getName());
				report.setStage(2);
				
				proposal = new ProposalBO().findByThesis(jury.getThesis().getIdThesis());
			} else {
				ProjectBO bo = new ProjectBO();
				Project project = bo.findById(jury.getProject().getIdProject());
				
				report.setTitle(project.getTitle());
				report.setIdStudent(project.getStudent().getIdUser());
				report.setStudent(project.getStudent().getName());
				report.setStage(1);
				
				proposal = new ProposalBO().findByProject(jury.getProject().getIdProject());
			}
			
			SigetConfig config = new SigetConfigBO().findByDepartment(proposal.getDepartment().getIdDepartment());
			report.setRequestFinalDocumentStage1(config.isRequestFinalDocumentStage1());
			
			JuryAppraiserBO appraiserBO = new JuryAppraiserBO();
			List<JuryAppraiser> appraisers = appraiserBO.listAppraisers(idJury);
			int member = 1;
			
			for(JuryAppraiser appraiser : appraisers) {
				if(!appraiser.isSubstitute()) {
					JuryAppraiserScoreBO bo = new JuryAppraiserScoreBO();
					List<JuryAppraiserScore> list = bo.listScores(appraiser.getIdJuryAppraiser());
					JuryFormAppraiserReport appraiserReport = new JuryFormAppraiserReport();
					JuryFormAppraiserScoreReport scoreReport = new JuryFormAppraiserScoreReport();
					double scoreSum = 0, writingPonderosity = 0, oralPonderosity = 0, argumentationPonderosity = 0;
					
					appraiserReport.setIdUser(appraiser.getAppraiser().getIdUser());
					appraiserReport.setName(appraiser.getAppraiser().getName());
					appraiserReport.setComments(appraiser.getComments());
					appraiserReport.setDate(report.getDate());
					appraiserReport.setLocal(report.getLocal());
					appraiserReport.setStage(report.getStage());
					appraiserReport.setStudent(report.getStudent());
					appraiserReport.setTitle(report.getTitle());
					
					for(JuryAppraiserScore score : list) {
						JuryFormAppraiserDetailReport appraiserDetail = new JuryFormAppraiserDetailReport();
						
						appraiserDetail.setEvaluationItemType(score.getEvaluationItem().getType().toString());
						appraiserDetail.setEvaluationItem(score.getEvaluationItem().getDescription());
						appraiserDetail.setPonderosity(score.getEvaluationItem().getPonderosity());
						appraiserDetail.setScore(this.round(score.getScore()));
						appraiserDetail.setOrder(appraiserReport.getDetail().size() + 1);
						
						appraiserReport.getDetail().add(appraiserDetail);
						
						switch(score.getEvaluationItem().getType()){
							case WRITING:
								scoreReport.setScoreWriting(scoreReport.getScoreWriting() + score.getScore());
								writingPonderosity = writingPonderosity + score.getEvaluationItem().getPonderosity();
								break;
							case ORAL:
								scoreReport.setScoreOral(scoreReport.getScoreOral() + score.getScore());
								oralPonderosity = oralPonderosity + score.getEvaluationItem().getPonderosity();
								break;
							case ARGUMENTATION:
								scoreReport.setScoreArgumentation(scoreReport.getScoreArgumentation() + score.getScore());
								argumentationPonderosity = argumentationPonderosity + score.getEvaluationItem().getPonderosity();
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
					
					scoreReport.setScore(this.round(scoreSum));
					appraiserReport.setScore(scoreReport.getScore());
						
					if(appraiser.isChair()){
						appraiserReport.setDescription("Presidente");
					}else{
						appraiserReport.setDescription("Membro " + String.valueOf(member));
						member = member + 1;
					}
					
					scoreReport.setIdUser(appraiserReport.getIdUser());
					scoreReport.setName(appraiserReport.getName());
					scoreReport.setDescription(appraiserReport.getDescription());
					
					JuryFormAppraiserReport appraiserName = new JuryFormAppraiserReport();
					appraiserName.setDescription(appraiserReport.getDescription());
					appraiserName.setName(appraiserReport.getName());
					
					if(appraiser.isChair()){
						report.getAppraisersName().add(0, appraiserName);
						
						if(jury.isSupervisorAssignsGrades()) {
							report.getAppraisers().add(0, appraiserReport);
							report.getScores().add(0, scoreReport);
						}
					}else{
						report.getAppraisersName().add(appraiserName);
						report.getAppraisers().add(appraiserReport);
						report.getScores().add(scoreReport);
					}
				}
			}
			
			report.setScore(0);
			if(report.getScores().size() > 0){
				for(JuryFormAppraiserScoreReport score : report.getScores()){
					report.setScore(report.getScore() + score.getScore());
				}
				report.setScore(this.round(report.getScore() / report.getScores().size()));
			}
			
			report.setIdSupervisor(0);
			
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
	
	public byte[] getTermOfApproval(int idJury, boolean hideSignatures, boolean isManager) throws Exception{
		TermOfApprovalReport report = this.getTermOfApprovalReport(idJury, hideSignatures, isManager);
		
		List<TermOfApprovalReport> list = new ArrayList<TermOfApprovalReport>();
		list.add(report);
		
		return new ReportUtils().createPdfStream(list, "TermOfApproval", this.findIdDepartment(idJury)).toByteArray();
	}
	
	public boolean hasAllScores(int idJury) throws Exception {
		try {
			return new JuryDAO().hasAllScores(idJury);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean isApproved(int idJury) throws Exception {
		return new JuryDAO().isApproved(idJury);
	}
	
	public TermOfApprovalReport getTermOfApprovalReport(int idJury, boolean hideSignatures, boolean isManager) throws Exception{
		try{
			Jury jury = this.findById(idJury);
			
			if((jury.getThesis() == null) || (jury.getThesis().getIdThesis() == 0)){
				throw new Exception("O Termo de Aprovação só é emitido para a defesa de TCC 2.");
			}
			
			if(!isManager) {
				if(!this.hasAllScores(idJury)){
					throw new Exception("Para gerar o Termo de Aprovação é necessário que todas as notas sejam lançadas.");
				}
				
				if(!this.isApproved(idJury)){
					throw new Exception("Não é possível gerar o Termo de Aprovação pois o acadêmico não obteve a aprovação.");
				}
			}
			
			Thesis thesis = new ThesisBO().findById(jury.getThesis().getIdThesis());
			
			TermOfApprovalReport report = new TermOfApprovalReport();
			
			report.setTitle(thesis.getTitle());
			report.setStudent(thesis.getStudent().getName());
			report.setSupervisor(thesis.getSupervisor().getName());
			report.setDate(jury.getDate());
			report.setLocal(jury.getLocal());
			report.setStartTime(jury.getStartTime());
			report.setEndTime(jury.getEndTime());
			report.setHideSignatures(hideSignatures);
			
			FinalDocument doc = new FinalDocumentBO().findByThesis(thesis.getIdThesis());
			if((doc != null) && (doc.getIdFinalDocument() != 0)) {
				report.setTitle(doc.getTitle());
			}
			
			JuryAppraiserBO bo2 = new JuryAppraiserBO();
			List<JuryAppraiser> list = bo2.listAppraisers(idJury);
			
			for(JuryAppraiser appraiser : list){
				if(appraiser.isChair()){
					report.setSupervisor(appraiser.getAppraiser().getName());
				} else {
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
	
	public byte[] getScheduleReport(int idDepartment, int idUser, int semester, int year, int stage) throws Exception {
		List<CalendarReport> report = this.getScheduleReportList(idDepartment, idUser, semester, year, stage);
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(report, "Calendar", idDepartment);
		return rep.toByteArray();
	}
	
	public List<CalendarReport> getScheduleReportList(int idDepartment, int idUser, int semester, int year, int stage) throws Exception{
		List<Jury> list;
		List<CalendarReport> report = new ArrayList<CalendarReport>();
		
		if(idUser == 0){
			list = this.listBySemester(idDepartment, semester, year, stage);
		}else{
			UserBO bo = new UserBO();
			User user = bo.findById(idUser);
			
			if(user.hasProfile(UserProfile.PROFESSOR)){
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
					c.setAppraisers(appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")));
				}else{
					c.setAppraisers(c.getAppraisers() + "\n" + appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")));	
				}
			}
			
			report.add(c);
		}
		
		return report;
	}
	
	public boolean hasScores(int idJury) throws Exception{
		JuryDAO dao = new JuryDAO();
		
		return dao.hasScores(idJury);
	}
	
	public List<JuryBySemester> listJuryBySemester(int idDepartment, int initialYear, int finalYear) throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			List<JuryBySemester> list = dao.listJuryBySemester(idDepartment, initialYear, finalYear);
			
			int index = 0;
			
			for(int year = initialYear; year <= finalYear; year++) {
				for(int semester = 1; semester <= 2; semester++) {
					boolean found = false;
					
					for(JuryBySemester jury : list) {
						if((jury.getYear() == year) && (jury.getSemester() == semester)) {
							found = true;
						}
					}
					
					if(!found) {
						JuryBySemester jury = new JuryBySemester();
						
						jury.setYear(year);
						jury.setSemester(semester);
						jury.setJuryStage1(0);
						jury.setJuryStage2(0);
						
						list.add(index, jury);
					}
					
					index++;
				}
			}
			
			return list;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public long getTotalJury() throws Exception{
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.getTotalJury();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryStudentReport> listJuryStudentReport(int idDepartment, int idJury, int semester, int year, boolean orderByDate) throws Exception {
		try {
			JuryDAO dao = new JuryDAO();
			
			return dao.listJuryStudentReport(idDepartment, idJury, semester, year, orderByDate);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public byte[] getJuryStudentReport(int idDepartment, int idJury, int semester, int year, boolean groupByJury) throws Exception {
		List<JuryStudentReport> list = this.listJuryStudentReport(idDepartment, idJury, semester, year, groupByJury);
		
		ByteArrayOutputStream report;
		
		if(groupByJury) {
			report = new ReportUtils().createPdfStream(list, "JuryParticipantsListGrouped", idDepartment);
		} else {
			report = new ReportUtils().createPdfStream(list, "JuryParticipantsList", idDepartment);
		}
		
		return report.toByteArray();
	}
	
	public byte[] getJuryParticipantsSignature(int idJury) throws Exception {
		JuryFormReport report = this.getJuryFormReport(idJury);
		
		List<JuryFormReport> list = new ArrayList<JuryFormReport>();
		list.add(report);
		
		return new ReportUtils().createPdfStream(list, "JuryParticipants", this.findIdDepartment(idJury)).toByteArray();
	}
	
	public byte[] getJuryGradesReport(int idDepartment, int semester, int year, int stage, boolean listAll) throws Exception {
		List<JuryGrade> list = this.getJuryGradesReportList(idDepartment, semester, year, stage, listAll);
		
		return new ReportUtils().createPdfStream(list, "JuryGrades", idDepartment).toByteArray();
	}
	
	public List<JuryGrade> getJuryGradesReportList(int idDepartment, int semester, int year, int stage, boolean listAll) throws Exception {
		SigetConfig config = new SigetConfigBO().findByDepartment(idDepartment);
		List<JuryGrade> report = new ArrayList<JuryGrade>();
		
		if(listAll) {
			if((stage == 0) || (stage == 1)) {
				List<Proposal> list1 = new ProposalBO().listBySemester(idDepartment, semester, year);
				
				for(Proposal proposal : list1) {
					JuryGrade grade = new JuryGrade();
					
					grade.setStudent(proposal.getStudent().getName());
					grade.setStage(1);
					grade.setJuryDate(null);
					
					Project project = new ProjectBO().findByProposal(proposal.getIdProposal());
					
					if((project != null) && (project.getIdProject() != 0)) {
						Jury jury = this.findByProject(project.getIdProject());
						
						if((jury != null) && (jury.getIdJury() != 0)) {
							grade.setJuryDate(jury.getDate());
							
							JuryFormReport form = this.getJuryFormReport(jury.getIdJury());
							grade.setScore(form.getScore());
							
							if(grade.getScore() >= config.getMinimumScore()) {
								grade.setResult(JuryResult.APPROVED);
							} else {
								grade.setResult(JuryResult.DISAPPROVED);
							}
						}
						
						if(config.isRequestFinalDocumentStage1()) {
							FinalDocument doc = new FinalDocumentBO().findByProject(project.getIdProject());
							if((doc != null) && (doc.getIdFinalDocument() != 0)) {
								grade.setSupervisorFeedback(doc.getSupervisorFeedback());
							}
							
							if(grade.getSupervisorFeedback() == DocumentFeedback.DISAPPROVED) {
								grade.setResult(JuryResult.DISAPPROVED);
							} else if(grade.getSupervisorFeedback() == DocumentFeedback.NONE) {
								grade.setResult(JuryResult.NONE);
							}
						}
					}
					
					report.add(grade);
				}
			}
			
			if((stage == 0) || (stage == 2)) {
				List<Thesis> list2 = new ThesisBO().listBySemester(idDepartment, semester, year);
				
				for(Thesis thesis : list2) {
					JuryGrade grade = new JuryGrade();
					
					grade.setStudent(thesis.getStudent().getName());
					grade.setStage(2);
					grade.setJuryDate(null);
					
					Jury jury = this.findByThesis(thesis.getIdThesis());
					
					if((jury != null) && (jury.getIdJury() != 0)) {
						grade.setJuryDate(jury.getDate());
						
						JuryFormReport form = this.getJuryFormReport(jury.getIdJury());
						grade.setScore(form.getScore());
						
						if(grade.getScore() >= config.getMinimumScore()) {
							grade.setResult(JuryResult.APPROVED);
						} else {
							grade.setResult(JuryResult.DISAPPROVED);
						}
					}
					
					FinalDocument doc = new FinalDocumentBO().findByThesis(thesis.getIdThesis());
					if((doc != null) && (doc.getIdFinalDocument() != 0)) {
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
		} else {
			List<Jury> list = this.listBySemester(idDepartment, semester, year, stage);
			
			for(Jury jury : list) {
				if((stage == 0) || (stage == jury.getStage())) {
					JuryGrade grade = new JuryGrade();
					
					grade.setStudent(jury.getStudent().getName());
					grade.setJuryDate(jury.getDate());
					grade.setStage(jury.getStage());
					
					JuryFormReport form = this.getJuryFormReport(jury.getIdJury());
					grade.setScore(form.getScore());
					
					if(grade.getScore() >= config.getMinimumScore()) {
						grade.setResult(JuryResult.APPROVED);
					} else {
						grade.setResult(JuryResult.DISAPPROVED);
					}
					
					if(jury.getStage() == 2) {
						FinalDocument doc = new FinalDocumentBO().findByThesis(jury.getThesis().getIdThesis());
						if((doc != null) && (doc.getIdFinalDocument() != 0)) {
							grade.setSupervisorFeedback(doc.getSupervisorFeedback());
						}
					} else if(config.isRequestFinalDocumentStage1()) {
						FinalDocument doc = new FinalDocumentBO().findByProject(jury.getProject().getIdProject());
						if((doc != null) && (doc.getIdFinalDocument() != 0)) {
							grade.setSupervisorFeedback(doc.getSupervisorFeedback());
						}
					}
					
					if((jury.getStage() == 2) || config.isRequestFinalDocumentStage1()) {
						if(grade.getSupervisorFeedback() == DocumentFeedback.DISAPPROVED) {
							grade.setResult(JuryResult.DISAPPROVED);
						} else if(grade.getSupervisorFeedback() == DocumentFeedback.NONE) {
							grade.setResult(JuryResult.NONE);
						}
					}
					
					report.add(grade);
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		Comparator<JuryGrade> comparator = new BeanComparator("student");
		Collections.sort(report, comparator);
		
		return report;
	}
	
}
