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
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;

public class EvaluationItemDAO {
	
	public EvaluationItem findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM evaluationitem WHERE idEvaluationItem = ?");
		
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
	
	public boolean hasScores(int idEvaluationItem) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM juryappraiserscore WHERE idEvaluationItem=?");
		
			stmt.setInt(1, idEvaluationItem);
			
			rs = stmt.executeQuery();
			
			rs.next();
			return (rs.getInt("total") > 0);
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<EvaluationItem> listAll(boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM evaluationitem " + (onlyActives ? " WHERE active = 1 " : "") + " ORDER BY stage, type, sequence");
			List<EvaluationItem> list = new ArrayList<EvaluationItem>();
			
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
	
	public List<EvaluationItem> listByDepartment(int idDepartment, boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM evaluationitem WHERE idDepartment=" + String.valueOf(idDepartment) + (onlyActives ? " AND active = 1 " : "") + " ORDER BY stage, type, sequence");
			List<EvaluationItem> list = new ArrayList<EvaluationItem>();
			
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
	
	public List<EvaluationItem> listByStage(int stage, int idDepartment, boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM evaluationitem WHERE idDepartment=" + String.valueOf(idDepartment) + " AND stage = " + String.valueOf(stage) + (onlyActives ? " AND active = 1 " : "") + " ORDER BY type, sequence");
			List<EvaluationItem> list = new ArrayList<EvaluationItem>();
			
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
	
	public int save(int idUser, EvaluationItem item) throws SQLException{
		boolean insert = (item.getIdEvaluationItem() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(!insert && this.hasScores(item.getIdEvaluationItem())){
				stmt = conn.prepareStatement("UPDATE evaluationitem SET active=? WHERE idEvaluationItem=?");
				
				stmt.setInt(1, (item.isActive() ? 1 : 0));
				stmt.setInt(2, item.getIdEvaluationItem());
				
				stmt.execute();
				
				new UpdateEvent(conn).registerUpdate(idUser, item);
			}else{
				if(insert){
					stmt = conn.prepareStatement("INSERT INTO evaluationitem(idDepartment, description, ponderosity, stage, active, sequence, type) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
					
					Statement stmt2 = conn.createStatement();
					rs = stmt2.executeQuery("SELECT COUNT(*) as total FROM evaluationitem WHERE idDepartment=" + String.valueOf(item.getDepartment().getIdDepartment()) + " AND stage = " + String.valueOf(item.getStage()));
					rs.next();
					item.setSequence(rs.getInt("total") + 1);
					rs.close();
					stmt2.close();
				}else{
					stmt = conn.prepareStatement("UPDATE evaluationitem SET idDepartment=?, description=?, ponderosity=?, stage=?, active=?, sequence=?, type=? WHERE idEvaluationItem=?");
				}
				
				stmt.setInt(1, item.getDepartment().getIdDepartment());
				stmt.setString(2, item.getDescription());
				stmt.setDouble(3, item.getPonderosity());
				stmt.setInt(4, item.getStage());
				stmt.setInt(5, (item.isActive() ? 1 : 0));
				stmt.setInt(6, item.getSequence());
				stmt.setInt(7, item.getType().getValue());
				
				if(!insert){
					stmt.setInt(8, item.getIdEvaluationItem());
				}
				
				stmt.execute();
				
				if(insert){
					rs = stmt.getGeneratedKeys();
					
					if(rs.next()){
						item.setIdEvaluationItem(rs.getInt(1));
					}

					new UpdateEvent(conn).registerInsert(idUser, item);
				} else {
					new UpdateEvent(conn).registerUpdate(idUser, item);
				}
			}
			
			return item.getIdEvaluationItem();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private EvaluationItem loadObject(ResultSet rs) throws SQLException{
		EvaluationItem item = new EvaluationItem();
		
		item.setIdEvaluationItem(rs.getInt("idEvaluationItem"));
		item.setDescription(rs.getString("description"));
		item.setPonderosity(rs.getDouble("ponderosity"));
		item.setStage(rs.getInt("stage"));
		item.setActive(rs.getInt("active") == 1);
		item.setSequence(rs.getInt("sequence"));
		item.setType(EvaluationItemType.valueOf(rs.getInt("type")));
		item.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		
		return item;
	}
	
	public boolean delete(int idUser, int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		EvaluationItem item = this.findById(id);
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			boolean ret = stmt.execute("DELETE FROM evaluationitem WHERE idEvaluationItem = " + String.valueOf(id));
			
			new UpdateEvent(conn).registerDelete(idUser, item);
			
			return ret;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public void moveUp(int idEvaluationItem) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT sequence, stage, type, idDepartment FROM evaluationitem WHERE idEvaluationItem=?");
			
			stmt.setInt(1, idEvaluationItem);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int sequence = rs.getInt("sequence");
				int stage = rs.getInt("stage");
				int type = rs.getInt("type");
				int idDepartment = rs.getInt("idDepartment");
				
				rs.close();
				stmt.close();
				stmt = conn.prepareStatement("SELECT idEvaluationItem FROM evaluationitem WHERE idDepartment=? AND sequence < ? AND stage=? AND type=? ORDER BY sequence DESC");
				stmt.setInt(1, idDepartment);
				stmt.setInt(2, sequence);
				stmt.setInt(3, stage);
				stmt.setInt(4, type);
				rs = stmt.executeQuery();
				
				if(rs.next()){
					int idEvaluationItem2 = rs.getInt("idEvaluationItem");
					
					try{
						conn.setAutoCommit(false);
						
						stmt = conn.prepareStatement("UPDATE evaluationitem SET sequence=? WHERE idEvaluationItem=?");
						stmt.setInt(1, sequence);
						stmt.setInt(2, idEvaluationItem2);
						stmt.execute();
						
						stmt = conn.prepareStatement("UPDATE evaluationitem SET sequence=? WHERE idEvaluationItem=?");
						stmt.setInt(1, sequence - 1);
						stmt.setInt(2, idEvaluationItem);
						stmt.execute();
						
						conn.commit();
					}catch(SQLException e){
						conn.rollback();
						
						throw e;
					}finally{
						conn.setAutoCommit(true);
					}
				}
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
	
	public void moveDown(int idEvaluationItem) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idDepartment, sequence, stage, type FROM evaluationitem WHERE idEvaluationItem=?");
			
			stmt.setInt(1, idEvaluationItem);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int sequence = rs.getInt("sequence");
				int stage = rs.getInt("stage");
				int type = rs.getInt("type");
				int idDepartment = rs.getInt("idDepartment");
				
				rs.close();
				stmt.close();
				stmt = conn.prepareStatement("SELECT idEvaluationItem FROM evaluationitem WHERE idDepartment=? AND sequence > ? AND stage=? AND type=? ORDER BY sequence");
				stmt.setInt(1, idDepartment);
				stmt.setInt(2, sequence);
				stmt.setInt(3, stage);
				stmt.setInt(4, type);
				rs = stmt.executeQuery();
				
				if(rs.next()){
					int idEvaluationItem2 = rs.getInt("idEvaluationItem");
					
					try{
						conn.setAutoCommit(false);
						
						stmt = conn.prepareStatement("UPDATE evaluationitem SET sequence=? WHERE idEvaluationItem=?");
						stmt.setInt(1, sequence);
						stmt.setInt(2, idEvaluationItem2);
						stmt.execute();
						
						stmt = conn.prepareStatement("UPDATE evaluationitem SET sequence=? WHERE idEvaluationItem=?");
						stmt.setInt(1, sequence + 1);
						stmt.setInt(2, idEvaluationItem);
						stmt.execute();
						
						conn.commit();
					}catch(SQLException e){
						conn.rollback();
						
						throw e;
					}finally{
						conn.setAutoCommit(true);
					}
				}
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

}
