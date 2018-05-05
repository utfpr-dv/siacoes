package br.edu.utfpr.dv.siacoes.service;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import br.edu.utfpr.dv.siacoes.model.UserInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;

@Path("/user")
public class UserService {

	@Secure
	@GET
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(@Context SecurityContext securityContext) {
		try {
			UserInfo user = new UserInfo(new LoginService().getUser(securityContext));
			
			return Response.ok(user).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}

}
