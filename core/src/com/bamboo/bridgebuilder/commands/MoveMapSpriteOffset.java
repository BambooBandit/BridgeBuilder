package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.math.Vector2;
import com.bamboo.bridgebuilder.map.MapSprite;

import static com.bamboo.bridgebuilder.commands.MoveMapSpriteOffset.Location.*;

public class MoveMapSpriteOffset implements Command
{
    private Vector2 originalOffsetPosition;
    private MapSprite selectedMapSprite;
    private float resultingOffsetX;
    private float resultingOffsetY;
    private Location location;

    public enum Location{ONE, TWO, THREE, FOUR}

    public MoveMapSpriteOffset(MapSprite selectedMapSprite, Location location)
    {
        this.selectedMapSprite = selectedMapSprite;
        this.location = location;

        if(location == ONE)
            this.originalOffsetPosition = new Vector2(selectedMapSprite.x1Offset, selectedMapSprite.y1Offset);
        else if(location == TWO)
            this.originalOffsetPosition = new Vector2(selectedMapSprite.x2Offset, selectedMapSprite.y2Offset);
        else if(location == THREE)
            this.originalOffsetPosition = new Vector2(selectedMapSprite.x3Offset, selectedMapSprite.y3Offset);
        else if(location == FOUR)
            this.originalOffsetPosition = new Vector2(selectedMapSprite.x4Offset, selectedMapSprite.y4Offset);
    }

    public void update(float currentDragX, float currentDragY)
    {
        this.resultingOffsetX = currentDragX;
        this.resultingOffsetY = currentDragY;

        if(location == ONE)
        {
            selectedMapSprite.x1Offset = (originalOffsetPosition.x + this.resultingOffsetX);
            selectedMapSprite.y1Offset = (originalOffsetPosition.y + this.resultingOffsetY);
        }
        else if(location == TWO)
        {
            selectedMapSprite.x2Offset = (originalOffsetPosition.x + this.resultingOffsetX);
            selectedMapSprite.y2Offset = (originalOffsetPosition.y + this.resultingOffsetY);
        }
        else if(location == THREE)
        {
            selectedMapSprite.x3Offset = (originalOffsetPosition.x + this.resultingOffsetX);
            selectedMapSprite.y3Offset = (originalOffsetPosition.y + this.resultingOffsetY);
        }
        else if(location == FOUR)
        {
            selectedMapSprite.x4Offset = (originalOffsetPosition.x + this.resultingOffsetX);
            selectedMapSprite.y4Offset = (originalOffsetPosition.y + this.resultingOffsetY);
        }
    }

    @Override
    public void execute()
    {
        if(location == ONE)
        {
            selectedMapSprite.x1Offset = (originalOffsetPosition.x + this.resultingOffsetX);
            selectedMapSprite.y1Offset = (originalOffsetPosition.y + this.resultingOffsetY);
        }
        else if(location == TWO)
        {
            selectedMapSprite.x2Offset = (originalOffsetPosition.x + this.resultingOffsetX);
            selectedMapSprite.y2Offset = (originalOffsetPosition.y + this.resultingOffsetY);
        }
        else if(location == THREE)
        {
            selectedMapSprite.x3Offset = (originalOffsetPosition.x + this.resultingOffsetX);
            selectedMapSprite.y3Offset = (originalOffsetPosition.y + this.resultingOffsetY);
        }
        else if(location == FOUR)
        {
            selectedMapSprite.x4Offset = (originalOffsetPosition.x + this.resultingOffsetX);
            selectedMapSprite.y4Offset = (originalOffsetPosition.y + this.resultingOffsetY);
        }
    }

    @Override
    public void undo()
    {
        if(location == ONE)
        {
            selectedMapSprite.x1Offset = (originalOffsetPosition.x);
            selectedMapSprite.y1Offset = (originalOffsetPosition.y);
        }
        else if(location == TWO)
        {
            selectedMapSprite.x2Offset = (originalOffsetPosition.x);
            selectedMapSprite.y2Offset = (originalOffsetPosition.y);
        }
        else if(location == THREE)
        {
            selectedMapSprite.x3Offset = (originalOffsetPosition.x);
            selectedMapSprite.y3Offset = (originalOffsetPosition.y);
        }
        else if(location == FOUR)
        {
            selectedMapSprite.x4Offset = (originalOffsetPosition.x);
            selectedMapSprite.y4Offset = (originalOffsetPosition.y);
        }
    }
}
