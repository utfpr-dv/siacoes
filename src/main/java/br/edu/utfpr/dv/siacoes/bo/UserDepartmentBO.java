package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.UserDepartmentDAO;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class UserDepartmentBO {

	public UserDepartment findById(int id) throws Exception {
		try {
			UserDepartmentDAO dao = new UserDepartmentDAO();
			
			return dao.findById(id);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public UserDepartment find(int idUser, UserProfile profile, int idDepartment) throws Exception {
		try {
			UserDepartmentDAO dao = new UserDepartmentDAO();
			
			return dao.find(idUser, profile, idDepartment);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<UserDepartment> list(int idUser) throws Exception {
		try {
			UserDepartmentDAO dao = new UserDepartmentDAO();
			
			return dao.list(idUser);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<UserDepartment> list(int idUser, UserProfile profile) throws Exception {
		try {
			UserDepartmentDAO dao = new UserDepartmentDAO();
			
			return dao.list(idUser, profile);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(int idUser, UserDepartment user) throws Exception {
		if((user.getUser() == null) || (user.getUser().getIdUser() == 0)) {
			throw new Exception("Informe o usuário.");
		}
		if((user.getDepartment() == null) || (user.getDepartment().getIdDepartment() == 0)) {
			throw new Exception("Informe a coordenação/departamento.");
		}
		
		if(user.getProfile() == UserProfile.STUDENT) {
			user.setDepartmentManager(false);
			user.setSigacManager(false);
			user.setSigesManager(false);
			user.setSigetManager(false);
			
			if((user.getRegisterSemester() <= 0) || (user.getRegisterSemester() >= 3)) {
				throw new Exception("Informe o semestre de ingresso.");
			}
			if(user.getRegisterYear() <= 1990) {
				throw new Exception("Informe o ano de ingresso.");
			}
			if(user.getRegisterYear() > DateUtils.getYear()) {
				throw new Exception("O ano de ingresso não pode ser superior ao ano atual.");
			}
		}
		
		
		try {
			UserDepartmentDAO dao = new UserDepartmentDAO();
			
			return dao.save(idUser, user);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
}
