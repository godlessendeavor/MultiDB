package image;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpringLayout;

import main.MultiDB;
import music.db.Disc;



public class ImageDealer {
	
	public static final int FRONT_COVER = 0;
	public static final int BACK_COVER = 1;
	public static final String FRONT_STRING = "front";
	public static final String BACK_STRING = "back";
    public static final Dimension COVERS_DIM = new Dimension(400,400);
    public static final Dimension MAX_COVERS_DIM = new Dimension(1200,800);
    public static final int SEEK_NUMBER_LOW = 4;
    public static final int SEEK_NUMBER_MAX = 8;
    public static final String BING_SEARCH = "Bing";
    
    private static final String NORTH = SpringLayout.NORTH;
    private static final String SOUTH = SpringLayout.SOUTH;
    private static final String WEST = SpringLayout.WEST;
    private static final String EAST = SpringLayout.EAST;
    
	private static final String bingUrl1=MultiDB.webBing1;
	private static final String bingUrl2=MultiDB.webBing2;
	

	public static boolean frontCover=true;
	public static boolean otherCover=false;
    Disc currentDisc;
    MultiDBImage multiIm;
    private JLabel currentLabel; 
    private File currentPath;
    
    protected JLabel selectCoversView;
    protected JFrame selectCoverFrame;
    protected JSpinner spinnerCovers;
    protected JButton saveCoverButton,deleteCoverButton;
    protected JRadioButton backRButton,frontRButton,otherRButton;
    protected JLabel nofPicLabel,picNLabel;
    protected JTextField newNameField;
    protected SpinnerListModel spinnerCoversM;
    protected ArrayList<MultiDBImage> imageList = new ArrayList<MultiDBImage>();
	protected String bingUrl;
	protected ViewCoverHandler viewHandler;   
	
	
    
    public ImageDealer() {
    	bingUrl = bingUrl1+SEEK_NUMBER_LOW+bingUrl2;
    	selectFrameInit();
	}
    
    public ImageDealer(int seekNumber) {
    	if (seekNumber>SEEK_NUMBER_MAX) seekNumber=SEEK_NUMBER_MAX;
    	if (seekNumber<1) seekNumber=SEEK_NUMBER_LOW;
    	bingUrl = bingUrl1+seekNumber+bingUrl2;
    	selectFrameInit();
	}
    
    public ImageDealer(Disc disc) {
    	currentDisc=disc;
    	selectFrameInit();
	}
    
    public void setDisc(Disc disc){
    	currentDisc=disc;
    }
    
    public MultiDBImage getImage(){
    	return multiIm;
    }


