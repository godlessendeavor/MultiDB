package main;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
 
public class ProgressBarWindow{
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final Dimension dimRelate = new Dimension(300,120);
	private JFrame frame;
	private JProgressBar progressBar;
    private JLabel infoLabel;
    private int min,max,per;

    public ProgressBarWindow() {
        //Create and set up the window.
        frame = new JFrame("Progress");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        infoLabel = new JLabel();
        BoxLayout blay=new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS);
        frame.getContentPane().setLayout(blay);
        frame.add(progressBar,BorderLayout.CENTER);
        frame.add(new JScrollPane(infoLabel), BorderLayout.CENTER); 
    }
    
    public void setFrameSize(Dimension dim){
    	frame.setSize(dim);
    }
    
    public int startProgBar(int min,int max) {  
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (max<=min) return Errors.NEGATIVE_NUMBER;
        setMin(min);
        setMax(max);
        setPer(0);
        frame.setVisible(true);
        return 0;
    }
 
    public int startProgBar(int max) {
    	return startProgBar(0,max);  
    }
    
    public void setPer(int quant){
    	this.per=100*quant/(max-min);
    	progressBar.setValue(this.per);
    	if (quant==max) closeProgBar();
    	infoLabel.setText(String.format("Completed %d%% of task.\n", per));
    }
    
    public void setPer(int quant,String message){
    	setPer(quant);
    	infoLabel.setText(message);
    }   
    
    public void closeProgBar() {
        frame.dispose();
    }
  
    public void setMin(int min){
    	this.min=min;
    }
    
    public void setMax(int max){
    	this.max=max;
    }
    
 

}
