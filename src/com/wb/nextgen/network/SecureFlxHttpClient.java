package com.wb.nextgen.network;

import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;


public class SecureFlxHttpClient extends DefaultHttpClient {

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        NextGenLogger.d(F.TAG, "SecureFlxHttpClient.createClientConnectionManager");
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), schemeRegistry);
    }
}