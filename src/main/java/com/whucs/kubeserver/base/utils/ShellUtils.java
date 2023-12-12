package com.whucs.kubeserver.base.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import java.io.IOException;

public class ShellUtils {

    public static Connection getConnection(String host, int port, String username, String password) throws Exception {
        Connection conn = new Connection(host, port);
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);
            if (isAuthenticated == false) {
                throw new Exception("Authentication failed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    public static Session getSession(Connection conn) throws Exception {
        Session session = null;
        try {
            session = conn.openSession();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return session;
    }
}
