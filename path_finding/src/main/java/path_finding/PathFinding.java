package path_finding;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import path_finding.dialog.JDialogCustomMapSize;
import path_finding.dialog.JFileChooserExportMap;
import path_finding.dialog.JFileChooserImportMap;

// import sun.awt.www.content.audio.wav;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;


public class PathFinding {

	// FRAME
	JFrame frame;
	// GENERAL VARIABLES
	private Map m = new Map();
	private int columns = m.getColumns();
	private int rows = m.getRows();
	private int delay = 30;
	private int tool = 0;
	private int checks = 0;
	private double length = 0;
	private long time = 0;
	private int curAlg = 0;
	private int WIDTH = 1145; // 850;
	private final int HEIGHT = 650;
	private final int MSIZE = 600;
	private int CSIZE = MSIZE / (columns > rows ? columns : rows);
	// UTIL ARRAYS
	private String[] algorithms = {"ACO", "ACO-BFS", "ACO-BFS-SP", "ACO-BFS-SP-N"};
	private String[] tools = { "Start", "Finish", "Wall", "Eraser" };
	// BOOLEANS
	private boolean solving = false;
	private boolean flag = true;
	// UTIL
	Node[][] map = m.getMap();
	ArrayList<Node> wallList;
	ArrayList<ArrayList<Node>> pathListACO;
	ArrayList<ArrayList<Node>> pathListACOG;
	ArrayList<ArrayList<Node>> pathListACOBFSAB;
	ArrayList<ArrayList<Node>> pathListACOBFS;
	ArrayList<ArrayList<Node>> pathListACOBFSSP;
	ArrayList<ArrayList<Node>> pathListACOBFSSPTG;

	ArrayList<Node> copyACOPath = new ArrayList<>();
	ArrayList<Node> copyACOGPath = new ArrayList<>();
	ArrayList<Node> copyACOBFSPath = new ArrayList<>();
	ArrayList<Node> copyACOBFSABPath = new ArrayList<>();
	ArrayList<Node> copyACOBFSSPPath = new ArrayList<>();
	ArrayList<Node> copyACOBFSSPTGPath = new ArrayList<>();

	Algorithm Alg = new Algorithm();
	Random r = new Random();
	// SLIDERS
	JSlider size = new JSlider(1, 5, 2);
	JSlider speed = new JSlider(0, 500, delay);
	JSlider obstacles = new JSlider(1, 100, 50);
	// LABELS
	JLabel algL = new JLabel("Algorithms");
	JLabel toolL = new JLabel("Toolbox");
	JLabel sizeL = new JLabel("Size:");
	JLabel cellsL = new JLabel(columns + "x" + rows);
	JLabel delayL = new JLabel("Delay:");
	JLabel msL = new JLabel(delay + "ms");
	JLabel obstacleL = new JLabel("Dens:");
	JLabel densityL = new JLabel(obstacles.getValue() + "%");
	JLabel checkL = new JLabel("Checks: " + checks);
	JLabel timeL = new JLabel("Time planning: " + time);
	JLabel lengthL = new JLabel("Path length: " + length);
	JLabel tableL = new JLabel("<html><nobr><b><font color=red>Tips:</font></b><br>"
			+ "&nbsp;&nbsp;+ Ctrl + Click chuột trái để bỏ chọn<br>"
			+ "&nbsp;&nbsp;+ Dùng phím mũi tên để chọn sẽ không<br>" + "chính xác bằng chuột</nobr></html>");
	// BUTTONS
	JButton searchB = new JButton("Start Search");
	JButton resetB = new JButton("Reset");
	JButton importB = new JButton("Import");
	JButton exportB = new JButton("Export");
	JButton genMapB = new JButton("Generate Map");
	JButton clearMapB = new JButton("Clear Map");
	// DROP DOWN
	JComboBox<String> algorithmsBx = new JComboBox<>(algorithms);
	JComboBox<String> toolBx = new JComboBox<>(tools);
	// TEXT AREAS
	JTextArea textArea = new JTextArea();
	// TABLE
	PathTable pathT = new PathTable();
	// PANELS
	JPanel menuBarP = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel toolP = new JPanel();
	JPanel ctrlsP = new JPanel();
	JPanel mapP = new JPanel();
	JPanel imExportP = new JPanel();
	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
	JPanel resultP = new JPanel(new BorderLayout());
	JPanel pathListP = new JPanel(new BorderLayout());
	// MENU
	JMenuBar mapMB = new JMenuBar();
	JMenu mapM = new JMenu("Map");
	JMenuItem customMapSizeMI = new JMenuItem("Custom map size", KeyEvent.VK_C);
	JCheckBoxMenuItem showMapNumCBMI = new JCheckBoxMenuItem("Show cell number");
	JMenuItem creditMI = new JMenuItem("Credit");
	// CANVAS
	Canvas canvas;
	// BORDER
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	public PathFinding() { // CONSTRUCTOR
		wallList = new ArrayList<>();
		pathListACO = new ArrayList<>();
		pathListACOG = new ArrayList<>();
		pathListACOBFSAB = new ArrayList<>();
		pathListACOBFS = new ArrayList<>();
		pathListACOBFSSP = new ArrayList<>();
		pathListACOBFSSPTG = new ArrayList<>();
		clearMap();
		initialize();
	}

