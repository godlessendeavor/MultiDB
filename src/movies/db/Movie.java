package movies.db;

import java.io.*;
import java.text.*;

public class Movie implements Serializable, Comparable<Object> {

	public static final int COL_ID=0;
    public static final int COL_TITLE=1;
    public static final int COL_DIR=2;
    public static final int COL_YEAR=3;
    public static final int COL_OTHER=4; 
    public static final int COL_LOC=5;
    public static final int COL_PRESENT=6;
    public static final int COL_PATH=7;
    public static final int COL_REVIEW=8;
    
	private static final long serialVersionUID = 1L;
	static public int caseCompare = 0;
    public int id;
    public String title;
    public String year;
    public String director;
    public String loc;
    public String other;
    public File path;
    public String review;
    public String present;

    public Movie() {
    }
    
    public String[] toStringArray(){
    	String[] ret = new String[6];
    	ret[COL_ID]=Integer.toString(this.id);
    	ret[COL_TITLE]=this.title;
    	ret[COL_DIR]=this.director;
    	ret[COL_YEAR]=this.year;
    	ret[COL_OTHER]=this.other;
    	ret[COL_LOC]=this.loc;
    	return ret;
    }
    
    //the same as before but only for database relevant data
    public String[] toStringArrayRel(){
    	return toStringArray();
    }
    
    public void setFromStringArray(String[] array){
    	this.id=Integer.parseInt(array[COL_ID]);
        this.title=array[COL_TITLE];
        this.director=array[COL_DIR];
        this.year=array[COL_YEAR];
        this.other=array[COL_OTHER];
        this.loc=array[COL_LOC];
        this.review=array[COL_REVIEW];
        this.present=new String("NO");
        this.path=new File("");
    }
    
    //interface comparable, para ordenar primero por titulo

    public int compareTo(Object otherMovie) {
        int valor = 0;
        Collator comparador = Collator.getInstance();
        comparador.setStrength(Collator.PRIMARY);

        switch (caseCompare) {
            case 0:
			valor = -1* comparador.compare(((Movie) otherMovie).title, this.title);
			if (valor == 0) {
				if ((this.year != null) && (((Movie) otherMovie).year) != null) {
					valor = this.year.compareTo(((Movie) otherMovie).year);
				} else {
					valor = 1;
				}
			}
			break;
		case 1:
			if ((this.title != null) && (((Movie) otherMovie).title) != null) {
				valor = this.title.compareTo(((Movie) otherMovie).title);
			}
			break;
		case 2:
			if ((this.director != null) && (((Movie) otherMovie).director) != null) {
				valor = this.director.compareTo(((Movie) otherMovie).director);
				if (valor == 0) {
					if ((this.year != null) && (((Movie) otherMovie).year) != null) {
						valor = this.year.compareTo(((Movie) otherMovie).year);
					} else {
						valor = 1;
					}
				}
			}
			break;
		case 3:
			if ((this.year != null) && (((Movie) otherMovie).year) != null) {
				valor = this.year.compareTo(((Movie) otherMovie).year);
			}
			break;
		case 4:
			if ((this.other != null) && (((Movie) otherMovie).other) != null) {
				valor = this.other.compareTo(((Movie) otherMovie).other);
			}
			break;
		case 5:
			if ((this.loc != null) && (((Movie) otherMovie).loc) != null) {
				valor = this.loc.compareTo(((Movie) otherMovie).loc);
			}
			break;
		default:
			valor = -1* comparador.compare(((Movie) otherMovie).title, this.title);
			if (valor == 0) {
				if ((this.year != null) && (((Movie) otherMovie).year) != null) {
					valor = this.year.compareTo(((Movie) otherMovie).year);
				} else {
					valor = 1;
				}
			}
			break;
        }
        return (valor);
    }
    
    
    public void check(){
    	try{
    		Integer.parseInt(this.year);
    	}catch(Exception ex){
    		this.year="0";
    	}
    }

//////////////////////////GETTERS AND SETTERS///////////////////////////////////////

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
	
	public String toString(){
		return  "Id: "+this.id+" ,title: "+this.title+" ,year: "+this.year+" ,director: "+this.director+" ,loc: "+this.loc+" ,other: "+this.other+" ,review: "+this.review;
	}
	
    public void reset(){
    	this.id=0;
        this.title="";
        this.year="0";
        this.director="";
        this.loc="";
        this.other="";
        this.review="";
        this.present="";
        this.path=null;
    }
    
    
}
