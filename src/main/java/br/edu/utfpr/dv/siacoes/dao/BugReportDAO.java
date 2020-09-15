package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import br.edu.utfpr.dv.siacoes.dao.base.BaseDAO;
import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;

public class BugReportDAO extends BaseDAO<BugReport> {
	@Override
	protected int insertResultSetStep(int idUser, Connection conn, ResultSet rs, BugReport object) throws SQLException {
		if(rs.next()){
			object.setIdBugReport(rs.getInt(1));
		}

		return object.getIdBugReport();
	}

	@Override
	protected String findByIdQuery() {
		return "SELECT bugreport.*, \"user\".name " +
				"FROM bugreport " +
				"INNER JOIN \"user\" " +
				"ON \"user\".idUser=bugreport.idUser " +
				"WHERE idBugReport = ?";
	}

	@Override
	protected String insertQuery() {
		return "INSERT INTO bugreport(" +
				"idUser, " +
				"module, " +
				"title, " +
				"description, " +
				"reportDate, " +
				"type, " +
				"status, " +
				"statusDate, " +
				"statusDescription" +
				") " +
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	@Override
	protected String updateQuery() {
		return "UPDATE bugreport " +
				"SET idUser=?, " +
				"module=?, " +
				"title=?, " +
				"description=?, " +
				"reportDate=?, " +
				"type=?, " +
				"status=?, " +
				"statusDate=?, " +
				"statusDescription=? " +
				"WHERE idBugReport=?";
	}

	@Override
	protected int getId(BugReport object) {
		return 0;
	}

	@Override
	protected PreparedStatement insertStatementStep(PreparedStatement stmt, BugReport object) throws SQLException {
		stmt.setInt(1, object.getUser().getIdUser());
		stmt.setInt(2, object.getModule().getValue());
		stmt.setString(3, object.getTitle());
		stmt.setString(4, object.getDescription());
		stmt.setDate(5, new java.sql.Date(object.getReportDate().getTime()));
		stmt.setInt(6, object.getType().getValue());
		stmt.setInt(7, object.getStatus().getValue());
		if(object.getStatus() == BugStatus.REPORTED){
			stmt.setNull(8, Types.DATE);
		}else{
			stmt.setDate(8, new java.sql.Date(object.getStatusDate().getTime()));
		}
		stmt.setString(9, object.getStatusDescription());

		return stmt;
	}

	@Override
	protected PreparedStatement updateStatementStep(PreparedStatement stmt, BugReport object) throws SQLException {
		stmt.setInt(1, object.getUser().getIdUser());
		stmt.setInt(2, object.getModule().getValue());
		stmt.setString(3, object.getTitle());
		stmt.setString(4, object.getDescription());
		stmt.setDate(5, new java.sql.Date(object.getReportDate().getTime()));
		stmt.setInt(6, object.getType().getValue());
		stmt.setInt(7, object.getStatus().getValue());
		if(object.getStatus() == BugStatus.REPORTED){
			stmt.setNull(8, Types.DATE);
		}else{
			stmt.setDate(8, new java.sql.Date(object.getStatusDate().getTime()));
		}
		stmt.setString(9, object.getStatusDescription());
		stmt.setInt(10, object.getIdBugReport());

		return stmt;
	}

	public List<BugReport> listAll() throws SQLException{
		return this.list("SELECT bugreport.*, \"user\".name " +
				       "FROM bugreport " +
				         "INNER JOIN \"user\" " +
				           "ON \"user\".idUser=bugreport.idUser " +
				       "ORDER BY status, reportdate");
	}
	
	protected BugReport loadObject(ResultSet rs) throws SQLException{
		BugReport bug = new BugReport();
		
		bug.setIdBugReport(rs.getInt("idBugReport"));
		bug.setUser(new User());
		bug.getUser().setIdUser(rs.getInt("idUser"));
		bug.getUser().setName(rs.getString("name"));
		bug.setModule(Module.SystemModule.valueOf(rs.getInt("module")));
		bug.setTitle(rs.getString("title"));
		bug.setDescription(rs.getString("description"));
		bug.setReportDate(rs.getDate("reportDate"));
		bug.setType(BugReport.BugType.valueOf(rs.getInt("type")));
		bug.setStatus(BugReport.BugStatus.valueOf(rs.getInt("status")));
		bug.setStatusDate(rs.getDate("statusDate"));
		bug.setStatusDescription(rs.getString("statusDescription"));
		
		return bug;
	}

}
