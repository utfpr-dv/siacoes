package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SigesConfigDAO;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;

public class SigesConfigBO {

	public SigesConfig findByDepartment(int idDepartment) throws Exception{
		try{
			SigesConfigDAO dao = new SigesConfigDAO();
			
			SigesConfig config = dao.findByDepartment(idDepartment);
			
			if(config == null){
				config = new SigesConfig();
				config.getDepartment().setIdDepartment(idDepartment);
			}
			
			return config;
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(SigesConfig config) throws Exception{
		if((config.getDepartment() == null) || (config.getDepartment().getIdDepartment() == 0)){
			throw new Exception("Informe o departamento.");
		}
		if(config.getMinimumScore() <= 0){
			throw new Exception("A nota mínima para aprovação deve ser superior a zero.");
		}
		if(config.getCompanySupervisorPonderosity() <= 0){
			throw new Exception("O peso da nota do supervisor deve ser superior a zero.");
		}
		if(config.getSupervisorPonderosity() <= 0){
			throw new Exception("O peso da nota do orientador deve ser superior a zero.");
		}
		
		try{
			SigesConfigDAO dao = new SigesConfigDAO();
			
			return dao.save(config);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
}
