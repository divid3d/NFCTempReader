package com.example.divided.nfcreader.model;

public class WriteCommand {
    private byte addr;
    private byte commandA;
    private byte commandB;

    public WriteCommand(byte addr, byte commandA, byte commandB) {
        this.addr = addr;
        this.commandA = commandA;
        this.commandB = commandB;
    }

    public byte getAddr() {
        return this.addr;
    }

    public byte getCommandA() {
        return this.commandA;
    }

    public byte getCommandB() {
        return this.commandB;
    }

    public byte[] getCommand() {
        return new byte[]{this.commandA, this.commandB};
    }
}
