package org.reldb.relui.parts;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.widgets.Composite;

import org.reldb.relui.dbui.DbMain;
import org.reldb.relui.tools.MainPanel;

public class Main {
	@Inject
	public Main() {		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		MainPanel mainPanel = new MainPanel(parent, SWT.None);
		mainPanel.addMenuDetectListener(new MenuDetectListener() {
			@Override
			public void menuDetected(MenuDetectEvent e) {
				System.out.println("Main: menu detected " + e);
			}
		});
		DbMain.setMainPanel(mainPanel);
	}

}