package br.edu.utfpr.dv.siacoes.service;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.model.UserInfo;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

import java.util.ArrayList;
import java.util.List;
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
			UserInfo user = new UserInfo(new LoginService().getUser(securityContext), null);
			
			return Response.ok(user).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/list/profiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listProfiles(@Context SecurityContext securityContext) {
		try {
			List<UserProfile> profiles = new LoginService().getUser(securityContext).getProfiles();
			
			return Response.ok(profiles).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/list/companysupervisors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listCompanySupervisors(@Context SecurityContext securityContext) {
		try {
			UserBO bo = new UserBO();
			List<User> list = bo.listAllCompanySupervisors(true);
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/list/companysupervisors/{idcompany}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listCompanySupervisors(@Context SecurityContext securityContext, @PathParam("idcompany") int idCompany) {
		try {
			UserBO bo = new UserBO();
			List<User> list = bo.listSupervisorsByCompany(idCompany, true);
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/list/supervisors/{iddepartment}/{module}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listSupervisors(@Context SecurityContext securityContext, @PathParam("iddepartment") int idDepartment, @PathParam("module") int module) {
		try {
			UserBO bo = new UserBO();
			List<User> list = new ArrayList<User>();
			SupervisorFilter filter = SupervisorFilter.EVERYONE;
			
			if(module == SystemModule.SIGET.getValue()) {
				filter = new SigetConfigBO().findByDepartment(idDepartment).getSupervisorFilter();
			} else if(module == SystemModule.SIGES.getValue()) {
				filter = new SigesConfigBO().findByDepartment(idDepartment).getSupervisorFilter();
			}
			
			if(filter == SupervisorFilter.DEPARTMENT) {
				list = bo.listSupervisorsByDepartment(idDepartment, true);
			} else if(filter == SupervisorFilter.CAMPUS) {
				list = bo.listSupervisorsByCampus(new CampusBO().findByDepartment(idDepartment).getIdCampus(), true);
			} else if(filter == SupervisorFilter.INSTITUTION) {
				list = bo.listInstitutionalSupervisors(true);
			} else {
				list = bo.listAllSupervisors(true, false);
			}
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/list/departments/{profile}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listDepartments(@Context SecurityContext securityContext, @PathParam("profile") int profile) {
		try {
			List<UserDepartment> departments = new UserDepartmentBO().list(new LoginService().getUser(securityContext).getIdUser(), UserProfile.valueOf(profile));
			
			return Response.ok(departments).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@POST
	@Path("/profile/update")
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
			//user.setRegisterSemester(info.getRegisterSemester());
			//user.setRegisterYear(info.getRegisterYear());
			user.setInstitution(info.getInstitution());
			
			if(user.isExternal()) {
				user.setName(info.getName());
				user.setStudentCode(info.getStudentCode());
			}
			
			new UserBO().save(user.getIdUser(), user);
			
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
