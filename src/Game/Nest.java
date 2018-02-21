package Game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Nest {
	int xpos;
	int ypos;
	int nestID;
	int currentRocks;
	int maxRocks;
	public boolean occupied = false;
	BufferedImage artwork = Images.load("/Textures/Nest.png");
	public Nest(int x, int y, int id){
		xpos=x;
		ypos=y;
		nestID=id;
		currentRocks = 3;
		//swarmer
		if(nestID==0){
			maxRocks=20;
		}
		//baiter
		else if(nestID==1){
			maxRocks=30;
		}
	}
	public void addRock(){
		if(currentRocks<maxRocks){
			currentRocks++;
		}
	}
	public void takeRock(){

		currentRocks--;
		if(currentRocks<=0){
			GamePanel.levels.get(GamePanel.currentLevel).nests.remove(this);
		}
	}
	public void Draw(Graphics g){
		//System.out.println("drawing nest!");
		if(this.currentRocks>0)
			g.drawImage(artwork,this.xpos-(currentRocks),this.ypos-(currentRocks),currentRocks*2,currentRocks*2,null);
	}
}
