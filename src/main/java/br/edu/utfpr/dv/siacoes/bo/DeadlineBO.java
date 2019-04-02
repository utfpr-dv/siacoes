package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.DeadlineDAO;
import br.edu.utfpr.dv.siacoes.model.Deadline;

public class DeadlineBO {

	public List<Deadline> listAll() throws Exception{
		try {
			DeadlineDAO dao = new DeadlineDAO();
			
			return dao.listAll();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Deadline> listByDepartment(int idDepartment) throws Exception{
		try {
			DeadlineDAO dao = new DeadlineDAO();
			
			return dao.listByDepartment(idDepartment);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, Deadline deadline) throws Exception{
		try {
			if((deadline.getSemester() < 1) || (deadline.getSemester() > 2)){
				throw new Exception("Informe o semestre.");
			}
			if(deadline.getYear() <= 0){
				throw new Exception("Informe o ano.");
			}
			if(deadline.getProjectFinalDocumentDeadline().before(deadline.getProjectDeadline())) {
				throw new Exception("O prazo de entrega da versão final do projeto não pode ser anterior ao prazo de entrega do projeto.");
			}
			if(deadline.getThesisFinalDocumentDeadline().before(deadline.getThesisDeadline())) {
				throw new Exception("O prazo de entrega da versão final da monografia não pode ser anterior ao prazo de entrega da monografia.");
			}
			
			DeadlineDAO dao = new DeadlineDAO();
			
			Deadline d = dao.findBySemester(deadline.getDepartment().getIdDepartment(), deadline.getSemester(), deadline.getYear());
			
			if((d != null) && (d.getIdDeadline() != deadline.getIdDeadline())){
				throw new Exception("Já existe um registro para este semestre.");
			}
			
			return dao.save(idUser, deadline);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Deadline findById(int id) throws Exception{
		try {
			DeadlineDAO dao = new DeadlineDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Deadline findBySemester(int idDepartment, int semester, int year) throws Exception{
		try {
			DeadlineDAO dao = new DeadlineDAO();
			
			return dao.findBySemester(idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
