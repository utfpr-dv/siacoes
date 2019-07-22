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

import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;

@Path("/siget")
public class SigetService {
	
	@Secure
	@GET
	@Path("/config/{iddepartment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConfig(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment) {
		try {
			return Response.ok(new SigetConfigBO().findByDepartment(idDepartment)).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}

}
