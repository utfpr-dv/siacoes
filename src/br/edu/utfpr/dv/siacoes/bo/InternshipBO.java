package br.edu.utfpr.dv.siacoes.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;
import br.edu.utfpr.dv.siacoes.dao.InternshipDAO;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;

public class InternshipBO {

	public List<Internship> listAll() throws Exception{
		try{
			InternshipDAO dao = new InternshipDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<Internship> list(int year, int idStudent, int idSupervisor, int idCompany, int type, int status) throws Exception{
		try{
			InternshipDAO dao = new InternshipDAO();
			
			if((year == 0) && (idStudent == 0) && (idSupervisor == 0) && (idCompany == 0) && (type == -1) && (status == -1)){
				return dao.listAll();
			}else{
				return dao.list(year, idStudent, idSupervisor, idCompany, type, status);	
			}
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<Internship> listByCompany(int idCompany) throws Exception{
		try{
			InternshipDAO dao = new InternshipDAO();
			
			return dao.listByCompany(idCompany);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<Internship> listByCompanySupervisor(int idCompanySupervisor) throws Exception{
		try{
			InternshipDAO dao = new InternshipDAO();
			
			return dao.listByCompanySupervisor(idCompanySupervisor);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<Internship> listBySupervisor(int idSupervisor) throws Exception{
		try{
			InternshipDAO dao = new InternshipDAO();
			
			return dao.listBySupervisor(idSupervisor);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<Internship> listByStudent(int idStudent) throws Exception{
		try{
			InternshipDAO dao = new InternshipDAO();
			
			return dao.listByStudent(idStudent);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public Internship findById(int id) throws Exception{
		try{
			InternshipDAO dao = new InternshipDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(Internship internship) throws Exception{
		if((internship.getDepartment() == null) || (internship.getDepartment().getIdDepartment() == 0)){
			throw new Exception("Informe o departamento do estágio.");
		}
		if((internship.getCompany() == null) || (internship.getCompany().getIdCompany() == 0)){
			throw new Exception("Informe a empresa concedente do estágio.");
		}
		if((internship.getCompanySupervisor() == null) || (internship.getCompanySupervisor().getIdUser() == 0)){
			throw new Exception("Informe o supervisor do estágio na empresa.");
		}
		if((internship.getSupervisor() == null) || (internship.getSupervisor().getIdUser() == 0)){
			throw new Exception("Informe o professor orientador do estágio.");
		}
		if((internship.getStudent() == null) || (internship.getStudent().getIdUser() == 0)){
			throw new Exception("Informe o aluno.");
		}
		if(internship.getStartDate() == null){
			throw new Exception("Informe a data de início do estágio.");
		}
		if(internship.getTotalHours() <= 0){
			throw new Exception("Informe o total de horas do estágio.");
		}
		if(internship.getInternshipPlan() == null){
			throw new Exception("Faça upload do plano de estágio.");
		}
		if((internship.getFinalReport() != null) && (internship.getReportTitle().trim().isEmpty())){
			throw new Exception("Informe o título do relatório final.");
		}
		
		if(internship.getType() == InternshipType.NONREQUIRED){
			InternshipJuryBO jbo = new InternshipJuryBO();
			
			InternshipJury jury = jbo.findByInternship(internship.getIdInternship());
			if((jury != null) && (jury.getIdInternshipJury() != 0)){
				throw new Exception("Este estágio não pode ser alterado para Não Obrigatório pois já foi marcada banca.");
			}
		}
		
		Connection conn = ConnectionDAO.getInstance().getConnection();
		int ret = 0;
		boolean isInsert = (internship.getIdInternship() == 0);
		
		try{
			conn.setAutoCommit(false);
			
			InternshipDAO dao = new InternshipDAO(conn);
			
			ret = dao.save(internship);
			
			if(internship.getReports() != null){
				String ids = "";
				
				for(InternshipReport report : internship.getReports()){
					report.getInternship().setIdInternship(ret);
					
					InternshipReportBO bo = new InternshipReportBO(conn);
					
					int id = bo.save(report);
					
					if(ids.isEmpty()){
						ids = String.valueOf(id);
					}else{
						ids = ids + ", " + String.valueOf(id);
					}
				}
				
				if(!ids.isEmpty()){
					Statement stmt = conn.createStatement();
					
					stmt.execute("DELETE FROM internshipreport WHERE idinternship=" + String.valueOf(ret) + " AND idinternshipreport NOT IN (" + ids + ")");
				}
			}
			
			conn.commit();
		}catch(SQLException e){
			conn.rollback();
			
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}finally {
			conn.setAutoCommit(true);
		}
		
		try{
			if(isInsert){
				EmailMessageBO bo = new EmailMessageBO();
				List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
				
				keys.add(new EmailMessageEntry<String, String>("student", internship.getStudent().getName()));
				keys.add(new EmailMessageEntry<String, String>("company", internship.getCompany().getName()));
				keys.add(new EmailMessageEntry<String, String>("companySupervisor", internship.getCompanySupervisor().getName()));
				keys.add(new EmailMessageEntry<String, String>("supervisor", internship.getSupervisor().getName()));
				keys.add(new EmailMessageEntry<String, String>("type", internship.getType().toString()));
				keys.add(new EmailMessageEntry<String, String>("startDate", new SimpleDateFormat("dd/MM/yyyy").format(internship.getStartDate())));
				keys.add(new EmailMessageEntry<String, String>("comments", internship.getComments()));
				
				bo.sendEmail(internship.getStudent().getIdUser(), MessageType.INTERNSHIPINCLUDEDSTUDENT, keys);
				bo.sendEmail(internship.getSupervisor().getIdUser(), MessageType.INTERNSHIPINCLUDEDSUPERVISOR, keys);
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		return ret;
	}
	
	public boolean delete(int id) throws Exception{
		try{
			InternshipReportBO bo = new InternshipReportBO();
			
			if(bo.listByInternship(id).size() > 0){
				throw new Exception("É necessário excluir os relatórios de orientação, supervisão e do aluno antes de excluir o estágio.");
			}
			
			InternshipJuryBO jbo = new InternshipJuryBO();
			
			InternshipJury jury = jbo.findByInternship(id);
			if((jury != null) && (jury.getIdInternshipJury() != 0)){
				throw new Exception("Não é possível excluir este estágio pois já foi marcada banca para o mesmo.");
			}
			
			InternshipDAO dao = new InternshipDAO();
			
			return dao.delete(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(Internship internship) throws Exception{
		return this.delete(internship.getIdInternship());
	}
	
}
