# Aventuriers du Rail

Pour voir le fichier de cryptage : src/java/chiffrement 

Pour jouer : cloner le projet & exécuter cette commande : 
- `java -jar Ticket-to-Ride.jar`

Si vous souhaitez apporter des modifications au projet initial, utilisez ces commandes :

- `javac -d bin src/main/java/*/*.java ; jar cfm Ticket-to-Ride.jar Manifest.mf -C bin . ;`

## Présentation
Ce projet a été réalisé dans le cadre de notre deuxième année de Licence en Informatique à l'Université Paris Cité.
Le but de ce projet est de produire une implémentation en Java du jeu de plateau _Les Aventuriers du Rail_. Il existe de nombreuses extensions du jeu qui modifient les règles de base. Dans ce projet, nous ne considérerons que la **version Europe** du jeu.

### Contributeurs
- Groupe ED2b :
* MERCIER Christophe
* MELART Arthur

**Quelques conseils avant de poursuivre la lecture du sujet :**
* Lire les règles du jeu [en français](src/main/resources/Règles/LesAventuriersDuRail-Règles.pdf) ou [en anglais](src/main/resources/Règles/TicketToRideEurope-Rules.pdf)

Le principe du jeu est de construire des tronçons de chemin de fer prédéterminés entre des villes sur une carte de l'Europe. Lorsqu'un tel tronçon est construit on dit que la route entre les villes correspondantes est **capturée**. Au début de la partie chaque joueur reçoit des **objectifs**, consistant à relier deux villes plus ou moins éloignées. Pour réaliser ces missions, les joueurs devront ainsi capturer des routes entre les différentes villes pour relier celles-ci.

Pour capturer une route il faut payer ! Pour cela, chaque joueur a besoin des **cartes wagon**. Ainsi, afin de capturer une route le joueur doit défausser autant de cartes wagon de la couleur de la route que d'espaces composant cette route. Une fois la route capturée, le joueur pose des *wagons* sur cette route pour marquer sa prise.

