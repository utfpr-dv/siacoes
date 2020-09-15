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

public class DepartmentDAO extends TemplateDao {
    
    
 
public Department findById(int id) throws SQLException{
    try (
	      Connection conn = ConnectionDAO.getInstance().getConnection();
	      PreparedStatement stmt = conn.prepareStatement("SELECT department.*, campus.name AS campusName " +
	  			"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?");
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
    
    }
}
