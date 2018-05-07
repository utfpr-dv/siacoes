package br.edu.utfpr.dv.siacoes.service;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.UserInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;

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
	
	@Secure
	@POST
	@Path("/profile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfile(@Context SecurityContext securityContext, UserInfo info) {
		try {
			User user = new LoginService().getUser(securityContext);
			
			user.setEmail(info.getEmail());
			user.setPhone(info.getPhone());
			user.setArea(info.getArea());
			user.setResearch(info.getResearch());
			user.setLattes(info.getLattes());
			user.setPhoto(info.getPhoto());
			user.setRegisterSemester(info.getRegisterSemester());
			user.setRegisterYear(info.getRegisterYear());
			user.setInstitution(info.getInstitution());
			
			if(user.isExternal()) {
				user.setName(info.getName());
				user.setStudentCode(info.getStudentCode());
			}
			
			new UserBO().save(user);
			
			return Response.ok().build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}		
	}
	
	@Secure
	@POST
	@Path("/changepassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePassword(@Context SecurityContext securityContext, ChangePassword change) {
		try {
			User user = new LoginService().getUser(securityContext);
			
			new UserBO().changePassword(user.getIdUser(), change.getCurrentPassword(), change.getNewPassword());
			
			return Response.ok().build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		
			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	public class ChangePassword {
		
		private String currentPassword;
		private String newPassword;
		
		public String getCurrentPassword() {
			return currentPassword;
		}
		public void setCurrentPassword(String currentPassword) {
			this.currentPassword = currentPassword;
		}
		public String getNewPassword() {
			return newPassword;
		}
		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}
		
	}

}
