package Game;

import java.util.ArrayList;

public class Tile {
	ArrayList<Baiter> baiters = new ArrayList<Baiter>();
	ArrayList<Swarmer> swarmers = new ArrayList<Swarmer>();
	ArrayList<Plant> plants = new ArrayList<Plant>();
	public Tile(){

	}
	public void addBaiter(Baiter newFish){
		baiters.add(newFish);
		if(newFish.fishID==0){
			GamePanel.levels.get(GamePanel.currentLevel).baiterCount++;
		}
		else if(newFish.fishID==1){
			GamePanel.levels.get(GamePanel.currentLevel).jubJubCount++;
		}
		else if(newFish.fishID==2){
			GamePanel.levels.get(GamePanel.currentLevel).anglerCount++;
		}
	}
	public void addSwarmer(Swarmer newFish){
		swarmers.add(newFish);
		GamePanel.levels.get(GamePanel.currentLevel).swarmerCount++;
	}
	public void addPlant(Plant newPlant){
		plants.add(newPlant);
		GamePanel.levels.get(GamePanel.currentLevel).plantCount++;
	}
	//remove
	public void removeBaiter(Baiter newFish){
		int baitersBeforeRemove = baiters.size();
		baiters.remove(newFish);
		if(baiters.size()<baitersBeforeRemove){
			if(newFish.fishID==0){
				GamePanel.levels.get(GamePanel.currentLevel).baiterCount--;
			}
			else if(newFish.fishID==1){
				GamePanel.levels.get(GamePanel.currentLevel).jubJubCount--;
			}
			else if(newFish.fishID==2){
				GamePanel.levels.get(GamePanel.currentLevel).anglerCount--;
			}
		}
	}
	public void removeSwarmer(Swarmer swarmer){
		int newFish = -1;
		int swarmersBeforeRemove = swarmers.size();
		for(int j = 0;j<swarmers.size();j++){
			if(swarmers.get(j).equals(swarmer)){
				newFish=j;
			}
		}
		if(newFish!=-1){
			swarmers.remove(newFish);
			if(swarmers.size()<swarmersBeforeRemove){
				GamePanel.levels.get(GamePanel.currentLevel).swarmerCount--;
			}
		}
	}
	public void removePlant(Plant newPlant){
		int plantsBeforeRemove = plants.size();
		plants.remove(newPlant);
		if(plants.size()<plantsBeforeRemove){
			GamePanel.levels.get(GamePanel.currentLevel).plantCount--;
		}
	}
}
