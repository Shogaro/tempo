# Vue d'ensemble de l'architecture

## 1) Schema d'architecture (classes + relations)

```
                         +-------------------+
                         |     Lwjgl3Launcher|
                         |  (entree desktop) |
                         +---------+---------+
                                   |
                                   v
+-------------------+     possede  +-------------------+
|     Main (Game)   |------------->| Ressources partagees
| - navigation      |              | SpriteBatch       |
| - changement ecran|              | ShapeRenderer     |
| - cycle de vie    |              | BitmapFont        |
+--+-----+-----+----+              | UiRenderer        |
   |     |     |                   | AssetStore        |
   |     |     |                   | AudioSystem       |
   |     |     |                   +-------------------+
   |     |     |
   |     |     +-------------------> EndScreen
   |     +------------------------> SettingsScreen
   +------------------------------> MenuScreen
   |
   +------------------------------> GameScreen
                                     |
                                     v
                            +------------------+
                            |     Entites      |
                            | Character (base) |
                            |   - Warrior      |
                            |   - Assassin     |
                            | Mob (ennemi)     |
                            +------------------+
```

Resume rapide
- Lwjgl3Launcher demarre l'appli desktop.
- Main cree les ressources partagees et change d'ecran.
- Chaque Screen gere son UI et son flux.
- GameScreen pilote le gameplay et les entites.

## 2) Main.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/Main.java`

Role
- Main est le point d'entree du jeu (extends Game) et possede les ressources partagees.
- Main est le seul endroit qui cree/dispense les ressources globales.
- Main est responsable des transitions d'ecrans.

Public vs private
- Les getters publics exposent les ressources partagees aux ecrans (batch, renderer, audio, assets).
- Les methodes show* publiques sont une API simple de navigation.
- switchScreen est private pour centraliser le cycle de vie des ecrans.

Fonctions
- create(): initialise SpriteBatch, ShapeRenderer, BitmapFont, UiRenderer, AssetStore, AudioSystem, puis affiche le menu.
- getBatch(): retourne le SpriteBatch partage pour le rendu.
- getShapeRenderer(): retourne le ShapeRenderer partage pour le debug/UI.
- getUiRenderer(): retourne l'outil de rendu texte.
- getAudioSystem(): retourne le systeme audio partage.
- getAssetStore(): retourne le store d'assets partage.
- showMenu(): bascule vers MenuScreen.
- showSettings(): bascule vers SettingsScreen.
- showGame(CharacterType choice): bascule vers GameScreen avec le perso choisi.
- showEnd(String message): bascule vers EndScreen avec un message.
- switchScreen(Screen newScreen): remplace l'ecran courant et dispose l'ancien.
- render(): delegue au render de l'ecran courant via Game.
- dispose(): dispose l'ecran courant et les ressources partagees.

Choix de conception
- Main utilise Game pour beneficier de la gestion d'ecrans.
- Les ressources partagees sont creees une seule fois pour eviter rechargements et fuites memoire.
- Le swap d'ecran dispose l'ancien immediatement pour stabiliser la memoire.

## 3) Character.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/entities/Character.java`

Entete et imports
- Declare le package des entites.
- Importe les types libGDX: Texture, Animation, SpriteBatch, TextureRegion, Rectangle, Array.
- Importe EnumMap pour les animations par etat/direction.

Enums internes
- Direction: TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT.
- State: IDLE, MOVE, ATTACK, DAMAGE.

Stats et position
- healthMax / healthCurrent: PV max et PV actuels.
- x, y, width, height: position et taille.
- attackDamage / attackSpeed / attackRange / speed: stats de gameplay.

Suivi d'etat
- direction: direction courante.
- lastHorizontal / lastVertical: dernier axe horizontal/vertical.
- state: etat d'animation courant.
- moving: indique si le perso se deplace.

Timers et flags
- stateTime: accumulateur de temps d'animation.
- attackCooldown / damageCooldown: cooldowns.
- attackTriggered: vrai si une attaque est declenchee ce frame.

Stockage des animations
- animations: EnumMap State -> Direction -> Animation.
- animationTextures: textures a disposer.
- currentFrame: frame a afficher.

Overlay degats + flash
- damageOverlayAnimation / damageOverlayFrame / damageOverlayTime / damageOverlayActive.
- damageOverlayScale controle la taille de l'overlay.
- flashTime / flashDuration / flashActive / flashScale / flashAlpha.

Hitboxes
- attackHitbox et hurtHitbox.

Constructeur
- Character(int health, int damage, float aSpeed, float aRange, float speed)
- Initialise les stats.
- healthCurrent = healthMax.

move() abstraite
- Definie par Warrior et Assassin pour les controles.

