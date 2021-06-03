package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ReminderConfig.Frequency;
import br.edu.utfpr.dv.siacoes.model.ReminderConfig.StartType;
import br.edu.utfpr.dv.siacoes.model.ReminderConfig;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage.ReminderType;

public class ReminderConfigDAO {
	
	public ReminderConfig find(int idDepartment, ReminderType type) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT reminderconfig.idreminderconfig, " + String.valueOf(idDepartment) + " AS iddepartment, remindermessage.idremindermessage, " +
					"reminderconfig.enabled, reminderconfig.startDays, reminderconfig.startType, reminderconfig.frequency FROM remindermessage " +
					"LEFT JOIN reminderconfig ON (reminderconfig.idreminder=remindermessage.idremindermessage AND reminderconfig.iddepartment=" + String.valueOf(idDepartment) + ") " +
					"WHERE remindermessage.idremindermessage=" + String.valueOf(type.getValue()));
			
			if(rs.next()) {
				return this.loadObject(rs);
			} else {
				return null;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<ReminderConfig> list(int idDepartment, SystemModule module) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT reminderconfig.idreminderconfig, " + String.valueOf(idDepartment) + " AS iddepartment, remindermessage.idremindermessage, " +
					"reminderconfig.enabled, reminderconfig.startDays, reminderconfig.startType, reminderconfig.frequency FROM remindermessage " +
					"LEFT JOIN reminderconfig ON (reminderconfig.idreminder=remindermessage.idremindermessage AND reminderconfig.iddepartment=" + String.valueOf(idDepartment) + ") " +
					"WHERE remindermessage.module=" + String.valueOf(module.getValue()) + " ORDER BY remindermessage.idremindermessage");
			
			List<ReminderConfig> list = new ArrayList<ReminderConfig>();
			
			while(rs.next()) {
				list.add(this.loadObject(rs));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(int idUser, ReminderConfig config) throws SQLException {
		boolean insert = (config.getIdReminderConfig() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert) {
				stmt = conn.prepareStatement("INSERT INTO reminderconfig(iddepartment, idreminder, enabled, startDays, startType, frequency) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			} else {
				stmt = conn.prepareStatement("UPDATE reminderconfig SET iddepartment=?, idreminder=?, enabled=?, startDays=?, startType=?, frequency=? WHERE idreminderconfig=?");
			}
			
			stmt.setInt(1, config.getDepartment().getIdDepartment());
			stmt.setInt(2, config.getReminder().getIdReminderMessage().getValue());
			stmt.setInt(3, config.isEnabled() ? 1 : 0);
			stmt.setInt(4, config.getStartDays());
			stmt.setInt(5, config.getStartType().getValue());
			stmt.setInt(6, config.getFrequency().getValue());
			
			if(!insert) {
				stmt.setInt(7, config.getIdReminderConfig());
			}
			
			stmt.execute();
			
			if(insert) {
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()) {
					config.setIdReminderConfig(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, config);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, config);
			}
			
			return config.getIdReminderConfig();
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private ReminderConfig loadObject(ResultSet rs) throws SQLException {
		ReminderConfig config = new ReminderConfig();
		
		if(rs.getInt("idReminderConfig") > 0) {	
			config.setIdReminderConfig(rs.getInt("idReminderConfig"));
			config.getDepartment().setIdDepartment(rs.getInt("iddepartment"));
			config.getReminder().setIdReminderMessage(ReminderType.valueOf(rs.getInt("idremindermessage")));
			config.setEnabled(rs.getInt("enabled") == 1);
			config.setStartDays(rs.getInt("startDays"));
			config.setStartType(StartType.valueOf(rs.getInt("startType")));
			config.setFrequency(Frequency.valueOf(rs.getInt("frequency")));
		} else {
			config.getDepartment().setIdDepartment(rs.getInt("iddepartment"));
			config.getReminder().setIdReminderMessage(ReminderType.valueOf(rs.getInt("idremindermessage")));
		}
		
		return config;
	}

}
