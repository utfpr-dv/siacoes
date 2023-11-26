package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ThesisFormatDAO;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem;
import br.edu.utfpr.dv.siacoes.model.ThesisFormat;

public class ThesisFormatBO {
	
	public ThesisFormat findById(int id) throws Exception{
		try {
			ThesisFormatDAO dao = new ThesisFormatDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<ThesisFormat> listAll(boolean onlyActives) throws Exception{
		try {
			ThesisFormatDAO dao = new ThesisFormatDAO();
			
			return dao.listAll(onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<ThesisFormat> listByDepartment(int idDepartment, boolean onlyActives) throws Exception{
		try {
			ThesisFormatDAO dao = new ThesisFormatDAO();
			
			return dao.listByDepartment(idDepartment, onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, ThesisFormat format) throws Exception{
		try {
			if(format.getDescription().isEmpty()){
				throw new Exception("Informe a descrição do formato de TCC.");
			}
			if(format.getItems() != null) {
				int sequence1 = 1, sequence2 = 1;
				
				for(EvaluationItem item : format.getItems()) {
					if(item.getStage() == 1) {
						item.setSequence(sequence1);
						sequence1++;
					} else {
						item.setSequence(sequence2);
						sequence2++;
					}
				}
				
				if(sequence1 == 1) {
					throw new Exception("O formato de TCC deve conter ao menos um quesito de avaliação para o TCC 1.");
				}
				if(sequence2 == 1) {
					throw new Exception("O formato de TCC deve conter ao menos um quesito de avaliação para o TCC 2.");
				}
			}
			
			ThesisFormatDAO dao = new ThesisFormatDAO();
			
			return dao.save(idUser, format);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
