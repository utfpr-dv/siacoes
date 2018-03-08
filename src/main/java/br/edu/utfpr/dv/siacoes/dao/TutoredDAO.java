package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.TutoredBySupervisor;

public class TutoredDAO {
	
	public List<TutoredBySupervisor> listTutoredBySupervisor(int idDepartment, int initialYear, int finalYear, int stage) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			List<TutoredBySupervisor> list = new ArrayList<TutoredBySupervisor>();
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			String sql = "SELECT idSupervisor, supervisorName, semester, year, SUM(total) AS total FROM(";
			
			if((stage == 0) || (stage == 1)) {
				sql += "SELECT COALESCE(newsupervisor.idUser, supervisor.idUser) AS idSupervisor, COALESCE(newsupervisor.name, supervisor.name) AS supervisorName, " + 
						"proposal.semester, proposal.year, COUNT(*) AS total " + 
						"FROM proposal INNER JOIN \"user\" supervisor ON supervisor.idUser=proposal.idSupervisor " +
						"LEFT JOIN project ON project.idProposal=proposal.idProposal " + 
						"LEFT JOIN \"user\" newsupervisor ON newsupervisor.idUser=project.idSupervisor " +
						"WHERE proposal.invalidated=0 AND proposal.idDepartment=" + String.valueOf(idDepartment) + 
						"GROUP BY proposal.year, proposal.semester, COALESCE(newsupervisor.idUser, supervisor.idUser), COALESCE(newsupervisor.name, supervisor.name)";
			}
			if(stage == 0) {
				sql += " UNION ALL ";
			}
			if((stage == 0) || (stage == 2)) {
				sql += "SELECT COALESCE(newsupervisor.idUser, supervisor.idUser) AS idSupervisor, COALESCE(newsupervisor.name, supervisor.name) AS supervisorName, " + 
						"CASE project.semester WHEN 1 THEN 2 ELSE 1 END AS semester, CASE project.semester WHEN 1 THEN project.year ELSE project.year + 1 END AS year, COUNT(*) AS total " + 
						"FROM project INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " + 
						"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
						"INNER JOIN finaldocument ON finaldocument.idProject=project.idProject " + 
						"LEFT JOIN supervisorchange ON supervisorchange.idProposal=proposal.idProposal " + 
						"LEFT JOIN \"user\" newsupervisor ON newsupervisor.idUser=supervisorchange.idNewSupervisor " +
						"WHERE finaldocument.supervisorFeedback=1 AND proposal.idDepartment=" + String.valueOf(idDepartment) + 
						"GROUP BY project.year, project.semester, COALESCE(newsupervisor.idUser, supervisor.idUser), COALESCE(newsupervisor.name, supervisor.name)";
			}
			
			sql += ") AS temp WHERE year BETWEEN " + String.valueOf(initialYear) + " AND " + String.valueOf(finalYear) + 
					" GROUP BY year, semester, idSupervisor, supervisorName " + 
					"ORDER BY year, semester, supervisorName";
			
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				TutoredBySupervisor tutored = new TutoredBySupervisor();
				
				tutored.setIdSupervisor(rs.getInt("idSupervisor"));
				tutored.setSupervisorName(rs.getString("supervisorName"));
				tutored.setSemester(rs.getInt("semester"));
				tutored.setYear(rs.getInt("year"));
				tutored.setTotal(rs.getInt("total"));
				
				list.add(tutored);
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

}
