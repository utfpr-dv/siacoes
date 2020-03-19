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
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.AttendanceFrequency;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
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
	
	public int save(int idUser, Attendance attendance) throws Exception {
		try {
			if(attendance.getComments().isEmpty()) {
				throw new Exception("Informe as observações/orientações.");
			}
			if((attendance.getProposal() == null) || (attendance.getProposal().getIdProposal() == 0)) {
				throw new Exception("Informe o Projeto de TCC 1.");
			}
			if((attendance.getSupervisor() == null) || (attendance.getSupervisor().getIdUser() == 0)) {
				throw new Exception("Informe o orientador.");
			}
			if(attendance.getIdAttendance() != 0) {
				if(this.hasSignature(attendance.getIdAttendance())) {
					throw new Exception("O registro não pode ser alterado pois já foi assinado.");
				}
			} else {
				if(this.hasSignature(attendance.getProposal().getIdProposal(), attendance.getStage(), attendance.getSupervisor().getIdUser())) {
					throw new Exception("Não é possível incluir uma nova reunião de TCC " + String.valueOf(attendance.getStage()) + " pois o documento de reuniões já foi assinado.");
				}
			}
			
			AttendanceDAO dao = new AttendanceDAO();
			
			return dao.save(idUser, attendance);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void sendAttendanceSignedMessage(int idGroup) throws Exception {
		Attendance attendance = new AttendanceDAO().listByGroup(idGroup).get(0);
		attendance.setProposal(new ProposalBO().findById(attendance.getProposal().getIdProposal()));
		User manager = new UserBO().findManager(attendance.getProposal().getDepartment().getIdDepartment(), SystemModule.SIGET);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
		keys.add(new EmailMessageEntry<String, String>("student", attendance.getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("supervisor", attendance.getSupervisor().getName()));
		keys.add(new EmailMessageEntry<String, String>("title", attendance.getProposal().getTitle()));
		keys.add(new EmailMessageEntry<String, String>("stage", String.valueOf(attendance.getStage())));
		
		new EmailMessageBO().sendEmail(manager.getIdUser(), MessageType.SIGNEDATTENDANCE, keys);
	}
	
	public void sendRequestSignAttendanceMessage(int idGroup, List<User> users) throws Exception {
		Attendance attendance = new AttendanceDAO().listByGroup(idGroup).get(0);
		attendance.setProposal(new ProposalBO().findById(attendance.getProposal().getIdProposal()));
		
		for(User user : users) {
			List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
			
			keys.add(new EmailMessageEntry<String, String>("name", user.getName()));
			keys.add(new EmailMessageEntry<String, String>("student", attendance.getStudent().getName()));
			keys.add(new EmailMessageEntry<String, String>("supervisor", attendance.getSupervisor().getName()));
			keys.add(new EmailMessageEntry<String, String>("title", attendance.getProposal().getTitle()));
			keys.add(new EmailMessageEntry<String, String>("stage", String.valueOf(attendance.getStage())));
			
			new EmailMessageBO().sendEmail(user.getIdUser(), MessageType.SIGNEDATTENDANCE, keys, false);
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
	
	public List<Integer> listIdGroup(int idProposal, int stage) throws Exception {
		try {
			AttendanceDAO dao = new AttendanceDAO();
			
			return dao.listIdGroup(idProposal, stage);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean hasSignature(int idAttendance) throws SQLException {
		int idGroup = new AttendanceDAO().findIdGroup(idAttendance);
		
		return Document.hasSignature(DocumentType.ATTENDANCE, idGroup);
	}
	
	public boolean hasSignature(int idAttendance, int idUser) throws SQLException {
		int idGroup = new AttendanceDAO().findIdGroup(idAttendance);
		
		return Document.hasSignature(DocumentType.ATTENDANCE, idGroup, idUser);
	}
	
	public boolean hasSignature(int idProposal, int stage, int idSupervisor) throws SQLException {
		int idGroup = new AttendanceDAO().findIdGroup(idProposal, stage, idSupervisor);
		
		return Document.hasSignature(DocumentType.ATTENDANCE, idGroup);
	}
	
	public boolean hasSignature(int idProposal, int stage, int idSupervisor, int idUser) throws SQLException {
		int idGroup = new AttendanceDAO().findIdGroup(idProposal, stage, idSupervisor);
		
		return Document.hasSignature(DocumentType.ATTENDANCE, idGroup, idUser);
	}
	
	public br.edu.utfpr.dv.siacoes.report.dataset.v1.Attendance buildDataset(int idStudent, int idProposal, int idSupervisor, int stage) throws SQLException {
		int idGroup = new AttendanceDAO().findIdGroup(idProposal, stage, idSupervisor);
		
		if((idGroup != 0) && Document.hasSignature(DocumentType.ATTENDANCE, idGroup)) {
			return SignDatasetBuilder.build(new AttendanceDAO().getReport(idGroup));
		} else {
			idGroup = new AttendanceDAO().createGroup(idStudent, idProposal, idSupervisor, stage);
			
			return SignDatasetBuilder.build(new AttendanceDAO().getReport(idGroup));
		}
	}
	
	public byte[] getReport(int idStudent, int idProposal, int idSupervisor, int stage) throws Exception {
		if(this.hasSignature(idProposal, stage, idSupervisor)) {
			int idGroup = new AttendanceDAO().findIdGroup(idProposal, stage, idSupervisor);
			
			return Document.getSignedDocument(DocumentType.ATTENDANCE, idGroup);
		} else {
			br.edu.utfpr.dv.siacoes.report.dataset.v1.Attendance dataset = this.buildDataset(idStudent, idProposal, idSupervisor, stage);
			
			List<br.edu.utfpr.dv.siacoes.report.dataset.v1.Attendance> list = new ArrayList<br.edu.utfpr.dv.siacoes.report.dataset.v1.Attendance>();
			list.add(dataset);
			
			return new ReportUtils().createPdfStream(list, "Attendances", new ProposalBO().findIdDepartment(idProposal)).toByteArray();
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
	
	public boolean delete(int idUser, int id) throws Exception{
		try {
			if(this.hasSignature(id)) {
				throw new Exception("O registro não pode ser excluído pois já foi assinado.");
			}
			
			AttendanceDAO dao = new AttendanceDAO();
			
			return dao.delete(idUser, id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int idUser, Attendance attendance) throws Exception{
		return this.delete(idUser, attendance.getIdAttendance());
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
