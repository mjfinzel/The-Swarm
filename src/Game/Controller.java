package Game;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;




import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;



public class Controller extends JPanel implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//public static Unit currentUnit;
	public static String currentTileName = "No tiles selected";
	private JPanel gamePanel;
	long starttime = 0;
	private static boolean[] keyboardState = new boolean[525];
	public static boolean mouseDragging = false;

	public Controller(){
		this.setDoubleBuffered(true);

	}
	public static boolean keyboardKeyState(int key)
	{
		return keyboardState[key];
	}
	public void setGamePanel(JPanel panelRef) {
		gamePanel = panelRef;
		gamePanel.addKeyListener(this);
		gamePanel.addMouseListener(this);
		gamePanel.addMouseMotionListener(this);
		gamePanel.addMouseWheelListener(this);
	}
	public void setGamePanelPos(int x, int y){
		gamePanel.setAlignmentX(x);
		gamePanel.setAlignmentX(y);
	}
	public void updateAll(){
		if (gamePanel != null)
			gamePanel.getParent().repaint();
	}
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void mouseClicked(MouseEvent arg0) {

	}
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void mousePressed(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON1){//left click
			boolean buttonPushed = false;
			for(int i = 0; i<GamePanel.buttons.size();i++){
				if(GamePanel.buttons.get(i).mouseOnButton==true){
					GamePanel.buttons.get(i).pushButton();
					buttonPushed=true;
				}
			}
			if(!buttonPushed){
				GamePanel.ripples.add(new Animation(GamePanel.rippleImg,6,80,0,MouseInfo.getPointerInfo().getLocation().x-20,MouseInfo.getPointerInfo().getLocation().y-20,false,0,0));
				//move the swarmers toward the point that was clicked
				int packSize = GamePanel.levels.get(GamePanel.currentLevel).swarmerCount;
				int destVariation = packSize*3;
				if(destVariation>100){
					destVariation=100;
				}
				for(int k = 0; k<16; k++){
					for(int j = 0; j<9;j++){
						for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.size();i++){
							Point dest = new Point(MouseInfo.getPointerInfo().getLocation().x+GamePanel.randomNumber(-destVariation, destVariation),MouseInfo.getPointerInfo().getLocation().y+GamePanel.randomNumber(-destVariation, destVariation));
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.get(i).moveToPoint(dest);
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.get(i).anchor=dest;
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.get(i).closestFood=null;
						}
					}
				}
			}
		}
		else if(arg0.getButton() == MouseEvent.BUTTON3){//right click
			GamePanel.ripples.add(new Animation(GamePanel.rippleImg,6,80,0,MouseInfo.getPointerInfo().getLocation().x-20,MouseInfo.getPointerInfo().getLocation().y-20,false,0,0));
			//move the swarmers toward the point that was clicked
			int packSize = GamePanel.levels.get(GamePanel.currentLevel).swarmerCount;
			int destVariation = packSize*2;
			if(destVariation>100){
				destVariation=100;
			}
			for(int k = 0; k<16; k++){
				for(int j = 0; j<9;j++){
					for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.size();i++){
						if(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.get(i).getVisibleBaiters().size()==0){
							Point dest = new Point(MouseInfo.getPointerInfo().getLocation().x+GamePanel.randomNumber(-destVariation, destVariation),MouseInfo.getPointerInfo().getLocation().y+GamePanel.randomNumber(-destVariation, destVariation));
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.get(i).moveToPoint(dest);
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.get(i).anchor=dest;
							GamePanel.levels.get(GamePanel.currentLevel).fishGrid[k][j].swarmers.get(i).closestFood=null;
						}
					}
				}
			}
		}
		if(GamePanel.godmodeMenu.isVisible){
			for(int i = 0; i<GamePanel.godmodeMenu.modifiableValues.size();i++){
				if(GamePanel.godmodeMenu.modifiableValues.get(i).mouseOnBottomButton()){
					GamePanel.godmodeMenu.modifiableValues.get(i).bottomButtonIsPushed=true;
				}
				if(GamePanel.godmodeMenu.modifiableValues.get(i).mouseOnTopButton()){
					GamePanel.godmodeMenu.modifiableValues.get(i).topButtonIsPushed=true;
				}
				if(GamePanel.godmodeMenu.modifiableValues.get(i).mouseOnSetButton()){
					GamePanel.godmodeMenu.modifiableValues.get(i).pressSetButton();
				}
			}
		}
	}
	//	public static void addSwarmerToStorage(int size){
	//		if(GamePanel.levels.get(GamePanel.currentLevel).getNumberOfSwarmers()>0){
	//			Swarmer smallestSwarmer=GamePanel.levels.get(GamePanel.currentLevel).swarmers.get(0);
	//			Swarmer largestSwarmer=GamePanel.levels.get(GamePanel.currentLevel).swarmers.get(0);
	//			Swarmer randomSwarmer = GamePanel.levels.get(GamePanel.currentLevel).swarmers.get(GamePanel.randomNumber(0, GamePanel.levels.get(GamePanel.currentLevel).swarmers.size()-1));
	//			for(int i = 0;i<GamePanel.levels.get(GamePanel.currentLevel).swarmers.size();i++){
	//				if(GamePanel.levels.get(GamePanel.currentLevel).swarmers.get(i).size<smallestSwarmer.size){
	//					smallestSwarmer=GamePanel.levels.get(GamePanel.currentLevel).swarmers.get(i);
	//				}
	//				if(GamePanel.levels.get(GamePanel.currentLevel).swarmers.get(i).size>largestSwarmer.size){
	//					largestSwarmer=GamePanel.levels.get(GamePanel.currentLevel).swarmers.get(i);
	//				}
	//			}
	//			if(size==0){//add smallest
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.add(smallestSwarmer);
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmers.remove(smallestSwarmer);
	//			}
	//			else if(size==1){//add random
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.add(randomSwarmer);
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmers.remove(randomSwarmer);
	//			}
	//			else if(size==2){//add largest
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.add(largestSwarmer);
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmers.remove(largestSwarmer);
	//			}
	//		}
	//
	//	}
	//	public static void takeSwarmerFromStorage(int size){
	//		if(GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.size()>0){
	//			Swarmer smallestSwarmer=GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.get(0);
	//			Swarmer largestSwarmer=GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.get(0);
	//			Swarmer randomSwarmer = GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.get(GamePanel.randomNumber(0, GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.size()-1));
	//			for(int i = 0;i<GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.size();i++){
	//				if(GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.get(i).size<smallestSwarmer.size){
	//					smallestSwarmer=GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.get(i);
	//				}
	//				if(GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.get(i).size>largestSwarmer.size){
	//					largestSwarmer=GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.get(i);
	//				}
	//			}
	//			if(size==0){//add smallest
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmers.add(smallestSwarmer);
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.remove(smallestSwarmer);			
	//			}
	//			else if(size==1){//add random
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmers.add(randomSwarmer);
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.remove(randomSwarmer);				
	//			}
	//			else if(size==2){//add largest
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmers.add(largestSwarmer);
	//				GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.remove(largestSwarmer);			
	//			}
	//		}
	//	}
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		for(int i = 0; i<GamePanel.godmodeMenu.modifiableValues.size();i++){
			GamePanel.godmodeMenu.modifiableValues.get(i).bottomButtonIsPushed=false;
			GamePanel.godmodeMenu.modifiableValues.get(i).topButtonIsPushed=false;
		}
	}
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_G) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			if(GamePanel.godmodeMenu.isVisible==false){
				GamePanel.godmodeMenu.isVisible=true;
			}
			else{
				GamePanel.godmodeMenu.isVisible=false;
			}
		}
	}
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
		//scale down
		//move the center of the map to the mouse position before the scaling

		if(e.getWheelRotation()<0){//mouse wheel moved up (zoom in)
			if(GamePanel.cameraScale<5){
				GamePanel.cameraScale=GamePanel.cameraScale+.02;
			}
		}
		else{//mouse wheel moved down (zoom out)
			if(GamePanel.cameraScale>.04){
				GamePanel.cameraScale=GamePanel.cameraScale-.02;
			}
		}
		if(GamePanel.cameraScale<=1){
			GamePanel.cameraX=(int)(MouseInfo.getPointerInfo().getLocation().x)-(int)(MouseInfo.getPointerInfo().getLocation().x*GamePanel.cameraScale);
			GamePanel.cameraY=(int)(MouseInfo.getPointerInfo().getLocation().y)-(int)(MouseInfo.getPointerInfo().getLocation().y*GamePanel.cameraScale);
		}
		else{
			GamePanel.cameraX=(int)(MouseInfo.getPointerInfo().getLocation().x)-(int)(MouseInfo.getPointerInfo().getLocation().x*GamePanel.cameraScale);
			GamePanel.cameraY=(int)(MouseInfo.getPointerInfo().getLocation().y)-(int)(MouseInfo.getPointerInfo().getLocation().y*GamePanel.cameraScale);
		}
		//GamePanel.cameraX = 960-(int)(960*GamePanel.cameraScale);
		//GamePanel.cameraY = 540-(int)(540*GamePanel.cameraScale);
		System.out.println("resized camera, camera scale is: "+GamePanel.cameraScale+", cameraX is "+GamePanel.cameraX+", cameraY is "+GamePanel.cameraY);
	}
}
