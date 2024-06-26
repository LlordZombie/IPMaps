package iputils;

import java.util.Objects;

public class IPAddress implements Comparable<IPAddress> {

    /**
     * ip as integer
     */
    private int ip;

    /**
     * create IP from 4 Numbers
     *
     * @param a3 octet 3
     * @param a2 octet 2
     * @param a1 octet 1
     * @param a0 octet 0
     */
    public IPAddress(int a3, int a2, int a1, int a0) {
        createIP(a3, a2, a1, a0);
    }

    /**
     * create IP from given integer (internal use)
     *
     * @param ip ip as integer
     */
    public IPAddress(int ip) {
        this.ip = ip;
    }

    public IPAddress(String ip) {
        String[] nums = ip.split("\\.");
        if (nums.length != 4) {
            throw new IllegalArgumentException("ill formed ip");
        }
        createIP(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]), Integer.parseInt(nums[2]), Integer.parseInt(nums[3]));
    }

    /**
     * create IP/Netmask with given number of bits
     *
     * @param cidr number of bits
     * @return netmask
     */
    public static IPAddress createNetmask(int cidr) {
        int mask = (int) (0xffffffff00000000L >> cidr);
        // System.out.format("cm %2d: %08x\n", cidr, mask);
        return new IPAddress(mask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPAddress ipAddress = (IPAddress) o;
        return ip == ipAddress.ip;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ip);
    }

    private void createIP(int a3, int a2, int a1, int a0) {
        this.ip = (a3 << 24) + (a2 << 16) + (a1 << 8) + a0;
        // System.out.format("%d.%d.%d.%d: %08x\n", a3,a2,a1,a0,ip);
    }

    /**
     * @return ip address as integer
     */
    public int getIP() {
        return ip;
    }

    @Override
    public String toString() {
        int a0 = (ip) & 0xff;
        int a1 = (ip >>> 8) & 0xff;
        int a2 = (ip >>> 16) & 0xff;
        int a3 = (ip >>> 24) & 0xff;

        return "IPAddress [" + a3 + "." + a2 + "." + a1 + "." + a0 + "]";
    }

    @Override
    public int compareTo(IPAddress o) {
        return this.ip - o.ip;
    }
}
