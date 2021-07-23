package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.*;

public class MergeDialog extends Window
{
    private TextButton mergeSelected;
    private TextField mergePropertyTextField1;
    private TextField mergePropertyTextField2;
    private TextButton mergeProperty;
    private TextButton close;

    private Skin skin;

    private Table choiceTable;
    private Table table;

    private Map map;

    private float z = 0;

    public MergeDialog(Stage stage, Skin skin, Map map)
    {
        super("Merge Polygons", skin);
        this.skin = skin;

        this.map = map;

        this.choiceTable = new Table();
        this.table = new Table();

        this.mergeSelected = new TextButton("Merge Selected", skin);
        this.mergeProperty = new TextButton("Merge by Property Minus Selected", skin);
        this.mergePropertyTextField1 = new TextField("", skin);
        this.mergePropertyTextField2 = new TextField("", skin);

        this.mergeSelected.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                    mergeSelected();
                    close();
            }
        });

        this.mergeProperty.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                    mergeProperty(mergePropertyTextField1.getText(), mergePropertyTextField2.getText());
                    close();
            }
        });

        this.close = new TextButton("Close", skin);
        this.close.setColor(Color.FIREBRICK);
        this.close.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                close();
            }
        });

        this.choiceTable.add();
        this.choiceTable.add(this.mergePropertyTextField1).padBottom(15).row();
        this.choiceTable.add();
        this.choiceTable.add(this.mergePropertyTextField2).padBottom(15).row();
        this.choiceTable.add(this.mergeSelected).padRight(15).padBottom(15);
        this.choiceTable.add(this.mergeProperty).padBottom(15);
        this.table.add(this.choiceTable).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 4f, Gdx.graphics.getHeight() / 4f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(false);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        if(!isVisible())
            return;

        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            if (Utils.getPropertyField(layer.properties, "playableFloor") != null)
            {
                this.z = layer.z;
                break;
            }
        }

        map.mergedPolygonPreview = findPolygonsWithProperty(mergePropertyTextField1.getText(), mergePropertyTextField2.getText());
    }

    private void mergeSelected()
    {
        map.mergePolygons(map.selectedObjects);
    }

    private void mergeProperty(String property1, String property2)
    {
        Array<MapObject> mapPolygons = findPolygonsWithProperty(property1, property2);
        map.mergePolygons(mapPolygons);
    }

    public Array<MapObject> findPolygonsWithProperty(String property1, String property2)
    {
        Array<MapObject> mapPolygons = new Array<>();
        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                if(objectLayer.z != z)
                    continue;
                for(int k = 0; k < objectLayer.children.size; k ++)
                {
                    MapObject mapObject = objectLayer.children.get(k);
                    if(!mapObject.selected && mapObject instanceof MapPolygon && (Utils.getPropertyField(mapObject.properties, property1) != null || Utils.getPropertyField(mapObject.properties, property2) != null))
                        mapPolygons.add(mapObject);
                }
            }
            else if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                if(spriteLayer.z != z)
                    continue;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    if(mapSprite.attachedMapObjects != null)
                    {
                        for(int s = 0; s < mapSprite.attachedMapObjects.size; s ++)
                        {
                            MapObject mapObject = mapSprite.attachedMapObjects.get(s);
                            if(!mapObject.selected && mapObject instanceof MapPolygon && (Utils.getPropertyField(mapObject.properties, property1) != null || Utils.getPropertyField(mapObject.properties, property2) != null))
                                mapPolygons.add(mapObject);
                        }
                    }
                }
            }
        }
        return mapPolygons;
    }

    public void close()
    {
        map.mergedPolygonPreview = null;
        this.setVisible(false);
    }

    public void open()
    {
        if(map.selectedLayer == null || !(map.selectedLayer instanceof ObjectLayer))
            return;
        this.setVisible(true);
    }

}
