package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;

public class ApplyGradient implements Command
{
    private Map map;
    private Array<MapSprite> selectedSprites;

    private float fromR, fromG, fromB, fromA;
    private float toR, toG, toB, toA;
    private float fromX, fromY, toX, toY;

    private ObjectMap<MapSprite, Color> oldColors;

    public ApplyGradient(Map map, float fromX, float fromY, float toX, float toY)
    {
        this.map = map;
        this.selectedSprites = new Array<>(map.selectedSprites);
        this.fromR = map.editor.fileMenu.toolPane.gradientDialog.getFromR();
        this.fromG = map.editor.fileMenu.toolPane.gradientDialog.getFromG();
        this.fromB = map.editor.fileMenu.toolPane.gradientDialog.getFromB();
        this.fromA = map.editor.fileMenu.toolPane.gradientDialog.getFromA();
        this.toR = map.editor.fileMenu.toolPane.gradientDialog.getToR();
        this.toG = map.editor.fileMenu.toolPane.gradientDialog.getToG();
        this.toB = map.editor.fileMenu.toolPane.gradientDialog.getToB();
        this.toA = map.editor.fileMenu.toolPane.gradientDialog.getToA();
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.oldColors = new ObjectMap<>();
    }

    @Override
    public void execute()
    {
        if(this.oldColors == null)
            this.oldColors = new ObjectMap<>();
        else
            this.oldColors.clear();

        float angle = Utils.degreeAngleFix(90 - Utils.getAngleDegree(this.fromX, this.fromY, this.toX, this.toY));
        this.map.camera.rotate(angle);
        this.map.camera.update();

        float projectedFromY = Utils.project(this.map.camera, this.fromX, this.fromY).y;
        float projectedToY = Utils.project(this.map.camera, this.toX, this.toY).y;

        for(int i = 0; i < this.selectedSprites.size; i ++)
        {
            MapSprite mapSprite = this.selectedSprites.get(i);
            this.oldColors.put(mapSprite, new Color(mapSprite.sprite.getColor()));
            float projY = Utils.project(this.map.camera, mapSprite.getX(), mapSprite.getY()).y;
            float norm = MathUtils.norm(projectedFromY, projectedToY, projY);
            mapSprite.setColor(MathUtils.lerp(this.fromR, this.toR, norm), MathUtils.lerp(this.fromG, this.toG, norm), MathUtils.lerp(this.fromB, this.toB, norm), MathUtils.lerp(this.fromA, this.toA, norm));
        }

        this.map.camera.rotate(-angle);
        this.map.camera.update();
    }

    @Override
    public void undo()
    {
        for(int i = 0; i < this.selectedSprites.size; i ++)
        {
            MapSprite mapSprite = this.selectedSprites.get(i);
            Color oldColor = oldColors.get(mapSprite);
            mapSprite.setColor(oldColor.r, oldColor.g, oldColor.b, oldColor.a);
        }
    }
}
