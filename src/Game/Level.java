package Game;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Level {
	BufferedImage map;
	//ArrayList<Swarmer> swarmers = new ArrayList<Swarmer>();
	ArrayList<Swarmer> swarmerStorage = new ArrayList<Swarmer>();
	//ArrayList<Baiter> baiters = new ArrayList<Baiter>();
	ArrayList<Item> items = new ArrayList<Item>();
	//ArrayList<Plant> plants = new ArrayList<Plant>();
	ArrayList<Animation> waves = new ArrayList<Animation>();
	ArrayList<Nest> nests = new ArrayList<Nest>();
	ArrayList<Rock> rocks = new ArrayList<Rock>();
	Tile[][]fishGrid = new Tile[GamePanel.levelWidth/GamePanel.chunkSize][GamePanel.levelHeight/GamePanel.chunkSize];
	int plantLimit = 500;
	int baiterCount = 0;
	int jubJubCount = 0;
	int anglerCount = 0;
	int swarmerCount = 0;
	int plantCount = 0;

	public Level(int whatLevel){
		for(int i = 0; i<GamePanel.levelWidth/GamePanel.chunkSize; i++){
			for(int j = 0; j<GamePanel.levelHeight/GamePanel.chunkSize;j++){
				fishGrid[i][j]=new Tile();
			}
		}
		map = GamePanel.maps.get(0);
		clearLists();
		if(true){
			addSwarmersToFishGrid(5,200,200);
			addBaitersToFishGrid(400,1100,900,0);
			addBaitersToFishGrid(15,1100,900,1);
			addBaitersToFishGrid(2,1100,900,2);
			addPlantsToFishGrid(15);
			//spawn rocks
			for(int k = 0; k<3;k++){
				Point temp = new Point(GamePanel.randomNumber(100, 1820),GamePanel.randomNumber(100, 980));
				int scale = 10;
				for(int i = 9;i>=0;i--){//scale of the rocks
					scale--;
					for(int j = 0; j<(i*2);j++){//amount of rocks this size
						rocks.add(new Rock(temp.x+GamePanel.randomNumber(-100, 100),temp.y+GamePanel.randomNumber(-100, 100),scale));
					}
				}
			}
		}
	}
	public void addSwarmersToFishGrid(int amount, int x, int y){
		//spawn swarmers
		for(int i = 0;i<amount;i++){
			fishGrid[x/GamePanel.chunkSize][y/GamePanel.chunkSize].swarmers.add((new Swarmer(x, y,x,y)));
		}
		swarmerCount=amount;
	}
	public void addBaitersToFishGrid(int amount, int x, int y,int id){
		//spawn baiters
		for(int i = 0;i<amount;i++){
			if(amount>1){
				fishGrid[x/GamePanel.chunkSize][y/GamePanel.chunkSize].baiters.add((new Baiter(GamePanel.randomNumber(100, 1800), GamePanel.randomNumber(800,950),id)));
			}
			else{
				fishGrid[x/GamePanel.chunkSize][y/GamePanel.chunkSize].baiters.add((new Baiter(x, y,id)));
			}

			if(id==0){
				baiterCount++;
			}
			else if(id==1){
				jubJubCount++;
			}
			else if(id==2){
				anglerCount++;
			}
		}
	}
	public void addPlantsToFishGrid(int amount){
		//spawn plants
		for(int i = 0; i<amount;i++){
			int x = GamePanel.randomNumber(100, 1820);
			int y = GamePanel.randomNumber(100, 980);
			Plant temp = new Plant(x,y,0);
			boolean retry = true;
			while(retry){
				
				temp = new Plant(GamePanel.randomNumber(100, 1820),GamePanel.randomNumber(100, 980),0);
				retry = false;

				for(int j = 0; j<fishGrid[x/GamePanel.chunkSize][y/GamePanel.chunkSize].plants.size();j++){
					double distanceToCurrentClosest = Math.sqrt(Math.pow((temp.xpos-fishGrid[x/GamePanel.chunkSize][y/GamePanel.chunkSize].plants.get(j).xpos),2)+Math.pow((temp.ypos-fishGrid[x/GamePanel.chunkSize][y/GamePanel.chunkSize].plants.get(j).ypos),2));
					if((distanceToCurrentClosest<10)){
						retry = true;
					}
				}
			}
			fishGrid[x/GamePanel.chunkSize][y/GamePanel.chunkSize].plants.add((temp));
			plantCount++;
		}
	}

	public void clearLists(){
		for(int i = 0; i<GamePanel.levelWidth/GamePanel.chunkSize; i++){
			for(int j = 0; j<GamePanel.levelHeight/GamePanel.chunkSize;j++){
				fishGrid[i][j]=new Tile();
			}
		}
		swarmerStorage.clear();
		items.clear();
		waves.clear();
		nests.clear();
		rocks.clear();
	}
}
