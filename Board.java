package Connect4;

import java.io.File;

import javafx.animation.PathTransition;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Board extends GridPane {
	Connect4 connect4;
	// tells which column is currently selected
	int colHovering = -1;
	// 2d array of slots
	// status: 0=free, 1=player1, 2=player2
	Slot[][] slots;
	int rows = 6;
	int cols = 7;
	Slot highlightedSlot = null;

	public Board(Connect4 connect4) {
		this.connect4 = connect4;
		slots = new Slot[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Slot slot = new Slot(connect4, i, j);
				add(slot, j, i);
				slots[i][j] = slot;
			}
		}
		addEventHandler(MouseEvent.ANY, (MouseEvent m) -> {
			mouseHovering(m);
		});
		addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent m) -> {
			mouseClicked(m);
		});

	}

	// check which column mouse is hovering over
	public void mouseHovering(MouseEvent m) {
		double x = m.getX();
		m.consume();
		if (m.getTarget() instanceof Slot) {
			Slot slot = (Slot) m.getTarget();
			setHover(slot);
		}
	}

	public void mouseClicked(MouseEvent m) {
		m.consume();
		if (m.getTarget() instanceof Slot) {
			System.out.println("is slot");
			Slot slot = (Slot) m.getTarget();
			if (slot != null) {
				action(slot);
			}
		}
	}

	// move chip over selected slot
	public void setHover(Slot slot) {
		if (!connect4.gameOver && !connect4.animationPlaying) {
			if (slot != null) {
				highlightSlot(slot);
				// move the chip to be placed over this col
				double chipX = convertXSlotCoord(slot.getLayoutX()) + slot.dimensions / 2;
				double chipY = this.getLayoutY() - connect4.circleRadius - 5;
				connect4.chipToBePlaced.setCenterX(chipX);
				connect4.chipToBePlaced.setCenterY(chipY);
			}
		}
	}

	// try to place chip, action btn was pressed
	public void action(Slot slot) {
		if (!connect4.gameOver && !connect4.animationPlaying) {
			if (slot != null) {
				int col = slot.col;
				// place a disk in the lowest empty slot in the col
				int row = -1;
				for (int i = rows - 1; i >= 0; i--) {
					if (slots[i][col].status == 0) {
						row = i;
						break;
					}
				}
				int row1 = row;
				if (row > -1) {
					// there is an open slot available
					// animate the hovering chip to this box
					double endY = convertYSlotCoord(slots[row][col].getLayoutY());
					Line line = new Line(connect4.chipToBePlaced.getCenterX(), connect4.chipToBePlaced.getCenterY(),
							connect4.chipToBePlaced.getCenterX(), endY);
					PathTransition pathTrans = new PathTransition();
					connect4.chipToBePlaced.toBack();
					pathTrans.setNode(connect4.chipToBePlaced);
					pathTrans.setPath(line);
					pathTrans.setDuration(new Duration(1000));
					pathTrans.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
					pathTrans.playFromStart();
					connect4.animationPlaying = true;
					pathTrans.setOnFinished((e) -> {
						// play sound
						connect4.mediaPlayer.stop();
						connect4.mediaPlayer.play();
						pathTrans.setNode(null); // turn off transition and delete its effects
						// reset the chip hovering over board
						connect4.animationPlaying = false;
						connect4.root.getChildren().remove(connect4.chipToBePlaced);
						connect4.chipToBePlaced = null;
						connect4.chipToBePlaced = new Circle(connect4.circleRadius);
						connect4.chipToBePlaced.setFill(connect4.player1Color);
						connect4.chipToBePlaced.setCenterX(connect4.board.getLayoutX() + connect4.circleRadius);
						connect4.chipToBePlaced.setCenterY(connect4.board.getLayoutY() - connect4.circleRadius - 5);
						connect4.root.getChildren().add(connect4.chipToBePlaced);
						// check who's turn it is
						Color color;
						if (connect4.turn == 1) {
							// player 1 so color should be red
							slots[row1][col].updateStatus(1);
							// switch to player 2's turn
							connect4.setTurn(2);
							if (checkForWin(1)) {
								// player 1 won
								playerWon(1);
							}
							// change color of chip to be placed
							connect4.chipToBePlaced.setFill(connect4.player2Color);
						} else {
							// player 2's turn
							color = Color.YELLOW;
							slots[row1][col].updateStatus(2);
							// switch to player 1's turn
							connect4.setTurn(1);
							if (checkForWin(2)) {
								// player 2 won
								playerWon(2);
							}
							connect4.chipToBePlaced.setFill(connect4.player1Color);
						}
						// change location of chip to be placed
						double chipX = convertXSlotCoord(slot.getLayoutX()) + slot.dimensions / 2;
						double chipY = this.getLayoutY() - connect4.circleRadius - 5;
						connect4.chipToBePlaced.setCenterX(chipX);
						connect4.chipToBePlaced.setCenterY(chipY);
					});
				}
			}
		}
	}
	// convert an x coordinate in terms of the slot
	// grid pane to the root
	public double convertXSlotCoord(double val) {
		double x = val + 50.0;
		return x;
	}

	// convert an y coordinate in terms of the slot
	// grid pane to the root
	public double convertYSlotCoord(double val) {
		double y = val + 100.0 + (slots[0][0].dimensions / 2);
		return y;
	}

	public void playerWon(int playerNum) {
		// stop the game
		connect4.gameOver = true;
		unHighlightAll();
		// play win sound
		Media sound = new Media(new File(connect4.winSoundName).toURI().toString());
		connect4.mediaPlayer = new MediaPlayer(sound);
		connect4.mediaPlayer.play();
		// display a message to players that someone won
		if (playerNum == 1) {
			connect4.winMessage.setText("Player 1 Wins!");
			connect4.winMessage.setAccessibleText("Player 1 Wins!");
			connect4.winMessage.setFill(connect4.player1Color);
		} else if (playerNum == 2) {
			connect4.winMessage.setText("Player 2 Wins!");
			connect4.winMessage.setAccessibleText("Player 2 Wins!");
			connect4.winMessage.setFill(connect4.player2Color);
		}
		connect4.title.setVisible(false);
		connect4.winMessage.setVisible(true);
		connect4.resetBoardBtn.setVisible(true);
		connect4.resetBoardBtn.setFocusTraversable(true);
		connect4.winMessage.requestFocus();
	}

	// reset the board
	public void resetBoard() {
		unHighlightAll();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				slots[i][j].updateStatus(0);
			}
		}
		connect4.winMessage.setVisible(false);
		connect4.resetBoardBtn.setFocusTraversable(false);
		connect4.resetBoardBtn.setVisible(false);
		connect4.title.setVisible(true);
		connect4.gameOver = false;
		// ste sound back to chip sound
		Media sound = new Media(new File(connect4.chipSoundName).toURI().toString());
		connect4.mediaPlayer = new MediaPlayer(sound);
	}

	// check if the given player has won the game
	public boolean checkForWin(int playerNum) {
		// check rows
		int cnt = 0;
		for (int row = 0; row < rows; row++) {
			cnt = 0;
			for (int col = 0; col < cols; col++) {
				if (slots[row][col].status == playerNum) {
					cnt++;
				} else {
					cnt = 0;
				}
				if (cnt >= 4) {
					return true;
				}
			}
		}
		// check cols
		for (int col = 0; col < cols; col++) {
			cnt = 0;
			for (int row = 0; row < rows; row++) {
				if (slots[row][col].status == playerNum) {
					cnt++;
				} else {
					cnt = 0;
				}
				if (cnt >= 4) {
					return true;
				}
			}
		}
		// check diagonals
		for (int i = slots.length - 1; i > 0; i--) {
			cnt = 0;
			for (int j = 0, x = i; x <= slots.length - 1; j++, x++) {
				if (slots[x][j].status == playerNum) {
					cnt++;
				} else {
					cnt = 0;
				}
				if (cnt >= 4) {
					return true;
				}
			}
		}
		cnt = 0;
		for (int i = 0; i <= slots.length - 1; i++) {
			cnt = 0;
			for (int j = 0, y = i; y <= slots.length - 1; j++, y++) {
				if (slots[j][y].status == playerNum) {
					cnt++;
				} else {
					cnt = 0;
				}
				if (cnt >= 4) {
					return true;
				}
			}
		}
		cnt = 0;
		for (int k = 0; k <= rows + cols - 2; k++) {
			cnt = 0;
			for (int j = 0; j <= k; j++) {
				int i = k - j;
				if (i < rows && j < cols) {
					if (slots[i][j].status == playerNum) {
						cnt++;
					} else {
						cnt = 0;
					}
					if (cnt >= 4) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void unHighlightAll() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				slots[i][j].unHighlight();
			}
		}
	}

	public void highlightSlot(Slot slot) {
		// unhighlight all other slots, since only one
		// should be highlighted at a time
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (slots[i][j] != slot) {
					slots[i][j].unHighlight();
				}
			}
		}
		highlightedSlot = slot;
		slot.requestFocus();
		slot.highlight();
	}
}