	public void generateMap() { // GENERATE MAP
		clearMap(); // CREATE CLEAR MAP TO START
		for (int i = 0; i < m.getDensity(); i++) {
			Node current;
			do {
				int x = r.nextInt(columns);
				int y = r.nextInt(rows);
				current = map[x][y]; // FIND A RANDOM NODE IN THE GRID
			} while (current.getType() == 2); // IF IT IS ALREADY A WALL, FIND A NEW ONE
			current.setType(2); // SET NODE TO BE A WALL
			wallList.add(current);
		}
	}

	public void clearMap() { // CLEAR MAP
		m.setFinishX(-1); // RESET THE START AND FINISH
		m.setFinishY(-1);
		m.setStartX(-1);
		m.setStartY(-1);
		m.createNewMap(); // CREATE NEW MAP OF NODES
		copyACOPath.clear();
		copyACOGPath.clear();
		copyACOBFSPath.clear();
		copyACOBFSABPath.clear();
		copyACOBFSSPPath.clear();
		copyACOBFSSPTGPath.clear();
		this.map = m.getMap();
		for (int x = 0, no = 0; x < columns; x++) {
			for (int y = 0; y < rows; y++, no++) {
				map[x][y] = new Node(no, 3, x, y); // SET ALL NODES TO EMPTY
			}
		}
		wallList.clear();
		reset(); // RESET SOME VARIABLES

	}

	public void resetMap() { // RESET MAP
		for (int x = 0; x < columns; x++) {
			for (int y = 0; y < rows; y++) {
				Node current = map[x][y];
				if (current.getType() == 4 || current.getType() == 5) // CHECK TO SEE IF CURRENT NODE IS EITHER CHECKED
					// OR FINAL PATH
					// map[x][y] = new Node(3,x,y); //RESET IT TO AN EMPTY NODE
					map[x][y].setType(3); // RESET IT TO AN EMPTY NODE
			}
		}
		if (m.hasStartNode()) { // RESET THE START AND FINISH
			// m.getStartNode() = new Node(0,startx,starty);
			m.getStartNode().setType(0);
			m.getStartNode().setHops(0);
		}
		if (m.hasFinishNode())
			// m.getFinishNode() = new Node(1,finishx,finishy);
			m.getFinishNode().setType(1);
		reset(); // RESET SOME VARIABLES
	}

	private int getCenter(int coord) {
		return (coord * CSIZE) + (CSIZE / 2);
	}

	private int getAQuarter(int coord) {
		return (coord * CSIZE) + (CSIZE / 4);
	}

