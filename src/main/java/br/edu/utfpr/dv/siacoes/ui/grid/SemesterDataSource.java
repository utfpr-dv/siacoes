package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Semester;

public class SemesterDataSource extends BasicDataSource {

	private int semester;
	private int year;
	private Date start;
	private Date end;
	
	public SemesterDataSource(Semester semester) {
		this.setId(Integer.parseInt(String.valueOf(semester.getYear()) + String.valueOf(semester.getSemester()) + String.valueOf(semester.getCampus().getIdCampus())));
		this.setSemester(semester.getSemester());
		this.setYear(semester.getYear());
		this.setStart(semester.getStartDate());
		this.setEnd(semester.getEndDate());
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
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	
}
