package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;

public class Button {
	int xpos;
	int ypos;
	int buttonID=0;
	boolean mouseOnButton = false;
	boolean depositing = false;
	public Button(int x, int y, int id){
		xpos=x;
		ypos=y;
		buttonID=id;
	}
	public void pushButton(){
		if(buttonID<=3&&buttonID>=0){
			buttonID++;
			GamePanel.swarmerAI++;
			if(buttonID>=3){
				buttonID=0;
				GamePanel.swarmerAI=0;
			}
		}
		else if(buttonID==4){//store largest
			//Controller.addSwarmerToStorage(2);
		}
		else if(buttonID==5){//store smallest
			//Controller.addSwarmerToStorage(0);
		}
		else if(buttonID==6){//store male
			//Controller.addSwarmerToStorage(3);
		}
		else if(buttonID==7){//store female
			//Controller.addSwarmerToStorage(4);
		}
		else if(buttonID==8){//store random
			//Controller.addSwarmerToStorage(1);
		}
		else if(buttonID==9){//take largest
			//Controller.takeSwarmerFromStorage(2);
		}
		else if(buttonID==10){//take smallest
			//Controller.takeSwarmerFromStorage(0);
		}
		else if(buttonID==11){//take male
			//Controller.takeSwarmerFromStorage(3);
		}
		else if(buttonID==12){//take female
			//Controller.takeSwarmerFromStorage(4);
		}
		else if(buttonID==13){//take random
			//Controller.takeSwarmerFromStorage(1);
		}
		else if(buttonID==14){//withdraw
			buttonID=15;
			for(int i = 0;i<GamePanel.buttons.size();i++){
				if(GamePanel.buttons.get(i).buttonID==4){
					GamePanel.buttons.get(i).buttonID=9;
				}
				else if(GamePanel.buttons.get(i).buttonID==5){
					GamePanel.buttons.get(i).buttonID=10;
				}
				else if(GamePanel.buttons.get(i).buttonID==6){
					GamePanel.buttons.get(i).buttonID=11;
				}
				else if(GamePanel.buttons.get(i).buttonID==7){
					GamePanel.buttons.get(i).buttonID=12;
				}
				else if(GamePanel.buttons.get(i).buttonID==8){
					GamePanel.buttons.get(i).buttonID=13;
				}
			}
		}
		else if(buttonID==15){//deposit
			buttonID=14;
			for(int i = 0;i<GamePanel.buttons.size();i++){
				if(GamePanel.buttons.get(i).buttonID==9){
					GamePanel.buttons.get(i).buttonID=4;
				}
				else if(GamePanel.buttons.get(i).buttonID==10){
					GamePanel.buttons.get(i).buttonID=5;
				}
				else if(GamePanel.buttons.get(i).buttonID==11){
					GamePanel.buttons.get(i).buttonID=6;
				}
				else if(GamePanel.buttons.get(i).buttonID==12){
					GamePanel.buttons.get(i).buttonID=7;
				}
				else if(GamePanel.buttons.get(i).buttonID==13){
					GamePanel.buttons.get(i).buttonID=8;
				}
			}
		}
		else if(buttonID==16){//retry
			GamePanel.reseting=true;
			GamePanel.levels.clear();
			GamePanel.maps.clear();
			GamePanel.maps.add(GamePanel.map1);
			GamePanel.levels.add(new Level(GamePanel.currentLevel+1));
			GamePanel.reseting=false;
			GamePanel.levelLost=false;
			for(int i = 0; i<GamePanel.buttons.size();i++){
				while(GamePanel.buttons.get(i).buttonID==16||GamePanel.buttons.get(i).buttonID==17||GamePanel.buttons.get(i).buttonID==18){
					GamePanel.buttons.remove(i);
					if(i>0)
						i--;
				}
			}

		}
		else if(buttonID==17){//view score board

		}
		else if(buttonID==18){//exit game
			System.exit(0);
		}
	}
	public void Draw(Graphics g){
		mouseOnButton=false;
		if(MouseInfo.getPointerInfo().getLocation().x>=xpos&&MouseInfo.getPointerInfo().getLocation().x<=xpos+150){
			if(MouseInfo.getPointerInfo().getLocation().y>=ypos&&MouseInfo.getPointerInfo().getLocation().y<=ypos+30){
				mouseOnButton=true;
				g.drawImage(GamePanel.buttonHighlight,this.xpos,this.ypos,150,30,null);
			}
		}
		if(buttonID<3){
			g.drawImage(GamePanel.buttonImg[0][buttonID],this.xpos,this.ypos,150,30,null);
		}
		else if(buttonID==14||buttonID==15){
			if(buttonID==14&&GamePanel.levels.get(GamePanel.currentLevel).swarmerStorage.size()>0){
				g.drawImage(GamePanel.buttonImg[0][4],this.xpos,this.ypos,150,30,null);
			}
			else if(buttonID==15&&GamePanel.levels.get(GamePanel.currentLevel).swarmerCount>0){
				g.drawImage(GamePanel.buttonImg[0][4],this.xpos,this.ypos,150,30,null);
			}
			else{
				g.drawImage(GamePanel.buttonImg[0][2],this.xpos,this.ypos,150,30,null);
			}
		}
		else{
			g.drawImage(GamePanel.buttonImg[0][3],this.xpos,this.ypos,150,30,null);
		}
		Font font = new Font("Iwona Heavy",Font.BOLD,14);
		g.setFont(font);
		g.setColor(Color.black);

		if(buttonID==0){
			g.drawString("Passive", xpos+20, ypos+20);
		}
		else if(buttonID==1){
			g.drawString("Sustain", xpos+20, ypos+20);
		}
		else if(buttonID==2){
			g.drawString("Aggressive", xpos+20, ypos+20);
		}
		else if(buttonID==4){
			g.drawString("Store Largest", xpos+20, ypos+20);
		}
		else if(buttonID==5){
			g.drawString("Store Smallest", xpos+20, ypos+20);
		}
		else if(buttonID==6){
			g.drawString("Store Male", xpos+20, ypos+20);
		}
		else if(buttonID==7){
			g.drawString("Store Female", xpos+20, ypos+20);
		}
		else if(buttonID==8){
			g.drawString("Store Random", xpos+20, ypos+20);
		}
		else if(buttonID==9){
			g.drawString("Take Largest", xpos+20, ypos+20);
		}
		else if(buttonID==10){
			g.drawString("Take Smallest", xpos+20, ypos+20);
		}
		else if(buttonID==11){
			g.drawString("Take Male", xpos+20, ypos+20);
		}
		else if(buttonID==12){
			g.drawString("Take Female", xpos+20, ypos+20);
		}
		else if(buttonID==13){
			g.drawString("Take Random", xpos+20, ypos+20);
		}
		else if(buttonID==14){
			g.drawString("Withdraw", xpos+20, ypos+20);
		}
		else if(buttonID==15){
			g.drawString("Deposit", xpos+20, ypos+20);
		}
		else if(buttonID==16){
			g.drawString("Retry", xpos+20, ypos+20);
		}
		else if(buttonID==17){
			g.drawString("View Scoreboard", xpos+20, ypos+20);
		}
		else if(buttonID==18){
			g.drawString("Exit Game", xpos+20, ypos+20);
		}
	}
}
