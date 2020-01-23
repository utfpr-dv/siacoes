package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import br.edu.utfpr.dv.siacoes.dao.CertificateDAO;
import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.Certificate;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryStudent;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportFeedback;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.StatementReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

public class CertificateBO {
	
	public byte[] getThesisProfessorStatement(int idSupervisor, int idProposal, int stage) throws Exception{
		UserBO ubo = new UserBO();
		User supervisor = ubo.findById(idSupervisor);
		
		if(stage == 1){
			ProjectBO bo = new ProjectBO();
			Project project = bo.findByProposal(idProposal);
			
			return this.getThesisProfessorStatement(supervisor, project);
		}else{
			ThesisBO bo = new ThesisBO();
			Thesis thesis = bo.findByProposal(idProposal);
			
			return this.getThesisProfessorStatement(supervisor, thesis);
		}
	}
	
	public byte[] getThesisProfessorStatement(User supervisor, Project project) throws Exception{
		if((project == null) || (project.getIdProject() == 0)){
			throw new Exception("A declaração só pode ser emitida após o acadêmico enviar o projeto.");
		}
		
		if(project.getCosupervisor() == null){
			project.setCosupervisor(new User());
		}
		
		if((project.getSupervisor().getIdUser() != supervisor.getIdUser()) && (project.getCosupervisor().getIdUser() != supervisor.getIdUser())){
			throw new Exception("A declaração só pode ser emitida para o professor que atuou como orientador ou coorientador do trabalho.");
		}
		
		return this.getThesisProfessorStatement(supervisor, project, null);
	}
	
	public byte[] getThesisProfessorStatement(User supervisor, Thesis thesis) throws Exception{
		if((thesis == null) || (thesis.getIdThesis() == 0)){
			throw new Exception("A declaração só pode ser emitida após o acadêmico enviar a monografia.");
		}
		
		if(thesis.getCosupervisor() == null){
			thesis.setCosupervisor(new User());
		}
		
		if((thesis.getSupervisor().getIdUser() != supervisor.getIdUser()) && (thesis.getCosupervisor().getIdUser() != supervisor.getIdUser())){
			throw new Exception("A declaração só pode ser emitida para o professor que atuou como orientador ou coorientador do trabalho.");
		}
		
		return this.getThesisProfessorStatement(supervisor, null, thesis);
	}
	
	private byte[] getThesisProfessorStatement(User supervisor, Project project, Thesis thesis) throws Exception{
		StatementReport report = this.loadThesisProfessorStatement(supervisor, project, thesis);
		List<StatementReport> list = new ArrayList<StatementReport>();
		list.add(report);
		
		int idDepartment = 0;
		if((thesis != null) && (thesis.getIdThesis() != 0)) {
			idDepartment = new ThesisBO().findIdDepartment(thesis.getIdThesis());
		} else {
			idDepartment = new ProjectBO().findIdDepartment(project.getIdProject());
		}
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list, "ProfessorThesisStatement", idDepartment);
		byte[] finalReport = rep.toByteArray();
		
		Certificate certificate = new Certificate();
		certificate.setDate(report.getGeneratedDate());
		certificate.setFile(finalReport);
		certificate.setGuid(report.getGuid());
		certificate.setModule(SystemModule.SIGET);
		certificate.setUser(supervisor);
		certificate.getDepartment().setIdDepartment(idDepartment);
		
		
		this.save(certificate);
		
