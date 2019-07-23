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
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@Path("/finaldocument")
public class FinalDocumentService {
	
	@Secure
	@GET
	@Path("/find/{iddepartment}/{stage}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment, @PathParam("stage") int stage) {
		try {
			User user = new LoginService().getUser(securityContext);
			Semester semester = new SemesterBO().findByDate(new CampusBO().findByDepartment(idDepartment).getIdCampus(), DateUtils.getToday().getTime());
			FinalDocument doc = null;
			
			if(stage == 2) {
				Thesis thesis = new ThesisBO().findCurrentThesis(user.getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
				
				if(thesis == null) {
					thesis = new ThesisBO().findLastThesis(user.getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
				}
				
				if(thesis != null) {
					doc = new FinalDocumentBO().findByThesis(thesis.getIdThesis());
				}
			} else {
				Project project = new ProjectBO().findCurrentProject(user.getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
				
				if(project == null) {
					project = new ProjectBO().findLastProject(user.getIdUser(), idDepartment, semester.getSemester(), semester.getYear());
				}
				
				if(project != null) {
					doc = new FinalDocumentBO().findByProject(project.getIdProject());
				}
			}
			
			if(doc != null) {
				doc.setFile(null);
			}
			
			return Response.ok(doc).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}

}
