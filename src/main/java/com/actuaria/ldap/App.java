package com.actuaria.ldap;

import java.util.Hashtable;

/**
 * Hello world!
 * Run passing the parameters to keystore
 * java -Djavax.net.ssl.keyStore=C:\\Users\\lenin.torres_actuari\\Documents\\certs\\cacerts -Djavax.net.ssl.keyStorePassword=changeit -cp ldap-ldap.jar com.actuaria.ldap.App 
 */
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;

public class App {
    
    public static void main(String[] args) {
        String searchFilter = "(&(objectClass=*))";
        String ldapUrl = "ldaps://ldap.google.com:636"; // LDAPS port
        String baseDn = "dc=actuaria,dc=com";
        try {
            String keyStorePath = System.getProperty("javax.net.ssl.keyStore");
            String trustStorePath = System.getProperty("javax.net.ssl.trustStore");
            System.out.println(System.getProperty("javax.net.ssl.keyStore"));
            System.out.println(System.getProperty("javax.net.ssl.trustStore"));
            if (keyStorePath != null && keyStorePath.isEmpty()) {
                System.out.println("Using default keystore (likely cacerts)");
            } else {
                System.out.println("Using keystore at: " + keyStorePath);
            }
        
            if (trustStorePath != null && trustStorePath.isEmpty()) {
                System.out.println("Using default truststore (likely cacerts)");
            } else {
                System.out.println("Using truststore at: " + trustStorePath);
            }
            
            // Configure environment for secure connection
            Hashtable<String, Object> env = new Hashtable<String, Object>();
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_PROTOCOL, "ssl");
            // Trust Google's LDAP certificate (replace with your trust store if needed)
            //env.put(Context.SSL_SOCKET_FACTORY, "com.sun.xnet.ssl.TrustAllSSLSocketFactory");
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");


            // Initiate connection
            DirContext ctx = new InitialDirContext(env);

            // Search for the user
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search all levels

            NamingEnumeration<SearchResult> results = ctx.search(baseDn, searchFilter, controls);

            if (results.hasMoreElements()) {
                System.out.println("Found entries:");
                while (results.hasMoreElements()) {
                    SearchResult result = results.next();
                    Attributes attrs = result.getAttributes();
                    String objectClass = (String) attrs.get("objectClass").get();
                    String name = result.getName();

                    if (objectClass.equals("inetOrgPerson") || objectClass.equals("posixAccount")) {
                        System.out.println("  - User: " + name);
                    } else if (objectClass.equals("groupOfUniqueNames") || objectClass.equals("posixGroup")) {
                        System.out.println("  - Group: " + name);
                    } else {
                        System.out.println("  - Unknown objectClass: " + name + " (" + objectClass + ")");
                    }
                }
            } else {
                System.out.println("No entries found.");
            }

            // Close connection
            ctx.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
