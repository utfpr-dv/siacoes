package br.edu.utfpr.dv.siacoes.service;

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
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@Path("/thesis")
public class ThesisService {
	
	@Secure
	@GET
	@Path("/find/current/{iddepartment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findCurrent(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment) {
		try {
			ThesisBO bo = new ThesisBO();
			Semester semester = new SemesterBO().findByDate(new CampusBO().findByDepartment(idDepartment).getIdCampus(), DateUtils.getToday().getTime());
			Thesis thesis = bo.findCurrentThesis(new LoginService().getUser(securityContext).getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
			
			if(thesis == null) {
				thesis = bo.findLastThesis(new LoginService().getUser(securityContext).getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
			}
			
			if(thesis != null) {
				thesis.setFile(null);
			}
			
			return Response.ok(thesis).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}

}
