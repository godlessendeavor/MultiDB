package image;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import main.Errors;
import web.WebReader;

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


	private ImageIcon scaledIcon;
	
	
    public MultiDBImage() {
		super();
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
	    
    
	public Image getImageFromUrl(String urlstring,boolean prog){
		try {
			InputStream finalStream;
			URL server = new URL(urlstring);
		    HttpURLConnection connection = (HttpURLConnection)server.openConnection();
		    connection.setRequestMethod("GET");
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    connection.setUseCaches(false);
		    connection.addRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, */*");
		    connection.addRequestProperty("Accept-Encoding", "gzip, deflate");
		    connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 2.0.50727; MS-RTC LM 8)");
		    connection.connect();
		    InputStream is = connection.getInputStream();	
		    finalStream = is;
		    if ("gzip".equals(connection.getContentEncoding())){
		    	//decompress stream
		    	finalStream = WebReader.decompressStream(is);		    	
		    }

		    Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix("jpg");
		    if (!readers.hasNext()){
		    	Errors.writeError(Errors.WEB_NOT_FOUND,urlstring);
		    	return null;
		    }
		    ImageReader imageReader = (ImageReader) readers.next();
		    
		    
		    String formatName = imageReader.getFormatName();
		    if (!formatName.equalsIgnoreCase("jpeg") && !formatName.equalsIgnoreCase("png") && !formatName.equalsIgnoreCase("gif")) {	    	
		    	return null;
		    }
		    		    
		    ImageInputStream imageInputStream = ImageIO.createImageInputStream(finalStream);
		    imageReader.setInput(imageInputStream, false);
		    if (prog){
		    	MultiIIOReadProgressListener prListener = new MultiIIOReadProgressListener();
		    	imageReader.addIIOReadProgressListener(prListener);
		    }
		    BufferedImage caption = imageReader.read(0);
		    /*Image image = ImageIO.read(is);*/
		    is.close();
		    return toImage(caption);
		} catch (MalformedURLException e) {
			Errors.writeError(Errors.WEB_MALF_URL,urlstring);
			//e.printStackTrace();
		} catch (IOException e) {
			Errors.writeError(Errors.WEB_ERROR_NOT_FOUND,urlstring);
			//e.printStackTrace();
		} catch (Exception e) {
			Errors.writeError(Errors.GENERIC_ERROR,e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}
	
	public void setImageFromUrl(String urlstring){
		this.image=getImageFromUrl(urlstring,true);
	}
	
	public void setImageFromUrl(){
		this.image=getImageFromUrl(this.url,true);
	}
	
	public static Image getImageFromFile(File path){
		return getImageFromFile(path.getAbsolutePath());
	} 
	
	public static Image getImageFromFile(String path){
		Image tempIm = null;
	 	File file = new File(path);
	 	ImageInputStream iis;
		try {
			iis = new FileImageInputStream(file);
			 try {
	    	     for (Iterator<ImageReader> i = ImageIO.getImageReaders(iis); tempIm == null && i.hasNext(); ) {
	    	         ImageReader r = i.next();
	    	         try {
	    	             r.setInput(iis);
	    	             tempIm = r.read(0);
	    	         } catch (IOException e) {
	    	        	 Errors.writeError(Errors.FILE_IO_ERROR, path);
	    	         }
	    	     }
	    	 } finally {
	    	     iis.close();
	    	 }
		} catch (FileNotFoundException e1) {
			Errors.writeError(Errors.FILE_NOT_FOUND, path);
		} catch (IOException e2) {
			Errors.writeError(Errors.FILE_IO_ERROR, path);
		}
		
    	return tempIm;
	} 
	
	public void setThumbNailFromUrl(String urlstring){
		this.thumbNail=getImageFromUrl(urlstring,false);
	}
	
	public void setThumbNailFromUrl(){
		this.thumbNail=getImageFromUrl(this.url,false);
	}
	
	public void setImageFromFile(){
		if (this.path!=null){
			this.image=getImageFromFile(this.path);
			if (this.image != null){
				this.width=this.image.getWidth(null);
				this.height=this.image.getHeight(null);
			}		
		}
	} 
	
	public void setImageFromFile(File pathToFile){
		if (pathToFile!=null){
			this.image=getImageFromFile(pathToFile);
			if (this.image != null){
				this.width=this.image.getWidth(null);
				this.height=this.image.getHeight(null);
			}
		}
	} 
	
	public void setImageFromFile(String pathToFile){
		if (pathToFile!=null){
			File file=new File(pathToFile);
			this.setImageFromFile(file);
		}
	} 
	
    public void putImage(JLabel labelFrom,int type, String name, Dimension dim) {
    	this.name = name;
    	PutImage putImageThread = new PutImage(labelFrom,type,name,dim);
    	putImageThread.start();
    }
    
    public void putImage(JLabel labelFrom,int type, String name) {
    	this.name = name;
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
    
    public void putImage(JLabel labelFrom,Dimension dim) {
    	putImage(labelFrom,this,dim);
    }
    public Dimension putImage(JLabel labelFrom) {
    	Dimension dim = new Dimension(this.image.getWidth(null),this.image.getHeight(null));
    	putImage(labelFrom,this,dim);
    	return dim;
    }
    
    public void putImage(JLabel labelFrom,Image image) {
    	PutImage putImageThread = new PutImage(labelFrom,image,COVERS_DIM);
    	putImageThread.start();
    }
    
    public Dimension putCurrentImageMaxDim(JLabel labelFrom) {
    	//Dimension dim = new Dimension(this.image.getWidth(null), this.image.getHeight(null));
    	PutImage putImageThread = new PutImage(labelFrom, FILE_TYPE, this.name, MAX_COVERS_DIM);
    	putImageThread.start();
    	synchronized(putImageThread){
             try{
            	 putImageThread.wait();
             }catch(InterruptedException e){
                 Errors.writeError(Errors.THREAD_INTERRUPTED_ERROR, "Interrupted thread when drawing image "+this.name);
             }
         }
    	return MAX_COVERS_DIM;
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
    		if ((this.thumbNail!=null)&&(this.url!=null)) 
    			writeImageToFile(this.path,(sun.awt.image.ToolkitImage)getImageFromUrl(this.url,true),this.type);
    	}else writeImageToFile(this.path, (sun.awt.image.ToolkitImage)this.image,this.type);
    }
    
    public static void writeImageToFile(File file,BufferedImage bufferedImage,String type) {
    	
    	try {
			ImageIO.write(bufferedImage,type,file);
		} catch (IOException e) {
			Errors.writeError(Errors.IMAGE_NOT_SAVED,": "+file.getAbsolutePath());
		}   	
 
    }   
    
    public static void writeImageToFile(File file, sun.awt.image.ToolkitImage image,String type) {
	
		BufferedImage bufferedImage= new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		writeImageToFile(file, bufferedImage, type);
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
    
  
    public static Image toImage(BufferedImage bufferedImage) {
        return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
    }
    

    
    private class MultiIIOReadProgressListener implements IIOReadProgressListener{
    	
    	JFrame infoFrame = new JFrame("Image download progress");
    	JTextArea infoText = new JTextArea();
    	 	
    	
    	public MultiIIOReadProgressListener() {
			super();
			infoFrame.add(infoText);
		}

		public void imageComplete(ImageReader source) {
			infoFrame.setVisible(false);
    	  }

    	  public void imageProgress(ImageReader source, float percentageDone) {
    		  infoText.setText("image progress: " + percentageDone + "%");
    	  }

    	  public void imageStarted(ImageReader source, int imageIndex) {
    		  infoFrame.setSize(500, 100);
    		  infoText.setWrapStyleWord(true);
    		  infoText.setLineWrap(true);   
    		  infoText.setText("image #" + imageIndex + " started " + source);
    		  infoFrame.setVisible(true);
    	  }

    	  public void readAborted(ImageReader source) {
    		  infoText.setText("Read aborted");
    	  }

    	  public void sequenceComplete(ImageReader source) {
    		  infoText.setText("sequence complete ");
    		  infoFrame.setVisible(false);
    	  }

    	  public void sequenceStarted(ImageReader source, int minIndex) {
    		  infoText.setText("sequence started: " + minIndex);
    	  }

    	  public void thumbnailComplete(ImageReader source) {
    		  infoText.setText("thumbnail complete " + source);
    	  }

    	  public void thumbnailProgress(ImageReader source, float percentageDone) {
    		  infoText.setText("thumbnail started " + source + ": " + percentageDone + "%");
    	  }

    	  public void thumbnailStarted(ImageReader source, int imageIndex, int thumbnailIndex) {
    		  infoText.setText("thumbnail progress, " + thumbnailIndex + " of "
    	        + imageIndex);
    	  }
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
    			synchronized(this){
    				if ((dim.width>MAX_COVERS_DIM.width)&&(dim.height>MAX_COVERS_DIM.height)){
    					dim.width=MAX_COVERS_DIM.width;
    					dim.height=MAX_COVERS_DIM.height;
    				}
        			if (this.label==null){
        				Errors.writeError(Errors.VAR_NULL,"Error in MultiDBImage when putting image on label");
        			}else{
    	    			switch(this.type){
    	    			case(FILE_TYPE):
    	   		     		imageThread = MultiDBImage.getScaledImage(getImageFromFile(sourceName),dim.width, dim.height);
        					tempIm = imageThread;
    	    				break;
    	    			case(URL_TYPE):
    	    				tempIm = MultiDBImage.this.getImageFromUrl(this.sourceName,true);
    	   		     		imageThread = MultiDBImage.getScaledImage(tempIm,dim.width, dim.height);  		    	
    	    				break;
    	    			case(IMAGE_TYPE):
    	    				if (this.imageThread==null){
    	        				Errors.writeError(Errors.VAR_NULL,"Error in MultiDBImage when putting image on label");
    	        			}
    	    				tempIm = imageThread;
    	   		     		imageThread = MultiDBImage.getScaledImage(tempIm,dim.width, dim.height);
    	    				break;
    	    			}
    	    			MultiDBImage.this.image=tempIm;
    	    			scaledIcon = new ImageIcon();
    	    		    scaledIcon.setImage(imageThread);
    	    		    this.label.setIcon(scaledIcon);
    	    		}
        			notify();
        		}
    	            
    	    }
    			
    }
    
    
    /*    public static BufferedImage toBufferedImage(Image image) {
	if (image instanceof BufferedImage) {
         return (BufferedImage)image;
    }
    BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    // Draw the image on to the buffered image
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(image, 0, 0, null);
    bGr.dispose();

    // Return the buffered image
    return bimage;
	}*/
    

   /*  // This method returns a buffered image with the contents of an image
    public static BufferedImage toBufferedImage2(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        System.out.println(image.getClass());

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
    }*/
    

}
