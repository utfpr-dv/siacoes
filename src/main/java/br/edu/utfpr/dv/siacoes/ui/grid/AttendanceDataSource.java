package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class AttendanceDataSource extends BasicDataSource {

	private LocalDate date;
	private LocalDateTime start;
	private LocalDateTime end;
	private String comments;
	
	public AttendanceDataSource(Attendance attendance) {
		this.setId(attendance.getIdAttendance());
		this.setDate(DateUtils.convertToLocalDate(attendance.getDate()));
		this.setStart(DateUtils.convertToLocalDateTime(attendance.getStartTime()));
		this.setEnd(DateUtils.convertToLocalDateTime(attendance.getEndTime()));
		this.setComments(attendance.getComments());
	}
	
	public static List<AttendanceDataSource> load(List<Attendance> list) {
		List<AttendanceDataSource> ret = new ArrayList<AttendanceDataSource>();
		
		for(Attendance attendance : list) {
			ret.add(new AttendanceDataSource(attendance));
		}
		
		return ret;
	}
	
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalDateTime getStart() {
		return start;
	}
	public void setStart(LocalDateTime start) {
		this.start = start;
	}
	public LocalDateTime getEnd() {
		return end;
	}
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
