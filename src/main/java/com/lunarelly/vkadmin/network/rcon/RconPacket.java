package com.lunarelly.vkadmin.network.rcon;

import lombok.Getter;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
public final class RconPacket {
    public static final int EXECUTE_COMMAND = 2;
    public static final int AUTHENTICATION = 3;

    private final int requestId;
    private final byte[] payload;

    private RconPacket(int requestId, byte[] payload) {
        this.requestId = requestId;
        this.payload = payload;
    }

    public static RconPacket send(Rcon rcon, int type, byte[] payload) throws IOException {
        try {
            RconPacket.write(rcon.getSocket().getOutputStream(), rcon.getRequestId(), type, payload);
        } catch (SocketException e) {
            rcon.getSocket().close();
            throw e;
        }
        return RconPacket.read(rcon.getSocket().getInputStream());
    }

    private static void write(OutputStream out, int requestId, int type, byte[] payload) throws IOException {
        int bodyLength = RconPacket.getBodyLength(payload.length);
        int packetLength = RconPacket.getPacketLength(bodyLength);
        ByteBuffer buffer = ByteBuffer.allocate(packetLength);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(bodyLength);
        buffer.putInt(requestId);
        buffer.putInt(type);
        buffer.put(payload);
        buffer.put((byte)0);
        buffer.put((byte)0);

        out.write(buffer.array());
        out.flush();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static RconPacket read(InputStream in) throws IOException {
        byte[] header = new byte[4 * 3];
        in.read(header);
        try {
            ByteBuffer buffer = ByteBuffer.wrap(header);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            int length = buffer.getInt();
            int requestId = buffer.getInt();
            buffer.getInt();
            byte[] payload = new byte[length - 4 - 4 - 2];
            DataInputStream inputStream = new DataInputStream(in);
            inputStream.readFully(payload);
            inputStream.read(new byte[2]);
            return new RconPacket(requestId, payload);
        } catch (BufferUnderflowException | EOFException e) {
            throw new IOException("Can't read the packet");
        }
    }

    private static int getPacketLength(int bodyLength) {
        return 4 + bodyLength;
    }

    private static int getBodyLength(int payloadLength) {
        return 4 + 4 + payloadLength + 2;
    }
}
