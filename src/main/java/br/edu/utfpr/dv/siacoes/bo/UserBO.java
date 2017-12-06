package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.UserDAO;
import br.edu.utfpr.dv.siacoes.ldap.LdapConfig;
import br.edu.utfpr.dv.siacoes.ldap.LdapUtils;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

public class UserBO {
	
	public List<User> listAll(boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listAll(onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listAllProfessors(boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listAllProfessors(onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listProfessorsByDepartment(int idDepartment, boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listProfessorsByDepartment(idDepartment, onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listProfessorsByCampus(int idCampus, boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listProfessorsByCampus(idCampus, onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listAllStudents(boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listAllStudents(onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listStudentBySupervisor(int idSupervisor) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listStudentBySupervisor(idSupervisor);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listAllCompanySupervisors(boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listAllCompanySupervisors(onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listSupervisorsByCompany(int idCompany, boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listSupervisorsByCompany(idCompany, onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> list(String name, int profile, boolean onlyActives, boolean onlyExternal) throws Exception {
		try{
			UserDAO dao = new UserDAO();
			
			return dao.list(name.trim(), profile, onlyActives, onlyExternal);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User findManager(int idDepartment, SystemModule module) throws Exception{
		try{
			UserDAO dao = new UserDAO();
			
			return dao.findManager(idDepartment, module);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User findDepartmentManager(int idDepartment) throws Exception{
		try{
			UserDAO dao = new UserDAO();
			
			return dao.findDepartmentManager(idDepartment);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(User user) throws Exception{
		try {
			if((user.getProfile() != UserProfile.COMPANYSUPERVISOR) && (user.getLogin().isEmpty())){
				throw new Exception("Informe o login.");
			}
			if((user.getProfile() != UserProfile.COMPANYSUPERVISOR) && (user.getPassword().isEmpty())){
				throw new Exception("Informe a senha.");
			}
			if(user.getName().isEmpty()){
				throw new Exception("Informe o nome.");
			}
			if((user.getProfile() == UserProfile.COMPANYSUPERVISOR) && ((user.getCompany() == null) || (user.getCompany().getIdCompany() == 0))){
				throw new Exception("Informe a empresa.");
			}
			
			UserDAO dao = new UserDAO();
			
			return dao.save(user);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	private boolean loginIsStudent(String login){
		if(login.toLowerCase().startsWith("a")){
			login = login.substring(1);
			
			try{
				Integer.parseInt(login);
				
				return true;
			}catch(Exception e){
				return false;
			}
		}
		
		return false;
	}
	
	public User findByLogin(String login) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.findByLogin(login);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User findById(int id) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public String findEmail(int idUser) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.findEmail(idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User changePassword(int idUser, String currentPassword, String newPassword) throws Exception{
		try {
			String currentHash = StringUtils.generateSHA3Hash(currentPassword);
			String newHash = StringUtils.generateSHA3Hash(newPassword);
			
			UserDAO dao = new UserDAO();
			User user = dao.findById(idUser);
			
			if(user == null){
				throw new Exception("Usuário não encontrado.");
			}
			if(!user.isExternal()){
				throw new Exception("A alteração de senha é permitida apenas para usuários externos. Alunos e Professores devem alterar a senha através do Sistema Acadêmico.");
			}
			if(!user.getPassword().equals(currentHash)){
				throw new Exception("A senha atual não confere.");
			}
			if(currentPassword.equals(newPassword)){
				throw new Exception("A nova senha não deve ser igual à senha atual.");
			}
			
			user.setPassword(newHash);
			dao.save(user);
			
			return user;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User validateLogin(String login, String password) throws Exception{
		String hash = StringUtils.generateSHA3Hash(password);
		
		if(login.contains("@")){
			login = login.substring(0, login.indexOf("@"));
		}
		
		login = login.toLowerCase().trim();
		
		User user = this.findByLogin(login);
		
		if(user == null){
			user = new User();
			user.setExternal(false);
		}
		
		if(user.isExternal()){
			if(!user.getPassword().equals(hash)){
				throw new Exception("Usuário ou senha inválidos.");
			}
		}else{
			LdapUtils ldapUtils = new LdapUtils(LdapConfig.getInstance().getHost(), 
					LdapConfig.getInstance().getPort(), LdapConfig.getInstance().isUseSSL(), 
					LdapConfig.getInstance().isIgnoreCertificates(), LdapConfig.getInstance().getBasedn(), 
					LdapConfig.getInstance().getUidField());
			
			try{
				ldapUtils.authenticate(login, password);
				
				Map<String, String> dataLdap = ldapUtils.getLdapProperties(login);

				//String cnpjCpf = dataLdap.get(LdapConfig.getInstance().getCpfField());
				//String matricula = dataLdap.get(LdapConfig.getInstance().getRegisterField());
				String name = this.formatName(dataLdap.get(LdapConfig.getInstance().getNameField()));
				String email = dataLdap.get(LdapConfig.getInstance().getEmailField());
				
				user.setPassword(hash);
				
				if(user.getIdUser() == 0){
					user.setName(name);
					user.setEmail(email);
					user.setLogin(login.toLowerCase());
					user.setInstitution("UTFPR");
					user.setExternal(false);
					if(this.loginIsStudent(login)){
						user.setProfile(UserProfile.STUDENT);
						user.setStudentCode(login.toLowerCase().replace("a", ""));
					}else{
						user.setProfile(UserProfile.PROFESSOR);
					}
				}
				
				this.save(user);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				throw new Exception("Usuário ou senha inválidos." + System.lineSeparator() + "Detalhes: " + e.getMessage());
			}
		}
		
		return user;
	}
	
	private String formatName(String name){
		String[] list = name.trim().split(" ");
		List<String> list2 = new ArrayList<String>();
		
		for(String s : list){
			if(s.length() > 2){
				s = s.charAt(0) + s.substring(1).toLowerCase();	
			}else{
				s = s.toLowerCase();
			}
			
			list2.add(s);
		}
		
		return String.join(" ", list2);
	}
	
	public String formatLoginFromStudentCode(String studentCode){
		String login = "";
		
		studentCode = studentCode.trim();
		
		for(int i = 0; i < studentCode.length(); i++){
			if(Character.isDigit(studentCode.charAt(i))){
				login = login + studentCode.charAt(i);
			}
		}
		
		while((login.length() > 0) && (login.charAt(0) == '0')){
			if(login.length() > 1){
				login = login.substring(1);	
			} else {
				login = "";
			}
		}
		
		login = "a" + login;
		
		return login;
	}
	
	public String[] findEmails(int[] ids) throws Exception{
		UserDAO dao = new UserDAO();
		
		return dao.findEmails(ids);
	}
	
}
