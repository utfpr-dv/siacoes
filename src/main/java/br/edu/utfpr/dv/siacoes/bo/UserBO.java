package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.CommunicationException;

import br.edu.utfpr.dv.siacoes.dao.UserDAO;
import br.edu.utfpr.dv.siacoes.ldap.LdapConfig;
import br.edu.utfpr.dv.siacoes.ldap.LdapUtils;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.Credential;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.service.LoginService;
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
	
	public List<User> listAllSupervisors(boolean onlyActives, boolean onlyExternal) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listAllSupervisors(onlyActives, onlyExternal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listInstitutionalSupervisors(boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listInstitutionalSupervisors(onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listSupervisorsByDepartment(int idDepartment, boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listSupervisorsByDepartment(idDepartment, onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<User> listSupervisorsByCampus(int idCampus, boolean onlyActives) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.listSupervisorsByCampus(idCampus, onlyActives);
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
	
	public User findSystemAdmin() throws Exception{
		try{
			UserDAO dao = new UserDAO();
			
			return dao.findSystemAdmin();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, User user) throws Exception {
		int ret = 0;
		boolean isInsert = (user.getIdUser() == 0);
		
		user.setLogin(user.getLogin().trim());
		user.setEmail(user.getEmail().trim());
		
		try {
			if(isInsert && user.getPassword().isEmpty()) {
				user.setSalt(StringUtils.generateSalt());
				user.setPassword(StringUtils.generateSHA3Hash(StringUtils.generateSalt() + user.getSalt()));
			}
			if((user.getProfiles() == null) || (user.getProfiles().size() == 0)){
				throw new Exception("Informe ao menos um perfil para o usuário.");
			}
			if(((!user.hasProfile(UserProfile.COMPANYSUPERVISOR) && !user.hasProfile(UserProfile.SUPERVISOR)) || (user.getProfiles().size() > 1)) && (user.getLogin().isEmpty())){
				throw new Exception("Informe o login.");
			}
			if(((!user.hasProfile(UserProfile.COMPANYSUPERVISOR) && !user.hasProfile(UserProfile.SUPERVISOR)) || (user.getProfiles().size() > 1)) && (user.getPassword().isEmpty())){
				throw new Exception("Informe a senha.");
			}
			if(user.getName().isEmpty()){
				throw new Exception("Informe o nome.");
			}
			if(user.hasProfile(UserProfile.COMPANYSUPERVISOR) && ((user.getCompany() == null) || (user.getCompany().getIdCompany() == 0))){
				throw new Exception("Informe a empresa.");
			}
			if((user.getPhoto() != null) && (user.getPhoto().length > (300 * 1024))) {
				throw new Exception("A foto de perfil deve ter no máximo 300 KB.");
			}
			if(user.isExternal() && (user.getEmail().endsWith("@utfpr.edu.br"))) {
				throw new Exception("Servidores da UTFPR devem efetuar seu cadastro acessando o sistema via login e senha do LDAP");
			}
			
			UserDAO dao = new UserDAO();
			
			if(dao.loginExists(user.getLogin(), user.getIdUser())) {
				throw new Exception("O login informado já está em uso.");
			}
			if(dao.emailExists(user.getEmail(), user.getIdUser())) {
				throw new Exception("O e-mail informado já está sendo utilizado.");
			}
			
			ret = dao.save(idUser, user);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
		
		if(isInsert) {
			try {
				EmailMessageBO bo = new EmailMessageBO();
				List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
				
				keys.add(new EmailMessageEntry<String, String>("name", user.getName()));
				keys.add(new EmailMessageEntry<String, String>("email", user.getEmail()));
				keys.add(new EmailMessageEntry<String, String>("profile", user.getProfiles().get(0).toString()));
				keys.add(new EmailMessageEntry<String, String>("host", AppConfig.getInstance().getHost()));
				
				bo.sendEmail(user.getIdUser(), MessageType.USERREGISTRED, keys);
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		return ret;
	}
	
	public boolean loginIsStudent(String login){
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
	
	public String findSalt(int idUser) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.findSalt(idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public String findSalt(String login) throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.findSalt(login);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User changePassword(String token, String newPassword) throws Exception {
		try {
			String login = new LoginService().validateToken(token);
			String newSalt = StringUtils.generateSalt();
			String newHash = StringUtils.generateSHA3Hash(newPassword + newSalt);
			
			UserDAO dao = new UserDAO();
			User user = dao.findByLogin(login);
			
			if(user == null){
				throw new Exception("Usuário não encontrado.");
			}
			if(!user.isExternal()){
				throw new Exception("A alteração de senha é permitida apenas para usuários externos. Acadêmicos e Professores devem alterar a senha através do Sistema Acadêmico.");
			}
			
			user.setPassword(newHash);
			user.setSalt(newSalt);
			dao.save(user.getIdUser(), user);
			
			return user;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User changePassword(int idUser, String currentPassword, String newPassword) throws Exception{
		try {
			String salt = this.findSalt(idUser);
			String currentHash = StringUtils.generateSHA3Hash(currentPassword + salt);
			String newSalt = StringUtils.generateSalt();
			String newHash = StringUtils.generateSHA3Hash(newPassword + newSalt);
			
			UserDAO dao = new UserDAO();
			User user = dao.findById(idUser);
			
			if(user == null){
				throw new Exception("Usuário não encontrado.");
			}
			if(!user.isExternal()){
				throw new Exception("A alteração de senha é permitida apenas para usuários externos. Acadêmicos e Professores devem alterar a senha através do Sistema Acadêmico.");
			}
			if(!user.getPassword().equals(currentHash)){
				throw new Exception("A senha atual não confere.");
			}
			if(currentPassword.equals(newPassword)){
				throw new Exception("A nova senha não deve ser igual à senha atual.");
			}
			
			user.setPassword(newHash);
			user.setSalt(newSalt);
			dao.save(idUser, user);
			
			return user;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User validateLogin(Credential credentials) throws Exception {
		return this.validateLogin(credentials.getLogin(), credentials.getPassword());
	}
	
	public User validateLogin(String login, String password) throws Exception{
		if(login.trim().isEmpty() || password.trim().isEmpty()) {
    		throw new Exception("Informe o usuário e a senha.");
    	}
		
		if(login.contains("@utfpr.edu.br")){
			login = login.substring(0, login.indexOf("@"));
		}
		
		login = login.toLowerCase().trim();
		
		if(this.loginIsStudent(login)){
			login = "a" + String.valueOf(Long.parseLong(login.replace("a", "")));
		}
		
		User user = this.findByLogin(login);
		
		if(user == null){
			user = new User();
			user.setExternal(false);
		}
		
		if(user.getSalt().isEmpty()) {
			user.setSalt(StringUtils.generateSalt());
		}
		
		String hash = StringUtils.generateSHA3Hash(password + user.getSalt());
		
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
				try {
					ldapUtils.authenticate(login, password);
				
					Map<String, String> dataLdap = ldapUtils.getLdapProperties(login);
					
					user.setPassword(hash);
					
					//String cnpjCpf = dataLdap.get(LdapConfig.getInstance().getCpfField());
					//String matricula = dataLdap.get(LdapConfig.getInstance().getRegisterField());
					String name = this.formatName(dataLdap.get(LdapConfig.getInstance().getNameField()));
					String email = dataLdap.get(LdapConfig.getInstance().getEmailField());
					
					if(user.getIdUser() == 0){
						user.setName(name);
						user.setEmail(email);
						user.setLogin(login.toLowerCase());
						user.setInstitution("UTFPR");
						user.setExternal(false);
						if(this.loginIsStudent(login)){
							user.setProfiles(new ArrayList<UserProfile>());
							user.getProfiles().add(UserProfile.STUDENT);
							user.setStudentCode(login.replace("a", ""));
						}else{
							user.setProfiles(new ArrayList<UserProfile>());
							user.getProfiles().add(UserProfile.PROFESSOR);
						}
					}
				} catch (CommunicationException e) {
					if(user.getIdUser() == 0) {
						throw new Exception("Não foi possível conectar ao servicor LDAP.");
					} else {
						if(!user.getPassword().equals(hash)){
							throw new Exception("Usuário ou senha inválidos.");
						}
					}
				}
				
				this.save(user.getIdUser(), user);
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
	
	public long getActiveStudents() throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.getActiveStudents();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public long getActiveProfessors() throws Exception{
		try {
			UserDAO dao = new UserDAO();
			
			return dao.getActiveProfessors();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
