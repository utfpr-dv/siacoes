package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.dao.JuryStudentDAO;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.StatementReport;

public class JuryStudentBO {
	
	public JuryStudent findById(int id) throws Exception{
		try {
			JuryStudentDAO dao = new JuryStudentDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryStudent> listByJury(int idJury) throws Exception{
		try {
			JuryStudentDAO dao = new JuryStudentDAO();
			
			return dao.listByJury(idJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryStudent> listByStudent(int idStudent) throws Exception{
		try {
			JuryStudentDAO dao = new JuryStudentDAO();
			
			return dao.listByStudent(idStudent);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public StatementReport getStatementReport(int id) throws Exception {
		if(id == 0){
			throw new Exception("É preciso salvar a presença do acadêmico antes de gerar a declaração.");
		}
		
		return this.loadStatement(this.findById(id));
	}
	
	public List<StatementReport> getStatementReportList(int idJury) throws Exception{
		List<JuryStudent> studentList = this.listByJury(idJury);
		List<StatementReport> list = new ArrayList<StatementReport>();
		
		for(JuryStudent student : studentList){
			list.add(this.loadStatement(student));
		}
		
		return list;
	}
	
	public List<StatementReport> getStatementReportListByThesis(int idThesis) throws Exception {
		ThesisBO bo = new ThesisBO();
		int idJury = bo.findIdJury(idThesis);
		
		return this.getStatementReportList(idJury);
	}
	
	public List<StatementReport> getStatementReportListByProject(int idProject) throws Exception {
		ProjectBO bo = new ProjectBO();
		int idJury = bo.findIdJury(idProject);
		
		return this.getStatementReportList(idJury);
	}
	
	private StatementReport loadStatement(JuryStudent student){
		StatementReport statement = new StatementReport();
		
		statement.setDate(student.getJury().getDate());
		statement.setEndTime(student.getJury().getEndTime());
		statement.setName(student.getStudent().getName());
		statement.setStartTime(student.getJury().getStartTime());
		statement.setStudentCode(student.getStudent().getStudentCode());
		
		if((student.getJury().getProject() != null) && (student.getJury().getProject().getIdProject() != 0)){
			statement.setEvent("Trabalho de Conclusão de Curso 1");
			statement.setStudent(student.getJury().getProject().getStudent().getName());
			statement.setTitle(student.getJury().getProject().getTitle());
		}else{
			statement.setEvent("Trabalho de Conclusão de Curso 2");
			statement.setStudent(student.getJury().getThesis().getStudent().getName());
			statement.setTitle(student.getJury().getThesis().getTitle());
		}
		
		statement.setManagerName(Session.getUser().getName());
		
		return statement;
	}
	
	/*public int save(JuryStudent student) throws Exception{
		if((student.getJury() == null) || (student.getJury().getIdJury() == 0)){
			throw new Exception("Informe a banca.");
		}
		
		if((student.getStudent() == null) || (student.getStudent().getIdUser() == 0)){
			throw new Exception("Informe o acadêmico.");
		}
		
		try {
			JuryStudentDAO dao = new JuryStudentDAO();
			
			return dao.save(student);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}*/

}
