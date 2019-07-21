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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionFooterReport;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;

import javax.ws.rs.core.Response;

@Path("/activitysubmission")
public class ActivitySubmissionService {
	
	@Secure
	@GET
	@Path("/summary/{iddepartment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSummary(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment) {
		try {
			User user = new LoginService().getUser(securityContext);
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			List<ActivitySubmission> listReport = bo.listByStudent(user.getIdUser(), idDepartment, ActivityFeedback.APPROVED.getValue(), false);
			List<ActivitySubmissionFooterReport> scores = bo.getFooterReport(listReport);
			
			return Response.ok(scores).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/list/{iddepartment}/{feedback}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment, @PathParam("feedback") int feedback) {
		try {
			User user = new LoginService().getUser(securityContext);
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			List<ActivitySubmission> list = bo.listByStudent(user.getIdUser(), idDepartment, feedback, false);
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/find/{idactivitysubmission}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response find(@Context SecurityContext securityContext, @PathParam("idactivitysubmission") int idActivitySubmission) {
		try {
			User user = new LoginService().getUser(securityContext);
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			ActivitySubmission submission = bo.findById(idActivitySubmission);
			
			if(submission.getStudent().getIdUser() != user.getIdUser()) {
				throw new Exception("A atividade requisitada não pertence ao acadêmico.");
			}
			
			return Response.ok(submission).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}

}
