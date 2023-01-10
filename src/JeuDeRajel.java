import extensions.CSVFile;
import extensions.File;

class JeuDeRajel extends Program{

    final int NB_PROBLEMES = length(getAllFilesFromDirectory("../ressources"))-3;
    final String DOSSIER_TUTO = "../ressources/tuto";
    final String DOSSIER_PROBLEMES = "../ressources/pb";
    final int TEMPS_ATTENTE = 3; // secondes entre chaque tour

    Couleur stringToCouleur(String string){
    // Transforme la couleur lue dans le CSV (String) en couleur (Couleur)
        if (equals(string, "BLANC") || equals(string, " BLANC")){
            return Couleur.BLANC;
        } else{
            return Couleur.NOIR;
        }
    }

    void testStringToCouleur(){
        assertEquals(Couleur.BLANC, stringToCouleur("BLANC"));
        assertEquals(Couleur.NOIR, stringToCouleur("NOIR"));
    }

    Pion newPion(Partie partie, String couleur){
        // Créé un nouveua pion
        Pion pion = new Pion();
        pion.couleur = stringToCouleur(couleur);
        if (pion.couleur==Couleur.BLANC) pion.joueur=partie.blanc;
        else pion.joueur=partie.noir;
        return pion;
    }

    void testNewPion(){
        Partie partie = chargerSauvegarde(1);
        Pion pion = new Pion();
        pion.couleur = Couleur.BLANC;
        Pion nvPion = newPion(partie, "BLANC");
        assertTrue((pion.couleur == nvPion.couleur));
    }

    void supprimerPion(Pion[][] plateau, int x, int y, Partie partie){
    // Supprime un pion mangé, et met à jour les scores en conséquence
        if (plateau[x][y]!=null){
            plateau[x][y].vivant = false;
            if (plateau[x][y].estRajel) plateau[x][y].joueur.nbRajels=plateau[x][y].joueur.nbRajels-1;
            else plateau[x][y].joueur.nbPions=plateau[x][y].joueur.nbPions-1;
            if (plateau[x][y].joueur.couleur==Couleur.BLANC) partie.noir.score++;
            else partie.blanc.score++;
            plateau[x][y]=null;
        }
    }

    void testSupprimerPion(){
        Partie partie = chargerSauvegarde(3);
        supprimerPion(partie.rajelier, 1, 2, partie);
        assertTrue((null == partie.rajelier[1][2]));
        supprimerPion(partie.rajelier, 1, 4, partie);
        assertTrue((null == partie.rajelier[1][4]));
        supprimerPion(partie.rajelier, 0, 0, partie);
        assertTrue((null == partie.rajelier[0][0]));
    }

    Joueur newJoueur(Couleur couleur, int nbPions, int nbRajels, int score){
    // Créé un type joueur
        Joueur j = new Joueur();
        j.couleur = couleur;
        j.nbPions = nbPions;
        j.nbRajels = nbRajels;
        j.score = score;
        return j;
    }

    void testNewJoueur(){
        Joueur j = new Joueur();
        j.couleur = Couleur.BLANC;
        j.nbPions = 3;
        j.nbRajels = 1;
        j.score = 10;
        Joueur jbis = newJoueur(Couleur.BLANC, 3, 1, 10);
        assertTrue((j.couleur==jbis.couleur));
        assertTrue((j.nbPions==jbis.nbPions));
        assertTrue((j.nbRajels==jbis.nbRajels));
        assertTrue((j.score==jbis.score));
    }

