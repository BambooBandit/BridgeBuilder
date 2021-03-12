package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

        float[] spriteVertices = selectedMapSprite.sprite.getVertices();
        selectedMapSprite.offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + selectedMapSprite.map.cameraX + selectedMapSprite.x1Offset - (selectedMapSprite.offsetMovebox1.scale * selectedMapSprite.offsetMovebox1.width / 2f), spriteVertices[SpriteBatch.Y2] + selectedMapSprite.map.cameraY + selectedMapSprite.y1Offset - (selectedMapSprite.offsetMovebox1.scale * selectedMapSprite.offsetMovebox1.height / 2f));
        selectedMapSprite.offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + selectedMapSprite.map.cameraX + selectedMapSprite.x2Offset - (selectedMapSprite.offsetMovebox2.scale * selectedMapSprite.offsetMovebox2.width / 2f), spriteVertices[SpriteBatch.Y3] + selectedMapSprite.map.cameraY + selectedMapSprite.y2Offset - (selectedMapSprite.offsetMovebox2.scale * selectedMapSprite.offsetMovebox2.height / 2f));
        selectedMapSprite.offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + selectedMapSprite.map.cameraX + selectedMapSprite.x3Offset - (selectedMapSprite.offsetMovebox3.scale * selectedMapSprite.offsetMovebox3.width / 2f), spriteVertices[SpriteBatch.Y4] + selectedMapSprite.map.cameraY + selectedMapSprite.y3Offset - (selectedMapSprite.offsetMovebox3.scale * selectedMapSprite.offsetMovebox3.height / 2f));
        selectedMapSprite.offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + selectedMapSprite.map.cameraX + selectedMapSprite.x4Offset - (selectedMapSprite.offsetMovebox4.scale * selectedMapSprite.offsetMovebox4.width / 2f), spriteVertices[SpriteBatch.Y1] + selectedMapSprite.map.cameraY + selectedMapSprite.y4Offset - (selectedMapSprite.offsetMovebox4.scale * selectedMapSprite.offsetMovebox4.height / 2f));
        selectedMapSprite.polygon.setOffset(selectedMapSprite.x1Offset, selectedMapSprite.x2Offset, selectedMapSprite.x3Offset, selectedMapSprite.x4Offset, selectedMapSprite.y1Offset, selectedMapSprite.y2Offset, selectedMapSprite.y3Offset, selectedMapSprite.y4Offset);
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

        float[] spriteVertices = selectedMapSprite.sprite.getVertices();
        selectedMapSprite.offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + selectedMapSprite.map.cameraX + selectedMapSprite.x1Offset - (selectedMapSprite.offsetMovebox1.scale * selectedMapSprite.offsetMovebox1.width / 2f), spriteVertices[SpriteBatch.Y2] + selectedMapSprite.map.cameraY + selectedMapSprite.y1Offset - (selectedMapSprite.offsetMovebox1.scale * selectedMapSprite.offsetMovebox1.height / 2f));
        selectedMapSprite.offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + selectedMapSprite.map.cameraX + selectedMapSprite.x2Offset - (selectedMapSprite.offsetMovebox2.scale * selectedMapSprite.offsetMovebox2.width / 2f), spriteVertices[SpriteBatch.Y3] + selectedMapSprite.map.cameraY + selectedMapSprite.y2Offset - (selectedMapSprite.offsetMovebox2.scale * selectedMapSprite.offsetMovebox2.height / 2f));
        selectedMapSprite.offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + selectedMapSprite.map.cameraX + selectedMapSprite.x3Offset - (selectedMapSprite.offsetMovebox3.scale * selectedMapSprite.offsetMovebox3.width / 2f), spriteVertices[SpriteBatch.Y4] + selectedMapSprite.map.cameraY + selectedMapSprite.y3Offset - (selectedMapSprite.offsetMovebox3.scale * selectedMapSprite.offsetMovebox3.height / 2f));
        selectedMapSprite.offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + selectedMapSprite.map.cameraX + selectedMapSprite.x4Offset - (selectedMapSprite.offsetMovebox4.scale * selectedMapSprite.offsetMovebox4.width / 2f), spriteVertices[SpriteBatch.Y1] + selectedMapSprite.map.cameraY + selectedMapSprite.y4Offset - (selectedMapSprite.offsetMovebox4.scale * selectedMapSprite.offsetMovebox4.height / 2f));
        selectedMapSprite.polygon.setOffset(selectedMapSprite.x1Offset, selectedMapSprite.x2Offset, selectedMapSprite.x3Offset, selectedMapSprite.x4Offset, selectedMapSprite.y1Offset, selectedMapSprite.y2Offset, selectedMapSprite.y3Offset, selectedMapSprite.y4Offset);
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

        float[] spriteVertices = selectedMapSprite.sprite.getVertices();
        selectedMapSprite.offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + selectedMapSprite.map.cameraX + selectedMapSprite.x1Offset - (selectedMapSprite.offsetMovebox1.scale * selectedMapSprite.offsetMovebox1.width / 2f), spriteVertices[SpriteBatch.Y2] + selectedMapSprite.map.cameraY + selectedMapSprite.y1Offset - (selectedMapSprite.offsetMovebox1.scale * selectedMapSprite.offsetMovebox1.height / 2f));
        selectedMapSprite.offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + selectedMapSprite.map.cameraX + selectedMapSprite.x2Offset - (selectedMapSprite.offsetMovebox2.scale * selectedMapSprite.offsetMovebox2.width / 2f), spriteVertices[SpriteBatch.Y3] + selectedMapSprite.map.cameraY + selectedMapSprite.y2Offset - (selectedMapSprite.offsetMovebox2.scale * selectedMapSprite.offsetMovebox2.height / 2f));
        selectedMapSprite.offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + selectedMapSprite.map.cameraX + selectedMapSprite.x3Offset - (selectedMapSprite.offsetMovebox3.scale * selectedMapSprite.offsetMovebox3.width / 2f), spriteVertices[SpriteBatch.Y4] + selectedMapSprite.map.cameraY + selectedMapSprite.y3Offset - (selectedMapSprite.offsetMovebox3.scale * selectedMapSprite.offsetMovebox3.height / 2f));
        selectedMapSprite.offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + selectedMapSprite.map.cameraX + selectedMapSprite.x4Offset - (selectedMapSprite.offsetMovebox4.scale * selectedMapSprite.offsetMovebox4.width / 2f), spriteVertices[SpriteBatch.Y1] + selectedMapSprite.map.cameraY + selectedMapSprite.y4Offset - (selectedMapSprite.offsetMovebox4.scale * selectedMapSprite.offsetMovebox4.height / 2f));
        selectedMapSprite.polygon.setOffset(selectedMapSprite.x1Offset, selectedMapSprite.x2Offset, selectedMapSprite.x3Offset, selectedMapSprite.x4Offset, selectedMapSprite.y1Offset, selectedMapSprite.y2Offset, selectedMapSprite.y3Offset, selectedMapSprite.y4Offset);
    }
}
