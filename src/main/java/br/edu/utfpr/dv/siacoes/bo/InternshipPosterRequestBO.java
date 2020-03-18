package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipPosterRequestDAO;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequestForm;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;

public class InternshipPosterRequestBO {
	
	public List<InternshipPosterRequest> listBySemester(int idDepartment, int semester, int year) throws Exception {
		try {
			Semester s = new SemesterBO().findBySemester(new DepartmentBO().findById(idDepartment).getCampus().getIdCampus(), semester, year);
			
			return new InternshipPosterRequestDAO().listByDate(idDepartment, DateUtils.getDayBegin(s.getStartDate()), DateUtils.getDayEnd(s.getEndDate()));
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipPosterRequest> listByAppraiser(int idDepartment, int idUser, int semester, int year) throws Exception {
		try {
			Semester s = new SemesterBO().findBySemester(new DepartmentBO().findById(idDepartment).getCampus().getIdCampus(), semester, year);
			
			return new InternshipPosterRequestDAO().listByAppraiser(idUser, DateUtils.getDayBegin(s.getStartDate()), DateUtils.getDayEnd(s.getEndDate()));
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipPosterRequest findById(int idInternshipPosterRequest) throws Exception {
		try {
			InternshipPosterRequestDAO dao = new InternshipPosterRequestDAO();
			
			return dao.findById(idInternshipPosterRequest);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdDepartment(int idInternshipPosterRequest) throws Exception {
		try {
			InternshipPosterRequestDAO dao = new InternshipPosterRequestDAO();
			
			return dao.findIdDepartment(idInternshipPosterRequest);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipPosterRequest findByInternship(int idInternship) throws Exception {
		try {
			InternshipPosterRequestDAO dao = new InternshipPosterRequestDAO();
			
			return dao.findByInternship(idInternship);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, InternshipPosterRequest request) throws Exception {
		try {
			if((request.getInternship() == null) || (request.getInternship().getIdInternship() == 0)) {
				throw new Exception("Informe o estágio a que a banca pertence.");
			}
			
			if(request.getAppraisers() != null) {
				int countMembers = 0, countSubstitutes = 0;
				
				for(InternshipPosterAppraiserRequest appraiser : request.getAppraisers()) {
					if(appraiser.isSubstitute()) {
						countSubstitutes++;
					} else {
						countMembers++;
					}
				}
				
				SigesConfig config = new SigesConfigBO().findByDepartment(new InternshipBO().findIdDepartment(request.getInternship().getIdInternship()));
				if(countMembers < config.getMinimumJuryMembers()) {
					throw new Exception("A banca deverá ser composta por, no mínimo, " + String.valueOf(config.getMinimumJuryMembers()) + " membros titulares.");
				}
				if(countSubstitutes < config.getMinimumJurySubstitutes()) {
					throw new Exception("A banca deverá ser conter, no mínimo, " + String.valueOf(config.getMinimumJurySubstitutes()) + " suplente(s).");
				}
			}
			
			if(Document.hasSignature(DocumentType.INTERNSHIPPOSTERREQUEST, request.getIdInternshipPosterRequest())) {
				throw new Exception("A solicitação não pode ser alterada pois ela já foi assinada.");
			}
			
			request.setRequestDate(DateUtils.getNow().getTime());
			
			return new InternshipPosterRequestDAO().save(idUser, request);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public void sendRequestSignedMessage(int idInternshipPosterRequest) throws Exception {
		InternshipPosterRequest request = new InternshipPosterRequestBO().findById(idInternshipPosterRequest);
		request.setInternship(new InternshipBO().findById(request.getInternship().getIdInternship()));
		User manager = new UserBO().findManager(request.getInternship().getDepartment().getIdDepartment(), SystemModule.SIGES);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
		keys.add(new EmailMessageEntry<String, String>("student", request.getInternship().getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("supervisor", request.getInternship().getSupervisor().getName()));
		keys.add(new EmailMessageEntry<String, String>("company", request.getInternship().getCompany().getName()));
		
		new EmailMessageBO().sendEmail(manager.getIdUser(), MessageType.SIGNEDINTERNSHIPPOSTERREQUEST, keys);
	}
	
	public void sendRequestSignInternshipPosterRequestMessage(int idInternshipPosterRequest, List<User> users) throws Exception {
		InternshipPosterRequest request = new InternshipPosterRequestBO().findById(idInternshipPosterRequest);
		request.setInternship(new InternshipBO().findById(request.getInternship().getIdInternship()));
		
		for(User user : users) {
			List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
			
			keys.add(new EmailMessageEntry<String, String>("name", user.getName()));
			keys.add(new EmailMessageEntry<String, String>("student", request.getInternship().getStudent().getName()));
			keys.add(new EmailMessageEntry<String, String>("supervisor", request.getInternship().getSupervisor().getName()));
			keys.add(new EmailMessageEntry<String, String>("company", request.getInternship().getCompany().getName()));
			
			new EmailMessageBO().sendEmail(user.getIdUser(), MessageType.REQUESTSIGNINTERNSHIPPOSTERREQUEST, keys, false);
		}
	}
	
	public boolean delete(int idUser, int id) throws Exception {
		InternshipPosterRequest request = this.findById(id);
		
		return this.delete(idUser, request);
	}
	
	private boolean delete(int idUser, InternshipPosterRequest request) throws Exception {
		if((request.getJury() != null) && (request.getJury().getIdInternshipJury() != 0)) {
			throw new Exception("A solicitação não pode ser excluída pois a banca já foi confirmada.");
		}
		if(Document.hasSignature(DocumentType.INTERNSHIPPOSTERREQUEST, request.getIdInternshipPosterRequest())) {
			throw new Exception("A solicitação não pode ser excluída pois ela já foi assinada.");
		}
		
		try {
			return new InternshipPosterRequestDAO().delete(idUser, request.getIdInternshipPosterRequest());
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public boolean canAddAppraiser(InternshipPosterRequest request, User appraiser) throws Exception {
		if(request.getAppraisers() != null) {
			for(InternshipPosterAppraiserRequest a : request.getAppraisers()) {
				if(a.getAppraiser().getIdUser() == appraiser.getIdUser()) {
					throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
				}
			}
		} else if(request.getIdInternshipPosterRequest() != 0) {
			InternshipPosterAppraiserRequest a = new InternshipPosterAppraiserRequestBO().findByAppraiser(request.getIdInternshipPosterRequest(), appraiser.getIdUser());
			
			if(a.getAppraiser().getIdUser() == appraiser.getIdUser()) {
				throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
			}
		}
		
		Internship internship = new InternshipBO().findById(request.getInternship().getIdInternship());
		if(internship.getSupervisor().getIdUser() == appraiser.getIdUser()) {
			throw new Exception("O Professor Orientador já faz parte da banca.");
		}
		
		return true;
	}
	
	public InternshipPosterRequest preparePosterRequest(int idInternship) throws Exception {
		InternshipPosterRequest request = this.findByInternship(idInternship);
		
		if((request == null) || (request.getIdInternshipPosterRequest() == 0)) {
			request = new InternshipPosterRequest();
			
			request.setInternship(new InternshipBO().findById(idInternship));
			
			if(request.getInternship().getType() != InternshipType.REQUIRED) {
				throw new Exception("A solicitação de banca só pode ser efetuada para o estágio obrigatório.");
			}
		}
		
		return request;
	}
	
	public byte[] getPosterRequestForm(int idInternshipPosterRequest) throws Exception {
		if(Document.hasSignature(DocumentType.INTERNSHIPPOSTERREQUEST, idInternshipPosterRequest)) {
			return Document.getSignedDocument(DocumentType.INTERNSHIPPOSTERREQUEST, idInternshipPosterRequest);
		} else {
			br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipPosterRequest dataset = SignDatasetBuilder.build(this.getPosterRequestFormReport(idInternshipPosterRequest));
			
			List<br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipPosterRequest> list = new ArrayList<br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipPosterRequest>();
			list.add(dataset);
			
			return new ReportUtils().createPdfStream(list, "InternshipPosterRequest", this.findIdDepartment(idInternshipPosterRequest)).toByteArray();
		}
	}
	
	public InternshipPosterRequestForm getPosterRequestFormReport(int idInternshipPosterRequest) throws Exception {
		try {
			InternshipPosterRequestForm report = new InternshipPosterRequestForm();
			InternshipPosterRequest request = this.findById(idInternshipPosterRequest);
			
			if((request == null) || (request.getIdInternshipPosterRequest() == 0)) {
				throw new Exception("A solicitação de banca ainda não foi preenchida.");
			}
			
			report.setArticles("os Capítulos VI e VII");
			report.setStudent(request.getInternship().getStudent());
			report.setSupervisor(request.getInternship().getSupervisor());
			report.setAppraisers(request.getAppraisers());
			report.setDepartment(new DepartmentBO().findById(request.getInternship().getDepartment().getIdDepartment()));
			report.getDepartment().setCampus(new CampusBO().findByDepartment(request.getInternship().getDepartment().getIdDepartment()));
			report.setManager(new UserBO().findManager(request.getInternship().getDepartment().getIdDepartment(), SystemModule.SIGES));
			report.setAppraisers(new InternshipPosterAppraiserRequestBO().listAppraisers(idInternshipPosterRequest));
			
			return report;
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
