package docs.db;

import java.io.*;
import java.text.*;


public class Doc implements Serializable, Comparable<Object> {

    /**
	 * 
	 */
		
	private static final long serialVersionUID = 1L;
	static public int caseCompare = 0;
    public int id;
    public String title;
    public String loc;
    public DocTheme theme;
	public String comments;

    public Doc() {
    }
    //interface comparable, para ordenar primero por t�tulo y luego por a�o

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
		if (theme==DocTheme.HISTORY){
			return "HISTORY";
		}else if(theme==DocTheme.WARFARE){
			return "WARFARE";
		}else if(theme==DocTheme.MISC){
			return "MISC";
		}else if(theme==DocTheme.F1){
			return "F1";
		}else if(theme==DocTheme.SECONDWW){
			return "SECONDWW";
		}else return "MISC";
	}

	public void setTheme(DocTheme theme) {
		this.theme = theme;
	}
	
	public void setThemeByString(String theme) {
		DocTheme aux=DocTheme.MISC;
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
		this.theme = aux;
	}
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}


    public void reset(){
    	this.id=0;
        this.title="";
        this.loc="";
        this.theme=DocTheme.MISC;
        this.comments="";
    }
    
    
}
