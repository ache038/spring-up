package io.spring.up.epic.net;

public interface IPFilter {
    String IPv6KeyWord = ":";

    boolean accept(String ipAddress);
}
