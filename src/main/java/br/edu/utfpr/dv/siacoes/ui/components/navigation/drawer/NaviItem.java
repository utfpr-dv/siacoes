package br.edu.utfpr.dv.siacoes.ui.components.navigation.drawer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import br.edu.utfpr.dv.siacoes.ui.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/components/navi-item.css")
public class NaviItem extends Div {

	private String CLASS_NAME = "navi-item";

	private int level = 0;

	private Component link;
	private Class<? extends Component> navigationTarget;
	private String text;

	protected Button expandCollapse;

	private List<NaviItem> subItems;
	private boolean subItemsVisible;

	public NaviItem(VaadinIcon icon, String text, Class<? extends Component> navigationTarget) {
		this(text, navigationTarget);
		link.getElement().insertChild(0, new Icon(icon).getElement());
	}
	
	public NaviItem(VaadinIcon icon, int notificationCount, String text, Class<? extends Component> navigationTarget) {
		this(text, navigationTarget);
		
		if(notificationCount > 0) {
			Div div = new Div();
			div.setHeight("24px");
			div.setWidth("24px");
			div.getElement().getStyle().set("position", "relative");
			div.add(new Icon(icon));
			
			Span span = new Span(notificationCount > 9 ? "9+" : String.valueOf(notificationCount));
			span.setHeight("15px");
			span.setWidth("15px");
			span.getElement().getStyle().set("font-weight", "600");
			span.getElement().getStyle().set("background", "red");
			span.getElement().getStyle().set("border-radius", "7px");
			span.getElement().getStyle().set("color", "white");
			span.getElement().getStyle().set("font-size", "8pt");
			span.getElement().getStyle().set("text-align", "center");
			span.getElement().getStyle().set("display", "block");
			
			div.add(span);
			span.getElement().getStyle().set("position", "absolute");
			span.getElement().getStyle().set("top", "0px");
			span.getElement().getStyle().set("right", "0px");
			
			link.getElement().insertChild(0, div.getElement());
		} else {
			link.getElement().insertChild(0, new Icon(icon).getElement());
		}
	}

	public NaviItem(Image image, String text, Class<? extends Component> navigationTarget) {
		this(text, navigationTarget);
		link.getElement().insertChild(0, image.getElement());
	}

	public NaviItem(String text, Class<? extends Component> navigationTarget) {
		setClassName(CLASS_NAME);
		setLevel(0);

		this.text = text;
		this.navigationTarget = navigationTarget;

		if (navigationTarget != null) {
			RouterLink routerLink = new RouterLink(null, navigationTarget);
			routerLink.add(new Span(text));
			routerLink.setClassName(CLASS_NAME + "__link");
			routerLink.setHighlightCondition(HighlightConditions.sameLocation());
			this.link = routerLink;
		} else {
			Div div = new Div(new Span(text));
			div.addClickListener(e -> expandCollapse.click());
			div.setClassName(CLASS_NAME + "__link");
			this.link = div;
		}

		expandCollapse = UIUtils.createButton(VaadinIcon.CARET_UP, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
		expandCollapse.addClickListener(event -> setSubItemsVisible(!subItemsVisible));
		expandCollapse.setVisible(false);

		subItems = new ArrayList<>();
		subItemsVisible = true;
		updateAriaLabel();

		add(link, expandCollapse);
	}

	private void updateAriaLabel() {
		String action = (subItemsVisible ? "Collapse " : "Expand ") + text;
		UIUtils.setAriaLabel(action, expandCollapse);
	}

	public boolean isHighlighted(AfterNavigationEvent e) {
		return link instanceof RouterLink && ((RouterLink) link)
				.getHighlightCondition().shouldHighlight((RouterLink) link, e);
	}

	public void setLevel(int level) {
		this.level = level;
		if (level > 0) {
			getElement().setAttribute("level", Integer.toString(level));
		}
	}

	public int getLevel() {
		return level;
	}

	public Class<? extends Component> getNavigationTarget() {
		return navigationTarget;
	}

	public void addSubItem(NaviItem item) {
		if (!expandCollapse.isVisible()) {
			expandCollapse.setVisible(true);
		}
		item.setLevel(getLevel() + 1);
		subItems.add(item);
	}
	
	public void collapse() {
		this.setSubItemsVisible(false);
	}
	
	public void expand() {
		this.setSubItemsVisible(true);
	}
	
	public void collapseAll() {
		subItems.forEach(item -> item.collapseAll());
		
		this.collapse();
	}
	
	public void expandAll() {
		subItems.forEach(item -> item.expandAll());
		
		this.expand();
	}

	private void setSubItemsVisible(boolean visible) {
		if (level == 0) {
			expandCollapse.setIcon(new Icon(visible ? VaadinIcon.CARET_UP : VaadinIcon.CARET_DOWN));
		}
		subItems.forEach(item -> item.setVisible(visible));
		subItemsVisible = visible;
		updateAriaLabel();
	}

	public String getText() {
		return text;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		// If true, we only update the icon. If false, we hide all the sub items
		if (visible) {
			if (level == 0) {
				expandCollapse.setIcon(new Icon(VaadinIcon.CARET_DOWN));
			}
		} else {
			setSubItemsVisible(visible);
		}
	}
}
