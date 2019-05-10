# Installation de Yaz4j

La librarie yaz4j.jar s'appuie sur des librairies natives, qui doivent être installées sur le poste sur lequel s'exécute l'application pour que les recherches Z39.50 fonctionnent correctement.

## Linux

* Installer la librarie libyaz4 (YAZ Z39.50 toolkit)

```
	apt-get install libyaz4
```
	
* Récupérer la librairie libyaz4j.so et la placer dans le répertoire correspondant à la valeur de la propriété java.library.path.

## Windows

```
	For Windows users: yaz4j is now part of the YAZ Windows installer and this is the recommended way of obtaining yaz4j on that platform.
```

* Récupérer Yaz sur [http://www.indexdata.com/yaz](http://www.indexdata.com/yaz)
* Lancer l'installation (a priori, il faut au minimum le runtime + yaz path)
* La librairie yaz4j.dll et ses dépendances doivent être accessibles via la variable d'environnement PATH
* Redémarrer le poste.
