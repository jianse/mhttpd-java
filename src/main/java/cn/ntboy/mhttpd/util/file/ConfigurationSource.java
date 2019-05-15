package cn.ntboy.mhttpd.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface ConfigurationSource{

    public default Resource getServerXml() throws IOException {
        return getConfResource("server.xml");
    }

    public default Resource getConfResource(String name)
            throws IOException {
        String fullName = "conf/" + name;
        return getResource(fullName);
    }

    public Resource getResource(String name) throws IOException;

    public URI getURI(String name);

    public class Resource implements AutoCloseable{
        private final InputStream inputStream;
        private final URI uri;

        public Resource(InputStream inputStream, URI uri) {
            this.inputStream = inputStream;
            this.uri = uri;
        }

        @Override
        public void close() throws Exception {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public URI getUri() {
            return uri;
        }

        public long getLastModified() throws IOException {
            return uri.toURL().openConnection().getLastModified();
        }
    }
}
