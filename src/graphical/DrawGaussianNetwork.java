/**
 * This program can draw a Gaussian network, a Honeycomb network, or 
 * the difference between their distance distributions. 
 * These networks can be drawn in two different representation:
 * Diamond and Square.
 * 
 * The numbers shown on each node represent the distance of a 
 * given node to the origin. 
 * 
 * The program takes two integer as a and b. These two integers
 * are the parameters of a Gaussian network generated by the
 * complex number a+bi. 
 * 
 * Assumptions: a and b are two (small) positive integers and a <= b
 * 
 */
package graphical;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Three different networks supported
 */
enum NETWORK {
	GAUSSIAN, HONEYCOMB, DIFFERENCE
};

/**
 * Gaussian are degree-4 networks whereas honeycomb are degree-3 network
 */
enum DEGREE {
	THREE, FOUR
};

/**
 * Type of a node which is either regular or boundary node
 * 
 */
enum NODE_TYPE {
	REGULAR, BOUNDARY
};

@SuppressWarnings("serial")
public class DrawGaussianNetwork extends JFrame {

	class DrawPanel extends JPanel {

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			// if user has not entered a and b yet, do nothing
			if (a == 0 && b == 0) {
				return;
			}
			doDrawing(g);
		}

		/**
		 * Draw the network by calling doDrawing on each node
		 * 
		 * @param g
		 *            Graphic parameter
		 */
		private void doDrawing(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			// calculate the drawing area
			Dimension size = getSize();
			Insets insets = getInsets();

			int panelW = size.width - insets.left - insets.right;
			int panelH = size.height - insets.top - insets.bottom;

			if (jrbRepresentation == REPRESENTATION.SQUARE) {
				switch (jrbNetwork) {
				case GAUSSIAN:
					gaussianNetwork = new SquareGaussian(a, b, panelW, panelH,
							factor);
					break;
				case HONEYCOMB:
					gaussianNetwork = new HoneycombGaussian(new SquareGaussian(
							a, b, panelW, panelH, factor));
					gaussianNetwork.setNodeDegrees();
					break;
				case DIFFERENCE:
					gaussianNetwork = new HoneycombGaussian(new SquareGaussian(
							a, b, panelW, panelH, factor));
					gaussianNetwork.setNodeDegrees();
					gaussianNetwork.setDifference();
					break;
				default:
					System.err
							.println("Wrong type of jradio button, Default Gaussian is running\n"
									+ jrbNetwork);
					System.exit(1);
				}
			} else if (jrbRepresentation == REPRESENTATION.DIAMOND) {
				switch (jrbNetwork) {
				case GAUSSIAN:
					gaussianNetwork = new DiamondGaussian(a, b, panelW, panelH,
							factor);
					break;
				case HONEYCOMB:
					gaussianNetwork = new HoneycombGaussian(
							new DiamondGaussian(a, b, panelW, panelH, factor));
					gaussianNetwork.setNodeDegrees();
					break;
				case DIFFERENCE:
					gaussianNetwork = new HoneycombGaussian(
							new DiamondGaussian(a, b, panelW, panelH, factor));
					gaussianNetwork.setNodeDegrees();
					gaussianNetwork.setDifference();
					break;
				default:
					System.err
							.println("Wrong type of jradio button, Default Gaussian is running\n"
									+ jrbNetwork);
					System.exit(1);
				}
			} else {
				System.err.println("Wrong type of network representatoin\n");
				System.exit(1);
			}

			gaussianNetwork.doDrawing(g2d, isWraparound);

			textArea.setText("Norm = " + gaussianNetwork.getNorm() + "\nt = "
					+ gaussianNetwork.getT() + "\nd = "
					+ gaussianNetwork.getD());

		}

	}

	/**
	 * The main panel
	 * 
	 */
	class Panel extends JPanel implements ActionListener, ChangeListener {

		private void doDrawing(Graphics g) {
			// add action listener to inputs and button
			inputA.addActionListener(this);
			inputB.addActionListener(this);
			drawButton.addActionListener(this);
			jrbGaussian.addActionListener(this);
			jrbHoneycombGaussian.addActionListener(this);
			jrbDifference.addActionListener(this);
			slider.addChangeListener(this);
			jrbDiamond.addActionListener(this);
			jrbSquare.addActionListener(this);
			checkBox.addActionListener(this);
		}

		@Override
		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			doDrawing(g);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				a = Integer.parseInt(inputA.getText());
				b = Integer.parseInt(inputB.getText());
			} catch (NumberFormatException ex) {
				System.err.println("a and b must be integer!");
				System.exit(1);
			}
			
			// a and b should be positive
			if ( a < 0 || b < 0 ){
				System.err.println("a and b must be positive integer!");
				System.exit(1);				
			}

			inputA.selectAll();
			inputB.selectAll();

			if (e.getSource() == jrbGaussian) {
				jrbNetwork = NETWORK.GAUSSIAN;
			}
			if (e.getSource() == jrbHoneycombGaussian) {
				jrbNetwork = NETWORK.HONEYCOMB;
			}
			if (e.getSource() == jrbDifference) {
				jrbNetwork = NETWORK.DIFFERENCE;
			}

			if (e.getSource() == jrbDiamond) {
				jrbRepresentation = REPRESENTATION.DIAMOND;
			}
			if (e.getSource() == jrbSquare) {
				jrbRepresentation = REPRESENTATION.SQUARE;
			}

			if (e.getSource() == checkBox) {
				isWraparound = checkBox.isSelected() ? true : false;
			}

			// Make sure the new text is visible, even if there
			// was a selection in the text area.
			textArea.setCaretPosition(textArea.getDocument().getLength());

			panel.repaint();
			drawPanel.repaint();
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			factor = slider.getValue();
			panel.repaint();
			drawPanel.repaint();
		}

	} // end of class - DrawPanel

	protected JTextArea textArea;
	protected JTextField inputA;
	protected JTextField inputB;
	protected JLabel labelA;
	protected JLabel labelB;
	protected JButton drawButton;
	protected JSlider slider;
	protected JRadioButton jrbGaussian;
	protected JRadioButton jrbHoneycombGaussian;
	protected JRadioButton jrbDifference;
	protected ButtonGroup jrbGroup;
	protected JRadioButton jrbSquare;
	protected JRadioButton jrbDiamond;
	protected ButtonGroup jrbRepGroup;
	protected JCheckBox checkBox;

	protected GaussianNetwork gaussianNetwork;
	protected SquareGaussian squareGaussian;

	// parameters of the Gaussian network
	private int a;
	private int b;
	private int factor = 10;
	private NETWORK jrbNetwork = NETWORK.GAUSSIAN;
	private REPRESENTATION jrbRepresentation = REPRESENTATION.DIAMOND;

	protected Panel panel;
	protected DrawPanel drawPanel;

	protected boolean isWraparound = false;

	public DrawGaussianNetwork() {
		initUI();
	}

	public final void initUI() {

		panel = new Panel();

		labelA = new JLabel("   a = ");
		labelA.setFont(new Font("Georgia", Font.PLAIN, 14));
		labelA.setForeground(new Color(50, 50, 25));

		labelB = new JLabel("   b = ");
		labelB.setFont(new Font("Georgia", Font.PLAIN, 14));
		labelB.setForeground(new Color(50, 50, 25));

		inputA = new JTextField(4);

		inputB = new JTextField(4);

		drawButton = new JButton("Draw Network");

		textArea = new JTextArea(3, 10);
		textArea.setEditable(false);

		panel.add(labelA);
		panel.add(inputA);
		panel.add(labelB);
		panel.add(inputB);
		panel.add(drawButton);
		panel.add(textArea);

		// Create the radio buttons and assign Keyboard shortcuts using
		// Mnemonics
		jrbGaussian = new JRadioButton("Gaussian");
		jrbGaussian.setMnemonic(KeyEvent.VK_N);
		jrbGaussian.setActionCommand("Gaussian");
		jrbGaussian.setSelected(true);

		jrbHoneycombGaussian = new JRadioButton("Honeycomb");
		jrbHoneycombGaussian.setMnemonic(KeyEvent.VK_A);
		jrbHoneycombGaussian.setActionCommand("HoneycombGaussian");

		jrbDifference = new JRadioButton("Difference");
		jrbDifference.setMnemonic(KeyEvent.VK_F);
		jrbDifference.setActionCommand("Difference");

		jrbGroup = new ButtonGroup();
		jrbGroup.add(jrbGaussian);
		jrbGroup.add(jrbHoneycombGaussian);
		jrbGroup.add(jrbDifference);

		panel.add(jrbGaussian);
		panel.add(jrbHoneycombGaussian);
		panel.add(jrbDifference);

		// add slider to zoom in & out
		slider = new JSlider(10, 50, 10);
		slider.setPreferredSize(new Dimension(150, 50));
		TitledBorder sliderTitledBorder = BorderFactory
				.createTitledBorder("Zoom");
		sliderTitledBorder.setTitleJustification(TitledBorder.CENTER);
		slider.setBorder(sliderTitledBorder);
		panel.add(slider);

		// create radio buttons for network representation
		jrbDiamond = new JRadioButton("Diamond");
		jrbDiamond.setMnemonic(KeyEvent.VK_D);
		jrbDiamond.setActionCommand("Diamond");
		jrbDiamond.setSelected(true);

		jrbSquare = new JRadioButton("Square");
		jrbSquare.setMnemonic(KeyEvent.VK_S);
		jrbSquare.setActionCommand("Square");

		jrbRepGroup = new ButtonGroup();
		jrbRepGroup.add(jrbDiamond);
		jrbRepGroup.add(jrbSquare);

		panel.add(jrbDiamond);
		panel.add(jrbSquare);

		checkBox = new JCheckBox("Wraparound", false);
		panel.add(checkBox);

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setPreferredSize(new Dimension(140, 200));

		setLayout(new BorderLayout());

		drawPanel = new DrawPanel();

		drawPanel.setBackground(Color.white);
		drawPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// drawPanel.setPreferredSize(new Dimension(1000, 1000));

		add(panel, BorderLayout.WEST);
		add(drawPanel, BorderLayout.CENTER);

		setSize(600, 420);

		// pack();

		setTitle("Graphical Gaussian Network");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DrawGaussianNetwork ex = new DrawGaussianNetwork();
				ex.setVisible(true);
			}
		});
	}

}