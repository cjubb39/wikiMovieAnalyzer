import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import java.awt.GridBagConstraints;
import javax.swing.DefaultComboBoxModel;
import java.awt.Insets;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class wikiAnalyzerGUI {

	private JFrame frmChaesWikipediaMovie;
	private JTextField txtOption;
	private JTextField txtOptionHere;
	private JTextArea textArea;

	private wikiGUIController control;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					wikiAnalyzerGUI window = new wikiAnalyzerGUI();
					window.frmChaesWikipediaMovie.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public wikiAnalyzerGUI() {
		this.control = new wikiGUIController();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChaesWikipediaMovie = new JFrame();
		frmChaesWikipediaMovie.setTitle("Chae's Wikipedia Movie Analyzer");
		frmChaesWikipediaMovie.setBounds(100, 100, 920, 600);
		frmChaesWikipediaMovie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 702, 0 };
		gridBagLayout.rowHeights = new int[] { 100, 50, 0, 80, 178, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		frmChaesWikipediaMovie.getContentPane().setLayout(gridBagLayout);

		final JComboBox comboBox = new JComboBox();
		comboBox
				.setModel(new DefaultComboBoxModel(
						new String[] {
								"<html><center>List all movies nominated for the Best Picture award for which one of the (OPTION1) was (OPTION2)</center></html>",
								"<html><center>For the Best Original Screenplay award, list the writers for the movie that was nominated/won title (OPTION1)</center></html>",
								"<html><center>List all actors nominated for a Best Leading Actor award whose role was playing (a/an) (OPTION1)</center></html>",
								"<html><center>For the year (OPTION1), list all actresses nominated for a Best Leading Actress award along with the movie and their age that year</center></html>",
								"<html><center>List all directors (with the corresponding movies) that have been nominated for at least (OPTION1) Best Director awards</center></html>",
								"<html><center>List the country (with the corresponding movies) that has been nominated the most number of times for Best Foreign Language Film award</center></html>",
								"<html><center>List all movies nominated for the (OPTION1) award that starred (OPTION2)</center></html>",
								"<html><center>List all movies that were nominated for Best Picture, Best Director, Best Leading Actor, and Best Leading Actress along with the number of awards won by each movie</center></html>" }));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.BOTH;
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 0;
		frmChaesWikipediaMovie.getContentPane().add(comboBox, gbc_comboBox);
		;

		JSplitPane splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		frmChaesWikipediaMovie.getContentPane().add(splitPane, gbc_splitPane);

		JButton btnClearAll = new JButton("Clear All");
		btnClearAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				txtOption.setText("");
				txtOptionHere.setText("");
			}
		});
		splitPane.setLeftComponent(btnClearAll);

		JButton btnSubmit = new JButton("Submit!");
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String option1 = "", option2 = "", response = "";
				String comboText = comboBox.getSelectedItem().toString();

				final Pattern oneOption = Pattern.compile("(.*).OPTION1.(.*)");
				final Pattern twoOption = Pattern
						.compile("(.*).OPTION1.(.*).OPTION2.(.*)");
				Matcher oneMatcher = oneOption.matcher(comboText);
				Matcher twoMatcher = twoOption.matcher(comboText);

				// get option values from text boxes
				try {
					option1 = txtOption.getText().trim();
				} catch (NullPointerException ex) {
				}

				try {
					option2 = txtOptionHere.getText().trim();
				} catch (NullPointerException ex) {
				}

				// put question top in result box
				if (twoMatcher.find()) {
					comboText = twoMatcher.group(1) + option1 + twoMatcher.group(2)
							+ option2 + twoMatcher.group(3);
				} else if (oneMatcher.find()) {
					comboText = oneMatcher.group(1) + option1 + oneMatcher.group(2);
				}

				comboText = comboText.split(">")[2].split("<")[0];

				int comboValue = comboBox.getSelectedIndex() + 1; // this will agree
																					// with numbering
																					// of questions
																					// on assignment

				try {
					switch (comboValue) {
					case 1:
						response = control.bestPictureSearch(option1, option2);
						break;

					case 2:
						response = control.bestOrigScreenplay(option1);
						break;

					case 3:
						response = control.bestActorRole(option1);
						break;

					case 4:
						response = control.actressAge(option1);
						break;

					case 5:
						response = control.directorThreshold(option1);
						break;

					case 6:
						response = control.topForeign();
						break;

					case 7:
						response = control.nomStarring(option1, option2);
						break;

					case 8:
						response = control.quadThreat();
						break;

					/*
					 * case 9: response = control.directorAgeThreshold(option1);
					 * break;
					 */
					}

				} catch (BadArgumentException excep) {
					if (excep.getMessage().equals(null)) {
						JOptionPane
								.showMessageDialog(
										null,
										"No Results! Either the search is null or your syntax is wrong! Consult README",
										"Whoops!", JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, excep.getMessage(),
								"Whoops!", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception excep) {
					excep.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Please check correct syntax! Consult README", "Whoops!",
							JOptionPane.ERROR_MESSAGE);
				}

				textArea.setText(comboText + "\n\n" + response);

			}
		});

		// generated GUI code
		splitPane.setRightComponent(btnSubmit);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		frmChaesWikipediaMovie.getContentPane().add(panel, gbc_panel);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblOptionBelow = new JLabel("     Option 1 Below");
		panel.add(lblOptionBelow, BorderLayout.WEST);

		JLabel lblOptionBelow_1 = new JLabel("Option 2 Below (if necessary)     ");
		panel.add(lblOptionBelow_1, BorderLayout.EAST);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		frmChaesWikipediaMovie.getContentPane().add(panel_1, gbc_panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		txtOption = new JTextField();
		panel_2.add(txtOption);
		txtOption.setColumns(10);

		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		txtOptionHere = new JTextField();
		panel_3.add(txtOptionHere);
		txtOptionHere.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		frmChaesWikipediaMovie.getContentPane().add(scrollPane, gbc_scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
	}

}
