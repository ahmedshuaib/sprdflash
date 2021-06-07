public class Utils {

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    public static int getUnsignedShort(byte b1, byte b0) {
        return ((b1 & 0xFF) << 8) | (b0 & 0xFF);
    }

    public static int crc(int crc, byte[] data) {
        int i;

        for (byte datum : data) {
            for (i = 0x80; i != 0; i >>>= 1) {
                if ((crc & 0x8000) != 0) {
                    crc <<= 1;
                    crc ^= 0x1021;
                } else {
                    crc <<= 1;
                }

                if ((datum & i) != 0) {
                    crc ^= 0x1021;
                }
            }
        }

        return crc;
    }
}
