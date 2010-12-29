package movies.db;

import java.io.*;
import java.text.*;

public class Movie implements Serializable, Comparable<Object> {

    /**
	 * 
	 */
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
    //interface comparable, para ordenar primero por t�tulo y luego por a�o

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
    public void reset(){
    	this.id=0;
        this.title="";
        this.year="";
        this.director="";
        this.loc="";
        this.other="";
        this.review="";
        this.present="";
        this.path=null;
    }
    
    
}
