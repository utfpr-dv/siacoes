package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipJuryRequestDAO;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryRequest;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

public class InternshipJuryRequestBO {

	public List<InternshipJuryRequest> listBySemester(int idDepartment, int semester, int year) throws Exception{
		try {
			InternshipJuryRequestDAO dao = new InternshipJuryRequestDAO();
			
			return dao.listBySemester(idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipJuryRequest> listByAppraiser(int idUser, int semester, int year) throws Exception{
		try {
			InternshipJuryRequestDAO dao = new InternshipJuryRequestDAO();
			
			return dao.listByAppraiser(idUser, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipJuryRequest findById(int id) throws Exception{
		try {
			InternshipJuryRequestDAO dao = new InternshipJuryRequestDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipJuryRequest findByInternship(int idInternship) throws Exception{
		try {
			InternshipJuryRequestDAO dao = new InternshipJuryRequestDAO();
			
			return dao.findByInternship(idInternship);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdDepartment(int idInternshipJuryRequest) throws Exception{
		try {
			InternshipJuryRequestDAO dao = new InternshipJuryRequestDAO();
			
			return dao.findIdDepartment(idInternshipJuryRequest);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, InternshipJuryRequest jury) throws Exception{
		try {
			if((jury.getInternship() == null) || (jury.getInternship().getIdInternship() == 0)){
				throw new Exception("Informe o estágio a que a banca pertence.");
			}
			if(jury.getLocal().isEmpty()){
				throw new Exception("Informe o local da banca.");
			}
			if((jury.getDate() == null) || jury.getDate().before(DateUtils.getNow().getTime())) {
				throw new Exception("A data e horário da banca não pode ser inferior à data e horário atual.");
			}
			if((jury.getInternshipJury() != null) && (jury.getInternshipJury().getIdInternshipJury() != 0)) {
				throw new Exception("A banca já foi confirmada e a solicitação não pode ser alterada.");
			}
			if(Document.hasSignature(DocumentType.INTERNSHIPJURYREQUEST, jury.getIdInternshipJuryRequest())) {
				throw new Exception("A solicitação não pode ser alterada pois ela já foi assinada.");
			}
			if(jury.getAppraisers() != null){
				int countChair = 0, countMembers = 0, countSubstitutes = 0;
				
				for(InternshipJuryAppraiserRequest appraiser : jury.getAppraisers()){
					if(new InternshipJuryAppraiserRequestBO().appraiserHasInternshipJuryRequest(jury.getIdInternshipJuryRequest(), appraiser.getAppraiser().getIdUser(), jury.getDate()) || new InternshipJuryAppraiserBO().appraiserHasJury(0, appraiser.getAppraiser().getIdUser(), jury.getDate())){
						throw new Exception("O membro " + appraiser.getAppraiser().getName() + " já tem uma banca marcada que conflita com este horário.");
					}
					
					if(appraiser.isSubstitute()) {
						countSubstitutes++;
					} else if(!appraiser.isChair()) {
						countMembers++;
					} else {
						countChair++;
					}
				}
				
				if(countChair == 0) {
					throw new Exception("É preciso indicar o presidente da banca.");
				} else if(countChair > 1) {
					throw new Exception("Apenas um membro pode ser presidente da banca.");
				}
				
				SigesConfig config = new SigesConfigBO().findByDepartment(new InternshipBO().findIdDepartment(jury.getInternship().getIdInternship()));
				if(countMembers < config.getMinimumJuryMembers()) {
					throw new Exception("A banca deverá ser composta por, no mínimo, " + String.valueOf(config.getMinimumJuryMembers()) + " membros titulares (sem contar o presidente).");
				}
				if(countSubstitutes < config.getMinimumJurySubstitutes()) {
					throw new Exception("A banca deverá ser conter, no mínimo, " + String.valueOf(config.getMinimumJurySubstitutes()) + " suplente(s).");
				}
			}
			
			InternshipJuryRequestDAO dao = new InternshipJuryRequestDAO();
			
			return  dao.save(idUser, jury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void sendRequestSignedMessage(int idInternshipJuryRequest) throws Exception {
		InternshipJuryRequest request = this.findById(idInternshipJuryRequest);
		request.setInternship(new InternshipBO().findById(request.getInternship().getIdInternship()));
		User manager = new UserBO().findManager(request.getInternship().getDepartment().getIdDepartment(), SystemModule.SIGET);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
		keys.add(new EmailMessageEntry<String, String>("student", request.getInternship().getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("supervisor", request.getInternship().getSupervisor().getName()));
		keys.add(new EmailMessageEntry<String, String>("company", request.getInternship().getCompany().getName()));
		
		new EmailMessageBO().sendEmail(manager.getIdUser(), MessageType.SIGNEDINTERNSHIPJURYREQUEST, keys);
	}
	
	public void sendRequestSignJuryRequestMessage(int idInternshipJuryRequest, List<User> users) throws Exception {
		InternshipJuryRequest request = this.findById(idInternshipJuryRequest);
		request.setInternship(new InternshipBO().findById(request.getInternship().getIdInternship()));
		
		for(User user : users) {
			List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
			
			keys.add(new EmailMessageEntry<String, String>("name", user.getName()));
			keys.add(new EmailMessageEntry<String, String>("student", request.getInternship().getStudent().getName()));
			keys.add(new EmailMessageEntry<String, String>("supervisor", request.getInternship().getSupervisor().getName()));
			keys.add(new EmailMessageEntry<String, String>("company", request.getInternship().getCompany().getName()));
			
			new EmailMessageBO().sendEmail(user.getIdUser(), MessageType.REQUESTSIGNINTERNSHIPJURYREQUEST, keys, false);
		}
	}
	
	public boolean delete(int idUser, int id) throws Exception {
		InternshipJuryRequest request = this.findById(id);
		
		return this.delete(idUser, request);
	}
	
	private boolean delete(int idUser, InternshipJuryRequest request) throws Exception {
		if((request.getInternshipJury() != null) && (request.getInternshipJury().getIdInternshipJury() != 0)) {
			throw new Exception("A solicitação não pode ser excluída pois a banca já foi confirmada.");
		}
		if(Document.hasSignature(DocumentType.INTERNSHIPJURYREQUEST, request.getIdInternshipJuryRequest())) {
			throw new Exception("A solicitação não pode ser excluída pois ela já foi assinada.");
		}
		
		try {
			return new InternshipJuryRequestDAO().delete(idUser, request.getIdInternshipJuryRequest());
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public boolean canAddAppraiser(InternshipJuryRequest jury, User appraiser) throws Exception{
		if(jury.getAppraisers() != null){
			for(InternshipJuryAppraiserRequest ja : jury.getAppraisers()){
				if(ja.getAppraiser().getIdUser() == appraiser.getIdUser()){
					throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
				}
			}
		}else if(jury.getIdInternshipJuryRequest() != 0){
			InternshipJuryAppraiserRequestBO bo = new InternshipJuryAppraiserRequestBO();
			InternshipJuryAppraiserRequest ja = bo.findByAppraiser(jury.getIdInternshipJuryRequest(), appraiser.getIdUser());
			
			if(ja != null){
				throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
			}
		}
		
		return true;
	}
	
	public InternshipJuryRequest prepareInternshipJuryRequest(int idInternship) throws Exception {
		InternshipJuryRequest jury = this.findByInternship(idInternship);
		
		if((jury != null) && (jury.getIdInternshipJuryRequest() != 0)) {
			return jury;
		}
		
		jury = new InternshipJuryRequest();
		
		jury.setAppraisers(new ArrayList<InternshipJuryAppraiserRequest>());
		
		Internship internship = new InternshipBO().findById(idInternship);
		jury.setInternship(internship);
		
		InternshipJuryAppraiserRequest chair = new InternshipJuryAppraiserRequest();
		
		chair.setAppraiser(internship.getSupervisor());
		chair.setSubstitute(false);
		chair.setChair(true);
		
		jury.getAppraisers().add(chair);
		
		return jury;
	}
	
	public byte[] getInternshipJuryRequestForm(int idInternshipJuryRequest) throws Exception {
		if(Document.hasSignature(DocumentType.INTERNSHIPJURYREQUEST, idInternshipJuryRequest)) {
			return Document.getSignedDocument(DocumentType.INTERNSHIPJURYREQUEST, idInternshipJuryRequest);
		} else {
			br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipJuryRequest dataset = SignDatasetBuilder.buildRequest(this.getInternshipJuryRequestFormReport(idInternshipJuryRequest));
			
			List<br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipJuryRequest> list = new ArrayList<br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipJuryRequest>();
			list.add(dataset);
			
			return new ReportUtils().createPdfStream(list, "InternshipJuryFormRequest", this.findIdDepartment(idInternshipJuryRequest)).toByteArray();
		}
	}
	
	public InternshipJuryFormReport getInternshipJuryRequestFormReport(int idInternshipJuryRequest) throws Exception {
		try {
			InternshipJuryFormReport report = new InternshipJuryFormReport();
			InternshipJuryRequest jury = this.findById(idInternshipJuryRequest);
			
			if((jury == null) || (jury.getIdInternshipJuryRequest() == 0)) {
				throw new Exception("A solicitação de agendamento de banca ainda não foi preenchida.");
			}
			
			report.setDate(jury.getDate());
			report.setLocal(jury.getLocal());
			report.setSupervisor(jury.getInternship().getSupervisor().getName());
			report.setIdSupervisor(jury.getInternship().getSupervisor().getIdUser());
			report.setStudent(jury.getInternship().getStudent().getName());
			report.setIdStudent(jury.getInternship().getStudent().getIdUser());
			report.setCompany(jury.getInternship().getCompany().getName());
			report.setComments(jury.getComments());	
			
			jury.setAppraisers(new InternshipJuryAppraiserRequestBO().listAppraisers(idInternshipJuryRequest));
			int member = 1, substitute = 1;
			
			for(InternshipJuryAppraiserRequest appraiser : jury.getAppraisers()) {
				JuryFormAppraiserReport appraiserReport = new JuryFormAppraiserReport();
				
				appraiserReport.setIdUser(appraiser.getAppraiser().getIdUser());
				appraiserReport.setName(appraiser.getAppraiser().getName());
				appraiserReport.setDate(report.getDate());
				appraiserReport.setStudent(report.getStudent());
				appraiserReport.setCompany(report.getCompany());
				
				if(appraiser.isChair()) {
					appraiserReport.setDescription("Presidente");
				} else if(appraiser.isSubstitute()) {
					appraiserReport.setDescription("Suplente " + String.valueOf(substitute));
					substitute = substitute + 1;
				} else {
					appraiserReport.setDescription("Membro " + String.valueOf(member));
					member = member + 1;
				}
				
				if(appraiser.isChair()) {
					report.getAppraisers().add(0, appraiserReport);
				} else {
					report.getAppraisers().add(appraiserReport);
				}
			}
			
			return report;
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
