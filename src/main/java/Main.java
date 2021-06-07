public class Main {
    public static void main(String[] args) {
        Device device = new Device();
        device.open();
        device.checkBaud();
        device.connect();
    }
}