	private double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
	}

	private Component getParentComponent(ActionEvent e) {
		return ((Component) e.getSource()).getParent().getComponent(0);
	}

	/*
	 * NOT WORKING private void resizeFrame(int width, int height) {
	 * frame.setSize(width, height); frame.validate(); frame.repaint();
	 * frame.pack(); }
	 */


	private void setEnableJPanel(JPanel panel, boolean state) {
		Component[] components = panel.getComponents();
		for (Component component : components) {
			if (component instanceof JPanel)
				setEnableJPanel((JPanel) component, state);
			if (component instanceof JScrollPane) {
				JScrollPane pane = (JScrollPane) component;
				pane.getHorizontalScrollBar().setEnabled(state);
				pane.getVerticalScrollBar().setEnabled(state);
				pane.getViewport().getView().setEnabled(state);
			}
			component.setEnabled(state);
		}
	}


	private void setEnableWorkableComponents(boolean state) {
		setEnableJPanel(mapP, state);
		resetB.setEnabled(state);
		searchB.setEnabled(state);
		algorithmsBx.setEnabled(state);
		mapM.setEnabled(state);
		setEnableJPanel(pathListP, state);
	}

	private void initialize() { // INITIALIZE THE GUI ELEMENTS
		frame = new JFrame();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle("Path Finding");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		mapP.setBorder(BorderFactory.createTitledBorder(loweredetched, "Map"));
		ctrlsP.setBorder(BorderFactory.createTitledBorder(loweredetched, "Controls"));
		int space = 25;
		int buff = 42;

		BoxLayout boxLayout = new BoxLayout(toolP, BoxLayout.Y_AXIS);
		toolP.setLayout(boxLayout);
		// toolP.setLayout(null);
		toolP.setBounds(10, 0, 210, 580);

		// Panel Preferred Size
		menuBarP.setPreferredSize(new Dimension(210, 15));

		mapP.setLayout(null);
		mapP.setPreferredSize(new Dimension(210, 260));
		// mapP.setBounds(0, 0, 210, 360);

		ctrlsP.setLayout(null);
		ctrlsP.setPreferredSize(new Dimension(210, 235));
		// ctrlsP.setBounds(0, 360, 210, 240);

		// Tool Panel Menubar
		mapM.setMnemonic(KeyEvent.VK_M);
		mapMB.add(mapM);
		mapMB.add(creditMI);

		showMapNumCBMI.setSelected(true);
		showMapNumCBMI.setToolTipText("The number won't display if number of columns or rows > 20");
		mapM.add(showMapNumCBMI);

		customMapSizeMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		mapM.add(customMapSizeMI);
		menuBarP.add(mapMB);

		// Map Panel
		imExportP.setBounds(22, space, 155, 35);
		importB.setMnemonic(KeyEvent.VK_I);
		imExportP.add(importB);
		imExportP.add(exportB);
		mapP.add(imExportP);
		space += buff + 5;

		genMapB.setBounds(40, space, 120, 25);
		mapP.add(genMapB);
		space += buff;

		clearMapB.setBounds(40, space, 120, 25);
		mapP.add(clearMapB);
		space += buff - 10;

		toolL.setBounds(40, space, 120, 25);
		mapP.add(toolL);
		space += 25;

		toolBx.setBounds(40, space, 120, 25);
		mapP.add(toolBx);
		space += buff - 5;

		sizeL.setBounds(15, space, 40, 25);
		mapP.add(sizeL);
		size.setMajorTickSpacing(10);
		size.setBounds(50, space, 100, 25);
		mapP.add(size);
		cellsL.setBounds(160, space, 40, 25);
		mapP.add(cellsL);
		space += buff - 10;

		obstacleL.setBounds(15, space, 100, 25);
		mapP.add(obstacleL);
		obstacles.setToolTipText("Obstacles density (Generate Map function)");
		obstacles.setMajorTickSpacing(5);
		obstacles.setBounds(50, space, 100, 25);
		mapP.add(obstacles);
		densityL.setBounds(160, space, 100, 25);
		mapP.add(densityL);

		// Controls Panel
		space = 25;

		searchB.setBounds(40, space, 120, 25);
		ctrlsP.add(searchB);
		space += buff;

		resetB.setBounds(40, space, 120, 25);
		ctrlsP.add(resetB);
		space += buff - 10;

		algL.setBounds(40, space, 120, 25);
		ctrlsP.add(algL);
		space += 25;

		algorithmsBx.setBounds(40, space, 120, 25);
		ctrlsP.add(algorithmsBx);
		space += 40;

		delayL.setBounds(15, space, 50, 25);
		ctrlsP.add(delayL);
		speed.setMajorTickSpacing(5);
		speed.setBounds(50, space, 100, 25);
		ctrlsP.add(speed);
		msL.setBounds(160, space, 40, 25);
		ctrlsP.add(msL);
		space += buff - 10;

		timeL.setBounds(15, space, 180, 15);
		ctrlsP.add(timeL);
		space += buff - 10;

		lengthL.setBounds(15, space, 180, 15);
		ctrlsP.add(lengthL);
		space += buff - 10;

		toolP.add(menuBarP);
		toolP.add(mapP);
		toolP.add(Box.createVerticalStrut(5));
		toolP.add(ctrlsP);

		frame.getContentPane().add(toolP);

		// Canvas
		canvas = new Canvas();
		canvas.setBounds(230, 5, MSIZE + 1, MSIZE + 1);
		frame.getContentPane().add(canvas);

		// Tabbed Pane
		JPanel rightP = new JPanel(new BorderLayout());
		rightP.setBounds(845, 5, 275, MSIZE);
		// Text Panel
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		resultP.add(new JScrollPane(textArea));
		tabbedPane.addTab("Process/Result", resultP);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		// Path List Panel
		tableL.setBorder(new EmptyBorder(4, 4, 4, 4));
		pathListP.add(tableL, BorderLayout.PAGE_START);
		pathListP.add(new JScrollPane(pathT), BorderLayout.CENTER);
		tabbedPane.addTab("Path list", pathListP);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		tabbedPane.setSelectedIndex(0);
		rightP.add(tabbedPane, BorderLayout.CENTER);

		frame.getContentPane().add(rightP);

		// ACTION LISTENERS
		showMapNumCBMI.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				canvas.repaint();
			}
		});
		customMapSizeMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialogCustomMapSize dialog = new JDialogCustomMapSize();
				if (dialog.show() == JOptionPane.OK_OPTION) {
					m.setMapSize(dialog.getColumns(), dialog.getRows());
					m.createNewMap();
					columns = m.getColumns();
					rows = m.getRows();
					map = m.getMap();
					clearMap();
					Update();
				}
			}
		});
		importB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooserImportMap dialog = new JFileChooserImportMap(getParentComponent(e), m);
				dialog.show();

				if (dialog.getResponse() == JFileChooser.CANCEL_OPTION)
					return;

				clearMap();
				m.setMap(dialog.getResult());
				map = m.getMap();
				columns = m.getColumns();
				rows = m.getRows();
				for (int x = 0; x < columns; x++) {
					for (int y = 0; y < rows; y++) {
						if (map[x][y].getType() == 2)
							wallList.add(map[x][y]);
					}
				}
				Update();
			}
		});
		exportB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooserExportMap dialog = new JFileChooserExportMap(getParentComponent(e), m);
				dialog.show();
			}
		});
		genMapB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateMap();
				Update();
			}
		});
		clearMapB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearMap();
				Update();
			}
		});
		toolBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				tool = toolBx.getSelectedIndex();
			}
		});
		size.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				m.setMapSize(size.getValue() * 10, size.getValue() * 10);
				// m.setCells(size.getValue());
				columns = m.getColumns();
				rows = m.getRows();
				clearMap();
				Update();
			}
		});
		obstacles.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				m.setDense((double) obstacles.getValue() / 100);
				Update();
			}
		});
		searchB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				flag = false;
				reset();
				if (m.hasStartNode() && m.hasFinishNode())
					solving = true;
			}
		});
		resetB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetMap();
				Update();
			}
		});
		algorithmsBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				curAlg = algorithmsBx.getSelectedIndex();
				Update();
			}
		});
		speed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				delay = speed.getValue();
				Update();
			}
		});
		pathT.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ArrayList<Node> path = pathT.getSelectedPath();
				//				pathList.clear();
				if (path != null) {
					if (algorithmsBx.getSelectedItem().equals("ACO")) {
						pathListACO.clear();
						pathListACO.add(path);
					}
//					else if (algorithmsBx.getSelectedItem().equals("ACO-BFS-AB")) {
//						pathListACOBFSAB.clear();
//						pathListACOBFSAB.add(path);
//					}
					else if (algorithmsBx.getSelectedItem().equals("ACO-BFS-SP")) {
						pathListACOBFSSP.clear();
						pathListACOBFSSP.add(path);
					}
					else if (algorithmsBx.getSelectedItem().equals("ACO-BFS-SP-N")) {
						pathListACOBFSSPTG.clear();
						pathListACOBFSSPTG.add(path);
					}
//					else if (algorithmsBx.getSelectedItem().equals("ACO-G")) {
//						pathListACOG.clear();
//						pathListACOG.add(path);
//					}
					else {
						pathListACOBFS.clear();
						pathListACOBFS.add(path);
					}
				}
				canvas.repaint();
			}
		});
		creditMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame,
						"	                         Pathfinding\n" + "             Copyright (c) 2017-2018\n"
								+ "                         Greer Viau\n" + "          Build Date:  March 28, 2018   ",
								"Credit", JOptionPane.PLAIN_MESSAGE, new ImageIcon(""));
			}
		});

		startSearch(); // START STATE
	}

	public void startSearch() { // START STATE
		if (solving) {
			switch (curAlg) {
			case 0:
				Alg.ACO();
				break;
			case 1:
				Alg.ACOBFS();
				break;
			case 2:
				Alg.ACOBFSSP();
				break;
			case 3:
				Alg.ACOBFSSPTG();
				break;
			}
		}
		pause(); // PAUSE STATE
	}

	public void pause() { // PAUSE STATE
		int i = 0;
		while (!solving) {
			i++;
			if (i > 500)
				i = 0;
			try {
				Thread.sleep(1);
			} catch (Exception e) {
			}
		}
		startSearch(); // START STATE
	}

	public void Update() { // UPDATE ELEMENTS OF THE GUI
		// auto update capacity when called func setCells
		m.updateDensity();
		CSIZE = MSIZE / (columns > rows ? columns : rows);
		canvas.repaint();
		cellsL.setText(columns + "x" + rows);
		msL.setText(delay + "ms");
		timeL.setText("Time planning: " + time);
		lengthL.setText("Path length: " + length);
		densityL.setText(obstacles.getValue() + "%");
		checkL.setText("Checks: " + checks);
	}

	public void updateWallList(Node node) {
		if (node.getType() == 2)
			wallList.add(node);
		else if (node.getType() == 3)
			wallList.remove(node);
	}

	public void reset() { // RESET METHOD
		solving = false;
		time = 0;
		length = 0;
		checks = 0;
		
		if (algorithmsBx.getSelectedItem().equals("ACO")) {
			pathListACO.clear();
		}
		if (algorithmsBx.getSelectedItem().equals("ACO-BFS")) {
			pathListACOBFS.clear();
		}
//		if (algorithmsBx.getSelectedItem().equals("ACO-BFS-AB")) {
//			pathListACOBFSAB.clear();
//		}
		if (algorithmsBx.getSelectedItem().equals("ACO-BFS-SP")) {
			pathListACOBFSSP.clear();
		}
		if (algorithmsBx.getSelectedItem().equals("ACO-BFS-SP-N")) {
			pathListACOBFSSPTG.clear();
		}
//		if (algorithmsBx.getSelectedItem().equals("ACO-G")) {
//			pathListACOG.clear();
//		}
		if (flag == true) {
			textArea.setText(null);
			pathListACO.clear();
			pathListACOBFSAB.clear();
			pathListACOBFS.clear();
			pathListACOBFSSP.clear();
			pathListACOBFSSPTG.clear();
			pathListACOG.clear();

			copyACOPath.clear();
			copyACOBFSPath.clear();
			copyACOBFSABPath.clear();
			copyACOBFSSPPath.clear();
			copyACOBFSSPTGPath.clear();
			copyACOGPath.clear();
		}
		flag = true;
		pathT.clearData();
	}

	public void delay() { // DELAY METHOD
		try {
			Thread.sleep(delay);
		} catch (Exception e) {
		}
	}

	class Canvas extends JPanel implements MouseListener, MouseMotionListener { // MAP CLASS
		private static final long serialVersionUID = 3006316467662386292L;

		public Canvas() {
			addMouseListener(this);
			addMouseMotionListener(this);
		}

		public void paintComponent(Graphics g) { // REPAINT
			super.paintComponent(g);
			for (int x = 0; x < columns; x++) { // PAINT EACH NODE IN THE GRID
				for (int y = 0; y < rows; y++) {
					switch (map[x][y].getType()) {
					case 0:
						g.setColor(Color.GREEN);
						break;
					case 1:
						g.setColor(Color.RED);
						break;
					case 2:
						g.setColor(Color.BLACK);
						break;
					case 3:
						g.setColor(Color.WHITE);
						break;
					case 4:
						g.setColor(Color.CYAN);
						break;
					case 5:
						g.setColor(Color.YELLOW);
						break;
					}
					g.fillRect(x * CSIZE, y * CSIZE, CSIZE, CSIZE);
					g.setColor(Color.BLACK);
					g.drawRect(x * CSIZE, y * CSIZE, CSIZE, CSIZE);

					if (showMapNumCBMI.isSelected() && columns <= 20 && rows <= 20) //HienDTk: ve trong so len ma tran
						g.drawString(String.valueOf(map[x][y].getNo() + 1), getAQuarter(x), getCenter(y));
//						g.drawString(String.valueOf(map[x][y].getNo() + "[" + map[x][y].getX() + "]" + "[" + map[x][y].getY() + "]"), getAQuarter(x), getCenter(y));
					
					// DEBUG STUFF
					/*
					 * if(curAlg == 1) g.drawString( map[x][y].getHops() + "/" +
					 * map[x][y].getEuclidDist(m.getFinishX(), m.getFinishY()),
					 * (x*CSIZE)+(CSIZE/2)-10, (y*CSIZE)+(CSIZE/2) ); else g.drawString("" +
					 * map[x][y].getHops(), (x*CSIZE)+(CSIZE/2), (y*CSIZE)+(CSIZE/2));
					 */

				}
			}

			g.setColor(Color.MAGENTA);
			drawPath(pathListACO, g);

			g.setColor(Color.GRAY);
			drawPath(pathListACOBFS, g);

//			g.setColor(Color.BLUE);
//			drawPath(pathListACOBFSAB, g);

			g.setColor(Color.CYAN);
			drawPath(pathListACOBFSSP, g);
			
//			g.setColor(Color.PINK);
//			drawPath(pathListACOG, g);
			
			g.setColor(Color.BLUE);
			drawPath(pathListACOBFSSPTG, g);
			
		}

		public void drawPath(ArrayList<ArrayList<Node>> pathList, Graphics g) {
			for (ArrayList<Node> path : pathList) {
				g.drawOval(getCenter(path.get(0).getX()) - 3, getCenter(path.get(0).getY()) - 3, 6, 6);
				g.fillOval(getCenter(path.get(0).getX()) - 3, getCenter(path.get(0).getY()) - 3, 6, 6);
				for (int i = 0; i < path.size() - 1; i++) {
					Node curr = path.get(i), next = path.get(i + 1);
					g.drawLine(getCenter(curr.getX()), getCenter(curr.getY()), getCenter(next.getX()),
							getCenter(next.getY()));
					g.drawOval(getCenter(next.getX()) - 3, getCenter(next.getY()) - 3, 6, 6);
					g.fillOval(getCenter(next.getX()) - 3, getCenter(next.getY()) - 3, 6, 6);
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			try {
				int x = e.getX() / CSIZE;
				int y = e.getY() / CSIZE;
				Node current = map[x][y];
				if ((tool == 2 || tool == 3) && (current.getType() != 0 && current.getType() != 1))
					current.setType(tool);
				Update();
				updateWallList(current);
			} catch (Exception z) {
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			resetMap(); // RESET THE MAP WHENEVER CLICKED
			try {
				int x = e.getX() / CSIZE; // GET THE X AND Y OF THE MOUSE CLICK IN RELATION TO THE SIZE OF THE GRID
				int y = e.getY() / CSIZE;
				Node current = map[x][y];
				switch (tool) {
				case 0: { // START NODE
					if (current.getType() != 2) { // IF NOT WALL
						if (m.hasStartNode()) { // IF START EXISTS SET IT TO EMPTY
							m.getStartNode().setType(3);
							m.getStartNode().setHops(-1);
						}
						current.setHops(0);
						m.setStartX(x); // SET THE START X AND Y
						m.setStartY(y);
						current.setType(0); // SET THE NODE CLICKED TO BE START
					}
					break;
				}
				case 1: {// FINISH NODE
					if (current.getType() != 2) { // IF NOT WALL
						if (m.hasFinishNode()) // IF FINISH EXISTS SET IT TO EMPTY
							m.getFinishNode().setType(3);
						m.setFinishX(x); // SET THE FINISH X AND Y
						m.setFinishY(y);
						current.setType(1); // SET THE NODE CLICKED TO BE FINISH
					}
					break;
				}
				default:
					if (current.getType() != 0 && current.getType() != 1)
						current.setType(tool);
					updateWallList(current);
					break;
				}
				Update();
			} catch (Exception z) {
			} // EXCEPTION HANDLER
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	class Algorithm { // ALGORITHM CLASS
		ArrayList<Node> copyPath = new ArrayList<>();
		public void findSimilarities(Node n0, ArrayList<Node> copyPath1) {
			for (int j = 1; j < copyPath1.size(); j++) {
				Node n1 = copyPath1.get(j);
				if (map[n0.getX()][n0.getY()].getNo() == map[n1.getX()][n1.getY()].getNo()) {
					copyPath.add(n0);
					continue;
				}
			}
		}

		public void clearPath(ArrayList<Node> copyPath0, ArrayList<Node> copyPath1, ArrayList<Node> copyPath2, ArrayList<Node> copyPath3, ArrayList<Node> copyPath4, ArrayList<Node> copyPath5) {
			if (copyPath0.size() > 0) {
				for (int i = 1; i < copyPath0.size(); i++) {
					Node n0 = copyPath0.get(i);
					if (n0.getX() == m.getFinishNode().getArrayCoords()[0] && n0.getY() == m.getFinishNode().getArrayCoords()[1]) {
						map[n0.getX()][n0.getY()].setType(1);
					}
					else {
						map[n0.getX()][n0.getY()].setType(3);
						findSimilarities(n0, copyPath1);
						findSimilarities(n0, copyPath2);
						findSimilarities(n0, copyPath3);
						findSimilarities(n0, copyPath4);
						findSimilarities(n0, copyPath5);
					}
				}

				for (int i = 0; i < copyPath.size(); i++) {
					map[copyPath.get(i).getX()][copyPath.get(i).getY()].setType(5);
				}
			}
		}

		public void ACO() {
			ACO aco = new ACO();
			ArrayList<Node> result = new ArrayList<>();

			aco.setLabelMatrix(m.getArrayIntMap());
			aco.start = m.getStartNode().getArrayCoords();
			aco.end = m.getFinishNode().getArrayCoords();
			aco.mWall = wallList;

			copyPath.clear();
			clearPath(copyACOPath, copyACOBFSPath, copyACOBFSABPath, copyACOBFSSPPath, copyACOGPath, copyACOBFSSPTGPath);
			
			long millis = System.currentTimeMillis();
			aco.main();
			time = System.currentTimeMillis() - millis;
			
			if (aco.finalPath.size() >  1) {
				length = aco.length;
//				System.out.println("\nBest path: size: " + aco.finalPath.size() + ":");
//				for(int i = 0; i < aco.finalPath.size(); i++ ) {
//					System.out.println("--> x = " + aco.finalPath.get(i)[0] + ", y = " + aco.finalPath.get(i)[1]);
//				}
//				aco.printPheromone();

				copyACOPath = result = aco.getPath_list();
				pathListACO.add(result);
				ArrayList<Integer> labelResult = new ArrayList<>();

				for (int i = 1; i < result.size(); i++) {
					Node n = result.get(i);
					labelResult.add(map[n.getX()][n.getY()].getNo());
					if (n.getX() == aco.end[0] && n.getY() == aco.end[1]) {
						map[n.getX()][n.getY()].setType(1);
					}
					else {
						map[n.getX()][n.getY()].setType(5);
					}
				}
				
				String s = "";
				for (int i = 0; i < labelResult.size(); i++) {
					if (i == 0) {
						s = s + labelResult.get(i);
					}
					else {
						s = s + " -> " + labelResult.get(i);
					}
				}
//				textArea.append("ACO: \n" + "\nLength: " + Double.parseDouble(new DecimalFormat("##.####").format(length)) + "\nTime: " + time + "\n--------------------------------------------------------------\n");
				Update();
				solving = false;

				System.out.println("Time ACO: " + time);
				System.out.println("Length ACO: " + length);
			}
			else {
				solving = false;
				JOptionPane.showMessageDialog(frame, "Can't find path", "Inane error", JOptionPane.ERROR_MESSAGE);
			}
		}

		public void ACOBFS() {
			ACOBFS acobfs = new ACOBFS();
			ArrayList<Node> result = new ArrayList<>();

			acobfs.setLabelMatrix(m.getArrayIntMap());
			acobfs.start = m.getStartNode().getArrayCoords();
			acobfs.end = m.getFinishNode().getArrayCoords();
			acobfs.mWall = wallList;

			copyPath.clear();
			clearPath(copyACOBFSPath, copyACOPath, copyACOBFSABPath, copyACOBFSSPPath, copyACOBFSSPTGPath, copyACOGPath);
			
			long millis = System.currentTimeMillis();
			acobfs.main();
			time = System.currentTimeMillis() - millis;
			
			if (acobfs.finalPath.size() >  1) {
				length = acobfs.length;
//				System.out.println("\nBest path: size: " + acobfs.finalPath.size() + ":");
//				for(int i = 0; i < acobfs.finalPath.size(); i++ ) {
//					System.out.println("--> x = " + acobfs.finalPath.get(i)[0] + ", y = " + acobfs.finalPath.get(i)[1]);
//				}
//				acobfs.printPheromone();

				copyACOBFSPath = result = acobfs.getPath_list();
				pathListACOBFS.add(result);
				ArrayList<Integer> labelResult = new ArrayList<>();

				for (int i = 1; i < result.size(); i++) {
					Node n = result.get(i);
					labelResult.add(map[n.getX()][n.getY()].getNo());
					if (n.getX() == acobfs.end[0] && n.getY() == acobfs.end[1]) {
						map[n.getX()][n.getY()].setType(1);
					}
					else {
						map[n.getX()][n.getY()].setType(5);
					}
				}
				
				String s = "";
				for (int i = 0; i < labelResult.size(); i++) {
					if (i == 0) {
						s = s + labelResult.get(i);
					}
					else {
						s = s + " -> " + labelResult.get(i);
					}
				}
//				textArea.append("ACO-BFS: \n" + "\nLength: " + Double.parseDouble(new DecimalFormat("##.####").format(length)) + "\nTime: " + time + "\n--------------------------------------------------------------\n");
				Update();
				solving = false;

				System.out.println("Time ACO-BFS: " + time);
				System.out.println("Length ACO-BFS: " + length);
			}
			else {
				solving = false;
				JOptionPane.showMessageDialog(frame, "Can't plan path", "Inane error", JOptionPane.ERROR_MESSAGE);
			}
		}

		public void ACOBFSSP() {
			ACOBFSSP acobfssp = new ACOBFSSP();
			ArrayList<Node> result = new ArrayList<>();

			acobfssp.setLabelMatrix(m.getArrayIntMap());
			acobfssp.start = m.getStartNode().getArrayCoords();
			acobfssp.end = m.getFinishNode().getArrayCoords();
			acobfssp.mWall = wallList;

			copyPath.clear();
			clearPath(copyACOBFSSPPath, copyACOPath, copyACOBFSPath, copyACOBFSABPath, copyACOBFSSPTGPath, copyACOGPath);
			
			long millis = System.currentTimeMillis();
			acobfssp.main();
			time = System.currentTimeMillis() - millis;
			
			if (acobfssp.finalPath.size() >  1) {
				length = acobfssp.length;
//				System.out.println("\nBest path: size: " + acobfssp.finalPath.size() + ":");
//				for(int i = 0; i < acobfssp.finalPath.size(); i++ ) {
//					System.out.println("--> x = " + acobfssp.finalPath.get(i)[0] + ", y = " + acobfssp.finalPath.get(i)[1]);
//				}
//				acobfssp.printPheromone();

				copyACOBFSSPPath = result = acobfssp.getPath_list();
				pathListACOBFSSP.add(result);
				ArrayList<Integer> labelResult = new ArrayList<>();

				for (int i = 1; i < result.size(); i++) {
					Node n = result.get(i);
					if (n.getX() == acobfssp.end[0] && n.getY() == acobfssp.end[1]) {
						map[n.getX()][n.getY()].setType(1);
					}
					else {
						map[n.getX()][n.getY()].setType(5);
					}
				}
				
				String s = "";
				for (int i = 0; i < labelResult.size(); i++) {
					if (i == 0) {
						s = s + labelResult.get(i);
					}
					else {
						s = s + " -> " + labelResult.get(i);
					}
				}
//				textArea.append("ACO-BFS-SP: \n" + "\nLength: " + Double.parseDouble(new DecimalFormat("##.####").format(length)) + "\nTime: " + time + "\n--------------------------------------------------------------\n");
				Update();
				solving = false;

				System.out.println("Time ACO-BFS-SP: " + time);
				System.out.println("Length ACO-BFS-SP: " + length);
			}
			else {
				solving = false;
				JOptionPane.showMessageDialog(frame, "Can't plan path", "Inane error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		public void ACOBFSSPTG() {
			ACOBFSSPTG acobfssptg = new ACOBFSSPTG();
			ArrayList<Node> result = new ArrayList<>();

			acobfssptg.setLabelMatrix(m.getArrayIntMap());
			acobfssptg.start = m.getStartNode().getArrayCoords();
			acobfssptg.end = m.getFinishNode().getArrayCoords();
			acobfssptg.mWall = wallList;

			copyPath.clear();
			clearPath(copyACOBFSSPTGPath, copyACOPath, copyACOBFSPath, copyACOBFSABPath, copyACOBFSSPPath, copyACOGPath);
			
			long millis = System.currentTimeMillis();
			acobfssptg.main();
			time = System.currentTimeMillis() - millis;
			
			if (acobfssptg.finalPath.size() >  1) {
				length = acobfssptg.length;
//				System.out.println("\nBest path: size: " + acobfssptg.finalPath.size() + ":");
//				for(int i = 0; i < acobfssp.finalPath.size(); i++ ) {
//					System.out.println("--> x = " + acobfssp.finalPath.get(i)[0] + ", y = " + acobfssp.finalPath.get(i)[1]);
//				}
//				acobfssp.printPheromone();

				copyACOBFSSPTGPath = result = acobfssptg.getPath_list();
				pathListACOBFSSPTG.add(result);
				ArrayList<Integer> labelResult = new ArrayList<>();

				for (int i = 1; i < result.size(); i++) {
					Node n = result.get(i);
					if (n.getX() == acobfssptg.end[0] && n.getY() == acobfssptg.end[1]) {
						map[n.getX()][n.getY()].setType(1);
					}
					else {
						map[n.getX()][n.getY()].setType(5);
					}
				}
				
				String s = "";
				for (int i = 0; i < labelResult.size(); i++) {
					if (i == 0) {
						s = s + labelResult.get(i);
					}
					else {
						s = s + " -> " + labelResult.get(i);
					}
				}
//				textArea.append("ACO-BFS-SP-N: \n" + "\nLength: " + Double.parseDouble(new DecimalFormat("##.####").format(length)) + "\nTime: " + time + "\n--------------------------------------------------------------\n");
				Update();
				solving = false;

				System.out.println("Time ACO-BFS-SP-N: " + time);
				System.out.println("Length ACO-BFS-SP-N: " + length);
			}
			else {
				solving = false;
				JOptionPane.showMessageDialog(frame, "Can't plan path", "Inane error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}