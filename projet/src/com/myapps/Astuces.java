package com.myapps;
public class Astuces {
	
	private String s[] = {
			"Appuyez sur Menu pour accéder aux options",
			"Vous pouvez visualiser jusqu'à 6 caméras simultanément",
			"En touchant l'�cran vous pouvez stopper ou changer les cameras", 
			"De prochaines astuces bient�t disponibles",
			"Vos cam�ras sont sauvegardées automatiquement",
			"Vous pouvez activer jusqu'à 9 fenêtres de dét�ction de mouvements par camera",
			"Selectionner un cadre dans lequel surveiller les mouvements",
			"Regler les parametres avanc�s de la camera en activant les controles avances dans le menu durant la visualisation de la video",
			"Diriger la camera tactilement",
			"Une notification vous informe que vous avez bien recu votre snapshot",
			"Vous pouvez desactiver cette astuce dans les paramètres",
			"N'hésitez pas � partager cette application"} ;
		
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