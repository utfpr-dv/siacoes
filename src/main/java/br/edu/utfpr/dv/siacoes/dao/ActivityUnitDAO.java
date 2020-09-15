package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.edu.utfpr.dv.siacoes.dao.base.BaseDAO;
import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;

public class ActivityUnitDAO extends BaseDAO<ActivityUnit> {
	
	public List<ActivityUnit> listAll() throws SQLException{
		return this.list("SELECT * " +
				"FROM activityunit " +
				"ORDER BY description");
	}

	@Override
	protected int insertResultSetStep(int idUser, Connection conn, ResultSet rs, ActivityUnit object) throws SQLException {
		if(rs.next()){
			object.setIdActivityUnit(rs.getInt(1));
		}

		new UpdateEvent(conn).registerInsert(idUser, object);

		return object.getIdActivityUnit();
	}

	@Override
	protected String findByIdQuery() {
		return "SELECT * " +
				"FROM activityunit " +
				"WHERE idActivityUnit=?";
	}

	@Override
	protected String insertQuery() {
		return "INSERT INTO activityunit(description, fillAmount, amountDescription) " +
				"VALUES(?, ?, ?)";
	}

	@Override
	protected String updateQuery() {
		return "UPDATE activityunit " +
				"SET description=?, fillAmount=?, amountDescription=? " +
				"WHERE idActivityUnit=?";
	}

	@Override
	protected int getId(ActivityUnit object) {
		return object.getIdActivityUnit();
	}

	@Override
	protected PreparedStatement insertStatementStep(PreparedStatement stmt, ActivityUnit object) throws SQLException {
		stmt.setString(1, object.getDescription());
		stmt.setInt(2, (object.isFillAmount() ? 1 : 0));
		stmt.setString(3, object.getAmountDescription());
		return stmt;
	}

	@Override
	protected PreparedStatement updateStatementStep(PreparedStatement stmt, ActivityUnit object) throws SQLException {
		stmt.setString(1, object.getDescription());
		stmt.setInt(2, (object.isFillAmount() ? 1 : 0));
		stmt.setString(3, object.getAmountDescription());
		stmt.setInt(4, object.getIdActivityUnit());
		return stmt;
	}

	protected ActivityUnit loadObject(ResultSet rs) throws SQLException{
		ActivityUnit unit = new ActivityUnit();
		
		unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
		unit.setDescription(rs.getString("Description"));
		unit.setFillAmount(rs.getInt("fillAmount") == 1);
		unit.setAmountDescription(rs.getString("amountDescription"));
		
		return unit;
	}

}
