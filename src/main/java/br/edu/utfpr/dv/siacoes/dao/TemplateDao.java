package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public abstract class TemplateDao{

	final void MethodTemplate() {
		
		findById();
		save();
		loadObject();
	}

	
	abstract void findById();
	abstract void save();
	abstract void loadObject();

}

public abstract class ActivityUnitDAO extends TemplateDao {
	
         private static final String SQLAct = 
            "SELECT * FROM activityunit WHERE idActivityUnit=?";
	
	void findById(){
		try (
		      Connection conn = ConnectionDAO.getInstance().getConnection();
		      PreparedStatement stmt = conn.prepareStatement(SQLAct);
	    ){
		stmt.setInt(1, id);
		try (ResultSet rs = stmt.executeQuery()){
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return;
			}}
		}
		 catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	void save(int idUser, ActivityUnit unit) {
		boolean insert = (unit.getIdActivityUnit() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO activityunit(description, fillAmount, amountDescription) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE activityunit SET description=?, fillAmount=?, amountDescription=? WHERE idActivityUnit=?");
			}
			
			stmt.setString(1, unit.getDescription());
			stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
			stmt.setString(3, unit.getAmountDescription());
			
			if(!insert){
				stmt.setInt(4, unit.getIdActivityUnit());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					unit.setIdActivityUnit(rs.getInt(1));
				}
				
				new UpdateEvent(conn).registerInsert(idUser, unit);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, unit);
			}
			
			return unit.getIdActivityUnit();
		}
		 catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	void loadObject(ResultSet rs) {
		ActivityUnit unit = new ActivityUnit();
		
		unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
		unit.setDescription(rs.getString("Description"));
		unit.setFillAmount(rs.getInt("fillAmount") == 1);
		unit.setAmountDescription(rs.getString("amountDescription"));
		
		return unit;
	}

}
	
	public class BugReportDAO extends TemplateDao {

		 
	         private static final String SQLBug = 
	            "SELECT bugreport.*, \"user\".name " + 
					"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
					"WHERE idBugReport = ?";
		
		void findById(int id){
			try (
			      Connection conn = ConnectionDAO.getInstance().getConnection();
			      PreparedStatement stmt = conn.prepareStatement(SQLBug);
		    ){
			stmt.setInt(1, id);
			
			try(ResultSet rs = stmt.executeQuery();
				
				if(rs.next()){
					return this.loadObject(rs);
				}else{
					return ;
				}
			}
			 catch (SQLException e) {
	                     throw new CloseException(e);
			}
		}
		
	
		
		void  save(BugReport bug) {
			boolean insert = (bug.getIdBugReport() == 0);
			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			try{
				conn = ConnectionDAO.getInstance().getConnection();
				
				if(insert){
					stmt = conn.prepareStatement("INSERT INTO bugreport(idUser, module, title, description, reportDate, type, status, statusDate, statusDescription) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				}else{
					stmt = conn.prepareStatement("UPDATE bugreport SET idUser=?, module=?, title=?, description=?, reportDate=?, type=?, status=?, statusDate=?, statusDescription=? WHERE idBugReport=?");
				}
				
				stmt.setInt(1, bug.getUser().getIdUser());
				stmt.setInt(2, bug.getModule().getValue());
				stmt.setString(3, bug.getTitle());
				stmt.setString(4, bug.getDescription());
				stmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
				stmt.setInt(6, bug.getType().getValue());
				stmt.setInt(7, bug.getStatus().getValue());
				if(bug.getStatus() == BugStatus.REPORTED){
					stmt.setNull(8, Types.DATE);
				}else{
					stmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
				}
				stmt.setString(9, bug.getStatusDescription());
				
				if(!insert){
					stmt.setInt(10, bug.getIdBugReport());
				}
				
				stmt.execute();
				
				if(insert){
					rs = stmt.getGeneratedKeys();
					
					if(rs.next()){
						bug.setIdBugReport(rs.getInt(1));
					}
				}
				
				return bug.getIdBugReport();
			}
			 catch (SQLException e) {
	                     throw new CloseException(e);
			}
		}
		
		void loadObject(ResultSet rs) {
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
	
public class DepartmentDAO extends TemplateDao {
        
        
         private static final String SQLDep = 
            "SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?";

  
	void findById(int id) {
        try (
		      Connection conn = ConnectionDAO.getInstance().getConnection();
		      PreparedStatement stmt = conn.prepareStatement(SQLDep);
	    ){
		stmt.setInt(1, id);
		try (ResultSet rs = stmt.executeQuery()) {
			
                        if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		} catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	
	
	void save(int idUser, Department department){
		boolean insert = (department.getIdDepartment() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE department SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? WHERE idDepartment=?");
			}
			
			stmt.setInt(1, department.getCampus().getIdCampus());
			stmt.setString(2, department.getName());
			if(department.getLogo() == null){
				stmt.setNull(3, Types.BINARY);
			}else{
				stmt.setBytes(3, department.getLogo());	
			}
			stmt.setInt(4, department.isActive() ? 1 : 0);
			stmt.setString(5, department.getSite());
			stmt.setString(6, department.getFullName());
			stmt.setString(7, department.getInitials());
			
			if(!insert){
				stmt.setInt(8, department.getIdDepartment());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					department.setIdDepartment(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, department);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, department);
			}
			
			return department.getIdDepartment();
		}
		}catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	void loadObject(ResultSet rs){
		Department department = new Department();
		
		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setInitials(rs.getString("initials"));
		
		return department;
	}
	
}
