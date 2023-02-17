package Views;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JTextField;

import Controller.Controller;
import Models.Deck;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class BrowsePublicDeckPage extends JPanel {
	private JTextField textField;
	private JPanel dynamicPanel = new JPanel(new FlowLayout());
	private JPanel resultsContainer = new JPanel();
	private ArrayList<Deck> publicDecks = new ArrayList<>();
	private Controller controller;
	/**
	 * Create the panel.
	 */
	public BrowsePublicDeckPage(Controller controller) {
		this.controller = controller;
		initialize();
	}
	
	private void initialize() {
		setPreferredSize(new Dimension(750, 500));
		setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 738, 47);
		add(panel);
		
		JButton btnNewButton = new JButton("Back");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.landingPage();
			}
		});
		btnNewButton.setBounds(10, 11, 63, 26);
		panel.add(btnNewButton);
		
		JButton profileButton = new JButton("Profile");
		profileButton.setBounds(655, 11, 71, 26);
		panel.add(profileButton);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 58, 738, 111);
		add(panel_1);
		panel_1.setLayout(null);
		
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				publicDecks = controller.searchPublicDecks(textField.getText());
				
				dynamicPanel.removeAll();
				for (int i = 0; i < publicDecks.size(); i++) {
					JLabel deckTitle = new JLabel(publicDecks.get(i).getDeckTitle());
					JLabel deckCreatedBy = new JLabel("Created by: " + publicDecks.get(i).createdBy);
					JPanel deckPanel = new JPanel();
					deckPanel.setPreferredSize(new Dimension(150, 50));
					deckPanel.setBackground(Color.YELLOW);
					deckPanel.add(deckTitle);
					deckPanel.add(deckCreatedBy);
					Deck current = publicDecks.get(i);
					dynamicPanel.add(deckPanel);
					
					dynamicPanel.addMouseListener(new MouseAdapter() {
						public void mousePressed(MouseEvent me) {
							controller.deckPage(current);
						}
						
					});
					
					dynamicPanel.revalidate();
					dynamicPanel.repaint();
					
					
					
				}
			
					
				dynamicPanel.revalidate();
				dynamicPanel.repaint();
			}
		});
		textField.setBounds(186, 51, 367, 20);
		panel_1.add(textField);
		textField.setColumns(10);
		
		JLabel pageTitle = new JLabel("Search for all Public Decks");
		pageTitle.setBounds(286, 23, 153, 16);
		panel_1.add(pageTitle);
		
		
		resultsContainer.setBounds(0, 197, 738, 303);
		add(resultsContainer);
		
		dynamicPanel.setPreferredSize(new Dimension(150, 500));
		
		JScrollPane scrollPane = new JScrollPane(dynamicPanel);
		scrollPane.setMinimumSize(new Dimension(720, 303));
		scrollPane.setPreferredSize(new Dimension(728, 303));
		resultsContainer.add(scrollPane);

		
	}
}
