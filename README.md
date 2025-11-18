## Java Framework

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff). Gradle will manage all dependencies. This was generated with a libGDX template.

## About the project

I've always wanted to make a game in Java. This is mostly because two of my favorite and most influential games in my life were developed in this programming language (RuneScape and Minecraft). In an effort to get more experience with Java and OOP principles, I wanted to have some fun and make a game myself.

## Project updates

### Update 1:
I spent most of my time focusing on world generation. I landed on 2D top-down graphics for simplicity, as this is my first game. With that in mind, I decided to then create a `World` class, which will generate `Chunks` of 8x8 `Tiles`. These tiles are random grass tiles so it does not look bland. Once I drew the character model in Paint.NET and a couple grass tiles, I was able to render the character and move it with WASD across tiles.
![Character in game](dev_stories/update_1_photos/player_in_world.png)
![Tile samples](dev_stories/update_1_photos/tile_samples.png)

Somewhere in between, I needed to create different screens for the game to use. I knew it would be useful to go from a main menu and into the game, but also from the game into a pause menu. I have stubbed out a settings button for future use.
![Main menu](dev_stories/update_1_photos/main_menu.png)
![Pause menu](dev_stories/update_1_photos/pause_menu.png)

In this update, I have also started working on debugging tools. Although incomplete, I found it difficult to make progress without tools. I think starting on this in my first update will help me understand game development and programming in Java a lot more.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
TLDR; Use `gradlew run` to run the game.

Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
