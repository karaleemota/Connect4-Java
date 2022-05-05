package Connect4;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Connect4 extends Application {
	int circleRadius = 24;
	String chipSoundName = "src/Connect4/sounds/chipDrops.wav";
	String winSoundName = "src/Connect4/sounds/win.wav";
	

	Pane root;
	Text winMessage;
	Text playerTurnText;
	Text title;
	Board board;
	Button resetBoardBtn;
	int turn = 1;
	Color player1Color = Color.RED;
	Color player2Color = Color.rgb(221, 232, 14);
	boolean gameOver = false;
	// this is used to animate moves by players
	Circle chipToBePlaced;
	Media sound;
	MediaPlayer mediaPlayer;
	boolean animationPlaying = false;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Connect4"); // local call
		root = new Pane();
		Scene scene = new Scene(root, 700, 600);
		stage.setScene(scene);
		stage.show();
		
		Media sound = new Media(new File(chipSoundName).toURI().toString());
		mediaPlayer = new MediaPlayer(sound);

		board = new Board(this);
		board.setLayoutX(50);
		board.setLayoutY(100);
		root.getChildren().add(board);

		winMessage = new Text("Player  Wins!");
		winMessage.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
		winMessage.setFill(Color.YELLOW);
		// Setting the width
		winMessage.setStrokeWidth(1);
		// Setting the stroke color
		winMessage.setStroke(Color.BLACK);

		winMessage.setLayoutX(100);
		winMessage.setLayoutY(520);
		winMessage.setVisible(false);
		root.getChildren().add(winMessage);
		
		title = new Text("Connect4");
		title.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
		title.setFill(player2Color);
		title.setStrokeWidth(1);
		title.setStroke(player1Color);
		title.setLayoutX(170);
		title.setLayoutY(550);
		title.setAccessibleText("Connect4. Player 1 is red. Player 2 is yellow");
		title.requestFocus();
		root.getChildren().add(title);

		playerTurnText = new Text("Player 1's Turn");
		playerTurnText.setFont(Font.font("Verdana", FontWeight.BOLD, 19));
		playerTurnText.setFill(Color.RED);
		playerTurnText.setStrokeWidth(1);
		playerTurnText.setStroke(Color.BLACK);
		playerTurnText.setFocusTraversable(true);
		playerTurnText.setLayoutX(520);
		playerTurnText.setLayoutY(70);
		root.getChildren().add(playerTurnText);

		resetBoardBtn = new Button("Reset Board");
		resetBoardBtn.setFont(Font.font("Verdana", 16));
		resetBoardBtn.setLayoutX(350);
		resetBoardBtn.setLayoutY(500);
		resetBoardBtn.setFocusTraversable(false);
		resetBoardBtn.setVisible(false);
		resetBoardBtn.setOnAction(e -> {
			board.resetBoard();
		});
		root.getChildren().add(resetBoardBtn);

		chipToBePlaced = new Circle(circleRadius);
		chipToBePlaced.setFill(player1Color);
		chipToBePlaced.setCenterX(board.getLayoutX() + circleRadius);
		chipToBePlaced.setCenterY(board.getLayoutY() - circleRadius - 5);
		chipToBePlaced.toBack();
		root.getChildren().add(chipToBePlaced);
	}
	
	// change who's turn it is
	public void setTurn(int playerNum) {
		this.turn = playerNum;
		if(playerNum == 1) {
			playerTurnText.setText("Player 1's Turn");
			playerTurnText.setFill(Color.RED);
		} else if(playerNum == 2) {
			playerTurnText.setText("Player 2's Turn");
			playerTurnText.setFill(Color.YELLOW);
		}
		playerTurnText.requestFocus();
	}
}
