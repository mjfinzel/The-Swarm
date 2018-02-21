package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.util.ArrayList;

public class ModifiableValue {
	int xpos=-1;
	int ypos=-1;
	int value=1;
	int valueLowerLimit=0;
	int valueUpperLimit=99999;
	String name = "";
	boolean bottomButtonIsPushed = false;
	boolean topButtonIsPushed = false;
	public ModifiableValue(int x, int y, int value, String valueName){
		this.xpos=x;
		this.ypos=y;
		this.name=valueName;
		if(this.name=="Number of Swarmers"){
			this.value=GamePanel.levels.get(GamePanel.currentLevel).swarmerCount;
		}
		else if(this.name=="Number of Baiters"){
			this.value=GamePanel.levels.get(GamePanel.currentLevel).baiterCount;
		}
		else if(this.name=="Number of JubJubs"){
			this.value=GamePanel.levels.get(GamePanel.currentLevel).jubJubCount;
		}
		else if(this.name=="Plant Limit"){
			this.value=GamePanel.levels.get(GamePanel.currentLevel).plantLimit;
		}
	}
	public boolean mouseOnTopButton(){
		//System.out.println("checking if mouse is on top button");
		if(value!=-1){
			if(MouseInfo.getPointerInfo().getLocation().x>=xpos+32&&MouseInfo.getPointerInfo().getLocation().y>=ypos){
				if(MouseInfo.getPointerInfo().getLocation().x<=xpos+80&&MouseInfo.getPointerInfo().getLocation().y<=ypos+15){
					return true;
				}
			}
		}
		return false;
	}
	public boolean mouseOnBottomButton(){
		//System.out.println("checking if mouse is on bottom button");

		if(MouseInfo.getPointerInfo().getLocation().x>=xpos+32&&MouseInfo.getPointerInfo().getLocation().y>=ypos+47){
			if(MouseInfo.getPointerInfo().getLocation().x<=xpos+80&&MouseInfo.getPointerInfo().getLocation().y<=ypos+62){
				return true;
			}
		}

		return false;
	}
	public boolean mouseOnSetButton(){
		//System.out.println("checking if mouse is on bottom button");

		if(MouseInfo.getPointerInfo().getLocation().x>=xpos&&MouseInfo.getPointerInfo().getLocation().y>=ypos+17){
			if(MouseInfo.getPointerInfo().getLocation().x<=xpos+30&&MouseInfo.getPointerInfo().getLocation().y<=ypos+47){
				return true;
			}
		}

		return false;
	}
	public void pressBottomButton(){

		if(value>valueLowerLimit){
			value--;
		}
		else{
			value=valueUpperLimit;
		}
	}
	public void pressTopButton(){
		//determine which button this is
		//if this is a button between 12 and 17
		//if the total value of buttons 12 through 17 added up is less than the power limit
		//increase the limit
		if(value<valueUpperLimit){
			value++;
		}
		else{
			value=valueLowerLimit;
		}

	}
	public void pressSetButton(){
		if(this.name=="Number of Swarmers"){
			if(this.value>GamePanel.levels.get(GamePanel.currentLevel).swarmerCount){
				while(this.value>GamePanel.levels.get(GamePanel.currentLevel).swarmerCount){
					int x=100;
					int y=100;
					GamePanel.levels.get(GamePanel.currentLevel).fishGrid[x/120][y/120].swarmers.add(new Swarmer(x, y, x, y));
				}
			}
			else if(this.value<GamePanel.levels.get(GamePanel.currentLevel).swarmerCount){
				while(this.value<GamePanel.levels.get(GamePanel.currentLevel).swarmerCount){
					int randomx = GamePanel.randomNumber(0, 15);
					int randomy = GamePanel.randomNumber(0, 8);
					if(GamePanel.levels.get(GamePanel.currentLevel).fishGrid[randomx][randomy].swarmers.size()>0){
						GamePanel.levels.get(GamePanel.currentLevel).fishGrid[randomx][randomy].swarmers.remove(GamePanel.randomNumber(0,GamePanel.levels.get(GamePanel.currentLevel).fishGrid[randomx][randomy].swarmers.size()-1));
					}
				}
			}
		}
		else if(this.name=="Number of Baiters"){
			if(this.value>GamePanel.levels.get(GamePanel.currentLevel).baiterCount){
				while(this.value>GamePanel.levels.get(GamePanel.currentLevel).baiterCount){
					int x=700;
					int y=700;
					
					GamePanel.levels.get(GamePanel.currentLevel).fishGrid[x/120][y/120].baiters.add(new Baiter(x, y, 0));
				}
			}
			
			else if(this.value<GamePanel.levels.get(GamePanel.currentLevel).baiterCount){
				while(this.value<GamePanel.levels.get(GamePanel.currentLevel).baiterCount){
					int randomx = GamePanel.randomNumber(0, 15);
					int randomy = GamePanel.randomNumber(0, 8);
					GamePanel.levels.get(GamePanel.currentLevel).fishGrid[randomx/120][randomy/120].baiters.remove(GamePanel.randomNumber(0,GamePanel.levels.get(GamePanel.currentLevel).fishGrid[randomx/120][randomy/120].baiters.size()-1));
				}
			}
		}
//		else if(this.name=="Number of JubJubs"){
//			ArrayList<Baiter> jubJubs = new ArrayList<Baiter>();
//			for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).baiterCount;i++){
//				if(GamePanel.levels.get(GamePanel.currentLevel).baiters.get(i).fishID==1){
//					jubJubs.add(GamePanel.levels.get(GamePanel.currentLevel).baiters.get(i));
//				}
//			}
//			if(this.value>jubJubs.size()){
//				while(this.value>jubJubs.size()){
//					int x=900;
//					int y=700;
//					if(jubJubs.size()>0){
//						x=jubJubs.get(0).anchor.x;
//						y=jubJubs.get(0).anchor.y;
//					}
//					Baiter newJubJub = new Baiter(x, y, 1);
//					GamePanel.levels.get(GamePanel.currentLevel).baiters.add(newJubJub);
//					jubJubs.add(newJubJub);
//				}
//			}
//			else if(this.value<jubJubs.size()){
//				while(this.value<jubJubs.size()){
//					Baiter victim = jubJubs.get(GamePanel.randomNumber(0,jubJubs.size()-1));
//					GamePanel.levels.get(GamePanel.currentLevel).baiters.remove(victim);
//					jubJubs.remove(victim);
//				}
//			}
//		}
//		else if(this.name=="Number of Anglers"){
//			ArrayList<Baiter> anglers = new ArrayList<Baiter>();
//			for(int i = 0; i<GamePanel.levels.get(GamePanel.currentLevel).baiters.size();i++){
//				if(GamePanel.levels.get(GamePanel.currentLevel).baiters.get(i).fishID==2){
//					anglers.add(GamePanel.levels.get(GamePanel.currentLevel).baiters.get(i));
//				}
//			}
//			if(this.value>anglers.size()){
//				while(this.value>anglers.size()){
//					int x=900;
//					int y=700;
//					if(anglers.size()>0){
//						x=anglers.get(0).anchor.x;
//						y=anglers.get(0).anchor.y;
//					}
//					Baiter newAngler = new Baiter(x, y, 2);
//					GamePanel.levels.get(GamePanel.currentLevel).baiters.add(newAngler);
//					anglers.add(newAngler);
//				}
//			}
//			else if(this.value<anglers.size()){
//				while(this.value<anglers.size()){
//					Baiter victim = anglers.get(GamePanel.randomNumber(0,anglers.size()-1));
//					GamePanel.levels.get(GamePanel.currentLevel).baiters.remove(victim);
//					anglers.remove(victim);
//				}
//			}
//		}
//		else if(this.name=="Plant Limit"){
//			if(this.value>=GamePanel.levels.get(GamePanel.currentLevel).plantLimit){
//				GamePanel.levels.get(GamePanel.currentLevel).plantLimit=value;
//			}
//			else{//there are more plants than the new limit allows
//				GamePanel.levels.get(GamePanel.currentLevel).plantLimit=value;
//				while(GamePanel.levels.get(GamePanel.currentLevel).plants.size()>GamePanel.levels.get(GamePanel.currentLevel).plantLimit){
//					GamePanel.levels.get(GamePanel.currentLevel).plants.remove(GamePanel.randomNumber(0, GamePanel.levels.get(GamePanel.currentLevel).plants.size()));
//				}
//			}
//		}
	}
	public void Draw(Graphics g){
		if(bottomButtonIsPushed){
			pressBottomButton();
		}
		else if(topButtonIsPushed){
			pressTopButton();
		}
		Font font = new Font("Iwona Heavy",Font.BOLD,16);
		g.setFont(font);

		g.drawImage(GamePanel.modifiableValueArt, xpos, ypos,80, 62, null);
		if(mouseOnBottomButton()){//highlight the bottom button
			g.drawImage(GamePanel.buttonHighlight, xpos+32, ypos+47,48, 15, null);
		}
		if(mouseOnTopButton()){//highlight the top button
			g.drawImage(GamePanel.buttonHighlight, xpos+32, ypos,48, 15, null);
		}
		if(mouseOnSetButton()){//highlight the set button
			g.drawImage(GamePanel.buttonHighlight, xpos, ypos+17,30, 30, null);
		}




		if(value==valueUpperLimit){
			g.setColor(Color.BLUE);
		}
		else{
			g.setColor(Color.BLACK);
		}
		g.drawString(value+"", xpos+34, ypos+36);
		g.setColor(Color.BLACK);
		g.drawString(name, xpos+85, ypos+36);

		g.setColor(Color.BLACK);
	}
}
