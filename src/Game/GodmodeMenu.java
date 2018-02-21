package Game;

import java.awt.Graphics;
import java.util.ArrayList;

public class GodmodeMenu {
	int xpos;
	int ypos;
	int menuWidth = 300;
	int menuHeight = 600;
	boolean isVisible = false;
	ArrayList<ModifiableValue> modifiableValues = new ArrayList<ModifiableValue>();
	public GodmodeMenu(){
		xpos=(AppletUI.windowWidth-menuWidth)/2;
		ypos=(AppletUI.windowHeight-menuWidth)/2;
		modifiableValues.add(new ModifiableValue(xpos+20, ypos+20+(65*modifiableValues.size()), GamePanel.levels.get(GamePanel.currentLevel).swarmerCount, "Number of Swarmers"));
		modifiableValues.add(new ModifiableValue(xpos+20, ypos+20+(65*modifiableValues.size()), GamePanel.levels.get(GamePanel.currentLevel).swarmerCount, "Number of Baiters"));
		modifiableValues.add(new ModifiableValue(xpos+20, ypos+20+(65*modifiableValues.size()), GamePanel.levels.get(GamePanel.currentLevel).swarmerCount, "Number of JubJubs"));
		modifiableValues.add(new ModifiableValue(xpos+20, ypos+20+(65*modifiableValues.size()), GamePanel.levels.get(GamePanel.currentLevel).swarmerCount, "Number of Anglers"));
		modifiableValues.add(new ModifiableValue(xpos+20, ypos+20+(65*modifiableValues.size()), GamePanel.levels.get(GamePanel.currentLevel).swarmerCount, "Plant Limit"));
	}
	public void Draw(Graphics g){
		if(isVisible){
			//draw background image for the menu
			g.drawImage(GamePanel.godModeWindow,this.xpos,this.ypos,menuWidth,menuHeight,null);
			//draw modifiable values
			for(int i = 0; i<modifiableValues.size();i++){
				modifiableValues.get(i).Draw(g);
			}
		}
	}
}
