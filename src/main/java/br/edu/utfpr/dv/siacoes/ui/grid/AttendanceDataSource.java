package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Attendance;

public class AttendanceDataSource extends BasicDataSource {

	private Date date;
	private Date start;
	private Date end;
	private String comments;
	
	public AttendanceDataSource(Attendance attendance) {
		this.setId(attendance.getIdAttendance());
		this.setDate(attendance.getDate());
		this.setStart(attendance.getStartTime());
		this.setEnd(attendance.getEndTime());
		this.setComments(attendance.getComments());
	}
	
	public static List<AttendanceDataSource> load(List<Attendance> list) {
		List<AttendanceDataSource> ret = new ArrayList<AttendanceDataSource>();
		
		for(Attendance attendance : list) {
			ret.add(new AttendanceDataSource(attendance));
		}
		
		return ret;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