    Partie chargerSauvegarde(int numero){
    // Renvoie la partie totalement chargée depuis le fichier CSV, prête à être démarrée
        int j1Pions = 0;
        int j2Pions = 0;
        int j1Rajels = 0;
        int j2Rajels = 0;
        Partie partie = new Partie();
        partie.tour = 1;
        partie.numero = numero;
        partie.terminee = false;
        CSVFile file = loadCSV(DOSSIER_PROBLEMES+numero+"/coups.csv");
        partie.nbTours = rowCount(file)-1;
        file = loadCSV(DOSSIER_PROBLEMES+numero+"/positionDepart.csv");
        String coordonnees, couleur, estRajel;
        Pion[][] plateau = new Pion[10][10];
        partie.noir = newJoueur(Couleur.NOIR, j1Pions, j1Rajels, 0);
        partie.blanc = newJoueur(Couleur.BLANC, j2Pions, j2Rajels, 0);
        for (int i =2; i<rowCount(file); i++){
            coordonnees = getCell(file, i, 0);
            couleur = getCell(file, i, 1);
            estRajel = getCell(file, i, 2);
            plateau[ligneToInt(coordonnees)][colonneToInt(coordonnees)]= newPion(partie, couleur);
            if (equals(estRajel, "oui")){ 
                plateau[ligneToInt(coordonnees)][colonneToInt(coordonnees)].estRajel = true;
                if (plateau[ligneToInt(coordonnees)][colonneToInt(coordonnees)].couleur==Couleur.BLANC) partie.blanc.score--;
                else partie.noir.score--;
            }
            
            if (plateau[ligneToInt(coordonnees)][colonneToInt(coordonnees)].couleur==Couleur.NOIR){
                if (plateau[ligneToInt(coordonnees)][colonneToInt(coordonnees)].estRajel) j1Rajels++;
                else j1Pions++;
            } else {
                if (plateau[ligneToInt(coordonnees)][colonneToInt(coordonnees)].estRajel) j2Rajels++;
                else j2Pions++;
            }
        }
        partie.noir.nbPions = j1Pions;
        partie.noir.nbRajels = j1Rajels;
        partie.blanc.nbPions = j2Pions;
        partie.blanc.nbRajels = j2Rajels;
        partie.rajelier = plateau;
        return partie;
    }

    String[] chargerCoup(int numero, int tour){
    // Retourne un tableau de String contenant [depart, destination] du joueur
        CSVFile file = loadCSV(DOSSIER_PROBLEMES+numero+"/coups.csv");
        if (tour+1<rowCount(file)) return new String []{getCell(file, tour+1, 0), getCell(file, tour+1, 1)};
        else return new String [2];
    }

    void testChargerCoup(){
        assertArrayEquals(new String[]{"J2", "I1"}, chargerCoup(1, 1));
        assertArrayEquals(new String[]{"G7", "E9"}, chargerCoup(1, 3));
        assertArrayEquals(new String[]{"G5", "F4"}, chargerCoup(3, 1));
        assertArrayEquals(new String[]{"...", "..."}, chargerCoup(3, 3));
    }

    String[] chargerCoupBot(int numero, int tour){
    // Retourne un tableau de String contenant [depart, destination] du bot
        CSVFile file = loadCSV(DOSSIER_PROBLEMES+numero+"/coupsBot.csv");
        return new String []{getCell(file, tour+1, 0), getCell(file, tour+1, 1)};
    }

    void testChargerCoupBot(){
        assertArrayEquals(new String[]{"H0", "J2"}, chargerCoupBot(1, 1));
        assertArrayEquals(new String[]{"...", "..."}, chargerCoupBot(1, 3));
        assertArrayEquals(new String[]{"H2", "J0"}, chargerCoupBot(3, 1));
        assertArrayEquals(new String[]{"D6", "G9"}, chargerCoupBot(3, 3));
    }

    String saisirCoup(Partie partie){
    // Le joueur saisit un coup jusqu'à ce que le coup soit le bon dans l'ordre
        String saisie;
        String[] coup = chargerCoup(partie.numero, partie.tour);
        boolean b = false;
        int cpt = 0;
        do {
            if (cpt>=6) print(bold("Indice n°2/2 : Le coup à effectuer est "+coup[0]+coup[1]+" : "));
            else if (cpt>=3) print(bold("Indice n°1/2 : Vous devez déplacer le pion "+coup[0]+" : "));
            else if (b) print(bold("Ce n'est pas la combinaison attendue, réflechissez encore et réessayez : "));
            else print(bold("Saisissez votre coup (ex: A0C2) : "));
            b = true;
            saisie = toUpperCase(readString());
            cpt++;
        } while (!equals(coup[0]+coup[1], saisie));
        return coup[0]+coup[1];
    }

