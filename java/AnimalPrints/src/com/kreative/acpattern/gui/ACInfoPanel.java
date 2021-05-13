package com.kreative.acpattern.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.kreative.acpattern.ACBaseFile;

public class ACInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JTextField titleField;
	private final JTextField creatorField;
	private final JTextField creatorIdField;
	private final JTextField townField;
	private final JTextField townIdField;
	
	public ACInfoPanel() {
		this.titleField = new JTextField(24);
		this.creatorField = new JTextField();
		this.creatorIdField = new JTextField(8);
		this.townField = new JTextField();
		this.townIdField = new JTextField(8);
		
		titleField.setToolTipText("The name of the design");
		creatorField.setToolTipText("The name of the player who created the design");
		creatorIdField.setToolTipText("The internal ID number for the player who created the design");
		townField.setToolTipText("The name of the town/island where the design was created");
		townIdField.setToolTipText("The internal ID number for the town/island where the design was created");
		
		JPanel creatorPanel = centerAndRight(creatorField, creatorIdField);
		JPanel townPanel = centerAndRight(townField, townIdField);
		
		JPanel iconPanel = column(icon("graphics.png"), icon("person.png"), icon("home.png"));
		JPanel textPanel = column(titleField, creatorPanel, townPanel);
		
		setLayout(new BorderLayout(4, 4));
		add(iconPanel, BorderLayout.LINE_START);
		add(textPanel, BorderLayout.CENTER);
	}
	
	public void pullFrom(ACBaseFile src) {
		titleField.setText(src.getTitle());
		creatorField.setText(src.getCreatorName());
		creatorIdField.setText(Integer.toHexString(src.getCreatorID()).toUpperCase());
		townField.setText(src.getTownName());
		townIdField.setText(Integer.toHexString(src.getTownID()).toUpperCase());
	}
	
	public void pushTo(ACBaseFile dst) {
		dst.setTitle(titleField.getText());
		dst.setCreatorName(creatorField.getText());
		try { dst.setCreatorID((int)Long.parseLong(creatorIdField.getText(), 16)); }
		catch (NumberFormatException nfe) {}
		dst.setTownName(townField.getText());
		try { dst.setTownID((int)Long.parseLong(townIdField.getText(), 16)); }
		catch (NumberFormatException nfe) {}
	}
	
	private static JPanel centerAndRight(Component c, Component r) {
		JPanel p = new JPanel(new BorderLayout(4, 4));
		p.add(c, BorderLayout.CENTER);
		p.add(r, BorderLayout.LINE_END);
		return p;
	}
	
	private static JPanel column(Component... cc) {
		JPanel p = new JPanel(new GridLayout(0, 1, 4, 4));
		for (Component c : cc) p.add(c);
		return p;
	}
	
	private static JLabel icon(String name) {
		return new JLabel(new ImageIcon(ACInfoPanel.class.getResource(name)));
	}
}
