package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryRequest;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class ProfessorScheduleDataSource extends BasicDataSource {

	private LocalDate date;
	private LocalDateTime start;
	private LocalDateTime end;
	private int type;
	private String description;
	
	public ProfessorScheduleDataSource(Jury jury) {
		this.setId(jury.getIdJury());
		this.setType(1);
		this.setDate(DateUtils.convertToLocalDate(jury.getDate()));
		this.setStart(DateUtils.convertToLocalDateTime(jury.getDate()));
		this.setEnd(DateUtils.convertToLocalDateTime(jury.getDate()).plusHours(1));
		if(jury.getStage() == 2) {
			this.setEnd(this.getEnd().plusMinutes(30));
			this.setDescription("Banca de TCC 2 - " + jury.getStudent());
		} else {
			this.setDescription("Banca de TCC 1 - " + jury.getStudent());
		}
	}
	
	public ProfessorScheduleDataSource(JuryRequest jury) {
		this.setId(jury.getIdJuryRequest());
		this.setType(2);
		this.setDate(DateUtils.convertToLocalDate(jury.getDate()));
		this.setStart(DateUtils.convertToLocalDateTime(jury.getDate()));
		this.setEnd(DateUtils.convertToLocalDateTime(jury.getDate()).plusHours(1));
		if(jury.getStage() == 2) {
			this.setEnd(this.getEnd().plusMinutes(30));
			this.setDescription("* Banca de TCC 2 - " + jury.getStudent());
		} else {
			this.setDescription("* Banca de TCC 1 - " + jury.getStudent());
		}
	}
	
	public ProfessorScheduleDataSource(InternshipJury jury) {
		this.setId(jury.getIdInternshipJury());
		this.setType(3);
		this.setDate(DateUtils.convertToLocalDate(jury.getDate()));
		this.setStart(DateUtils.convertToLocalDateTime(jury.getDate()));
		this.setEnd(DateUtils.convertToLocalDateTime(jury.getDate()).plusHours(1));
		this.setDescription("Banca de Estágio - " + jury.getStudent());
	}
	
	public ProfessorScheduleDataSource(InternshipJuryRequest jury) {
		this.setId(jury.getIdInternshipJuryRequest());
		this.setType(4);
		this.setDate(DateUtils.convertToLocalDate(jury.getDate()));
		this.setStart(DateUtils.convertToLocalDateTime(jury.getDate()));
		this.setEnd(DateUtils.convertToLocalDateTime(jury.getDate()).plusHours(1));
		this.setDescription("* Banca de Estágio - " + jury.getStudent());
	}
	
	public static List<ProfessorScheduleDataSource> load(List<Jury> listJury, List<JuryRequest> listJuryRequest, List<InternshipJury> listInternshipJury, List<InternshipJuryRequest> listInternshipJuryRequest) {
		List<ProfessorScheduleDataSource> ret = new ArrayList<ProfessorScheduleDataSource>();
		
		for(Jury jury : listJury) {
			ret.add(new ProfessorScheduleDataSource(jury));
		}
		
		for(JuryRequest jury : listJuryRequest) {
			ret.add(new ProfessorScheduleDataSource(jury));
		}
		
		for(InternshipJury jury : listInternshipJury) {
			ret.add(new ProfessorScheduleDataSource(jury));
		}
		
		for(InternshipJuryRequest jury : listInternshipJuryRequest) {
			ret.add(new ProfessorScheduleDataSource(jury));
		}
		
		Collections.sort(ret, new Comparator<ProfessorScheduleDataSource>() {
			@Override
			public int compare(ProfessorScheduleDataSource e1, ProfessorScheduleDataSource e2) {
				if (e1.getDate().isBefore(e2.getDate())) {
		            return -1;
		        } else if (e1.getDate().isAfter(e2.getDate())) {
		            return 1;
		        } else {
		            return 0;
		        }       
			}
		});
		
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
