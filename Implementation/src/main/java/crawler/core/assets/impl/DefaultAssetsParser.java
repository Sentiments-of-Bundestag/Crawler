package crawler.core.assets.impl;

import crawler.core.assets.AssetResponse;
import crawler.core.assets.AssetsParser;
import models.Person.Person;
import models.Protokoll;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DefaultAssetsParser implements AssetsParser {

    public DefaultAssetsParser() {}

    @Override
    public Set<Protokoll> getProtokolls(Set<AssetResponse> assetResponses, Set<Person> stammdaten, boolean deleteAfterParsing) {
        Set<Protokoll> protokolls = new LinkedHashSet<>();

        for (AssetResponse assetResponse : assetResponses) {
            if (assetResponse.getAssetPath() != null) {
                if (assetResponse.getAssetPath().endsWith("xml") && assetResponse.getAssetPath().contains("data")) {
                    protokolls.add(getProtokoll(assetResponse.getAssetPath()));
                } else if (assetResponse.getAssetPath().endsWith("zip") && assetResponse.getAssetPath().contains("data") && assetResponse.getAssetPath().contains("pp")) {
                    Set<Protokoll> wahlPeriodeProtokolls = getWahlPeriodeProtokolls(assetResponse.getAssetPath());
                    protokolls.addAll(wahlPeriodeProtokolls);
                }
            }
        }

        // clean downloaded assets after parsing them
        if (deleteAfterParsing) {
            cleanDownloadedAssets(assetResponses);
        }

        return protokolls;
    }

    @Override
    public Set<Person> getStammdaten(AssetResponse assetResponse, boolean deleteAfterParsing) {
        Set<Person> stammdaten = getStammdatenFromZipFile(assetResponse.getAssetPath());

        // delete asset after parsing
        if (deleteAfterParsing) {
            delete(assetResponse.getAssetPath());
        }

        return stammdaten;
    }

    /**
     * Get list of persons (stammdaten) from file (zip)
     *
     * @param filename fileName path of the file (zip)
     * @return list of persons
     */
    private static Set<Person> getStammdatenFromZipFile(String filename) {
        Set<Person> stammdaten = new LinkedHashSet<>();

        // Unzip Stammdaten file
        String[] filenameDestParts = filename.split(".");
        String destinationDir = filenameDestParts.length == 2 ? filenameDestParts[0] : null;
        if (destinationDir != null && "zip".equals(filenameDestParts[1])) {
            unzip(filename, destinationDir);
            final FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("N/A", "xml");
            final File file = new File(destinationDir);
            for (final File child : Objects.requireNonNull(file.listFiles())) {
                if(extensionFilter.accept(child)) {
                    stammdaten.addAll(getStammdaten(child.getPath()));
                }
            }
        }

        return stammdaten;
    }

    /**
     * Get list of persons (stammdaten) from file (xml)
     *
     * @param filename fileName path of the file (xml)
     * @return list of persons
     */
    private static Set<Person> getStammdaten(String filename) {
        Set<Person> stammdaten = new LinkedHashSet<>();
        // Need to pe implemented (@Marlon)

        return stammdaten;
    }

    /**
     * Parse a protokoll from file
     * @param filename path of the file
     * @return list of protokolls
     */
    private static Protokoll getProtokoll(String filename) {
        Protokoll protokoll = new Protokoll();
        // Need to pe implemented (@Marlon)

        return protokoll;
    }

    /**
     * Get list of protokolls for an entire wahlperiode (*.zip)
     * @param filename path of the file
     * @return list of protokolls
     */
    private static Set<Protokoll> getWahlPeriodeProtokolls(String filename) {
        Set<Protokoll> protokolls = new LinkedHashSet<>();

        // Unzip the file (wahlperiode should always be a zip file)
        String[] filenameDestParts = filename.split("\\.");
        String destinationDir = filenameDestParts.length == 2 ? filenameDestParts[0] : null;
        if (destinationDir != null && "zip".equals(filenameDestParts[1])) {
            unzip(filename, destinationDir);
            final FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("N/A", "xml");
            final File file = new File(destinationDir);
            for (final File child : Objects.requireNonNull(file.listFiles())) {
                if(extensionFilter.accept(child)) {
                    protokolls.add(getProtokoll(child.getPath()));
                }
            }
        }

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
     * clean downloaded assets
     * @param assetResponses asset list
     */
    private static void cleanDownloadedAssets(Set<AssetResponse> assetResponses) {
        for (AssetResponse assetResponse : assetResponses) {
            delete(assetResponse.getAssetPath());
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