update(float delta, Rectangle mobHitbox, Rectangle mobAttackHitbox, int mobDamage)
- Reset attackTriggered.
- Met a jour stateTime et cooldowns.
- Met a jour les hitboxes.
- Declenche l'attaque si overlap et cooldown OK.
- Declenche les degats si overlap et cooldown OK.
- Retourne en MOVE/IDLE si animation ATTACK finie.
- Choisit l'animation et la frame selon l'etat/direction.
- Met a jour overlay degats si actif.
- Met a jour le flash si actif.

render(SpriteBatch batch)
- Dessine currentFrame en (x, y).
- Dessine l'effet flash si actif.
- Dessine l'overlay degats si actif.

dispose()
- Dispose toutes les textures d'animation.

Getters
- getHealthMax, getHealthCurrent
- getX, getY
- getAttackDamage, getAttackSpeed, getAttackRange, getSpeed
- getWidth, getHeight
- getAttackHitbox, getHurtHitbox

takingDamage(int damage)
- Soustrait les degats, clamp a 0.

setState(State newState)
- Change l'etat et reset stateTime si changement.

setDirection(Direction newDirection)
- Met a jour la direction et les memoires horizontale/verticale.
- Gere correctement les diagonales.

addAnimation(State state, Direction direction, Animation<TextureRegion> animation)
- Ajoute une animation dans la map.

addAnimationForAllDirections(State state, Animation<TextureRegion> animation)
- Applique une animation a toutes les directions.

setDamageOverlayAnimation(Animation<TextureRegion> animation)
- Definit l'animation d'overlay degats.

buildAnimationSeries(...)
- Charge des frames depuis le disque.
- Cree une Animation avec la duree et le playMode.

buildSingleFrame(String path)
- Cree une animation d'une seule frame.

triggerAttack()
- Passe en ATTACK, reset cooldown, active attackTriggered.

triggerDamage(int damage)
- Applique degats et active overlay + flash.
- Reset timers et cooldown.

isCurrentAnimationFinished()
- Retourne true si l'animation courante est finie ou absente.

getAnimation(State state, Direction direction)
- Cherche l'animation exacte, fallback BOTTOM, LEFT, TOP_LEFT.

resolveDirectionForState(State state, Direction baseDirection)
- Corrige la direction pour ATTACK via lastHorizontal/lastVertical.
- Fallback sur directions miroir si besoin.

updateHitboxes()
- Hurt hitbox = 45% de la taille, centree.
- Attack hitbox = taille * attackRange.

consumeAttackTriggered()
- Consomme attackTriggered une seule fois.

tryTriggerAttack()
- Tente de declencher l'attaque si cooldown OK.

applyDamage(int damage)
- Applique les degats si cooldown OK.

## 4) GameScreen.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/screens/GameScreen.java`

Role
- Gere la boucle de jeu: input joueur, update mobs, collisions, victoire/defaite.
- Rend la map, les entites et le HUD (barres de vie + hitboxes).

Public vs private
- render/resize/show/hide/pause/resume/dispose publics (interface Screen).
- startGame, renderHealthBars, renderHitboxes en private.

Fonctions
- GameScreen(Main game, CharacterType selection): recupere les ressources partagees et demarre la partie.
- render(float delta): boucle principale, input, updates, win/lose, rendu.
- startGame(CharacterType selection): instancie joueur et mobs.
- renderHealthBars(): dessine les barres de vie.
- renderHitboxes(): dessine les hitboxes (debug).
- dispose(): libere les textures des entites.

Choix de conception
- N'importe pas d'assets directement: AssetStore centralise.
- Gameplay garde dans GameScreen.
- win/lose delegue a EndScreen.

## 5) MenuScreen.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/screens/MenuScreen.java`

Role
- Menu de selection du personnage et navigation vers jeu/settings.
- Rend des cartes animees et des labels.

Public vs private
- Methodes Screen publiques.
- handleInput, renderMenuCharacter, renderMenuLabel en private.

Fonctions
- MenuScreen(Main game): recupere les ressources partagees.
- render(float delta): clear, update menuTime, input, rendu cartes + UI.
- handleInput(): selection gauche/droite, Enter/Space pour jouer, S pour settings.
- renderMenuCharacter(...): dessine animation selec/deselec.
- renderMenuLabel(...): dessine le label centre.
- resize/show/hide/pause/resume/dispose: no-op.

Choix de conception
- Utilise AssetStore pour les animations idle.
- Utilise UiRenderer pour le texte.
- Contour selection en ShapeRenderer pour la lisibilite.

## 6) SettingsScreen.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/screens/SettingsScreen.java`

Role
- Ecran de reglages audio (toggle + volume).

Public vs private
- Uniquement les methodes Screen publiques.

