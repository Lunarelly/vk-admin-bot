package com.lunarelly.vkadmin.network.rcon;

import lombok.Getter;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public final class Rcon implements AutoCloseable {
    private final Object sync = new Object();
    private final Random random = new Random();

    @Getter
    private int requestId;
    @Getter
    private Socket socket;

    public Rcon(String host, int port, byte[] password) throws IOException {
        this.connect(host, port, password);
    }

    public void connect(String host, int port, byte[] password) throws IOException {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Host can't be null or empty");
        }

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port is out of range");
        }

        synchronized (this.sync) {
            this.requestId = this.random.nextInt();
            this.socket = new Socket(host, port);
        }

        if (this.send(RconPacket.AUTHENTICATION, password).getRequestId() == -1) {
            throw new IOException("Password have been rejected by server");
        }
    }

    public String command(String payload) throws IOException {
        if (payload == null || payload.trim().isEmpty()) {
            throw new IllegalArgumentException("Payload can't be null or empty");
        }
        return new String(this.send(RconPacket.EXECUTE_COMMAND, payload.getBytes()).getPayload(), StandardCharsets.UTF_8);
    }

    private RconPacket send(int type, byte[] payload) throws IOException {
        synchronized (this.sync) {
            return RconPacket.send(this, type, payload);
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this.sync) {
            this.socket.close();
        }
    }
}
