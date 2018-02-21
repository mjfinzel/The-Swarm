package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Baiter {
	double xpos;
	double ypos;
	int fishID;
	double size = 1;
	int moveSpeed=1;
	double turnSpeed = 1;
	double angleInRadians = 0;
	double angleInDegrees=0;
	double desiredAngleInDegrees=0;
	Animation forwardAnim;
	boolean movingForward = false;
	Point moveDestination;
	int visionRadius = 250;
	int attackRadius = 1;
	double currentHealth;
	double maxHealth;
	int currentHunger = 17;
	int maxHunger = 20;
	//long lastUpdateTime = System.currentTimeMillis();
	//int updateSpeed = 1000/55;
	int attack = 1;
	int strength = 3;
	int runThreshold = 1;//number of swarmers this has to see before it runs away from them
	Point anchor = new Point(0,0);
	boolean isDead=false;
	boolean eatsPlants = false;//wether or not this can eat plants
	boolean eatsMeat = false;
	boolean isBeingChased = false;
	Plant closestFood;
	Baiter closestMate = null;
	String gender = "";
	Nest currentNest;
	Rock currentRock;
	Rock closestRock;
	Swarmer closestHostile;
	ArrayList<Swarmer> hostilesThatCanSeeThis = new ArrayList<Swarmer>();
	public Baiter(int x, int y, int ID){
		xpos = x;
		ypos = y;
		fishID=ID;
		anchor.x=x;
		anchor.y=y;
		int temp=GamePanel.randomNumber(1, 2);
		if(temp==1){
			this.gender="Male";
		}
		else{
			this.gender="Female";

		}
		if(fishID==0){
			maxHealth = 5;
			currentHealth=maxHealth;
			if(this.gender=="Male"){
				forwardAnim = new Animation(GamePanel.maleBaiterForwardAnim, 4, 250/moveSpeed, 0, (int)xpos, (int)ypos, true, 0, 0);
			}
			else{
				forwardAnim = new Animation(GamePanel.femaleBaiterForwardAnim, 4, 250/moveSpeed, 0, (int)xpos, (int)ypos, true, 0, 0);
			}
			this.size=(.1*GamePanel.randomNumber(8, 10));
			forwardAnim.xScale=size;
			forwardAnim.yScale=size;
			this.turnSpeed=1/size;
			this.eatsPlants=true;
			this.eatsMeat=false;
		}
		else if(fishID==1){
			maxHealth = 50;
			currentHealth=maxHealth;
			if(this.gender=="Male"){
				forwardAnim = new Animation(GamePanel.maleJubJubForwardAnim, 12, 70/moveSpeed, 0, (int)xpos, (int)ypos, true, 0, 0);
			}
			else{
				forwardAnim = new Animation(GamePanel.femaleJubJubForwardAnim, 12, 70/moveSpeed, 0, (int)xpos, (int)ypos, true, 0, 0);
			}
			this.size = .5;
			forwardAnim.xScale=size;
			forwardAnim.yScale=size;
			this.turnSpeed=.5/size;
			this.runThreshold=15;
			this.eatsPlants=true;
			this.eatsMeat=true;
			this.maxHunger=30;
			this.visionRadius=80;
			moveSpeed=3;
		}
		else if(fishID==2){
			maxHealth = 250;
			currentHealth=maxHealth;
			if(this.gender=="Male"){
				forwardAnim = new Animation(GamePanel.maleAnglerForwardAnim, 4, 200/moveSpeed, 0, (int)xpos, (int)ypos, true, 0, 0);
			}
			else{
				forwardAnim = new Animation(GamePanel.femaleAnglerForwardAnim, 4, 200/moveSpeed, 0, (int)xpos, (int)ypos, true, 0, 0);
			}
			this.size = .7;
			forwardAnim.xScale=size;
			forwardAnim.yScale=size;
			this.turnSpeed=.3/size;
			this.runThreshold=90;
			this.eatsMeat=true;
			this.maxHunger=30;
			this.visionRadius=150;
			this.attackRadius=16;
		}
	}
	public int getVisibleHostiles(){

		int total=0;
		double distanceToCurrentClosest=-1;
		ArrayList <Swarmer> hostiles = new ArrayList<Swarmer>();
		//x positions
		for(int i = (int)(this.xpos/GamePanel.chunkSize)-((this.visionRadius/GamePanel.chunkSize)+1); i<(this.xpos/GamePanel.chunkSize)+((this.visionRadius/GamePanel.chunkSize)+1);i++){
			//y positions
			for(int j = (int)(this.ypos/GamePanel.chunkSize)-((this.visionRadius/GamePanel.chunkSize)+1); j<(this.ypos/GamePanel.chunkSize)+((this.visionRadius/GamePanel.chunkSize)+1);j++){
				if(i<GamePanel.levelWidth/GamePanel.chunkSize&&i>0&&j<GamePanel.levelHeight/GamePanel.chunkSize&&j>0){
					hostiles = GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].swarmers;
					//loop through all the baiters in this arraylist
					for(int k = 0; k<hostiles.size();k++){
						//if((Math.sqrt(Math.pow((GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].swarmers.get(k).xpos-this.xpos),2)+Math.pow((GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].swarmers.get(k).ypos-this.ypos),2)))<=this.visionRadius){
						Point a = new Point((int)this.xpos,(int)this.ypos);
						Point b = new Point((int)hostiles.get(k).xpos,(int)hostiles.get(k).ypos);
						if(getDistanceBetween(a,b)<=this.visionRadius){	
							//add this baiter to the food list
							total++;
							double distanceToCurrent = Math.sqrt(Math.pow((this.xpos-hostiles.get(k).xpos),2)+Math.pow((this.ypos-hostiles.get(k).ypos),2));
							if(distanceToCurrent<distanceToCurrentClosest||distanceToCurrentClosest==-1){
								if(closestHostile==null){
									closestHostile=hostiles.get(k);
								}
								distanceToCurrentClosest = Math.sqrt(Math.pow((closestHostile.xpos-this.xpos),2)+Math.pow((closestHostile.ypos-this.ypos),2));
							}
							//System.out.println("added a hostile!");
						}
					}
				}                                   

			}
		}
		return total;

	}

	public ArrayList<Baiter> getVisibleMates(){
		ArrayList<Baiter> mates = new ArrayList<Baiter>();
		for(int k = 0; k<GamePanel.levelWidth/GamePanel.chunkSize; k++){
			for(int j = 0; j<GamePanel.levelHeight/GamePanel.chunkSize;j++){
				//loop through all baiters
				for(int i=0; i<GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].baiters.size();i++){
					//check if they are the proper gender
					if(!GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].baiters.get(i).gender.equals(this.gender)){
						//check if any are capable of breeding
						if(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].baiters.get(i)!=this&&GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].baiters.get(i).currentHunger==GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].baiters.get(i).maxHunger){
							if(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].baiters.get(i).closestMate==null||GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].baiters.get(i).closestMate==this){
								if(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].baiters.get(i).fishID==this.fishID){//same type of fish as this
									mates.add(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].baiters.get(i));
								}
							}
						}
					}
				}
			}
		}
		return mates;
	}
	public Point getMouthPos(){
		int x=(int)xpos;
		int y=(int)ypos;
		if(this.fishID==2){//angler
			x=(int)xpos+82+(int)((31*size)*Math.sin(angleInDegrees));
			y=(int)ypos+32+(int)((31*size)*Math.cos(angleInDegrees));
		}
		return new Point(x,y);
	}
	public Point getLanternPos(){
		int x=(int)xpos;
		int y=(int)ypos;
		if(this.fishID==2){
			x=(int)xpos+82+(int)((61*size)*Math.cos(angleInDegrees));
			y=(int)ypos+32+(int)((61*size)*Math.sin(angleInDegrees));
		}
		return new Point(x,y);
	}

	public void reproduce(){

		if(this.gender=="Male"){


			//if(mates.size()>0){
			if(closestMate==null||closestMate.currentHunger<closestMate.maxHunger){
				ArrayList<Baiter> mates = getVisibleMates();
				if(mates.size()>0){
					closestMate = mates.get(0);
					//determine which mate is closest to this
					for(int i = 0;i<mates.size();i++){
						double distanceToCurrent = Math.sqrt(Math.pow((mates.get(i).xpos-this.xpos),2)+Math.pow((mates.get(i).ypos-this.ypos),2));
						double distanceToCurrentClosest = Math.sqrt(Math.pow((closestMate.xpos-this.xpos),2)+Math.pow((closestMate.ypos-this.ypos),2));
						if(distanceToCurrent<distanceToCurrentClosest){
							closestMate = mates.get(i);
						}
					}
					closestMate.closestMate=this;
				}
			}
			if(closestMate!=null&&closestMate.currentNest!=null){
				if(closestMate.currentNest.occupied==true){
					//move towards closest mate
					boolean moved = false;
					while(!moved){
						//calculate the direction this would have to go to find the mate
						int approachAngle = (int)Math.toDegrees(Math.atan2((closestMate.ypos-this.ypos),(closestMate.xpos-this.xpos)));
						if(approachAngle<0){
							approachAngle+=360;
						}

						desiredAngleInDegrees=approachAngle;
						this.changeDirection();
						angleInRadians = Math.toRadians(angleInDegrees);
						this.forwardAnim.setAngle(angleInRadians);
						if(closestMate!=null){
							double distanceToMate = Math.sqrt(Math.pow((xpos-closestMate.xpos),2)+Math.pow((ypos-closestMate.ypos),2));
							if(Math.abs(desiredAngleInDegrees-angleInDegrees)<=45&&distanceToMate<=50){
								angleInDegrees=desiredAngleInDegrees;
							}
						}
						if(true){
							Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
							if(xpos+Math.cos(angleInRadians)>0&&xpos+Math.cos(angleInRadians)<GamePanel.levelWidth){
								xpos += Math.cos(angleInRadians);
							}
							if(ypos+Math.sin(angleInRadians)>0&&ypos+Math.sin(angleInRadians)<GamePanel.levelHeight){
								ypos += Math.sin(angleInRadians);
							}
							setPos(xpos,ypos);
							if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
								GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeBaiter((this));
								GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addBaiter((this));
							}
							moved=true;
						}
						if((int)xpos==(int)closestMate.xpos&&(int)ypos==(int)closestMate.ypos){
							this.currentHunger=5;
							if(this.currentRock!=null){
								GamePanel.levels.get(GamePanel.currentLevel).rocks.add(currentRock);
							}
							if(closestMate.currentRock!=null){
								GamePanel.levels.get(GamePanel.currentLevel).rocks.add(closestMate.currentRock);
							}
							closestMate.currentHunger=5;
							closestMate.currentNest.occupied=false;
							closestMate.closestMate=null;
							closestMate=null;
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)xpos/GamePanel.chunkSize][(int)ypos/GamePanel.chunkSize].addBaiter(new Baiter((int)xpos, (int)ypos,this.fishID));
						}
						//System.out.println("changed xpos to "+xpos+", changed ypos to "+ypos);
					}
				}
				//}
			}
		}
		else{//female
			if(currentNest==null){
				//check if there are any existing unoccupied nests
				for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).nests.size();i++){
					if(GamePanel.levels.get(GamePanel.currentLevel).nests.get(i).occupied==false&&GamePanel.levels.get(GamePanel.currentLevel).nests.get(i).nestID==1){
						currentNest=GamePanel.levels.get(GamePanel.currentLevel).nests.get(i);
						break;
					}
				}
				if(currentNest==null){
					currentNest = new Nest(GamePanel.randomNumber(0, GamePanel.levelWidth),GamePanel.randomNumber(0, GamePanel.levelHeight),1);
				}
				GamePanel.levels.get(GamePanel.currentLevel).nests.add(currentNest);
				//System.out.println("created a new nest");
			}
			//build the nest
			if(currentNest.currentRocks<currentNest.maxRocks&&currentRock==null){
				//find the nearest suitably sized rock
				//create a list of all rocks that are small enough for this to move
				ArrayList<Rock> liftableRocks = new ArrayList<Rock>();
				for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).rocks.size();i++){
					if(GamePanel.levels.get(GamePanel.currentLevel).rocks.get(i).size<=this.strength){
						liftableRocks.add(GamePanel.levels.get(GamePanel.currentLevel).rocks.get(i));
					}
				}
				//determine if there are any liftable rocks
				if(liftableRocks.size()>0){
					//System.out.println("there are liftable rocks");
					//determine which of the lift able rocks is closest to the nest

					closestRock = liftableRocks.get(0);
					for(int i = 0; i<liftableRocks.size();i++){
						double distanceToCurrent = Math.sqrt(Math.pow((liftableRocks.get(i).xpos-currentNest.xpos),2)+Math.pow((liftableRocks.get(i).ypos-currentNest.ypos),2));
						double distanceToCurrentClosest = Math.sqrt(Math.pow((closestRock.xpos-currentNest.xpos),2)+Math.pow((closestRock.ypos-currentNest.ypos),2));
						if(distanceToCurrent<distanceToCurrentClosest){
							closestRock = liftableRocks.get(i);
						}
					}
					//System.out.println("moving towards the closest rock");
					//move to the closest rock


					//calculate the direction this would have to go to find the rock
					int approachAngle = (int)Math.toDegrees(Math.atan2((closestRock.ypos-this.ypos),(closestRock.xpos-this.xpos)));
					if(approachAngle<0){
						approachAngle+=360;
					}

					desiredAngleInDegrees=approachAngle;
					this.changeDirection();
					angleInRadians = Math.toRadians(angleInDegrees);
					this.forwardAnim.setAngle(angleInRadians);
					if(closestRock!=null){
						double distanceToRock = Math.sqrt(Math.pow((xpos-closestRock.xpos),2)+Math.pow((ypos-closestRock.ypos),2));
						if(Math.abs(desiredAngleInDegrees-angleInDegrees)<=45&&distanceToRock<=30){
							angleInDegrees=desiredAngleInDegrees;
						}
					}
					if(true){
						Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
						if(xpos+Math.cos(angleInRadians)>0&&xpos+Math.cos(angleInRadians)<GamePanel.levelWidth){
							xpos += Math.cos(angleInRadians);
						}
						if(ypos+Math.sin(angleInRadians)>0&&ypos+Math.sin(angleInRadians)<GamePanel.levelHeight){
							ypos += Math.sin(angleInRadians);
						}
						setPos(xpos,ypos);
						if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeBaiter(this);
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addBaiter(this);
						}
					}
					//if this is at the closest rock
					if((int)this.xpos==closestRock.xpos&&(int)this.ypos==closestRock.ypos){
						//System.out.println("picked up the closest rock!");
						//set the closest rock to be this's current rock
						Rock temp = new Rock(closestRock.xpos,closestRock.ypos,closestRock.rockID);
						temp.size=closestRock.size;
						this.currentRock=temp;//closestRock;
						//remove the closest rock from the map
						GamePanel.levels.get(GamePanel.currentLevel).rocks.remove(closestRock);
					}


					//this.moveToPoint(new Point(closestRock.xpos,closestRock.ypos));

				}
				else{
					for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).nests.size();i++){
						if(GamePanel.levels.get(GamePanel.currentLevel).nests.get(i).occupied&&GamePanel.levels.get(GamePanel.currentLevel).nests.get(i)!=currentNest){
							GamePanel.levels.get(GamePanel.currentLevel).nests.get(i).takeRock();
							GamePanel.levels.get(GamePanel.currentLevel).rocks.add(new Rock(GamePanel.levels.get(GamePanel.currentLevel).nests.get(i).xpos, GamePanel.levels.get(GamePanel.currentLevel).nests.get(i).ypos, GamePanel.randomNumber(1, strength)));
						}
					}
				}
			}
			if(currentNest.currentRocks<currentNest.maxRocks&&currentRock!=null){
				//System.out.println("carrying the rock back to nest!");
				//carry the rock to the nest
				//calculate the direction this would have to go to find the nest
				int approachAngle = (int)Math.toDegrees(Math.atan2((currentNest.ypos-this.ypos),(currentNest.xpos-this.xpos)));
				if(approachAngle<0){
					approachAngle+=360;
				}

				desiredAngleInDegrees=approachAngle;
				this.changeDirection();
				angleInRadians = Math.toRadians(angleInDegrees);
				this.forwardAnim.setAngle(angleInRadians);
				if(currentNest!=null){
					double distanceToNest = Math.sqrt(Math.pow((xpos-currentNest.xpos),2)+Math.pow((ypos-currentNest.ypos),2));
					if(Math.abs(desiredAngleInDegrees-angleInDegrees)<=45&&distanceToNest<=30){
						angleInDegrees=desiredAngleInDegrees;
					}
				}
				if(true){
					Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
					if(xpos+Math.cos(angleInRadians)>0&&xpos+Math.cos(angleInRadians)<GamePanel.levelWidth){
						xpos += Math.cos(angleInRadians);
					}
					if(ypos+Math.sin(angleInRadians)>0&&ypos+Math.sin(angleInRadians)<GamePanel.levelHeight){
						ypos += Math.sin(angleInRadians);
					}
					setPos(xpos,ypos);
					if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
						GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeBaiter(this);
						GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addBaiter(this);
					}
				}
				//place the rock on the nest
				double distanceToNest = Math.sqrt(Math.pow((currentNest.xpos-this.xpos),2)+Math.pow((currentNest.ypos-this.ypos),2));
				if(distanceToNest<=20){//if((int)this.xpos==currentNest.xpos&&(int)this.ypos==currentNest.ypos){//
					//System.out.println("added the rock to the nest!");
					currentNest.currentRocks+=currentRock.size;
					currentRock=null;
					if(currentNest.currentRocks>currentNest.maxRocks){
						currentNest.currentRocks=currentNest.maxRocks;
					}
				}


				//this.moveToPoint(new Point(currentNest.xpos,currentNest.ypos));

			}
			double distanceToMate=151;
			if(closestMate!=null){
				distanceToMate = Math.sqrt(Math.pow((xpos-closestMate.xpos),2)+Math.pow((ypos-closestMate.ypos),2));
			}
			//if the current nest is completed
			if(currentNest.currentRocks==currentNest.maxRocks&&distanceToMate>150){
				currentNest.occupied=true;
				//calculate the direction this would have to go to find the nest
				int approachAngle = (int)Math.toDegrees(Math.atan2((currentNest.ypos-this.ypos),(currentNest.xpos-this.xpos)));
				if(approachAngle<0){
					approachAngle+=360;
				}

				desiredAngleInDegrees=approachAngle;
				this.changeDirection();
				angleInRadians = Math.toRadians(angleInDegrees);
				this.forwardAnim.setAngle(angleInRadians);
				if(currentNest!=null){
					double distanceToNest = Math.sqrt(Math.pow((xpos-currentNest.xpos),2)+Math.pow((ypos-currentNest.ypos),2));
					if(Math.abs(desiredAngleInDegrees-angleInDegrees)<=45&&distanceToNest<=30){
						angleInDegrees=desiredAngleInDegrees;
					}
				}
				if(true){
					Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
					if(xpos+Math.cos(angleInRadians)>0&&xpos+Math.cos(angleInRadians)<GamePanel.levelWidth){
						xpos += Math.cos(angleInRadians);
					}
					if(ypos+Math.sin(angleInRadians)>0&&ypos+Math.sin(angleInRadians)<GamePanel.levelHeight){
						ypos += Math.sin(angleInRadians);
					}
					setPos(xpos,ypos);
					if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
						GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeBaiter(this);
						GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addBaiter(this);
					}
				}
			}
			//this.moveToPoint(currentNest.xpos,currentNest.ypos);
		}

	}

	public double getDistanceBetween(Point a, Point b){
		//return (Math.pow((a.x-b.x),2)+Math.pow((a.y-b.y),2));
		return Math.sqrt(Math.pow((a.x-b.x),2)+Math.pow((a.y-b.y),2));
	}
	public void wanderRandomly(){
		if((int)xpos==moveDestination.x&&(int)ypos==moveDestination.y){
			moveDestination = new Point(GamePanel.randomNumber(0, GamePanel.levelWidth),GamePanel.randomNumber(0, GamePanel.levelHeight));
		}
		//rotate towards the destination
		desiredAngleInDegrees=(int)Math.toDegrees(Math.atan2((moveDestination.y-ypos),(moveDestination.x-xpos)));
		if(desiredAngleInDegrees < 0){
			desiredAngleInDegrees += 360;
		}
		this.changeDirection();
		angleInRadians = Math.toRadians(angleInDegrees);
		this.forwardAnim.setAngle(angleInRadians);
		if(GamePanel.randomNumber(1, 10)<=8){
			Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
			if(xpos+Math.cos(angleInRadians)>0&&xpos+Math.cos(angleInRadians)<GamePanel.levelWidth){
				xpos += Math.cos(angleInRadians);
			}
			if(ypos+Math.sin(angleInRadians)>0&&ypos+Math.sin(angleInRadians)<GamePanel.levelHeight){
				ypos += Math.sin(angleInRadians);
			}
			setPos(xpos,ypos);
			if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
				GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeBaiter(this);
				GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addBaiter(this);
			}
		}
	}
	public void update(){

		if((closestFood==null||closestFood.currentFoodValue==0)&&GamePanel.levels.get(GamePanel.currentLevel).plantCount>0){
			int randomx = GamePanel.randomNumber(0, ((GamePanel.levelWidth/GamePanel.chunkSize)-1));
			int randomy = GamePanel.randomNumber(0, ((GamePanel.levelHeight/GamePanel.chunkSize)-1));
			while(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[randomx][randomy].plants.size()<=0){
				randomx = GamePanel.randomNumber(0, ((GamePanel.levelWidth/GamePanel.chunkSize)-1));
				randomy = GamePanel.randomNumber(0, ((GamePanel.levelHeight/GamePanel.chunkSize)-1));
			}
			closestFood = GamePanel.levels.get(GamePanel.currentLevel).fishGrid[randomx][randomy].plants.get(GamePanel.randomNumber(0, GamePanel.levels.get(GamePanel.currentLevel).fishGrid[randomx][randomy].plants.size()-1));
		}
		//move away from hostile creatures
		//pick a visible point that is farthest from all the hostiles

		int hostilesCount = 0;

		hostilesCount = getVisibleHostiles();


		if(hostilesCount==0&&GamePanel.levels.get(GamePanel.currentLevel).plantCount==0){
			moveToPoint(anchor);
		}
		//if there are any hostiles visible
		if(hostilesCount>0){

			//calculate the direction this would have to go to escape the hostile
			int escapeAngle=0;
			Point targetLocation = this.getLanternPos();
			//rise = 61*sin(angle)
			//run = 61*cos(angle)
			//Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
			boolean runningAway = this.runThreshold<=hostilesCount;
			if(runningAway&&this.fishID!=2){
				if(GamePanel.randomNumber(1, 4)<=1){
					return;
				}
			}
			//run from hostile
			if(this.runThreshold<=hostilesCount&&this.fishID!=2){
				//escapeAngle = (int)Math.toDegrees(Math.atan2((closestHostile.ypos-targetLocation.y),(closestHostile.xpos-targetLocation.x)))-180;
				move(new Point((int)closestHostile.xpos,(int)closestHostile.ypos),false);
			}
			else{//attack hostile
				//escapeAngle = (int)Math.toDegrees(Math.atan2((closestHostile.ypos-targetLocation.y),(closestHostile.xpos-targetLocation.x)));
				move(new Point((int)closestHostile.xpos,(int)closestHostile.ypos),true);
			}
			//			if(escapeAngle<0){
			//				escapeAngle+=360;
			//			}
			//
			//			desiredAngleInDegrees=escapeAngle;
			//			this.changeDirection();
			//			double distanceToFood= Math.sqrt(Math.pow((closestFood.xpos-targetLocation.x),2)+Math.pow((closestFood.ypos-targetLocation.y),2));
			//			if(Math.abs(desiredAngleInDegrees-angleInDegrees)<=45&&distanceToFood<=40){
			//				angleInDegrees=desiredAngleInDegrees;
			//			}
			//			angleInRadians = Math.toRadians(angleInDegrees);
			//			this.forwardAnim.setAngle(angleInRadians);

			//			
			//			if(this.fishID!=2){
			//				if(xpos+Math.cos(angleInRadians)>0&&xpos+Math.cos(angleInRadians)<1920){
			//					xpos += Math.cos(angleInRadians);
			//				}
			//				if(ypos+Math.sin(angleInRadians)>0&&ypos+Math.sin(angleInRadians)<1080){
			//					ypos += Math.sin(angleInRadians);
			//				}
			//				setPos(xpos,ypos);
			//			}
			//			if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
			//				GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeBaiter(this);
			//				GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addBaiter(this);
			//			}

			if((int)targetLocation.x-6<=(int)closestHostile.xpos&&(int)targetLocation.x+6>=(int)closestHostile.xpos&&(int)targetLocation.y-6<=(int)closestHostile.ypos&&(int)targetLocation.y+6>=(int)closestHostile.ypos&&this.eatsMeat&&this.runThreshold>hostilesCount){
				closestHostile.takeDmg((int)this.attack,this);
				if(closestHostile.isDead){
					if(this.size<3)
						this.size+=.01;
					if(GamePanel.randomNumber(1, 3)==1){//33% chance to add another hostile baiter to the level upon killing a swarmer
						GamePanel.levels.get(GamePanel.currentLevel).addBaitersToFishGrid(1, (int)xpos, (int)ypos, 1);
					}
					forwardAnim.xScale=size;
					forwardAnim.yScale=size;
					this.attack+=.1;
					//this.turnSpeed=this.turnSpeed*.95;
					GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)closestHostile.xpos/GamePanel.chunkSize][(int)closestHostile.ypos/GamePanel.chunkSize].removeSwarmer(closestHostile);
					if(this.currentHunger<this.maxHunger){
						this.currentHunger+=5;
					}
					this.closestHostile=null;
				}
			}
			//System.out.println("changed xpos to "+xpos+", changed ypos to "+ypos);


		}
		//visible plants and no hostiles visible
		else if(GamePanel.levels.get(GamePanel.currentLevel).plantCount>50&&this.currentHunger!=this.maxHunger&&this.eatsPlants){

			//swim towards the plants
			move(new Point((int)closestFood.xpos,(int)closestFood.ypos),true);
			
						//if it caught the target
			if((int)xpos==(int)closestFood.xpos&&(int)ypos==(int)closestFood.ypos){
				closestFood.takeDmg(this.attack,this);
				this.currentHunger++;
				GamePanel.ripples.add(new Animation(GamePanel.rippleImg,6,80,0,(int)((this.xpos-20)*GamePanel.cameraScale),(int)((this.ypos-20)*GamePanel.cameraScale),false,0,0));
				
			}

		}
		else if(this.currentHunger==this.maxHunger){
			if(GamePanel.levels.get(GamePanel.currentLevel).baiterCount<3500){
				this.reproduce();
			}
			else{

				this.wanderRandomly();
			}
		}
		else{
			if(this.moveDestination==null){
				moveDestination = new Point(GamePanel.randomNumber(0, GamePanel.levelWidth),GamePanel.randomNumber(0, GamePanel.levelHeight));
			}

			this.wanderRandomly();

		}
		//move
		if(movingForward){
			changeDirection();
			moveForward();
		}
		//System.out.println("updated baiter!");
	}
	public void takeDmg(int dmg, Swarmer aggressor){
		GamePanel.ripples.add(new Animation(GamePanel.rippleImg,6,80,0,(int)this.getLanternPos().x,(int)this.getLanternPos().y,false,0,0));
		if(!GamePanel.godmodeMenu.isVisible){
			currentHealth-=dmg;
			if(this.eatsMeat){
				if(GamePanel.randomNumber(1, 100)<=10){
					aggressor.takeDmg(this.attack, this);
				}
			}
		}
		if(this.currentHealth<=0){
			this.isDead=true;
			//create blood
			for(int k = 0; k<runThreshold*4;k++){
				GamePanel.ripples.add(new Animation(GamePanel.bloodImg,10,GamePanel.randomNumber(200, 400),0,(int)this.xpos+GamePanel.randomNumber(-(runThreshold*2), (runThreshold*2)),
						(int)this.ypos+GamePanel.randomNumber(-(runThreshold*2), (runThreshold*2)),false,GamePanel.randomNumber(0, 1),GamePanel.randomNumber(0, 1)));
			}
			for(int k = 0; k<runThreshold;k++){
				if(GamePanel.levels.get(GamePanel.currentLevel).swarmerCount<100){
					GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)xpos/GamePanel.chunkSize][(int)ypos/GamePanel.chunkSize].addSwarmer((new Swarmer(xpos, ypos,aggressor.anchor.x,aggressor.anchor.y)));
				}
				else{
					GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.add(new Swarmer(xpos, ypos,aggressor.anchor.x,aggressor.anchor.y));
				}
			}
			GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)xpos/GamePanel.chunkSize][(int)ypos/GamePanel.chunkSize].removeBaiter((this));

			//GamePanel.levels.get(GamePanel.currentLevel).items.add(new Item((int)this.xpos,(int)this.ypos,0));
		}
		//if the player has beaten the current level
		if(GamePanel.levels.get(GamePanel.currentLevel).baiterCount==0&&!GamePanel.reseting){
			System.out.println("beat the level!");
			GamePanel.levelBeat=true;
			GamePanel.currentLevel++;
			GamePanel.startLevel();
		}
	}
	public void changeDirection(){//-1 means turn left 1 degree, 1 means turn right 1 degree
		double direction=0;
		if(angleInDegrees>desiredAngleInDegrees){
			double difference = angleInDegrees-desiredAngleInDegrees;

			if(difference<180){
				direction = -turnSpeed*AppletUI.delta;
			}
			else{
				direction = turnSpeed*AppletUI.delta;
			}
		}
		else{
			double difference = desiredAngleInDegrees-angleInDegrees;
			if(difference<180){
				direction=turnSpeed*AppletUI.delta;
			}
			else{
				direction=-turnSpeed*AppletUI.delta;
			}
		}
		if(this.desiredAngleInDegrees!=this.angleInDegrees){
			angleInDegrees+=direction;
			if(angleInDegrees < 0){
				angleInDegrees += 360;
			}
			else if(angleInDegrees>360){
				angleInDegrees-=360;
			}
		}
		else if(this.desiredAngleInDegrees!=this.angleInDegrees&&Math.abs(desiredAngleInDegrees-angleInDegrees)<=this.turnSpeed){
			angleInDegrees=desiredAngleInDegrees;
		}
	}
	public void biteNearbyFish(){

	}
	
	public void moveForward(){
		boolean collided = false;
		//stop if it it has reached it's destination
		if((int)this.xpos==(int)this.moveDestination.x&&(int)this.ypos==(int)this.moveDestination.y||this.xpos<=0||this.xpos>=GamePanel.levelWidth||this.ypos<=0||this.ypos>=GamePanel.levelHeight){
			collided=true;
			//System.out.println("reached destination");
		}
		//if not then move forward
		if(!collided){
			desiredAngleInDegrees=(int)Math.toDegrees(Math.atan2((moveDestination.y-ypos),(moveDestination.x-xpos)));
			if(desiredAngleInDegrees < 0){
				desiredAngleInDegrees += 360;
			}
			this.changeDirection();
			angleInRadians = Math.toRadians(angleInDegrees);
			this.forwardAnim.setAngle(angleInRadians);
			Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
			if(xpos+Math.cos(angleInRadians)>0&&xpos+Math.cos(angleInRadians)<GamePanel.levelWidth){
				xpos += Math.cos(angleInRadians);
			}
			if(ypos+Math.sin(angleInRadians)>0&&ypos+Math.sin(angleInRadians)<GamePanel.levelHeight){
				ypos += Math.sin(angleInRadians);
			}

			setPos(xpos,ypos);
			if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
				GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeBaiter(this);
				GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addBaiter(this);
			}
		}
		else{
			this.movingForward=false;
			forwardAnim.delay=250;
		}
	}
	public void moveToPoint(Point destination){
		movingForward=false;
		//rotate towards the destination
		desiredAngleInDegrees=(int)Math.toDegrees(Math.atan2((destination.y-ypos),(destination.x-xpos)));
		if(desiredAngleInDegrees < 0){
			desiredAngleInDegrees += 360;
		}
		this.changeDirection();
		angleInRadians = Math.toRadians(angleInDegrees);
		//move forward
		moveDestination = destination;
		movingForward=true;
		forwardAnim.delay=125;
	}
	public void move(Point dest,boolean movingTowardsPoint){
		//moveDestination = dest;
		//rotate towards the destination
		if(movingTowardsPoint){
			desiredAngleInDegrees=Math.toDegrees(Math.atan2((dest.y-ypos),(dest.x-xpos)));
		}
		else{
			desiredAngleInDegrees=(Math.toDegrees(Math.atan2((dest.y-ypos),(dest.x-xpos)))-180);
		}
		if(desiredAngleInDegrees < 0){
			desiredAngleInDegrees += 360;
		}
		//moveDestination = dest;
		this.changeDirection();
		angleInRadians = Math.toRadians(angleInDegrees);
		this.forwardAnim.setAngle(angleInRadians);
		//move forward

		Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
		int leftBorder = 0;//-((GamePanel.levelWidth-1920)/2);
		int rightBorder = 1919;//((GamePanel.levelWidth-1920)/2);
		int topBorder = 0;//-((GamePanel.levelHeight-1920)/2);
		int bottomBorder = 1079;//+((GamePanel.levelHeight-1080)/2);
		if(xpos+(Math.cos(angleInRadians)*AppletUI.delta)>leftBorder&&xpos+(Math.cos(angleInRadians)*AppletUI.delta)<rightBorder){
			xpos += (Math.cos(angleInRadians)*AppletUI.delta);
		}
		if(ypos+(Math.sin(angleInRadians)*AppletUI.delta)>topBorder&&ypos+(Math.sin(angleInRadians)*AppletUI.delta)<bottomBorder){
			ypos += (Math.sin(angleInRadians)*AppletUI.delta);
		}
		setPos(xpos,ypos);
		if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
			GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeBaiter(this);
			GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addBaiter(this);
		}
	}
	public void setPos(double x, double y){
		//set the position of this swarmer(used for collisions and stuff)
		this.xpos=x;
		this.ypos=y;
		//set the position of this swarmer's forward moving animation
		this.forwardAnim.xpos=(int)x;
		this.forwardAnim.ypos=(int)y;
	}
	public void Draw(Graphics g){

		forwardAnim.Draw(g);
		//draw the rock this is carrying if it has one
		if(this.currentRock!=null){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			AffineTransform at = new AffineTransform();
			at.translate(xpos-16, ypos-currentRock.size);
			at.scale((double)currentRock.size/10, (double)currentRock.size/10);
			//at.rotate(angleInRadians,xpos-16, ypos-currentRock.size);

			g2d.drawImage(GamePanel.rockImg[currentRock.rockID][0], at, null);


			//g.drawImage(GamePanel.rockImg[currentRock.rockID][0],(int)(this.xpos-currentRock.size),(int)(this.ypos-currentRock.size),currentRock.size*2,currentRock.size*2,null);
		}
		//draw hearts
		if(this.currentHunger==this.maxHunger){//&&GamePanel.levels.get(GamePanel.currentLevel).baiters.size()<3500){
			if(this.gender=="Female"){
				if(this.fishID==0){
					g.drawImage(GamePanel.heart[0][0],(int)((int)this.xpos*GamePanel.cameraScale),(int)(((int)this.ypos-10)*GamePanel.cameraScale),(int)(10*GamePanel.cameraScale),(int)(10*GamePanel.cameraScale),null);
				}
				else if(this.fishID==1){
					g.drawImage(GamePanel.heart[0][0],(int)((int)this.xpos*GamePanel.cameraScale),(int)(((int)this.ypos-10)*GamePanel.cameraScale),(int)(20*GamePanel.cameraScale),(int)(20*GamePanel.cameraScale),null);
				}
			}
			else{
				if(this.fishID==0){
					g.drawImage(GamePanel.heart[1][0],(int)((int)this.xpos*GamePanel.cameraScale),(int)(((int)this.ypos-10)*GamePanel.cameraScale),(int)(10*GamePanel.cameraScale),(int)(10*GamePanel.cameraScale),null);
				}
				else if(this.fishID==1){
					g.drawImage(GamePanel.heart[1][0],(int)((int)this.xpos*GamePanel.cameraScale),(int)(((int)this.ypos-10)*GamePanel.cameraScale),(int)(20*GamePanel.cameraScale),(int)(20*GamePanel.cameraScale),null);
				}
			}
		}
		//draw health bars
		if(this.currentHealth<this.maxHealth){
			g.drawImage(GamePanel.healthBarBackgroun,(int)((this.xpos-3)*GamePanel.cameraScale),(int)((this.ypos+16)*GamePanel.cameraScale),(int)(20*GamePanel.cameraScale),(int)(4*GamePanel.cameraScale),null);
			for(int i = 0; i<(currentHealth/maxHealth)*20;i++){
				if((currentHealth/maxHealth)*10>7){
					//green
					g.drawImage(GamePanel.bars[0][0],(int)((this.xpos-3+i)*GamePanel.cameraScale),(int)((this.ypos+16)*GamePanel.cameraScale),(int)(1*GamePanel.cameraScale),(int)(4*GamePanel.cameraScale),null);
				}
				else if((currentHealth/maxHealth)*10<=7&&(currentHealth/maxHealth)*10>3){
					//yellow
					g.drawImage(GamePanel.bars[1][0],(int)((this.xpos-3+i)*GamePanel.cameraScale),(int)((this.ypos+16)*GamePanel.cameraScale),(int)(1*GamePanel.cameraScale),(int)(4*GamePanel.cameraScale),null);
				}
				else if((currentHealth/maxHealth)*10<=3){
					//red
					g.drawImage(GamePanel.bars[2][0],(int)((this.xpos-3+i)*GamePanel.cameraScale),(int)((this.ypos+16)*GamePanel.cameraScale),(int)(1*GamePanel.cameraScale),(int)(4*GamePanel.cameraScale),null);
				}
			}
		}
	}
}
