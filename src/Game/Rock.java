package Game;

import java.awt.Graphics;

public class Rock {
	int xpos;
	int ypos;
	int rockID;
	int size = 1;
	public Rock(int x, int y, int scale){
		xpos = x;
		ypos = y;
		size = scale;
		rockID=GamePanel.randomNumber(0, 9);
	}
	public void Draw(Graphics g){
		g.drawImage(GamePanel.rockImg[rockID][0],(int)((this.xpos-size)*GamePanel.cameraScale),(int)((this.ypos-size)*GamePanel.cameraScale),(int)(size*2*GamePanel.cameraScale),(int)(size*2*GamePanel.cameraScale),null);
	}
}
