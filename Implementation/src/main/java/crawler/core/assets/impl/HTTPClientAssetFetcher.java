package crawler.core.assets.impl;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.google.inject.Inject;
import crawler.core.CrawlerURL;
import crawler.core.assets.AssetFetcher;
import crawler.core.assets.AssetResponse;
import crawler.util.StatusCode;
import org.apache.http.HttpStatus;

import java.io.*;
import java.net.URL;
import java.util.Map;

public class HTTPClientAssetFetcher implements AssetFetcher {

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
            return new AssetResponse(url.getUrl(), url.getReferer(), assetPath, HttpStatus.SC_OK, time, assetSizeInByte * 1024 / 1000000);
        } catch (IOException e){
            return new AssetResponse(url.getUrl(), url.getReferer(), assetPath, StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(), -1, assetSizeInByte);
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

        return byteSize;
    }

    private static int getFileResponse(WebResponse response, String fileName){
        int byteSize = 0;
        InputStream inputStream = null;

        // write the inputStream to a FileOutputStream
        OutputStream outputStream = null;

        try {
            inputStream = response.getContentAsStream();

            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(new File(fileName));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
                byteSize++;
            }
            System.out.println("Done!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return byteSize;
    }
}