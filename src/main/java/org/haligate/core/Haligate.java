package org.haligate.core;

import org.apache.http.impl.client.*;

public class Haligate
{
    public static final String jsonHalContentType = "application/hal+json";

    public static Client defaultClient( )
    {
        final CloseableHttpClient httpClient = HttpClients.createDefault( );
        return new Client( httpClient );
    }
}