	public void selectFrameInit(){
        ///////////setting icons for pictures
		frontCover=true;
        multiIm = new MultiDBImage();
        if (selectCoverFrame!=null) selectCoverFrame.dispose();
	    selectCoverFrame = new JFrame("Select a picture");
	    selectCoverFrame.setSize(500, 580);
	    selectCoversView = new JLabel();
	    selectCoversView.setMinimumSize(COVERS_DIM);	    
	    
	    spinnerCoversM = new SpinnerListModel();	
	    try{
	    	spinnerCoversM.setList(imageList);
	    }catch(IllegalArgumentException ilex){
	    	//do nothing
	    	//this exception breaks when the list has 0 members, like the first time this function is invoked
	    }
	    spinnerCovers = new JSpinner(spinnerCoversM);
	    JComponent field = ((JSpinner.DefaultEditor) spinnerCovers.getEditor());
	    Dimension prefSize = field.getPreferredSize();
	    prefSize = new Dimension(300, prefSize.height);
	    field.setPreferredSize(prefSize);
	    saveCoverButton = new JButton("Save current cover");
	    deleteCoverButton = new JButton("Delete current cover");
	    //handler to view covers on selectFrameCover
        viewHandler = new ViewCoverHandler(this);
        spinnerCovers.addChangeListener(viewHandler);
	    SaveCurrentCoverHandler saveCurrentCoverHandler = new SaveCurrentCoverHandler(this);
	    saveCoverButton.addActionListener(saveCurrentCoverHandler);
	    DeleteCurrentCoverHandler deleteCurrentCoverHandler = new DeleteCurrentCoverHandler(this);
	    deleteCoverButton.addActionListener(deleteCurrentCoverHandler);

	    //Create the radio buttons.
	    frontRButton = new JRadioButton("Front");
	    frontRButton.setSelected(true);

	    backRButton = new JRadioButton("Back");
	    otherRButton = new JRadioButton("Other");

	    //Group the radio buttons.
	    ButtonGroup frontBackSelect = new ButtonGroup();
	    frontBackSelect.add(frontRButton);
	    frontBackSelect.add(backRButton);
	    frontBackSelect.add(otherRButton);
	    
	    SelectTypeCoverHandler selectTypeCoverHandler = new SelectTypeCoverHandler(this);
	    frontRButton.addActionListener(selectTypeCoverHandler);
	    backRButton.addActionListener(selectTypeCoverHandler);
	    otherRButton.addActionListener(selectTypeCoverHandler);
	    
	    nofPicLabel = new JLabel("Number of pics: ");
	    //picNLabel = new JLabel("Pic number: ");
	    newNameField = new JTextField(30);	    
	    newNameField.setEnabled(false);
	    
	    //set layout
	    SpringLayout layout = new SpringLayout();
	    selectCoverFrame.getContentPane().setLayout(layout);
	    
	    
	    
	    selectCoverFrame.getContentPane().add(spinnerCovers);
	    selectCoverFrame.getContentPane().add(frontRButton);
	    selectCoverFrame.getContentPane().add(backRButton);
	    selectCoverFrame.getContentPane().add(otherRButton);
	    selectCoverFrame.getContentPane().add(nofPicLabel);
	    //selectCoverFrame.getContentPane().add(picNLabel);
	    selectCoverFrame.getContentPane().add(newNameField);
	    selectCoverFrame.getContentPane().add(saveCoverButton);
	    selectCoverFrame.getContentPane().add(deleteCoverButton);
	    
	    layout.putConstraint(NORTH, spinnerCovers,0, NORTH, selectCoverFrame.getContentPane());
	    layout.putConstraint(WEST, spinnerCovers,0, WEST, selectCoverFrame.getContentPane());
	    layout.putConstraint(NORTH, frontRButton,3, SOUTH, spinnerCovers);
	    layout.putConstraint(NORTH, backRButton,3, SOUTH, frontRButton);
	    layout.putConstraint(NORTH, otherRButton,3, SOUTH, backRButton);
	    layout.putConstraint(NORTH, saveCoverButton,3, SOUTH, spinnerCovers);
	    layout.putConstraint(WEST, saveCoverButton,50, EAST, frontRButton);
	    layout.putConstraint(NORTH, deleteCoverButton,3, SOUTH, saveCoverButton);
	    layout.putConstraint(WEST, deleteCoverButton,50, EAST, frontRButton);    
	    layout.putConstraint(NORTH, nofPicLabel,3, SOUTH, spinnerCovers);
	    layout.putConstraint(WEST, nofPicLabel,50, EAST, deleteCoverButton);
	    //layout.putConstraint(NORTH, picNLabel,3, SOUTH, nofPicLabel);
	    //layout.putConstraint(WEST, picNLabel,50, EAST, deleteCoverButton);
	    layout.putConstraint(NORTH, newNameField,3, SOUTH, otherRButton);
	    layout.putConstraint(SOUTH, selectCoversView,-3, SOUTH, selectCoverFrame.getContentPane());
	    layout.putConstraint(WEST, selectCoversView,20, WEST, selectCoverFrame.getContentPane());
	    
	    FrameCloseHandler frameCloseHandler = new FrameCloseHandler();
	    selectCoverFrame.addWindowListener(frameCloseHandler);
    }
    
