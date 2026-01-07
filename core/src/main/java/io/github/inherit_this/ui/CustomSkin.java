package io.github.inherit_this.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import io.github.inherit_this.util.FontManager;

/**
 * Custom VisUI skin that uses the game's pixel font (m3x6)
 */
public class CustomSkin {

    private static Skin skin;

    /**
     * Initialize VisUI with custom m3x6 font
     */
    public static void initialize() {
        // Load default VisUI first
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        skin = VisUI.getSkin();

        // Replace fonts with our pixel font
        BitmapFont menuFont = FontManager.getInstance().getMenuFont();
        BitmapFont uiFont = FontManager.getInstance().getUIFont();

        // Replace all default fonts with our custom font
        skin.add("default-font", menuFont, BitmapFont.class);
        skin.add("small-font", uiFont, BitmapFont.class);

        // Update button styles to use our font
        skin.get("default", com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle.class).font = menuFont;
        skin.get("default", com.kotcrab.vis.ui.widget.VisLabel.LabelStyle.class).font = menuFont;

        // Create custom styles for title and subtitle
        com.kotcrab.vis.ui.widget.VisLabel.LabelStyle titleStyle = new com.kotcrab.vis.ui.widget.VisLabel.LabelStyle();
        titleStyle.font = menuFont;
        titleStyle.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
        skin.add("title", titleStyle);

        com.kotcrab.vis.ui.widget.VisLabel.LabelStyle subtitleStyle = new com.kotcrab.vis.ui.widget.VisLabel.LabelStyle();
        subtitleStyle.font = uiFont;
        subtitleStyle.fontColor = com.badlogic.gdx.graphics.Color.LIGHT_GRAY;
        skin.add("subtitle", subtitleStyle);
    }

    public static Skin getSkin() {
        if (skin == null) {
            initialize();
        }
        return skin;
    }

    public static void dispose() {
        if (VisUI.isLoaded()) {
            VisUI.dispose();
        }
    }
}
