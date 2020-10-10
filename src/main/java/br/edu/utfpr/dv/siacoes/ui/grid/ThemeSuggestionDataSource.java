package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ThemeSuggestion;

public class ThemeSuggestionDataSource extends BasicDataSource {

	private Date submission;
	private String title;
	private String user;
	
	public ThemeSuggestionDataSource(ThemeSuggestion theme) {
		this.setId(theme.getIdThemeSuggestion());
		this.setSubmission(theme.getSubmissionDate());
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
	
	public Date getSubmission() {
		return submission;
	}
	public void setSubmission(Date submission) {
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
