package Game;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Animation{
	public BufferedImage[][]spriteSheet;

	private int frameCount=0;
	int xpos =0;
	int ypos =0;
	double xScale = 1;
	double yScale = 1;
	int xmove;
	int ymove;
	double angle = 0;
	int delay = 0;
	int index = 0;
	private int currentFrame = 0;
	int cutWidth = 0;
	private long timeForNextFrame=0;
	private long LastFrameTime = 0;
	boolean additive = false;
	boolean loopAnim = false;
	public Animation(BufferedImage[][] sprites, int numFrames, int frameDelay, int spriteIndex, int x, int y, boolean loop, int xIncreasePerFrame, int yIncreasePerFrame){
		xmove = xIncreasePerFrame;
		ymove = yIncreasePerFrame;

		spriteSheet=sprites;
		index = spriteIndex;
		frameCount=numFrames;
		delay=frameDelay;
		xpos=x;
		ypos=y;
		loopAnim=loop;
	}

	public void update(){

		if(this.timeForNextFrame <= System.currentTimeMillis()){
			this.currentFrame++;
			ypos=ypos+xmove;
			xpos=xpos+ymove;
			if(this.currentFrame>=this.frameCount){
				if(loopAnim){
					this.currentFrame=0;
				}
				
			}
			
			timeForNextFrame = LastFrameTime + delay;
		}
	}
	public int getCurrentFrame(){
		return this.currentFrame;
	}
	public void setAngle(double angl){
		this.angle = angl;
	}
	public void updatePosition(int x, int y){
		this.xpos=x;
		this.ypos=y;
	}
	public int getFrameCount(){
		return this.frameCount;
	}
	public void Draw(Graphics g){

		this.update();

		if(this.currentFrame<this.frameCount){
			if(!additive){
				if(angle==0){
					g.drawImage(spriteSheet[currentFrame][index], GamePanel.cameraX+xpos, GamePanel.cameraY+ypos, (int)(spriteSheet[currentFrame][0].getWidth()*xScale*GamePanel.cameraScale), (int)(spriteSheet[currentFrame][0].getHeight()*yScale*GamePanel.cameraScale), null);
				}
				else{
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					AffineTransform at = new AffineTransform();
					at.translate(xpos*GamePanel.cameraScale, ypos*GamePanel.cameraScale);
					at.scale(xScale*GamePanel.cameraScale, yScale*GamePanel.cameraScale);
					at.rotate(angle,spriteSheet[currentFrame][index].getWidth()/2,spriteSheet[currentFrame][index].getHeight()/2);
					
					g2d.drawImage(spriteSheet[currentFrame][index], at, null);
				}
			}
			else{
				for(int i = currentFrame; i>0;i--){
					g.drawImage(spriteSheet[i][index], xpos+(int)(i*(spriteSheet[currentFrame][0].getWidth()*xScale)), ypos, (int)(spriteSheet[currentFrame][0].getWidth()*xScale), (int)(spriteSheet[currentFrame][0].getHeight()*yScale), null);
				}
			}


			this.LastFrameTime=System.currentTimeMillis();
		}
		//}

	}
}
