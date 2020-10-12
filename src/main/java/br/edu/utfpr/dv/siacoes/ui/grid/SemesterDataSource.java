package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class SemesterDataSource extends BasicDataSource {

	private int semester;
	private int year;
	private LocalDate start;
	private LocalDate end;
	
	public SemesterDataSource(Semester semester) {
		this.setId(Integer.parseInt(String.valueOf(semester.getYear()) + String.valueOf(semester.getSemester()) + String.valueOf(semester.getCampus().getIdCampus())));
		this.setSemester(semester.getSemester());
		this.setYear(semester.getYear());
		this.setStart(DateUtils.convertToLocalDate(semester.getStartDate()));
		this.setEnd(DateUtils.convertToLocalDate(semester.getEndDate()));
	}
	
	public static List<SemesterDataSource> load(List<Semester> list) {
		List<SemesterDataSource> ret = new ArrayList<SemesterDataSource>();
		
		for(Semester semester : list) {
			ret.add(new SemesterDataSource(semester));
		}
		
		return ret;
	}
	
	public static int getYearFromId(int id) {
		String value = String.valueOf(id).substring(0, 3);
		
		return Integer.parseInt(value);
	}
	
	public static int getSemesterFromId(int id) {
		String value = String.valueOf(id).substring(4, 4);
		
		return Integer.parseInt(value);
	}
	
	public static int getIdCampusFromId(int id) {
		String value = String.valueOf(id).substring(5, String.valueOf(id).length() - 1);
		
		return Integer.parseInt(value);
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
	public LocalDate getStart() {
		return start;
	}
	public void setStart(LocalDate start) {
		this.start = start;
	}
	public LocalDate getEnd() {
		return end;
	}
	public void setEnd(LocalDate end) {
		this.end = end;
	}
	
}
