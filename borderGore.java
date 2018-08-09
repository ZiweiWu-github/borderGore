package borderGore;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The borderGore class is the main class
 * @author James Wu
 *
 */
public class borderGore {
	private static Rectangles rr;
	private static Color[] colors = {Color.BLACK, Color.CYAN, Color.DARK_GRAY, Color.GRAY,
			Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
			Color.WHITE, Color.YELLOW, Color.BLUE};
	private static int width, height;
	private static boolean changeWater = true;
	public static void main(String[] args) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		
		//helper popup
		JFrame pop = new JFrame("Map Creation");
		pop.setPreferredSize(new Dimension(800,200));
		pop.setLayout(new GridLayout(2,1));
		
		//Jpanel for text inputs
		JPanel popPanel = new JPanel();
		popPanel.setLayout(new GridLayout(2,4));
		
		//JLabels
		JLabel cleanRateLabel = new JLabel("Number of times to clean");
		popPanel.add(cleanRateLabel);
		JLabel moveRateLabel = new JLabel("Number of random movements");
		popPanel.add(moveRateLabel);
		JLabel numCountriesLabel = new JLabel("Number of draws");
		popPanel.add(numCountriesLabel);
		JLabel changeWaterLabel = new JLabel("Change water? (y/n)");
		popPanel.add(changeWaterLabel);
		
		//The TextFields for inputs
		JTextField cleanUpRateField = new JTextField("4");
		popPanel.add(cleanUpRateField);
		JTextField moveRateField = new JTextField(Integer.toString((height - 50) / 20 * width / 20));
		popPanel.add(moveRateField);
		JTextField numCountriesField = new JTextField(Integer.toString(getRandomNumberInRange(colors.length, colors.length * 2)));
		popPanel.add(numCountriesField);
		JTextField changeWaterField = new JTextField("y");
		popPanel.add(changeWaterField);
		
