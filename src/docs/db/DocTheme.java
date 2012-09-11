package docs.db;

public enum DocTheme {
	HISTORY,SECONDWW,WARFARE,F1,MISC,SCIENCE,PROTEST,SPORTS;
	
	public static DocTheme getDocTheme(String theme) {
		DocTheme aux=MISC;
		if (theme.compareTo("HISTORY")==0){
			aux=HISTORY;
		}else if(theme.compareTo("WARFARE")==0){
			aux=WARFARE;
		}else if(theme.compareTo("MISC")==0){
			aux=MISC;
		}else if(theme.compareTo("F1")==0){
			aux=F1;
		}else if(theme.compareTo("SCIENCE")==0){
			aux=SCIENCE;
		}else if(theme.compareTo("PROTEST")==0){
			aux=PROTEST;
		}else if(theme.compareTo("SPORTS")==0){
			aux=SPORTS;
		}else if(theme.compareTo("SECONDWW")==0){
			aux=SECONDWW;
		}
		return aux;
	}
	public static String getStringTheme(DocTheme theme) {
		if (theme==HISTORY){
			return "HISTORY";
		}else if(theme==WARFARE){
			return "WARFARE";
		}else if(theme==MISC){
			return "MISC";
		}else if(theme==F1){
			return "F1";
		}else if(theme==SECONDWW){
			return "SECONDWW";
		}else if(theme==SCIENCE){
			return "SCIENCE";
		}else if(theme==PROTEST){
			return "PROTEST";
		}else if(theme==SPORTS){
			return "SPORTS";
		}else return "MISC";
	}
	
	
}
