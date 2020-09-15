package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;

import br.edu.utfpr.dv.siacoes.model.Department;


public class CloseException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CloseException(Throwable e) {
        super(e);
    }
}    

public class ActivityUnitDAO extends TemplateDao {
	
	
	
	public List<ActivityUnit> listAll() throws SQLException{
		try(
            Connection conn = ConnectionDAO.getInstance().getConnection();
		    PreparedStatement stmt = (PreparedStatement) conn.createStatement();
			){
		
		try (ResultSet rs = stmt.executeQuery("SELECT * FROM activityunit ORDER BY description")){
			
			List<ActivityUnit> list = new ArrayList<ActivityUnit>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
		
			
			return list;
		}
		 catch (SQLException e) {
                     throw new CloseException(e);
		}
	  }
	}
}
	