Fonctions
- SettingsScreen(Main game): recupere UiRenderer et AudioSystem.
- render(float delta): clear, input, rendu texte.
- resize/show/hide/pause/resume/dispose: no-op.

Choix de conception
- Toute la logique audio est dans AudioSystem.
- UI texte simple pour rapidite.

## 7) EndScreen.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/screens/EndScreen.java`

Role
- Affiche un message de fin (GG ou GAME OVER) et retour menu.

Public vs private
- Uniquement les methodes Screen publiques.

Fonctions
- EndScreen(Main game, String message): stocke le message et les ressources partagees.
- render(float delta): rend le message, attend Enter/Space.
- resize/show/hide/pause/resume/dispose: no-op.

Choix de conception
- Un seul ecran pour win/lose.

## 8) AssetStore.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/systems/AssetStore.java`

Role
- Charge et met en cache les textures/animations communes.

Public vs private
- load/getters/dispose en public.
- buildAnimationSeries en private.

Fonctions
- load(): charge la map et les animations idle menu.
- getMapTexture(): retourne la texture map.
- getMenuIdleAnimation(CharacterType type): retourne l'animation selon le perso.
- buildAnimationSeries(...): charge une sequence de frames.
- dispose(): libere toutes les textures.

Choix de conception
- Liste des textures possedees pour un dispose propre.
- Centralisation des assets menu.

## 9) AudioSystem.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/systems/AudioSystem.java`

Role
- Gere musique: disponibilite, volume, activation.

Public vs private
- API publique stable: initialize, toggle, set volume, dispose.
- Champs internes prives.

Fonctions
- AudioSystem(String musicPath): stocke le chemin.
- initialize(): verifie l'asset et cree Music si possible.
- toggleEnabled(): ON/OFF.
- setVolume(float newVolume): clamp et applique.
- changeVolume(float delta): ajuste le volume.
- applySettings(): play/pause + volume.
- isAvailable(): musique disponible.
- isEnabled(): musique activee.
- getVolume(): volume courant.
- dispose(): libere Music.

Choix de conception
- Centralise l'audio pour eviter l'acces direct aux ecrans.
- Protege contre les assets manquants.

## 10) UiRenderer.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/render/UiRenderer.java`

Role
- Outils de rendu texte (centrage, scale, couleurs).

Public vs private
- draw/drawCentered/getTextWidth en public.
- resetFont en private.

Fonctions
- UiRenderer(BitmapFont font): constructeur.
- drawCentered(...): centre horizontalement et dessine le texte.
- draw(...): dessine le texte a une position.
- getTextWidth(...): mesure la largeur du texte.
- resetFont(): restaure scale/couleur par defaut.
- dispose(): placeholder.

Choix de conception
- Evite la repetition des calculs de layout.
- Reset automatique pour garder un etat propre.

## 11) Warrior.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/entities/Warrior.java`

Role
- Personnage jouable IOP: plus tanky, plus lent.

Public vs private
- Constructeur et move() publics.
- loadAnimations en private.

Fonctions
- Warrior(float x, float y, float width, float height): init stats, position, animations.
- move(): input clavier, deplacement, clamp ecran, direction/etat.
- loadAnimations(): enregistre MOVE/IDLE/ATTACK + overlay degats.

Choix de conception
- Reutilise toute la logique de Character.
- Stats hardcode pour l'instant.

## 12) Assassin.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/entities/Assassin.java`

Role
- Personnage jouable SRAM: rapide, grande portee, moins de PV.

Public vs private
- Constructeur et move() publics.
- loadAnimations en private.

Fonctions
- Assassin(float x, float y, float width, float height): init stats, position, animations.
- move(): input clavier, deplacement, clamp ecran, direction/etat.
- loadAnimations(): enregistre MOVE/IDLE/ATTACK + overlay degats.

Choix de conception
- Meme pattern de mouvement que Warrior.
- Differenciation via stats + assets.

## 13) Mob.java explication detaillee

Fichier: `core/src/main/java/fr/shogaro/testgame/entities/Mob.java`

Role
- Ennemi basique qui se deplace vers le joueur.

Public vs private
- API publique pour update/render/getters.
- Helpers internes en private.

Fonctions
- Mob(...): fixe stats, charge animations, init hitboxes.
- update(...): avance vers cible et anime.
- render(SpriteBatch batch): dessine la frame.
- dispose(): libere textures.
- getHitbox(), getAttackHitbox(): acces collisions.
- getHealthMax(), getHealthCurrent(), getDamage(): getters.
- takingDamage(int damage): applique degats.
- updateHitboxes(): met a jour rectangles.
- loadAnimations(String basePath): charge animations gauche/droite.
- buildAnimationSeries(...): construit l'animation.

