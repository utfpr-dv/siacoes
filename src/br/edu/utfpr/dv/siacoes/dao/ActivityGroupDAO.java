package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ActivityGroup;

public class ActivityGroupDAO {
	
	public List<ActivityGroup> listAll() throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM activitygroup ORDER BY sequence");
		
		List<ActivityGroup> list = new ArrayList<ActivityGroup>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public ActivityGroup findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT * FROM activitygroup WHERE idActivityGroup=?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public ActivityGroup findByActivity(int idActivity) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT idActivityGroup FROM activity WHERE idActivity=?");
		
		stmt.setInt(1, idActivity);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.findById(rs.getInt("idActivityGroup"));
		}else{
			return null;
		}
	}
	
	public int save(ActivityGroup group) throws SQLException{
		boolean insert = (group.getIdActivityGroup() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO activitygroup(description, sequence, minimumScore, maximumScore) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			Statement stmt2 = ConnectionDAO.getInstance().getConnection().createStatement();
			ResultSet rs = stmt2.executeQuery("SELECT COUNT(*) as total FROM activitygroup");
			rs.next();
			group.setSequence(rs.getInt("total") + 1);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE activitygroup SET description=?, sequence=?, minimumScore=?, maximumScore=? WHERE idActivityGroup=?");
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
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				group.setIdActivityGroup(rs.getInt(1));
			}
		}
		
		return group.getIdActivityGroup();
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
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT sequence FROM activitygroup WHERE idActivityGroup=?");
		stmt.setInt(1, idActivityGroup);
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			int sequence = rs.getInt("sequence");
			
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT idActivityGroup FROM activitygroup WHERE sequence < ? ORDER BY sequence DESC");
			stmt.setInt(1, sequence);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int idActivityGroup2 = rs.getInt("idActivityGroup");
				
				try{
					ConnectionDAO.getInstance().getConnection().setAutoCommit(false);
					
					stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE activitygroup SET sequence=? WHERE idActivityGroup=?");
					stmt.setInt(1, sequence);
					stmt.setInt(2, idActivityGroup2);
					stmt.execute();
					
					stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE activitygroup SET sequence=? WHERE idActivityGroup=?");
					stmt.setInt(1, sequence - 1);
					stmt.setInt(2, idActivityGroup);
					stmt.execute();
					
					ConnectionDAO.getInstance().getConnection().commit();
				}catch(SQLException e){
					ConnectionDAO.getInstance().getConnection().rollback();
					
					throw e;
				}finally{
					ConnectionDAO.getInstance().getConnection().setAutoCommit(true);
				}
			}
		}
	}
	
	public void moveDown(int idActivityGroup) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT sequence FROM activitygroup WHERE idActivityGroup=?");
		stmt.setInt(1, idActivityGroup);
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			int sequence = rs.getInt("sequence");
			
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT idActivityGroup FROM activitygroup WHERE sequence > ? ORDER BY sequence");
			stmt.setInt(1, sequence);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int idActivityGroup2 = rs.getInt("idActivityGroup");
				
				try{
					ConnectionDAO.getInstance().getConnection().setAutoCommit(false);
					
					stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE activitygroup SET sequence=? WHERE idActivityGroup=?");
					stmt.setInt(1, sequence);
					stmt.setInt(2, idActivityGroup2);
					stmt.execute();
					
					stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE activitygroup SET sequence=? WHERE idActivityGroup=?");
					stmt.setInt(1, sequence + 1);
					stmt.setInt(2, idActivityGroup);
					stmt.execute();
					
					ConnectionDAO.getInstance().getConnection().commit();
				}catch(SQLException e){
					ConnectionDAO.getInstance().getConnection().rollback();
					
					throw e;
				}finally{
					ConnectionDAO.getInstance().getConnection().setAutoCommit(true);
				}
			}
		}
	}

}
