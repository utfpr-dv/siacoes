package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryRequest;

public class InternshipJuryRequestDAO {

	public InternshipJuryRequest findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internshipjuryrequest.*, internship.idStudent, internship.idCompany, internship.idSupervisor, " +
					"company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipjuryrequest INNER JOIN internship ON internship.idinternship=internshipjuryrequest.idinternship " +
					"INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"WHERE internshipjuryrequest.idInternshipJuryRequest = ?");
		
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
	
	public InternshipJuryRequest findByInternship(int idInternship) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internshipjuryrequest.*, internship.idStudent, internship.idCompany, internship.idSupervisor, " +
					"company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipjuryrequest INNER JOIN internship ON internship.idinternship=internshipjuryrequest.idinternship " +
					"INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"WHERE internshipjuryrequest.idInternship = ?");
		
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
	
	public int findIdDepartment(int idInternshipJuryRequest) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internship.iddepartment, internship.idStudent, internship.idCompany, internship.idSupervisor, " +
					"company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipjuryrequest INNER JOIN internship ON internship.idinternship=internshipjuryrequest.idinternship " +
					"INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"WHERE internshipjuryrequest.idinternshipjuryrequest=?");
		
			stmt.setInt(1, idInternshipJuryRequest);
			
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
	
	public List<InternshipJuryRequest> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT internshipjuryrequest.*, internship.idStudent, internship.idCompany, internship.idSupervisor, " +
					"company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipjuryrequest INNER JOIN internship ON internship.idinternship=internshipjuryrequest.idinternship " +
					"INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"INNER JOIN department ON department.idDepartment=internship.idDepartment " + 
					"INNER JOIN semester ON (semester.idCampus=department.idCampus AND semester.year=" + String.valueOf(year) + " AND semester.semester=" + String.valueOf(semester) + ") " + 
					"WHERE internshipjuryrequest.date BETWEEN semester.startDate AND semester.endDate AND internship.idDepartment=" + String.valueOf(idDepartment) + 
					" ORDER BY date");
			
			List<InternshipJuryRequest> list = new ArrayList<InternshipJuryRequest>();
			
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
	
	public List<InternshipJuryRequest> listByAppraiser(int idUser, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT internshipjuryrequest.*, internship.idStudent, internship.idCompany, internship.idSupervisor, " +
					"company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
					"FROM internshipjuryrequest INNER JOIN internshipjuryappraiserrequest ON internshipjuryappraiserrequest.idInternshipJuryRequest=internshipjuryrequest.idInternshipJuryRequest " +
					"INNER JOIN internship ON internship.idInternship=internshipjuryrequest.idInternship " +
					"INNER JOIN company ON company.idcompany=internship.idcompany " +
					"INNER JOIN \"user\" student ON student.iduser=internship.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=internship.idsupervisor " + 
					"INNER JOIN department ON department.idDepartment=internship.idDepartment " + 
					"INNER JOIN semester ON (semester.idCampus=department.idCampus AND semester.year=" + String.valueOf(year) + " AND semester.semester=" + String.valueOf(semester) + ") " + 
					"WHERE internshipjuryrequest.date BETWEEN semester.startDate AND semester.endDate AND internshipjuryappraiserrequest.idAppraiser=" + String.valueOf(idUser) + 
					" ORDER BY date");
			
			List<InternshipJuryRequest> list = new ArrayList<InternshipJuryRequest>();
			
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
	
	public int save(int idUser, InternshipJuryRequest jury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			boolean insert = (jury.getIdInternshipJuryRequest() == 0);
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO internshipjuryrequest(date, local, idInternship, comments, idInternshipJury) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE internshipjuryrequest SET date=?, local=?, idInternship=?, comments=?, idInternshipJury=? WHERE idInternshipJuryRequest=?");
			}
			
			stmt.setTimestamp(1, new java.sql.Timestamp(jury.getDate().getTime()));
			stmt.setString(2, jury.getLocal());
			stmt.setInt(3, jury.getInternship().getIdInternship());
			stmt.setString(4, jury.getComments());
			if((jury.getInternshipJury() == null) || (jury.getInternshipJury().getIdInternshipJury() == 0)){
				stmt.setNull(5, Types.INTEGER);
			}else{
				stmt.setInt(5, jury.getInternshipJury().getIdInternshipJury());
			}
			
			if(!insert){
				stmt.setInt(6, jury.getIdInternshipJuryRequest());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					jury.setIdInternshipJuryRequest(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, jury);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, jury);
			}
			
			if(jury.getAppraisers() != null) {
				InternshipJuryAppraiserRequestDAO dao = new InternshipJuryAppraiserRequestDAO(conn);
				String ids = "";
				
				for(InternshipJuryAppraiserRequest ja : jury.getAppraisers()){
					ja.setInternshipJuryRequest(jury);
					int paId = dao.save(idUser, ja);
					ids = ids + String.valueOf(paId) + ",";
				}
				
				Statement st = conn.createStatement();
				st.execute("DELETE FROM internshipjuryappraiserrequest WHERE idInternshipJuryRequest=" + String.valueOf(jury.getIdInternshipJuryRequest()) + 
						(ids.isEmpty() ? "" : " AND idInternshipJuryAppraiserRequest NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")"));
			}
			
			conn.commit();
			
			return jury.getIdInternshipJuryRequest();
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
	
	public boolean delete(int idUser, int id) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		InternshipJuryRequest jury = this.findById(id);
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			stmt = conn.createStatement();
		
			stmt.execute("DELETE FROM internshipjuryappraiserrequest WHERE idInternshipJuryRequest = " + String.valueOf(id));
			boolean ret = stmt.execute("DELETE FROM internshipjuryrequest WHERE idInternshipJuryRequest = " + String.valueOf(id));
			
			new UpdateEvent(conn).registerDelete(idUser, jury);
			
			conn.commit();
			
			return ret;
		} catch (SQLException e) {
			conn.rollback();
			
			throw e;
		} finally {
			conn.setAutoCommit(true);
			
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private InternshipJuryRequest loadObject(ResultSet rs) throws SQLException{
		InternshipJuryRequest jury = new InternshipJuryRequest();
		
		jury.setIdInternshipJuryRequest(rs.getInt("idInternshipJuryRequest"));
		jury.setDate(rs.getTimestamp("date"));
		jury.setLocal(rs.getString("local"));
		jury.setComments(rs.getString("comments"));
		jury.getInternship().setIdInternship(rs.getInt("idInternship"));
		jury.getInternship().getCompany().setIdCompany(rs.getInt("idCompany"));
		jury.getInternship().getCompany().setName(rs.getString("companyName"));
		jury.getInternship().getStudent().setIdUser(rs.getInt("idStudent"));
		jury.getInternship().getStudent().setName(rs.getString("studentName"));
		jury.getInternship().getSupervisor().setIdUser(rs.getInt("idSupervisor"));
		jury.getInternship().getSupervisor().setName(rs.getString("supervisorName"));
		if(rs.getInt("idInternshipJury") > 0) {
			jury.setInternshipJury(new InternshipJury());
			jury.getInternshipJury().setIdInternshipJury(rs.getInt("idInternshipJury"));
		}
		
		return jury;
	}
	
}
