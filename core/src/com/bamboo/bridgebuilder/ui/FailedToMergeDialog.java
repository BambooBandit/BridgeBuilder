package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.commands.SelectLayer;
import com.bamboo.bridgebuilder.commands.SelectLayerChild;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.ObjectLayer;
import com.bamboo.bridgebuilder.map.PolygonMerger;

public class FailedToMergeDialog extends Window
{
    private TextButton returnResults;
    private TextButton cancel;

    private Skin skin;

    private Table choiceTable;
    private Table table;

    private Map map;

    private PolygonMerger polygonMerger;
    private Array<FloatArray> result;

    public FailedToMergeDialog(Stage stage, Skin skin, Map map, PolygonMerger polygonMerger)
    {
        super("Failed to Merge Polygons.", skin);
        this.skin = skin;

        this.map = map;

        this.polygonMerger = polygonMerger;
        this.result = new Array<>(polygonMerger.result);

        this.choiceTable = new Table();
        this.table = new Table();

        this.returnResults = new TextButton("Return Results", skin);

        this.returnResults.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                    returnResults();
                    close();
            }
        });

        this.cancel = new TextButton("Cancel", skin);
        this.cancel.setColor(Color.FIREBRICK);
        this.cancel.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                cancelMerge();
                close();
            }
        });

        this.choiceTable.add(this.returnResults);
        this.table.add(this.choiceTable).row();
        this.table.add(this.cancel);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 5f, Gdx.graphics.getHeight() / 5f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(true);
    }

    private void returnResults()
    {
        polygonMerger.convertToMapPolygons(result);
        this.setVisible(false);
    }

    public void cancelMerge()
    {
        if(polygonMerger.failedToMergePolygon != null)
        {
            SelectLayer selectLayer = new SelectLayer(map, map.selectedLayer, polygonMerger.failedToMergePolygon.layer, false);
            map.executeCommand(selectLayer);
            SelectLayerChild selectLayerChild = new SelectLayerChild(map, polygonMerger.failedToMergePolygon, false);
            map.executeCommand(selectLayerChild);
        }
        polygonMerger.result.clear();
    }
    public void close()
    {
        polygonMerger.failedPolygon1.clear();
        polygonMerger.failedPolygon2.clear();
        this.setVisible(false);
    }

    public void open()
    {
        if(map.selectedLayer == null || !(map.selectedLayer instanceof ObjectLayer))
            return;
        this.setVisible(true);
    }

}