### Tour de jeu
À son tour, un joueur doit faire une, et une seule, des quatre actions suivantes :
* **Piocher des cartes wagon** - le joueur peut prendre au plus deux cartes wagon (ou une seule, s'il choisit de prendre une carte _locomotive_) ;
* **Capturer une route** – le joueur peut s'emparer d'une route sur le plateau en posant autant de cartes wagon de la couleur de la route que d'espaces composant cette route (une carte _locomotive_ est considérée comme étant de toutes les couleurs). Après avoir défaussé ses cartes, le joueur pose ses wagons sur chaque espace constituant la route. Cette action impliquera une augmentation du score du joueur suivant un barème défini ;
* **Piocher des cartes objectif supplémentaires** - le joueur tire trois cartes
  _objectifs_ du dessus de la pioche. Il doit en conserver au moins une, et remettre celles qu'il ne souhaite pas garder en dessous de la pioche ;
* **Construire une gare** - le joueur peut bâtir une gare sur toute ville qui n'en possède pas une. S'il s'agit de sa première gare, le joueur se défausse d'une carte wagon. S'il s'agit de sa seconde gare, le joueur doit se défausser de deux cartes de même couleur pour la bâtir. Et s'il s'agit de sa troisième gare, il doit se défausser de trois cartes de couleur identique.

### Les cartes wagon
Il existe 9 types de cartes wagon différents en plus des locomotives. Les couleurs de chaque carte wagon correspondent aux couleurs des routes présentes sur le plateau connectant des villes – noir, bleu, vert, orange, rose, rouge, blanc, jaune et locomotive.

Lorsqu'un joueur choisit de prendre des cartes wagon, il peut en piocher jusqu'à deux par tour. Chacune de ces cartes doit être soit prise parmi les quatre faces visibles, soit tirée du dessus de la pioche. Lorsqu'une carte face visible est choisie, elle doit être immédiatement remplacée par une nouvelle carte tirée du sommet de la pioche.
Si le joueur choisit de prendre une _Locomotive_ face visible parmi les quarte cartes exposées, il ne peut pas piocher d'autre carte lors de ce tour.

Si, à un moment quelconque, deux des quatres cartes face visible sont des _Locomotives_, alors les quatres cartes sont immédiatement défaussées
et remplacées par quatres nouvelles cartes face visible.

Un joueur peut conserver en main autant de cartes qu'il le souhaite durant la partie. Si la pioche est épuisée, la défausse est mélangée pour constituer une nouvelle pioche.

**Remarque :** Il se peut que la pioche et la défausse soient simultanément épuisées (si les joueurs ont gardé toutes les cartes en main). Dans ce cas, le joueur dont c’est le tour ne peut pas piocher des _cartes wagon_ et se contentera des trois autres options : prendre possession d'une route, piocher de nouvelles _cartes objectifs_ ou bâtir une gare.

#### L'importance des locomotives
Les _locomotives_ sont des cartes "Joker" : elles peuvent remplacer n'importe quelle couleur lors de la capture d'une route. Les _locomotives_ sont également nécessaires pour emprunter des _Ferries_.


### Les _Routes_
Une route est représentée sur le plateau par une ligne continue de rectangles colorés reliant deux villes adjacentes. Pour prendre possession d'une route, le joueur doit jouer des cartes wagon de la couleur correspondante, en quantité suffisante pour égaler le nombre d'espaces figurant sur la route convoitée. Pour se faire le joueur doit cliquer sur une des villes puis sur la seconde, une boîte de dialogue s'affichera alors pour lui demander si oui ou non il souhaite construire cette route.


#### _Ferries_
Les _Ferries_ sont des routes spéciales, de couleur grise, reliant deux villes séparées par des eaux. Les lignes de Ferries sont facilement reconnaissables grâce au symbole de _Locomotive_ figurant sur l'un au moins des espaces constituant cette route.

Pour prendre possession d'une ligne de Ferries, un joueur doit jouer une carte _locomotive_ pour chaque rectangle encadré en rouge de figurant sur la route.

#### _Tunnels_
Les _Tunnels_ sont des routes spéciales, dont le prix à payer peut être supérieur à la longueur effective de cette route. Ainsi, le joueur annonce d'abord le nombre de cartes de la couleur requise (appelons cette couleur **_color_**) comme une route classique. Puis, il retourne de la pioche wagon trois cartes. Pour chaque carte **_color_** ou _locomotive_ révélée, le joueur doit rajouter une nouvelle carte wagon couleur **_color_** (ou une _locomotive_ au choix). C'est uniquement une fois celles-ci rajoutées que le joueur peut prendre possession du _Tunnel_.

  **Remarque** : il est à noter que lors de la phase de pioche des cartes wagon supplémentaires, le joueur peut décider de passer son tour (parce qu'il n'a pas les cartes supplémentaires nécessaires dans sa main). Dans ce cas, le joueur ne défausse aucune carte de sa main et passe tout simplement son tour.

### Les _Objectifs_
Un joueur peut utiliser son tour de jeu pour piocher des _objectifs_ supplémentaires. Pour cela, il doit prendre 3 cartes sur le dessus de la pioche de cartes _objectif_ (le nombre de cartes restantes s'il y en a moins que 3). Il doit conserver **au moins** l’une des trois cartes piochées. Chaque carte non conservée est remise sous la pioche de carte _objectifs_.

Les villes figurant sur une _carte objectif_ sont le point de départ et d'arrivée d'un objectif secret que le joueur s'efforce d’atteindre en reliant ses villes avec des trains de sa couleur avant la fin de la partie. En cas de réussite, les points figurant sur la carte objectif sont gagnés par le joueur lorsque les objectifs sont révélés et comptabilisés en fin de partie. En cas d'échec, ces points sont déduits du score du joueur en fin de partie. Au cours d'une partie, un joueur peut piocher et conserver autant de cartes objectifs qu’il le souhaite.

### Bâtir une gare
Une gare permet à son propriétaire d'emprunter une des routes appartenant à l'un de ses opposants pour relier des villes. Les gares peuvent être construites sur n'importe quelle ville qui n'en contient pas. Chaque joueur peut en construire une seule par tour. Le nombre de gares maximum par joueur est de 3. 

### Fin du jeu
Le déclenchement de la fin de la partie a lieu lorsqu'un joueur **_toto_** vient de capturer une route et sa réserve de wagons passe à 0, 1 ou 2 wagons. Ainsi, après le tour de **_toto_**, chaque joueur, en incluant **_toto_**, joue un dernier tour. À l’issue de celui-ci, le jeu s’arrête et chacun compte ses points.
