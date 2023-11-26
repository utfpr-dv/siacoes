package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem;
import br.edu.utfpr.dv.siacoes.model.ThesisFormat;

public class ThesisFormatDAO {

	public ThesisFormat findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM thesisformat WHERE idthesisformat = ?");
		
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
	
	public List<ThesisFormat> listAll(boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM thesisformat " + (onlyActives ? " WHERE active = 1 " : "") + " ORDER BY description");
			List<ThesisFormat> list = new ArrayList<ThesisFormat>();
			
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
	
	public List<ThesisFormat> listByDepartment(int idDepartment, boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM thesisformat WHERE idDepartment=" + String.valueOf(idDepartment) + (onlyActives ? " AND active = 1 " : "") + " ORDER BY description");
			List<ThesisFormat> list = new ArrayList<ThesisFormat>();
			
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
	
	public int save(int idUser, ThesisFormat format) throws SQLException{
		boolean insert = (format.getIdThesisFormat() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO thesisformat(idDepartment, description, active) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE thesisformat SET idDepartment=?, description=?, active=? WHERE idthesisformat=?");
			}
			
			stmt.setInt(1, format.getDepartment().getIdDepartment());
			stmt.setString(2, format.getDescription());
			stmt.setInt(3, (format.isActive() ? 1 : 0));
			
			if(!insert){
				stmt.setInt(4, format.getIdThesisFormat());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					format.setIdThesisFormat(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, format);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, format);
			}
			
			if(format.getItems() != null) {
				EvaluationItemDAO dao = new EvaluationItemDAO(conn);
				String ids = "";
				
				for(EvaluationItem item : format.getItems()) {
					item.setFormat(format);
					int paId = dao.save(idUser, item);
					ids = ids + String.valueOf(paId) + ",";
				}
				
				Statement st = conn.createStatement();
				st.execute("DELETE FROM evaluationitem WHERE idThesisFormat=" + String.valueOf(format.getIdThesisFormat()) + 
						(ids.isEmpty() ? "" : " AND idEvaluationItem NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")"));
				st.close();
			}
			
			conn.commit();
			
			return format.getIdThesisFormat();
		}catch(SQLException e){
			conn.rollback();
			throw e;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private ThesisFormat loadObject(ResultSet rs) throws SQLException{
		ThesisFormat format = new ThesisFormat();
		
		format.setIdThesisFormat(rs.getInt("IdThesisFormat"));
		format.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		format.setDescription(rs.getString("description"));
		format.setActive(rs.getInt("active") == 1);
		format.setItems(new EvaluationItemDAO().listByFormat(format.getIdThesisFormat(), false));
		
		return format;
	}
	
}
