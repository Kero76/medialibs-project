# MediaLibs Services

## Installation

Pour installer les dépendances du **core** de MediaLibs, 
il faut utiliser la commande suivante : 
`mvn install:install-file -Dfile=[path_to_source]/[jar_name].jar -DgroupId=[group_id] -DartifactId=[artifactId] -Dversion=[jar_version] -Dpackaging=jar`
en remplaçant les éléments entre '[]' par la bibliothèque que vous souhaitez ajouter.

Cela aura pour effet d'ajouter ces bibliothèques au sein de votre repository 
local présent dans le dossier *$HOME/.m2/*, qui seront ains utilisable 
par maven directement, comme celle présente sur le site Maven Repository.

Ensuite, il vous faudra faire la commande suivante :
`mvn spring-boot:run` pour lancer le serveur embarqué et 
pouvoir tester les services, en vérifiant que vous vous trouvez
bien dans un dossier racine d'un des services.

## Auteur 
- Nicolas GILLE : <nic.gille@gmail.com>

## License 
Ce projet est sous license GPLv3.

