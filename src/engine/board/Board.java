package engine.board;

import java.util.ArrayList;

import engine.GameManager;
import exception.CannotFieldException;
import exception.IllegalDestroyException;
import exception.IllegalMovementException;
import exception.IllegalSwapException;
import exception.InvalidMarbleException;
import model.Colour;
import model.player.Marble;

@SuppressWarnings("unused")
public class Board implements BoardManager {
    private final ArrayList<Cell> track;
    private final ArrayList<SafeZone> safeZones;
	private final GameManager gameManager;
    private int splitDistance;

    public Board(ArrayList<Colour> colourOrder, GameManager gameManager) {
        this.track = new ArrayList<>();
        this.safeZones = new ArrayList<>();
        this.gameManager = gameManager;
        
        for (int i = 0; i < 100; i++) {
            this.track.add(new Cell(CellType.NORMAL));
            
            if (i % 25 == 0) 
                this.track.get(i).setCellType(CellType.BASE);
            
            else if ((i+2) % 25 == 0) 
                this.track.get(i).setCellType(CellType.ENTRY);
        }

        for(int i = 0; i < 8; i++)
            this.assignTrapCell();

        for (int i = 0; i < 4; i++)
            this.safeZones.add(new SafeZone(colourOrder.get(i)));

        splitDistance = 3;
    }

    public ArrayList<Cell> getTrack() {
        return this.track;
    }

    public ArrayList<SafeZone> getSafeZones() {
        return this.safeZones;
    }
    
    private ArrayList<Cell> getSafeZone(Colour colour){
    	int i;
    	for(i = 0; i<this.safeZones.size();i++) {
    		if(this.safeZones.get(i).getColour() == colour) {
    			return this.safeZones.get(i).getCells();
    		}
    	}
    	return null;
    }
    private int getPositionInPath(ArrayList<Cell> path, Marble marble) {
    	for(int i = 0; i<path.size();i++) {
    		if(path.get(i).getMarble() == marble) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    private int getBasePosition(Colour colour) {
    	int i = -1;
    	for(i = 0; i<safeZones.size();i++) {
    		if (safeZones.get(i).getColour() == colour) {
    			break;
    		}
    	}
    	if(i == -1) {
    		return i;
    	}
    	int index = i*25;
    	return index;
    	
    	
    }
    
    
    private int getEntryPosition(Colour colour) {
    	int i = this.getBasePosition(colour);
    	if(i == 0) {
    		return 98;
    	}
    	return i-2;
    }
    
    /*private ArrayList<Cell> validateSteps(Marble marble, int steps) throws IllegalMovementException{
    	boolean onTrack = false;
    	boolean inSafe = false;
    	
    	
    	//searching for marble on track or safezone
    	int index=-1;
    	for(index = 0; index < this.track.size(); index++) {
    		if(this.track.get(index).getMarble() == marble) {
    			onTrack = true;
    			break;
    		}
    	}
    	for(SafeZone safeZone: this.safeZones) {
    		for(Cell cell: safeZone.getCells()) {
        		if(cell.getMarble() == marble) {
        			inSafe = true;
        		}
        	}
    	}
    	//marble not found on track or in safe zone
    	if(!onTrack && !inSafe) {
    		throw new IllegalMovementException("Marble Cannont Be Moved");
    	}
    	else {
    		if(onTrack) {
    			
    		}
    	}
    	
    }
    */
    @Override
    public int getSplitDistance() {
        return this.splitDistance;
    }

    public void setSplitDistance(int splitDistance) {
        this.splitDistance = splitDistance;
    }
   
    private void assignTrapCell() {
        int randIndex = -1;
        
        do
            randIndex = (int)(Math.random() * 100); 
        while(this.track.get(randIndex).getCellType() != CellType.NORMAL || this.track.get(randIndex).isTrap());
        
        this.track.get(randIndex).setTrap(true);
    }

	@Override
	public void moveBy(Marble marble, int steps, boolean destroy)
			throws IllegalMovementException, IllegalDestroyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void swap(Marble marble_1, Marble marble_2) throws IllegalSwapException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroyMarble(Marble marble) throws IllegalDestroyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToBase(Marble marble) throws CannotFieldException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToSafe(Marble marble) throws InvalidMarbleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Marble> getActionableMarbles() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