Choix de conception
- IA simple: se diriger vers le centre joueur.
- Attack hitbox = taille du sprite.

## 14) Lwjgl3Launcher.java explication detaillee

Fichier: `lwjgl3/src/main/java/fr/shogaro/testgame/lwjgl3/Lwjgl3Launcher.java`

Role
- Entree desktop et configuration fenetre.

Public vs private
- main() public.
- createApplication() et getDefaultConfiguration() en private.

Fonctions
- main(String[] args): verifie la JVM et lance.
- createApplication(): cree l'app LWJGL3 avec Main.
- getDefaultConfiguration(): titre, vsync, resolution, icones, OpenGL.

Choix de conception
- Utilise StartupHelper (macOS + Windows).
- Centralise la config fenetre.

## 15) StartupHelper.java explication detaillee

Fichier: `lwjgl3/src/main/java/fr/shogaro/testgame/lwjgl3/StartupHelper.java`

Role
- Redemarre la JVM sur macOS si necessaire et corrige un bug Windows.

Public vs private
- Constructeur prive et exception pour interdire l'instanciation.
- startNewJvmIfRequired publics.

Fonctions
- StartupHelper(): private, leve UnsupportedOperationException.
- startNewJvmIfRequired(boolean redirectOutput): detecte OS, relance si besoin.
- startNewJvmIfRequired(): surcharge avec redirection.

Choix de conception
- Defense contre l'obligation XstartOnFirstThread sur macOS.
- Contournement des chemins Windows non-ASCII.

## 16) Fichiers Gradle explication detaillee

Fichier: `build.gradle`
Role
- Config Gradle racine partagee.

Sections principales
- buildscript.repositories: repos Maven.
- allprojects: plugins IDE et output dirs.
- configure(subprojects): java-library, Java 17, task generateAssetList.
- generateAssetList: genere assets.txt a partir de assets/.
- processResources.dependsOn generateAssetList: garantit la mise a jour.
- subprojects.repositories: Maven Central, local, snapshots, JitPack.
- eclipse.project.name: nom du projet parent.

Fichier: `settings.gradle`
Role
- Declare les modules core et lwjgl3.

Fichier: `gradle.properties`
Role
- Options Gradle et versions.

Champs clefs
- org.gradle.daemon: desactive par defaut.
- org.gradle.jvmargs: memoire et encodage UTF-8.
- gdxVersion: version libGDX.
- graalHelperVersion / enableGraalNative: config Graal.
- projectVersion: version.

Fichier: `core/build.gradle`
Role
- Dependances du module core.

Champs clefs
- Dependances gdx.
- Encodage UTF-8.

Fichier: `lwjgl3/build.gradle`
Role
- Config desktop, run et packaging.

Sections principales
- application.mainClass: Lwjgl3Launcher.
- sourceSets.main.resources: ajoute assets/.
- dependencies: backend + natives + core.
- run { workingDir = assets }: resolution assets.
- jar tasks: packaging multi-OS.
- construo: config de packaging.

Fichier: `lwjgl3/nativeimage.gradle`
Role
- Config GraalVM optionnelle.

Sections principales
- graalvmNative.binaries: imageName, mainClass, args.
- generateResourcesConfigFile: genere resource-config.json.

Fichier: `gradle/gradle-daemon-jvm.properties`
Role
- URLs toolchains et version JDK.

Fichier: `gradle/wrapper/gradle-wrapper.properties`
Role
- URL et parametres du wrapper (Gradle 9.2.1).

## 17) Assets: structure et conventions

Racine runtime: `assets/`
- audio/: musiques et sons.
- maps/: textures de map.
- characters/: sprites et animations des persos.
- mobs/: sprites et animations des ennemis.
- bosses/: sprites des boss.
- vfx/: effets visuels (degats).
- ui/: images UI.

Racine sources: `assetsSource/`
- Contient les .aseprite (hors runtime).
- Meme structure que assets/ quand possible.

assets.txt
- Genere par generateAssetList.
- Liste exhaustive des assets.
- Ne pas editer a la main.

Conventions
- Nommage des dossiers en camelCase.
- Chemins dans le code relatifs a assets/.

## 18) Resume des stats personnages (valeurs actuelles)

Warrior (IOP)
- HEALTH_MAX: 125
- DAMAGE: 75
- ATTACK_SPEED: 0.8f
- ATTACK_RANGE: 1.5f
- SPEED: 125

Assassin (SRAM)
- HEALTH_MAX: 75
- DAMAGE: 50
- ATTACK_SPEED: 1.5f
- ATTACK_RANGE: 3.0f
- SPEED: 250

Notes
- Constantes hardcodees dans Warrior et Assassin.
- Peut etre externalise dans une config plus tard.
