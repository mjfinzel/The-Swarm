package Game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Item {
	int xpos;
	int ypos;
	int itemID;
	int imgScale = 0;
	String Name;
	BufferedImage artwork;
	long lastUpdateTime = System.currentTimeMillis();
	int updateRate = 500;//half a second
	public Item(int x, int y, int id){
		xpos = x;
		ypos = y;
		itemID=id;
		if(itemID==0){
			Name = "MeatChunk";
			artwork = GamePanel.meatChunk;
		}
		
	}
	public void update(){
		if(lastUpdateTime+updateRate<System.currentTimeMillis()){
			imgScale++;
			if(imgScale>1){
				imgScale=0;
			}
			lastUpdateTime=System.currentTimeMillis();
		}
	}
	public void Draw(Graphics g){
		g.drawImage(artwork,this.xpos-imgScale,this.ypos-imgScale,16+(imgScale*2),16+(imgScale*2),null);
	}
}