		return finalReport;
	}
	
	private StatementReport loadThesisProfessorStatement(User supervisor, Project project, Thesis thesis) {
		StatementReport statement = new StatementReport();
		
		statement.setName(supervisor.getName());
		statement.setGuid(this.generateGuid());
		statement.setLink(this.getLink(statement.getGuid()));
		
		if(thesis == null){
			statement.setEvent("Trabalho de Conclusão de Curso 1");
			statement.setStudent(project.getStudent().getName());
			statement.setTitle(project.getTitle());
			statement.setSemester(project.getSemester());
			statement.setYear(project.getYear());
			
			if(project.getSupervisor().getIdUser() == supervisor.getIdUser()){
				statement.setType("orientador(a)");
			}else if((project.getCosupervisor() != null) && (project.getCosupervisor().getIdUser() == supervisor.getIdUser())){
				statement.setType("coorientador(a)");
			}else{
				statement.setType("ASPONE");
			}
		}else{
			statement.setEvent("Trabalho de Conclusão de Curso 2");
			statement.setStudent(thesis.getStudent().getName());
			statement.setTitle(thesis.getTitle());
			statement.setSemester(thesis.getSemester());
			statement.setYear(thesis.getYear());
			
			if(thesis.getSupervisor().getIdUser() == supervisor.getIdUser()){
				statement.setType("orientador(a)");
			} else if((thesis.getCosupervisor() != null) && (thesis.getCosupervisor().getIdUser() == supervisor.getIdUser())){
				statement.setType("coorientador(a)");
			}else{
				statement.setType("ASPONE");
			}
		}
		
		try {
			statement.setQrCode(new ByteArrayInputStream(this.createQRCode(statement.getLink(), 100, 100)));
		} catch (WriterException | IOException e1) {
			e1.printStackTrace();
		}
		
		int idDepartment = 0;
		
		try {
			if((thesis != null) && (thesis.getIdThesis() != 0)) {
				idDepartment = new ThesisBO().findIdDepartment(thesis.getIdThesis());
			} else {
				idDepartment = new ProjectBO().findIdDepartment(project.getIdProject());
			}
		} catch (Exception e) { }
		
		try {
			User user = new UserBO().findManager(idDepartment, SystemModule.SIGET);
			
			statement.setManagerName(user.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setManagerName("");
		}
		
		try {
			UserBO bo = new UserBO();
			User manager = bo.findDepartmentManager(idDepartment);
			
			statement.setDepartmentManager(manager.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setDepartmentManager("");
		}
		
		return statement;
	}
	
	public byte[] getInternshipProfessorStatement(int idInternship) throws Exception{
		InternshipBO bo = new InternshipBO();
		
		return this.getInternshipProfessorStatement(bo.findById(idInternship));
	}
	
	public byte[] getInternshipProfessorStatement(Internship internship) throws Exception{
		if((internship == null) || (internship.getIdInternship() == 0)){
			throw new Exception("É necessário informar o estágio antes de gerar a declaração.");
		}
		if(internship.getStatus() == InternshipStatus.FINISHED){
			InternshipReportBO bo = new InternshipReportBO();
			
			if((internship.getType() == InternshipType.REQUIRED) && (internship.getFinalReport() == null)){
				throw new Exception("O acadêmico precisa entregar o relatório final de estágio para gerar a declaração.");	
			}
			if(!bo.hasReport(internship.getIdInternship(), ReportType.SUPERVISOR, ReportFeedback.APPROVED)){
				throw new Exception("O professor precisa entregar o relatório de orientação para gerar a declaração.");
			}
		}
		
		StatementReport report = this.loadInternshipProfessorStatement(internship);
		List<StatementReport> list = new ArrayList<StatementReport>();
		list.add(report);
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list, "ProfessorInternshipStatement", internship.getDepartment().getIdDepartment());
		byte[] finalReport = rep.toByteArray();
		
		Certificate certificate = new Certificate();
		certificate.setDate(report.getGeneratedDate());
		certificate.getDepartment().setIdDepartment(internship.getDepartment().getIdDepartment());
		certificate.setFile(finalReport);
		certificate.setGuid(report.getGuid());
		certificate.setModule(SystemModule.SIGES);
		certificate.setUser(internship.getSupervisor());
		
		this.save(certificate);
		
		return finalReport;
	}
	
	public byte[] getInternshipStudentStatement(int idInternship) throws Exception{
		InternshipBO bo = new InternshipBO();
		
		return this.getInternshipStudentStatement(bo.findById(idInternship));
	}
	
	public byte[] getInternshipStudentStatement(Internship internship) throws Exception{
		if((internship == null) || (internship.getIdInternship() == 0)){
			throw new Exception("É necessário informar o estágio antes de gerar a declaração.");
		}
		if((internship.getStatus() == InternshipStatus.FINISHED) && (internship.getFinalReport() == null)){
			if(internship.getType() == InternshipType.REQUIRED){
				throw new Exception("O acadêmico precisa entregar o relatório final de estágio para gerar a declaração.");	
			}else{
				InternshipReportBO bo = new InternshipReportBO();
				
				if(!bo.hasReport(internship.getIdInternship(), ReportType.STUDENT, ReportFeedback.APPROVED)){
					throw new Exception("O acadêmico precisa entregar o relatório de estágio para gerar a declaração.");
				}
			}
		}
		
		StatementReport report = this.loadInternshipStudentStatement(internship);
		List<StatementReport> list = new ArrayList<StatementReport>();
		list.add(report);
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list, "StudentInternshipStatement", internship.getDepartment().getIdDepartment());
		byte[] finalReport = rep.toByteArray();
		
		Certificate certificate = new Certificate();
		certificate.setDate(report.getGeneratedDate());
		certificate.getDepartment().setIdDepartment(internship.getDepartment().getIdDepartment());
		certificate.setFile(finalReport);
		certificate.setGuid(report.getGuid());
		certificate.setModule(SystemModule.SIGES);
		certificate.setUser(internship.getStudent());
		
		this.save(certificate);
		
		return finalReport;
	}
	
	public byte[] getJuryProfessorStatement(int idJuryAppraiser) throws Exception{
		JuryAppraiserBO bo = new JuryAppraiserBO();
		
		return this.getJuryProfessorStatement(bo.findById(idJuryAppraiser));
	}
	
	public byte[] getJuryProfessorStatement(JuryAppraiser appraiser) throws Exception{
		if((appraiser == null) || (appraiser.getIdJuryAppraiser() == 0)){
			throw new Exception("É preciso salvar o membro antes de gerar a declaração.");
		}
		if(appraiser.isSubstitute()){
			throw new Exception("A declaração somente pode ser gerada para membros titulares da banca.");
		}
		if(!new JuryAppraiserScoreBO().hasScore(appraiser.getJury().getIdJury(), appraiser.getAppraiser().getIdUser())){
			throw new Exception("A declaração somente pode ser emitida após o lançamento das notas.");
		}
		
		StatementReport report = this.loadJuryProfessorStatement(appraiser);
		List<StatementReport> list = new ArrayList<StatementReport>();
		list.add(report);
		
		int idDepartment = new JuryBO().findIdDepartment(appraiser.getJury().getIdJury());
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list, "ProfessorStatement", idDepartment);
		byte[] finalReport = rep.toByteArray();
		
		Certificate certificate = new Certificate();
		certificate.setDate(report.getGeneratedDate());
		certificate.getDepartment().setIdDepartment(idDepartment);
		certificate.setFile(finalReport);
		certificate.setGuid(report.getGuid());
		certificate.setModule(SystemModule.SIGET);
		certificate.setUser(appraiser.getAppraiser());
		
		this.save(certificate);
		
		return finalReport;
	}
	
	public byte[] getJuryProfessorStatementReportList(int idJury) throws Exception {
		JuryAppraiserBO jabo = new JuryAppraiserBO();
		List<JuryAppraiser> appraiserList = jabo.listAppraisers(idJury);
		
		if(appraiserList.size() > 0){
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PDFMergerUtility pdfMerge = new PDFMergerUtility();
			pdfMerge.setDestinationStream(output);
			
			for(JuryAppraiser appraiser : appraiserList){
				if(!appraiser.isSubstitute()) {
					pdfMerge.addSource(new ByteArrayInputStream(this.getJuryProfessorStatement(appraiser)));
				}
			}
			
			pdfMerge.mergeDocuments(null);
			
			return output.toByteArray();
		}else{
			return null;
		}
	}
	
	public byte[] getJuryProfessorStatementReportListByThesis(int idThesis) throws Exception {
		ThesisBO bo = new ThesisBO();
		int idJury = bo.findIdJury(idThesis);
		
		return this.getJuryProfessorStatementReportList(idJury);
	}
	
	public byte[] getJuryProfessorStatementReportListByProject(int idProject) throws Exception {
		ProjectBO bo = new ProjectBO();
		int idJury = bo.findIdJury(idProject);
		
		return this.getJuryProfessorStatementReportList(idJury);
	}
	
	public byte[] getJuryStudentStatement(int idJuryStudent) throws Exception{
		JuryStudentBO bo = new JuryStudentBO();
		
		return this.getJuryStudentStatement(bo.findById(idJuryStudent));
	}
	
	public byte[] getJuryStudentStatement(JuryStudent student) throws Exception{
		if((student == null) || (student.getIdJuryStudent() == 0)){
			throw new Exception("É preciso salvar o acadêmico antes de gerar a declaração.");
		}
		
		StatementReport report = this.loadJuryStudentStatement(student);
		List<StatementReport> list = new ArrayList<StatementReport>();
		list.add(report);
		
		int idDepartment = new JuryBO().findIdDepartment(student.getJury().getIdJury());
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list, "StudentStatement", idDepartment);
		byte[] finalReport = rep.toByteArray();
		
		Certificate certificate = new Certificate();
		certificate.setDate(report.getGeneratedDate());
		certificate.getDepartment().setIdDepartment(idDepartment);
		certificate.setFile(finalReport);
		certificate.setGuid(report.getGuid());
		certificate.setModule(SystemModule.SIGET);
		certificate.setUser(student.getStudent());
		
		this.save(certificate);
		
		return finalReport;
	}
	
	public byte[] getJuryStudentStatementReportList(int idJury) throws Exception{
		JuryStudentBO jsbo = new JuryStudentBO();
		List<JuryStudent> studentList = jsbo.listByJury(idJury);
		
		if(studentList.size() > 0){
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PDFMergerUtility pdfMerge = new PDFMergerUtility();
			pdfMerge.setDestinationStream(output);
			
			for(JuryStudent student : studentList){
				pdfMerge.addSource(new ByteArrayInputStream(this.getJuryStudentStatement(student)));
			}
			
			pdfMerge.mergeDocuments(null);
			
			return output.toByteArray();	
		}else{
			return null;
		}
	}
	
	public byte[] getJuryStudentStatementReportListByThesis(int idThesis) throws Exception {
		ThesisBO bo = new ThesisBO();
		int idJury = bo.findIdJury(idThesis);
		
		return this.getJuryStudentStatementReportList(idJury);
	}
	
	public byte[] getJuryStudentStatementReportListByProject(int idProject) throws Exception {
		ProjectBO bo = new ProjectBO();
		int idJury = bo.findIdJury(idProject);
		
		return this.getJuryStudentStatementReportList(idJury);
	}
	
	public byte[] getInternshipJuryProfessorStatement(int idJuryAppraiser) throws Exception{
		InternshipJuryAppraiserBO bo = new InternshipJuryAppraiserBO();
		
		return this.getInternshipJuryProfessorStatement(bo.findById(idJuryAppraiser));
	}
	
	public byte[] getInternshipJuryProfessorStatement(InternshipJuryAppraiser appraiser) throws Exception{
		if((appraiser == null) || (appraiser.getIdInternshipJuryAppraiser() == 0)){
			throw new Exception("É preciso salvar o membro antes de gerar a declaração.");
		}
		if(appraiser.isSubstitute()){
			throw new Exception("A declaração somente pode ser gerada para membros titulares da banca.");
		}
		if(!new InternshipJuryAppraiserScoreBO().hasScore(appraiser.getInternshipJury().getIdInternshipJury(), appraiser.getAppraiser().getIdUser())){
			throw new Exception("A declaração somente pode ser emitida após o lançamento das notas.");
		}
		
		StatementReport report = this.loadInternshipJuryProfessorStatement(appraiser);
		List<StatementReport> list = new ArrayList<StatementReport>();
		list.add(report);
		
		int idDepartment = new InternshipJuryBO().findIdDepartment(appraiser.getInternshipJury().getIdInternshipJury());
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list, "ProfessorStatement", idDepartment);
		byte[] finalReport = rep.toByteArray();
		
		Certificate certificate = new Certificate();
		certificate.setDate(report.getGeneratedDate());
		certificate.getDepartment().setIdDepartment(idDepartment);
		certificate.setFile(finalReport);
		certificate.setGuid(report.getGuid());
		certificate.setModule(SystemModule.SIGES);
		certificate.setUser(appraiser.getAppraiser());
		
		this.save(certificate);
		
		return finalReport;
	}
	
	public byte[] getInternshipJuryProfessorStatementReportList(int idInternshipJury) throws Exception {
		InternshipJuryAppraiserBO jabo = new InternshipJuryAppraiserBO();
		List<InternshipJuryAppraiser> appraiserList = jabo.listAppraisers(idInternshipJury);
		
		if(appraiserList.size() > 0){
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PDFMergerUtility pdfMerge = new PDFMergerUtility();
			pdfMerge.setDestinationStream(output);
			
			for(InternshipJuryAppraiser appraiser : appraiserList){
				if(!appraiser.isSubstitute()) {
					pdfMerge.addSource(new ByteArrayInputStream(this.getInternshipJuryProfessorStatement(appraiser)));
				}
			}
			
			pdfMerge.mergeDocuments(null);
			
			return output.toByteArray();
		}else{
			return null;
		}
	}
	
	public byte[] getInternshipJuryProfessorStatementReportListByInternship(int idInternship) throws Exception {
		InternshipJuryBO bo = new InternshipJuryBO();
		int idJury = bo.findByInternship(idInternship).getIdInternshipJury();
		
		return this.getInternshipJuryProfessorStatementReportList(idJury);
	}
	
	public byte[] getInternshipJuryStudentStatement(int idJuryStudent) throws Exception{
		InternshipJuryStudentBO bo = new InternshipJuryStudentBO();
		
		return this.getInternshipJuryStudentStatement(bo.findById(idJuryStudent));
	}
	
	public byte[] getInternshipJuryStudentStatement(InternshipJuryStudent student) throws Exception{
		if((student == null) || (student.getIdInternshipJuryStudent() == 0)){
			throw new Exception("É preciso salvar o acadêmico antes de gerar a declaração.");
		}
		
		StatementReport report = this.loadInternshipJuryStudentStatement(student);
		List<StatementReport> list = new ArrayList<StatementReport>();
		list.add(report);
		
		int idDepartment = new InternshipJuryBO().findIdDepartment(student.getInternshipJury().getIdInternshipJury());
		
		ByteArrayOutputStream rep = new ReportUtils().createPdfStream(list, "StudentStatement", idDepartment);
		byte[] finalReport = rep.toByteArray();
		
		Certificate certificate = new Certificate();
		certificate.setDate(report.getGeneratedDate());
		certificate.getDepartment().setIdDepartment(idDepartment);
		certificate.setFile(finalReport);
		certificate.setGuid(report.getGuid());
		certificate.setModule(SystemModule.SIGES);
		certificate.setUser(student.getStudent());
		
		this.save(certificate);
		
		return finalReport;
	}
	
	public byte[] getInternshipJuryStudentStatementReportList(int idInternshipJury) throws Exception{
		InternshipJuryStudentBO jsbo = new InternshipJuryStudentBO();
		List<InternshipJuryStudent> studentList = jsbo.listByJury(idInternshipJury);
		
		if(studentList.size() > 0){
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PDFMergerUtility pdfMerge = new PDFMergerUtility();
			pdfMerge.setDestinationStream(output);
			
			for(InternshipJuryStudent student : studentList){
				pdfMerge.addSource(new ByteArrayInputStream(this.getInternshipJuryStudentStatement(student)));
			}
			
			pdfMerge.mergeDocuments(null);
			
			return output.toByteArray();	
		}else{
			return null;
		}
	}
	
	public byte[] getInternshipJuryStudentStatementReportListByThesis(int idInternship) throws Exception {
		InternshipJuryBO bo = new InternshipJuryBO();
		int idJury = bo.findByInternship(idInternship).getIdInternshipJury();
		
		return this.getInternshipJuryStudentStatementReportList(idJury);
	}
	
	public Certificate findById(int id) throws Exception{
		try{
			CertificateDAO dao = new CertificateDAO();
			
			return dao.findById(id);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Certificate findByGuid(String guid) throws Exception{
		try{
			CertificateDAO dao = new CertificateDAO();
			
			return dao.findByGuid(guid);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	private StatementReport loadJuryProfessorStatement(JuryAppraiser appraiser){
		StatementReport statement = new StatementReport();
		
		statement.setDate(appraiser.getJury().getDate());
		statement.setEndTime(appraiser.getJury().getEndTime());
		statement.setName(appraiser.getAppraiser().getName());
		statement.setStartTime(appraiser.getJury().getStartTime());
		statement.setGuid(this.generateGuid());
		statement.setLink(this.getLink(statement.getGuid()));
		
		try {
			statement.setQrCode(new ByteArrayInputStream(this.createQRCode(statement.getLink(), 100, 100)));
		} catch (WriterException | IOException e1) {
			e1.printStackTrace();
		}
		
		if((appraiser.getJury().getProject() != null) && (appraiser.getJury().getProject().getIdProject() != 0)){
			statement.setEvent("Trabalho de Conclusão de Curso 1");
			statement.setStudent(appraiser.getJury().getProject().getStudent().getName());
			statement.setTitle(appraiser.getJury().getProject().getTitle());
		}else{
			statement.setEvent("Trabalho de Conclusão de Curso 2");
			statement.setStudent(appraiser.getJury().getThesis().getStudent().getName());
			statement.setTitle(appraiser.getJury().getThesis().getTitle());
		}
		
		int idDepartment = 0;
		
		try {
			idDepartment = new JuryBO().findIdDepartment(appraiser.getJury().getIdJury());
		} catch (Exception e) { }
		
		try {
			UserBO bo = new UserBO();
			User user = bo.findManager(idDepartment, SystemModule.SIGET);
			
			statement.setManagerName(user.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setManagerName("");
		}
		
		try {
			UserBO bo = new UserBO();
			User manager = bo.findDepartmentManager(idDepartment);
			
			statement.setDepartmentManager(manager.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setDepartmentManager("");
		}
		
		return statement;
	}
	
	private StatementReport loadJuryStudentStatement(JuryStudent student){
		StatementReport statement = new StatementReport();
		
		statement.setDate(student.getJury().getDate());
		statement.setEndTime(student.getJury().getEndTime());
		statement.setName(student.getStudent().getName());
		statement.setStartTime(student.getJury().getStartTime());
		statement.setStudentCode(student.getStudent().getStudentCode());
		statement.setGuid(this.generateGuid());
		statement.setLink(this.getLink(statement.getGuid()));
		
		try {
			statement.setQrCode(new ByteArrayInputStream(this.createQRCode(statement.getLink(), 100, 100)));
		} catch (WriterException | IOException e1) {
			e1.printStackTrace();
		}
		
		if((student.getJury().getProject() != null) && (student.getJury().getProject().getIdProject() != 0)){
			statement.setEvent("Trabalho de Conclusão de Curso 1");
			statement.setStudent(student.getJury().getProject().getStudent().getName());
			statement.setTitle(student.getJury().getProject().getTitle());
		}else{
			statement.setEvent("Trabalho de Conclusão de Curso 2");
			statement.setStudent(student.getJury().getThesis().getStudent().getName());
			statement.setTitle(student.getJury().getThesis().getTitle());
		}
		
		int idDepartment = 0;
		
		try {
			idDepartment = new JuryBO().findIdDepartment(student.getJury().getIdJury());
		} catch (Exception e) { }
		
		try {
			UserBO bo = new UserBO();
			User user = bo.findManager(idDepartment, SystemModule.SIGET);
			
			statement.setManagerName(user.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setManagerName("");
		}
		
		try {
			UserBO bo = new UserBO();
			User manager = bo.findDepartmentManager(idDepartment);
			
			statement.setDepartmentManager(manager.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setDepartmentManager("");
		}
		
		return statement;
	}
	
	private StatementReport loadInternshipProfessorStatement(Internship internship){
		StatementReport statement = new StatementReport();
		
		statement.setName(internship.getSupervisor().getName());
		statement.setStartTime(internship.getStartDate());
		
		if(internship.getStatus() == InternshipStatus.CURRENT){
			statement.setEndTime(null);
		}else{
			statement.setEndTime(internship.getEndDate());
		}
		
		statement.setGuid(this.generateGuid());
		statement.setLink(this.getLink(statement.getGuid()));
		
		try {
			statement.setQrCode(new ByteArrayInputStream(this.createQRCode(statement.getLink(), 100, 100)));
		} catch (WriterException | IOException e1) {
			e1.printStackTrace();
		}
		
		statement.setEvent("Estágio");
		
		if(internship.getType() == InternshipType.REQUIRED){
			statement.setDetailedEvent("Estágio Obrigatório");
		}else{
			statement.setDetailedEvent("Estágio não Obrigatório");
		}
		
		statement.setStudent(internship.getStudent().getName());
		statement.setCompany(internship.getCompany().getName());
		
		try {
			UserBO bo = new UserBO();
			User user = bo.findManager(internship.getDepartment().getIdDepartment(), SystemModule.SIGES);
			
			statement.setManagerName(user.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setManagerName("");
		}
		
		try {
			UserBO bo = new UserBO();
			User manager = bo.findDepartmentManager(internship.getDepartment().getIdDepartment());
			
			statement.setDepartmentManager(manager.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setDepartmentManager("");
		}
		
		return statement;
	}
	
	private StatementReport loadInternshipStudentStatement(Internship internship){
		StatementReport statement = new StatementReport();
		
		statement.setName(internship.getSupervisor().getName());
		statement.setStartTime(internship.getStartDate());
		
		if(internship.getStatus() == InternshipStatus.CURRENT){
			statement.setEndTime(null);
		}else{
			statement.setEndTime(internship.getEndDate());
		}
		
		statement.setGuid(this.generateGuid());
		statement.setLink(this.getLink(statement.getGuid()));
		
		try {
			statement.setQrCode(new ByteArrayInputStream(this.createQRCode(statement.getLink(), 100, 100)));
		} catch (WriterException | IOException e1) {
			e1.printStackTrace();
		}
		
		statement.setEvent("Estágio");
		
		if(internship.getType() == InternshipType.REQUIRED){
			statement.setDetailedEvent("Estágio Obrigatório");
		}else{
			statement.setDetailedEvent("Estágio não Obrigatório");
		}
		
		statement.setStudent(internship.getStudent().getName());
		statement.setCompany(internship.getCompany().getName());
		
		try {
			UserBO bo = new UserBO();
			User user = bo.findManager(internship.getDepartment().getIdDepartment(), SystemModule.SIGES);
			
			statement.setManagerName(user.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setManagerName("");
		}
		
		try {
			UserBO bo = new UserBO();
			User manager = bo.findDepartmentManager(internship.getDepartment().getIdDepartment());
			
			statement.setDepartmentManager(manager.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setDepartmentManager("");
		}
		
		return statement;
	}
	
	private StatementReport loadInternshipJuryProfessorStatement(InternshipJuryAppraiser appraiser){
		StatementReport statement = new StatementReport();
		
		statement.setDate(appraiser.getInternshipJury().getDate());
		statement.setEndTime(appraiser.getInternshipJury().getEndTime());
		statement.setName(appraiser.getAppraiser().getName());
		statement.setStartTime(appraiser.getInternshipJury().getStartTime());
		statement.setGuid(this.generateGuid());
		statement.setLink(this.getLink(statement.getGuid()));
		
		try {
			statement.setQrCode(new ByteArrayInputStream(this.createQRCode(statement.getLink(), 100, 100)));
		} catch (WriterException | IOException e1) {
			e1.printStackTrace();
		}
		
		statement.setEvent("Estágio Curricular Obrigatório");
		statement.setStudent(appraiser.getInternshipJury().getInternship().getStudent().getName());
		statement.setTitle(appraiser.getInternshipJury().getInternship().getReportTitle());
		statement.setCompany(appraiser.getInternshipJury().getInternship().getCompany().getName());
		
		int idDepartment = 0;
		
		try {
			idDepartment = new InternshipJuryBO().findIdDepartment(appraiser.getInternshipJury().getIdInternshipJury());
		} catch (Exception e) { }
		
		try {
			UserBO bo = new UserBO();
			User user = bo.findManager(idDepartment, SystemModule.SIGES);
			
			statement.setManagerName(user.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setManagerName("");
		}
		
		try {
			UserBO bo = new UserBO();
			User manager = bo.findDepartmentManager(idDepartment);
			
			statement.setDepartmentManager(manager.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setDepartmentManager("");
		}
		
		return statement;
	}
	
	private StatementReport loadInternshipJuryStudentStatement(InternshipJuryStudent student){
		StatementReport statement = new StatementReport();
		
		statement.setDate(student.getInternshipJury().getDate());
		statement.setEndTime(student.getInternshipJury().getEndTime());
		statement.setName(student.getStudent().getName());
		statement.setStartTime(student.getInternshipJury().getStartTime());
		statement.setStudentCode(student.getStudent().getStudentCode());
		statement.setGuid(this.generateGuid());
		statement.setLink(this.getLink(statement.getGuid()));
		
		try {
			statement.setQrCode(new ByteArrayInputStream(this.createQRCode(statement.getLink(), 100, 100)));
		} catch (WriterException | IOException e1) {
			e1.printStackTrace();
		}
		
		statement.setEvent("Estágio Curricular Obrigatório");
		statement.setStudent(student.getInternshipJury().getInternship().getStudent().getName());
		statement.setTitle(student.getInternshipJury().getInternship().getReportTitle());
		statement.setCompany(student.getInternshipJury().getInternship().getCompany().getName());
		
		int idDepartment = 0;
		
		try {
			idDepartment = new InternshipJuryBO().findIdDepartment(student.getInternshipJury().getIdInternshipJury());
		} catch (Exception e) { }
		
		try {
			UserBO bo = new UserBO();
			User user = bo.findManager(idDepartment, SystemModule.SIGES);
			
			statement.setManagerName(user.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setManagerName("");
		}
		
		try {
			UserBO bo = new UserBO();
			User manager = bo.findDepartmentManager(idDepartment);
			
			statement.setDepartmentManager(manager.getName());
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			statement.setDepartmentManager("");
		}
		
		return statement;
	}
	
	private String generateGuid(){
		/*String guid = UUID.randomUUID().toString();

		for(int i = 0; i < 100; i++){
			Certificate certificate = null;
			
			try{
				CertificateDAO dao = new CertificateDAO();
				
				certificate = dao.findByGuid(guid);
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
			
			if(certificate == null){
				return guid;
			}
		}
		
		return "";*/
		
		return UUID.randomUUID().toString();
	}
	
	private String getLink(String guid){
		return AppConfig.getInstance().getHost() + "/#!authenticate/" + guid;
	}
	
	private byte[] createQRCode(String qrCodeData, int qrCodeheight, int qrCodewidth) throws WriterException, IOException {
		String charset = "UTF-8"; // or "ISO-8859-1"
		Map hintMap = new HashMap();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset), BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		MatrixToImageWriter.writeToStream(matrix, "jpeg", stream);
		
		return stream.toByteArray();
	}
	
	private int save(Certificate certificate) throws Exception{
		if((certificate.getDepartment() == null) || (certificate.getDepartment().getIdDepartment() == 0)){
			throw new Exception("Informe o departamento que gerou o certificado.");
		}
		if((certificate.getUser() == null) || (certificate.getUser().getIdUser() == 0)){
			throw new Exception("Informe o usuário que recebeu o certificado.");
		}
		if(certificate.getDate() == null){
			throw new Exception("Informe a data de geração do certificado.");
		}
		if(certificate.getGuid().isEmpty()){
			throw new Exception("Informe o GUID do certificado.");
		}
		if(certificate.getFile() == null){
			throw new Exception("Informe o arquivo do certificado.");
		}
		
		try{
			CertificateDAO dao = new CertificateDAO();
			
			return dao.save(certificate);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
