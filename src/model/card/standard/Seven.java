package model.card.standard;

import java.util.ArrayList;

import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.InvalidMarbleException;
import model.player.Marble;

public class Seven extends Standard {

    public Seven(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 7, suit, boardManager, gameManager);
    }
public boolean validateMarbleSize(ArrayList<Marble> marbles) {
	if(marbles.size() == 2)
		return true;
	if(marbles.size() == 1)
		return true;
	return false;
	
}
public void act(ArrayList<Marble> marbles) throws ActionException,InvalidMarbleException{
	
}
}