    void jouerCoup(Partie partie, String coordonnees, String coup){
    // Déplacer un pion de coordonnées "coordonnees" vers les coordonnées "coup" et supprime les pions mangés
        if (!equals(coup, "...")){
            int j = colonneToInt(coordonnees);
            Pion[][] plateau = partie.rajelier;
            Pion pion = plateau[ligneToInt(coordonnees)][colonneToInt(coordonnees)];
            if (ligneToInt(coordonnees)<ligneToInt(coup)){
                for (int i = ligneToInt(coordonnees)+1; i<=ligneToInt(coup); i++){
                    if (colonneToInt(coordonnees)<colonneToInt(coup)){
                        j++;
                        if (plateau[i][j]!=null && plateau[i][j].couleur!=pion.couleur) supprimerPion(plateau, i, j, partie);
                    } else if (colonneToInt(coordonnees)>colonneToInt(coup)){
                        j--;
                        if (plateau[i][j]!=null && plateau[i][j].couleur!=pion.couleur) supprimerPion(plateau, i, j, partie);
                    }
                }
            } else {
                for (int i = ligneToInt(coordonnees)-1; i>=ligneToInt(coup); i--){
                    if (colonneToInt(coordonnees)<colonneToInt(coup)){
                        j++;
                        if (plateau[i][j]!=null && plateau[i][j].couleur!=pion.couleur) supprimerPion(plateau, i, j, partie);
                    } else if (colonneToInt(coordonnees)>colonneToInt(coup)){
                        j--;
                        if (plateau[i][j]!=null && plateau[i][j].couleur!=pion.couleur) supprimerPion(plateau, i, j, partie);
                    }
                }
            }
            if (pion.couleur==Couleur.NOIR && ligneToInt(coup)==9 && pion.joueur.score>0) pion.estRajel = true;
            else if (pion.couleur==Couleur.BLANC && ligneToInt(coup)==0 && pion.joueur.score>0) pion.estRajel = true;
            plateau[ligneToInt(coup)][colonneToInt(coup)]=plateau[ligneToInt(coordonnees)][colonneToInt(coordonnees)];
            plateau[ligneToInt(coordonnees)][colonneToInt(coordonnees)]=null;
        }
    }

    int ligneToInt(String coordonnees){
    // Récupérer le numéro de ligne à partir des coordonnées en String
        return ((int) charAt(coordonnees, 0))-65;
    }

    void testLigneToInt(){
        assertEquals(0, ligneToInt("A0"));
        assertEquals(4, ligneToInt("E3"));
        assertEquals(9, ligneToInt("J2"));
    }

    int colonneToInt(String coordonnees){
    // Récupérer le numéro de colonne à partir des coordonnées en String
        return ((int) charAt(coordonnees, 1))-48;
    }

    void testColonneToInt(){
        assertEquals(0, colonneToInt("A0"));
        assertEquals(3, colonneToInt("E3"));
        assertEquals(2, colonneToInt("J2"));
    }

    void attendre(int secondes){
        for (int i = secondes; i>0; i--){
            print(""+i+", ");
            delay(1000);
        }
    }

    void afficherLogo(){
        File logo = newFile("../ressources/img/logo.txt");
        afficherFichier(logo);
    }

    void victoire(){
    // Afficher une image en ASCII art lors de la victoire
        int n = (int) (random()*3)+1;
        File dessin = newFile("../ressources/img/"+n+".txt");
        afficherFichier(dessin);
    }

    void afficherFichier(File fichier){
        while (ready(fichier)){
            println(readLine(fichier));
        }
    }

    void demarrerTutoriel(){
        String[] tutos = getAllFilesFromDirectory(DOSSIER_TUTO);
        println(length(tutos));
        File tuto;
        int i = 0;
        clear();
        print("Appuyez sur entrée pour démarrer, tapez \"fin\" pour arrêter le tutoriel ");
        String saisie = readString();
        while (i<length(tutos) && !equals(toLowerCase(saisie), "fin")){
            clear();
            tuto = newFile(DOSSIER_TUTO+"/tuto_p"+i+".ans");
            afficherFichier(tuto);
            print("Appuyez sur entrée pour voir la suite, \"fin\" pour arrêter ");
            saisie = readString();
            i++;
        }
    }

    void clear(){
        // Efface les écritures sur les terminal
        println("\u001b[2J");
        print("\u001b[50A");
    }

    String bold(String texte){
        // Renvoie le texte entré en gras
        return "\u001b[1m"+texte+"\u001b[0m";
    }

    String invertBg(String texte){
        // Renvoie le texte entré avec un fond inversé
        return "\u001b[7m"+texte+"\u001b[0m";
    }

    String redBg(String texte){
        // Renvoie le texte entré avec un fond rouge
        return "\u001b[41;1m"+texte+"\u001b[0m";
    }

