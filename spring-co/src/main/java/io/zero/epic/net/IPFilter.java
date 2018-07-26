package io.zero.epic.net;

public interface IPFilter {
    String IPv6KeyWord = ":";

    boolean accept(String ipAddress);
}
