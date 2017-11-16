package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import br.edu.utfpr.dv.siacoes.dao.FinalDocumentDAO;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.LibraryCoverReport;
import br.edu.utfpr.dv.siacoes.model.LibraryReport;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

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
	
	public List<FinalDocument> listByDepartment(int idDepartment) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.listByDepartment(idDepartment);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(FinalDocument thesis) throws Exception{
		if(((thesis.getThesis() == null) || (thesis.getThesis().getIdThesis() == 0)) && ((thesis.getProject() == null) || (thesis.getProject().getIdProject() == 0))){
			throw new Exception("Informe o projeto ou monografia.");
		}
		if(thesis.getTitle().isEmpty()){
			throw new Exception("Informe o título do projeto/monografia.");
		}
		if(thesis.getFile() == null){
			throw new Exception("É necessário enviar o arquivo.");
		}
		
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.save(thesis);
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
			
			for(LibraryReport item : list){
				item.setFileName(campus.getInitials() + "_" + department.getInitials() + "_" + String.valueOf(year) + "_" + 
						String.valueOf(semester) + "_" + String.valueOf(item.getSequence()) + ".pdf");
			}
			
			ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list, "Library");
			
			ByteArrayOutputStream coverRep = new ReportUtils().createPdfStream(listCover, "LibraryCover");
			
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

}
