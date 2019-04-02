package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SigetConfigDAO;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;

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
	
	public int save(int idUser, SigetConfig config) throws Exception{
		if((config.getDepartment() == null) || (config.getDepartment().getIdDepartment() == 0)) {
			throw new Exception("Informe o departamento.");
		}
		if(config.getMinimumScore() <= 0) {
			throw new Exception("A nota mínima para aprovação deve ser superior a zero.");
		}
		if((config.getMaxTutoredStage1() < 1) || (config.getMaxTutoredStage2() < 1)) {
			throw new Exception("O número máximo de orientados não deve ser inferior a 1.");
		}
		if(config.getMaxFileSize() < 0) {
			throw new Exception("O tamanho máximo para submissão de arquivos não pode ser inferior a zero.");
		}
		if(config.getJuryTimeStage1() < 0) {
			throw new Exception("O tempo previsto de duração da banca de TCC 1 não pode ser inferior à zero.");
		}
		if(config.getJuryTimeStage2() < 0) {
			throw new Exception("O tempo previsto de duração da banca de TCC 2 não pode ser inferior à zero.");
		}
		if(config.getSupervisorIndication() < 0) {
			config.setSupervisorIndication(0);
		} else if(config.getSupervisorIndication() > 10) {
			config.setSupervisorIndication(10);
		}
		if(config.getMinimumJuryMembers() < 0) {
			config.setMinimumJuryMembers(0);
		}
		if(config.getMinimumJurySubstitutes() < 0) {
			config.setMinimumJurySubstitutes(0);
		}
		
		try{
			SigetConfigDAO dao = new SigetConfigDAO();
			
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
	
	public SupervisorFilter getCosupervisorFilter(int idDepartment) {
		try {
			return this.findByDepartment(idDepartment).getCosupervisorFilter();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			return SupervisorFilter.DEPARTMENT;
		}
	}

}
