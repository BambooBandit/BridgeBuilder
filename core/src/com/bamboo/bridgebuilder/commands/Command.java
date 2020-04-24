package com.bamboo.bridgebuilder.commands;

/** Command pattern used for undo/redo. */
public interface Command
{
    void execute();
    void undo();
}
