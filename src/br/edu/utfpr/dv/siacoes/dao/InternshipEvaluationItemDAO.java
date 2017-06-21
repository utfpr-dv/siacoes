package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.model.InternshipEvaluationItem;

public class InternshipEvaluationItemDAO {
	
	public InternshipEvaluationItem findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM internshipevaluationitem WHERE idInternshipEvaluationItem = ?");
		
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
	
	public boolean hasScores(int idInternshipEvaluationItem) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM internshipjuryappraiserscore WHERE idInternshipEvaluationItem=?");
		
			stmt.setInt(1, idInternshipEvaluationItem);
			
			ResultSet rs = stmt.executeQuery();
			
			rs.next();
			return (rs.getInt("total") > 0);
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<InternshipEvaluationItem> listAll(boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM internshipevaluationitem " + (onlyActives ? " WHERE active = 1 " : "") + " ORDER BY type, sequence");
			List<InternshipEvaluationItem> list = new ArrayList<InternshipEvaluationItem>();
			
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
	
	public List<InternshipEvaluationItem> listByDepartment(int idDepartment, boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM internshipevaluationitem WHERE idDepartment=" + String.valueOf(idDepartment) + (onlyActives ? " AND active = 1 " : "") + " ORDER BY type, sequence");
			List<InternshipEvaluationItem> list = new ArrayList<InternshipEvaluationItem>();
			
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
	
	public int save(InternshipEvaluationItem item) throws SQLException{
		boolean insert = (item.getIdInternshipEvaluationItem() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(!insert && this.hasScores(item.getIdInternshipEvaluationItem())){
				stmt = conn.prepareStatement("UPDATE internshipevaluationitem SET active=? WHERE idInternshipEvaluationItem=?");
				
				stmt.setInt(1, (item.isActive() ? 1 : 0));
				stmt.setInt(2, item.getIdInternshipEvaluationItem());
				
				stmt.execute();
			}else{
				if(insert){
					stmt = conn.prepareStatement("INSERT INTO internshipevaluationitem(idDepartment, description, ponderosity, active, sequence, type) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
					
					Statement stmt2 = conn.createStatement();
					ResultSet rs = stmt2.executeQuery("SELECT COUNT(*) as total FROM internshipevaluationitem WHERE idDepartment=" + String.valueOf(item.getDepartment().getIdDepartment()));
					rs.next();
					item.setSequence(rs.getInt("total") + 1);
					stmt2.close();
				}else{
					stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE internshipevaluationitem SET idDepartment=?, description=?, ponderosity=?, active=?, sequence=?, type=? WHERE idInternshipEvaluationItem=?");
				}
				
				stmt.setInt(1, item.getDepartment().getIdDepartment());
				stmt.setString(2, item.getDescription());
				stmt.setDouble(3, item.getPonderosity());
				stmt.setInt(4, (item.isActive() ? 1 : 0));
				stmt.setInt(5, item.getSequence());
				stmt.setInt(6, item.getType().getValue());
				
				if(!insert){
					stmt.setInt(7, item.getIdInternshipEvaluationItem());
				}
				
				stmt.execute();
				
				if(insert){
					ResultSet rs = stmt.getGeneratedKeys();
					
					if(rs.next()){
						item.setIdInternshipEvaluationItem(rs.getInt(1));
					}
				}
			}
			
			return item.getIdInternshipEvaluationItem();
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private InternshipEvaluationItem loadObject(ResultSet rs) throws SQLException{
		InternshipEvaluationItem item = new InternshipEvaluationItem();
		
		item.setIdInternshipEvaluationItem(rs.getInt("idInternshipEvaluationItem"));
		item.setDescription(rs.getString("description"));
		item.setPonderosity(rs.getDouble("ponderosity"));
		item.setActive(rs.getInt("active") == 1);
		item.setSequence(rs.getInt("sequence"));
		item.setType(EvaluationItemType.valueOf(rs.getInt("type")));
		item.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		
		return item;
	}
	
	public boolean delete(int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			return stmt.execute("DELETE FROM internshipevaluationitem WHERE idInternshipEvaluationItem = " + String.valueOf(id));
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public void moveUp(int idInternshipEvaluationItem) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT sequence, type, idDepartment FROM internshipevaluationitem WHERE idInternshipEvaluationItem=?");
			stmt.setInt(1, idInternshipEvaluationItem);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				int sequence = rs.getInt("sequence");
				int type = rs.getInt("type");
				int idDepartment = rs.getInt("idDepartment");
				
				stmt.close();
				stmt = conn.prepareStatement("SELECT idInternshipEvaluationItem FROM internshipevaluationitem WHERE idDepartment=? AND sequence < ? AND type=? ORDER BY sequence DESC");
				stmt.setInt(1, idDepartment);
				stmt.setInt(2, sequence);
				stmt.setInt(3, type);
				rs = stmt.executeQuery();
				
				if(rs.next()){
					int idInternshipEvaluationItem2 = rs.getInt("idInternshipEvaluationItem");
					
					try{
						conn.setAutoCommit(false);
						
						stmt = conn.prepareStatement("UPDATE internshipevaluationitem SET sequence=? WHERE idInternshipEvaluationItem=?");
						stmt.setInt(1, sequence);
						stmt.setInt(2, idInternshipEvaluationItem2);
						stmt.execute();
						
						stmt = conn.prepareStatement("UPDATE internshipevaluationitem SET sequence=? WHERE idInternshipEvaluationItem=?");
						stmt.setInt(1, sequence - 1);
						stmt.setInt(2, idInternshipEvaluationItem);
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
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public void moveDown(int idInternshipEvaluationItem) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idDepartment, sequence, type FROM internshipevaluationitem WHERE idInternshipEvaluationItem=?");
			stmt.setInt(1, idInternshipEvaluationItem);
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				int sequence = rs.getInt("sequence");
				int type = rs.getInt("type");
				int idDepartment = rs.getInt("idDepartment");
				
				stmt.close();
				stmt = conn.prepareStatement("SELECT idInternshipEvaluationItem FROM internshipevaluationitem WHERE idDepartment=? AND sequence > ? AND type=? ORDER BY sequence");
				stmt.setInt(1, idDepartment);
				stmt.setInt(2, sequence);
				stmt.setInt(3, type);
				rs = stmt.executeQuery();
				
				if(rs.next()){
					int idInternshipEvaluationItem2 = rs.getInt("idInternshipEvaluationItem");
					
					try{
						conn.setAutoCommit(false);
						
						stmt = conn.prepareStatement("UPDATE internshipevaluationitem SET sequence=? WHERE idInternshipEvaluationItem=?");
						stmt.setInt(1, sequence);
						stmt.setInt(2, idInternshipEvaluationItem2);
						stmt.execute();
						
						stmt = conn.prepareStatement("UPDATE internshipevaluationitem SET sequence=? WHERE idInternshipEvaluationItem=?");
						stmt.setInt(1, sequence + 1);
						stmt.setInt(2, idInternshipEvaluationItem);
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
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}

}
