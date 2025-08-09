package Application;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.player.Marble;

public class MarbleView extends StackPane {
	Marble marble;
	Circle c;
	private boolean selected;
	Color color;
	private Circle border;

	public MarbleView(int raduis, Marble marble, Color color) {
		this.marble = marble;
		c = new Circle(raduis);
		c.setFill(color);
		this.color = color;
		if (marble == null) {
			c.setOpacity(0.4);
		} else {
			c.setOpacity(1);
		}
		border = new Circle(8 + 2);
		border.setFill(Color.TRANSPARENT);
		border.setStroke(Color.WHITE);
		border.setStrokeWidth(2);
		getChildren().addAll(c);
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if (selected) {
			getChildren().add(border);
		} else {
			getChildren().remove(border);
		}
	}

	public Marble getMarble() {
		return marble;
	}
}
