package Game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;





public class Images implements Serializable{
	public static BufferedImage load(String imageName)
    {
        BufferedImage image;
        try
        {
            image = ImageIO.read(Images.class.getResourceAsStream(imageName));
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = img;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return image;
    }
	
	public static BufferedImage[][] cut(String imageName, int sliceWidth, int sliceHeight)
	{
		//System.out.println("Starting image cut for "+imageName);
		long temp = System.currentTimeMillis();
		BufferedImage sheet;
		try
		{
			sheet = ImageIO.read(Images.class.getResourceAsStream(imageName));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

		int w = sheet.getWidth();
		int h = sheet.getHeight();

		int xSlices = w/sliceWidth;
		int ySlices = h/sliceHeight;

		BufferedImage[][] images = new BufferedImage[xSlices][ySlices];
		for (int x=0; x<xSlices; x++)
			for (int y=0; y<ySlices; y++)
			{
				BufferedImage img = new BufferedImage(sliceWidth, sliceHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics g = img.getGraphics();
				g.drawImage(sheet, -x*sliceWidth, -y*sliceHeight, null);
				g.dispose();
				images[x][y] = img;
			}
		
		//System.out.println("Done cutting image for "+imageName+". Total time spent on cut = "+(System.currentTimeMillis()-temp));
		return images;
	}
}
