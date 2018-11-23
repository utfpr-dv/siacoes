package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.AttendanceDAO;
import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.AttendanceFrequency;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import javatests.Issue1833;

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
	
	public int save(Attendance attendance) throws Exception {
		try {
			if(attendance.getComments().isEmpty()) {
				throw new Exception("Informe as observações/orientações.");
			}
			if((attendance.getProposal() == null) || (attendance.getProposal().getIdProposal() == 0)) {
				throw new Exception("Informe o Projeto de TCC 1");
			}
			
			AttendanceDAO dao = new AttendanceDAO();
			
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
	
	public List<AttendanceReport> getReportList(int idStudent, int idProposal, int idSupervisor, int stage) throws Exception{
		try {
			AttendanceDAO dao = new AttendanceDAO();
			List<AttendanceReport> list = new ArrayList<AttendanceReport>();
			
			list.add(dao.getReport(idStudent, idProposal, idSupervisor, stage));
			
			return list;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public byte[] getReport(int idStudent, int idProposal, int idSupervisor, int stage) throws Exception{
		try {
			ByteArrayOutputStream report = new ReportUtils().createPdfStream(getReportList(idStudent, idProposal, idSupervisor, stage), "Attendances", new ProposalBO().findIdDepartment(idProposal));
			
			return report.toByteArray();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public byte[] getAttendanceReport(int idDepartment, int semester, int year, int stage, boolean includeCosupervisor, boolean showDetail) throws Exception{
		try {
			AttendanceDAO dao = new AttendanceDAO();
			
			List<AttendanceReport> list = dao.getAttendanceReport(idDepartment, semester, year, stage, includeCosupervisor);
			
			ByteArrayOutputStream report;
			
			if(!showDetail){
				report = new ReportUtils().createPdfStream(list, "AttendanceList", idDepartment);
			}else{
				report = new ReportUtils().createPdfStream(list, "AttendanceDetailedList", idDepartment);
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
	
	public boolean validateFrequency(int idStudent, int idSupervisor, int idProposal, int stage, AttendanceFrequency frequency) throws Exception {
		List<Attendance> list = this.listByStudent(idStudent, idSupervisor, idProposal, stage);
		Date startDate, endDate = DateUtils.getToday().getTime();
		int numberAttendances = 0;
		
		if(stage == 2) {
			/*Project project = new ProjectBO().findByProposal(idProposal);
			
			if(project.getSemester() == 2) {
				startDate = new SemesterBO().findBySemester(new ProjectBO().findIdCampus(project.getIdProject()), 1, project.getYear() + 1).getStartDate();
			} else {
				startDate = new SemesterBO().findBySemester(new ProjectBO().findIdCampus(project.getIdProject()), 2, project.getYear()).getStartDate();
			}*/
			
			startDate = new SemesterBO().findByDate(new ProposalBO().findIdCampus(idProposal), endDate).getStartDate();
		} else {
			startDate = new ProposalBO().findById(idProposal).getSubmissionDate();
		}
		
		List<Pair> list2 = new ArrayList<Pair>();
		for(Attendance attendance : list) {
			int key = 0, year = DateUtils.getYear(attendance.getDate());
			boolean find = false;
			
			if((frequency == AttendanceFrequency.WEEKLY) || (frequency == AttendanceFrequency.BIWEEKLY)) {
				key = DateUtils.getWeekOfYear(attendance.getDate());
			} else {
				key = DateUtils.getMonth(attendance.getDate());
			}
			
			if((frequency == AttendanceFrequency.BIWEEKLY) || (frequency == AttendanceFrequency.BIMONTHLY)) {
				key = key / 2;
			} else if(frequency == AttendanceFrequency.QUARTERLY) {
				key = key / 3;
			}
			
			for(Pair p : list2) {
				if((p.getKey() == key) && (p.getYear() == year)) {
					find = true;
				}
			}
			
			if(!find) {
				list2.add(new Pair(key, year));
			}
		}
		
		if(frequency == AttendanceFrequency.WEEKLY) {
			numberAttendances = DateUtils.getDifferenceInWeeks(startDate, endDate);
		} else if(frequency == AttendanceFrequency.BIWEEKLY) {
			numberAttendances = DateUtils.getDifferenceInWeeks(startDate, endDate) / 2;
		} else if(frequency == AttendanceFrequency.MONTHLY) {
			numberAttendances = DateUtils.getDifferenceInMonths(startDate, endDate);
		} else if(frequency == AttendanceFrequency.BIMONTHLY) {
			numberAttendances = DateUtils.getDifferenceInMonths(startDate, endDate) / 2;
		} else if(frequency == AttendanceFrequency.QUARTERLY) {
			numberAttendances = DateUtils.getDifferenceInMonths(startDate, endDate) / 3;
		}
		
		return (list2.size() >= numberAttendances);
	}
	
	private class Pair {
		
		private int key;
		private int year;
		
		public Pair(int key, int year) {
			this.setKey(key);
			this.setYear(year);
		}
		
		public int getKey() {
			return key;
		}
		public void setKey(int key) {
			this.key = key;
		}
		public int getYear() {
			return year;
		}
		public void setYear(int year) {
			this.year = year;
		}
		
	}
	
}
