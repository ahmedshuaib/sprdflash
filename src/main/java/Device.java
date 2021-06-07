import com.fazecast.jSerialComm.SerialPort;
import jdk.jshell.execution.Util;

public class Device {
    private static final int BAUDRATE = 115200;
    private static final int TIMEOUT = 5;

    private SerialPort serialPort;

    public Device() {

    }

    public void open() {
        if (serialPort != null && serialPort.isOpen()) {
            System.out.println("Already connected!");
            return;
        }

        System.out.println("Looking for device...");
        serialPort = null;

        do {
            for (SerialPort sp : SerialPort.getCommPorts())
                if (sp.getDescriptivePortName().contains("SPRD"))
                    serialPort = sp;

            if (serialPort == null)
                Utils.sleep(500);
        } while (serialPort == null);

        System.out.println("Device found!");

        serialPort.openPort();
        serialPort.setBaudRate(BAUDRATE);
        serialPort.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING,
                TIMEOUT, 0);
    }

    public void writePacket(byte cmd) {
        writePacket(cmd, null);
    }

    public void writePacket(byte cmd, byte[] data) {
        if (cmd == Constants.BSL_CMD_CHECK_BAUD) {
            serialPort.writeBytes(new byte[]{cmd}, 1);
            return;
        }

        int length = data != null ? data.length : 0;
        byte[] buf = new byte[8 + length];

        buf[0] = Constants.HDLC_HEADER;
        buf[1] = (byte) (cmd >>> 8);
        buf[2] = (byte) (cmd & 0xFF);
        buf[3] = (byte) (length >>> 8);
        buf[4] = (byte) (length & 0xFF);
        if (length > 0) System.arraycopy(data, 0, buf, 5, length);
        int checksum = 0;
        checksum = Utils.crc(checksum, new byte[]{buf[1], buf[2]});
        checksum = Utils.crc(checksum, new byte[]{buf[3], buf[4]});
        if (length > 0) {
            checksum = Utils.crc(checksum, data);
            if (length % 2 != 0) checksum = Utils.crc(checksum, new byte[]{0});
        }
        buf[buf.length - 3] = (byte) (checksum >> 8);
        buf[buf.length - 2] = (byte) (checksum & 0xFF);
        buf[buf.length - 1] = Constants.HDLC_HEADER;

        serialPort.writeBytes(buf, buf.length);
    }

    public Packet readPacket() {
        Packet packet = new Packet();
        byte[] header = new byte[5];
        byte[] footer = new byte[3];

        serialPort.readBytes(header, header.length);
        assert(header[0] == Constants.HDLC_HEADER);

        packet.type = Utils.getUnsignedShort(header[1], header[2]);
        packet.len = Utils.getUnsignedShort(header[3], header[4]);
        if (packet.len > 0) {
            packet.data = new byte[packet.len];
            serialPort.readBytes(packet.data, packet.len);
        }

        serialPort.readBytes(footer, 3);
        int checksum = Utils.getUnsignedShort(footer[0], footer[1]);
        // TODO: Check checksum
        assert(footer[2] == Constants.HDLC_HEADER);

        return packet;
    }

    public String checkBaud() {
        writePacket(Constants.BSL_CMD_CHECK_BAUD);
        Packet packet = readPacket();
        assert(packet.type == Constants.BSL_REP_VER);
        return new String(packet.data);
    }

    public void connect() {
        writePacket(Constants.BSL_CMD_CONNECT);
        Packet packet = readPacket();
        assert(packet.type == Constants.BSL_REP_ACK);
    }
}
