/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ande.bridexpro.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.bind.JAXB;

/**
 *
 * @author Ande
 */
public class Updater {
    
    private static final Logger log = Logger.getLogger("Updater");
    private static String backupFolderName = "bk-bridexpro";
    
    public static FXManifest syncManifest(FXManifest manifest ,Map<String, String> namedParams) throws IOException{
        
        if (namedParams == null) namedParams = Collections.emptyMap();
        
        String appStr = null;

        if (namedParams.containsKey("app")) {
            // get --app-param
            appStr = namedParams.get("app");
            log.info(String.format("Loading manifest from 'app' parameter supplied: %s", appStr));
        }

        if (namedParams.containsKey("uri")) {
            // get --uri-param
            String uriStr  = namedParams.get("uri");
            if (! uriStr.endsWith("/")) { uriStr = uriStr + "/"; }
            log.info(String.format("Syncing files from 'uri' parameter supplied:  %s", uriStr));

            URI uri = URI.create(uriStr);
            // load manifest from --app param if supplied, else default file at supplied uri
            URI app = appStr != null ? URI.create(appStr) : uri.resolve("app.xml");
            manifest = FXManifest.load(app);
            // set supplied uri in manifest
            manifest.uri = uri;
            return manifest;
        }

        if (appStr != null) {
            // --uri was not supplied, but --app was, so load manifest from that
            manifest = FXManifest.load(URI.create(appStr));
            return manifest;
        }
                
        try {            
            Path cacheDir = manifest.resolveCacheDir(namedParams);
            Path manifestPath = manifest.getPath(cacheDir);
            
            System.out.println("manifest ts = "+manifest.ts);
            FXManifest remoteManifest = FXManifest.load(manifest.getFXAppURI());

            if (remoteManifest == null) {
                log.info(String.format("No remote manifest at %s", manifest.getFXAppURI()));
            } else if (!remoteManifest.equals(manifest)) {
                // Update to remote manifest if newer or we specifically accept downgrades
                if (remoteManifest.isNewerThan(manifest) || manifest.acceptDowngrade) {
                    // create folder backup and copy all files to backup folder
                    Path backupFolder = cacheDir.getParent().resolve(backupFolderName+"/");
                    if(copyDirectory(cacheDir.toFile(), backupFolder.toFile())){
                        // jika sukses copy folder to backup folder
                        System.out.println("manifest diperbaharui sesuai remote");
                        manifest = remoteManifest;
                        JAXB.marshal(manifest, manifestPath.toFile());
                    }
                }
            }
            System.out.println("remote ts = "+remoteManifest.ts);
        } catch (Exception ex) {
            log.log(Level.WARNING,
                    String.format("Unable to update manifest from %s", manifest.getFXAppURI()), ex);
        }
        
        return manifest;
    }
    
    public static boolean syncFile(FXManifest manifest, Path cacheDir) throws IOException{
        boolean result = true;
        
        // berisi semua tag lib pada app.xml
        // @needsUpdate memfilter tag lib pada app.xml dengan kunci : 
        // jika, os(xmlAtribut) = OS.pada devicenya, karena jika OS nya ga sama berarti bukan update untuk OS tsb
        // dan 
        // needsUpdate dg kriteria, (file tidak ada) OR (size(xmlAtribut) = size.FilePadaDevice) OR (checksum(xmlAtribut)=checksum.FilePadaDevice)
        List<LibraryFile> needsUpdate = manifest.files.stream()
                .filter(LibraryFile::loadForCurrentPlatform)
                .filter(it -> it.needsUpdate(cacheDir))
                .collect(Collectors.toList());
        
        Long totalBytes = needsUpdate.stream().mapToLong(f -> f.size).sum();
        Long totalWritten = 0L;

        for (LibraryFile lib : needsUpdate) {
            Path target = cacheDir.resolve(lib.file).toAbsolutePath();
            Files.createDirectories(target.getParent());

            URI uri = manifest.uri.resolve(lib.file); // alamat file yang didownload
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            if (uri.getUserInfo() != null) {
                byte[] payload = uri.getUserInfo().getBytes(StandardCharsets.UTF_8);
                String encoded = Base64.getEncoder().encodeToString(payload);
                connection.setRequestProperty("Authorization", String.format("Basic %s", encoded));
            }
            try (InputStream input = connection.getInputStream();
                 OutputStream output = Files.newOutputStream(target)) {

                byte[] buf = new byte[65536];

                int read;
                while ((read = input.read(buf)) > -1) {
                    output.write(buf, 0, read);
                    totalWritten += read;
                    Double progress = totalWritten.doubleValue() / totalBytes.doubleValue() * 0.8;
                    //Platform.runLater(() -> uiProvider.updateProgress(progress)); untuk update progressbar
                }
            }catch(Exception e){
                log.log(Level.WARNING,
                    String.format("Failed to syncFile from %s", manifest.getFXAppURI()), e);
                result = false;
            }
        }
        
        return result;
    }
    
    public static void reserveBackUp(Path cacheDir, Path backupDir){
        // delete all files in cacheDir
        for(File file : cacheDir.toFile().listFiles()) file.delete();
        
        // copy all file from backup folder to cacheDir
        copyDirectory(backupDir.toFile(), cacheDir.toFile());
        
        // delete backup folder
        backupDir.toFile().delete();
    }
    
    private static boolean copyDirectory(File sourceLocation, File targetLocation){
        boolean result = true;
        
        if(sourceLocation.isDirectory()){
            if(!targetLocation.exists()){
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (String x : children) {
                copyDirectory(new File(sourceLocation, x), new File(targetLocation, x));
            }
        }
        else{
            OutputStream out;
            try (InputStream in = new FileInputStream(sourceLocation)) {
                out = new FileOutputStream(targetLocation);
                //copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while((len=in.read(buf))>0){
                    out.write(buf, 0, len);
                }
                out.close();
            }catch(IOException e){
                result = false;
            }
        }
        
        return result;
    }
}
