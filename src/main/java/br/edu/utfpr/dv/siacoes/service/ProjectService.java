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
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@Path("/project")
public class ProjectService {
	
	@Secure
	@GET
	@Path("/find/current/{iddepartment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findCurrent(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment) {
		try {
			ProjectBO bo = new ProjectBO();
			Semester semester = new SemesterBO().findByDate(new CampusBO().findByDepartment(idDepartment).getIdCampus(), DateUtils.getToday().getTime());
			Project project = bo.findCurrentProject(new LoginService().getUser(securityContext).getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
			
			if(project == null) {
				project = bo.findLastProject(new LoginService().getUser(securityContext).getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
			}
			
			if(project != null) {
				project.setFile(null);
			}
			
			return Response.ok(project).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}

}
