package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapPoint;

import javax.swing.event.ChangeEvent;

public class BranchDialog extends Window
{
    private TextButton close;

    private Skin skin;

    private Table table;
    private Label doubleLinkedLabel;
    private CheckBox doubleLinkedCheckBox;
    private TextButton flipBranchButton;

    public BranchDialog(Stage stage, Skin skin)
    {
        super("MapPoint connections/branches", skin);
        this.skin = skin;

        this.table = new Table();

        this.doubleLinkedLabel = new Label("Double linked branches: ", skin);
        this.doubleLinkedCheckBox = new CheckBox("", skin);
        this.doubleLinkedCheckBox.setChecked(false);

        this.flipBranchButton = new TextButton("Flip selected branch points", skin);
        this.flipBranchButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (BridgeBuilder.bridgeBuilder.activeMap == null) return;

                Array<MapObject> selectedObjects = BridgeBuilder.bridgeBuilder.activeMap.selectedObjects;
                Array<MapPoint> selected = new Array<>();
                for (MapObject o : selectedObjects) if (o instanceof MapPoint) selected.add((MapPoint)o);
                if (selected.size == 0) return;

                // snapshots
                ObjectMap<MapPoint, Array<MapPoint>> oldTo = new ObjectMap<>();
                ObjectMap<MapPoint, Array<MapPoint>> oldFrom = new ObjectMap<>();
                for (MapPoint p : selected) {
                    oldTo.put(p, p.toBranchPoints == null ? new Array<MapPoint>() : new Array<MapPoint>(p.toBranchPoints));
                    oldFrom.put(p, p.fromBranchPoints == null ? new Array<MapPoint>() : new Array<MapPoint>(p.fromBranchPoints));
                }

                // update non-selected endpoints so edges incident on selected points get reversed
                for (MapPoint p : selected) {
                    Array<MapPoint> pOldTo = oldTo.get(p);
                    Array<MapPoint> pOldFrom = oldFrom.get(p);

                    // for each q that p used to point to (p -> q), we want q to point to p (q -> p) afterwards
                    for (MapPoint q : pOldTo) {
                        if (selected.contains(q, true)) continue; // skip selected endpoints; they'll be set from their snapshot
                        // remove p from q.from (if present)
                        if (q.fromBranchPoints != null) q.fromBranchPoints.removeValue(p, true);
                        // ensure q.to exists and contains p
                        if (q.toBranchPoints == null) q.toBranchPoints = new Array<>();
                        if (q.toBranchIds == null) q.toBranchIds = new LongArray();
                        if (!q.toBranchPoints.contains(p, true)) {
                            q.toBranchPoints.add(p);
                            if (!q.toBranchIds.contains(p.id)) q.toBranchIds.add(p.id);
                        }
                    }

                    // for each r that used to point to p (r -> p), we want p to point to r afterwards (p -> r),
                    // so remove p from r.to and add p to r.from
                    for (MapPoint r : pOldFrom) {
                        if (selected.contains(r, true)) continue;
                        if (r.toBranchPoints != null) r.toBranchPoints.removeValue(p, true);
                        if (r.toBranchIds != null) r.toBranchIds.removeValue(p.id);
                        if (r.fromBranchPoints == null) r.fromBranchPoints = new Array<>();
                        if (!r.fromBranchPoints.contains(p, true)) r.fromBranchPoints.add(p);
                    }
                }

                // now swap each selected point's lists using the snapshots
                for (MapPoint p : selected) {
                    Array<MapPoint> newTo = oldFrom.get(p); // incoming become outgoing
                    Array<MapPoint> newFrom = oldTo.get(p); // outgoing become incoming

                    // set toBranchPoints / toBranchIds (keep null if empty)
                    if (newTo == null || newTo.size == 0) {
                        p.toBranchPoints = null;
                        p.toBranchIds = null;
                    } else {
                        p.toBranchPoints = new Array<>(newTo);
                        if (p.toBranchIds == null) p.toBranchIds = new LongArray(newTo.size);
                        else p.toBranchIds.clear();
                        for (MapPoint t : p.toBranchPoints) {
                            if (!p.toBranchIds.contains(t.id)) p.toBranchIds.add(t.id);
                        }
                    }

                    // set fromBranchPoints (keep null if empty)
                    if (newFrom == null || newFrom.size == 0) {
                        p.fromBranchPoints = null;
                    } else {
                        p.fromBranchPoints = new Array<>(newFrom);
                    }
                }
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

        this.table.add(this.doubleLinkedLabel).padBottom(15);
        this.table.add(this.doubleLinkedCheckBox).padBottom(15).row();
        this.table.add(this.flipBranchButton).padBottom(15).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 3f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(false);
    }

    public void close()
    {
        this.setVisible(false);
    }

    public void open()
    {
        this.setVisible(true);
    }

    public boolean isDoubleLinked()
    {
        return doubleLinkedCheckBox.isChecked();
    }
}
