package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EventLogDataSource extends BasicDataSource {

	private LocalDateTime date;
	private String type;
	private String user;
	private String table;
	
	public EventLogDataSource(UpdateEvent event) {
		this.setId((int)event.getIdLog());
		this.setDate(DateUtils.convertToLocalDateTime(event.getDate()));
		this.setType(event.getEvent().toString());
		this.setUser(event.getUser().getName());
		this.setTable(event.getClassName());
	}
	
	public static List<EventLogDataSource> load(List<UpdateEvent> list) {
		List<EventLogDataSource> ret = new ArrayList<EventLogDataSource>();
		
		for(UpdateEvent event : list) {
			ret.add(new EventLogDataSource(event));
		}
		
		return ret;
	}
	
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	
}
