package crawler.core.assets.impl;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.inject.Inject;
import crawler.core.CrawlerURL;
import crawler.core.assets.AssetFetcher;
import crawler.core.assets.AssetResponse;
import crawler.utils.StatusCode;
import org.apache.http.HttpStatus;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

public class HTTPClientAssetFetcher implements AssetFetcher {

    private static final String DEFAULT_FILE_DESCRIPTION_DTD = Paths.get("output", "dbtplenarprotokoll-data.dtd").toString();
    private static final String DEFAULT_FILE_DESCRIPTION_DTD_COPY = Paths.get("output", "dbtplenarprotokoll.dtd").toString();
    private final WebClient webClient;
    private int assetSizeInByte;

    @Inject
    public HTTPClientAssetFetcher(WebClient client) {
        webClient = client;
        assetSizeInByte = 0;
    }

    /**
     * Shutdown the client.
     */
    public void shutdown() {
        webClient.close();
    }

    @Override
    public AssetResponse getAsset(CrawlerURL url, Map<String, String> requestHeaders, String assetPath) {
        final long start = System.currentTimeMillis();
        try{
            assetSizeInByte = downloadUsingStream(url.getUrl(), assetPath);
            final long time = System.currentTimeMillis() - start;
            return new AssetResponse(url.getUrl(), url.getTitle(), url.getReferer(), assetPath, HttpStatus.SC_OK, time, assetSizeInByte * 1024 / 1000);
        } catch (IOException e){
            return new AssetResponse(url.getUrl(), url.getTitle(), url.getReferer(), assetPath, StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(), -1, assetSizeInByte);
        }
    }

    private static int downloadUsingStream(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        int byteSize = 0;
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
            byteSize++;
        }
        fis.close();
        bis.close();

        if (file.equals(DEFAULT_FILE_DESCRIPTION_DTD)) {
            copyFileUsingStream(file, DEFAULT_FILE_DESCRIPTION_DTD_COPY);
        }

        return byteSize;
    }

    private static void copyFileUsingStream(String source, String dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            assert is != null;
            is.close();
            assert os != null;
            os.close();
        }
    }
}