    String underline(String texte){
        // Renvoie le texte souligné
        return "\u001b[4m"+texte+"\u001b[0m";
    }

    void afficherRajelier(Partie partie){
        // Afficher le plateau
        Pion[][] rajelier = partie.rajelier;
        char car;
        String result = bold("       \n    0   1   2   3   4   5   6   7   8   9")+"\n  ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐\n";
        for (int i = 0; i<10; i++){
            car=(char) (i+65);
            result=result+bold(car+" ");
            for (int j = 0; j<10; j++){
                if (i%2!=0) {
                    if (j%2==0) {
                        if (rajelier[i][j]!=null){
                            if (rajelier[i][j].couleur==Couleur.NOIR){
                                if (rajelier[i][j].estRajel) result=result+"│"+invertBg(" ☆ ");
                                else result=result+"│"+invertBg(" ○ ");    
                            } else {
                                if (rajelier[i][j].estRajel) result=result+"│"+invertBg(" ★ ");
                                else result=result+"│"+invertBg(" ● ");
                            }
                        } else {
                            result=result+"│"+invertBg("   ");
                        }
                    } else result=result+"│   ";
                } else {
                    if (j%2!=0) {
                        if (rajelier[i][j]!=null){
                            if (rajelier[i][j].couleur==Couleur.NOIR){
                                if (rajelier[i][j].estRajel) result=result+"│"+invertBg(" ☆ ");
                                else result=result+"│"+invertBg(" ○ ");    
                            } else {
                                if (rajelier[i][j].estRajel) result=result+"│"+invertBg(" ★ ");
                                else result=result+"│"+invertBg(" ● ");
                            }
                        } else {
                            result=result+"│"+invertBg("   ");
                        }
                    } else result=result+"│   ";
                }
            }
            result=result+"│\n  ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤\n";
        }
        result=substring(result, 0, length(result)-46)+"│\n  └───┴───┴───┴───┴───┴───┴───┴───┴───┴───┘";
        println(result);

    }

    void afficherRajelier(Partie partie, String dep, String dest){
        // Afficher le plateau en mettant en surbrillance le déplacement effectué
        Pion[][] rajelier = partie.rajelier;
        char car;
        String result = "\n"+ bold("       \n    0   1   2   3   4   5   6   7   8   9")+"\n  ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐\n";
        for (int i = 0; i<10; i++){
            car=(char) (i+65);
            result=result+bold(car+" ");
            for (int j = 0; j<10; j++){
                if ((i==ligneToInt(dep) && j==colonneToInt(dep)) || (i==ligneToInt(dest) && j==colonneToInt(dest))) {
                    // Colorier en rouge les cases de mouvement
                    if (rajelier[i][j]!=null){
                            if (rajelier[i][j].couleur==Couleur.NOIR){
                                if (rajelier[i][j].estRajel) result=result+"│"+redBg(" ☆ ");
                                else result=result+"│"+redBg(" ○ ");    
                            } else {
                                if (rajelier[i][j].estRajel) result=result+"│"+redBg(" ★ ");
                                else result=result+"│"+redBg(" ● ");
                            }
                        } else {
                            result=result+"│"+redBg("   ");
                        }
                }
                else if (i%2!=0) {
                    // Colorier une case sur deux sur une ligne impair
                    if (j%2==0) {
                        if (rajelier[i][j]!=null){
                            if (rajelier[i][j].couleur==Couleur.NOIR){
                                if (rajelier[i][j].estRajel) result=result+"│"+invertBg(" ☆ ");
                                else result=result+"│"+invertBg(" ○ ");    
                            } else {
                                if (rajelier[i][j].estRajel) result=result+"│"+invertBg(" ★ ");
                                else result=result+"│"+invertBg(" ● ");
                            }
                        } else {
                            result=result+"│"+invertBg("   ");
                        }
                    } else result=result+"│   ";
                } else {
                    // Colorier une case sur deux sur une ligne pair
                    if (j%2!=0) {
                        if (rajelier[i][j]!=null){
                            if (rajelier[i][j].couleur==Couleur.NOIR){
                                if (rajelier[i][j].estRajel) result=result+"│"+invertBg(" ☆ ");
                                else result=result+"│"+invertBg(" ○ ");    
                            } else {
                                if (rajelier[i][j].estRajel) result=result+"│"+invertBg(" ★ ");
                                else result=result+"│"+invertBg(" ● ");
                            }
                        } else {
                            result=result+"│"+invertBg("   ");
                        }
                    } else result=result+"│   ";
                }
            }
            result=result+"│\n  ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤\n";
        }
        result=substring(result, 0, length(result)-46)+"│\n  └───┴───┴───┴───┴───┴───┴───┴───┴───┴───┘";
        println(result);
    }

