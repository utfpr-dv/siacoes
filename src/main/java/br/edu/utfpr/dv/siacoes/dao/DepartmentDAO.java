package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Department;
//Em primeiro momento foi constatado que o código abaixo, este bem detalhado,
//fácil entendimento e visualização, porem há muitas validações para fechamento de conexão com banco de dados, então visto isso
//encontrei uma outra maneira para fazer, utilizando try-with-resources


public class DepartmentDAO {
        
        //criado clase privada chamando a consulta do departamento e ao gerar a conexão utilizamos a variavel criada
         private static final String SQLDep = 
            "SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?";

   //neste momento iremos usar try-with-resources
	public Department findById(int id) throws SQLException{
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
			}//ao inves de usar o finally ou mesmo chamar o método close(), para fechamento de recursos, 
			 //usamos apenas catch (SQLException e)
		} catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	public List<Department> listAll(boolean onlyActive) throws SQLException{
		try (
             Connection conn = ConnectionDAO.getInstance().getConnection();
             Statement stmt = conn.createStatement();
			){

		try (ResultSet rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " + 
					(onlyActive ? " WHERE department.active=1" : "") + " ORDER BY department.name");
			
			List<Department> list = new ArrayList<Department>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			return list;
			//ao inves de usar o finally ou mesmo chamar o método close(), para fechamento de recursos, 
			 //usamos apenas catch (SQLException e)
		}catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
		try (
             Connection conn = ConnectionDAO.getInstance().getConnection();
             Statement stmt = conn.createStatement();
			){
		
		try (ResultSet rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE department.idCampus=" + String.valueOf(idCampus) + (onlyActive ? " AND department.active=1" : "") + " ORDER BY department.name");
			
			List<Department> list = new ArrayList<Department>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}//ao inves de usar o finally ou mesmo chamar o método close(), para fechamento de recursos, 
			 //usamos apenas catch (SQLException e)
		  catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	public int save(int idUser, Department department) throws SQLException{
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
		}//ao inves de usar o finally ou mesmo chamar o método close(), para fechamento de recursos, 
			 //usamos apenas catch (SQLException e)
		}catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	private Department loadObject(ResultSet rs) throws SQLException{
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
