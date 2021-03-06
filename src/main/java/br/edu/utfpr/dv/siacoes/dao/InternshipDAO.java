package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipByCompany;
import br.edu.utfpr.dv.siacoes.model.InternshipListReport;
import br.edu.utfpr.dv.siacoes.model.InternshipMissingDocumentsReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipRequiredType;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;

public class InternshipDAO {
	
	private Connection conn;
	
	public InternshipDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public InternshipDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();
		}else{
			this.conn = conn;	
		}
	}
	
	public byte[] getInternshipPlan(int id) throws SQLException{
		if(id == 0){
			return null;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			this.conn = ConnectionDAO.getInstance().getConnection();
			stmt = this.conn.createStatement();
		
			rs = stmt.executeQuery("SELECT internship.internshipPlan FROM internship WHERE idInternship=" + id);
			
			if(rs.next()){
				return rs.getBytes("internshipPlan");
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public byte[] getFinalReport(int id) throws SQLException{
		if(id == 0){
			return null;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			this.conn = ConnectionDAO.getInstance().getConnection();
			stmt = this.conn.createStatement();
		
			rs = stmt.executeQuery("SELECT internship.finalReport FROM internship WHERE idInternship=" + id);
			
			if(rs.next()){
				return rs.getBytes("finalReport");
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<Internship> listAll() throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipview AS internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"ORDER BY internship.startDate DESC");
			
			List<Internship> list = new ArrayList<Internship>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<Internship> listByDepartment(int idDepartment) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipview AS internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"WHERE internship.iddepartment=" + String.valueOf(idDepartment) +
					" ORDER BY internship.startDate DESC");
			
			List<Internship> list = new ArrayList<Internship>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<Internship> list(int idDepartment, int year, int idStudent, int idSupervisor, int idCompany, int type, int status, Date startDate1, Date startDate2, Date endDate1, Date endDate2, int companyStatus, String tag) throws SQLException {
		ResultSet rs = null;
		Statement stmt = null;
		String filterDate = "";
		String filterStatus = "";
		String filterTag = "";
		
		if ((startDate1 != null) && (DateUtils.getYear(startDate1) > 1900) && (startDate2 != null) && (DateUtils.getYear(startDate2) > 1900)) {
			filterDate = filterDate + " AND internship.startDate BETWEEN '" + DateUtils.format(DateUtils.getDayBegin(startDate1), "yyyy-MM-dd HH:mm:ss") + "' AND '" + DateUtils.format(DateUtils.getDayEnd(startDate2), "yyyy-MM-dd HH:mm:ss") + "'";
		} else if ((startDate1 != null) && (DateUtils.getYear(startDate1) > 1900)) {
			filterDate = filterDate + " AND internship.startDate >= '" + DateUtils.format(DateUtils.getDayBegin(startDate1), "yyyy-MM-dd HH:mm:ss") + "'";
		} else if ((startDate2 != null) && (DateUtils.getYear(startDate2) > 1900)) {
			filterDate = filterDate + " AND internship.startDate <= '" + DateUtils.format(DateUtils.getDayEnd(startDate2), "yyyy-MM-dd HH:mm:ss") + "'";
		}
		
		if ((endDate1 != null) && (DateUtils.getYear(endDate1) > 1900) && (endDate2 != null) && (DateUtils.getYear(endDate2) > 1900)) {
			filterDate = filterDate + " AND internship.endDate BETWEEN '" + DateUtils.format(DateUtils.getDayBegin(endDate1), "yyyy-MM-dd HH:mm:ss") + "' AND '" + DateUtils.format(DateUtils.getDayEnd(endDate2), "yyyy-MM-dd HH:mm:ss") + "'";
		} else if ((endDate1 != null) && (DateUtils.getYear(endDate1) > 1900)) {
			filterDate = filterDate + " AND internship.endDate >= '" + DateUtils.format(DateUtils.getDayBegin(endDate1), "yyyy-MM-dd HH:mm:ss") + "'";
		} else if ((endDate2 != null) && (DateUtils.getYear(endDate2) > 1900)) {
			filterDate = filterDate + " AND internship.endDate <= '" + DateUtils.format(DateUtils.getDayEnd(endDate2), "yyyy-MM-dd HH:mm:ss") + "'";
		}
		
		if(status == 0) {
			filterStatus = " AND ((NOT internship.hasfinalreport) OR (internship.type = 1 AND NOT EXISTS(SELECT idinternshipjury FROM internshipjury WHERE internshipjury.idinternship = internship.idinternship)))";	
		} else if(status == 1) {
			filterStatus = " AND internship.hasfinalreport AND (internship.type = 0 OR EXISTS(SELECT idinternshipjury FROM internshipjury WHERE internshipjury.idinternship = internship.idinternship))";
		}
		
		if(companyStatus == 0) {
			filterStatus = filterStatus + " AND internship.terminationDate IS NULL AND (internship.endDate IS NULL OR internship.endDate >= CURRENT_DATE)";
		} else if(companyStatus == 1) {
			filterStatus = filterStatus + " AND internship.terminationDate IS NULL AND internship.endDate < CURRENT_DATE";
		} else if(companyStatus == 2) {
			filterStatus = filterStatus + " AND internship.terminationDate IS NOT NULL";
		}
		
		if((tag != null) && !tag.trim().isEmpty()) {
			filterTag = " AND internship.tags ILIKE '%|" + tag.replace("\'", "") + "|%'";
		}
		
		try {
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipview AS internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " +
					"WHERE internship.iddepartment=" + String.valueOf(idDepartment) +
					(year > 0 ? " AND (YEAR(internship.startDate) >= " + String.valueOf(year) + " AND (YEAR(internship.endDate) <= " + String.valueOf(year) + " OR internship.endDate IS NULL)) " : "") +
					(idStudent > 0 ? " AND internship.idstudent = " + String.valueOf(idStudent) : "") +
					(idSupervisor > 0 ? " AND internship.idsupervisor = " + String.valueOf(idSupervisor) : "") +
					(idCompany > 0 ? " AND internship.idcompany = " + String.valueOf(idCompany) : "") +
					(type >= 0 ? " AND internship.type = " + String.valueOf(type) : "") + filterDate + filterStatus + filterTag +
					" ORDER BY internship.startDate DESC");
			
			List<Internship> list = new ArrayList<Internship>();
			
			while(rs.next()) {
				list.add(this.loadObject(rs, false));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<Internship> listByCompany(int idCompany) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipview AS internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"WHERE internship.idcompany=" + String.valueOf(idCompany) + " ORDER BY internship.startDate DESC");
			
			List<Internship> list = new ArrayList<Internship>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<Internship> listByCompanySupervisor(int idCompanySupervisor) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipview AS internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"WHERE internship.idcompanysupervisor=" + String.valueOf(idCompanySupervisor) + " ORDER BY internship.startDate DESC");
			
			List<Internship> list = new ArrayList<Internship>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<Internship> listBySupervisor(int idSupervisor, int idDepartment) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipview AS internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " +  
					"WHERE internship.idsupervisor=" + String.valueOf(idSupervisor) + " AND internship.iddepartment=" + String.valueOf(idDepartment) + 
					" ORDER BY internship.startDate DESC");
			
			List<Internship> list = new ArrayList<Internship>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<Internship> listByStudent(int idStudent, int idDepartment) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipview AS internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " +  
					"WHERE internship.idstudent=" + String.valueOf(idStudent) + " AND internship.iddepartment=" + String.valueOf(idDepartment) + 
					" ORDER BY internship.startDate DESC");
			
			List<Internship> list = new ArrayList<Internship>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public int findIdDepartment(int idInternship) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			this.conn = ConnectionDAO.getInstance().getConnection();
			stmt = this.conn.prepareStatement("SELECT idDepartment FROM internship WHERE idInternship=?");
		
			stmt.setInt(1, idInternship);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("idDepartment");
			}else{
				return 0;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public InternshipType getType(int idInternship) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			this.conn = ConnectionDAO.getInstance().getConnection();
			stmt = this.conn.prepareStatement("SELECT type FROM internship WHERE idInternship=?");
		
			stmt.setInt(1, idInternship);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return InternshipType.valueOf(rs.getInt("type"));
			}else{
				return InternshipType.NONREQUIRED;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public Internship findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"WHERE internship.idinternship=?");
			
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs, true);
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public int save(int idUser, Internship internship) throws SQLException{
		boolean insert = (internship.getIdInternship() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO internship(iddepartment, idcompany, idcompanysupervisor, idsupervisor, idstudent, type, comments, startDate, endDate, totalHours, internshipPlan, finalReport, reportTitle, requiredType, term, weekHours, weekDays, fillOnlyTotalHours, sei, terminationDate, tags) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE internship SET iddepartment=?, idcompany=?, idcompanysupervisor=?, idsupervisor=?, idstudent=?, type=?, comments=?, startDate=?, endDate=?, totalHours=?, internshipPlan=?, finalReport=?, reportTitle=?, requiredType=?, term=?, weekHours=?, weekDays=?, fillOnlyTotalHours=?, sei=?, terminationDate=?, tags=? WHERE idinternship=?");
			}
			
			stmt.setInt(1, internship.getDepartment().getIdDepartment());
			stmt.setInt(2, internship.getCompany().getIdCompany());
			stmt.setInt(3, internship.getCompanySupervisor().getIdUser());
			stmt.setInt(4, internship.getSupervisor().getIdUser());
			stmt.setInt(5, internship.getStudent().getIdUser());
			stmt.setInt(6, internship.getType().getValue());
			stmt.setString(7, internship.getComments());
			stmt.setDate(8, new java.sql.Date(internship.getStartDate().getTime()));
			if(internship.getEndDate() == null){
				stmt.setNull(9, Types.DATE);
			}else{
				stmt.setDate(9, new java.sql.Date(internship.getEndDate().getTime()));
			}
			stmt.setInt(10, internship.getTotalHours());
			stmt.setBytes(11, internship.getInternshipPlan());
			if(internship.getFinalReport() == null){
				stmt.setNull(12, Types.BINARY);
			}else{
				stmt.setBytes(12, internship.getFinalReport());
			}
			stmt.setString(13, internship.getReportTitle());
			stmt.setInt(14, internship.getRequiredType().getValue());
			stmt.setString(15, internship.getTerm());
			stmt.setDouble(16, internship.getWeekHours());
			stmt.setInt(17, internship.getWeekDays());
			stmt.setInt(18, (internship.isFillOnlyTotalHours() ? 1 : 0));
			stmt.setString(19, internship.getSei());
			if(internship.getTerminationDate() == null){
				stmt.setNull(20, Types.DATE);
			}else{
				stmt.setDate(20, new java.sql.Date(internship.getTerminationDate().getTime()));
			}
			stmt.setString(21, this.getTagsString(internship.getTags()));
			
			if(!insert){
				stmt.setInt(22, internship.getIdInternship());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					internship.setIdInternship(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, internship);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, internship);
			}
			
			return internship.getIdInternship();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private String getTagsString(List<String> tags) {
		if((tags == null) || (tags.size() == 0)) {
			return "";
		} else {
			String ret = "|";
			
			for(String s : tags) {
				ret = ret + s + "|";
			}
			
			return ret;
		}
	}
	
	private List<String> getTagsList(String tags) {
		if((tags == null) || (tags.trim().isEmpty())) {
			return new ArrayList<String>();	
		} else {
			List<String> ret = new ArrayList<String>();
			String[] list = tags.split("\\|");
			
			for(String s : list) {
				if(!s.isEmpty()) {
					ret.add(s);
				}
			}
			
			return ret;
		}
	}
	
	private Internship loadObject(ResultSet rs, boolean loadFiles) throws SQLException{
		Internship internship = new Internship();
		
		internship.setIdInternship(rs.getInt("idinternship"));
		internship.getDepartment().setIdDepartment(rs.getInt("iddepartment"));
		internship.getCompany().setIdCompany(rs.getInt("idcompany"));
		internship.getCompany().setName(rs.getString("companyName"));
		internship.getCompanySupervisor().setIdUser(rs.getInt("idcompanysupervisor"));
		internship.getSupervisor().setIdUser(rs.getInt("idsupervisor"));
		internship.getSupervisor().setName(rs.getString("supervisorName"));
		internship.getStudent().setIdUser(rs.getInt("idstudent"));
		internship.getStudent().setName(rs.getString("studentName"));
		internship.setType(InternshipType.valueOf(rs.getInt("type")));
		internship.setRequiredType(InternshipRequiredType.valueOf(rs.getInt("requiredType")));
		internship.setComments(rs.getString("comments"));
		internship.setStartDate(rs.getDate("startDate"));
		internship.setEndDate(rs.getDate("endDate"));
		internship.setTerminationDate(rs.getDate("terminationDate"));
		internship.setTerm(rs.getString("term"));
		internship.setWeekHours(rs.getDouble("weekHours"));
		internship.setWeekDays(rs.getInt("weekDays"));
		internship.setTotalHours(rs.getInt("totalHours"));
		internship.setReportTitle(rs.getString("reportTitle"));
		internship.setFillOnlyTotalHours(rs.getInt("fillOnlyTotalHours") == 1);
		internship.setSei(rs.getString("sei"));
		internship.setTags(this.getTagsList(rs.getString("tags")));
		
		if(loadFiles) {
			internship.setInternshipPlan(rs.getBytes("internshipPlan"));
			internship.setFinalReport(rs.getBytes("finalReport"));
		}
		
		return internship;
	}
	
	public boolean delete(int idUser, int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		Internship internship = this.findById(id);
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			boolean ret = stmt.execute("DELETE FROM internship WHERE idinternship = " + String.valueOf(id));
			
			new UpdateEvent(conn).registerDelete(idUser, internship);
			
			return ret;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<InternshipMissingDocumentsReport> getMissingDocumentsReport(int idDepartment, int year, int idStudent, int idSupervisor, int idCompany, int type, int status, boolean finalReportMissing) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT student.name AS studentName, supervisor.name AS supervisorName, companySupervisor.name AS companySupervisorName, " +
					"company.name AS companyName, internship.startDate, internship.endDate, internship.type, " +
					"CASE WHEN internship.endDate IS NULL OR internship.endDate > CURRENT_DATE THEN 0 ELSE 1 END AS status, " +
					"CASE WHEN internship.finalReport IS NOT NULL THEN 1 ELSE 0 END AS finalReport, " +
					"(SELECT COUNT(*) FROM internshipReport WHERE internshipReport.idinternship=internship.idinternship AND internshipReport.type=" + String.valueOf(ReportType.STUDENT.getValue()) + ") AS studentReport, " +  
					"(SELECT COUNT(*) FROM internshipReport WHERE internshipReport.idinternship=internship.idinternship AND internshipReport.type=" + String.valueOf(ReportType.SUPERVISOR.getValue()) + ") AS supervisorReport, " +
					"(SELECT COUNT(*) FROM internshipReport WHERE internshipReport.idinternship=internship.idinternship AND internshipReport.type=" + String.valueOf(ReportType.COMPANY.getValue()) + ") AS companySupervisorReport " +
					"FROM internship INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " +
					"INNER JOIN \"user\" companySupervisor ON companySupervisor.iduser=internship.idcompanysupervisor " +
					"INNER JOIN company ON company.idcompany=internship.idcompany " +
					"WHERE internship.iddepartment=" + String.valueOf(idDepartment) +
					(year > 0 ? " AND (YEAR(internship.startDate) >= " + String.valueOf(year) + " AND (YEAR(internship.endDate) <= " + String.valueOf(year) + " OR internship.endDate IS NULL)) " : "") +
					(idStudent > 0 ? " AND internship.idstudent = " + String.valueOf(idStudent) : "") +
					(idSupervisor > 0 ? " AND internship.idsupervisor = " + String.valueOf(idSupervisor) : "") +
					(idCompany > 0 ? " AND internship.idcompany = " + String.valueOf(idCompany) : "") +
					(type >= 0 ? " AND internship.type = " + String.valueOf(type) : "") +
					(status == 0 ? " AND (internship.endDate IS NULL OR internship.endDate >= CURRENT_DATE)" : (status == 1 ? " AND internship.endDate < CURRENT_DATE" : "")) +
					(finalReportMissing ? " AND internship.finalReport IS NULL" : "") +
					" ORDER BY internship.startDate DESC");
			
			List<InternshipMissingDocumentsReport> list = new ArrayList<InternshipMissingDocumentsReport>();
			
			while(rs.next()){
				InternshipMissingDocumentsReport report = new InternshipMissingDocumentsReport();
				
				report.setStudent(rs.getString("studentName"));
				report.setSupervisor(rs.getString("supervisorName"));
				report.setCompanySupervisor(rs.getString("companySupervisorName"));
				report.setCompany(rs.getString("companyName"));
				report.setStartDate(rs.getDate("startDate"));
				report.setEndDate(rs.getDate("endDate"));
				report.setFinalReport(rs.getInt("finalReport") == 1);
				report.setStudentReport(rs.getInt("studentReport"));
				report.setSupervisorReport(rs.getInt("supervisorReport"));
				report.setCompanySupervisorReport(rs.getInt("companySupervisorReport"));
				report.setStatus(InternshipStatus.valueOf(rs.getInt("status")));
				report.setType(InternshipType.valueOf(rs.getInt("type")));
				
				list.add(report);
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<InternshipByCompany> listInternshipByCompany(int idDepartment, int idCountry, int idState, int idCity, int type, int status, int companyStatus) throws SQLException {
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internship.idcompany, company.name, COUNT(*) AS total " +
					"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"LEFT JOIN city ON city.idcity=company.idcity " +
					"LEFT JOIN state ON state.idstate=city.idstate " +
					"WHERE internship.iddepartment=" + String.valueOf(idDepartment) +
					(idCountry > 0 ? " AND state.idcountry=" + String.valueOf(idCountry) : "") +
					(idState > 0 ? " AND city.idstate=" + String.valueOf(idState) : "") +
					(idCity > 0 ? " AND company.idcity=" + String.valueOf(idCity) : "") +
					(type == 0 || type == 1 ? " AND internship.type=" + String.valueOf(type) : "") +
					(status == 0 ? " AND (internship.endDate IS NULL OR internship.endDate >= CURRENT_DATE)" : (status == 1 ? " AND internship.endDate < CURRENT_DATE" : "")) +
					(companyStatus == 0 ? " AND company.agreement = ''" : (companyStatus == 1 ? " AND company.agreement <> ''" : "")) +
					" GROUP BY internship.idcompany, company.name ORDER BY total DESC");
			
			List<InternshipByCompany> list = new ArrayList<InternshipByCompany>();
			
			while(rs.next()){
				InternshipByCompany item = new InternshipByCompany();
				
				item.setIdCompany(rs.getInt("idcompany"));
				item.setCompanyName(rs.getString("name"));
				item.setTotalStudents(rs.getInt("total"));
				
				list.add(item);
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<InternshipByCompany> listInternshipByCity(int idDepartment, int idCountry, int idState, int type, int status, int companyStatus) throws SQLException {
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT company.idcity, city.name, COUNT(*) AS total " +
					"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"LEFT JOIN city ON city.idcity=company.idcity " +
					"LEFT JOIN state ON state.idstate=city.idstate " +
					"WHERE internship.iddepartment=" + String.valueOf(idDepartment) +
					(idCountry > 0 ? " AND state.idcountry=" + String.valueOf(idCountry) : "") +
					(idState > 0 ? " AND city.idstate=" + String.valueOf(idState) : "") +
					(type == 0 || type == 1 ? " AND internship.type=" + String.valueOf(type) : "") +
					(status == 0 ? " AND (internship.endDate IS NULL OR internship.endDate >= CURRENT_DATE)" : (status == 1 ? " AND internship.endDate < CURRENT_DATE" : "")) +
					(companyStatus == 0 ? " AND company.agreement = ''" : (companyStatus == 1 ? " AND company.agreement <> ''" : "")) +
					" GROUP BY company.idcity, city.name ORDER BY total DESC");
			
			List<InternshipByCompany> list = new ArrayList<InternshipByCompany>();
			
			while(rs.next()){
				InternshipByCompany item = new InternshipByCompany();
				
				item.setIdCompany(rs.getInt("idcity"));
				item.setCompanyName(rs.getString("name"));
				item.setTotalStudents(rs.getInt("total"));
				
				list.add(item);
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<InternshipByCompany> listInternshipByState(int idDepartment, int idCountry, int type, int status, int companyStatus) throws SQLException {
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT city.idstate, state.name, COUNT(*) AS total " +
					"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"LEFT JOIN city ON city.idcity=company.idcity " +
					"LEFT JOIN state ON state.idstate=city.idstate " +
					"WHERE internship.iddepartment=" + String.valueOf(idDepartment) +
					(idCountry > 0 ? " AND state.idcountry=" + String.valueOf(idCountry) : "") +
					(type == 0 || type == 1 ? " AND internship.type=" + String.valueOf(type) : "") +
					(status == 0 ? " AND (internship.endDate IS NULL OR internship.endDate >= CURRENT_DATE)" : (status == 1 ? " AND internship.endDate < CURRENT_DATE" : "")) +
					(companyStatus == 0 ? " AND company.agreement = ''" : (companyStatus == 1 ? " AND company.agreement <> ''" : "")) +
					" GROUP BY city.idstate, state.name ORDER BY total DESC");
			
			List<InternshipByCompany> list = new ArrayList<InternshipByCompany>();
			
			while(rs.next()){
				InternshipByCompany item = new InternshipByCompany();
				
				item.setIdCompany(rs.getInt("idstate"));
				item.setCompanyName(rs.getString("name"));
				item.setTotalStudents(rs.getInt("total"));
				
				list.add(item);
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<InternshipByCompany> listInternshipByCountry(int idDepartment, int type, int status, int companyStatus) throws SQLException {
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT state.idcountry, country.name, COUNT(*) AS total " +
					"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
					"LEFT JOIN city ON city.idcity=company.idcity " +
					"LEFT JOIN state ON state.idstate=city.idstate " +
					"LEFT JOIN country ON country.idcountry=state.idcountry " +
					"WHERE internship.iddepartment=" + String.valueOf(idDepartment) +
					(type == 0 || type == 1 ? " AND internship.type=" + String.valueOf(type) : "") +
					(status == 0 ? " AND (internship.endDate IS NULL OR internship.endDate >= CURRENT_DATE)" : (status == 1 ? " AND internship.endDate < CURRENT_DATE" : "")) +
					(companyStatus == 0 ? " AND company.agreement = ''" : (companyStatus == 1 ? " AND company.agreement <> ''" : "")) +
					" GROUP BY state.idcountry, country.name ORDER BY total DESC");
			
			List<InternshipByCompany> list = new ArrayList<InternshipByCompany>();
			
			while(rs.next()){
				InternshipByCompany item = new InternshipByCompany();
				
				item.setIdCompany(rs.getInt("idcountry"));
				item.setCompanyName(rs.getString("name"));
				item.setTotalStudents(rs.getInt("total"));
				
				list.add(item);
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public long getCurrentInternships() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(idInternship) AS total FROM internship WHERE endDate IS NULL OR endDate >= CURRENT_DATE");
			
			if(rs.next()) {
				return rs.getLong("total");
			} else {
				return 0;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public long getFinishedInternships() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(idInternship) AS total FROM internship WHERE endDate < CURRENT_DATE");
			
			if(rs.next()) {
				return rs.getLong("total");
			} else {
				return 0;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public InternshipListReport completeReport(Internship internship) throws Exception {
		InternshipListReport report = new InternshipListReport();
		
		report.setStudent(internship.getStudent().getName());
		report.setSupervisor(internship.getSupervisor().getName());
		report.setCompany(internship.getCompany().getName());
		report.setCompanySupervisor(internship.getCompanySupervisor().getName());
		report.setStartDate(internship.getStartDate());
		report.setEndDate(internship.getEndDate());
		report.setSei(internship.getSei());
		report.setType(internship.getType().toString());
		report.setStatus(internship.getStatus().toString());
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT student.studentcode, CASE WHEN internshipplan IS NULL THEN 0 ELSE 1 END AS internshiplan, CASE WHEN finalreport IS NULL THEN 0 ELSE 1 END AS finalreport, " +
					"(SELECT COUNT(*) FROM internshipreport WHERE internshipreport.idinternship=internship.idinternship AND internshipreport.type=0) AS studentreport, " +
					"(SELECT COUNT(*) FROM internshipreport WHERE internshipreport.idinternship=internship.idinternship AND internshipreport.type=1) AS supervisorreport, " + 
					"(SELECT COUNT(*) FROM internshipreport WHERE internshipreport.idinternship=internship.idinternship AND internshipreport.type=2) AS companysupervisorreport " + 
					"FROM internship INNER JOIN \"user\" student ON student.iduser=internship.idstudent WHERE idinternship=" + String.valueOf(internship.getIdInternship()));
			
			if(rs.next()) {
				report.setStudentCode(rs.getString("studentcode"));
				report.setInternshipPlan(rs.getInt("internshiplan") == 1);
				report.setFinalReport(rs.getInt("finalreport") == 1);
				report.setStudentReport(rs.getInt("studentreport"));
				report.setSupervisorReport(rs.getInt("supervisorreport"));
				report.setCompanySupervisorReport(rs.getInt("companysupervisorreport"));
			}
			
			rs.close();
			stmt.close();
			
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT idinternshipjury, date, sei FROM internshipjury WHERE idinternship=" + String.valueOf(internship.getIdInternship()));
			
			if(rs.next()) {
				report.setJurySei(rs.getString("sei"));
				report.setJuryDate(rs.getTimestamp("date"));
				
				report.setJuryScore(new InternshipJuryBO().getJuryFormReport(rs.getInt("idinternshipjury")).getFinalScore());
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
		
		return report;
	}
	
	public List<String> getTagsList(int idDepartment, boolean includeAll) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<String> ret = new ArrayList<String>();
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT tags FROM internship WHERE iddepartment=" + String.valueOf(idDepartment));
			
			while(rs.next()) {
				List<String> l = this.getTagsList(rs.getString("tags"));
				
				for(String s : l) {
					if(!ret.contains(s)) {
						ret.add(s);
					}
				}
			}
			
			Collections.sort(ret);
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
		
		if(includeAll) {
			ret.add(0, "(TODOS)");
		}
		
		return ret;
	}

}
