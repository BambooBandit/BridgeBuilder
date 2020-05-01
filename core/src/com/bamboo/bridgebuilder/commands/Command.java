package com.bamboo.bridgebuilder.commands;

/** Command pattern used for undo/redo. TODO pool them. */
public interface Command
{
    void execute();
    void undo();
}
