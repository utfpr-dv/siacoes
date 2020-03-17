package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SigesConfigDAO;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;

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
	
	public int save(int idUser, SigesConfig config) throws Exception{
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
		if(config.getMaxFileSize() < 0) {
			throw new Exception("O tamanho máximo para submissão de arquivos não pode ser inferior a zero.");
		}
		if(config.getJuryTime() < 0) {
			throw new Exception("O tempo previsto de duração da banca de estágio não pode ser inferior à zero.");
		}
		if(config.getMinimumJuryMembers() < 0) {
			config.setMinimumJuryMembers(0);
		}
		if(config.getMinimumJurySubstitutes() < 0) {
			config.setMinimumJurySubstitutes(0);
		}
		
		try{
			SigesConfigDAO dao = new SigesConfigDAO();
			
			return dao.save(idUser, config);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public SupervisorFilter getSupervisorFilter(int idDepartment) {
		try {
			return this.findByDepartment(idDepartment).getSupervisorFilter();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			return SupervisorFilter.DEPARTMENT;
		}
	}
	
}
