package image;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
	public Image thumbNail;
	public int width;
	public int height;
	public int fileSize;
	public File path;
	public String url;
	public String type;
	public String name;


	private ImageIcon origIcon, scaledIcon;
	
	
    public MultiDBImage() {
		super();
		this.origIcon = new ImageIcon();
		this.scaledIcon = new ImageIcon();
		this.width = COVERS_DIM.width;
		this.height = COVERS_DIM.height;
		this.fileSize = 0;
		this.url = "";
		this.path=null;
		this.name=null;
	}

	/*public static Image getImageFromUrl2(String urlstring){
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
    }*/
	
	
	public static Image getImageFromUrl(String urlstring){
		try {
			URL server = new URL(urlstring);
		    HttpURLConnection connection = (HttpURLConnection)server.openConnection();
		    connection.setRequestMethod("GET");
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    connection.setUseCaches(false);
		    connection.addRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, */*");
		    //connection.addRequestProperty("Accept-Language", "en-us,zh-cn;q=0.5");
		    connection.addRequestProperty("Accept-Encoding", "gzip, deflate");
		    connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 2.0.50727; MS-RTC LM 8)");
		    connection.connect();
		    InputStream is = connection.getInputStream();	
					
		    Image image = ImageIO.read(is);
		    is.close();
		    return image;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Errors.writeError(Errors.WEB_ERROR_NOT_FOUND,urlstring);
			//e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Errors.writeError(Errors.GENERIC_STACK_TRACE,e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}
	
	public void setImageFromUrl(String urlstring){
		this.image=getImageFromUrl(urlstring);
	}
	
	public void setImageFromUrl(){
		this.image=getImageFromUrl(this.url);
	}
	
	public static Image getImageFromFile(File path){
		return getImageFromFile(path.getAbsolutePath());
	} 
	
	public static Image getImageFromFile(String path){
		ImageIcon icon = new ImageIcon(path);
    	return icon.getImage();
	} 
	
	public void setThumbNailFromUrl(String urlstring){
		this.thumbNail=getImageFromUrl(urlstring);
	}
	
	public void setThumbNailFromUrl(){
		this.thumbNail=getImageFromUrl(this.url);
	}
	
	public void setImageFromFile(){
		if (this.path!=null){
			this.image=getImageFromFile(this.path);
			this.width=this.image.getWidth(null);
			this.height=this.image.getHeight(null);
		}
	} 
	
	public void setImageFromFile(File pathToFile){
		if (pathToFile!=null){
			this.image=getImageFromFile(pathToFile);
			this.width=this.image.getWidth(null);
			this.height=this.image.getHeight(null);
		}
	} 
	
	public void setImageFromFile(String pathToFile){
		if (pathToFile!=null){
			File file=new File(pathToFile);
			this.setImageFromFile(file);
		}
	} 
	
    public void putImage(JLabel labelFrom,int type, String name, Dimension dim) {
    	PutImage putImageThread = new PutImage(labelFrom,type,name,dim);
    	putImageThread.start();
    }
    
    public void putImage(JLabel labelFrom,int type, String name) {
    	PutImage putImageThread = new PutImage(labelFrom,type,name,COVERS_DIM);
    	putImageThread.start();
    }
    
    public void putImage(JLabel labelFrom,MultiDBImage im) {
    	putImage(labelFrom,im,COVERS_DIM);
    }
    
    public void putImage(JLabel labelFrom,MultiDBImage im,Dimension dim) {
    	if (im.image!=null) putImage(labelFrom,im.image,dim);
    	else if (im.thumbNail!=null) putImage(labelFrom,im.thumbNail,dim);
    	else Errors.writeError(Errors.IMAGE_NOT_FOUND, "Image not present before putting image");
    }
    
    public void putImage(JLabel labelFrom,Image image) {
    	PutImage putImageThread = new PutImage(labelFrom,image,COVERS_DIM);
    	putImageThread.start();
    }
    
    public void putImage(JLabel labelFrom,Image image,Dimension dim) {
    	PutImage putImageThread = new PutImage(labelFrom,image,dim);
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
    
    public void writeImageToFile(){    	
    	if (this.image==null){
    		if ((this.thumbNail!=null)&&(this.url!=null)) writeImageToFile(this.path,toBufferedImage(getImageFromUrl(this.url)),this.type);
    	}else writeImageToFile(this.path,toBufferedImage(this.image),this.type);
    }
    
    public static void writeImageToFile(File file,BufferedImage bufferedImage,String type) {
    	
    	try {
			ImageIO.write(bufferedImage,type,file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void writeImageToFile(String path,BufferedImage bufferedImage,String type) {
    	File file = new File(path);
    	writeImageToFile(file,bufferedImage,type);
    }
    
    public String toString(){
    	if (this.name==null){
	    	String name="";
	    	if (this.path!=null) name=this.path.getName();
	    	return "Dim: "+this.width+"x"+this.height+" "+name;
    	}else {
    		return this.name;
    	}
    }
    
    
 // This method returns a buffered image with the contents of an image
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }
    
 // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }
    
  //PUT IMAGE/////////////////////////////////////////////////////////////////////////  
    public class PutImage extends Thread {
    	   
    	    private JLabel label;
    	    private String sourceName="";
    	    private int type=0;
    	    private Dimension dim=COVERS_DIM;
    	    private Image imageThread;
    	    private Image tempIm;
    	   
    	    
    	    public PutImage(JLabel label,int type,String name,Dimension dim) {
    			super();
    			this.label=label;
    			this.type=type;
    			if (dim==null) this.dim=COVERS_DIM; else this.dim=dim;  
    			this.sourceName=name;
    		}
    	    
    	    public PutImage(JLabel label,Image image,Dimension dim) {
    			super();
    			this.label=label;
    			this.type=IMAGE_TYPE;
    			this.imageThread=image;
    			if (dim==null) this.dim=COVERS_DIM; else this.dim=dim;  
    		}
    	   		

    		@Override
    		public void run() {
    			if ((dim.width>MAX_COVERS_DIM.width)&&(dim.height>MAX_COVERS_DIM.height)){
					dim.width=MAX_COVERS_DIM.width;
					dim.height=MAX_COVERS_DIM.height;
				}
    			if (this.label==null){
    				Errors.writeError(Errors.VAR_NULL,"Error in MultiDBImage when putting image on label");
    			}else{
	    			switch(this.type){
	    			case(FILE_TYPE):
	    				origIcon = new ImageIcon(sourceName);
	   		     		tempIm = origIcon.getImage();
	   		     		//imageThread = imagen.getScaledInstance(dim.width, dim.height, Image.SCALE_FAST);
	   		     		imageThread = MultiDBImage.getScaledImage(tempIm,dim.width, dim.height);
	    				break;
	    			case(URL_TYPE):
	    				tempIm=MultiDBImage.getImageFromUrl(this.sourceName);
		    			//imageThread = imagen.getScaledInstance(dim.width, dim.height, Image.SCALE_FAST);
	   		     		imageThread = MultiDBImage.getScaledImage(tempIm,dim.width, dim.height);  		    	
	    				break;
	    			case(IMAGE_TYPE):
	    				if (this.imageThread==null){
	        				Errors.writeError(Errors.VAR_NULL,"Error in MultiDBImage when putting image on label");
	        			}
	    				tempIm=imageThread;
	    				//imageThread = imagen.getScaledInstance(dim.width, dim.height, Image.SCALE_FAST);
	   		     		imageThread = MultiDBImage.getScaledImage(tempIm,dim.width, dim.height);
	    				break;
	    			}
	    			scaledIcon = new ImageIcon();
	    		    scaledIcon.setImage(imageThread);
	    		    this.label.setIcon(scaledIcon);
	    		}
    		}
    }

}
