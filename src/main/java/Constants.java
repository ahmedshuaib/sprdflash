public class Constants {
    public static final byte HDLC_HEADER = (byte) 0x7E;

    /* Link Control */
    public static final byte BSL_CMD_CONNECT = (byte) 0x00;

    /* Data Download */
    public static final byte BSL_CMD_START_DATA = (byte) 0x01; // the start flag of the data downloading
    public static final byte BSL_CMD_MIDST_DATA = (byte) 0x02; // the midst flag of the data downloading
    public static final byte BSL_CMD_END_DATA = (byte) 0x03;   // the end flag of the data downloading
    public static final byte BSL_CMD_EXEC_DATA = (byte) 0x04;  // Execute from a certain address
    /* End of Data Download command*/

    public static final byte BSL_CMD_CHECK_BAUD = (byte) 0x7E;     // CheckBaud command,for internal use
    public static final byte BSL_CMD_END_PROCESS = (byte) 0x7F;    // End flash process

    public static final byte BSL_REP_ACK = (byte) 0x80; // The operation acknowledge
    public static final byte BSL_REP_VER = (byte) 0x81;
}
