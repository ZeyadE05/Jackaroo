package Application;

import java.util.ArrayList;

import model.player.Marble;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class HomeZoneView extends StackPane{
	ArrayList<MarbleView> marbles;
	Rectangle r;
	
	public HomeZoneView(Color c, ArrayList<Marble> HomeZoneMarble){
		r = new Rectangle();
		r.setWidth(50);
		r.setHeight(50);
		r.setFill(c);
		r.setOpacity(0.1);
		GridPane g = new GridPane();
		marbles = new ArrayList<>();
		for(int i = 0; i<HomeZoneMarble.size();i++){
			MarbleView m = new MarbleView(8,HomeZoneMarble.get(i),c);
			marbles.add(m);
			if(i == 0){
				g.add(m,0,0);
			}
			if(i == 1){
				g.add(m,0,1);
			}
			if(i == 2){
				g.add(m,1,0);
			}
			if(i == 3){
				g.add(m,1,1);
			}
		}
		g.setVgap(10);
		g.setHgap(15);
		getChildren().addAll(r,g);
	}
	public void FieldMarble(){
		
	}
}
