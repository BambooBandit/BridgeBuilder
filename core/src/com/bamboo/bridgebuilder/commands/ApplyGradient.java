package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;

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
        this.fromR = map.editor.fileMenu.buttonPane.gradientDialog.getFromR();
        this.fromG = map.editor.fileMenu.buttonPane.gradientDialog.getFromG();
        this.fromB = map.editor.fileMenu.buttonPane.gradientDialog.getFromB();
        this.fromA = map.editor.fileMenu.buttonPane.gradientDialog.getFromA();
        this.toR = map.editor.fileMenu.buttonPane.gradientDialog.getToR();
        this.toG = map.editor.fileMenu.buttonPane.gradientDialog.getToG();
        this.toB = map.editor.fileMenu.buttonPane.gradientDialog.getToB();
        this.toA = map.editor.fileMenu.buttonPane.gradientDialog.getToA();
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.oldColors = new ObjectMap<>();
    }

    public void update()
    {

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
            ColorPropertyField colorProperty = Utils.getLockedColorField("Tint", mapSprite.lockedProperties);
            Color oldColor = new Color(colorProperty.getR(), colorProperty.getG(), colorProperty.getB(), colorProperty.getA());
            this.oldColors.put(mapSprite, oldColor);
            float projY = Utils.project(this.map.camera, mapSprite.getX(), mapSprite.getY()).y;
            float norm = MathUtils.norm(projectedFromY, projectedToY, projY);
            float newR = MathUtils.lerp(this.fromR, this.toR, norm);
            float newG = MathUtils.lerp(this.fromG, this.toG, norm);
            float newB = MathUtils.lerp(this.fromB, this.toB, norm);
            float newA = MathUtils.lerp(this.fromA, this.toA, norm);
            if(this.fromR < 0 || this.toR < 0)
                newR = mapSprite.sprite.getColor().r;
            if(this.fromG < 0 || this.toG < 0)
                newG = mapSprite.sprite.getColor().g;
            if(this.fromB < 0 || this.toB < 0)
                newB = mapSprite.sprite.getColor().b;
            if(this.fromA < 0 || this.toA < 0)
                newA = mapSprite.sprite.getColor().a;
            mapSprite.setColor(newR, newG, newB, newA);
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