	private class FrameCloseHandler extends WindowAdapter{
		@Override
	    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
			ImageDealer.this.showDiscCover(ImageDealer.this.currentPath, ImageDealer.this.currentLabel, FRONT_COVER);
		    selectCoverFrame.dispose();
	    }
	}
    
    public void searchImage(String name,String type){
    	selectFrameInit();
    	SearchImageInternet searchImageInternetThread = new SearchImageInternet(this, name);
    	searchImageInternetThread.type=type;
    	searchImageInternetThread.start();
	}    
    
    public Dimension showCurrentImageInLabel(JLabel labelIn){
    	return multiIm.putCurrentImageMaxDim(labelIn);		
    } 
    
    private ArrayList<MultiDBImage> getListOfImages(File pathDisc){
    	int numArchivos;    
    	String[] imageNamesList;
    	MultiDBImage tempIm;
    	ArrayList<MultiDBImage> imList = new ArrayList<MultiDBImage>();
    
    	imageNamesList = pathDisc.list();
		if (imageNamesList!=null){
			numArchivos = imageNamesList.length;
			imList.clear();
			
			for (int i = 0; i < numArchivos; i++) {
				String currentImageName = imageNamesList[i].toLowerCase();
				if (((currentImageName.indexOf(".jpg") > -1) || (currentImageName.indexOf(".gif") > -1)|| (currentImageName.indexOf(".png")) > -1)) {
					tempIm=new MultiDBImage();
					tempIm.path = new File(pathDisc.getAbsolutePath() + File.separator + imageNamesList[i]);
					tempIm.name = imageNamesList[i];
					//System.out.println("Found image file: "+tempIm.name);
					imList.add(tempIm);					
				}
			}
		}
		return imList;
    }
    
    public void showImages(File pathDisc){    	
    	if ((imageList=getListOfImages(pathDisc)).size()>0){
    		showListOfImages();
    	}else JOptionPane.showMessageDialog(selectCoverFrame,"Cannot find images");
    }
    
    private void showListOfImages(){
    	if (imageList.size()>0){
			for (int i=0;i<imageList.size();i++){
				imageList.get(i).setImageFromFile();
			}
			selectFrameInit();
			nofPicLabel.setText("Number of pics: "+imageList.size());
			spinnerCoversM.setList(imageList);
			//System.out.println(imageListWeb.get(0).width);
			multiIm.putImage(selectCoversView, imageList.get(0));
			selectCoverFrame.getContentPane().add(selectCoversView);
			selectCoverFrame.setVisible(true);
		}
    }
    
    public boolean showDiscCover(File pathDisc,JLabel labelIn,int type){
    	return showDiscCover(pathDisc, labelIn, type, COVERS_DIM);
    }
    
    public boolean showDiscCover(File pathDisc,JLabel labelIn,int type, Dimension dim){
    	int indexCover=0;
    	boolean found=false;
    	currentLabel = labelIn;
    	currentPath = pathDisc; 
    	String stringSearch="";
    	
		imageList.clear();			
		imageList=getListOfImages(pathDisc);
		
		if (imageList.size()<1) return false;
		if (type == ImageDealer.FRONT_COVER) stringSearch=FRONT_STRING;
		if (type == ImageDealer.BACK_COVER) stringSearch=BACK_STRING;		
		
		for (int i = 0; i < imageList.size(); i++) {
			String currentImageName = imageList.get(i).name.toLowerCase();
			if (currentImageName.indexOf(stringSearch) > -1) {
				found = true;
				indexCover = i;
				break;
			}
		}
			
		if (found) {
			if (type == FRONT_COVER) frontCover = true; else frontCover = false;
			multiIm = new MultiDBImage();
			multiIm.putImage(labelIn, MultiDBImage.FILE_TYPE, imageList.get(indexCover).path.getAbsolutePath(), dim);
		} else  showListOfImages();			
		return true;
    }
    

}
