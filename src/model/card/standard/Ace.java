package model.card.standard;
import java.util.ArrayList;
import engine.GameManager;
import engine.board.BoardManager;
import exception.ActionException;
import exception.InvalidMarbleException;
import model.player.Marble;
import model.card.Card;

public class Ace extends Standard {

    public Ace(String name, String description, Suit suit, BoardManager boardManager, GameManager gameManager) {
        super(name, description, 1, suit, boardManager, gameManager);
    }
    
    @Override
    public void act(ArrayList<Marble> marbles) throws ActionException,InvalidMarbleException{
    	
    		    gameManager.fieldMarble();
    		    Marble chosen = marbles.get(0);
    		    boardManager.moveBy(chosen, getRank(), false);
    }
    public boolean validateMarbleSize(ArrayList<Marble> marbles) {
    	if(marbles.size() == 1)
    		return true;
    	if(marbles.size() == 0)
    		return true;
    	return false;
    	
    }
   
}