		//Button to take in the inputs and creates map using them
		JButton go = new JButton("Go!");
		go.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int cleanNum = Integer.parseInt(cleanUpRateField.getText());
				int moveNum = Integer.parseInt(moveRateField.getText());
				int countries =Integer.parseInt(numCountriesField.getText());;
				String changeWaterS = changeWaterField.getText();
				changeWater = true;
				if(changeWaterS.toLowerCase().equals("n")) {
					changeWater = false;
				}
				rr = createRectangle(width, height, moveNum, cleanNum, countries, changeWater);
				JFrame frame = new JFrame("map");
				frame.add(rr);
				frame.pack();
				frame.setVisible(true);
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				frame.addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent arg0) {}

					@Override
					public void mouseEntered(MouseEvent arg0) {}

					@Override
					public void mouseExited(MouseEvent arg0) {}

					@Override
					public void mousePressed(MouseEvent arg0) {
						frame.remove(rr);
						rr = createRectangle(width, height, moveNum, cleanNum, countries, changeWater);
						frame.add(rr);
						frame.revalidate();
					}
					@Override
					public void mouseReleased(MouseEvent arg0) {}
					
				});
				
			}
			
		});
		
		pop.add(popPanel);
		pop.add(go);
		pop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pop.pack();
		pop.setVisible(true);
		
	}
	
	public static Rectangles createRectangle(int width, int height, int count, 
			int cleanNumber, int numCountries, boolean changeWater) {
		String[][] map;
		Map<String, Color> mapColoring = new HashMap<String, Color>();
		map = new String[(height - 50) / 20][width / 20];
		addWater(map);
		mapColoring.put("W", Color.BLUE);
		
		
		//int count = map.length * map[0].length ;
		for(int i =0; i<numCountries; i++){
			addCountry(map, Integer.toString(i),count);
			mapColoring.put(Integer.toString(i), colors[i%colors.length]);
		}
		
		//int cleanNumber =4;
		for(int i =0; i< cleanNumber; i++) {
			cleanUpNearestNeighbor(map, changeWater);
		}
		
		Rectangles r = new Rectangles(map, mapColoring);
		return r;
	}
	
	/**
	 * Completely fills up the 2D String array with "water"
	 * @param m a 2D array of Strings of any size
	 */
	public static void addWater(String[][] m){
		for(int i =0; i<m.length; i++){
			for(int j =0; j<m[0].length; j++){
				m[i][j] = "W";
			}
		}
	}
	
	/**
	 * This method takes in an 2D array of Strings m and a String border
	 * The 2D array is then filled with the String using a random walk algorithm
	 * @param m the 2D array of Strings
	 * @param border a String
	 */
	public static void addCountry(String[][] m, String border, int count) {
		int ii = getRandomNumberInRange(0, m.length - 1);
		int jj = getRandomNumberInRange(0, m[0].length - 1);
		m[ii][jj] = border;
		
		while (count >= 0) {
			int k = getRandomNumberInRange(1, 4);
			if (k == 1)
				ii--;
			else if (k == 2)
				jj++;
			else if (k == 3)
				ii++;
			else if (k == 4)
				jj--;
			if (ii < 0)
				ii = 0;
			if (ii >= m.length)
				ii = m.length - 1;
			if (jj < 0)
				jj = 0;
			if (jj >= m[0].length)
				jj = m[0].length - 1;
			m[ii][jj] = border;
			count--;
		}
	}

	/**
	 * This method cleans up a 2D array of Strings by eliminating some clutter
	 * using a modified nearest neighbor classification algorithm
	 * @param m a 2D array of strings
	 */
	public static void cleanUpNearestNeighbor(String[][] m, boolean changeWater){
		for(int i = 0; i<m.length; i++) {
			for(int j = 0; j<m[0].length; j++) {
				
				if(!changeWater) {
					//don't change the water tiles
					if(m[i][j].equals("W") || m[i][j].equals("12")) {
						continue; 
					}
				}
				
				//set is to keep track of any individual Strings without repeating
				Set<String> colorSet = new HashSet<String>();
				
				//ArrayList is to keep track of all 
				ArrayList<String> stringArr = new ArrayList<String>();
				
				
				// itself -- used as a tiebreaker and to prevent a single
				// color from dominating the entire map
				String curString = m[i][j];
					stringArr.add(curString); 
					colorSet.add(curString);
				
				//up
				if(i!=0) {
					curString = m[i-1][j];
					stringArr.add(curString); 
					colorSet.add(curString);
				}
				
				//down
				if(i != m.length-1) {
					curString =  m[i+1][j];
					stringArr.add(curString);
					colorSet.add(curString);
				}
					
				//left
				if(j !=0) {
					curString =  m[i][j-1];
					stringArr.add(curString);
					colorSet.add(curString);
				}
					
				//right
				if(j != m[0].length-1) {
					curString =  m[i][j+1];
					stringArr.add(curString);
					colorSet.add(curString);
				}
					
				int biggestElementLocation = 0;
				String[] colorSetArr = colorSet.toArray(new String[0]);
				int[] colorCounts = new int[colorSetArr.length];
				
				//fill an array with the amount of times each String shows up
				for(int ii =0; ii<colorSetArr.length; ii++) {
					colorCounts[ii] = getNumCountElements(colorSetArr[ii], stringArr);
				}
				
				//get the location of the String that shows up the most
				for(int ii =0; ii<colorCounts.length; ii++) {
					if(colorCounts[ii] > colorCounts[biggestElementLocation]) {
						biggestElementLocation = ii;
					}
				}
				
				//go with the majority
				m[i][j] = colorSetArr[biggestElementLocation];
			}
		}
	}
	
	/**
	 * This method counts the number of a given String in an ArrayList
	 * @param s a String
	 * @param ss an ArrayList
	 * @return the number of times String s shows up in ArrayList ss
	 */
	public static int getNumCountElements(String s, ArrayList<String> ss) {
		int count = 0;
		for(String sss: ss) {
			if(sss.equals(s)) count++;
		}
		return count;
	}
	
	/**
	 * This methods returns an int between the min inclusive and max inclusive
	 * @param min the minimum int
	 * @param max the maximum int
	 * @return a random number between the min inclusive and max inclusive
	 */
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}

/**
 * The Rectangles class extends JPanel and is used to draw the map
 * @author James Wu
 *
 */
class Rectangles extends JPanel {
	private static final long serialVersionUID = 173439344614863889L;
	private String[][] map;
	private Map<String, Color> mColor;

	/**
	 * The constructor of the class
	 * @param m a 2D array of strings
	 * @param mColor a Map of the Strings to their corresponding Color
	 */
	public Rectangles(String[][] m, Map<String, Color> mColor) {
		this.map = m;
		this.mColor = mColor;
	}

	public Dimension getPreferredSize() {
		return new Dimension(map[0].length * 20, map.length * 20);
	}

	/**
	 * This goes through the 2D array of Strings and Map from Strings to Colors
	 * Using the Colors, it draws rectangles filled with the Color
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				String s = map[i][j];
				Color c = mColor.get(s);
				g2d.setColor(c);
				g2d.fillRect(j * 20, i * 20, 20, 20);
			}
		}

	}
}