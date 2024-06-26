package iputils;

public class Subnet {

    /**
     * network address
     */
    private IPAddress net;
    /**
     * network mask
     */
    private IPAddress mask;

    /**
     * create netmask from network ip and number of bits
     *
     * @param net  network address
     * @param cidr number of bits
     */
    public Subnet(IPAddress net, int cidr) {
        createMask(net, cidr);
    }

    /**
     * create netmask from ip (four number) and number of bits
     *
     * @param a3 octet 3
     * @param a2 octet 2
     * @param a1 octet 1
     * @param a0 octet 0
     * @param cidr snm in cidr notation
     */
    public Subnet(int a3, int a2, int a1, int a0, int cidr) {
        this(new IPAddress(a3, a2, a1, a0), cidr);
    }

    public Subnet(String mask) {
        String[] parts = mask.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("ill formed subnet");
        }
        IPAddress ip = new IPAddress(parts[0]);
        int cidr = Integer.parseInt(parts[1]);
        createMask(ip, cidr);
    }

    private void createMask(IPAddress net, int cidr) {
        this.net = net;
        this.mask = IPAddress.createNetmask(cidr);

        if ((this.net.getIP() & mask.getIP()) != this.net.getIP()) {
            throw new IllegalArgumentException("bad network");
        }
    }

    public IPAddress getNet() {
        return net;
    }

    public IPAddress getMask() {
        return mask;
    }

    public IPAddress getBroadcast() {
        return new IPAddress(net.getIP() + ~mask.getIP());
    }

    /**
     * is IP in this network
     *
     * @param ip ip address
     * @return true if ip is in this network
     */
    public boolean contains(IPAddress ip) {

        return (ip.getIP() & mask.getIP()) == net.getIP();
    }

    @Override
    public String toString() {
        return "Subnet [net=" + net + ", mask=" + mask + "]";
    }

}
