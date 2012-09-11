package docs.db;

import java.io.*;
import java.text.*;


public class Doc implements Serializable, Comparable<Object> {

    public static final int COL_ID=0;
    public static final int COL_TITLE=1;
    public static final int COL_LOC=2;
    public static final int COL_THEME=3;
    public static final int COL_COMMENTS=4; 
		
	private static final long serialVersionUID = 1L;
	static public int caseCompare = 0;
    public int id;
    public String title;
    public String loc;
    public DocTheme theme;
	public String comments;

    public Doc() {
    }
    
    public String[] toStringArray(){
    	String[] ret = new String[5];
    	ret[COL_ID]=Integer.toString(this.id);
    	ret[COL_TITLE]=this.title;
    	ret[COL_LOC]=this.loc;
    	ret[COL_THEME]=DocTheme.getStringTheme(this.theme);
    	ret[COL_COMMENTS]=this.comments;
    	return ret;
    }
    
    //the same as before but only for database relevant data
    public String[] toStringArrayRel(){
    	return(this.toStringArray());
    }
    
    public void setFromStringArray(String[] array){
    	this.id=Integer.parseInt(array[COL_ID]);
        this.title=array[COL_TITLE];
        this.loc=array[COL_LOC];
        this.comments=array[COL_COMMENTS];
        this.theme=DocTheme.getDocTheme(array[COL_THEME]);
    }
    
    //interface comparable, para ordenar primero por titulo

    public int compareTo(Object otherDoc) {
        int valor = 0;
        Collator comparador = Collator.getInstance();
        comparador.setStrength(Collator.PRIMARY);

        switch (caseCompare) {
            case 0:
			valor = -1* comparador.compare(((Doc) otherDoc).title, this.title);
			if (valor == 0) {
				if ((this.title != null) && (((Doc) otherDoc).title) != null) {
					valor = this.title.compareTo(((Doc) otherDoc).title);
				} else {
					valor = 1;
				}
			}
			break;
		case 1:
			if ((this.title != null) && (((Doc) otherDoc).title) != null) {
				valor = this.title.compareTo(((Doc) otherDoc).title);
			}
			break;
		case 2:
			if ((this.loc != null) && (((Doc) otherDoc).loc) != null) {
				valor = this.loc.compareTo(((Doc) otherDoc).loc);
			}
			break;
		case 3:
			if ((this.theme != null) && (((Doc) otherDoc).theme) != null) {
				valor = this.theme.compareTo(((Doc) otherDoc).theme);
			}
			break;
		case 4:
			if ((this.comments != null) && (((Doc) otherDoc).comments) != null) {
				valor = this.comments.compareTo(((Doc) otherDoc).comments);
			}
			break;
		default:
			valor = -1* comparador.compare(((Doc) otherDoc).title, this.title);
			if (valor == 0) {
				if ((this.title != null) && (((Doc) otherDoc).title) != null) {
					valor = this.title.compareTo(((Doc) otherDoc).title);
				} else {
					valor = 1;
				}
			}
			break;
        }
        return (valor);
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
    public DocTheme getTheme() {
		return theme;
	}
    
	public String getThemeInString() {
		return DocTheme.getStringTheme(theme);
		/*if (theme==DocTheme.HISTORY){
			return "HISTORY";
		}else if(theme==DocTheme.WARFARE){
			return "WARFARE";
		}else if(theme==DocTheme.MISC){
			return "MISC";
		}else if(theme==DocTheme.F1){
			return "F1";
		}else if(theme==DocTheme.SECONDWW){
			return "SECONDWW";
		}else return "MISC";*/
	}

	public void setTheme(DocTheme theme) {
		this.theme = theme;
	}
	
	public void setThemeByString(String theme) {
		this.theme=DocTheme.getDocTheme(theme);
		/*DocTheme aux=DocTheme.MISC;
		if (theme.compareTo("HISTORY")==0){
			aux=DocTheme.HISTORY;
		}else if(theme.compareTo("WARFARE")==0){
			aux=DocTheme.WARFARE;
		}else if(theme.compareTo("MISC")==0){
			aux=DocTheme.MISC;
		}else if(theme.compareTo("F1")==0){
			aux=DocTheme.F1;
		}else if(theme.compareTo("SECONDWW")==0){
			aux=DocTheme.SECONDWW;
		}		
		this.theme = aux;*/
	}
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String toString(){
		return  "Id: "+this.id+" ,title: "+this.title+" ,loc: "+this.loc+" ,theme: "+this.theme+" ,comments: "+this.comments;
	}
	
    public void reset(){
    	this.id=0;
        this.title="";
        this.loc="";
        this.theme=DocTheme.MISC;
        this.comments="";
    }
    
    
}
