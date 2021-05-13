package com.kreative.acpattern.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PropertiesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JPanel labelPanel;
	private final JPanel componentPanel;
	
	public PropertiesPanel(Object... objects) {
		labelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		componentPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		setProperties(objects);
		
		JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
		mainPanel.add(labelPanel, BorderLayout.LINE_START);
		mainPanel.add(componentPanel, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.PAGE_START);
		setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
	}
	
	public void setProperties(Object... objects) {
		labelPanel.removeAll();
		componentPanel.removeAll();
		for (int i = 0; i < objects.length; i++) {
			JPanel p = ((i & 1) == 0) ? labelPanel : componentPanel;
			if (objects[i] == null) {
				p.add(new JPanel());
			} else if (objects[i] instanceof Component) {
				JPanel wrapper = new JPanel(new BorderLayout());
				wrapper.add((Component)objects[i], BorderLayout.LINE_START);
				p.add(wrapper);
			} else {
				p.add(new JLabel(objects[i].toString()));
			}
		}
	}
}
