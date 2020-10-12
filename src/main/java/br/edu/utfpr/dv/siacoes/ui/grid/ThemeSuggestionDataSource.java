package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ThemeSuggestion;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class ThemeSuggestionDataSource extends BasicDataSource {

	private LocalDate submission;
	private String title;
	private String user;
	
	public ThemeSuggestionDataSource(ThemeSuggestion theme) {
		this.setId(theme.getIdThemeSuggestion());
		this.setSubmission(DateUtils.convertToLocalDate(theme.getSubmissionDate()));
		this.setTitle(theme.getTitle());
		this.setUser(theme.getUser().getName());
	}
	
	public static List<ThemeSuggestionDataSource> load(List<ThemeSuggestion> list) {
		List<ThemeSuggestionDataSource> ret = new ArrayList<ThemeSuggestionDataSource>();
		
		for(ThemeSuggestion theme : list) {
			ret.add(new ThemeSuggestionDataSource(theme));
		}
		
		return ret;
	}
	
	public LocalDate getSubmission() {
		return submission;
	}
	public void setSubmission(LocalDate submission) {
		this.submission = submission;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
}
