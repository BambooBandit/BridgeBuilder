package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public class RemoveProperty implements Command
{
    private Map map;
    private PropertyField propertyField;
    private Array<PropertyField> properties;
    private int propertyIndex;

    public RemoveProperty(Map map, PropertyField propertyField, Array<PropertyField> properties)
    {
        this.map = map;
        this.propertyField = propertyField;
        this.properties = properties;
        this.propertyIndex = properties.indexOf(propertyField, true);
    }

    @Override
    public void execute()
    {
        this.map.propertyMenu.removeProperty(this.propertyField);
    }

    @Override
    public void undo()
    {
        this.properties.insert(this.propertyIndex, this.propertyField);
        this.map.propertyMenu.rebuild();
    }
}
