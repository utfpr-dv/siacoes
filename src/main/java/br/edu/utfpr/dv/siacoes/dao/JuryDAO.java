package br.edu.utfpr.dv.siacoes.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryBySemester;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.JuryStudentReport;

public class JuryDAO {
	
	public Jury findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM jury WHERE idJury = ?");
		
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
	
	public Jury findByProject(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM jury WHERE idProject = ?");
		
			stmt.setInt(1, idProject);
			
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
	
	public Jury findByThesis(int idThesis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM jury WHERE idThesis = ?");
		
			stmt.setInt(1, idThesis);
			
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
	
	public int findIdDepartment(int idJury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.iddepartment FROM jury " +
					"LEFT JOIN thesis ON thesis.idthesis=jury.idthesis " +
					"LEFT JOIN project ON project.idproject=jury.idproject OR project.idproject=thesis.idproject " +
					"LEFT JOIN proposal ON proposal.idproposal=project.idproposal " +
					"WHERE jury.idjury=?");
		
			stmt.setInt(1, idJury);
			
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
	
	public List<Jury> listBySemester(int idDepartment, int semester, int year, int stage) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT jury.* " + 
					"FROM jury LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN proposal proposal1 ON proposal1.idProposal=project.idProposal " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project p ON p.idProject=thesis.idProject " + 
					"LEFT JOIN proposal proposal2 ON proposal2.idProposal=p.idProposal " +
					"WHERE (proposal1.idDepartment=" + String.valueOf(idDepartment) + " OR proposal2.idDepartment=" + String.valueOf(idDepartment) + 
						") AND (project.semester=" + String.valueOf(semester) + " OR thesis.semester=" + String.valueOf(semester) + 
						") AND (project.year=" + String.valueOf(year) + " OR thesis.year=" + String.valueOf(year) + 
						") " + (stage == 2 ? " AND jury.idThesis IS NOT NULL " : (stage == 1 ? " AND jury.idProject IS NOT NULL " : "")) +
					"ORDER BY jury.date");
			
			List<Jury> list = new ArrayList<Jury>();
			
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
	
	public List<Jury> listByAppraiser(int idUser, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT jury.* FROM jury " +
					"INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury " +
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " +
					"WHERE juryappraiser.idAppraiser=" + String.valueOf(idUser) + 
					(((semester > 0) && (year > 0)) ? 
					" AND (project.semester=" + String.valueOf(semester) + " OR thesis.semester=" + String.valueOf(semester) + 
					") AND (project.year=" + String.valueOf(year) + " OR thesis.year=" + String.valueOf(year) + ")" : "") +
					" ORDER BY jury.date");
			
			List<Jury> list = new ArrayList<Jury>();
			
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
	
	public List<Jury> listByStudent(int idUser, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT jury.* FROM jury " +
					"INNER JOIN jurystudent ON jurystudent.idJury=jury.idJury " +
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " +
					"WHERE jurystudent.idStudent=" + String.valueOf(idUser) + 
					(((semester > 0) && (year > 0)) ? 
					" AND (project.semester=" + String.valueOf(semester) + " OR thesis.semester=" + String.valueOf(semester) + 
					") AND (project.year=" + String.valueOf(year) + " OR thesis.year=" + String.valueOf(year) + ")" : "") +
					" ORDER BY jury.date");
			
			List<Jury> list = new ArrayList<Jury>();
			
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
	
	public int save(int idUser, Jury jury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			boolean insert = (jury.getIdJury() == 0);
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO jury(date, local, idProject, idThesis, comments, startTime, endTime, minimumScore, supervisorAbsenceReason, supervisorAssignsGrades) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				
				jury.setMinimumScore(10);
				
				Statement stmt2 = conn.createStatement();
				
				if(jury.getStage() == 2){
					rs = stmt2.executeQuery("SELECT idDepartment FROM thesis INNER JOIN project ON project.idProject=thesis.idProject " + 
						"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
						"WHERE thesis.idThesis=" + String.valueOf(jury.getThesis().getIdThesis()));
				}else{
					rs = stmt2.executeQuery("SELECT idDepartment FROM project INNER JOIN proposal ON proposal.idProposal=project.idProject " + 
						"WHERE project.idProject=" + String.valueOf(jury.getProject().getIdProject()));
				}
				
				if(rs.next()){
					int idDepartment = rs.getInt("idDepartment");
					
					rs.close();
					rs = stmt2.executeQuery("SELECT minimumScore FROM sigetconfig WHERE idDepartment=" + String.valueOf(idDepartment));
					
					if(rs.next()){
						jury.setMinimumScore(rs.getDouble("minimumScore"));
					}
				}
				
				rs.close();
			}else{
				stmt = conn.prepareStatement("UPDATE jury SET date=?, local=?, idProject=?, idThesis=?, comments=?, startTime=?, endTime=?, minimumScore=?, supervisorAbsenceReason=?, supervisorAssignsGrades=? WHERE idJury=?");
			}
			
			stmt.setTimestamp(1, new java.sql.Timestamp(jury.getDate().getTime()));
			stmt.setString(2, jury.getLocal());
			if((jury.getProject() == null) || (jury.getProject().getIdProject() == 0)){
				stmt.setNull(3, Types.INTEGER);
			}else{
				stmt.setInt(3, jury.getProject().getIdProject());
			}
			if((jury.getThesis() == null) || (jury.getThesis().getIdThesis() == 0)){
				stmt.setNull(4, Types.INTEGER);
			}else{
				stmt.setInt(4, jury.getThesis().getIdThesis());
			}
			stmt.setString(5, jury.getComments());
			stmt.setTime(6, new java.sql.Time(jury.getStartTime().getTime()));
			stmt.setTime(7, new java.sql.Time(jury.getEndTime().getTime()));
			stmt.setDouble(8, jury.getMinimumScore());
			stmt.setString(9, jury.getSupervisorAbsenceReason());
			stmt.setInt(10, (jury.isSupervisorAssignsGrades() ? 1 : 0));
			
			if(!insert){
				stmt.setInt(11, jury.getIdJury());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					jury.setIdJury(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, jury);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, jury);
			}
			
