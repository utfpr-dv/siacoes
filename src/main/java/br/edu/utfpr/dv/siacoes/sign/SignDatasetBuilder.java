package br.edu.utfpr.dv.siacoes.sign;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequestForm;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserScoreReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.Attendance;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipJury;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.Jury;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.JuryRequest;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.SupervisorAgreement;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.SupervisorChange;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class SignDatasetBuilder {
	
	public static SupervisorAgreement build(Proposal proposal) {
		SupervisorAgreement dataset = new SupervisorAgreement();
		
		dataset.setDate(DateUtils.getNow().getTime());
		dataset.setTitle(proposal.getTitle());
		dataset.setStudent(proposal.getStudent().getName());
		dataset.setSupervisor(proposal.getSupervisor().getName());
		dataset.setFeedback(proposal.getSupervisorFeedback());
		dataset.setComments(proposal.getSupervisorComments());
		dataset.addSignature(proposal.getSupervisor().getIdUser(), proposal.getSupervisor().getName());
		
		return dataset;
	}
	
	public static ProposalFeedback build(ProposalAppraiser appraiser) {
		ProposalFeedback dataset = new ProposalFeedback();
		
		dataset.setDate(DateUtils.getNow().getTime());
		dataset.setTitle(appraiser.getProposal().getTitle());
		dataset.setStudent(appraiser.getProposal().getStudent().getName());
		dataset.setSupervisor(appraiser.getProposal().getSupervisor().getName());
		dataset.setAppraiser(appraiser.getAppraiser().getName());
		dataset.setInstitution(appraiser.getAppraiser().getInstitution());
		dataset.setArea(appraiser.getAppraiser().getArea());
		dataset.setFeedback(appraiser.getFeedback());
		dataset.setComments(appraiser.getComments());
		dataset.addSignature(appraiser.getAppraiser().getIdUser(), appraiser.getAppraiser().getName());
		
		return dataset;
	}
	
	public static JuryRequest build(JuryFormReport jury) {
		JuryRequest dataset = new JuryRequest();
		
		dataset.setStage(jury.getStage());
		dataset.setTitle(jury.getTitle());
		dataset.setDate(jury.getDate());
		dataset.setLocal(jury.getLocal());
		dataset.setIdStudent(jury.getIdStudent());
		dataset.setIdSupervisor(jury.getIdSupervisor());
		dataset.setComments(jury.getComments());
		
		boolean findSupervisor = false;
		
		for(JuryFormAppraiserReport appraiser : jury.getAppraisers()) {
			dataset.addAppraiser(appraiser.getIdUser(), appraiser.getDescription());
			dataset.addSignature(appraiser.getIdUser(), appraiser.getName());
			
			if(appraiser.getIdUser() == jury.getIdSupervisor()) {
				findSupervisor = true;
			}
		}
		
		dataset.addSignature(jury.getIdStudent(), jury.getStudent());
		
		if(!findSupervisor) {
			dataset.addSignature(jury.getIdSupervisor(), jury.getSupervisor());
		}
		
		return dataset;
	}
	
	public static List<User> getSignaturesList(JuryFormReport jury) {
		List<User> users = new ArrayList<User>();
		boolean findSupervisor = false;
		
		for(JuryFormAppraiserReport appraiser : jury.getAppraisers()) {
			User u = new User();
			
			u.setIdUser(appraiser.getIdUser());
			u.setName(appraiser.getName());
			
			users.add(u);
			
			if(appraiser.getIdUser() == jury.getIdSupervisor()) {
				findSupervisor = true;
			}
		}
		
		if(!findSupervisor && (jury.getIdSupervisor() != 0)) {
			User supervisor = new User();
			
			supervisor.setIdUser(jury.getIdSupervisor());
			supervisor.setName(jury.getSupervisor());
			
			users.add(supervisor);
		}
		
		User student = new User();
		
		student.setIdUser(jury.getIdStudent());
		student.setName(jury.getStudent());
		
		users.add(student);
		
		return users;
	}
	
	public static SupervisorChange build(br.edu.utfpr.dv.siacoes.model.SupervisorChange supervisorChange) {
		SupervisorChange dataset = new SupervisorChange();
		
		dataset.setSupervisorRequest(supervisorChange.isSupervisorRequest());
		dataset.setOldSupervisor(supervisorChange.getOldSupervisor().getName());
		dataset.setStudent(supervisorChange.getProposal().getStudent().getName());
		dataset.setStudentCode(supervisorChange.getProposal().getStudent().getStudentCode());
		dataset.setStage(supervisorChange.getStage());
		dataset.setTitle(supervisorChange.getProposal().getTitle());
		dataset.setNewSupervisor(supervisorChange.getNewSupervisor().getName());
		dataset.setComments(supervisorChange.getComments());
		dataset.setDate(supervisorChange.getDate());
		
		if(supervisorChange.isSupervisorRequest()) {
			dataset.addSignature(supervisorChange.getOldSupervisor().getIdUser(), supervisorChange.getOldSupervisor().getName());
		} else {
			dataset.addSignature(supervisorChange.getProposal().getStudent().getIdUser(), supervisorChange.getProposal().getStudent().getName());
		}
		
		return dataset;
	}
	
	public static Attendance build(AttendanceReport attendance) {
		Attendance dataset = new Attendance();
		
		dataset.setIdGroup(attendance.getIdGroup());
		dataset.setStage(attendance.getStage());
		dataset.setTitle(attendance.getTitle());
		dataset.setIdStudent(attendance.getIdStudent());
		dataset.setIdSupervisor(attendance.getIdSupervisor());
		
		for(br.edu.utfpr.dv.siacoes.model.Attendance a : attendance.getAttendances()) {
			dataset.getAttendances().add(a);
		}
		
		dataset.addSignature(attendance.getIdStudent(), attendance.getStudent());
		dataset.addSignature(attendance.getIdSupervisor(), attendance.getSupervisor());
		
		return dataset;
	}
	
	public static Jury buildJury(JuryFormReport jury) {
		Jury dataset = new Jury();
		
		dataset.setStage(jury.getStage());
		dataset.setTitle(jury.getTitle());
		dataset.setDate(jury.getDate());
		dataset.setLocal(jury.getLocal());
		dataset.setIdStudent(jury.getIdStudent());
		dataset.setComments(jury.getComments());
		dataset.setScore(jury.getScore());
		dataset.setEvaluationText(jury.getEvaluationText());
		dataset.setRequestFinalDocumentStage1(jury.isRequestFinalDocumentStage1());
		
		for(JuryFormAppraiserReport appraiser : jury.getAppraisers()) {
			double scoreWriting = 0, scoreOral = 0, scoreArgumentation = 0;
			
			for(JuryFormAppraiserScoreReport score : jury.getScores()) {
				if(score.getIdUser() == appraiser.getIdUser()) {
					scoreWriting = score.getScoreWriting();
					scoreOral = score.getScoreOral();
					scoreArgumentation = score.getScoreArgumentation();
				}
			}
			
			dataset.addAppraiser(appraiser.getIdUser(), appraiser.getDescription(), scoreWriting, scoreOral, scoreArgumentation, appraiser.getScore(), appraiser.getComments(), appraiser.getDetail());
			dataset.addSignature(appraiser.getIdUser(), appraiser.getName());
		}
		
		dataset.addSignature(jury.getIdStudent(), jury.getStudent());
		
		return dataset;
	}
	
	public static InternshipPosterRequest build(InternshipPosterRequestForm request) {
		InternshipPosterRequest dataset = new InternshipPosterRequest();
		
		dataset.setDate(request.getDate());
		dataset.setCity(request.getDepartment().getCampus().getName());
		dataset.setDepartment(request.getDepartment().getName());
		dataset.setManager(request.getManager().getName());
		dataset.setArticles(request.getArticles());
		dataset.setIdStudent(request.getStudent().getIdUser());
		dataset.setIdSupervisor(request.getSupervisor().getIdUser());
		
		int member = 1, substitute = 1;
		
		for(InternshipPosterAppraiserRequest appraiser : request.getAppraisers()) {
			dataset.addAppraiser(appraiser.getAppraiser().getIdUser(), (appraiser.isSubstitute() ? "Suplente " + String.valueOf(substitute++) : "Membro " + String.valueOf(member++)));
			
			dataset.addSignature(appraiser.getAppraiser().getIdUser(), appraiser.getAppraiser().getName());
		}
		
		dataset.addSignature(request.getStudent().getIdUser(), request.getStudent().getName());
		dataset.addSignature(request.getSupervisor().getIdUser(), request.getSupervisor().getName());
		
		return dataset;
	}
	
	public static List<User> getSignaturesList(InternshipPosterRequestForm request) {
		List<User> users = new ArrayList<User>();
		
		for(InternshipPosterAppraiserRequest appraiser : request.getAppraisers()) {
			User u = new User();
			
			u.setIdUser(appraiser.getAppraiser().getIdUser());
			u.setName(appraiser.getAppraiser().getName());
			
			users.add(u);
		}
		
		User student = new User();
		
		student.setIdUser(request.getStudent().getIdUser());
		student.setName(request.getStudent().getName());
		
		users.add(student);
		
		User supervisor = new User();
		
		supervisor.setIdUser(request.getSupervisor().getIdUser());
		supervisor.setName(request.getSupervisor().getName());
		
		users.add(supervisor);
		
		return users;
	}
	
	public static InternshipJury build(InternshipJuryFormReport jury) {
		InternshipJury dataset = new InternshipJury();
		
		dataset.setCompany(jury.getCompany());
		dataset.setTitle(jury.getTitle());
		dataset.setDate(jury.getDate());
		dataset.setLocal(jury.getLocal());
		dataset.setIdStudent(jury.getIdStudent());
		dataset.setIdSupervisor(jury.getIdSupervisor());
		dataset.setComments(jury.getComments());
		dataset.setCompanySupervisorPonderosity(jury.getCompanySupervisorPonderosity());
		dataset.setSupervisorPonderosity(jury.getSupervisorPonderosity());
		dataset.setAppraisersPonderosity(jury.getAppraisersPonderosity());
		dataset.setCompanySupervisorScore(jury.getCompanySupervisorScore());
		dataset.setSupervisorScore(jury.getSupervisorScore());
		dataset.setFinalScore(jury.getFinalScore());
		
		int app = 1;
		boolean findSupervisor = false;
		for(JuryFormAppraiserReport appraiser : jury.getAppraisers()) {
			dataset.addAppraiser(appraiser.getIdUser(), appraiser.getDescription(), appraiser.getScore(), appraiser.getComments(), appraiser.getDetail());
			dataset.addSignature(appraiser.getIdUser(), appraiser.getName());
			
			if(appraiser.getIdUser() != jury.getIdSupervisor()) {
				if(app == 1) {
					dataset.setIdAppraiser1(appraiser.getIdUser());
				} else if(app == 2) {
					dataset.setIdAppraiser2(appraiser.getIdUser());
				}
				app++;
			} else {
				findSupervisor = true;
			}
		}
		
		dataset.addSignature(jury.getIdStudent(), jury.getStudent());
		
		if(!findSupervisor) {
			dataset.addSignature(jury.getIdSupervisor(), jury.getSupervisor());
			dataset.addSupervisorSignature();
		}
		
		return dataset;
	}
	
	public static List<User> getSignaturesList(InternshipJuryFormReport jury) {
		List<User> users = new ArrayList<User>();
		boolean findSupervisor = false;
		
		for(JuryFormAppraiserReport appraiser : jury.getAppraisers()) {
			User u = new User();
			
			u.setIdUser(appraiser.getIdUser());
			u.setName(appraiser.getName());
			
			users.add(u);
			
			if(appraiser.getIdUser() == jury.getIdSupervisor()) {
				findSupervisor = true;
			}
		}
		
		if(!findSupervisor && (jury.getIdSupervisor() != 0)) {
			User supervisor = new User();
			
			supervisor.setIdUser(jury.getIdSupervisor());
			supervisor.setName(jury.getSupervisor());
			
			users.add(supervisor);
		}
		
		User student = new User();
		
		student.setIdUser(jury.getIdStudent());
		student.setName(jury.getStudent());
		
		users.add(student);
		
		return users;
	}

}
