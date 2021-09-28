import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TetrisClone extends JPanel {

	private final Point[][][] Tetraminos = {
			// I-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
			},
			
			// J-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
			},
			
			// L-Piece
			{
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
				{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) }
			},
			
			// O-Piece
			{
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
			},
			
			// S-Piece
			{
				{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
				{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
				{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
			},
			
			// T-Piece
			{
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
				{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
				{ new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) }
			},
			
			// Z-Piece
			{
				{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
				{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
				{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
			}
	};
	
	private final Color[] tetraminoColors = {Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.PINK, Color.RED};
	private Point pieceOrigin;
	private int currentPiece;
	private int rotation;
	private ArrayList<Integer> nextPieces = new ArrayList<Integer>();
	private long score = 200;
	private Color[][] well;
	
	// Creates a border around the well and initializes the dropping piece
	private void init() {
		well = new Color[12][24];
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 23; j++) {
				if (i == 0 || i == 11 || j == 22) {
					well[i][j] = Color.GRAY;
				} 
				else {
					well[i][j] = Color.BLACK;
				}
			}
		}
		newPiece();
	}
	
	// Put a new, random piece into the dropping position
	public void newPiece() {
		pieceOrigin = new Point(5, 1);
		rotation = 0;
		if (nextPieces.isEmpty()) {
			Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
			Collections.shuffle(nextPieces);
		}
		currentPiece = nextPieces.get(0);
		nextPieces.remove(0);
		
	}
	
	// Collision test for the dropping piece
	private boolean collidesAt(int x, int y, int rotation) {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			if (well[p.x + x][p.y + y] != Color.BLACK) {
				return true;
			}
		}
		return false;
	}
	
	//Checks to see if a piece collides with the top most row, and returns a true if a piece does.
	// If gameOver() is true, the the Thread method will end the game. 
	public boolean gameOver() {
		if (collidesAt(pieceOrigin.x, pieceOrigin.y - 1, rotation)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// Rotate the piece clockwise or counterclockwise
	public void rotate(int i) {
		int newRotation = (rotation + i) % 4;
		if (newRotation < 0) {
			newRotation = 3;
		}
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
			rotation = newRotation;
		}
		repaint();
	}
	
	// Move the piece left or right
	public void move(int i) {
		if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
			pieceOrigin.x += i;	
		}
		repaint();
	}
	
	// Drops the piece one line or fixes it to the well if it can't drop
	public void dropDown() {
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			pieceOrigin.y += 1;
		} else {
			fixToWell();
		}	
		repaint();
	}
	
	
	// Drops the piece straight down and fixes it to the well
	public void dropStraightDown() {
		while (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			pieceOrigin.y += 1;
		}
		fixToWell();
		repaint();
	}
	
	// Make the dropping piece part of the well, so it is available for
	// collision detection.
	public void fixToWell() {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
		}
		clearRows();
		newPiece();
	}
	
	public void deleteRow(int row) {
		for (int j = row-1; j > 0; j--) {
			for (int i = 1; i < 11; i++) {
				well[i][j+1] = well[i][j];
			}
		}
	}
	
	// Clear completed rows from the field and award score according to
	// the number of simultaneously cleared rows.
	public void clearRows() {
		boolean gap;
		int numClears = 0;
		
		for (int j = 21; j > 0; j--) {
			gap = false;
			for (int i = 1; i < 11; i++) {
				if (well[i][j] == Color.BLACK) {
					gap = true;
					break;
				}
			}
			if (!gap) {
				deleteRow(j);
				j += 1;
				numClears += 1;
			}
		}
		
		switch (numClears) {
		case 1:
			score += 100;
			break;
		case 2:
			score += 300;
			break;
		case 3:
			score += 500;
			break;
		case 4:
			score += 800;
			break;
		}
	}
	
	// Draw the falling piece
	private void drawPiece(Graphics g) {		
		g.setColor(tetraminoColors[currentPiece]);
		for (Point p : Tetraminos[currentPiece][rotation]) {
			g.drawRect((p.x + pieceOrigin.x) * 26, 
					   (p.y + pieceOrigin.y) * 26, 
					   25, 25);
		}
	}
	
	@Override 
	public void paintComponent(Graphics g) {
		// Paint the well
		g.fillRect(0, 0, 26*12, 26*23);
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 23; j++) {
				g.setColor(well[i][j]);
				g.fillRect(26*i, 26*j, 25, 25);
			}
		}
		
		// Display the score
		if (score <= 0) {
			g.setColor(Color.RED);
			g.drawString("SCORE: " + score, 19*11, 26);
			
		}
		else {
			g.setColor(Color.WHITE);
			g.drawString("SCORE: " + score, 19*11, 26);
		}
		
		// Display a game over
		if (gameOver() == true) {
			g.setColor(Color.RED);
			g.drawString("GAME OVER", 25, 25);
		}
		// Draw the currently falling piece
		drawPiece(g);
	}
	
	public void music(String filepath) {
		try {
			File filePath = new File(filepath);
			if (filePath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(filePath);
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.start();
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
			else {
				System.out.println("File not found");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Tetris");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(12*26+15, 26*23+39);
		f.setVisible(true);
		
		final TetrisClone game = new TetrisClone();
		game.init();
		f.add(game);
		
		// Keyboard controls
		f.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_Z:
					if (game.score > 0) {
						game.score -= 75;
						game.rotate(-1);
						break;
					}
					else {
						break;
					}
				case KeyEvent.VK_X:
					if (game.score > 0) {
						game.rotate(+1);
						game.score -= 75;
						break;
					}
					else {
						break;
					}
				case KeyEvent.VK_LEFT:
					game.move(-1);
					break;
				case KeyEvent.VK_RIGHT:
					game.move(+1);
					break;
				case KeyEvent.VK_DOWN:
					game.dropDown();
					game.score += 2;
					break;
				case KeyEvent.VK_SPACE:
					game.dropStraightDown();
					game.score += 24;
					break;
				} 
			}
			
			public void keyReleased(KeyEvent e) {
			}
		});
		
		// Make the falling piece drop every second and checking to see if the game is over
		new Thread() {
			@Override public void run() {
				boolean run = true;
				game.music("EXAMPLE.wav");
				while (run) {
					try {
						if (game.gameOver() == true) {
							run = false;
						}
						else {
							Thread.sleep(1000);
							game.dropDown();
						}
					} 
					catch (InterruptedException e) {}
				}
			}
		}.start();
	}
}
