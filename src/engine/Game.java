package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import engine.board.Board;
import exception.CannotDiscardException;
import exception.CannotFieldException;
import exception.IllegalDestroyException;
import model.Colour;
import model.card.Card;
import model.card.Deck;
import model.player.*;

@SuppressWarnings("unused")
public class Game implements GameManager {
    private final Board board;
    private final ArrayList<Player> players;
	private int currentPlayerIndex;
    private final ArrayList<Card> firePit;
    private int turn;

    public Game(String playerName) throws IOException {
        turn = 0;
        currentPlayerIndex = 0;
        firePit = new ArrayList<>();

        ArrayList<Colour> colourOrder = new ArrayList<>();
        
        colourOrder.addAll(Arrays.asList(Colour.values()));
        
        Collections.shuffle(colourOrder);
        
        this.board = new Board(colourOrder, this);
        
        Deck.loadCardPool(this.board, (GameManager)this);
        
        this.players = new ArrayList<>();
        this.players.add(new Player(playerName, colourOrder.get(0)));
        
        for (int i = 1; i < 4; i++) 
            this.players.add(new CPU("CPU " + i, colourOrder.get(i), this.board));
        
        for (int i = 0; i < 4; i++) 
            this.players.get(i).setHand(Deck.drawCards());
        
    }
    
    public Board getBoard() {
        return board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Card> getFirePit() {
        return firePit;
    }

	@Override
	public void sendHome(Marble marble) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fieldMarble() throws CannotFieldException, IllegalDestroyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discardCard(Colour colour) throws CannotDiscardException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discardCard() throws CannotDiscardException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Colour getActivePlayerColour() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Colour getNextPlayerColour() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
