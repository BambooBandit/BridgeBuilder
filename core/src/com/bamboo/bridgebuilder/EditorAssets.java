package com.bamboo.bridgebuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;

/**Singleton that holds all different kinds
 * of textures, colors, fonts and sounds.*/
public class EditorAssets
{
    private static EditorAssets editorAssets = null;
    private static AssetManager assets;
    private static BitmapFont smallFont;
    private static BitmapFont font;
    private static BitmapFont headerFont;
    private static GlyphLayout glyph;
    private static TextureAtlas uiAtlas;
    private static Skin uiSkin;

    private static ObjectMap<String, ObjectMap<String, TextureAtlas.AtlasRegion>> nameToCachedAtlas = new ObjectMap<>();
    private static ObjectMap<String, TextureAtlas> nameToGameAtlas = new ObjectMap<>();

    private EditorAssets() { }

    /**Gets the static instance if the GameAssets class.
     * It holds all different kinds of textures, colors,
     * fonts and sounds.
     * @return gameAssets, instantiates it if null*/
    public static EditorAssets get()
    {
        if(editorAssets == null)
        {
            EditorAssets.editorAssets = new EditorAssets();
            assets = new AssetManager();
            uiSkin = new Skin();
            initFonts();
            loadAssets();
        }
        return editorAssets;
    }


    public static <T> T getAsset(String string, Class<T> c) { return get().assets.get(string, c); }

    /**Fills the AssetManager object with
     * texture atlases for UI and game art.*/
    public static void loadAssets()
    {
        assets.load("ui/ui.atlas", TextureAtlas.class);
        assets.finishLoading();
        uiAtlas = assets.get("ui/ui.atlas");
        uiSkin.addRegions(uiAtlas);
        uiSkin.add("default-font", font);
        uiSkin.add("header-font", headerFont);
        uiSkin.add("small-font", smallFont);
        uiSkin.load(Gdx.files.internal("ui/ui.json"));
        assets.finishLoading();
    }

    /**Initializes fonts upon game start up.
     * The fonts will be sized according to window size.*/
    private static void initFonts()
    {
        int size = 13;

        FreeTypeFontGenerator.setMaxTextureSize(2048);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/sitka.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = size;
        params.color = Color.WHITE;
        font = generator.generateFont(params);

        params.size = (int) (size * 2.5);
        headerFont = generator.generateFont(params);

        params.size = (int) (size / 1.5f);
        smallFont = generator.generateFont(params);

        glyph = new GlyphLayout();
    }

    public static EditorAssets getEditorAssets()
    {
        return get().editorAssets;
    }

    public static AssetManager getAssets()
    {
        return get().assets;
    }

    public static BitmapFont getFont() { return get().font; }

    public static BitmapFont getHeaderFont() { return get().headerFont; }

    public static BitmapFont getSmallFont() { return get().smallFont; }

    public static GlyphLayout getGlyph()
    {
        return get().glyph;
    }

    public static TextureAtlas getGameAtlas(String name)
    {
        return get().nameToGameAtlas.get(name);
    }

    /**Should only be used by the Loader
     * @param name - Which atlas to work with
     * @param atlas - Setter*/
    public static void setGameAtlas(String name, TextureAtlas atlas)
    {
        get().nameToGameAtlas.put(name, atlas);
        ObjectMap<String, TextureAtlas.AtlasRegion> cachedGameAtlas = new ObjectMap<>(atlas.getRegions().size);
        for(int i = 0; i < atlas.getRegions().size; i++)
            cachedGameAtlas.put(atlas.getRegions().get(i).name, atlas.getRegions().get(i));
        nameToCachedAtlas.put(name, cachedGameAtlas);
    }

    public static TextureRegion getTextureRegion(String name, String path)
    {
        if(nameToCachedAtlas.get(name) == null)
            return null;
        return nameToCachedAtlas.get(name).get(path);
    }

    public static boolean hasTextureRegion(String name, String path) { return nameToCachedAtlas.get(name).containsKey(path); }

    public static Sprite createSprite(String name, String path) { return new Sprite(nameToCachedAtlas.get(name).get(path)); }

    public static TextureAtlas.AtlasSprite createAtlasSprite(String name, String path) { return new TextureAtlas.AtlasSprite(nameToCachedAtlas.get(name).get(path)); }

    public static TextureAtlas getUIAtlas()
    {
        return get().uiAtlas;
    }

    public static Skin getUISkin()
    {
        return get().uiSkin;
    }
}
