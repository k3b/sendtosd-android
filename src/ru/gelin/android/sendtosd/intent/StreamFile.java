package ru.gelin.android.sendtosd.intent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

/**
 *  Intent file which reads the content of the file from
 *  the stream from the provided content URI.
 *  This file is not deletable.
 */
public class StreamFile extends IntentFile {

    /** Content resolver to read Uri */
    ContentResolver contentResolver;
    
    /** Uri of the stream */
    Uri uri;
    
    StreamFile(Context context, Intent intent) {
        contentResolver = context.getContentResolver();
        uri = getStreamUri(intent);
    }
    
    StreamFile(Context context, Uri uri) {
        contentResolver = context.getContentResolver();
        this.uri = uri;
    }
    
    /**
     *  Tries to guess the filename from the intent.
     */
    public String getName() {
        String fileName = uri.getLastPathSegment();
        return addExtension(fileName);
    }
    
    /**
     *  Returns false.
     */
    public boolean isDeletable() {
        return false;
    }
    
    /**
     *  Saves the file as the specified file on SD card.
     */
    public void saveAs(File file) throws IOException {
        InputStream in = getStream();
        OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int read;
        while ((read = in.read(buf)) > 0) {
            out.write(buf, 0, read);
        }
        out.close();
        in.close();
    }
    
    /**
     *  Always throws exception.
     */
    public void delete() throws IOException {
        throw new IOException("stream file is not deletable");
    }

    /**
     *  Adds extension to the file name if it hasn't it.
     */
    String addExtension(String fileName) {
        if (fileName.contains(".")) {   //has extension
            return fileName;
        }
        String mimeType = contentResolver.getType(uri);
        if (mimeType != null) {
            String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            if (extension != null) {
                return fileName + "." + extension;
            }
        }
        return fileName;
    }
    
    /**
     *  Returns the file as stream.
     */
    InputStream getStream() throws FileNotFoundException {
        return contentResolver.openInputStream(uri);
    }

}
