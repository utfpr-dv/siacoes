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

import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.model.User;

@Path("/attendance")
public class AttendanceService {
	
	@Secure
	@GET
	@Path("/list/{idproposal}/{idsupervisor}/{stage}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context SecurityContext securityContext, @PathParam("idproposal") int idProposal, @PathParam("idsupervisor") int idSupervisor, @PathParam("stage") int stage) {
		try {
			AttendanceBO bo = new AttendanceBO();
			List<Attendance> list = bo.listByStudent(new LoginService().getUser(securityContext).getIdUser(), idSupervisor, idProposal, stage);
			
			return Response.ok(list).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}
	
	@Secure
	@GET
	@Path("/find/{idattendance}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response find(@Context SecurityContext securityContext, @PathParam("idattendance") int idAttendance) {
		try {
			User user = new LoginService().getUser(securityContext);
			AttendanceBO bo = new AttendanceBO();
			Attendance attendance = bo.findById(idAttendance);
			
			if(attendance.getStudent().getIdUser() != user.getIdUser()) {
				throw new Exception("O registro de reunião requisitado não pertence ao acadêmico.");
			}
			
			return Response.ok(attendance).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.INTERNAL_SERVER_ERROR.ordinal(), e.getMessage()).build();
		}
	}

}
