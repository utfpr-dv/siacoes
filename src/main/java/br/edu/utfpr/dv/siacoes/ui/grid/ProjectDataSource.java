package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class ProjectDataSource extends BasicDataSource {

	private int semester;
	private int year;
	private String student;
	private String supervisor;
	private String title;
	private LocalDate submisstion;
	
	public ProjectDataSource(Project project) {
		this.setId(project.getIdProject());
		this.setSemester(project.getSemester());
		this.setYear(project.getYear());
		this.setStudent(project.getStudent().getName());
		this.setSupervisor(project.getSupervisor().getName());
		this.setTitle(project.getTitle());
		this.setSubmisstion(DateUtils.convertToLocalDate(project.getSubmissionDate()));
	}
	
	public static List<ProjectDataSource> load(List<Project> list) {
		List<ProjectDataSource> ret = new ArrayList<ProjectDataSource>();
		
		for(Project project : list) {
			ret.add(new ProjectDataSource(project));
		}
		
		return ret;
	}
	
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public LocalDate getSubmisstion() {
		return submisstion;
	}
	public void setSubmisstion(LocalDate submisstion) {
		this.submisstion = submisstion;
	}
	
}
