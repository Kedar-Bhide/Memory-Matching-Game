import java.io.File;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * This class implements the methods to run a complete Memory Matching Game
 * 
 * @author Kedar Bhide
 *
 */
public class MemoryGame {

	// Congratulations message
	private final static String CONGRA_MSG = "CONGRATULATIONS! YOU WON!";

	// Cards not matched message
	private final static String NOT_MATCHED = "CARDS NOT MATCHED. Try again!";

	// Cards matched message
	private final static String MATCHED = "CARDS MATCHED! Good Job!";

	// 2D-array which stores cards coordinates on the window display
	private final static float[][] CARDS_COORDINATES = new float[][] { { 170, 170 }, { 324, 170 },
			{ 478, 170 }, { 632, 170 }, { 170, 324 }, { 324, 324 }, { 478, 324 }, { 632, 324 },
			{ 170, 478 }, { 324, 478 }, { 478, 478 }, { 632, 478 } };

	// Array that stores the card images filenames
	private final static String[] CARD_IMAGES_NAMES = new String[] { "ball.png", "redFlower.png",
			"yellowFlower.png", "apple.png", "peach.png", "shark.png" };

	// PApplet object that represents the graphic display window
	private static PApplet processing;

	// one dimensional array of cards
	private static Card[] cards;

	// array of images of the different cards
	private static PImage[] images;

	// First selected card
	private static Card selectedCard1;

	// Second selected card
	private static Card selectedCard2;

	// boolean evaluated true if the game is won, and false otherwise
	private static boolean winner;

	// number of cards matched so far in one session of the game
	private static int matchedCardsCount;

	// Displayed message to the display window
	private static String message;

	/**
	 * Starts the memory game
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Utility.startApplication();
	}

	/**
	 * Defines the initial environment properties of this game as the program starts
	 */
	public static void setup(PApplet processing) {

		MemoryGame.processing = processing; // initialize the processing field to input argument
											// parameter

		images = new PImage[CARD_IMAGES_NAMES.length]; // initialize array of length equal to total
														// images(6)

		// load and store all image files into the array
		for (int i = 0; i < images.length; i++) {
			images[i] = processing.loadImage("images" + File.separator + CARD_IMAGES_NAMES[i]);
		}

		startNewGame();
	}

	/**
	 * Initializes the Game
	 */
	public static void startNewGame() {

		// initialize static fields back to base case before the start of a new game
		selectedCard1 = null;
		selectedCard2 = null;
		matchedCardsCount = 0;
		winner = false;
		message = "";

		cards = new Card[CARDS_COORDINATES.length]; // initialize array array which holds Card
													// objects, with particular image and its
													// coordinates

		int[] mixedUp = Utility.shuffleCards(cards.length); // creates array that stores digits 0-5
															// in random order, each repeating twice

		// store particular card object with image and coordinates for 3x4 playing board
		for (int i = 0; i < mixedUp.length; i++) {
			cards[i] = new Card(images[mixedUp[i]], CARDS_COORDINATES[i][0],
					CARDS_COORDINATES[i][1]);
		}

	}

	/**
	 * Callback method called each time the user presses a key
	 */
	public static void keyPressed() {

		// checks if the key n or N is pressed, and starts a new game in that case
		if (processing.key == 'N' || processing.key == 'n') {
			startNewGame();
		}
	}

	/**
	 * Callback method draws continuously this application window display
	 */
	public static void draw() {

		// Set the color used for the background of the Processing window
		processing.background(245, 255, 250); // Mint cream color

		// loop through the array with card objects, and drawing to our game board based
		// on their coordinates
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] != null) {
				cards[i].draw();
			}
		}

		// calling displayMessage to update the game board with the appropriate message
		// after moves
		displayMessage(message);
	}

	/**
	 * Displays a given message to the display window
	 * 
	 * @param message to be displayed to the display window
	 */
	public static void displayMessage(String message) {
		processing.fill(0);
		processing.textSize(20);
		processing.text(message, processing.width / 2, 50);
		processing.textSize(12);
	}

	/**
	 * Checks whether the mouse is over a given Card
	 * 
	 * @return true if the mouse is over the storage list, false otherwise
	 */
	public static boolean isMouseOver(Card card) {

		// getting the reference of the image
		PImage image = card.getImage();

		float upperWidth = image.width / 2 + card.getX(); // right half of the card
		float upperHeight = image.height / 2 + card.getY(); // lower half of the card
		float lowerWidth = -image.width / 2 + card.getX(); // left half of the card
		float lowerHeight = -image.height / 2 + card.getY(); // upper half of the card

		// essentially checks if the card is at the position as asked by the user from
		// an input from the mouse
		if (processing.mouseX < upperWidth && processing.mouseX > lowerWidth
				&& processing.mouseY < upperHeight && processing.mouseY > lowerHeight) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Callback method called each time the user presses the mouse
	 */
	public static void mousePressed() {

		Card card = null;
		message = "";

		if (selectedCard1 != null && selectedCard2 != null) {
			if (matchingCards(selectedCard1, selectedCard2)) {
				// set appropriate states of matched cards so they cannot be selected again to
				// compare
				selectedCard1.setMatched(true);
				selectedCard2.setMatched(true);
				// deselect the matched cards, so 2 new cards can be selected to compare
				if (matchedCardsCount != cards.length) {
					selectedCard1.deselect();
					selectedCard2.deselect();
				}
			} else {
				// set appropriate state of unmatched cards, so they can be selected again to
				// find their pairs
				selectedCard1.setVisible(false);
				selectedCard2.setVisible(false);
				selectedCard1.deselect();
				selectedCard2.deselect();
			}
			// set the 2 selected cards to null again so new cards can be assigned to these
			// static fields for comparison
			selectedCard1 = null;
			selectedCard2 = null;
		}

		for (int i = 0; i < cards.length; i++) {
			card = cards[i]; // checking every card from the array

			// if mouse is over the card and it is not already matched with its pair, it is
			// selected and is visible to player
			if (isMouseOver(card) && !card.isMatched()) {
				card.setVisible(true);
				card.select();

				if (selectedCard1 == null) { // the above selected card is set to first card if
												// there is no card already selected
					selectedCard1 = card;
				} else if (selectedCard2 == null && card != selectedCard1) {
					// checks if a card is selected, this is the second card selected and it is not
					// equal to the first card selected
					selectedCard2 = card;
					// if cards matched, increment total cards matched by 2
					if (matchingCards(selectedCard1, selectedCard2)) {
						matchedCardsCount += 2;
						message = MATCHED;
					} else {
						message = NOT_MATCHED;
					}
				}
				break;
			}

		}

		// if total cards matched is equal to total cards, game ends
		if (matchedCardsCount == cards.length) {
			winner = true;
			message = CONGRA_MSG;
		}

	}

	/**
	 * Checks whether two cards match or not
	 * 
	 * @param card1 reference to the first card
	 * @param card2 reference to the second card
	 * @return true if card1 and card2 image references are the same, false
	 *         otherwise
	 */
	public static boolean matchingCards(Card card1, Card card2) {

		// checks if references for 2 selected cards is the same
		if (card1.getImage() == card2.getImage()) {
			return true;
		}

		return false;
	}
}
