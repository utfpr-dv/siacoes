import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;




public class BugReportDAO extends TemplateDao {


         
	public BugReport findById(int id) throws SQLException{
		
		try (
			      Connection conn = ConnectionDAO.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT bugreport.*, \"user\".name " + 
				"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
				"WHERE idBugReport = ?"))
						{

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
