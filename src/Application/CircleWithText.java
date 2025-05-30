package Application;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class CircleWithText extends StackPane {
    private Circle circle;
    private Text text;
    Circle border;
    public CircleWithText(String s, double radius,Color c) {
        // Circle setup
        circle = new Circle(radius);
        circle.setFill(c);
        circle.setStroke(Color.DARKGREEN);
        circle.setStrokeWidth(2);
        // Text setup
        text = new Text(s);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: " + (radius / 2) + ";");

        // Add to StackPane (automatically centers)
        getChildren().addAll(circle, text);
    }

    // New method to update the color dynamically
    public void setCircleColor(Color color) {
        circle.setFill(color);
    }
    public void highlightCircle(Color c) {
    	getChildren().remove(border);
    	border = new Circle(50+5);
		border.setFill(Color.TRANSPARENT);
		border.setStroke(c);
        border.setStrokeWidth(5);
        getChildren().add(border);
    }
}