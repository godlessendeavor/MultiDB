package music.dealfiles;

import java.io.File;
import java.text.Collator;

import main.Errors;

public class DealFiles {

	
	public DealFiles() {
	}


	//METODO QUE DEVUELVE PATH DE UN DISCO DETERMINADO
    public static String buscarDisco(String nombreGrupo, String nombreDisco, String pathDisco) {
        String resul = "";
        File fPathDisc;
        String currentGroup, currentDisc;
        int posGuion = -1, longNombre = 0, group = -1, disc = -1;


        fPathDisc = new File(pathDisco);
        if (fPathDisc.isDirectory() == false) {
        } else {
            String[] grupos = fPathDisc.list();
            Integer tam = grupos.length;
            if (tam == 0) {
            } else {

                //para todos los grupos de la carpeta

                for (int j = 0; j < tam; j++) {
                    currentGroup = grupos[j];

                    File discosGrupoF = new File(pathDisco + File.separator + nombreGrupo);
                    if (discosGrupoF.isDirectory() == false) {
                    } else {
                        String[] discosGrupo = discosGrupoF.list();
                        Integer numeroDiscos = discosGrupo.length;

                        //para todos los discos de este grupo

                        for (int k = 0; k < numeroDiscos; k++) {
                            File discoF = new File(pathDisco + File.separator + nombreGrupo + File.separator + discosGrupo[k]);
                            if (discoF.isDirectory() == false) {
                            } else {
                                posGuion = discosGrupo[k].indexOf("-");

                                if (posGuion < 0) {
                                } else {
                                    longNombre = discosGrupo[k].length();
                                    currentDisc = discosGrupo[k].substring(posGuion + 1, longNombre);
                                    Collator comparador = Collator.getInstance();
                                    comparador.setStrength(Collator.PRIMARY);
                                    group = comparador.compare(currentGroup, nombreGrupo);
                                    disc = comparador.compare(currentDisc, nombreDisco);
                                    if ((group == 0) && (disc == 0)) {
                                        resul = discoF.getAbsolutePath();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return resul;
    }
    
    
    //METODO QUE DEVUELVE EL DIRECTORIO DE UN GRUPO DETERMINADO
    public static File buscarGrupo(String nombreGrupo, File pathDisco) {
        File resul = null;
 
        String currentGroup;
        int group = -1;

        if (pathDisco.isDirectory() == false) {
            Errors.errorDir(pathDisco.toString());
        } else {
            String[] grupos = pathDisco.list();
            Integer tam = grupos.length;
            if (tam == 0) {
            	Errors.errorDir(pathDisco.toString());
            } else {
                for (int j = 0; j < tam; j++) {
                    currentGroup = grupos[j];
                    File discosGrupoF = new File(pathDisco + File.separator + nombreGrupo);                
                    Collator comparador = Collator.getInstance();
                    comparador.setStrength(Collator.PRIMARY);
                    group = comparador.compare(currentGroup, nombreGrupo);
                    if (group == 0) {
                        resul = discosGrupoF.getAbsoluteFile();
                        break;
                    }
                }
            }
        }
        return resul;
    }
    /////////method that searches for lyrics file in music folder
    public static File searchLyricsFile(File lyricsPath){
    	File lyrics = null;
      	String[] files = null;

 		if (lyricsPath!=null) files=lyricsPath.list();
 	    if (files!=null){
	        int tam = files.length;
	        if (tam == 0) { //no files
	        } else { //for every file in this folder
	            for (int j = 0; j < tam; j++) {
	                if ((files[j].indexOf("lyrics.txt")) > -1) {
	                	lyrics=new File(lyricsPath+File.separator+files[j]);
	                	break;
	                }
	            }	
	        }
 	    }
    	return lyrics;
    }
    
    
}


