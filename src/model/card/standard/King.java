package model.card.standard;

import java.util.ArrayList;

import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.InvalidMarbleException;
import model.player.Marble;

public class King extends Standard {

    public King(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 13, suit, boardManager, gameManager);
    }
public void act(ArrayList<Marble> marbles) throws ActionException,InvalidMarbleException{
    	
    }
public boolean validateMarbleSize(ArrayList<Marble> marbles) {
	if(marbles.size() == 0)
		return true;
	if(marbles.size() == 1)
		return true;
	return false;
	
}

}
