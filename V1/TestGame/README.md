# TestGame

Projet de jeu libGDX (LWJGL3) avec architecture par ecrans, systemes et assets. Ce README documente chaque fichier de code et chaque fonction exposee dans le projet.

## Lancer le projet

Commande depuis la racine du projet :

```
./gradlew.bat lwjgl3:run
```

## Structure du projet (dossiers et fichiers)

### Racine
- `build.gradle` : configuration Gradle commune (repositories, plugins IDE, task `generateAssetList`, config Java 17, nom d'app).
- `settings.gradle` : declare les modules `core` et `lwjgl3`.
- `gradle.properties` : versions (gdx, graal), options Gradle et JVM.
- `gradlew` / `gradlew.bat` : wrapper Gradle pour Linux/macOS et Windows.
- `gradle/gradle-daemon-jvm.properties` : URLs de toolchains et version JDK pour le daemon Gradle.
- `gradle/wrapper/gradle-wrapper.properties` : version du wrapper (Gradle 9.2.1).
- `gradle/wrapper/gradle-wrapper.jar` : binaire du wrapper.
- `assets/` : assets utilises en runtime.
- `assetsSource/` : sources artistiques (fichiers `.aseprite`) hors runtime.
- `core/` : code commun du jeu.
- `lwjgl3/` : launcher desktop et packaging LWJGL3.
- `build/` : sorties generees (ne pas modifier).
- `.gradle/` : cache Gradle (ne pas modifier).

### Module `core`
- `core/build.gradle` : dependance `gdx` et config encodage.
- `core/src/main/java/fr/shogaro/testgame/...` : logique du jeu, ecrans, systemes, entites.

### Module `lwjgl3`
- `lwjgl3/build.gradle` : dependances LWJGL3, config run/jar/distributions, construo.
- `lwjgl3/nativeimage.gradle` : config GraalVM (si active).
- `lwjgl3/src/main/java/fr/shogaro/testgame/lwjgl3/...` : launcher desktop.
- `lwjgl3/icons/` : icones de packaging.
- `lwjgl3/src/main/resources/` : ressources du launcher (icones libGDX).

### Assets runtime
- `assets/assets.txt` : liste exhaustive des assets, generee par la task `generateAssetList`.
- `assets/audio/` : musiques et sons.
- `assets/maps/` : textures de map.
- `assets/characters/` : sprites et animations des personnages.
- `assets/mobs/` : sprites des ennemis.
- `assets/bosses/` : sprites des boss.
- `assets/vfx/` : effets visuels (ex: damage).
- `assets/ui/` : images UI (ex: libgdx.png).

## Detail des fichiers et fonctions

### `core/src/main/java/fr/shogaro/testgame/Main.java`
Role : point d'entree du jeu (extends `Game`), cree les ressources partagees et gere la navigation entre ecrans.

Fonctions :
- `create()` : instancie `SpriteBatch`, `ShapeRenderer`, `BitmapFont`, `UiRenderer`, `AssetStore`, `AudioSystem` et affiche le menu.
- `getBatch()` : retourne le `SpriteBatch` partage.
- `getShapeRenderer()` : retourne le `ShapeRenderer` partage.
- `getUiRenderer()` : retourne le `UiRenderer` partage.
- `getAudioSystem()` : retourne le `AudioSystem` partage.
- `getAssetStore()` : retourne le `AssetStore` partage.
- `showMenu()` : bascule vers `MenuScreen`.
- `showSettings()` : bascule vers `SettingsScreen`.
- `showGame(CharacterType choice)` : bascule vers `GameScreen` avec le perso choisi.
- `showEnd(String message)` : bascule vers `EndScreen` avec un message.
- `switchScreen(Screen newScreen)` : remplace l'ecran courant et dispose l'ancien.
- `render()` : appelle le render de l'ecran courant via `super.render()`.
- `dispose()` : libere ecran courant et ressources partagees.

### `core/src/main/java/fr/shogaro/testgame/CharacterType.java`
Role : enum des personnages jouables.

Valeurs :
- `IOP`
- `SRAM`

### `core/src/main/java/fr/shogaro/testgame/render/UiRenderer.java`
Role : utilitaire d'affichage texte (centrage, scale, couleur) via `BitmapFont`.

Fonctions :
- `UiRenderer(BitmapFont font)` : constructeur, injecte la font.
- `drawCentered(SpriteBatch batch, String text, float y, float scale, Color color)` : dessine un texte centre en X.
- `draw(SpriteBatch batch, String text, float x, float y, float scale, Color color)` : dessine un texte a une position donnee.
- `getTextWidth(String text, float scale)` : retourne la largeur du texte apres scale.
- `resetFont()` : remet scale et couleur par defaut (private).
- `dispose()` : methode vide, reservee si besoin.

### `core/src/main/java/fr/shogaro/testgame/systems/AssetStore.java`
Role : charge et detient les textures/animations communes (menu + map).

Fonctions :
- `load()` : charge la map et les animations idle de menu.
- `getMapTexture()` : retourne la texture de map.
- `getMenuIdleAnimation(CharacterType type)` : retourne l'animation idle selon le perso.
- `buildAnimationSeries(String basePath, String framePrefix, int frameCount, float frameDuration)` : construit une animation a partir d'images (private).
- `dispose()` : libere toutes les textures chargees.

### `core/src/main/java/fr/shogaro/testgame/systems/AudioSystem.java`
Role : gere la musique (lecture, volume, activation) et sa disponibilite.

Fonctions :
- `AudioSystem(String musicPath)` : constructeur, stocke le chemin.
- `initialize()` : verifie la presence et initialise `Music` si possible.
- `toggleEnabled()` : active/desactive la musique.
- `setVolume(float newVolume)` : change le volume (clamp 0..1).
- `changeVolume(float delta)` : applique un delta de volume.
- `applySettings()` : applique `enabled` et `volume` a la musique.
- `isAvailable()` : retourne si la musique est chargeable.
- `isEnabled()` : retourne l'etat ON/OFF.
- `getVolume()` : retourne le volume courant.
- `dispose()` : libere la ressource `Music`.

### `core/src/main/java/fr/shogaro/testgame/screens/MenuScreen.java`
Role : ecran de menu, selection de personnage et navigation vers le jeu ou les settings.

Fonctions :
- `MenuScreen(Main game)` : constructeur, recupere les ressources partagees.
- `render(float delta)` : logiques menu + rendu (cartes, titres, selection).
- `handleInput()` : lit les touches et met a jour la selection (private).
- `renderMenuCharacter(CharacterType choice, float x, float y, float width, float height)` : affiche l'animation d'un perso (private).
- `renderMenuLabel(String text, float x, float y, float width, boolean selected)` : affiche le label avec couleur (private).
- `resize(int width, int height)` : no-op.
- `show()` : no-op.
- `hide()` : no-op.
- `pause()` : no-op.
- `resume()` : no-op.
- `dispose()` : no-op (pas d'assets propres a cet ecran).

### `core/src/main/java/fr/shogaro/testgame/screens/GameScreen.java`
Role : boucle de jeu (mouvements, collisions, fin de partie), rendu de la map et HUD.

Fonctions :
- `GameScreen(Main game, CharacterType selection)` : constructeur, recupere ressources et demarre la partie.
- `render(float delta)` : update + rendu, detecte victoire/defaite.
- `startGame(CharacterType selection)` : instancie le joueur et les mobs (private).
- `renderHealthBars()` : dessine les barres de vie joueur/mobs (private).
- `renderHitboxes()` : dessine les hitboxes (debug visuel) (private).
- `resize(int width, int height)` : no-op.
- `show()` : no-op.
- `hide()` : no-op.
- `pause()` : no-op.
- `resume()` : no-op.
- `dispose()` : libere textures des entites.

### `core/src/main/java/fr/shogaro/testgame/screens/SettingsScreen.java`
Role : ecran de configuration audio (toggle musique + volume).

Fonctions :
- `SettingsScreen(Main game)` : constructeur, recupere ressources partagees.
- `render(float delta)` : affiche l'UI et traite les inputs.
- `resize(int width, int height)` : no-op.
- `show()` : no-op.
- `hide()` : no-op.
- `pause()` : no-op.
- `resume()` : no-op.
- `dispose()` : no-op.

### `core/src/main/java/fr/shogaro/testgame/screens/EndScreen.java`
Role : ecran de fin (GG ou GAME OVER).

Fonctions :
- `EndScreen(Main game, String message)` : constructeur avec message.
- `render(float delta)` : affiche le message et attend l'entree.
- `resize(int width, int height)` : no-op.
- `show()` : no-op.
- `hide()` : no-op.
- `pause()` : no-op.
- `resume()` : no-op.
- `dispose()` : no-op.

### `core/src/main/java/fr/shogaro/testgame/entities/Character.java`
Role : base commune des personnages jouables (animation, etats, hitboxes).

Fonctions :
- `Character(int health, int damage, float aSpeed, float aRange, float speed)` : constructeur de base.
- `move()` : deplacement specifique au perso (abstract).
- `update(float delta, Rectangle mobHitbox, Rectangle mobAttackHitbox, int mobDamage)` : logique generale (cooldowns, animations, hits).
- `render(SpriteBatch batch)` : rendu du sprite + overlays (flash/damage).
- `dispose()` : libere toutes les textures d'animation.
- `getHealthMax()` : retourne la vie max.
- `getHealthCurrent()` : retourne la vie courante.
- `getX()` : position X.
- `getY()` : position Y.
- `getAttackDamage()` : degats d'attaque.
- `getAttackSpeed()` : cooldown d'attaque.
- `getAttackRange()` : multiplicateur de portee.
- `getSpeed()` : vitesse de deplacement.
- `getWidth()` : largeur.
- `getHeight()` : hauteur.
- `getAttackHitbox()` : hitbox d'attaque.
- `getHurtHitbox()` : hitbox de degats.
- `takingDamage(int damage)` : applique des degats a la vie.
- `setState(State newState)` : change l'etat et reset le timer (protected).
- `setDirection(Direction newDirection)` : change la direction + historiques (protected).
- `addAnimation(State state, Direction direction, Animation<TextureRegion> animation)` : enregistre une animation (protected).
- `addAnimationForAllDirections(State state, Animation<TextureRegion> animation)` : applique une animation a toutes les directions (protected).
- `setDamageOverlayAnimation(Animation<TextureRegion> animation)` : definit l'animation d'effet de degats (protected).
- `buildAnimationSeries(String basePath, String framePrefix, int frameCount, float frameDuration, Animation.PlayMode playMode)` : cree une animation multi-frames (protected).
- `buildSingleFrame(String path)` : cree une animation a une frame (protected).
- `triggerAttack()` : passe en etat ATTACK et active le trigger (protected).
- `triggerDamage(int damage)` : applique degats + effet overlay/flash (protected).
- `isCurrentAnimationFinished()` : indique si l'animation courante est terminee (protected).
- `getAnimation(State state, Direction direction)` : recupere une animation avec fallback (protected).
- `resolveDirectionForState(State state, Direction baseDirection)` : corrige la direction selon l'etat (protected).
- `updateHitboxes()` : recalcule les hitboxes (protected).
- `consumeAttackTriggered()` : consomme le trigger d'attaque si actif.
- `tryTriggerAttack()` : tente de declencher une attaque si cooldown ok.
- `applyDamage(int damage)` : applique les degats si cooldown ok.

### `core/src/main/java/fr/shogaro/testgame/entities/Assassin.java`
Role : personnage SRAM (rapide, grande portee).

Fonctions :
- `Assassin(float x, float y, float width, float height)` : constructeur, initialise stats et animations.
- `move()` : deplacement clavier + gestion direction/etat.
- `loadAnimations()` : charge toutes les animations de SRAM (private).

### `core/src/main/java/fr/shogaro/testgame/entities/Warrior.java`
Role : personnage IOP (tank/dps).

Fonctions :
- `Warrior(float x, float y, float width, float height)` : constructeur, initialise stats et animations.
- `move()` : deplacement clavier + gestion direction/etat.
- `loadAnimations()` : charge toutes les animations de IOP (private).

### `core/src/main/java/fr/shogaro/testgame/entities/Mob.java`
Role : ennemi basique qui suit le joueur.

Fonctions :
- `Mob(float x, float y, float width, float height, float speed, int damage, int health, String basePath)` : constructeur, charge animations et hitboxes.
- `update(float delta, float targetX, float targetY, float targetWidth, float targetHeight)` : suit la cible + anime.
- `render(SpriteBatch batch)` : dessine l'ennemi.
- `dispose()` : libere textures d'animation.
- `getHitbox()` : hitbox principale.
- `getAttackHitbox()` : hitbox d'attaque.
- `getHealthMax()` : vie max.
- `getHealthCurrent()` : vie courante.
- `getDamage()` : degats.
- `takingDamage(int damage)` : applique degats.
- `updateHitboxes()` : met a jour les hitboxes (private).
- `loadAnimations(String basePath)` : charge animations gauche/droite (private).
- `buildAnimationSeries(String basePath, String framePrefix, int frameCount, float frameDuration)` : cree une animation (private).

### `lwjgl3/src/main/java/fr/shogaro/testgame/lwjgl3/Lwjgl3Launcher.java`
Role : point d'entree desktop LWJGL3 et config de fenetre.

Fonctions :
- `main(String[] args)` : verifie le redemarrage JVM puis lance l'appli.
- `createApplication()` : cree l'application LWJGL3.
- `getDefaultConfiguration()` : definit la config (titre, vsync, resolution, icones, OpenGL).

### `lwjgl3/src/main/java/fr/shogaro/testgame/lwjgl3/StartupHelper.java`
Role : helper libGDX pour relancer la JVM sur macOS et contourner des soucis Windows.

Fonctions :
- `StartupHelper()` : constructeur prive, lance une exception pour empecher l'instanciation.
- `startNewJvmIfRequired(boolean redirectOutput)` : relance la JVM si necessaire (macOS/Windows) et retourne un booleen.
- `startNewJvmIfRequired()` : surcharge qui redirige la sortie par defaut.

## Notes sur les assets
- `assets/assets.txt` est genere automatiquement par Gradle (task `generateAssetList`).
- `assetsSource/` contient les fichiers `.aseprite` et n'est pas utilise au runtime.
- Toutes les references d'assets dans le code partent de la racine `assets/`.

## Conventions
- Nommage des dossiers en camelCase.
- Code Java en modules clairs : `screens`, `systems`, `render`, `entities`.
