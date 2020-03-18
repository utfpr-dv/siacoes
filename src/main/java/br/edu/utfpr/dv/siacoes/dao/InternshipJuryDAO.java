package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryStudent;
import br.edu.utfpr.dv.siacoes.model.JuryStudentReport;
import br.edu.utfpr.dv.siacoes.model.SigesConfig.JuryFormat;
import br.edu.utfpr.dv.siacoes.model.Jury.JuryResult;

public class InternshipJuryDAO {
	
	public InternshipJury findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM internshipjury WHERE idInternshipJury = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
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
	
	public InternshipJury findByInternship(int idInternship) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM internshipjury WHERE idInternship = ?");
		
			stmt.setInt(1, idInternship);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
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
	
	public List<InternshipJury> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT internshipjury.* " + 
					"FROM internshipjury INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " +
					"WHERE internship.idDepartment=" + String.valueOf(idDepartment) + " AND MONTH(internshipjury.date) " + (semester == 1 ? "<= 7 " : " > 7") + " AND YEAR(internshipjury.date)=" + String.valueOf(year) + " ORDER BY internshipjury.date");
			
			List<InternshipJury> list = new ArrayList<InternshipJury>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<InternshipJury> listByAppraiser(int idUser, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT internshipjury.* " +
					"FROM internshipjury INNER JOIN internshipjuryappraiser ON internshipjuryappraiser.idInternshipJury=internshipjury.idInternshipJury " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " +
					"WHERE internshipjuryappraiser.idAppraiser=" + String.valueOf(idUser) + 
					(((semester > 0) && (year > 0)) ? " AND MONTH(internshipjury.date) " + (semester == 1 ? "<= 7 " : " > 7") + " AND YEAR(internshipjury.date)=" + String.valueOf(year) : "" ) + 
					" ORDER BY internshipjury.date");
			
			List<InternshipJury> list = new ArrayList<InternshipJury>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<InternshipJury> listByStudent(int idUser, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT internshipjury.* " +
					"FROM internshipjury INNER JOIN internshipjurystudent ON internshipjurystudent.idInternshipJury=internshipjury.idInternshipJury " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " +
					"WHERE internshipjurystudent.idStudent=" + String.valueOf(idUser) + 
					(((semester > 0) && (year > 0)) ? " AND MONTH(internshipjury.date) " + (semester == 1 ? "<= 7 " : " > 7") + " AND YEAR(internshipjury.date)=" + String.valueOf(year) : "" ) + 
					" ORDER BY internshipjury.date");
			
			List<InternshipJury> list = new ArrayList<InternshipJury>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int findIdDepartment(int idInternshipJury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internship.iddepartment FROM internshipjury " +
					"INNER JOIN internship ON internship.idinternship=internshipjury.idinternship " +
					"WHERE internshipjury.idinternshipjury=?");
		
			stmt.setInt(1, idInternshipJury);
			
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public boolean saveSupervisorScore(int idUser, int idInternshipJury, double score) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("UPDATE internshipjury SET supervisorscore=? WHERE idinternshipjury=?");
			
			stmt.setDouble(1, score);
			stmt.setInt(2, idInternshipJury);
			
			return stmt.execute();
		} finally {
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(int idUser, InternshipJury jury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			boolean insert = (jury.getIdInternshipJury() == 0);
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO internshipjury(date, local, idInternship, comments, startTime, endTime, minimumScore, supervisorPonderosity, companySupervisorPonderosity, companySupervisorScore, result, supervisorAbsenceReason, supervisorScore, supervisorFillJuryForm, juryformat) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				
				jury.setMinimumScore(10);
				
				Statement stmt2 = conn.createStatement();
				
				rs = stmt2.executeQuery("SELECT idDepartment FROM internship WHERE internship.idInternship=" + String.valueOf(jury.getInternship().getIdInternship()));
				
				if(rs.next()){
					int idDepartment = rs.getInt("idDepartment");
					
					rs.close();
					rs = stmt2.executeQuery("SELECT minimumScore, supervisorPonderosity, companySupervisorPonderosity FROM sigesconfig WHERE idDepartment=" + String.valueOf(idDepartment));
					
					if(rs.next()){
						jury.setMinimumScore(rs.getDouble("minimumScore"));
						jury.setSupervisorPonderosity(rs.getDouble("supervisorPonderosity"));
						jury.setCompanySupervisorPonderosity(rs.getDouble("companySupervisorPonderosity"));
					}
				}
			}else{
				stmt = conn.prepareStatement("UPDATE internshipjury SET date=?, local=?, idInternship=?, comments=?, startTime=?, endTime=?, minimumScore=?, supervisorPonderosity=?, companySupervisorPonderosity=?, companySupervisorScore=?, result=?, supervisorAbsenceReason=?, supervisorScore=?, supervisorFillJuryForm=?, juryformat=? WHERE idInternshipJury=?");
			}
			
			stmt.setTimestamp(1, new java.sql.Timestamp(jury.getDate().getTime()));
			stmt.setString(2, jury.getLocal());
			stmt.setInt(3, jury.getInternship().getIdInternship());
			stmt.setString(4, jury.getComments());
			stmt.setTime(5, new java.sql.Time(jury.getStartTime().getTime()));
			stmt.setTime(6, new java.sql.Time(jury.getEndTime().getTime()));
			stmt.setDouble(7, jury.getMinimumScore());
			stmt.setDouble(8, jury.getSupervisorPonderosity());
			stmt.setDouble(9, jury.getCompanySupervisorPonderosity());
			stmt.setDouble(10, jury.getCompanySupervisorScore());
			stmt.setInt(11, jury.getResult().getValue());
			stmt.setString(12, jury.getSupervisorAbsenceReason());
			stmt.setDouble(13, jury.getSupervisorScore());
			stmt.setInt(14, jury.isSupervisorFillJuryForm() ? 1 : 0);
			stmt.setInt(15, jury.getJuryFormat().getValue());
			
			if(!insert){
				stmt.setInt(16, jury.getIdInternshipJury());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					jury.setIdInternshipJury(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, jury);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, jury);
			}
			
			if(jury.getAppraisers() != null){
				InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO(conn);
				String ids = "";
				
				for(InternshipJuryAppraiser ja : jury.getAppraisers()){
					ja.setInternshipJury(jury);
					int paId = dao.save(idUser, ja);
					ids = ids + String.valueOf(paId) + ",";
				}
				
				Statement st = conn.createStatement();
				st.execute("DELETE FROM internshipjuryappraiser WHERE idInternshipJury=" + String.valueOf(jury.getIdInternshipJury()) + 
						(ids.isEmpty() ? "" : " AND idInternshipJuryAppraiser NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")"));
			}
			
			if(jury.getParticipants() != null){
				InternshipJuryStudentDAO dao = new InternshipJuryStudentDAO(conn);
				String ids = "";
				
				for(InternshipJuryStudent js : jury.getParticipants()){
					if((js.getStudent() != null) && (js.getStudent().getIdUser() != 0)){
						js.setInternshipJury(jury);
						int paId = dao.save(idUser, js);
						ids = ids + String.valueOf(paId) + ",";	
					}
				}
				
				Statement st = conn.createStatement();
				st.execute("DELETE FROM internshipjurystudent WHERE idInternshipJury=" + String.valueOf(jury.getIdInternshipJury()) + 
						(ids.isEmpty() ? "" : " AND idInternshipJuryStudent NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")"));
			}
			
			conn.commit();
			
			return jury.getIdInternshipJury();
		}catch(SQLException e){
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private InternshipJury loadObject(ResultSet rs) throws SQLException{
		InternshipJury jury = new InternshipJury();
		InternshipDAO dao = new InternshipDAO();
		
		jury.setIdInternshipJury(rs.getInt("idInternshipJury"));
		jury.setDate(rs.getTimestamp("date"));
		jury.setLocal(rs.getString("local"));
		jury.setComments(rs.getString("comments"));
		jury.setStartTime(rs.getTime("startTime"));
		jury.setEndTime(rs.getTime("endTime"));
		jury.setInternship(dao.findById(rs.getInt("idInternship")));
		jury.setMinimumScore(rs.getDouble("minimumScore"));
		jury.setSupervisorPonderosity(rs.getDouble("supervisorPonderosity"));
		jury.setCompanySupervisorPonderosity(rs.getDouble("companySupervisorPonderosity"));
		jury.setCompanySupervisorScore(rs.getDouble("companySupervisorScore"));
		jury.setResult(JuryResult.valueOf(rs.getInt("result")));
		jury.setSupervisorAbsenceReason(rs.getString("supervisorAbsenceReason"));
		jury.setSupervisorScore(rs.getDouble("supervisorScore"));
		jury.setSupervisorFillJuryForm(rs.getInt("supervisorFillJuryForm") == 1);
		jury.setJuryFormat(JuryFormat.valueOf(rs.getInt("juryformat")));
		
		return jury;
	}
	
	public boolean hasScores(int idInternshipJury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM internshipjuryappraiser " + 
					"INNER JOIN internshipjuryappraiserscore ON internshipjuryappraiserscore.idinternshipjuryappraiser=internshipjuryappraiser.idinternshipjuryappraiser " + 
					"WHERE internshipjuryappraiser.idinternshipjury=?");
			stmt.setInt(1, idInternshipJury);
			
			rs = stmt.executeQuery();
			rs.next();
			return rs.getInt("total") > 0;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public boolean hasAllScores(int idInternshipJury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM internshipjuryappraiser WHERE idInternshipJury=?");
			stmt.setInt(1, idInternshipJury);
			
			rs = stmt.executeQuery();
			rs.next();
			int numAppraisers = rs.getInt("total");
			rs.close();
			
			stmt.close();
			stmt = conn.prepareStatement("SELECT supervisorFillJuryForm, supervisorScore, companySupervisorScore FROM internshipJury WHERE idInternshipJury=?");
			stmt.setInt(1, idInternshipJury);
			
			rs = stmt.executeQuery();
			rs.next();
			if(rs.getInt("companySupervisorScore") <= 0) {
				return false;
			}
			if((rs.getInt("supervisorFillJuryForm") == 0) && (rs.getDouble("supervisorScore") > 0)) {
				numAppraisers = numAppraisers - 1;
			}
			rs.close();
			
			stmt.close();
			stmt = conn.prepareStatement("SELECT COUNT(DISTINCT internshipjuryappraiser.idInternshipJuryAppraiser) AS total FROM internshipjuryappraiserscore INNER JOIN internshipjuryappraiser ON internshipjuryappraiser.idInternshipJuryAppraiser=internshipjuryappraiserscore.idInternshipJuryAppraiser WHERE idInternshipJury=?");
			stmt.setInt(1, idInternshipJury);
			
			rs = stmt.executeQuery();
			rs.next();
			int numScores = rs.getInt("total");
			
			return (numScores >= numAppraisers);
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<JuryStudentReport> listJuryStudentReport(int idDepartment, int idInternshipJury, Date startDate, Date endDate, boolean orderByDate) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String filter = "internshipjury.idInternshipJury=" + String.valueOf(idInternshipJury);
		
		if(idInternshipJury <= 0) {
			filter = "internship.idDepartment=" + String.valueOf(idDepartment) + " AND internshipjury.date BETWEEN ? AND ?";
		}
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internshipjurystudent.idInternshipJury, internshipjurystudent.idStudent, student.name AS studentName, student.studentCode, " + 
					"internshipjury.date, internshipjury.starttime, internshipjury.endtime, internship.idStudent AS idJuryStudent, student2.name AS juryStudentName " + 
					"FROM internshipjurystudent INNER JOIN \"user\" student ON student.idUser=internshipjurystudent.idStudent " + 
					"INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjurystudent.idInternshipJury " + 
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
					"INNER JOIN \"user\" student2 ON student2.idUser=internship.idStudent " + 
					"WHERE " + filter + " ORDER BY " + (orderByDate ? "internshipjury.date, internshipjury.startTime, student.name" : "student.name, internshipjury.date, internshipjury.startTime"));
			
			if(idInternshipJury <= 0) {
				stmt.setDate(1, new java.sql.Date(startDate.getTime()));
				stmt.setDate(2, new java.sql.Date(endDate.getTime()));
			}
			
			rs = stmt.executeQuery();
			
			List<JuryStudentReport> list = new ArrayList<JuryStudentReport>();
			
			while(rs.next()) {
				JuryStudentReport item = new JuryStudentReport();
				
				item.setIdJury(rs.getInt("idInternshipJury"));
				item.setIdStudent(rs.getInt("idStudent"));
				item.setIdJuryStudent(rs.getInt("idJuryStudent"));
				item.setStudentName(rs.getString("studentName"));
				item.setStudentCode(rs.getString("studentCode"));
				item.setJuryStudentName(rs.getString("juryStudentName"));
				item.setDate(rs.getDate("date"));
				item.setStartTime(rs.getTime("startTime"));
				item.setEndTime(rs.getTime("endTime"));
				item.setStage(0);
				
				list.add(item);
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}

}
