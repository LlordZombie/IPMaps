package iputils;

import java.util.Comparator;

public class MySubnetComparator implements Comparator<Subnet> {
    @Override
    public int compare(Subnet o1, Subnet o2) {
        return Long.compare(Integer.toUnsignedLong(o1.getNet().getIP()), Integer.toUnsignedLong(o2.getNet().getIP()));
    }
}
