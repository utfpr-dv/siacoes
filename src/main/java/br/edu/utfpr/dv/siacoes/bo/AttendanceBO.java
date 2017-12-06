package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.AttendanceDAO;
import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

public class AttendanceBO {

	public List<Attendance> listAll() throws Exception{
		try {
			AttendanceDAO dao = new AttendanceDAO();
			
			return dao.listAll();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Attendance> listByStudent(int idStudent) throws Exception{
		try {
			AttendanceDAO dao = new AttendanceDAO();
			
			return dao.listByStudent(idStudent);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Attendance> listByStudent(int idStudent, int idSupervisor, int idProposal, int stage) throws Exception{
		try {
			AttendanceDAO dao = new AttendanceDAO();
			
			return dao.listByStudent(idStudent, idSupervisor, idProposal, stage);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(Attendance attendance) throws Exception{
		try {
			if(attendance.getComments().isEmpty()){
				throw new Exception("Informe as observações/orientações.");
			}
			
			AttendanceDAO dao = new AttendanceDAO();
			
			if(attendance.getProposal().getIdProposal() == 0){
				UserBO ubo = new UserBO();
				User user = ubo.findById(attendance.getStudent().getIdUser());
				
				ProposalBO bo = new ProposalBO();
				
				Semester semester = new SemesterBO().findByDate(new CampusBO().findByDepartment(user.getDepartment().getIdDepartment()).getIdCampus(), DateUtils.getToday().getTime());
				
				attendance.setProposal(bo.findCurrentProposal(user.getIdUser(), user.getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear()));
			}
			
			return dao.save(attendance);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Attendance findById(int id) throws Exception{
		try {
			AttendanceDAO dao = new AttendanceDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public AttendanceReport getReport(int idStudent, int idProposal, int idSupervisor, int stage) throws Exception{
		try {
			AttendanceDAO dao = new AttendanceDAO();
			
			return dao.getReport(idStudent, idProposal, idSupervisor, stage);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public byte[] getAttendanceReport(int idDepartment, int semester, int year, int stage, boolean showDetail) throws Exception{
		try {
			AttendanceDAO dao = new AttendanceDAO();
			
			List<AttendanceReport> list = dao.getAttendanceReport(idDepartment, semester, year, stage);
			
			ByteArrayOutputStream report;
			
			if(!showDetail){
				report = new ReportUtils().createPdfStream(list, "AttendanceList");
			}else{
				report = new ReportUtils().createPdfStream(list, "AttendanceDetailedList");
			}
			
			return report.toByteArray();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int id) throws Exception{
		try {
			AttendanceDAO dao = new AttendanceDAO();
			
			return dao.delete(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(Attendance attendance) throws Exception{
		return this.delete(attendance.getIdAttendance());
	}
	
}