    void algorithm(){
        String coup = "";
        String coordonnees = "";
        boolean statut = true;
        int choix = -1;
        String[] tabCoup = new String[]{null, null};
        int numSauvegarde = (int) ((random()*NB_PROBLEMES)+1);
        Partie partie = chargerSauvegarde(numSauvegarde);
        partie.numero = numSauvegarde;
        while (statut){
            clear();
            afficherLogo();
            println(bold("\n                                       1 - JOUER\n                                       2 - GUIDE\n                                      3 - QUITTER\n\n"));
            while (!(choix<=3 && choix>=1)){ // Choix dans le menu
                    print(bold("Entrez votre choix (ex: 1) : "));
                    choix = readInt();
            }
            if (choix==1){
                clear();
                while (partie.tour<partie.nbTours){ // Boucle tant que la partie n'est pas terminée
                    clear();
                    println("               "+underline("Problème n°"+partie.numero+"/"+NB_PROBLEMES)+"\n");
                    println("   "+invertBg(" ● ")+" "+underline("Vous :")+" "+partie.blanc.score+" pts   /   "+invertBg(" ○ ")+" "+underline("Ordi :")+" "+partie.noir.score+" pts");

                    if (partie.tour>1){ // Vérification d'un potentiel coup multiple
                        if (equals(coordonnees, "...")) {
                            coordonnees = chargerCoup(partie.numero, partie.tour-1)[0];
                            coup = chargerCoup(partie.numero, partie.tour-1)[1];
                        }
                        afficherRajelier(partie, coordonnees, coup);
                    }else {afficherRajelier(partie);}

                    if (partie.tour>1 && !equals(chargerCoupBot(partie.numero, partie.tour-1)[0], "...")) println("L'ordinateur a joué "+bold(coordonnees+coup)+" !");
                    else if (equals(coordonnees, "...")) println("Un coup multiple est possible,"+bold(" encore à vous !"));

                    // Le tour du joueur
                    if (!equals(chargerCoup(partie.numero, partie.tour)[0], "...")){ // On vérifie si c'est bien au joueur de jouer et non pas au bot de rejouer
                        coup = saisirCoup(partie);
                        coordonnees = substring(coup, 0, 2);
                        coup = substring(coup, 2, 4);
                        jouerCoup(partie, coordonnees, coup);
                    }

                    // Le tour du bot
                    tabCoup = chargerCoupBot(partie.numero, partie.tour);

                    clear();
                    println("                 "+underline("Problème n°"+partie.numero)+"\n");
                    println("       "+invertBg(" ● ")+" "+underline("Vous :")+" "+partie.blanc.score+" pts   /   "+invertBg(" ○ ")+" "+underline("Ordi :")+" "+partie.noir.score+" pts");
                    if (!equals(tabCoup[0], "...")){ // On vérifie si c'est bien au bot de jouer et non pas au joueur de rejouer
                        afficherRajelier(partie, coordonnees, coup);
                        println("Au tour de l'ordinateur, patientez le temps qu'il réflechisse...");
                        attendre(TEMPS_ATTENTE);
                    }

                    coordonnees = tabCoup[0];
                    coup = tabCoup[1];
                    jouerCoup(partie, coordonnees, coup);

                    clear();
                    partie.tour++;
                }
                choix=0;
                victoire();
                println("\n\nFéliciations, vous avez réussi à résoudre le "+bold("problème n°"+partie.numero+"/"+NB_PROBLEMES)+" du jeu de Rajel !\nRéussissez les tous pour devenir le Rajel D'Or en titre.\n");
                print("Appuyez sur entrée pour retourner au menu principal ");
                readString();
                tabCoup = new String[]{null, null};
                while (numSauvegarde==partie.numero) numSauvegarde = (int) ((random()*NB_PROBLEMES)+1);
                partie = chargerSauvegarde(numSauvegarde);
                partie.numero = numSauvegarde;
            } else if (choix==2) {
                demarrerTutoriel();
                choix=-1;
            } else statut = false;
        }
        clear();
        println("");
    }



}