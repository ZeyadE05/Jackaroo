package model.card.standard;

import java.util.ArrayList;

import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.InvalidMarbleException;
import model.player.Marble;

public class Jack extends Standard {

    public Jack(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 11, suit, boardManager, gameManager);
    }
public void act(ArrayList<Marble> marbles) throws ActionException,InvalidMarbleException{
    	
    }
public boolean validateMarbleSize(ArrayList<Marble> marbles) {
	if(marbles.size() == 0)
		return true;
	if(marbles.size() == 2)
		return true;
	return false;
	
}
}
