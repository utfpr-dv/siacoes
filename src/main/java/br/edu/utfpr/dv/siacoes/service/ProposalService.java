package br.edu.utfpr.dv.siacoes.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@Path("/proposal")
public class ProposalService {
	
	@Secure
	@GET
	@Path("/have/{iddepartment}")
	public Response haveProposal(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment) {
		try {
			ProposalBO bo = new ProposalBO();
			Semester semester = new SemesterBO().findByDate(new CampusBO().findByDepartment(idDepartment).getIdCampus(), DateUtils.getToday().getTime());
			Proposal proposal = bo.findCurrentProposal(new LoginService().getUser(securityContext).getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
			
			if(proposal.getIdProposal() == 0) {
				return Response.ok(false).build();
			} else {
				return Response.ok(true).build();	
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/find/current/{iddepartment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findCurrent(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment) {
		try {
			ProposalBO bo = new ProposalBO();
			Semester semester = new SemesterBO().findByDate(new CampusBO().findByDepartment(idDepartment).getIdCampus(), DateUtils.getToday().getTime());
			Proposal proposal = bo.findCurrentProposal(new LoginService().getUser(securityContext).getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
			
			proposal.setFile(null);
			
			return Response.ok(proposal).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/find/current/id/{iddepartment}")
	public Response findCurrentId(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment) {
		try {
			ProposalBO bo = new ProposalBO();
			Semester semester = new SemesterBO().findByDate(new CampusBO().findByDepartment(idDepartment).getIdCampus(), DateUtils.getToday().getTime());
			Proposal proposal = bo.findCurrentProposal(new LoginService().getUser(securityContext).getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
			
			return Response.ok(proposal.getIdProposal()).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/list/supervisors/{idproposal}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listSupervisors(@Context SecurityContext securityContext, @PathParam("idproposal") int idProposal) {
		try {
			User user = new LoginService().getUser(securityContext);
			ProposalBO bo = new ProposalBO();
			Proposal proposal = bo.findById(idProposal);
			
			if(proposal.getStudent().getIdUser() != user.getIdUser()) {
				throw new Exception("O proposta de TCC requisitada não pertence ao acadêmico.");
			}
			
			List<User> list = bo.listSupervisors(idProposal, true);
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/supervisor/current/{idproposal}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findCurrentSupervisor(@Context SecurityContext securityContext, @PathParam("idproposal") int idProposal) {
		try {
			User user = new LoginService().getUser(securityContext);
			ProposalBO bo = new ProposalBO();
			Proposal proposal = bo.findById(idProposal);
			
			if(proposal.getStudent().getIdUser() != user.getIdUser()) {
				throw new Exception("O proposta de TCC requisitada não pertence ao acadêmico.");
			}
			
			User supervisor = new SupervisorChangeBO().findCurrentSupervisor(idProposal);
			
			return Response.ok(supervisor).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/list/appraisers/{idproposal}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAppraisers(@Context SecurityContext securityContext, @PathParam("idproposal") int idProposal) {
		try {
			User user = new LoginService().getUser(securityContext);
			ProposalBO bo = new ProposalBO();
			Proposal proposal = bo.findById(idProposal);
			
			if(proposal.getStudent().getIdUser() != user.getIdUser()) {
				throw new Exception("O proposta de TCC requisitada não pertence ao acadêmico.");
			}
			
			List<ProposalAppraiser> list = new ProposalAppraiserBO().listAppraisers(idProposal);
			
			for(ProposalAppraiser appraiser : list) {
				appraiser.setFile(null);
			}
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}

}
