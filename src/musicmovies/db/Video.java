package musicmovies.db;

import java.io.*;
import java.text.*;

public class Video implements Serializable, Comparable<Object> {


    public static final int COL_ID=0;
    public static final int COL_GROUP=1;
    public static final int COL_TITLE=2;
    public static final int COL_STYLE=3;
    public static final int COL_YEAR=4;
    public static final int COL_LOC=5;
    public static final int COL_COPY=6;
    public static final int COL_TYPE=7;
    public static final int COL_MARK=8;
    public static final int COL_REVIEW=9;

	
	private static final long serialVersionUID = 1L;
	static public int caseCompare = 0;
    public Long id;
    public String group;
    public String title;
    public String year;
    public String style;
    public String loc;
    public String type;
    public String copy;
    public String mark;
    public String review;
    
    public Video() {
    }
    
    public String[] toStringArray(){
    	String[] ret = new String[10];
    	ret[COL_ID]=Long.toString(this.id);
    	ret[COL_GROUP]=this.group;
    	ret[COL_TITLE]=this.title;
    	ret[COL_STYLE]=this.style;
    	ret[COL_YEAR]=this.year;
    	ret[COL_LOC]=this.loc;
    	ret[COL_COPY]=this.copy;
    	ret[COL_TYPE]=this.type;
    	ret[COL_MARK]=this.mark;
    	ret[COL_REVIEW]=this.review;
    	return ret;
    }
    
    
    public void setFromStringArray(String[] array){
    	try{
	    	this.id=Long.parseLong(array[COL_ID]);
	    	this.group=array[COL_GROUP];
	        this.title=array[COL_TITLE];
	        this.style=array[COL_STYLE];
	        try{
	        	Integer.valueOf(array[COL_YEAR]);
	        	this.year=array[COL_YEAR];
        	}catch(NumberFormatException ex){
        		this.year="0";
        	}
	        this.loc=array[COL_LOC];
	        this.copy=array[COL_COPY];
	        this.type=array[COL_TYPE];
	        this.mark=array[COL_MARK];
	        this.review=array[COL_REVIEW];
    	}catch(NumberFormatException ex){
    		this.reset();
    	}
    }
    
  //interface comparable, para ordenar primero por titulo y luego por anho

    
    public int compareTo(Object otherVideo) {
        int valor = 0;
        Collator comparador = Collator.getInstance();
        comparador.setStrength(Collator.PRIMARY);

        switch (caseCompare) {
            case 0:
			valor = -1* comparador.compare(((Video) otherVideo).group, this.group);
			if (valor == 0) {
				if ((this.year != null) && (((Video) otherVideo).year) != null) {
					valor = this.year.compareTo(((Video) otherVideo).year);
					if (valor == 0) {
						if ((this.title != null) && (((Video) otherVideo).title) != null) {
							valor = this.title.compareTo(((Video) otherVideo).title);
						}
					}
				} else {
					valor = 1;
				}
			}
			break;
		case 1:
			if ((this.group != null) && (((Video) otherVideo).group) != null) {
				valor = this.group.compareTo(((Video) otherVideo).group);
			}
			break;
		case 2:
			if ((this.title != null) && (((Video) otherVideo).title) != null) {
				valor = this.title.compareTo(((Video) otherVideo).title);
			}
			break;
		case 3:
			if ((this.style != null) && (((Video) otherVideo).style) != null) {
				valor = this.style.compareTo(((Video) otherVideo).style);
			}
			break;
		case 4:
			if ((this.year != null) && (((Video) otherVideo).year) != null) {
				valor = this.year.compareTo(((Video) otherVideo).year);
			}
			break;
		case 5:
			if ((this.loc != null) && (((Video) otherVideo).loc) != null) {
				valor = this.loc.compareTo(((Video) otherVideo).loc);
			}
			break;
		case 6:
			if ((this.copy != null) && (((Video) otherVideo).copy) != null) {
				valor = this.copy.compareTo(((Video) otherVideo).copy);
				if (valor == 0) {
					if ((this.group != null) && (((Video) otherVideo).group) != null) {
						valor = this.group.compareTo(((Video) otherVideo).group);
					} else {
						valor = 1;
					}
				}
			}
			break;
		case 7:
			if ((this.type != null) && (((Video) otherVideo).type) != null) {
				valor = this.type.compareTo(((Video) otherVideo).type);
			}
			break;
		case 8:
			if ((this.mark != null) && (((Video) otherVideo).mark) != null) {
				valor = this.mark.compareTo(((Video) otherVideo).mark);
			}
			break;
		case 9:    //for comparing titles via web 
			comparador.setStrength(Collator.TERTIARY);
			valor = -1* comparador.compare(((Video) otherVideo).title, this.title);
			break;
		default:
			valor = -1* comparador.compare(((Video) otherVideo).group, this.group);
			if (valor == 0) {
				if ((this.year != null) && (((Video) otherVideo).year) != null) {
					valor = this.year.compareTo(((Video) otherVideo).year);
				} else {
					valor = 1;
				}
			}
			break;
        }
        return (valor);
    }
    
    

    public int getId() {
		return id.intValue();
	}

	public void setId(int id) {
		this.id = new Long(id);
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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
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
   
	public String toString(){
		return  "Id: "+this.id+" ,group: "+this.group+" ,title: "+this.title+", year: "+this.year+" ,style: "+this.style+" ,style: "+this.style+" ,loc: "+this.loc+" ,copy: "+this.copy+" ,type: "+this.type+" ,mark: "+this.mark+" ,review: "+this.review;
	}

	public void reset(){
    	this.id=new Long(0);
        this.group="";
        this.title="";
        this.year="0";
        this.style="";
        this.loc="";
        this.type="";
        this.copy="";
        this.mark="";
        this.review="";
    }
}
