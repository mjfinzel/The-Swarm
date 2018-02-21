package Game;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Swarmer {
	public double xpos;
	public double ypos;
	public double moveSpeed = 2;
	public double turnSpeed = 2;
	public double size=.5;//percent increased size of the fish
	double currentHealth = 2;
	double maxHealth = 2;
	BufferedImage [][]body = GamePanel.swarmerForwardAnim;
	double angleInRadians = 0;
	double angleInDegrees=0;
	double desiredAngleInDegrees=0;
	boolean movingForward = false;
	Point moveDestination;
	Animation forwardAnim;
	int visionRadius = 210;
	int currentHunger;
	int maxHunger;
	long lastHungerUpdateTime = System.currentTimeMillis();
	Point anchor = new Point(0,0);
	double attack = 1;
	long lastMoveTime = System.currentTimeMillis();
	int moveUpdateSpeed = 400;
	Baiter closestFood;
	boolean isDead = false;
	boolean isBeingChased = false;
	ArrayList <Baiter>visibleBaiters = new ArrayList<Baiter>();
	public Swarmer(double x, double y, int anchorX, int anchorY){
		xpos=x;
		ypos=y;
		forwardAnim = new Animation(body, 4, 500/(int)moveSpeed, 0, (int)xpos, (int)ypos, true, 0, 0);
		forwardAnim.xScale=size;
		forwardAnim.yScale=size;
		anchor.x=anchorX;
		anchor.y=anchorY;
		maxHunger = 10;
		currentHunger =GamePanel.randomNumber(8, 10);
	}
	public void update(){
		//System.out.println("update called!");
		//double distanceFromAnchor = Math.sqrt(Math.pow((xpos-anchor.x),2)+Math.pow((ypos-anchor.y),2));
		moveUpdateSpeed=(int)(400/size);
		//System.out.println("update 1");
		//update hunger
		if(lastHungerUpdateTime+36000<System.currentTimeMillis()){
			lastHungerUpdateTime=System.currentTimeMillis();
			currentHunger--;
			if(currentHunger==0){
				GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)xpos/GamePanel.chunkSize][(int)ypos/GamePanel.chunkSize].removeSwarmer(this);
			}
		}

		//move
		if(movingForward){
			changeDirection();
			moveForward();
			//System.out.println("moving forwards");
		}
		else if((GamePanel.swarmerAI==2||(GamePanel.swarmerAI==1&&this.currentHunger<this.maxHunger))){
			//System.out.println("getting visible baiters");
			ArrayList<Baiter> foodFish = getVisibleBaiters();
			//System.out.println("number of visible baiters: "+foodFish.size());
			//if there are any hostiles visible
			if(foodFish.size()>0||closestFood!=null){
				//this.movingForward=false;
				//System.out.println("detected a hostile!");

				//				if(closestFood==null){
				//					closestFood = foodFish.get(0);
				//					//determine which baiter is closest to this
				//					for(int i = 0;i<foodFish.size();i++){
				//						double distanceToCurrent = Math.sqrt(Math.pow((GamePanel.levels.get(GamePanel.currentLevel).baiters.get(i).xpos-this.xpos),2)+Math.pow((GamePanel.levels.get(GamePanel.currentLevel).baiters.get(i).ypos-this.ypos),2));
				//						double distanceToCurrentClosest = Math.sqrt(Math.pow((closestFood.xpos-this.xpos),2)+Math.pow((closestFood.ypos-this.ypos),2));
				//						if(distanceToCurrent<distanceToCurrentClosest){
				//							closestFood = foodFish.get(i);
				//						}
				//					}
				//				}
				for(int i = 0;i<moveSpeed&&closestFood!=null;i++){
					Point targetLocation = new Point ((int)(double)(closestFood.xpos+((closestFood.forwardAnim.spriteSheet[0][0].getWidth()/2)*closestFood.size)),(int)closestFood.ypos);
					if(closestFood.fishID==2){
						targetLocation = closestFood.getLanternPos();
					}
					//calculate the direction this would have to go to attack the food

					int	attackAngle = (int)Math.toDegrees(Math.atan2((targetLocation.y-this.ypos),((targetLocation.x-this.xpos))));

					if(attackAngle<0){
						attackAngle+=360;
					}
					desiredAngleInDegrees=attackAngle;
					this.changeDirection();
					angleInRadians = Math.toRadians(angleInDegrees);
					this.forwardAnim.setAngle(angleInRadians);
					double distanceToFood= Math.sqrt(Math.pow((closestFood.xpos-xpos),2)+Math.pow((closestFood.ypos-ypos),2));
					if(Math.abs(desiredAngleInDegrees-angleInDegrees)<=45&&distanceToFood<=20){
						angleInDegrees=desiredAngleInDegrees;
					}
					boolean go = true;
					if(this.isBeingChased){
						if(GamePanel.randomNumber(1, 6)==1){
							go=false;
						}
					}
					if(go){
						Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
						if(xpos+Math.cos(angleInRadians)>0&&xpos+Math.cos(angleInRadians)<GamePanel.levelWidth&&ypos+Math.sin(angleInRadians)>0&&ypos+Math.sin(angleInRadians)<GamePanel.levelHeight){
							xpos += Math.cos(angleInRadians);
							ypos += Math.sin(angleInRadians);
							//System.out.println("moving1");
							setPos(xpos,ypos);
						}
						if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addSwarmer(this);
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeSwarmer(this);
						}
					}
					//System.out.println("changed xpos to "+xpos+", changed ypos to "+ypos);
					//if it caught the target

					if((int)xpos==targetLocation.x&&(int)ypos==targetLocation.y){
						closestFood.takeDmg((int)this.attack,this);
						if(closestFood.isDead){
							if(this.size<2){
								this.size+=.05;
								forwardAnim.xScale=size;
								forwardAnim.yScale=size;
								this.visionRadius+=1;
								this.attack+=.1;
								if(this.currentHealth<this.maxHealth){
									this.currentHealth++;
								}
								else{
									this.maxHealth++;
									this.currentHealth++;
								}
							}
							//this.turnSpeed=this.turnSpeed*.95;
							closestFood=null;
							if(this.currentHunger<this.maxHunger){
								this.currentHunger++;
							}
						}
					}
				}
			}
			else{
				if(lastMoveTime+moveUpdateSpeed<System.currentTimeMillis()&&foodFish.size()==0){
					lastMoveTime=System.currentTimeMillis();
					int packSize = GamePanel.levels.get(GamePanel.currentLevel).swarmerCount;
					int destVariation = packSize*3;
					if(destVariation>100){
						destVariation=100;
					}
					Point dest = new Point(anchor.x+GamePanel.randomNumber(-destVariation, destVariation),anchor.y+GamePanel.randomNumber(-destVariation, destVariation));
					moveToPoint(dest);
				}
			}

		}
		else{
			if(lastMoveTime+moveUpdateSpeed<System.currentTimeMillis()){
				lastMoveTime=System.currentTimeMillis();
				int packSize = GamePanel.levels.get(GamePanel.currentLevel).swarmerCount;
				int destVariation = packSize*3;
				if(destVariation>100){
					destVariation=100;
				}
				Point dest = new Point(anchor.x+GamePanel.randomNumber(-destVariation, destVariation),anchor.y+GamePanel.randomNumber(-destVariation, destVariation));
				moveToPoint(dest);
			}
		}
		//System.out.println("update 2");
		//System.out.println("finished update");
	}
	public int getNumberOfVisibleHelpers(){
		int count = 1;
		for(int k = 0; k<GamePanel.levelWidth/GamePanel.chunkSize; k++){
			for(int j = 0; j<GamePanel.levelHeight/GamePanel.chunkSize;j++){
				for(int i=0; i<GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.size();i++){
					//check if any are within vision range
					if((Math.sqrt(Math.pow((GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.get(i).xpos-this.xpos),2)+Math.pow((GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.get(i).ypos-this.ypos),2)))<=this.visionRadius){
						count++;
					}
				}
			}

		}
		return count;
	}
	public ArrayList<Baiter> getVisibleBaiters(){
		this.isBeingChased=false;
		ArrayList<Baiter> food = new ArrayList<Baiter>();
		
		double distanceToCurrentClosest=-1;
		for(int i = (int)(this.xpos/GamePanel.chunkSize)-((this.visionRadius/GamePanel.chunkSize)+1); i<(this.xpos/GamePanel.chunkSize)+((this.visionRadius/GamePanel.chunkSize)+1);i++){
			
			//y positions
			for(int j = (int)(this.ypos/GamePanel.chunkSize)-((this.visionRadius/GamePanel.chunkSize)+1); j<(this.ypos/GamePanel.chunkSize)+((this.visionRadius/GamePanel.chunkSize)+1);j++){
				
				if(i<GamePanel.levelWidth/GamePanel.chunkSize&&i>0&&j<GamePanel.levelHeight/GamePanel.chunkSize&&j>0){
					
					//loop through all the baiters in this arraylist
					for(int k = 0; k<GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.size();k++){
						if((Math.sqrt(Math.pow((GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.get(k).xpos-this.xpos),2)+Math.pow((GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.get(k).ypos-this.ypos),2)))<=this.visionRadius){
							double distanceToCurrent = Math.sqrt(Math.pow((this.xpos-GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.get(k).xpos),2)+Math.pow((this.ypos-GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.get(k).ypos),2));
							if(distanceToCurrent<distanceToCurrentClosest||distanceToCurrentClosest==-1){
								if(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.get(k).fishID<3&&getNumberOfVisibleHelpers()>=GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.get(k).runThreshold){
								closestFood=GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.get(k);
								distanceToCurrentClosest = Math.sqrt(Math.pow((closestFood.xpos-this.xpos),2)+Math.pow((closestFood.ypos-this.ypos),2));
								}
							}
							//add this baiter to the food list
							food.add(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.get(k));
							//System.out.println("swarmer found food!");
						}
					}
				}

			}
		}
		return food;
	}
	public void changeDirection(){//-1 means turn left 1 degree, 1 means turn right 1 degree
		double direction=0;
		if(angleInDegrees>desiredAngleInDegrees){
			double difference = angleInDegrees-desiredAngleInDegrees;

			if(difference<180){
				direction = -turnSpeed;
			}
			else{
				direction = turnSpeed;
			}
		}
		else{
			double difference = desiredAngleInDegrees-angleInDegrees;
			if(difference<180){
				direction=turnSpeed;
			}
			else{
				direction=-turnSpeed;
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
	public void moveToPoint(Point destination){
		//System.out.println("move to point called!");
		if(destination.x>GamePanel.levelWidth){
			destination.x=GamePanel.levelWidth;
		}
		if(destination.x<0){
			destination.x=0;
		}
		if(destination.y>GamePanel.levelHeight){
			destination.y=GamePanel.levelHeight;
		}
		if(destination.y<0){
			destination.y=0;
		}
		movingForward=false;
		//rotate towards the destination
		//toa = tangent = opposite over adjacent
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
		
		this.changeDirection();
		angleInRadians = Math.toRadians(angleInDegrees);
		this.forwardAnim.setAngle(angleInRadians);
		//move forward

		Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
		if(xpos+(Math.cos(angleInRadians)*AppletUI.delta)>0&&xpos+(Math.cos(angleInRadians)*AppletUI.delta)<GamePanel.levelWidth){
			xpos += (Math.cos(angleInRadians)*AppletUI.delta);
		}
		if(ypos+(Math.sin(angleInRadians)*AppletUI.delta)>0&&ypos+(Math.sin(angleInRadians)*AppletUI.delta)<GamePanel.levelHeight){
			ypos += (Math.sin(angleInRadians)*AppletUI.delta);
		}
		setPos(xpos,ypos);
		if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
			GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeSwarmer(this);
			GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(this.xpos/GamePanel.chunkSize)][(int)(this.ypos/GamePanel.chunkSize)].addSwarmer(this);
		}
	}
	public void moveForward(){
		
		boolean collided = false;
		//calculate where this swarmer will be after moving forwards
		//check if it will collide with any terrain
		//loop through all of this swarmers positions and check if any equal the position of an invalid terrain position
		//stop if it it has reached it's destination
		for(int i = 0; i<this.moveSpeed;i++){
			if((int)this.xpos==(int)this.moveDestination.x&&(int)this.ypos==(int)this.moveDestination.y){
				collided=true;
				
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
				//if the distance between this and the destination is greater than the distance it would take to rotate to face the destination
				double distanceToDestination = Math.sqrt(Math.pow((moveDestination.x-this.xpos),2)+Math.pow((moveDestination.y-this.ypos),2));
				double minimumRotationMovement = Math.abs(desiredAngleInDegrees-angleInDegrees);
				Point temp = new Point((int)(this.xpos/GamePanel.chunkSize),(int)(this.ypos/GamePanel.chunkSize));
				Swarmer tempFish = this;
				if(distanceToDestination>=minimumRotationMovement){
					if(xpos+(Math.cos(angleInRadians)*AppletUI.delta)>0&&xpos+(Math.cos(angleInRadians)*AppletUI.delta)<GamePanel.levelWidth){
						xpos += (Math.cos(angleInRadians)*AppletUI.delta);
					}
					if(ypos+(Math.sin(angleInRadians)*AppletUI.delta)>0&&ypos+(Math.sin(angleInRadians)*AppletUI.delta)<GamePanel.levelHeight){
						ypos += (Math.sin(angleInRadians)*AppletUI.delta);
					}
				}
				else{
					
					if(distanceToDestination<=5){
						this.movingForward=false;
						if(moveDestination.x<=GamePanel.levelWidth-1&&moveDestination.x>=0&&moveDestination.y<=GamePanel.levelHeight-1&&moveDestination.y>=0)
							setPos(moveDestination.x,moveDestination.y);
					}
				}

				//rotate to face the destination before moving
				setPos(xpos,ypos);
				
				
				if(temp.x!=(int)(this.xpos/GamePanel.chunkSize)||temp.y!=(int)(this.ypos/GamePanel.chunkSize)){
					GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(xpos/GamePanel.chunkSize)][(int)(ypos/GamePanel.chunkSize)].addSwarmer(this);
					GamePanel.levels.get(GamePanel.currentLevel).fishGrid[temp.x][temp.y].removeSwarmer(this);
				}
			}
			else{
				this.movingForward=false;
				forwardAnim.delay=500;
			}
		}
	}
	public void takeDmg(int dmg, Baiter aggressor){
		GamePanel.ripples.add(new Animation(GamePanel.swarmerDust,10,80,0,(int)this.xpos,(int)this.ypos,false,0,0));
		if(!GamePanel.godmodeMenu.isVisible){
			currentHealth-=dmg;
		}
		if(currentHealth<=0){
			isDead=true;


			GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)xpos/GamePanel.chunkSize][(int)ypos/GamePanel.chunkSize].removeSwarmer(this);

			aggressor.runThreshold++;
		}
		//if the player has lost, draw "you lose" and let them restart the level
		if(GamePanel.levels.get(GamePanel.currentLevel).swarmerCount==0&&GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.size()==0){
			GamePanel.levelLost=true;
			Button retry = new Button(885, 500, 16);
			Button viewScore = new Button(885, 540, 17);
			Button exitGame = new Button(885, 580, 18);
			GamePanel.buttons.add(retry);
			GamePanel.buttons.add(viewScore);
			GamePanel.buttons.add(exitGame);	
		}
	}
	public void setPos(double x, double y){
		if(x>GamePanel.levelWidth){
			x=GamePanel.levelWidth;
		}
		if(y>GamePanel.levelHeight){
			y=GamePanel.levelHeight;
		}
		if(x<0){
			x=0;
		}
		if(y<0){
			y=0;
		}
		//set the position of this swarmer(used for collisions and stuff)
		this.xpos=x;
		this.ypos=y;
		//set the position of this swarmer's forward moving animation
		this.forwardAnim.xpos=(int)x;
		this.forwardAnim.ypos=(int)y;
	}
	public void Draw(Graphics g){
		Graphics2D g2d = (Graphics2D) g;

		AffineTransform old = g2d.getTransform();
		//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//g.drawImage(body,this.xpos*32,(this.ypos*32)-16,32,48,null);

		//		AffineTransform at = new AffineTransform();
		//		BufferedImage curFrame = this.forwardAnim.spriteSheet[this.forwardAnim.getCurrentFrame()][0];
		//		at.rotate(angleInRadians);

		//this.forwardAnim.setAngle(angleInDegrees);
		this.forwardAnim.Draw(g2d);
		//set the graphics to not be rotated after drawing this
		g2d.setTransform(old);
		//draw health bars
		if(this.currentHealth<this.maxHealth){
			g.drawImage(GamePanel.healthBarBackgroun,(int)this.xpos-3,(int)this.ypos+16,20,4,null);
			for(int i = 0; i<(currentHealth/maxHealth)*20;i++){
				if((currentHealth/maxHealth)*10>7){
					//green
					g.drawImage(GamePanel.bars[0][0],(int)this.xpos-3+i,(int)this.ypos+16,1,4,null);
				}
				else if((currentHealth/maxHealth)*10<=7&&(currentHealth/maxHealth)*10>3){
					//yellow
					g.drawImage(GamePanel.bars[1][0],(int)this.xpos-3+i,(int)this.ypos+16,1,4,null);
				}
				else if((currentHealth/maxHealth)*10<=3){
					//red
					g.drawImage(GamePanel.bars[2][0],(int)this.xpos-3+i,(int)this.ypos+16,1,4,null);
				}
			}
		}
		if(this.currentHunger<=3&&this.currentHunger>0){
			g.drawImage(GamePanel.hungerBubbles[0][3-currentHunger],(int)this.xpos-20,(int)this.ypos-30,30,30,null);
		}
	}
}
