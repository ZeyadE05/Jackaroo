package model.card.standard;

import java.util.ArrayList;

import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.InvalidMarbleException;
import model.Colour;
import model.player.Marble;

public class Jack extends Standard {

    public Jack(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 11, suit, boardManager, gameManager);
    }
public void act(ArrayList<Marble> marbles) throws ActionException,InvalidMarbleException{
    	if(marbles.get(0))
    }
public boolean validateMarbleSize(ArrayList<Marble> marbles) {
	if(marbles.size() == 1)
		return true;
	if(marbles.size() == 2)
		return true;
	return false;
	
}
public boolean validateMarbleColours(ArrayList<Marble> marbles) {
	int size = marbles.size();
	 if(size < 2 ) return false;
	 for(int i = 0 ; i<size ; i++) {
		 for(int j = i+1 ; j<size ; j++) {
			 if(marbles.get(i).getColour().equals(marbles.get(j).getColour()))
				 return false;}
	 }
	 
	 return true;

}
}