			if(jury.getAppraisers() != null){
				JuryAppraiserDAO dao = new JuryAppraiserDAO(conn);
				String ids = "";
				
				for(JuryAppraiser ja : jury.getAppraisers()){
					ja.setJury(jury);
					int paId = dao.save(idUser, ja);
					ids = ids + String.valueOf(paId) + ",";
				}
				
				Statement st = conn.createStatement();
				st.execute("DELETE FROM juryappraiser WHERE idJury=" + String.valueOf(jury.getIdJury()) + 
						(ids.isEmpty() ? "" : " AND idJuryAppraiser NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")"));
				st.close();
			}
			
			if(jury.getParticipants() != null){
				JuryStudentDAO dao = new JuryStudentDAO(conn);
				String ids = "";
				
				for(JuryStudent js : jury.getParticipants()){
					if((js.getStudent() != null) && (js.getStudent().getIdUser() != 0)){
						js.setJury(jury);
						int paId = dao.save(idUser, js);
						ids = ids + String.valueOf(paId) + ",";	
					}
				}
			
				Statement st = conn.createStatement();
				st.execute("DELETE FROM jurystudent WHERE idJury=" + String.valueOf(jury.getIdJury()) + 
						(ids.isEmpty() ? "" : " AND idJuryStudent NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")"));
				st.close();
			}
			
			if((jury.getJuryRequest() != null) && (jury.getJuryRequest().getIdJuryRequest() != 0)) {
				Statement st = conn.createStatement();
				st.execute("UPDATE juryrequest SET idJury=" + String.valueOf(jury.getIdJury()) + " WHERE idJuryRequest=" + String.valueOf(jury.getJuryRequest().getIdJuryRequest()));
				st.close();
			}
			
			conn.commit();
			
			return jury.getIdJury();
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
	
	private Jury loadObject(ResultSet rs) throws SQLException{
		Jury jury = new Jury();
		
		jury.setIdJury(rs.getInt("idJury"));
		jury.setDate(rs.getTimestamp("date"));
		jury.setLocal(rs.getString("local"));
		jury.setComments(rs.getString("comments"));
		jury.setStartTime(rs.getTime("startTime"));
		jury.setEndTime(rs.getTime("endTime"));
		if(rs.getInt("idProject") == 0){
			jury.setProject(null);
		}else{
			ProjectDAO dao = new ProjectDAO();
			jury.setProject(dao.findById(rs.getInt("idProject")));
		}
		if(rs.getInt("idThesis") == 0){
			jury.setThesis(null);
		}else{
			ThesisDAO dao = new ThesisDAO();
			jury.setThesis(dao.findById(rs.getInt("idThesis")));
		}
		jury.setMinimumScore(rs.getDouble("minimumScore"));
		jury.setSupervisorAbsenceReason(rs.getString("supervisorAbsenceReason"));
		jury.setSupervisorAssignsGrades(rs.getInt("supervisorAssignsGrades") == 1);
		
		return jury;
	}
	
	public boolean hasScores(int idJury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM juryappraiser " + 
					"INNER JOIN juryappraiserscore ON juryappraiserscore.idjuryappraiser=juryappraiser.idjuryappraiser " + 
					"WHERE juryappraiser.idjury=?");
			stmt.setInt(1, idJury);
			
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
	
	public boolean hasAllScores(int idJury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(juryappraiser.idJuryAppraiser) AS total FROM juryappraiser " +
					"INNER JOIN jury ON jury.idJury=juryappraiser.idJury " +
					"WHERE juryappraiser.substitute=0 AND (jury.supervisorAssignsGrades=1 OR juryappraiser.chair=0) AND jury.idJury=?");
			stmt.setInt(1, idJury);
			
			rs = stmt.executeQuery();
			rs.next();
			int numAppraisers = rs.getInt("total");
			rs.close();
			stmt.close();
			
			stmt = conn.prepareStatement("SELECT COUNT(DISTINCT juryappraiser.idJuryAppraiser) AS total FROM juryappraiserscore INNER JOIN juryappraiser ON juryappraiser.idJuryAppraiser=juryappraiserscore.idJuryAppraiser WHERE juryappraiser.substitute=0 AND juryappraiser.idJury=?");
			stmt.setInt(1, idJury);
			
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
	
	public boolean isApproved(int idJury) throws Exception{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(!this.hasAllScores(idJury)){
				throw new Exception("Ainda não foram lançadas todas as notas.");
			}
			
			stmt = conn.prepareStatement(
					"SELECT SUM(juryappraiserscore.score) AS score " +
					"FROM juryappraiserscore INNER JOIN juryappraiser ON juryappraiser.idJuryAppraiser=juryappraiserscore.idJuryAppraiser " +
					"INNER JOIN jury ON jury.idJury=juryappraiser.idJury " + 
					"WHERE juryappraiser.substitute=0 AND (jury.supervisorAssignsGrades=1 OR juryappraiser.chair=0) AND jury.idJury=? GROUP BY juryappraiser.idJuryAppraiser");
			stmt.setInt(1, idJury);
			
			rs = stmt.executeQuery();
			double sumScore=0;
			int countScore=0;
			while(rs.next()){
				sumScore = sumScore + this.round(rs.getDouble("score"));
				countScore++;
			}
			rs.close();
			
			sumScore = this.round(sumScore / countScore);
			
			double minimumScore = 10;
			stmt.close();
			stmt = conn.prepareStatement("SELECT minimumScore FROM jury WHERE idJury=?");
			stmt.setInt(1, idJury);
			
			rs = stmt.executeQuery();
			if(rs.next()){
				minimumScore = rs.getDouble("minimumScore");
			}
			
			return (sumScore >= minimumScore);
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private double round(double value){
		BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(1, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public List<JuryBySemester> listJuryBySemester(int idDepartment, int initialYear, int finalYear) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT semester, year, SUM(total1) AS total1, SUM(total2) AS total2 FROM (" + 
					"SELECT project.semester, project.year, COUNT(*) AS total1, 0 AS total2 " + 
					"FROM project INNER JOIN jury ON jury.idProject=project.idProject " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"WHERE proposal.idDepartment=" + String.valueOf(idDepartment) + " AND project.year BETWEEN " + String.valueOf(initialYear) + " AND " + String.valueOf(finalYear) +
					" GROUP BY project.semester, project.year " + 
					" UNION ALL " + 
					"SELECT thesis.semester, thesis.year, 0 AS total1, COUNT(*) AS total2 " + 
					"FROM thesis INNER JOIN jury ON jury.idThesis=thesis.idThesis " + 
					"INNER JOIN project ON project.idProject=thesis.idProject " + 
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"WHERE proposal.idDepartment=" + String.valueOf(idDepartment) + " AND thesis.year BETWEEN " + String.valueOf(initialYear) + " AND " + String.valueOf(finalYear) +
					" GROUP BY thesis.semester, thesis.year) temp " + 
					"GROUP BY semester, year " + 
					"ORDER BY year, semester");
			
			List<JuryBySemester> list = new ArrayList<JuryBySemester>();
			
			while(rs.next()){
				JuryBySemester item = new JuryBySemester();
				
				item.setYear(rs.getInt("year"));
				item.setSemester(rs.getInt("semester"));
				item.setJuryStage1(rs.getInt("total1"));
				item.setJuryStage2(rs.getInt("total2"));
				
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
	
	public long getTotalJury() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT SUM(total) AS total FROM(SELECT COUNT(idjury) AS total FROM jury UNION ALL SELECT COUNT(idinternshipjury) AS total FROM internshipjury) AS temp");
			
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
	
	public List<JuryStudentReport> listJuryStudentReport(int idDepartment, int idJury, int semester, int year, boolean orderByDate) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String filter = "jury.idJury=" + String.valueOf(idJury);
		
		if(idJury <= 0) {
			filter = "(proposal.idDepartment=" + String.valueOf(idDepartment) + " OR proposal2.idDepartment=" + String.valueOf(idDepartment) + 
					") AND (project.semester=" + String.valueOf(semester) + " OR thesis.semester=" + String.valueOf(semester) + 
					") AND (project.year=" + String.valueOf(year) + " OR thesis.year=" + String.valueOf(year) + ")";
		}
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT jurystudent.idJury, jurystudent.idStudent, student.name AS studentName, student.studentCode, " + 
					"jury.date, jury.starttime, jury.endtime, COALESCE(project.idStudent, thesis.idStudent) AS idJuryStudent, COALESCE(student2.name, student3.name) AS juryStudentName " + 
					"FROM jurystudent INNER JOIN \"user\" student ON student.idUser=jurystudent.idStudent " + 
					"INNER JOIN jury ON jury.idJury=jurystudent.idJury " + 
					"LEFT JOIN project ON project.idProject=jury.idProject " + 
					"LEFT JOIN \"user\" student2 ON student2.idUser=project.idStudent " + 
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN \"user\" student3 ON student3.idUser=thesis.idStudent " +
					"LEFT JOIN project project2 ON project2.idProject=thesis.idProject " +
					"LEFT JOIN proposal ON proposal.idProposal=project.idProposal " +
					"LEFT JOIN proposal proposal2 ON proposal2.idProposal=project2.idProposal " +
					"WHERE " + filter + " ORDER BY " + (orderByDate ? "jury.date, jury.startTime, student.name" : "student.name, jury.date, jury.startTime"));
			
			List<JuryStudentReport> list = new ArrayList<JuryStudentReport>();
			
			while(rs.next()) {
				JuryStudentReport item = new JuryStudentReport();
				
				item.setIdJury(rs.getInt("idJury"));
				item.setIdStudent(rs.getInt("idStudent"));
				item.setIdJuryStudent(rs.getInt("idJuryStudent"));
				item.setStudentName(rs.getString("studentName"));
				item.setStudentCode(rs.getString("studentCode"));
				item.setJuryStudentName(rs.getString("juryStudentName"));
				item.setDate(rs.getDate("date"));
				item.setStartTime(rs.getTime("startTime"));
				item.setEndTime(rs.getTime("endTime"));
				item.setStage(1);
				
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
