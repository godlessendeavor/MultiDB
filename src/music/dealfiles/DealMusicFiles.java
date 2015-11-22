package music.dealfiles;

import java.io.File;
import java.text.Collator;

import main.Errors;

public class DealMusicFiles {

	
	public DealMusicFiles() {
	}


	//METODO QUE DEVUELVE PATH DE UN DISCO DETERMINADO
    public static String searchDiscInFolder(String nombreGrupo, String nombreDisco, String pathDisco) {
        String resul = "";
        File fPathDisc;
        String currentBand, currentDisc;
        int posGuion = -1, longNombre = 0, band = -1, disc = -1;


        fPathDisc = new File(pathDisco);
        if (fPathDisc.isDirectory() == false) {
        } else {
            String[] grupos = fPathDisc.list();
            Integer tam = grupos.length;
            if (tam == 0) {
            } else {

                //para todos los grupos de la carpeta

                for (int j = 0; j < tam; j++) {
                    currentBand = grupos[j];

                    File discosGrupoF = new File(pathDisco + File.separator + nombreGrupo);
                    if (discosGrupoF.isDirectory() == false) {
                    	Errors.writeError(Errors.WRONG_DIRECTORY,discosGrupoF.toString());
                    } else {
                        String[] discosGrupo = discosGrupoF.list();
                        Integer numeroDiscos = discosGrupo.length;

                        //para todos los discos de este grupo

                        for (int k = 0; k < numeroDiscos; k++) {
                            File discoF = new File(pathDisco + File.separator + nombreGrupo + File.separator + discosGrupo[k]);
                            if (discoF.isDirectory() == false) {
                            	Errors.writeError(Errors.WRONG_DIRECTORY,discoF.toString());
                            } else {
                                posGuion = discosGrupo[k].indexOf("-");

                                if (posGuion < 0) {
                                } else {
                                    longNombre = discosGrupo[k].length();
                                    currentDisc = discosGrupo[k].substring(posGuion + 1, longNombre);
                                    Collator comparador = Collator.getInstance();
                                    comparador.setStrength(Collator.PRIMARY);
                                    band = comparador.compare(currentBand, nombreGrupo);
                                    disc = comparador.compare(currentDisc, nombreDisco);
                                    if ((band == 0) && (disc == 0)) {
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
    public static File searchBand(String bandName, File discPath) {
        File returnPath = null;
 
        String currentBand;
        int comparisonResult = -1;

        if (discPath.isDirectory() == false) {
            Errors.showWarning(Errors.WRONG_DIRECTORY, discPath.toString());
        } else {
            String[] grupos = discPath.list();
            Integer tam = grupos.length;
            if (tam == 0) {
            	Errors.showWarning(Errors.WRONG_DIRECTORY, discPath.toString());
            } else {
                for (int j = 0; j < tam; j++) {
                    currentBand = grupos[j];
                    File bandDiscsPath = new File(discPath + File.separator + bandName);                
                    Collator comparador = Collator.getInstance();
                    comparador.setStrength(Collator.PRIMARY);
                    comparisonResult = comparador.compare(currentBand, bandName);
                    if (comparisonResult == 0) {
                        returnPath = bandDiscsPath.getAbsoluteFile();
                        break;
                    }
                }
            }
        }
        return returnPath;
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


