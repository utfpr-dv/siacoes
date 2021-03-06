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

import br.edu.utfpr.dv.siacoes.bo.ActivityBO;
import br.edu.utfpr.dv.siacoes.model.Activity;

@Path("/activity")
public class ActivityService {
	
	@Secure
	@GET
	@Path("/list/{iddepartment}/{idactivitygroup}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment, @PathParam("idactivitygroup") int idGroup) {
		try {
			ActivityBO bo = new ActivityBO();
			List<Activity> list = bo.listByGroup(idDepartment, idGroup);
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			//return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal()).build();
		}
	}

}
