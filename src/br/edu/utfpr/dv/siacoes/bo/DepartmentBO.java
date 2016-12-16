package br.edu.utfpr.dv.siacoes.bo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.DepartmentDAO;
import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentBO {
	
	public Department findById(int id) throws Exception{
		try{
			DepartmentDAO dao = new DepartmentDAO();
			
			return dao.findById(id);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Department> listAll(boolean onlyActive) throws Exception{
		try{
			DepartmentDAO dao = new DepartmentDAO();
			
			return dao.listAll(onlyActive);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws Exception{
		try{
			DepartmentDAO dao = new DepartmentDAO();
			
			return dao.listByCampus(idCampus, onlyActive);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(Department department) throws Exception{
		if((department.getCampus() == null) || (department.getCampus().getIdCampus() == 0)){
			throw new Exception("Informe o câmpus do departamento.");
		}
		if(department.getName().isEmpty()){
			throw new Exception("Informe o nome do departamento.");
		}
		if((department.getSigacMinimumScore() < 0) || (department.getSigacMinimumScore() > 100)){
			throw new Exception("A pontuação mínima das atividades complementares deve estar entre 0 e 100.");
		}
		if((department.getSigetMinimumScore() < 0) || (department.getSigetMinimumScore() > 10)){
			throw new Exception("A nota mínima para aprovação no TCC deve estar entre 0 e 10.");
		}
		
		try{
			DepartmentDAO dao = new DepartmentDAO();
			
			return dao.save(department);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
