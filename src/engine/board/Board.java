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
    
    private ArrayList<Cell> validateSteps(Marble marble, int steps) throws IllegalMovementException{
    	boolean onTrack = false;
    	boolean inSafe = false;
    	ArrayList<Cell> res = new ArrayList<>();
    	
    	//searching for marble on track or safezone
    	int index=-1;
    	for(index = 0; index < this.track.size(); index++) {
    		if(this.track.get(index).getMarble() == marble) {
    			onTrack = true;
    			break;
    		}
    	}
    	if(!onTrack) {
    		ArrayList<Cell> zone = this.getSafeZone(marble.getColour());
    		for(index = 0; index<zone.size();index++) {
        		if(zone.get(index).getMarble() == marble) {
        			inSafe = true;
        			break;
        		}
        	}
    	}
    	
    	
    	//marble not found on track or in safe zone
    	if(!onTrack && !inSafe) {
    		throw new IllegalMovementException("Marble Cannont Be Moved");
    	}
    	else {
    		if(onTrack) {
    			int szIndex=0;
    			if(steps != 4 && steps!=5) {
    				if(index + steps > this.getEntryPosition(marble.getColour())+4) {
        				throw new IllegalMovementException("Rank of Card Played is too high");
        			}
    				else if(index + steps<= this.getEntryPosition(marble.getColour()) + 4) {
        				for(int i = 0; i<=steps;i++) {
        					if(i+index <=this.getEntryPosition(marble.getColour())) {
        						res.add(track.get(i+index));
        					}
        					else {
        						res.add(this.getSafeZone(marble.getColour()).get(szIndex++));
        					}
        				}
        			}
    			}
    			if (steps == 4) {
    			    for (int i = 0; i <= 4; i++) {
    			        int newIndex = index - i;
    			        // Handle wrapping for negative indices
    			        while (newIndex < 0) {
    			            newIndex += track.size();
    			        }
    			        // Handle wrapping for indices beyond track size
    			        newIndex %= track.size();
    			        res.add(track.get(newIndex));
    			    }
    			}
    			if(steps == 5) {
    				
    				for(int i = 0;i<=5;i++) {
    					res.add(track.get((index+i)%100));
    				}
    			}
    			
    		}
    		else if(inSafe) {
    			if(steps == 4) {
    				throw new IllegalMovementException("Marble moving backward in Safe Zone");
    			}
    			else if(index + steps >= 4) {
    				throw new IllegalMovementException("Rank of Card Played is too high");
    			}
    			else {
    				for(int i = 0; i<steps;i++) {
    					res.add(this.getSafeZone(marble.getColour()).get(index+i));
    				}
    			}
    		}
    	}
    	return res;
    	
    }

    private void validatePath(Marble marble, ArrayList<Cell> fullPath, boolean destroy) throws IllegalMovementException{
    	boolean validPath = true;
    	
    	int marbleCount = 0;
    	
    	for(Cell cell: fullPath) {
    		if(cell.getMarble()!= null) {
    			marbleCount++;
    			if(cell.getCellType() == CellType.SAFE) {//Marble in safezone
    				throw new IllegalMovementException("Cannot Bypass or Land on a marble in its SafeZone");
    			}
    			if( cell == track.get(this.getBasePosition(cell.getMarble().getColour()))){ //Base Cell Blockage
    				throw new IllegalMovementException("Cannot Bypass or Land on a marble in its Base Cell");
    			}
    			if(!destroy) {
        			if(cell == track.get(this.getEntryPosition(marble.getColour()))) {//SafeZone Entry
        				throw new IllegalMovementException("Cannot Bypass or Land on a marble because the SafeZone Entry is blocked");
        			}
        			if(cell.getMarble().getColour() == marble.getColour()) { //Self Blocking
        				throw new IllegalMovementException("Cannot Bypass or Land on a marble on player's own marble");
            		}
    			}
    		}
    		
    		if(marbleCount>1 && !destroy) {//Path Blockage
    			throw new IllegalMovementException("Cannot Bypass or Land on a marble in its safezone");
    		}
    	}
    }
    
    private void move(Marble marble, ArrayList<Cell> fullPath, boolean destroy) throws IllegalDestroyException{
    	Cell TargetPosition = fullPath.get(fullPath.size()-1);
    	if(destroy) {
    		for(Cell cell: fullPath) {
        		if(cell.getCellType() == CellType.SAFE && cell.getMarble()!=null) {
        			throw new IllegalDestroyException("Attempting to destroy a marble in its safeZone");//Illegal Attempt to destroy a marble in its safeZone
        		}
        	}
    	}
    	if(TargetPosition.getMarble().getColour() == marble.getColour()) {
    		throw new IllegalDestroyException("Attempting to land on target position containing a marble of the same colour");//Illegal Attempt to destroy a marble of the same colour
    	}
    	
    	for(Cell cell: this.track) {
    		if (cell.getMarble()==marble) {// checks for marble in Track
    			cell.setMarble(null);
    		}
    	}
    	for(Cell cell:this.getSafeZone(marble.getColour())) {// checks for marble in SafeZone
    		if(cell.getMarble() == marble) {
    			cell.setMarble(null);
    		}
    	}
    	if(!TargetPosition.isTrap()) TargetPosition.setMarble(marble);
    	else { // removes marble from track and changes position of trap cell
    		TargetPosition.setTrap(false);
    		this.assignTrapCell();
    	}	
    }
    
    private void validateSwap(Marble marble_1, Marble marble_2) throws IllegalSwapException{
    	boolean onTrack1 = false;
    	boolean onTrack2 = false;
    	
    	int marble1Position = -1;
    	int marble2Position = -1;
    	
    	for(int i = 0; i<this.track.size();i++) {
    		if(this.track.get(i).getMarble() == marble_1) {
    			onTrack1 = true;
    			marble1Position = i;
    		}
    		if(this.track.get(i).getMarble() == marble_2) {
    			onTrack2 = true;
    			marble2Position = i;
    		}
    	}
    	if(!onTrack1 || !onTrack2) {
    		throw new IllegalSwapException("Marbles are not available on Track");
    	}
    	if(marble1Position == this.getBasePosition(marble_1.getColour())) {
    		throw new IllegalSwapException("Marble 1 is in its base zone");
    	}
    	if(marble2Position == this.getBasePosition(marble_2.getColour())) {
    		throw new IllegalSwapException("Marble 2 is in its base zone");
    	}
    	
    }
    
    private void validateDestroy(int positionInPath) throws IllegalDestroyException{
    	if(positionInPath == -1 || positionInPath >= track.size()) {
    		throw new IllegalDestroyException("Marble not on track");
    	}
    	else{
    		Cell cell = this.track.get(positionInPath);	
    		Marble marble = cell.getMarble();
    		if(positionInPath == this.getBasePosition(marble.getColour())) {
    			throw new IllegalDestroyException("Marble is on its Base cell");
    		}
    	}
    }
    
    private void validateFielding(Cell occupiedBaseCell) throws CannotFieldException{
    	Marble marble = occupiedBaseCell.getMarble();
    	if(marble!=null) {
    		if(this.track.get(this.getBasePosition(marble.getColour()))==occupiedBaseCell) {
    			throw new CannotFieldException("Base Cell occupied by another marble of the same colour");
    		}
    	}
    }
    
    private void validateSaving(int positionInSafeZone, int positionOnTrack) throws InvalidMarbleException {
        // Check if marble is already in the safe zone
        if (positionOnTrack == -1) {
            throw new InvalidMarbleException("Marble is already in the safe zone!");
        }
        
        // Check if the marble is in a valid position
        if (positionOnTrack < 0 || positionOnTrack >= track.size()) {
            throw new InvalidMarbleException("Marble is not on the track!");
        }
        
        // Check if the target safe zone position is valid
        if (positionInSafeZone < 0 || positionInSafeZone >= 4) {
            throw new InvalidMarbleException("Invalid safe zone position!");
        }
    }
    
    
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
		ArrayList<Cell> path = validateSteps(marble,steps);
		validatePath(marble,path,destroy);
		move(marble,path,destroy);
	}

	@Override
	public void swap(Marble marble_1, Marble marble_2) throws IllegalSwapException {
		validateSwap(marble_1,marble_2);
		Cell position1 = null;
		Cell position2 = null;
		for(Cell cell: track) {
			if(cell.getMarble() == marble_1) {
				position1 = cell;
			}
			if(cell.getMarble() == marble_2) {
				position2 = cell;
			}
		}
		position1.setMarble(marble_2);
		position2.setMarble(marble_1);
	}

	@Override
	public void destroyMarble(Marble marble) throws IllegalDestroyException {
		int i = -1;
		for(int index = 0; index<track.size();index++) {
			if(track.get(index).getMarble() == marble) {
				i = index;
				break;
			}
		}
		validateDestroy(i);
		track.get(i).setMarble(null);
		gameManager.sendHome(marble);
	}

	@Override
	public void sendToBase(Marble marble) throws CannotFieldException {
		Cell cell = track.get(this.getBasePosition(marble.getColour()));
		validateFielding(cell);
		if(cell.getMarble()!=null) {
			gameManager.sendHome(cell.getMarble());
		}
		cell.setMarble(marble);
	}

	@Override
	public void sendToSafe(Marble marble) throws InvalidMarbleException {
		int r = (int)(Math.random()*4);
		int i = -1;
		for(int index = 0; index<track.size();index++) {
			if(track.get(index).getMarble() == marble) {
				i = index;
				break;
			}
		}
		validateSaving(r,i);
		this.track.get(i).setMarble(null);
		this.getSafeZone(marble.getColour()).get(i).setMarble(marble);
	}

	@Override
	public ArrayList<Marble> getActionableMarbles() {
		Colour colour = gameManager.getActivePlayerColour();
		ArrayList<Marble> res = new ArrayList<>();
		for(Cell cell: this.track) {
			if(cell.getMarble().getColour() == colour) {
				res.add(cell.getMarble());			
			}
		}
		SafeZone sz = null;
		for(SafeZone safeZone: this.safeZones) {
			if(safeZone.getColour()== colour) {
				sz = safeZone;
			}
		}
		for(Cell cell:sz.getCells()) {
			if(cell.getMarble()!=null) {
				res.add(cell.getMarble());
			}
		}
		return res;
	}
    
}
