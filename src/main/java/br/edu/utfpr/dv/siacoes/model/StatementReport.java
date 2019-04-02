package br.edu.utfpr.dv.siacoes.model;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class StatementReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String studentCode;
	private String name;
	private String event;
	private String detailedEvent;
	private String student;
	private String title;
	private String company;
	private Date date;
	private Date startTime;
	private Date endTime;
	private String managerName;
	private String departmentManager;
	private String guid;
	private String link;
	private String type;
	private int semester;
	private int year;
	private InputStream qrCode;
	private Date generatedDate;
	
	public StatementReport(){
		this.setGeneratedDate(DateUtils.getNow().getTime());
		this.setStudentCode("");
		this.setName("");
		this.setEvent("");
		this.setDetailedEvent("");
		this.setStudent("");
		this.setTitle("");
		this.setCompany("");
		this.setDate(new Date());
		this.setStartTime(new Date());
		this.setEndTime(new Date());
		this.setManagerName("");
		this.setDepartmentManager("");
		this.setGuid("");
		this.setLink("");
		this.setQrCode(null);
		this.setType("");
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
	}
	
	public String getStudentCode(){
		return studentCode;
	}
	
	public void setStudentCode(String studentCode){
		this.studentCode = studentCode;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getDetailedEvent() {
		return detailedEvent;
	}
	public void setDetailedEvent(String detailedEvent) {
		this.detailedEvent = detailedEvent;
	}
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getDepartmentManager() {
		return departmentManager;
	}
	public void setDepartmentManager(String departmentManager) {
		this.departmentManager = departmentManager;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public InputStream getQrCode() {
		return qrCode;
	}
	public void setQrCode(InputStream qrCode) {
		this.qrCode = qrCode;
	}
	public Date getGeneratedDate() {
		return generatedDate;
	}
	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}
	public String getType(){
		return this.type;
	}
	public void setType(String type){
		this.type = type;
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

}
