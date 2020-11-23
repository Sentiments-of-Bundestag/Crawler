package crawler.core.assets.impl;

import crawler.core.assets.AssetResponse;
import crawler.core.assets.AssetsParser;
import models.Protokoll;
import models.Wahlperiode;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DefaultAssetsParser implements AssetsParser {

    public DefaultAssetsParser() {}

    @Override
    public Protokoll getProtokoll(String protocolVersion, String fileName) {
        Protokoll protokoll = new Protokoll();
        // Need to pe implemented (@Marlon)

        return protokoll;
    }

    @Override
    public Wahlperiode getWahlPeriode(String wahlPeriodeVersion, String fileName) {
        Wahlperiode wahlperiode = new Wahlperiode();
        // Need to pe implemented (@Marlon)

        return wahlperiode;
    }

    @Override
    public Set<Protokoll> getProtokolls(Set<AssetResponse> assetResponses) {
        Set<Protokoll> protokolls = new LinkedHashSet<>();
        // Need to pe implemented (@Marlon)

        return protokolls;
    }

    /**
     * Unzip downloaded files to a destination directory
     * @param zipFilePath path to the file to unzip
     * @param destDir destination directory
     */
    private static void unzip(String zipFilePath, String destDir) {

        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * delete file or directory by path
     * @param filePath file path
     * @return status code
     */
    private static int delete (String filePath) {
        File directory = new File(filePath);
        try {
            deleteRecursive(directory);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * delete recursive a folder content
     * @param file file to be deleted
     * @throws IOException
     */
    private static void deleteRecursive (File file) throws IOException {
        if( file.isDirectory() ){
            for(File f : Objects.requireNonNull(file.listFiles())){
                deleteRecursive(f);
            }
        }

        if(!file.delete()){
            throw new IOException("File cannot be deleted: " + file);
        }
    }
}