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

import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.User;

@Path("/internship")
public class InternshipService {
	
	@Secure
	@GET
	@Path("/list/{iddepartment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment) {
		try {
			User user = new LoginService().getUser(securityContext);
			InternshipBO bo = new InternshipBO();
			List<Internship> list = bo.listByStudent(user.getIdUser(), idDepartment);
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/find/{idinternship}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response find(@Context SecurityContext securityContext, @PathParam("idinternship") int idInternship) {
		try {
			User user = new LoginService().getUser(securityContext);
			InternshipBO bo = new InternshipBO();
			Internship internship = bo.findById(idInternship);
			
			if(internship.getStudent().getIdUser() != user.getIdUser()) {
				throw new Exception("O registro de estágio requisitado não pertence ao acadêmico.");
			}
			
			return Response.ok(internship).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}

}
