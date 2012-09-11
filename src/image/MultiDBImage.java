package image;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import main.Errors;

public class MultiDBImage{
	
    public static final Dimension COVERS_DIM = new Dimension(400,400);
    public static final Dimension MAX_COVERS_DIM = new Dimension(1200,800);
    public static final int FILE_TYPE=0;
    public static final int URL_TYPE=1;
    public static final int IMAGE_TYPE=2;
	public Image image;
	public int width;
	public int height;
	public int fileSize;
	public String name;
	public String url;


	private ImageIcon origIcon, scaledIcon, bigIcon;
	private Image imagen;
	
    public MultiDBImage() {
		super();
		this.origIcon = new ImageIcon();
		this.scaledIcon = new ImageIcon();
		this.bigIcon = new ImageIcon();
		this.width = COVERS_DIM.width;
		this.height = COVERS_DIM.height;
		this.fileSize = 0;
		this.url = "";
		this.name="";
	}

	public static Image getImageFromUrl(String urlstring){
		URL url;
		BufferedImage bimage;
		try {
			url = new URL(urlstring);
			bimage = ImageIO.read(url);
			return bimage;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
	
    public void putImage(JLabel labelFrom,int type, String name, Dimension dim) {
    	PutImage putImageThread = new PutImage(labelFrom,type,name,dim);
    	putImageThread.start();
    }
    
    public void putImage(JLabel labelFrom,int type, String name) {
    	PutImage putImageThread = new PutImage(labelFrom,type,name,COVERS_DIM);
    	putImageThread.start();
    }
    
    public void putImage(JLabel labelFrom,Image image) {
    	PutImage putImageThread = new PutImage(labelFrom,image,"");
    	putImageThread.start();
    }
    
    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    public static Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
    
    public static void writeImageToJPG(File file,BufferedImage bufferedImage) {
    	
    	try {
			ImageIO.write(bufferedImage,"jpg",file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void writeImageToJPG(String path,BufferedImage bufferedImage) {
    	
    	try {
    		File file = new File(path);
			ImageIO.write(bufferedImage,"jpg",file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public String toString(){
    	return "Dim: "+this.width+"x"+this.height;
    }
    
  //PUT IMAGE/////////////////////////////////////////////////////////////////////////  
    public class PutImage extends Thread {
    	   
    	    private JLabel label;
    	    private String sourceName="";
    	    private int type=0;
    	    private Dimension dim=COVERS_DIM;
    	    private Image imageThread;
    	   
    	    
    	    public PutImage(JLabel label,int type,String name,Dimension dim) {
    			super();
    			this.label=label;
    			this.type=type;
    			this.dim=dim;
    			this.sourceName=name;
    		}
    	   		
    	    public PutImage(JLabel label,Image image,String name) {
    			super();
    			this.label=label;
    			this.type=IMAGE_TYPE;
    			this.imageThread=image;
    			this.dim=COVERS_DIM;
    			this.sourceName=name;
    		}
    	   		

    		@Override
    		public void run() {
    			if (this.label==null){
    				Errors.writeError(Errors.VAR_NULL,"Error in MultiDBImage when putting image on label");
    			}else{
	    			switch(this.type){
	    			case(FILE_TYPE):
	    				origIcon = new ImageIcon(sourceName);
	   		     		imagen = origIcon.getImage();
	   		     		//imageThread = imagen.getScaledInstance(dim.width, dim.height, Image.SCALE_FAST);
	   		     		imageThread = MultiDBImage.getScaledImage(imagen,dim.width, dim.height);
	    				break;
	    			case(URL_TYPE):
		    			imagen=MultiDBImage.getImageFromUrl(this.sourceName);
		    			//imageThread = imagen.getScaledInstance(dim.width, dim.height, Image.SCALE_FAST);
	   		     		imageThread = MultiDBImage.getScaledImage(imagen,dim.width, dim.height);  		    	
	    				break;
	    			case(IMAGE_TYPE):
	    				if (this.imageThread==null){
	        				Errors.writeError(Errors.VAR_NULL,"Error in MultiDBImage when putting image on label");
	        			}
	    				imagen=imageThread;
	    				//imageThread = imagen.getScaledInstance(dim.width, dim.height, Image.SCALE_FAST);
	   		     		imageThread = MultiDBImage.getScaledImage(imagen,dim.width, dim.height);
	    				break;
	    			}
	    			scaledIcon = new ImageIcon();
	    		    scaledIcon.setImage(imageThread);
	    		    this.label.setIcon(scaledIcon);
	    		    this.label.repaint();
	    		}
    		}
    }

}
