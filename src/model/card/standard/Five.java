package model.card.standard;
import java.util.ArrayList;
import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.InvalidMarbleException;
import model.player.Marble;
import model.card.Card;

public class Five extends Standard {

    public Five(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 5, suit, boardManager, gameManager);
    }
 public void act(ArrayList<Marble> marbles) throws ActionException,InvalidMarbleException{
	 Marble chosen = marbles.get(0);  
	    boardManager.moveBy(chosen, 5, false);
    }
 public boolean validateMarbleColours(ArrayList<Marble> marbles) {
	 
	 int size = marbles.size();
	 
	 for(int i = 0 ; i<size ; i++) {
		 for(int j = i+1 ; j<size ; j++) {
			 if(marbles.get(i).getColour().equals(marbles.get(j).getColour()))
				 return false;}
	 }
	 
	 return true;
	}
}
