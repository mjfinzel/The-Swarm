package Game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;






public class AppletUI extends JFrame{

	private static final long serialVersionUID = -6215774992938009947L;
	public static final long milisecInNanosec = 1000000L;
	public static final long secInNanosec = 1000000000L;
	private int GAME_FPS = 60;
	private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
	public long lastDrawTime = System.currentTimeMillis();
	public static int windowWidth=1920;
	public static int windowHeight=1080;
	public static double delta = 1.0;

	//variables for sparkles
	long lastSparkleUpdateTime = 0;
	int sparkleUpdateDelay = 10;
	int sparkleXpos = 725;//1195

	//detect the fps the game is running at
	int currentFPS = 0;
	double avgXpos = 0;
	double avgYpos = 0;
	static int fps = 0;
	//time since last second fps was measured
	long lastFPStimeUpdate = System.currentTimeMillis();

	long lastUpdateTime = System.nanoTime();

	Controller ctrl;
	public static void main(String[] args){
		AppletUI f = new AppletUI ();
		f.setSize(windowWidth,windowHeight);
		f.setVisible(true);
	}
	public AppletUI() {

		setSize(windowWidth,windowHeight);
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		JPanel drawPanel = new GamePanel();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		drawPanel.setBackground(Color.BLACK);
		ctrl = new Controller();
		this.addKeyListener(ctrl);
		ctrl.setGamePanel(drawPanel);
		this.setFocusable(true);
		pane.add(drawPanel);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);  
		this.setUndecorated(true);  
		//We start game in new thread.
		Thread gameThread = new Thread() {			
			public void run(){
				gameLoop();
			}
		};
		gameThread.start();
		//BattleshorePanel.game_is_running=true;
	}
	int delay = 0;
	public void gameLoop(){

		// This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
		long beginTime, timeTaken, timeLeft;
		int ticks = 0;
		while(true)
		{	
			//System.out.println("looping!");
			beginTime = System.nanoTime();
			// Repaint the screen 100 times per second.

			if(!GamePanel.reseting&&!GamePanel.levelBeat){
				repaint();
			}
			if(!GamePanel.levelLost&&!GamePanel.reseting&&!GamePanel.levelBeat&&GamePanel.levels.size()>=GamePanel.currentLevel-1){

				if(lastFPStimeUpdate+1000>=System.currentTimeMillis()){
					currentFPS++;
				}
				else{
					fps=currentFPS;
					currentFPS=0;
					lastFPStimeUpdate=System.currentTimeMillis();
				}
				//while( now - lastUpdateTime > TIME_BETWEEN_UPDATES
				lastUpdateTime = System.nanoTime();
				int recentUpdates = 0;
				//while(System.nanoTime()-lastUpdateTime>GAME_UPDATE_PERIOD){
					//spawn waves
					if(GamePanel.levels.get(GamePanel.currentLevel).waves.size()<30&&GamePanel.randomNumber(1, 100)<=20){
						GamePanel.levels.get(GamePanel.currentLevel).waves.add(new Animation(GamePanel.waveImg,16,180,0,(int)(GamePanel.randomNumber(-39, 1939)*GamePanel.cameraScale),(int)(GamePanel.randomNumber(-20, 1100)*GamePanel.cameraScale),false,(int)(GamePanel.randomNumber(-1, 1)*GamePanel.cameraScale),(int)(GamePanel.randomNumber(-1, 1)*GamePanel.cameraScale)));
					}
					//long updateSwarmersStart = System.nanoTime();

					//update all swarmers
					//GamePanel.visibleBaiters.clear();
					//determine the average position of the swarm
					double totalX = 0;
					double totalY = 0;
					//					for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).swarmers.size();i++){
					//						totalX+=GamePanel.levels.get(GamePanel.currentLevel).swarmers.get(i).xpos;
					//						totalY+=GamePanel.levels.get(GamePanel.currentLevel).swarmers.get(i).ypos;
					//					}
					//					avgXpos = totalX/GamePanel.levels.get(GamePanel.currentLevel).swarmers.size();
					//					avgYpos = totalY/GamePanel.levels.get(GamePanel.currentLevel).swarmers.size();
					for(int i = 0; i<1920/GamePanel.chunkSize; i++){
						for(int j = 0; j<1080/GamePanel.chunkSize;j++){
							for(int k = 0; k<GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].swarmers.size();k++){
								GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].swarmers.get(k).update();
							}
						}
					}
					//System.out.println("Took "+((System.nanoTime()-updateSwarmersStart)/1000000)+" milliseconds to update swarmers. ("+(System.nanoTime()-updateSwarmersStart)+ " nanoseconds)");

					//long updateBaitersStart = System.nanoTime();
					//update all baiters
					for(int i = 0; i<1920/GamePanel.chunkSize; i++){
						for(int j = 0; j<1080/GamePanel.chunkSize;j++){
							for(int k = 0; k<GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.size();k++){
								GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].baiters.get(k).update();
							}
						}
					}
					//System.out.println("Took "+((System.nanoTime()-updateBaitersStart)/1000000)+" milliseconds to update baiters. ("+(System.nanoTime()-updateBaitersStart)+ " nanoseconds)");
					//long updatePlantsStart = System.nanoTime();
					//update all plants
					for(int i = 0; i<1920/GamePanel.chunkSize; i++){
						for(int j = 0; j<1080/GamePanel.chunkSize;j++){
							for(int k = 0; k<GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].plants.size();k++){
								GamePanel.levels.get(GamePanel.currentLevel).fishGrid[i][j].plants.get(k).update();
							}
						}
					}
					//System.out.println("Took "+((System.nanoTime()-updatePlantsStart)/1000000)+" milliseconds to update plants. ("+(System.nanoTime()-updatePlantsStart)+ " nanoseconds)");
					//update all items
					for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).items.size();i++){
						GamePanel.levels.get(GamePanel.currentLevel).items.get(i).update();
					}


				//}
				if(GamePanel.levelLost){
					for(int i=0;i<200;i++){
						GamePanel.ripples.add(new Animation(GamePanel.bloodImg,10,GamePanel.randomNumber(200, 400),0,(int)(GamePanel.randomNumber(0, 1920)*GamePanel.cameraScale),(int)(GamePanel.randomNumber(0, 1080)*GamePanel.cameraScale),false,0,0));
					}
				}
				//System.out.println("updated items");
				lastDrawTime=System.currentTimeMillis();
				for(int i = 0; i<GamePanel.ripples.size();i++){
					while(i<GamePanel.ripples.size()&&GamePanel.ripples.get(i)==null){
						GamePanel.ripples.remove(i);
					}
				}
			}
			// Here we calculate the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
			timeTaken = System.nanoTime() - beginTime;
			
			delta=(double)timeTaken/GAME_UPDATE_PERIOD;
			if(delta<1){
				delta=1;
			}
			//System.out.println("time taken: "+timeTaken+"game update period"+GAME_UPDATE_PERIOD+" delta: "+delta);
			//System.out.println("Took "+timeTaken+" nanoseconds to update all");
			timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
			// If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
			if (timeLeft < 10) 
				timeLeft = 10; //set a minimum
			try {
				//Provides the necessary delay and also yields control so that other thread can do work.
				Thread.sleep(timeLeft);
			} catch (InterruptedException ex) { }
			
		}
	}

	public void Draw(Graphics g){

	}

}

