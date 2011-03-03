package com.myapps;
public class Astuces {
	
	private String s[] = {
			"Appuyez sur Menu pour accéder aux options",
			"Vous pouvez visualiser jusqu'à 4 caméras",
			"En touchant l'écran vous pouvez echanger les cameras", 
			"De prochaines astuces bientôt disponibles",
			"Sauvegarder vos caméras dans un fichier. Ca évitera de les rentrer à chaque fois"} ;
		
	private int max = 5 ;
	Astuces(){
	}
	
	public String getLabel(int i) {
	      return s[i] ;
	   }
	
	public int getMax(){
		return max;
	}
	
}