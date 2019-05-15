package cn.ntboy.mhttpd.startup;

import cn.ntboy.Mhttpd;
import cn.ntboy.mhttpd.util.file.ConfigurationSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MhttpdBaseConfigurationSource implements ConfigurationSource{

    private final String serverXmlPath;
    private final File mhttpdBaseFile;
    private final URI mhttpdBaseUri;

    public MhttpdBaseConfigurationSource(File mhttpdBaseFile, String serverXmlPath) {
        this.mhttpdBaseFile = mhttpdBaseFile;
        mhttpdBaseUri = mhttpdBaseFile.toURI();
        this.serverXmlPath = serverXmlPath;
    }

    @Override
    public Resource getServerXml() throws IOException {
        IOException ioe = null;
        Resource result = null;
        try {
            if (serverXmlPath == null || serverXmlPath.equals(Mhttpd.SERVER_XML)) {
                result = ConfigurationSource.super.getServerXml();
            } else {
                result = getResource(serverXmlPath);
            }
        } catch (IOException e) {
            ioe = e;
        }
        if (result == null) {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("server-embed.xml");
            if (stream != null) {
                try {
                    result = new Resource(stream, getClass().getClassLoader().getResource("server-embed.xml").toURI());
                } catch (URISyntaxException e) {
                    stream.close();
                }
            }
        }

        if (result == null && ioe != null) {
            throw ioe;
        } else {
            return result;
        }
    }

    @Override
    public Resource getResource(String name) throws IOException {

        File f = new File(name);
        if (!f.isAbsolute()) {
            f = new File(mhttpdBaseFile, name);
        }
        if (f.isFile()) {
            return new Resource(new FileInputStream(f), f.toURI());
        }

        InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
        if (stream != null) {
            try {
                return new Resource(stream, getClass().getClassLoader().getResource(name).toURI());
            } catch (URISyntaxException e) {
                stream.close();
                throw new IOException(e);
            }
        }

        URI uri = getURI(name);

        try {
            URL url = uri.toURL();
            return new Resource(url.openConnection().getInputStream(), uri);
        } catch (MalformedURLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public URI getURI(String name) {

        return null;
    }
}
