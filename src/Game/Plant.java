package Game;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import Game.GamePanel;


public class Plant {
	double xpos;
	double ypos;
	int plantID;
	BufferedImage[][] artWork;
	int angleInDegrees = 0;
	int defaultAngleInDegrees=0;
	int maxSway = 10;//maximum angle the plant can rotate to the left or right
	int swayDirection = 1;//direction the plant should sway
	int swaySpeed = 200;
	int growSpeed = 1000;
	long lastGrowTime = System.currentTimeMillis();
	int currentStage = 0;
	long lastUpdateTime = System.currentTimeMillis();
	double currentFoodValue;//current health and how much food this plant can yield from being eaten
	double maxFoodValue;//max health and how much food this plant can yield total from being eaten
	public Plant(double x, double y, int ID){
		this.xpos=x;
		this.ypos=y;
		this.plantID=ID;
		if(this.plantID==0){
			this.artWork=GamePanel.kelpImg;
			maxFoodValue=10;
			currentFoodValue=maxFoodValue;
		}
	}
	public void update(){
		if(lastUpdateTime+swaySpeed<=System.currentTimeMillis()){
			lastUpdateTime=System.currentTimeMillis();
			if(angleInDegrees<defaultAngleInDegrees-maxSway){//swayed all the way to the left
				swayDirection=1;//sway right
			}
			if(angleInDegrees>defaultAngleInDegrees+maxSway){//swayed all the way to the right
				swayDirection=-1;//sway left
			}
			angleInDegrees+=swayDirection;//sway

		}
		//if the plant has had enough time to regenerate
		if(lastGrowTime+growSpeed<=System.currentTimeMillis()){
			//if the plant is currently damaged
			if(currentStage>0){
				//grow
				currentStage--;
			}
			if(this.currentStage==0){
				//reproduce
				if(GamePanel.levels.get(GamePanel.currentLevel).plantCount<GamePanel.levels.get(GamePanel.currentLevel).plantLimit){
					this.reproduce();
				}
			}
			lastGrowTime=System.currentTimeMillis();
		}
	}
	public void reproduce(){
		int direction = GamePanel.randomNumber(1, 4);
		int upOrDown = 0;
		int leftOrRight = 0;
		int tries = 0;
		Plant temp = new Plant(this.xpos+GamePanel.randomNumber(-40, 40),this.ypos+GamePanel.randomNumber(-60, 60),0);
		double distanceToCurrentClosest = Math.sqrt(Math.pow((temp.xpos-this.xpos),2)+Math.pow((temp.ypos-this.ypos),2));
		boolean added = false;
		while(distanceToCurrentClosest<30&&tries<150&&temp.xpos<100&&temp.xpos>1820&&temp.ypos<100&&temp.ypos>980){
			tries++;
			temp = new Plant(this.xpos+GamePanel.randomNumber(-40, 40),this.ypos+GamePanel.randomNumber(-60, 60),0);
			if(temp.xpos/GamePanel.chunkSize<0){
				temp.xpos=0;
			}
			if(temp.ypos/GamePanel.chunkSize<0){
				temp.ypos=0;
			}
			if(temp.xpos/GamePanel.chunkSize>15){
				temp.xpos=15;
			}
			if(temp.ypos/GamePanel.chunkSize>8){
				temp.ypos=8;
			}
			distanceToCurrentClosest = Math.sqrt(Math.pow((temp.xpos-this.xpos),2)+Math.pow((temp.ypos-this.ypos),2));
		}
		temp.currentStage=9;
		//System.out.println("ypos: "+ypos+"," +"xpos: "+xpos);
		GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)(temp.xpos/GamePanel.chunkSize)][(int)(temp.ypos/GamePanel.chunkSize)].addPlant(temp);
	}
	public boolean isPlantAtLocation(double x, double y){
		for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)x/GamePanel.chunkSize][(int)y/GamePanel.chunkSize].plants.size();i++){
			if(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)x/GamePanel.chunkSize][(int)y/GamePanel.chunkSize].plants.get(i).xpos==x&&GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)x/GamePanel.chunkSize][(int)y/GamePanel.chunkSize].plants.get(i).ypos==y){
				return true;
			}
		}
		return false;
	}
	public void takeDmg(int dmg, Baiter aggressor){
		currentFoodValue-=dmg;
		if(this.currentFoodValue<=0){
			GamePanel.levels.get(GamePanel.currentLevel).fishGrid[(int)xpos/GamePanel.chunkSize][(int)ypos/GamePanel.chunkSize].plants.remove(this);
			aggressor.closestFood=null;
			//System.out.println("plant died!");
		}
		currentStage = (int)((currentFoodValue/maxFoodValue)*10);
		if(((currentFoodValue/maxFoodValue)*10)%1!=0&&currentStage<9){
			currentStage = (int)((currentFoodValue/maxFoodValue)*10)+1;
		}
		if(currentStage<0){
			currentStage=0;
		}
	}
	public void Draw(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform at = new AffineTransform();
		at.translate((xpos-12)*GamePanel.cameraScale, (ypos-8)*GamePanel.cameraScale);
		at.scale(GamePanel.cameraScale,GamePanel.cameraScale);
		at.rotate(Math.toRadians(angleInDegrees),13,44);

		g2d.drawImage(artWork[currentStage][0], at, null);
	}
}
