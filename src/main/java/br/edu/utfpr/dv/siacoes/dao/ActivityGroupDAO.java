package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;

public class ActivityGroupDAO {
	
	public List<ActivityGroup> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT * FROM activitygroup ORDER BY sequence");
			
			List<ActivityGroup> list = new ArrayList<ActivityGroup>();
			
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
	
	public ActivityGroup findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM activitygroup WHERE idActivityGroup=?");
		
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
	
	public ActivityGroup findByActivity(int idActivity) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idActivityGroup FROM activity WHERE idActivity=?");
		
			stmt.setInt(1, idActivity);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.findById(rs.getInt("idActivityGroup"));
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
	
	public int save(int idUser, ActivityGroup group) throws SQLException{
		boolean insert = (group.getIdActivityGroup() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO activitygroup(description, sequence, minimumScore, maximumScore) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				
				Statement stmt2 = conn.createStatement();
				rs = stmt2.executeQuery("SELECT COUNT(*) as total FROM activitygroup");
				rs.next();
				group.setSequence(rs.getInt("total") + 1);
				rs.close();
				stmt2.close();
			}else{
				stmt = conn.prepareStatement("UPDATE activitygroup SET description=?, sequence=?, minimumScore=?, maximumScore=? WHERE idActivityGroup=?");
			}
			
			stmt.setString(1, group.getDescription());
			stmt.setInt(2, group.getSequence());
			stmt.setInt(3, group.getMinimumScore());
			stmt.setInt(4, group.getMaximumScore());
			
			if(!insert){
				stmt.setInt(5, group.getIdActivityGroup());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					group.setIdActivityGroup(rs.getInt(1));
				}
				
				new UpdateEvent(conn).registerInsert(idUser, group);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, group);
			}
			
			return group.getIdActivityGroup();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private ActivityGroup loadObject(ResultSet rs) throws SQLException{
		ActivityGroup group = new ActivityGroup();
		
		group.setIdActivityGroup(rs.getInt("idActivityGroup"));
		group.setDescription(rs.getString("description"));
		group.setSequence(rs.getInt("sequence"));
		group.setMaximumScore(rs.getInt("maximumScore"));
		group.setMinimumScore(rs.getInt("minimumScore"));
		
		return group;
	}
	
	public void moveUp(int idActivityGroup) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT sequence FROM activitygroup WHERE idActivityGroup=?");
			stmt.setInt(1, idActivityGroup);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int sequence = rs.getInt("sequence");
				
				rs.close();
				stmt.close();
				stmt = conn.prepareStatement("SELECT idActivityGroup FROM activitygroup WHERE sequence < ? ORDER BY sequence DESC");
				stmt.setInt(1, sequence);
				rs = stmt.executeQuery();
				
				if(rs.next()){
					int idActivityGroup2 = rs.getInt("idActivityGroup");
					
					try{
						conn.setAutoCommit(false);
						
						stmt = conn.prepareStatement("UPDATE activitygroup SET sequence=? WHERE idActivityGroup=?");
						stmt.setInt(1, sequence);
						stmt.setInt(2, idActivityGroup2);
						stmt.execute();
						
						stmt = conn.prepareStatement("UPDATE activitygroup SET sequence=? WHERE idActivityGroup=?");
						stmt.setInt(1, sequence - 1);
						stmt.setInt(2, idActivityGroup);
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
	
	public void moveDown(int idActivityGroup) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT sequence FROM activitygroup WHERE idActivityGroup=?");
			stmt.setInt(1, idActivityGroup);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int sequence = rs.getInt("sequence");
				
				rs.close();
				stmt.close();
				stmt = conn.prepareStatement("SELECT idActivityGroup FROM activitygroup WHERE sequence > ? ORDER BY sequence");
				stmt.setInt(1, sequence);
				rs = stmt.executeQuery();
				
				if(rs.next()){
					int idActivityGroup2 = rs.getInt("idActivityGroup");
					
					try{
						conn.setAutoCommit(false);
						
						stmt = conn.prepareStatement("UPDATE activitygroup SET sequence=? WHERE idActivityGroup=?");
						stmt.setInt(1, sequence);
						stmt.setInt(2, idActivityGroup2);
						stmt.execute();
						
						stmt = conn.prepareStatement("UPDATE activitygroup SET sequence=? WHERE idActivityGroup=?");
						stmt.setInt(1, sequence + 1);
						stmt.setInt(2, idActivityGroup);
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
