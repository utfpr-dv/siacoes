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
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserRequest;

public class InternshipJuryAppraiserRequestDAO {

private Connection conn;

	public InternshipJuryAppraiserRequestDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}

	public InternshipJuryAppraiserRequestDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}

	public InternshipJuryAppraiserRequest findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipjuryappraiserrequest.*, appraiser.name as appraiserName, internshipjuryrequest.date, " +
					"internshipjuryrequest.idInternship, internship.idStudent, internship.idSupervisor, internship.idCompany, " +
					"student.name AS studentName, supervisor.name AS supervisorName, company.name AS companyName " +
					"FROM internshipjuryappraiserrequest INNER JOIN \"user\" appraiser ON appraiser.idUser=internshipjuryappraiserrequest.idAppraiser " +
					"INNER JOIN internshipjuryrequest ON internshipjuryrequest.idInternshipJuryRequest=internshipjuryappraiserrequest.idInternshipJuryRequest " +
					"INNER JOIN internship ON internship.idInternship=internshipjuryrequest.idInternship " + 
					"INNER JOIN \"user\" student ON student.idUser=internship.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=internship.idSupervisor " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE internshipjuryappraiserrequest.idInternshipJuryAppraiserRequest=?");

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
		}
	}

	public InternshipJuryAppraiserRequest findByAppraiser(int idInternshipJuryRequest, int idUser) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipjuryappraiserrequest.*, appraiser.name as appraiserName, internshipjuryrequest.date, " +
					"internshipjuryrequest.idInternship, internship.idStudent, internship.idSupervisor, internship.idCompany, " +
					"student.name AS studentName, supervisor.name AS supervisorName, company.name AS companyName " +
					"FROM internshipjuryappraiserrequest INNER JOIN \"user\" appraiser ON appraiser.idUser=internshipjuryappraiserrequest.idAppraiser " +
					"INNER JOIN internshipjuryrequest ON internshipjuryrequest.idInternshipJuryRequest=internshipjuryappraiserrequest.idInternshipJuryRequest " +
					"INNER JOIN internship ON internship.idInternship=internshipjuryrequest.idInternship " + 
					"INNER JOIN \"user\" student ON student.idUser=internship.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=internship.idSupervisor " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE internshipjuryappraiserrequest.idInternshipJuryRequest=? AND internshipjuryappraiserrequest.idAppraiser=?");

			stmt.setInt(1, idInternshipJuryRequest);
			stmt.setInt(2, idUser);

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
		}
	}

	public List<InternshipJuryAppraiserRequest> listAppraisers(int idInternshipJuryRequest) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;

		try{
			stmt = this.conn.createStatement();

			rs = stmt.executeQuery("SELECT internshipjuryappraiserrequest.*, appraiser.name as appraiserName, internshipjuryrequest.date, " +
					"internshipjuryrequest.idInternship, internship.idStudent, internship.idSupervisor, internship.idCompany, " +
					"student.name AS studentName, supervisor.name AS supervisorName, company.name AS companyName " +
					"FROM internshipjuryappraiserrequest INNER JOIN \"user\" appraiser ON appraiser.idUser=internshipjuryappraiserrequest.idAppraiser " +
					"INNER JOIN internshipjuryrequest ON internshipjuryrequest.idInternshipJuryRequest=internshipjuryappraiserrequest.idInternshipJuryRequest " +
					"INNER JOIN internship ON internship.idInternship=internshipjuryrequest.idInternship " + 
					"INNER JOIN \"user\" student ON student.idUser=internship.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=internship.idSupervisor " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE internshipjuryappraiserrequest.idInternshipJuryRequest = " + String.valueOf(idInternshipJuryRequest) + 
					" ORDER BY internshipjuryappraiserrequest.chair DESC, internshipjuryappraiserrequest.substitute, appraiser.name");
			List<InternshipJuryAppraiserRequest> list = new ArrayList<InternshipJuryAppraiserRequest>();

			while(rs.next()){
				list.add(this.loadObject(rs));
			}

			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}

	public int save(int idUser, InternshipJuryAppraiserRequest appraiser) throws SQLException{
		boolean insert = (appraiser.getIdInternshipJuryAppraiserRequest() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO internshipjuryappraiserrequest(idInternshipJuryRequest, idAppraiser, chair, substitute) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE internshipjuryappraiserrequest SET idInternshipJuryRequest=?, idAppraiser=?, chair=?, substitute=? WHERE idInternshipJuryAppraiserRequest=?");
			}

			stmt.setInt(1, appraiser.getInternshipJuryRequest().getIdInternshipJuryRequest());
			stmt.setInt(2, appraiser.getAppraiser().getIdUser());
			stmt.setInt(3, (appraiser.isChair() ? 1 : 0));
			stmt.setInt(4, (appraiser.isSubstitute() ? 1 : 0));

			if(!insert){
				stmt.setInt(5, appraiser.getIdInternshipJuryAppraiserRequest());
			}

			stmt.execute();

			if(insert){
				rs = stmt.getGeneratedKeys();

				if(rs.next()){
					appraiser.setIdInternshipJuryAppraiserRequest(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, appraiser);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, appraiser);
			}

			return appraiser.getIdInternshipJuryAppraiserRequest();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}

	private InternshipJuryAppraiserRequest loadObject(ResultSet rs) throws SQLException{
		InternshipJuryAppraiserRequest p = new InternshipJuryAppraiserRequest();

		p.setIdInternshipJuryAppraiserRequest(rs.getInt("idInternshipJuryAppraiserRequest"));
		p.getInternshipJuryRequest().setIdInternshipJuryRequest(rs.getInt("idInternshipJuryRequest"));
		p.getInternshipJuryRequest().setDate(rs.getDate("date"));
		p.getInternshipJuryRequest().getInternship().setIdInternship(rs.getInt("idInternship"));
		p.getInternshipJuryRequest().getInternship().getStudent().setName(rs.getString("studentName"));
		p.getInternshipJuryRequest().getInternship().getStudent().setIdUser(rs.getInt("idStudent"));
		p.getInternshipJuryRequest().getInternship().getSupervisor().setName(rs.getString("supervisorName"));
		p.getInternshipJuryRequest().getInternship().getSupervisor().setIdUser(rs.getInt("idSupervisor"));
		p.getInternshipJuryRequest().getInternship().getCompany().setName(rs.getString("companyName"));
		p.getInternshipJuryRequest().getInternship().getCompany().setIdCompany(rs.getInt("idCompany"));
		p.getAppraiser().setIdUser(rs.getInt("idAppraiser"));
		p.getAppraiser().setName(rs.getString("appraiserName"));
		p.setSubstitute(rs.getInt("substitute") == 1);
		p.setChair(rs.getInt("chair") == 1);

		return p;
	}

	public boolean appraiserHasJuryRequest(int idInternshipJuryRequest, int idUser, Date juryDate) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try{
			stmt = this.conn.prepareStatement(
					"SELECT SUM(total) AS total FROM (" +
					"SELECT COUNT(*) AS total FROM internshipjuryrequest INNER JOIN internshipjuryappraiserrequest ON internshipjuryappraiserrequest.idInternshipJuryRequest=internshipjuryrequest.idInternshipJuryRequest " +
					"WHERE internshipjuryrequest.idInternshipJuryRequest <> ? AND internshipjuryappraiserrequest.idAppraiser = ? " +
					"AND internshipjuryrequest.date BETWEEN ?::TIMESTAMP - INTERVAL '1 minute' * ( " +
					"SELECT sigesconfig.jurytime - 1 FROM sigesconfig INNER JOIN internship ON internship.iddepartment=sigesconfig.iddepartment " +
					"WHERE internship.idInternship=internshipjuryrequest.idInternship) AND ?::TIMESTAMP + INTERVAL '1 minute' * ( " +
					"SELECT sigesconfig.jurytime - 1 FROM sigesconfig INNER JOIN internship ON internship.iddepartment=sigesconfig.iddepartment " +
					"WHERE internship.idInternship=internshipjuryrequest.idInternship)" + 
					" UNION ALL " +
					"SELECT COUNT(*) AS total FROM juryrequest INNER JOIN juryappraiserrequest ON juryappraiserrequest.idJuryRequest=juryrequest.idJuryRequest " +
					"WHERE juryappraiserrequest.idAppraiser = ? AND juryrequest.date BETWEEN ?::TIMESTAMP - INTERVAL '1 minute' * ( " +
					"SELECT CASE WHEN juryrequest.stage = 2 THEN sigetconfig.jurytimestage2 ELSE sigetconfig.jurytimestage1 END - 1 " +
					"FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " +
					"WHERE proposal.idproposal=juryrequest.idproposal) AND ?::TIMESTAMP + INTERVAL '1 minute' * ( " +
					"SELECT CASE WHEN juryrequest.stage = 2 THEN sigetconfig.jurytimestage2 ELSE sigetconfig.jurytimestage1 END - 1 " +
					"FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " +
					"WHERE proposal.idproposal=juryrequest.idproposal)" + 
					") AS teste");

			stmt.setInt(1, idInternshipJuryRequest);
			stmt.setInt(2, idUser);
			stmt.setTimestamp(3, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setTimestamp(4, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setInt(5, idUser);
			stmt.setTimestamp(6, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setTimestamp(7, new java.sql.Timestamp(juryDate.getTime()));

			rs = stmt.executeQuery();
			rs.next();

			return (rs.getInt("total") > 0);
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}

}
