package com.myapps;
public class Astuces {
	
	private String s[] = {
			"Appuyez sur Menu pour accéder aux options",
			"Vous pouvez visualiser jusqu'à 6 caméras simultanément",
			"En touchant l'écran vous pouvez stopper ou changer les cameras", 
			"De prochaines astuces bientôt disponibles",
			"Vos caméra se sauvegardent automatiquement",
			"Vous pouvre activer jusqu'a 9 détéction de mouvement par camera",
			"Selectionner un cadre dans lequel surveiller les mouvements",
			"Regler les parametres avancé de la camera en activant les controles avances dans le menu durant la visualisation de la video",
			"Diriger la camera avec vote doigt",
			"Une notification vous informe que vous avez bien recu votre snapshot",
			"Vous pouvez desactiver cette astuces dans les paramètres",
			"N'esitez pas à partager cette application"} ;
		
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