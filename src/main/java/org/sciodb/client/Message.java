package org.sciodb.client;

import org.sciodb.utils.models.Command;

/**
 * @author jesus.navarrete  (06/03/16)
 */
public class Message {

    private String operation;
    private Command command;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
