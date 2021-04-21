package br.edu.utfpr.dv.siacoes.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;

@Path("/internshipjury")
public class InternshipJuryService {
	
	@Secure
	@GET
	@Path("/list/student")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listByStudent(@Context SecurityContext securityContext) {
		try {
			InternshipJuryBO bo = new InternshipJuryBO();
			List<InternshipJury> list = bo.listByStudent(new LoginService().getUser(securityContext).getIdUser(), 0, 0, 0);
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			//return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal()).build();
		}
	}

}
