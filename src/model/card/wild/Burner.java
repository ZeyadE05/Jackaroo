package model.card.wild;

import java.util.ArrayList;

import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.InvalidMarbleException;
import model.player.Marble;

public class Burner extends Wild {

    public Burner(String name, String description, BoardManager boardManager, GameManager gameManager) {
        super(name, description, boardManager, gameManager); 
    }
public void act(ArrayList<Marble> marbles) throws ActionException,InvalidMarbleException{
    	
    }
    public boolean validateMarbleColours(ArrayList<Marble> marbles) {
   	 	if(marbles.size() == 0)
   	 		return false;
   	 	if(marbles.get(0).getColour().equals(gameManager.getActivePlayerColour()))return false;
   	 	return true;
   	 

   	}
    
}
