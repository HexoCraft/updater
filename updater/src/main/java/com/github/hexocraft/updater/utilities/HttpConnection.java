/**
 *    Copyright 2015-2020 hexosse <hexosse@gmail.com>
 *
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.hexocraft.updater.utilities;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class HttpConnection {

    //
    private static Proxy proxy;


    /**
     * This class cannot be instantiate.
     * Use static function below.
     */
    private HttpConnection() {
    }

    /*
     * Get the proxy used by the JVM
     */
    public static Proxy getProxy() {

        // Return already foud proxy
        if(proxy != null) {
            return proxy;
        }

        // Try to find the first proxy used by the JVM
        try {
            ProxySelector selector = ProxySelector.getDefault();
            List<Proxy> proxyList = selector.select(new URI("http://foo/bar"));

            if(proxyList != null && !proxyList.isEmpty()) {
                proxy = proxyList.get(0);
                return proxy;
            }
        }
        catch(Exception ignored) {
        }
        return null;
    }


    /**
     * Open an {@link HttpURLConnection} based on {@link URL}
     * @param url {@link URL} to open
     * @return {@link HttpURLConnection}
     * @throws IOException if an I/O exception occurs.
     */
    public static HttpURLConnection openConnection(URL url) throws IOException
    {
        return Open(Connection(url));
    }

    /**
     * Create a {@link HttpURLConnection} based on {@link URL}
     * @param url {@link URL} to connect
     * @return {@link HttpURLConnection}
     * @throws IOException if an I/O exception occurs.
     */
    public static HttpURLConnection Connection(URL url) throws IOException
    {
        Proxy proxy = getProxy();
        final HttpURLConnection connection = (HttpURLConnection) (proxy != null ? url.openConnection(proxy) : url.openConnection());
        connection.setRequestProperty("User-Agent", Downloader.USER_AGENT);
        connection.setInstanceFollowRedirects(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(2500);

        return connection;
    }


    /**
     * Open an {@link HttpURLConnection}
     * @param connection {@link HttpURLConnection} to open
     * @return {@link HttpURLConnection}
     * @throws IOException if an error occurred connecting to the server.
     */
    public static HttpURLConnection Open(HttpURLConnection connection) throws IOException
    {
        int status = connection.getResponseCode();
        boolean redirect = status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER;

        if (redirect) {
            String newUrl = connection.getHeaderField("Location");
            return Open(Connection(new URL(newUrl)));
        }

        return connection;
    }
}
