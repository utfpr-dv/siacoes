package br.edu.utfpr.dv.siacoes.dao.base;

import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;
import br.edu.utfpr.dv.siacoes.log.UpdateEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T> {
    public T findById(int id) throws SQLException {
        String query = this.findByIdQuery();

        try(
                Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()
        ) {
            stmt.setInt(1, id);

            if(rs.next()){
                return this.loadObject(rs);
            }else{
                return null;
            }
        }
    }

    protected List<T> list(String query) throws SQLException {
        try(
                Connection conn = ConnectionDAO.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            List<T> list = new ArrayList<T>();

            while(rs.next()){
                list.add(this.loadObject(rs));
            }

            return list;
        }
    }

    private int insert(int idUser, T object) throws SQLException {
        String query = this.insertQuery();

        try (
                Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            PreparedStatement newStmt = this.insertStatementStep(stmt, object);
            newStmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return this.insertResultSetStep(idUser, conn, rs, object);
            }
        }
    }

    public int save(int idUser, T object) throws SQLException {
        return this.getId(object) == 0
                ? this.insert(idUser, object)
                : this.update(idUser, object);
    }

    private int update(int idUser, T object) throws SQLException {
        String query = this.updateQuery();

        try (
                Connection conn = ConnectionDAO.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            PreparedStatement newStmt = this.updateStatementStep(stmt, object);

            newStmt.execute();

            new UpdateEvent(conn).registerUpdate(idUser, object);

            return this.getId(object);
        }
    }

    protected abstract String findByIdQuery();

    protected abstract String insertQuery();

    protected abstract String updateQuery();

    protected abstract int getId(T object);

    protected abstract PreparedStatement insertStatementStep(PreparedStatement stmt, T object) throws SQLException;

    protected abstract int insertResultSetStep(int idUser, Connection conn, ResultSet rs, T object) throws SQLException;

    protected abstract PreparedStatement updateStatementStep(PreparedStatement stmt, T object) throws SQLException;

    protected abstract T loadObject(ResultSet rs) throws SQLException;
}
