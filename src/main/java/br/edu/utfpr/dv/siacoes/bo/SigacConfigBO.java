package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SigacConfigDAO;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;

public class SigacConfigBO {
	
	public SigacConfig findByDepartment(int idDepartment) throws Exception{
		try{
			SigacConfigDAO dao = new SigacConfigDAO();
			
			SigacConfig config = dao.findByDepartment(idDepartment);
			
			if(config == null){
				config = new SigacConfig();
				config.getDepartment().setIdDepartment(idDepartment);
			}
			
			return config;
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(int idUser, SigacConfig config) throws Exception{
		if((config.getDepartment() == null) || (config.getDepartment().getIdDepartment() == 0)){
			throw new Exception("Informe o departamento.");
		}
		if(config.getMaxFileSize() < 0) {
			throw new Exception("O tamanho máximo para submissão de arquivos não pode ser inferior a zero.");
		}
		if(config.getMinimumScore() <= 0){
			throw new Exception("A nota mínima para aprovação deve ser superior a zero.");
		}
		
		try{
			SigacConfigDAO dao = new SigacConfigDAO();
			
			return dao.save(idUser, config);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}

}
