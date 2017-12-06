package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Activity;

public class ActivityDAO {
	
	public List<Activity> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT activity.*, activityunit.description AS unit, activitygroup.description AS group, activitygroup.sequence AS groupSequence " +
					"FROM activity INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
					"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
					"ORDER BY activitygroup.sequence, activity.sequence");
			
			List<Activity> list = new ArrayList<Activity>();
			
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
	
	public List<Activity> listByDepartment(int idDepartment) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT activity.*, activityunit.description AS unit, activitygroup.description AS groupDescription, activitygroup.sequence AS groupSequence " +
					"FROM activity INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
					"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
					"WHERE activity.idDepartment=? ORDER BY activitygroup.sequence, activity.sequence");
			
			stmt.setInt(1, idDepartment);
					
			rs = stmt.executeQuery();
			
			List<Activity> list = new ArrayList<Activity>();
			
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
	
	public List<Activity> listByGroup(int idDepartment, int idGroup) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT activity.*, activityunit.description AS unit, activitygroup.description AS groupDescription, activitygroup.sequence AS groupSequence " +
				"FROM activity INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
				"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
				"WHERE activity.idDepartment=? AND activity.idActivityGroup=? ORDER BY activity.sequence");
		
			stmt.setInt(1, idDepartment);
			stmt.setInt(2, idGroup);
					
			rs = stmt.executeQuery();
			
			List<Activity> list = new ArrayList<Activity>();
			
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
	
	public Activity findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT activity.*, activityunit.description AS unit, activitygroup.description AS groupDescription, activitygroup.sequence AS groupSequence " +
				"FROM activity INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
				"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
				"WHERE activity.idActivity=?");
		
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
	
	public int save(Activity activity) throws SQLException{
		boolean insert = (activity.getIdActivity() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO activity(idDepartment, idActivityGroup, idActivityUnit, description, score, maximumInSemester, active, sequence) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				
				Statement stmt2 = conn.createStatement();
				rs = stmt2.executeQuery("SELECT COUNT(*) as total FROM activity WHERE idActivityGroup=" + String.valueOf(activity.getGroup().getIdActivityGroup()) + " AND idDepartment=" + String.valueOf(activity.getDepartment().getIdDepartment()));
				rs.next();
				activity.setSequence(rs.getInt("total") + 1);
				stmt2.close();
				rs.close();
			}else{
				stmt = conn.prepareStatement("UPDATE activity SET idDepartment=?, idActivityGroup=?, idActivityUnit=?, description=?, score=?, maximumInSemester=?, active=?, sequence=? WHERE idActivity=?");
			}
			
			stmt.setInt(1, activity.getDepartment().getIdDepartment());
			stmt.setInt(2, activity.getGroup().getIdActivityGroup());
			stmt.setInt(3, activity.getUnit().getIdActivityUnit());
			stmt.setString(4, activity.getDescription());
			stmt.setDouble(5, activity.getScore());
			stmt.setDouble(6, activity.getMaximumInSemester());
			stmt.setInt(7, (activity.isActive() ? 1 : 0));
			stmt.setInt(8, activity.getSequence());
			
			if(!insert){
				stmt.setInt(9, activity.getIdActivity());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					activity.setIdActivity(rs.getInt(1));
				}
			}
			
			return activity.getIdActivity();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Activity loadObject(ResultSet rs) throws SQLException{
		Activity activity = new Activity();

		activity.setIdActivity(rs.getInt("idActivity"));
		activity.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		activity.getGroup().setIdActivityGroup(rs.getInt("idActivityGroup"));
		activity.getUnit().setIdActivityUnit(rs.getInt("idActivityUnit"));
		activity.setDescription(rs.getString("description"));
		activity.setActive(rs.getInt("active") == 1);
		activity.setMaximumInSemester(rs.getDouble("maximumInSemester"));
		activity.setScore(rs.getDouble("score"));
		activity.setSequence(rs.getInt("sequence"));
		activity.getUnit().setDescription(rs.getString("unit"));
		activity.getGroup().setDescription(rs.getString("groupDescription"));
		activity.getGroup().setSequence(rs.getInt("groupSequence"));
		
		return activity;
	}
	
	public void moveUp(int idActivity) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT sequence, idActivityGroup FROM activity WHERE idActivity=?");
			stmt.setInt(1, idActivity);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int sequence = rs.getInt("sequence");
				int idActivityGroup = rs.getInt("idActivityGroup");
				
				rs.close();
				stmt.close();
				stmt = conn.prepareStatement("SELECT idActivity FROM activity WHERE idActivityGroup=? AND sequence < ? ORDER BY sequence DESC");
				stmt.setInt(1, idActivityGroup);
				stmt.setInt(2, sequence);
				rs = stmt.executeQuery();
				
				if(rs.next()){
					int idActivity2 = rs.getInt("idActivity");
					
					try{
						conn.setAutoCommit(false);
						
						stmt = conn.prepareStatement("UPDATE activity SET sequence=? WHERE idActivity=?");
						stmt.setInt(1, sequence);
						stmt.setInt(2, idActivity2);
						stmt.execute();
						
						stmt = conn.prepareStatement("UPDATE activity SET sequence=? WHERE idActivity=?");
						stmt.setInt(1, sequence - 1);
						stmt.setInt(2, idActivity);
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
	
	public void moveDown(int idActivity) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT sequence, idActivityGroup FROM activity WHERE idActivity=?");
			stmt.setInt(1, idActivity);
			rs = stmt.executeQuery();
			
			if(rs.next()){
				int sequence = rs.getInt("sequence");
				int idActivityGroup = rs.getInt("idActivityGroup");
				
				rs.close();
				stmt.close();
				stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT idActivity FROM activity WHERE idActivityGroup=? AND sequence > ? ORDER BY sequence");
				stmt.setInt(1, idActivityGroup);
				stmt.setInt(2, sequence);
				rs = stmt.executeQuery();
				
				if(rs.next()){
					int idActivity2 = rs.getInt("idActivity");
					
					try{
						conn.setAutoCommit(false);
						
						stmt = conn.prepareStatement("UPDATE activity SET sequence=? WHERE idActivity=?");
						stmt.setInt(1, sequence);
						stmt.setInt(2, idActivity2);
						stmt.execute();
						
						stmt = conn.prepareStatement("UPDATE activity SET sequence=? WHERE idActivity=?");
						stmt.setInt(1, sequence + 1);
						stmt.setInt(2, idActivity);
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
