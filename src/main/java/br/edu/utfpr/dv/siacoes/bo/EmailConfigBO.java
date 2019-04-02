package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.EmailConfigDAO;
import br.edu.utfpr.dv.siacoes.model.EmailConfig;

public class EmailConfigBO {
	
	public EmailConfig loadConfiguration() throws Exception{
		try{
			EmailConfigDAO dao = new EmailConfigDAO();
			
			return dao.loadConfiguration();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, EmailConfig email) throws Exception {
		try{
			EmailConfigDAO dao = new EmailConfigDAO();
			
			return dao.save(idUser, email);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
