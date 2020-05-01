package com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.TextFieldAction;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;

public abstract class PropertyField extends Group
{
    protected Table table;
    protected TextButton remove; // Null if removeable is false
    protected boolean removeable;
    protected PropertyMenu menu;
    protected Array<PropertyField> properties; // properties of the object holding this property field. Null if locked properties

    protected static Array<TextFieldAction> textFieldActions = new Array<>();

    public PropertyField(final PropertyMenu menu, Array<PropertyField> properties, boolean removeable)
    {
        this.menu = menu;
        this.properties = properties;
        this.removeable = removeable;
    }

    /** Any changes you make to one removeable property will change all other identical properties in a selection of objects or sprites*/
    protected abstract void addRemoveableListener();

    /** Any changes you make to one locked property will change all other identical properties in a selection of objects or sprites*/
    protected abstract void addLockedListener();

    public abstract boolean equals(PropertyField propertyField);
}
