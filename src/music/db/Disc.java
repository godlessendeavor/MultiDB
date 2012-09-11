package music.db;

import java.io.*;
import java.text.*;
public class Disc implements Serializable, Comparable<Object> {

    /**
	  NUMBER OF COLUMN FOR TABLE MUSIC
	 */
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
    public static final int COL_PRESENT=10;
    public static final int COL_PATH=11;
    
   /**
	  NUMBER OF COLUMN FOR TABLE NEW_DISCS
	*/
  public static final int COL_NID=0;
  public static final int COL_NGROUP=1;
  public static final int COL_NTITLE=2;
  public static final int COL_NTYPE=3;
  public static final int COL_NSTYLE=4;
  public static final int COL_NYEAR=5;
  public static final int COL_NLOC=6;

	
	private static final long serialVersionUID = 1L;
	static public int caseCompare = 0;
    public int id;
    public String group;
    public String title;
    public String year;
    public String style;
    public String loc;
    public String type;
    public String copy;
    public String mark;
    public String review;
    public String present;
    public String link; //link for info in web
    public File path;

    public Disc() {
    }
    
    public String[] toStringArray(){
    	String[] ret = new String[12];
    	ret[COL_ID]=Integer.toString(this.id);
    	ret[COL_GROUP]=this.group;
    	ret[COL_TITLE]=this.title;
    	ret[COL_STYLE]=this.style;
    	ret[COL_YEAR]=this.year;
    	ret[COL_LOC]=this.loc;
    	ret[COL_COPY]=this.copy;
    	ret[COL_TYPE]=this.type;
    	ret[COL_MARK]=this.mark;
    	ret[COL_REVIEW]=this.review;
    	ret[10]=this.present;
    	ret[11]=this.path.getAbsolutePath();
    	return ret;
    }
    
    //the same as before but only for database relevant data
    public String[] toStringArrayRel(){
    	String[] ret = new String[10];
    	ret[COL_ID]=Integer.toString(this.id);
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
	    	this.id=Integer.parseInt(array[COL_ID]);
	    	this.group=array[COL_GROUP];
	        this.title=array[COL_TITLE];
	        this.style=array[COL_STYLE];
	        this.year=array[COL_YEAR];
	        this.loc=array[COL_LOC];
	        this.copy=array[COL_COPY];
	        this.type=array[COL_TYPE];
	        this.mark=array[COL_MARK];
	        this.review=array[COL_REVIEW];
	        this.present=new String("NO");
	        this.path=new File("");
    	}catch(NumberFormatException ex){
    		this.reset();
    	}
    }
    
  //interface comparable, para ordenar primero por titulo y luego por anho

    
    public int compareTo(Object otroDisco) {
        int valor = 0;
        Collator comparador = Collator.getInstance();
        comparador.setStrength(Collator.PRIMARY);

        switch (caseCompare) {
            case 0:
			valor = -1* comparador.compare(((Disc) otroDisco).group, this.group);
			if (valor == 0) {
				if ((this.year != null) && (((Disc) otroDisco).year) != null) {
					valor = this.year.compareTo(((Disc) otroDisco).year);
					if (valor == 0) {
						if ((this.title != null) && (((Disc) otroDisco).title) != null) {
							valor = this.title.compareTo(((Disc) otroDisco).title);
						}
					}
				} else {
					valor = 1;
				}
			}
			break;
		case 1:
			if ((this.group != null) && (((Disc) otroDisco).group) != null) {
				valor = this.group.compareTo(((Disc) otroDisco).group);
			}
			break;
		case 2:
			if ((this.title != null) && (((Disc) otroDisco).title) != null) {
				valor = this.title.compareTo(((Disc) otroDisco).title);
			}
			break;
		case 3:
			if ((this.style != null) && (((Disc) otroDisco).style) != null) {
				valor = this.style.compareTo(((Disc) otroDisco).style);
			}
			break;
		case 4:
			if ((this.year != null) && (((Disc) otroDisco).year) != null) {
				valor = this.year.compareTo(((Disc) otroDisco).year);
			}
			break;
		case 5:
			if ((this.loc != null) && (((Disc) otroDisco).loc) != null) {
				valor = this.loc.compareTo(((Disc) otroDisco).loc);
			}
			break;
		case 6:
			if ((this.copy != null) && (((Disc) otroDisco).copy) != null) {
				valor = this.copy.compareTo(((Disc) otroDisco).copy);
				if (valor == 0) {
					if ((this.group != null) && (((Disc) otroDisco).group) != null) {
						valor = this.group.compareTo(((Disc) otroDisco).group);
					} else {
						valor = 1;
					}
				}
			}
			break;
		case 7:
			if ((this.type != null) && (((Disc) otroDisco).type) != null) {
				valor = this.type.compareTo(((Disc) otroDisco).type);
			}
			break;
		case 8:
			if ((this.mark != null) && (((Disc) otroDisco).mark) != null) {
				valor = this.mark.compareTo(((Disc) otroDisco).mark);
			}
			break;
		case 9:    //for comparing titles via web 
			comparador.setStrength(Collator.TERTIARY);
			valor = -1* comparador.compare(((Disc) otroDisco).title, this.title);
			break;
		default:
			valor = -1* comparador.compare(((Disc) otroDisco).group, this.group);
			if (valor == 0) {
				if ((this.year != null) && (((Disc) otroDisco).year) != null) {
					valor = this.year.compareTo(((Disc) otroDisco).year);
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
    

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
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
    public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String toString(){
		return  "Id: "+this.id+" ,group: "+this.group+" ,title: "+this.title+", year: "+this.year+" ,style: "+this.style+" ,loc: "+this.loc+" ,copy: "+this.copy+" ,type: "+this.type+" ,mark: "+this.mark+" ,review: "+this.review+" ,link: "+this.link;
	}

	public void reset(){
    	this.id=0;
        this.group="";
        this.title="";
        this.year="0";
        this.style="";
        this.loc="";
        this.type="";
        this.copy="";
        this.mark="";
        this.review="";
        this.present="";
        this.link="";
        this.path=null;
    }
}
