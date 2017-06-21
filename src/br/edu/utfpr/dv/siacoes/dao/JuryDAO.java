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

import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Thesis;

public class JuryDAO {
	
	public Jury findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM jury WHERE idJury = ?");
		
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public Jury findByProject(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM jury WHERE idProject = ?");
		
			stmt.setInt(1, idProject);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				Jury jury = new Jury();
				
				jury.setProject(new Project());
				jury.getProject().setIdProject(idProject);
				
				return jury;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public Jury findByThesis(int idThesis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM jury WHERE idThesis = ?");
		
			stmt.setInt(1, idThesis);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				Jury jury = new Jury();
				
				jury.setThesis(new Thesis());
				jury.getThesis().setIdThesis(idThesis);
				
				return jury;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Jury> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT jury.* " + 
					"FROM jury LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN proposal proposal1 ON proposal1.idProposal=project.idProposal " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project p ON p.idProject=thesis.idProject " + 
					"LEFT JOIN proposal proposal2 ON proposal2.idProposal=p.idProposal " +
					"WHERE (proposal1.idDepartment=" + String.valueOf(idDepartment) + " OR proposal2.idDepartment=" + String.valueOf(idDepartment) + ") AND (project.semester=" + String.valueOf(semester) + " OR thesis.semester=" + String.valueOf(semester) + ") AND (project.year=" + String.valueOf(year) + " OR thesis.year=" + String.valueOf(year) + ") ORDER BY jury.date");
			
			List<Jury> list = new ArrayList<Jury>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Jury> listByAppraiser(int idUser, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT jury.* FROM jury INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury LEFT JOIN project ON project.idProject=jury.idProject LEFT JOIN thesis ON thesis.idThesis=jury.idThesis WHERE juryappraiser.idAppraiser=" + String.valueOf(idUser) + " AND (project.semester=" + String.valueOf(semester) + " OR thesis.semester=" + String.valueOf(semester) + ") AND (project.year=" + String.valueOf(year) + " OR thesis.year=" + String.valueOf(year) + ") ORDER BY jury.date");
			
			List<Jury> list = new ArrayList<Jury>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Jury> listByStudent(int idUser, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT jury.* FROM jury INNER JOIN jurystudent ON jurystudent.idJury=jury.idJury LEFT JOIN project ON project.idProject=jury.idProject LEFT JOIN thesis ON thesis.idThesis=jury.idThesis WHERE jurystudent.idStudent=" + String.valueOf(idUser) + " AND (project.semester=" + String.valueOf(semester) + " OR thesis.semester=" + String.valueOf(semester) + ") AND (project.year=" + String.valueOf(year) + " OR thesis.year=" + String.valueOf(year) + ") ORDER BY jury.date");
			
			List<Jury> list = new ArrayList<Jury>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(Jury jury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			boolean insert = (jury.getIdJury() == 0);
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO jury(date, local, idProject, idThesis, comments, startTime, endTime, minimumScore) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				
				jury.setMinimumScore(10);
				
				Statement stmt2 = conn.createStatement();
				ResultSet rs;
				
				if(jury.getStage() == 2){
					rs = stmt2.executeQuery("SELECT idDepartment FROM thesis INNER JOIN project ON project.idProject=thesis.idProject " + 
						"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
						"WHERE thesis.idThesis=" + String.valueOf(jury.getThesis().getIdThesis()));
				}else{
					rs = stmt2.executeQuery("SELECT idDepartment FROM project INNER JOIN proposal ON proposal.idProposal=project.idProject " + 
						"WHERE project.idProject=" + String.valueOf(jury.getProject().getIdProject()));
				}
				
				if(rs.next()){
					rs = stmt2.executeQuery("SELECT minimumScore FROM sigetconfig WHERE idDepartment=" + String.valueOf(rs.getInt("idDepartment")));
					
					if(rs.next()){
						jury.setMinimumScore(rs.getDouble("minimumScore"));
					}
				}
			}else{
				stmt = conn.prepareStatement("UPDATE jury SET date=?, local=?, idProject=?, idThesis=?, comments=?, startTime=?, endTime=?, minimumScore=? WHERE idJury=?");
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
			
			if(!insert){
				stmt.setInt(9, jury.getIdJury());
			}
			
			stmt.execute();
			
			if(insert){
				ResultSet rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					jury.setIdJury(rs.getInt(1));
				}
			}
			
			if(jury.getAppraisers() != null){
				JuryAppraiserDAO dao = new JuryAppraiserDAO(conn);
				String ids = "";
				
				for(JuryAppraiser ja : jury.getAppraisers()){
					ja.setJury(jury);
					int paId = dao.save(ja);
					ids = ids + String.valueOf(paId) + ",";
				}
				
				if(!ids.isEmpty()){
					Statement st = conn.createStatement();
					st.execute("DELETE FROM juryappraiser WHERE idJury=" + String.valueOf(jury.getIdJury()) + " AND idJuryAppraiser NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")");
				}
			}
			
			if(jury.getParticipants() != null){
				JuryStudentDAO dao = new JuryStudentDAO(conn);
				String ids = "";
				
				for(JuryStudent js : jury.getParticipants()){
					if((js.getStudent() != null) && (js.getStudent().getIdUser() != 0)){
						js.setJury(jury);
						int paId = dao.save(js);
						ids = ids + String.valueOf(paId) + ",";	
					}
				}
				
				if(!ids.isEmpty()){
					Statement st = conn.createStatement();
					st.execute("DELETE FROM jurystudent WHERE idJury=" + String.valueOf(jury.getIdJury()) + " AND idJuryStudent NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")");
				}
			}
			
			conn.commit();
			
			return jury.getIdJury();
		}catch(SQLException e){
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
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
		
		return jury;
	}
	
	public boolean hasAllScores(int idJury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) AS total FROM juryappraiser WHERE idJury=?");
			stmt.setInt(1, idJury);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			int numAppraisers = rs.getInt("total");
			
			stmt.close();
			stmt = conn.prepareStatement("SELECT COUNT(DISTINCT juryappraiser.idJuryAppraiser) AS total FROM juryappraiserscore INNER JOIN juryappraiser ON juryappraiser.idJuryAppraiser=juryappraiserscore.idJuryAppraiser WHERE idJury=?");
			stmt.setInt(1, idJury);
			
			rs = stmt.executeQuery();
			rs.next();
			int numScores = rs.getInt("total");
			
			return (numScores >= numAppraisers);
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public boolean isApproved(int idJury) throws Exception{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(!this.hasAllScores(idJury)){
				throw new Exception("Ainda não foram lançadas todas as notas.");
			}
			
			stmt = conn.prepareStatement(
					"SELECT SUM(juryappraiserscore.score * evaluationitem.ponderosity) / SUM(evaluationitem.ponderosity) AS score" +
					"FROM juryappraiserscore INNER JOIN evaluationitem ON evaluationitem.idEvaluationItem=juryappraiserscore.idEvaluationItem " +
					"INNER JOIN juryappraiser ON juryappraiser.idJuryAppraiser=juryappraiserscore.idJuryAppraiser " +
					"WHERE juryappraiser.idJury=? GROUP BY juryappraiser.idJuryAppraiser");
			stmt.setInt(1, idJury);
			
			ResultSet rs = stmt.executeQuery();
			double sumScore=0;
			int countScore=0;
			while(rs.next()){
				sumScore = sumScore + this.round(rs.getDouble("score"));
				countScore++;
			}
			
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
	
}
