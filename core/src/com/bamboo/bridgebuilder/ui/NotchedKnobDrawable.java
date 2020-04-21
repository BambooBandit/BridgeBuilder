package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class NotchedKnobDrawable extends BaseDrawable
{
    private Drawable knob;
    private Drawable bg;
    private boolean stretchHorizontally;

    public NotchedKnobDrawable() { this.stretchHorizontally = true; }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height)
    {
        float leftWidth = 0;
        float rightWidth = 0;
        float bottomHeight = 0;
        float topHeight = 0;
        
        if (this.bg != null)
        {
            this.bg.draw(batch, x, y, width, height);
            
            leftWidth = this.bg.getLeftWidth();
            rightWidth = this.bg.getRightWidth();
            bottomHeight = this.bg.getBottomHeight();
            topHeight = this.bg.getTopHeight();
        }
        
        if (this.knob != null)
        {
            if (this.stretchHorizontally)
            {
                float knobWidth = width - leftWidth - rightWidth;
                float knobHeight = this.knob.getMinHeight();
                float xOffset = x + leftWidth;
                float yOffset = y + height / 2 - knobHeight / 2;
                this.knob.draw(batch, MathUtils.round(xOffset), MathUtils.round(yOffset), MathUtils.round(knobWidth), MathUtils.round(knobHeight));
            }
            else
            {
                float knobWidth = this.knob.getMinWidth();
                float knobHeight = height - bottomHeight - topHeight;
                float xOffset = x + width / 2 - knobWidth / 2;
                float yOffset = y + bottomHeight;
                    this.knob.draw(batch, MathUtils.round(xOffset), MathUtils.round(yOffset), MathUtils.round(knobWidth), MathUtils.round(knobHeight));
            }
        }
    }

    public Drawable getKnob() { return this.knob; }

    public void setKnob(Drawable knob) { this.knob = knob; }

    public Drawable getBg() { return this.bg; }

    public void setBg(Drawable bg) { this.bg = bg; }

    public boolean isStretchHorizontally() { return this.stretchHorizontally; }

    public void setStretchHorizontally(boolean stretchHorizontally) { this.stretchHorizontally = stretchHorizontally; }
    
    @Override
    public float getMinHeight()
    {
        float returnValue = super.getMinHeight();
        
        if (this.bg != null)
            returnValue = Math.max(returnValue, this.bg.getMinHeight());

        if (this.knob != null)
            returnValue = Math.max(returnValue, this.knob.getMinHeight());

        return returnValue;
    }

    @Override
    public float getMinWidth()
    {
        float returnValue = super.getMinWidth();
        
        if (this.bg != null)
            returnValue = Math.max(returnValue, this.bg.getMinWidth());

        if (this.knob != null)
            returnValue = Math.max(returnValue, this.knob.getMinWidth());

        return returnValue;
    }
}