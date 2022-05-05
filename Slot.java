package Connect4;

import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * tile that holds info about whos piece is on here
 */
public class Slot extends Pane {
	Connect4 connect4;
	int dimensions = 62;
	Rectangle rect;
	Circle chip;
	// status: 0=free, 1=player1, 2=player2
	int status;
	int row;
	int col;
	boolean highlighted;
	Shape shape;

	public Slot(Connect4 connect4, int row, int col) {
		super();
		this.connect4 = connect4;
		this.row = row;
		this.col = col;
		highlighted = false;
		setPrefSize(dimensions, dimensions);
		Circle circle = new Circle(connect4.circleRadius);
		circle.setFill(Color.WHITE);
		circle.setCenterX(dimensions / 2);
		circle.setCenterY(dimensions / 2);
		
		rect = new Rectangle(dimensions, dimensions);
		rect.setStyle("-fx-background-color: #1822a3;");
		shape = Shape.subtract(rect, circle);
		shape.setFill(Color.rgb(24, 34, 163));
		shape.toFront();
		getChildren().add(shape);
		
		// accessibility
		setAccessibleRole(AccessibleRole.TEXT);
		setAccessibleText("row " + row+" column "+col+" is empty");
		setFocusTraversable(true);
		
		focusedProperty().addListener((observalbe, oldState, newState) -> {
			if (newState) {
				connect4.board.setHover(this);
			} 
		});
		
		setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
				connect4.board.action(this);
			}
		});
	}
	
	public void updateStatus(int status) {
		this.status = status;
		Color color = Color.WHITE;
		if(status == 1) {
			color = connect4.player1Color;
			setAccessibleText("row " + row+" column "+col+" is red");
		} else if(status == 2) {
			color = connect4.player2Color;
			setAccessibleText("row " + row+" column "+col+" is yellow");
		} else {
			// status is none so remove chip form this slot
			if(chip != null) {
				getChildren().remove(chip);
				chip = null;
			}
			return;
		}
		// add disk to this slot
		chip = new Circle(connect4.circleRadius);
		chip.setFill(color);
		chip.setCenterX(dimensions / 2);
		chip.setCenterY(dimensions / 2);
		getChildren().add(chip);
	}

	public void highlight() {
		highlighted = true;
		shape.setFill(Color.rgb(92, 103, 242));
	}

	public void unHighlight() {
		highlighted = false;
		shape.setFill(Color.rgb(24, 34, 163));
	}
}
