# **Présentation du Jeu**

- [**Présentation du Jeu**](#présentation-du-jeu)
  - [**Structure de données**](#structure-de-données)
    - [**Classes**](#classes)
    - [**Fonctions**](#fonctions)
      - [**Initialisation**](#initialisation)
      - [**Jeu**](#jeu)
      - [**Utilitaires**](#utilitaires)
    - [**Algorithme principal**](#algorithme-principal)


## **Structure de données**

### **Classes**

- **`class Srir`** - *(alias Pion)*
    - `Joueur joueur` - Le joueur propriétaire du pion
    - `boolean estRajel` - Si le pion est devenu une dame
    - `boolean vivant` - Si le pion a été mangé ou non
- **`class Joueur`**
    - `String nom` - Pseudo du joueur
    - `int num` - 1 ou 2
    - `int nbSrirs` - Nombre de pions
    - `int nbRajels` - Nombre de pions dames
    - `int score` 
- **`class Partie`**
    - `Srir[][] rajelier` - Plateau contenant tout les pions, de taille 10x10.
    - `Joueur joueur1`
    - `Joueur joueur2`
    - `Mode mode` - Mode de jeu
    - `boolean terminee`
- **`enum Mode`**
  - `CLASSIQUE` - Mode de jeu de Dame classique
  - `SUICIDE` - Mode de jeu de Dame inversé, le gagnant est le joueur qui aura réussi à faire manger l'adversaire tout ses pions.
  - `APPRENTISSAGE` - Mode de jeu avec des situtations prédéfinies à résoudre pour apprendre.

### **Fonctions**

#### **Initialisation**
- [x] `Joueur newJoueur(String nom, int num)`
  - Créé un joueur avec par défaut **20 pions**, **0 dames**, et **0 de score**.
- [x] `Srir newSrir(Joueur joueur)`
  - Créé un pion qui n'est par défaut **pas une dame** *(False)* et est **vivant** *(True)*
- [x] `Partie initialiser(Joueur joueur1, Joueur joueur2, Mode mode)`
  - Initialise une partie avec les deux joueurs avec un plateau de taille 10x10.

#### **Jeu**
- [x] `void deplacerSrir(Srir[][] plateau, int Lig, int Col, int LigDest, int ColDest)`
  - Déplacer un pion sur le plateau.
- `boolean jouerCoup(Srir[][] plateau, int Lig, int Col, int LigDest, int ColDest)`
  - Déplace un pion si le déplacement est valide, retourne vrai si un autre coup d'affilée est possible, faux sinon.
- [x] `Joueur vainqueur(Partie partie)`
  - Renvoie le joueur qui a gagné la partie (condition : la partie est terminée)
- [x] `String saisieCoordonnees(String nomJoueur)`
  - L'utilisateur doit saisir les coordonnées d'un pion
  - `print(nomJoueur+", entrez les coordonnées du pion à mouvoir : ");
    coordPion = readString()
          while (!coordonneesValides(coordPion)){
                print("Coordonnées invalides (ex: A2), veuillez réessayer : ");
                coordPion = readString();
          }
          return coordPion;`
- [ ] `String saisieCoup(Srir[][] plateau, string coordPion)`
  - L'utilisateur doit saisir les coordonnées d'un coup pour un pion

#### **Utilitaires**
- [x] `boolean estVide(Srir[][] plateau, int Lig, int Col)`
  - Vérifie que la case est vide.
- [ ] `boolean estValideDeplacement(Srir[][] plateau, int Lig, int Col, int LigDest, int ColDest`
  - Vérifie que le déplacement est faisable, en vérifiant qu'un pion est présent sur la case, la distance, si la case de destination est vide, si le pion est une dame.
- [x] `boolean estTerminee(Partie partie)`
  - Vérifie que les joueurs ont tous au moins un pion.
- [x] `boolean coupPossible(Srir[][] plateau, int Lig, int Col)`
  - Vérifie qu'un pion peut se déplacer (si un coup est possible depuis sa position). 
- [x] `boolean coordonneesCoupValides(String coordCoup)`
  - Vérifie que les coordonnées données sont valides.
  - `return (charAt(coordCoup, 0)=>'A' || charAt(coordCoup, 0)=<'Z') && (charAt(coordCoup, 1)=>'0' || charAt(coordCoup, 1)=<'9')` 
- [x] `ìnt ligCoord(String coord)`
  - Retourne le numéro de ligne correspondant à la ligne en char.
- [x] `ìnt colCoord(String coord)`
  - Retourne le numéro de colonne correspondant à la colonne en char.
- [x] `String toString(Partie partie)`
  - Retourne l'affichage en String du plateau avec le score de chaque joueur.
- [x] `void enregistrerVainqueur(String nom)`
  - Ajoute dans l'historique des vainqueurs le nom du vainqueur.
- [x] `void enregistrerPerdant(String nom)`
  - Ajoute dans l'historique des perdants le nom du perdant.

### **Algorithme principal**

```java
final int NB_MODES = 3;

void algorithm(){
    println("Bienvenue dans le jeu de Rajel !\n");
    print("Choisissez un mode :\n
              1. CLASSIQUE\n
              2. SUICIDE\n
          Entrez votre choix : ");
    int choixMode = readInt();
    while (!(choixMode>=1 && choixMode<=NB_MODES)){
        print("Entrez votre choix : ");
        choixMode = readInt();
    }
    print("\nJoueur 1, choisissez un nom : ");
    Joueur j1 = newJoueur(readString(), 1);
    print("\nJoueur 2, choisissez un nom : ");
    Joueur j2 = newJoueur(readString(), 2);
    
    Partie partie = initialiser(j1, j2);

    int tours = 0;
    String coordPionJ1, coordPionJ2, coordCoupJ1, coordCoupJ2;
    while (!estTerminee(partie)){
          tours++;
          println("Tour n°"+tours);
          println(toString(partie.plateau)+"\n");
          print(j1.nom+", quel srir voulez vous déplacer ? : ");
          coordPionJ1 = saisieCoordonnees(j1.nom);
          coordCoupJ1 = saisieCoup(partie.plateau, coordPionJ1);
          jouerCoup(partie.plateau, ligCoord(coordPionJ1), colCoord(coordPionJ1), ligCoord(coordCoupJ1), colCoord(coordCoupJ1));

          println("\n"+toString(partie)+"\n");

          coordPionJ2 = saisieCoordonnees(j2.nom);
          coupJ2 = saisieCoup(partie.plateau, coordPionJ2);
          jouerCoup(partie.plateau, ligCoord(coordPionJ2), colCoord(coordPionJ2), ligCoord(coordCoupJ2), colCoord(coordCoupJ2));
    }
    if (vainqueur(partie).nom == j1.nom){
            enregistrerVainqueur(j1.nom);
            enregistrerPerdant(j2.nom);
            println("Félicitation "+j1.nom+" !\n
             Vous êtes devenu un vrai Rajel, votre nom est désormais inscrit dans le Rajel d'Or !")
    } else{
            enregistrerVainqueur(j2.nom);
            enregistrerPerdant(j1.nom);
            println("Félicitation "+j2.nom+" !\n
             Vous êtes devenu un vrai Rajel, votre nom est désormais inscrit dans le Rajel d'Or !")
    }
}
```