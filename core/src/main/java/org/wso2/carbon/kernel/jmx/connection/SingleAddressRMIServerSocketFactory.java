package org.wso2.carbon.kernel.jmx.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

/**
 * SingleAddressRMIServerSocketFactory
 */
public class SingleAddressRMIServerSocketFactory implements java.rmi.server.RMIServerSocketFactory {
    private final java.net.InetAddress inetAddress;

    public SingleAddressRMIServerSocketFactory(java.net.InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    @Override
    public java.net.ServerSocket createServerSocket(int port) throws java.io.IOException {
        return new java.net.ServerSocket(port, 0, inetAddress);
    }
}
