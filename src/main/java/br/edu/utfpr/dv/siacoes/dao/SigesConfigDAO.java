package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigesConfig.JuryFormat;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;

public class SigesConfigDAO {

	public SigesConfig findByDepartment(int idDepartment) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM sigesconfig WHERE idDepartment = ?");
		
			stmt.setInt(1, idDepartment);
			
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
	
	public int save(int idUser, SigesConfig config) throws SQLException{
		boolean insert = (this.findByDepartment(config.getDepartment().getIdDepartment()) == null);
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO sigesconfig(minimumScore, supervisorPonderosity, companySupervisorPonderosity, showgradestostudent, supervisorfilter, supervisorFillJuryForm, maxfilesize, jurytime, fillOnlyTotalHours, juryformat, useDigitalSignature, appraiserFillsGrades, minimumJuryMembers, minimumJurySubstitutes, idDepartment) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			}else{
				stmt = conn.prepareStatement("UPDATE sigesconfig SET minimumScore=?, supervisorPonderosity=?, companySupervisorPonderosity=?, showgradestostudent=?, supervisorfilter=?, supervisorFillJuryForm=?, maxfilesize=?, jurytime=?, fillOnlyTotalHours=?, juryformat=?, useDigitalSignature=?, appraiserFillsGrades=?, minimumJuryMembers=?, minimumJurySubstitutes=? WHERE idDepartment=?");
			}
			
			stmt.setDouble(1, config.getMinimumScore());
			stmt.setDouble(2, config.getSupervisorPonderosity());
			stmt.setDouble(3, config.getCompanySupervisorPonderosity());
			stmt.setInt(4, config.isShowGradesToStudent() ? 1 : 0);
			stmt.setInt(5, config.getSupervisorFilter().getValue());
			stmt.setInt(6, config.isSupervisorFillJuryForm() ? 1 : 0);
			stmt.setInt(7, config.getMaxFileSize());
			stmt.setInt(8, config.getJuryTime());
			stmt.setInt(9, (config.isFillOnlyTotalHours() ? 1 : 0));
			stmt.setInt(10, config.getJuryFormat().getValue());
			stmt.setInt(11, config.isUseDigitalSignature() ? 1 : 0);
			stmt.setInt(12, config.isAppraiserFillsGrades() ? 1 : 0);
			stmt.setInt(13, config.getMinimumJuryMembers());
			stmt.setInt(14, config.getMinimumJurySubstitutes());
			stmt.setInt(15, config.getDepartment().getIdDepartment());
			
			stmt.execute();
			
			new UpdateEvent(conn).registerUpdate(idUser, config);
			
			return config.getDepartment().getIdDepartment();
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private SigesConfig loadObject(ResultSet rs) throws SQLException{
		SigesConfig config = new SigesConfig();
		
		config.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		config.setMinimumScore(rs.getDouble("minimumScore"));
		config.setSupervisorPonderosity(rs.getDouble("supervisorPonderosity"));
		config.setCompanySupervisorPonderosity(rs.getDouble("companySupervisorPonderosity"));
		config.setShowGradesToStudent(rs.getInt("showgradestostudent") == 1);
		config.setSupervisorFilter(SupervisorFilter.valueOf(rs.getInt("supervisorfilter")));
		config.setSupervisorFillJuryForm(rs.getInt("supervisorFillJuryForm") == 1);
		config.setMaxFileSize(rs.getInt("maxfilesize"));
		config.setJuryTime(rs.getInt("jurytime"));
		config.setFillOnlyTotalHours(rs.getInt("fillOnlyTotalHours") == 1);
		config.setJuryFormat(JuryFormat.valueOf(rs.getInt("juryformat")));
		config.setUseDigitalSignature(rs.getInt("useDigitalSignature") == 1);
		config.setAppraiserFillsGrades(rs.getInt("appraiserFillsGrades") == 1);
		config.setMinimumJuryMembers(rs.getInt("minimumJuryMembers"));
		config.setMinimumJurySubstitutes(rs.getInt("minimumJurySubstitutes"));
		
		return config;
	}
	
}
