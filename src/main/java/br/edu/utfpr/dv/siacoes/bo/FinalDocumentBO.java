package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import br.edu.utfpr.dv.siacoes.dao.FinalDocumentDAO;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.LibraryCoverReport;
import br.edu.utfpr.dv.siacoes.model.LibraryReport;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

public class FinalDocumentBO {
	
	public FinalDocument findById(int id) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public FinalDocument findByProject(int idProject) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.findByProject(idProject);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public FinalDocument findByThesis(int idThesis) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.findByThesis(idThesis);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public FinalDocument findCurrentThesis(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.findCurrentThesis(idStudent, idDepartment, semester, year);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public FinalDocument findCurrentProject(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.findCurrentProject(idStudent, idDepartment, semester, year);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<FinalDocument> listAll() throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<FinalDocument> listBySemester(int idDepartment, int semester, int year, boolean includePrivate) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.listBySemester(idDepartment, semester, year, includePrivate);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<FinalDocument> listBySupervisor(int idSupervisor, int semester, int year) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.listBySupervisor(idSupervisor, semester, year);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<FinalDocument> listByDepartment(int idDepartment, boolean showThesis, boolean showProjects) throws Exception{
		try{
			if(!showThesis && !showProjects) {
				throw new Exception("É necessário filtrar apenas por monografias, projetos, ou ambos.");
			}
			
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.listByDepartment(idDepartment, showThesis, showProjects);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, FinalDocument thesis) throws Exception{
		SigetConfig config;
		
		if(((thesis.getThesis() == null) || (thesis.getThesis().getIdThesis() == 0)) && ((thesis.getProject() == null) || (thesis.getProject().getIdProject() == 0))){
			throw new Exception("Informe o projeto ou monografia.");
		}
		if(thesis.getTitle().isEmpty()){
			throw new Exception("Informe o título do projeto/monografia.");
		}
		if(thesis.getFile() == null){
			throw new Exception("É necessário enviar o arquivo.");
		}
		if((thesis.getThesis() != null) && (thesis.getThesis().getIdThesis() != 0)) {
			if(thesis.getNativeAbstract().trim().isEmpty()) {
				throw new Exception("É necessário preencher o resumo na língua vernácula.");
			}
			if(thesis.getEnglishAbstract().trim().isEmpty()) {
				throw new Exception("É necessário preencher o resumo na língua inglesa.");
			}
			
			config = new SigetConfigBO().findByDepartment(new ThesisBO().findIdDepartment(thesis.getThesis().getIdThesis()));
		} else {
			config = new SigetConfigBO().findByDepartment(new ProjectBO().findIdDepartment(thesis.getProject().getIdProject()));
		}
		
		if((config.getMaxFileSize() > 0) && ((thesis.getIdFinalDocument() == 0) || !Arrays.equals(thesis.getFile(), new FinalDocumentDAO().getFile(thesis.getIdFinalDocument()))) && (thesis.getFile().length > config.getMaxFileSize())) {
			throw new Exception("O arquivo deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
		}
		
		try{
			boolean isInsert = (thesis.getIdFinalDocument() == 0);
			DocumentFeedback feedback = DocumentFeedback.NONE;
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			if(!isInsert) {
				feedback = dao.findFeedback(thesis.getIdFinalDocument());
			}
			
			int ret = dao.save(idUser, thesis);
			
			try {
				EmailMessageBO bo = new EmailMessageBO();
				List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
				
				if((thesis.getThesis() != null) && (thesis.getThesis().getIdThesis() != 0)) {
					keys.add(new EmailMessageEntry<String, String>("documenttype", "Monografia de TCC 2"));
					keys.add(new EmailMessageEntry<String, String>("student", thesis.getThesis().getStudent().getName()));
					keys.add(new EmailMessageEntry<String, String>("title", thesis.getThesis().getTitle()));
					keys.add(new EmailMessageEntry<String, String>("supervisor", thesis.getThesis().getSupervisor().getName()));
				} else {
					keys.add(new EmailMessageEntry<String, String>("documenttype", "Projeto de TCC 1"));
					keys.add(new EmailMessageEntry<String, String>("student", thesis.getProject().getStudent().getName()));
					keys.add(new EmailMessageEntry<String, String>("title", thesis.getProject().getTitle()));
					keys.add(new EmailMessageEntry<String, String>("supervisor", thesis.getProject().getSupervisor().getName()));
				}
				keys.add(new EmailMessageEntry<String, String>("feedback", thesis.getSupervisorFeedback().toString()));
				
				if(isInsert) {
					int idSupervisor = 0, idStudent = 0;
					
					if((thesis.getThesis() != null) && (thesis.getThesis().getIdThesis() != 0)) {
						idSupervisor = thesis.getThesis().getSupervisor().getIdUser();
						idStudent = thesis.getThesis().getStudent().getIdUser();
					} else {
						idSupervisor = thesis.getProject().getSupervisor().getIdUser();
						idStudent = thesis.getProject().getStudent().getIdUser();
					}
					
					bo.sendEmail(idSupervisor, MessageType.FINALDOCUMENTSUBMITTED, keys);
					bo.sendEmail(idStudent, MessageType.FINALDOCUMENTSUBMITTEDSTUDENT, keys);
				} else if((thesis.getSupervisorFeedback() != DocumentFeedback.NONE) && (feedback != thesis.getSupervisorFeedback())) {
					int idDepartment = 0, idStudent = 0;
					
					if((thesis.getThesis() != null) && (thesis.getThesis().getIdThesis() != 0)) {
						idDepartment = new ThesisBO().findIdDepartment(thesis.getThesis().getIdThesis());
						idStudent = thesis.getThesis().getStudent().getIdUser();
					} else {
						idDepartment = new ProjectBO().findIdDepartment(thesis.getProject().getIdProject());
						idStudent = thesis.getProject().getStudent().getIdUser();
					}
					
					User user = new UserBO().findManager(idDepartment, SystemModule.SIGET);
					
					keys.add(new EmailMessageEntry<String, String>("manager", user.getName()));
					
					bo.sendEmail(user.getIdUser(), MessageType.FINALDOCUMENTVALIDATED, keys);
					bo.sendEmail(idStudent, MessageType.FINALDOCUMENTVALIDATEDSTUDENT, keys);
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
			
			return ret;
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public byte[] getLibraryReport(int idDepartment, int year, int semester) throws Exception{
		try{
			ByteArrayOutputStream ret = new ByteArrayOutputStream();
			Department department = new DepartmentBO().findById(idDepartment);
			Campus campus = new CampusBO().findById(department.getCampus().getIdCampus());
			LibraryCoverReport cover = new LibraryCoverReport();
			
			cover.setYear(year);
			cover.setSemester(semester);
			cover.setCourse("CURSO SUPERIOR DE " + department.getName().toUpperCase());
			cover.setCity(campus.getName());
			
			List<LibraryCoverReport> listCover = new ArrayList<LibraryCoverReport>();
			listCover.add(cover);
			
			FinalDocumentDAO dao = new FinalDocumentDAO();
			List<LibraryReport> list = dao.getLibraryReport(idDepartment, year, semester);
			
			if(list.size() == 0) {
				throw new Exception("Não há nenhuma monografia para enviar à biblioteca.");
			}
			
			for(LibraryReport item : list){
				item.setFileName(campus.getInitials() + "_" + department.getInitials() + "_" + String.valueOf(year) + "_" + 
						String.valueOf(semester) + "_" + String.valueOf(item.getSequence()) + ".pdf");
			}
			
			ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list, "Library", idDepartment);
			
			ByteArrayOutputStream coverRep = new ReportUtils().createPdfStream(listCover, "LibraryCover", idDepartment);
			
			ZipOutputStream out = new ZipOutputStream(ret);
			
			for(LibraryReport item : list){
				ZipEntry e = new ZipEntry(item.getFileName());
				out.putNextEntry(e);
				
				out.write(item.getFile(), 0, item.getFile().length);
				out.closeEntry();
			}
			
			ZipEntry e = new ZipEntry("Lista de TCCs.pdf");
			out.putNextEntry(e);
			out.write(rep.toByteArray(), 0, rep.size());
			out.closeEntry();
			
			e = new ZipEntry("Capa.pdf");
			out.putNextEntry(e);
			out.write(coverRep.toByteArray(), 0, coverRep.size());
			out.closeEntry();
			
			out.flush();
			out.close();
			
			return ret.toByteArray();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public FinalDocument prepareFinalProject(int idUser, int idDepartment, int semester, int year) throws Exception {
		DeadlineBO dbo = new DeadlineBO();
		Deadline d = dbo.findBySemester(idDepartment, semester, year);
		
		if((d == null) || DateUtils.getToday().getTime().after(d.getProjectFinalDocumentDeadline())){
			throw new Exception("A submissão da versão final dos projetos já foi encerrada.");
		}
		
		FinalDocument ft = this.findCurrentProject(idUser, idDepartment, semester, year);
		
		if(ft == null){
			ProjectBO bo = new ProjectBO();
			Project project = bo.findCurrentProject(idUser, idDepartment, semester, year);
			
			if(project == null){
				throw new Exception("É necessário submeter o projeto para avaliação da banca antes.");
			}else{
				JuryBO jbo = new JuryBO();
				Jury jury = jbo.findByProject(project.getIdProject());
				
				if((jury == null) || (jury.getIdJury() == 0) || jury.getDate().after(DateUtils.getNow().getTime())){
					throw new Exception("A versão final do projeto só pode ser enviada após a realização da banca.");
				}else{
					ft = new FinalDocument();
					ft.setTitle(project.getTitle());
					ft.setProject(project);
				}
			}
		}
		
		return ft;
	}
	
	public FinalDocument prepareFinalThesis(int idUser, int idDepartment, int semester, int year) throws Exception {
		DeadlineBO dbo = new DeadlineBO();
		Deadline d = dbo.findBySemester(idDepartment, semester, year);
		
		if((d == null) || DateUtils.getToday().getTime().after(d.getThesisFinalDocumentDeadline())){
			throw new Exception("A submissão da versão final das monografias já foi encerrada.");
		}
		
		FinalDocument ft = this.findCurrentThesis(idUser, idDepartment, semester, year);
		
		if(ft == null){
			ThesisBO bo = new ThesisBO();
			Thesis thesis = bo.findCurrentThesis(idUser, idDepartment, semester, year);
			
			if(thesis == null){
				throw new Exception("É necessário submeter a monografia para avaliação da banca antes.");
			}else{
				JuryBO jbo = new JuryBO();
				Jury jury = jbo.findByThesis(thesis.getIdThesis());
				
				if((jury == null) || (jury.getIdJury() == 0) || jury.getDate().after(DateUtils.getNow().getTime())){
					throw new Exception("A versão final da monografia só pode ser enviada após a realização da banca.");
				}else{
					ft = new FinalDocument();
					ft.setTitle(thesis.getTitle());
					ft.setThesis(thesis);
				}
			}
		}
		
		return ft;
	}
	
	public long getTotalFinalThesis() throws Exception{
		try {
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.getTotalFinalThesis();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
