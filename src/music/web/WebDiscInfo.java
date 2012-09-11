package music.web;

import java.io.*;

public class WebDiscInfo {
	
		private static final long serialVersionUID = 1L;
	    public int id;
	    public String group;
	    public String title;
	    public String year;
	    public String style;
	    public String loc;
	    public String link;
	    public String type;
	    public String copy;
	    public String mark;
	    public String review;
	    public File path;

	    public WebDiscInfo() {
	    	this.reset();
	    }
	    
	    public String getCopy() {
	        return copy;
	    }

	    public void setCopy(String copy) {
	        this.copy = copy;
	    }

	    public String getGroup() {
	        return group;
	    }

	    public void setGroup(String group) {
	        this.group = group;
	    }

	    public String getLoc() {
	        return loc;
	    }

	    public void setLoc(String loc) {
	        this.loc = loc;
	    }

	    public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getMark() {
	        return mark;
	    }

	    public void setMark(String mark) {
	        this.mark = mark;
	    }

	    public File getPath() {
	        return path;
	    }

	    public void setPath(File path) {
	        this.path = path;
	    }

	  
	    public String getReview() {
	        return review;
	    }

	    public void setReview(String review) {
	        this.review = review;
	    }

	    public String getStyle() {
	        return style;
	    }

	    public void setStyle(String style) {
	        this.style = style;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }

	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    public String getYear() {
	        return year;
	    }

	    public void setYear(String year) {
	        this.year = year;
	    }
	    public void reset(){
	    	this.id=0;
	        this.group="";
	        this.title="";
	        this.year="";
	        this.style="";
	        this.loc="";
	        this.type="";
	        this.copy="";
	        this.mark="";
	        this.review="";
	    }

}
