package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ReminderConfigDAO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage.ReminderType;
import br.edu.utfpr.dv.siacoes.model.ReminderConfig;

public class ReminderConfigBO {

	public List<ReminderConfig> list(int idDepartment, SystemModule module) throws Exception {
		try {
			ReminderConfigDAO dao = new ReminderConfigDAO();
			
			return dao.list(idDepartment, module);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public ReminderConfig find(int idDepartment, ReminderType type) throws Exception {
		try {
			ReminderConfigDAO dao = new ReminderConfigDAO();
			
			return dao.find(idDepartment, type);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, ReminderConfig config) throws Exception {
		try {
			if(config.isEnabled() && (config.getStartDays() <= 0)) {
				throw new Exception("Informe a quantidade de dias, semanas ou meses para o envio do lembrete.");
			}
			
			ReminderConfigDAO dao = new ReminderConfigDAO();
			
			return dao.save(idUser, config);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
