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
        				for(int i = 1; i<=steps;i++) {
        					if(i+index <=this.getEntryPosition(marble.getColour())) {
        						res.add(track.get(i+index));
        					}
        					else {
        						res.add(this.getSafeZone(marble.getColour()).get(szIndex++));
        					}
        				}
        			}
    			}
    			else if(steps == 4) {
    				for(int i = 1; i<=4;i++) {
    					res.add(track.get(index-i));
    				}
    			}
    			else if(steps == 5) {
    				
    				for(int i = 1;i<=5;i++) {
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
