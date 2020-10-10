package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.User;

public class StudentDataSource extends BasicDataSource {

	private String name;
	private String studentCode;
	
	public StudentDataSource(User user) {
		this.setId(user.getIdUser());
		this.setName(user.getName());
		this.setStudentCode(user.getStudentCode());
	}
	
	public static List<StudentDataSource> load(List<User> list) {
		List<StudentDataSource> ret = new ArrayList<StudentDataSource>();
		
		for(User user : list) {
			ret.add(new StudentDataSource(user));
		}
		
		return ret;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStudentCode() {
		return studentCode;
	}
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}
	
}
