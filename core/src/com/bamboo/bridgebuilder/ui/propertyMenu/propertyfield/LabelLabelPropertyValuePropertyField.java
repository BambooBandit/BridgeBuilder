package com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.commands.RemoveProperty;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;

public class LabelLabelPropertyValuePropertyField extends PropertyField
{
    private Label property;
    public Label value;

    public LabelLabelPropertyValuePropertyField(String property, String value, Skin skin, final PropertyMenu menu, Array<PropertyField> properties, boolean removeable)
    {
        super(menu, properties, removeable);

        this.property = new Label(property, skin);
        this.value = new Label(value, skin);

        this.table = new Table();
        this.table.bottom().left();
        this.table.add(this.property);
        this.table.add(this.value);

        if(removeable)
        {
            this.remove = new TextButton("X", skin);
            this.remove.setColor(Color.FIREBRICK);
            final PropertyField removeableField = this;
            this.remove.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    RemoveProperty removeProperty = new RemoveProperty(menu.map, removeableField, properties);
                    menu.map.executeCommand(removeProperty);
                }
            });

            this.table.add(this.remove);
        }

        addActor(this.table);
    }

    public String getProperty()
    {
        return this.property.getText().toString();
    }
    public String getValue()
    {
        return this.value.getText().toString();
    }

    @Override
    public void setSize(float width, float height)
    {
        this.value.setSize(width / 2, height);
        this.table.getCell(this.property).size(width / 2, height);
        if (this.removeable)
            this.table.getCell(this.value).size((width / 2) - height, height);
        else
            this.table.getCell(this.value).size(width / 2, height);
        if(this.removeable)
            this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    @Override
    protected void addRemoveableListener()
    {

    }

    @Override
    protected void addLockedListener()
    {

    }

    @Override
    public boolean equals(PropertyField propertyField)
    {
        if(propertyField instanceof LabelLabelPropertyValuePropertyField)
        {
            LabelLabelPropertyValuePropertyField toCompare = (LabelLabelPropertyValuePropertyField) propertyField;
            return this.property.getText().equals(toCompare.property.getText()) && this.value.getText().equals(toCompare.value.getText());
        }
        return false;
    }
}
