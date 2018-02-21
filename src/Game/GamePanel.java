package Game;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel{

	private static final long serialVersionUID = 7734877696044080629L;
	public static BufferedImage [][]swarmerForwardAnim = Images.cut("/Textures/SwarmerForwardAnim.png", 24, 16);
	public static BufferedImage [][]femaleBaiterForwardAnim = Images.cut("/Textures/FemaleBaiterForwardAnim.png", 16, 10);
	public static BufferedImage [][]maleBaiterForwardAnim = Images.cut("/Textures/MaleBaiterForwardAnim.png", 16, 10);
	public static BufferedImage [][]maleJubJubForwardAnim = Images.cut("/Textures/MaleJubJubForwardAnim.png", 128, 64);
	public static BufferedImage [][]femaleJubJubForwardAnim = Images.cut("/Textures/FemaleJubJubForwardAnim.png", 128, 64);
	public static BufferedImage [][]maleAnglerForwardAnim = Images.cut("/Textures/MaleAnglerForwardAnim.png", 164, 64);
	public static BufferedImage [][]femaleAnglerForwardAnim = Images.cut("/Textures/FemaleAnglerForwardAnim.png", 164, 64);
	public static BufferedImage meatChunk = Images.load("/Textures/MeatChunk.png");
	public static BufferedImage loseLevelBackground = Images.load("/Textures/loseLevelBackground.png");
	public static BufferedImage[][] heart = Images.cut("/Textures/Heart.png",20,20);
	public static BufferedImage healthBarBackgroun = Images.load("/Textures/HealthBarBackground.png");
	public static BufferedImage [][]bars = Images.cut("/Textures/Bars.png", 2, 4);
	public static BufferedImage [][]kelpImg = Images.cut("/Textures/Kelp.png", 30, 50);
	public static BufferedImage [][]rockImg = Images.cut("/Textures/Rocks.png", 20, 20);
	//maps
	public static BufferedImage map1 = Images.load("/Maps/Map1.png");
	public static BufferedImage map2 = Images.load("/Maps/Map2.png");
	public static BufferedImage godModeWindow = Images.load("/Textures/GodmodeWindow.png");
	public static BufferedImage modifiableValueArt = Images.load("/Textures/ModifiableValueArt.png");
	public static BufferedImage buttonHighlight = Images.load("/Textures/ButtonHighlight.png");
	public static BufferedImage[][] hungerBubbles = Images.cut("/Textures/HungerBubbles.png", 30, 30);
	public static BufferedImage[][] rippleImg = Images.cut("/Textures/Ripple.png", 40, 40);
	public static BufferedImage[][] bloodImg = Images.cut("/Textures/Blood.png", 10, 10);
	public static BufferedImage[][] swarmerDust = Images.cut("/Textures/SwarmerDust.png", 10, 10);
	public static BufferedImage[][] waveImg = Images.cut("/Textures/Wave.png", 40, 40);
	public static BufferedImage[][] buttonImg = Images.cut("/Textures/Buttons.png", 150, 50);
	public static BufferedImage[][] dayNightFade = Images.cut("/Textures/DayNightFade.png", 1, 1);
	static ArrayList<Animation> ripples = new ArrayList<Animation>();
	public static int chunkSize = 120;
	public static int swarmDisplacement = 1;
	
	public static double cameraScale = 1.0;
	public static int cameraX = 0;
	public static int cameraY = 0;
	public static int levelWidth=16*chunkSize;//16 for 1920
	public static int levelHeight=9*chunkSize;//9 for 1080
	//list of all the maps for the game
	static ArrayList<BufferedImage> maps = new ArrayList<BufferedImage>();
	//list of all the levels
	public static ArrayList<Level> levels = new ArrayList<Level>();

	public static boolean levelLost = false;
	public static boolean levelBeat = false;
	public static boolean reseting = false;
	int[][] lightMap = new int[GamePanel.levelWidth][GamePanel.levelHeight];
	//current level the player is on
	public static int currentLevel = 0;
	public static int swarmerAI=0;//0 = passive, 1 = sustain, 2 = aggressive
	public static ArrayList<Button> buttons = new ArrayList<Button>();
	//public static ArrayList <Baiter> visibleBaiters = new ArrayList<Baiter>();
	public static GodmodeMenu godmodeMenu;
	Animation timeOfDay = new Animation(dayNightFade,192,1562,0,0,0,true,0,0);
	public GamePanel(){
		this.setDoubleBuffered(true);
		maps.add(map1);
		levels.add(new Level(1));
		buttons.add(new Button(1770,1050,0));
		buttons.add(new Button(1770,1020,4));
		buttons.add(new Button(1770,990,5));
		buttons.add(new Button(1770,960,6));
		buttons.add(new Button(1770,930,7));
		buttons.add(new Button(1770,900,8));
		buttons.add(new Button(1770,870,14));
		godmodeMenu = new GodmodeMenu();
		//Animation timeOfDay = new Animation(dayNightFade,42,1000,0,0,0,true,0,0);
		timeOfDay.xScale=GamePanel.levelWidth;
		timeOfDay.yScale=GamePanel.levelHeight;
		for(int i = 0; i<GamePanel.levelWidth;i++){
			for(int j = 0; j<GamePanel.levelHeight;j++){
				lightMap[i][j]=0;
			}
		}
	}
	public static void startLevel(){
		GamePanel.reseting=true;
		GamePanel.maps.clear();
		GamePanel.maps.add(GamePanel.map2);
		GamePanel.levels.add(new Level(GamePanel.currentLevel+1));
		GamePanel.reseting=false;
		for(int i = 0; i<GamePanel.buttons.size();i++){
			while(GamePanel.buttons.get(i).buttonID==16||GamePanel.buttons.get(i).buttonID==17||GamePanel.buttons.get(i).buttonID==18){
				GamePanel.buttons.remove(i);
				if(i>0)
					i--;
			}
		}
		GamePanel.levelBeat=false;
		GamePanel.levelLost=false;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Draw(g);
	}
	static int randomNumber(int min, int max){
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	public void Draw(Graphics g){
		if(!levelLost&&!levelBeat&&!reseting){
			//draw the current map
			g.drawImage(levels.get(currentLevel).map,cameraX,cameraY,(int)(1920*cameraScale),(int)(1080*cameraScale),null);
			//draw rocks
			for(int i = 0; i<levels.get(currentLevel).rocks.size();i++){
				levels.get(currentLevel).rocks.get(i).Draw(g);
			}
			//draw nests
			for(int i = 0; i<levels.get(currentLevel).nests.size();i++){
				levels.get(currentLevel).nests.get(i).Draw(g);
			}
			//draw all the baiters in the level
			for(int i = 0; i<GamePanel.levelWidth/GamePanel.chunkSize; i++){
				for(int j = 0; j<GamePanel.levelHeight/GamePanel.chunkSize;j++){
					for(int k = 0; k<levels.get(currentLevel).fishGrid[i][j].baiters.size();k++){
						if(k<levels.get(currentLevel).fishGrid[i][j].baiters.size()){
							if(levels.get(currentLevel).fishGrid[i][j].baiters.get(k)!=null){
								levels.get(currentLevel).fishGrid[i][j].baiters.get(k).Draw(g);
							}
						}
					}
				}
			}

			//draw all the player's swarmers
			for(int i = 0; i<GamePanel.levelWidth/GamePanel.chunkSize; i++){
				for(int j = 0; j<GamePanel.levelHeight/GamePanel.chunkSize;j++){
					for(int k = 0; k<levels.get(currentLevel).fishGrid[i][j].swarmers.size();k++){
						levels.get(currentLevel).fishGrid[i][j].swarmers.get(k).Draw(g);
					}
				}
			}
			//draw plants
			for(int i = 0; i<GamePanel.levelWidth/GamePanel.chunkSize; i++){
				for(int j = 0; j<GamePanel.levelHeight/GamePanel.chunkSize;j++){
					for(int k = 0; k<levels.get(currentLevel).fishGrid[i][j].plants.size();k++){
						levels.get(currentLevel).fishGrid[i][j].plants.get(k).Draw(g);
					}
				}
			}
			//draw items
			for(int i = 0; i<levels.get(currentLevel).items.size();i++){
				levels.get(currentLevel).items.get(i).Draw(g);
			}
			//draw ripples
			for(int i = 0;i<ripples.size();i++){
				if(i<ripples.size()){
					ripples.get(i).Draw(g);
				}
			}
			//draw waves
			for(int i = 0;i<levels.get(currentLevel).waves.size();i++){
				levels.get(currentLevel).waves.get(i).Draw(g);
				if(levels.get(currentLevel).waves.get(i).getCurrentFrame()==16){
					levels.get(currentLevel).waves.remove(i);
				}
			}
			//timeOfDay.Draw(g);
		}
		//if level is lost
		else {
			g.drawImage(loseLevelBackground,0,0,GamePanel.levelWidth,GamePanel.levelHeight,null);
			//draw ripples
			for(int i = 0;i<ripples.size();i++){
				ripples.get(i).Draw(g);
			}
			Font font = new Font("Iwona Heavy",Font.BOLD,58);;
			g.setFont(font);
			g.setColor(Color.black);
			g.drawString("Level Failed", 800, 450);
		}
		//draw godmodeMenu
		godmodeMenu.Draw(g);
		//draw fps
		Font font = new Font("Iwona Heavy",Font.BOLD,12);;
		g.setFont(font);
		g.setColor(Color.black);
		g.drawString("FPS: "+AppletUI.fps, 10, 20);
		if(!reseting&&!levelBeat){
			//draw the current number of swarmers and baiters
			g.drawString("Swarmers: "+GamePanel.levels.get(GamePanel.currentLevel).swarmerCount, 10, 40);
			g.drawString("Swarmers at den: "+GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.size(), 10, 60);
			g.drawString("Baiters: "+GamePanel.levels.get(GamePanel.currentLevel).baiterCount, 10, 80);
			g.drawString("Plants: "+GamePanel.levels.get(GamePanel.currentLevel).plantCount, 10, 100);
			g.drawString("Zoom: "+(int)(GamePanel.cameraScale*100)+"%", 10, 120);

			//draw buttons
			for(int i = 0; i<buttons.size();i++){
				buttons.get(i).Draw(g);
			}
		}
	}
}
