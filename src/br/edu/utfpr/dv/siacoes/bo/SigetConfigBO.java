package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SigetConfigDAO;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;

public class SigetConfigBO {
	
	public SigetConfig findByDepartment(int idDepartment) throws Exception{
		try{
			SigetConfigDAO dao = new SigetConfigDAO();
			
			SigetConfig config = dao.findByDepartment(idDepartment);
			
			if(config == null){
				config = new SigetConfig();
				config.getDepartment().setIdDepartment(idDepartment);
			}
			
			return config;
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(SigetConfig config) throws Exception{
		if((config.getDepartment() == null) || (config.getDepartment().getIdDepartment() == 0)){
			throw new Exception("Informe o departamento.");
		}
		if(config.getMinimumScore() <= 0){
			throw new Exception("A nota mínima para aprovação deve ser superior a zero.");
		}
		
		try{
			SigetConfigDAO dao = new SigetConfigDAO();
			
			return dao.save(config);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}

}
