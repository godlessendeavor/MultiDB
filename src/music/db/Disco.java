package music.db;

import java.io.*;
import java.text.*;

public class Disco implements Serializable, Comparable<Object> {

    /**
	 * 
	 */
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
    public File path;

    public Disco() {
    }
    //interface comparable, para ordenar primero por t�tulo y luego por a�o

    public int compareTo(Object otroDisco) {
        int valor = 0;
        Collator comparador = Collator.getInstance();
        comparador.setStrength(Collator.PRIMARY);

        switch (caseCompare) {
            case 0:
			valor = -1* comparador.compare(((Disco) otroDisco).group, this.group);
			if (valor == 0) {
				if ((this.year != null) && (((Disco) otroDisco).year) != null) {
					valor = this.year.compareTo(((Disco) otroDisco).year);
					if (valor == 0) {
						if ((this.title != null) && (((Disco) otroDisco).title) != null) {
							valor = this.title.compareTo(((Disco) otroDisco).title);
						}
					}
				} else {
					valor = 1;
				}
			}
			break;
		case 1:
			if ((this.group != null) && (((Disco) otroDisco).group) != null) {
				valor = this.group.compareTo(((Disco) otroDisco).group);
			}
			break;
		case 2:
			if ((this.title != null) && (((Disco) otroDisco).title) != null) {
				valor = this.title.compareTo(((Disco) otroDisco).title);
			}
			break;
		case 3:
			if ((this.style != null) && (((Disco) otroDisco).style) != null) {
				valor = this.style.compareTo(((Disco) otroDisco).style);
			}
			break;
		case 4:
			if ((this.year != null) && (((Disco) otroDisco).year) != null) {
				valor = this.year.compareTo(((Disco) otroDisco).year);
			}
			break;
		case 5:
			if ((this.loc != null) && (((Disco) otroDisco).loc) != null) {
				valor = this.loc.compareTo(((Disco) otroDisco).loc);
			}
			break;
		case 6:
			if ((this.copy != null) && (((Disco) otroDisco).copy) != null) {
				valor = this.copy.compareTo(((Disco) otroDisco).copy);
				if (valor == 0) {
					if ((this.group != null) && (((Disco) otroDisco).group) != null) {
						valor = this.group.compareTo(((Disco) otroDisco).group);
					} else {
						valor = 1;
					}
				}
			}
			break;
		case 7:
			if ((this.type != null) && (((Disco) otroDisco).type) != null) {
				valor = this.type.compareTo(((Disco) otroDisco).type);
			}
			break;
		case 8:
			if ((this.mark != null) && (((Disco) otroDisco).mark) != null) {
				valor = this.mark.compareTo(((Disco) otroDisco).mark);
			}
			break;
		case 9:    //for comparing titles via web 
			comparador.setStrength(Collator.TERTIARY);
			valor = -1* comparador.compare(((Disco) otroDisco).title, this.title);
			break;
		default:
			valor = -1* comparador.compare(((Disco) otroDisco).group, this.group);
			if (valor == 0) {
				if ((this.year != null) && (((Disco) otroDisco).year) != null) {
					valor = this.year.compareTo(((Disco) otroDisco).year);
				} else {
					valor = 1;
				}
			}
			break;
        }
        return (valor);
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
        this.present="";
        this.path=null;
    }
}
