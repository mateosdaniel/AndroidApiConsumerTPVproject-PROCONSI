package com.proconsi.electrobazar.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class PdfUtils {
    private static final String TAG = "PdfUtils";

    public static void saveAndOpenFile(Context context, ResponseBody body, String filename) {
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) break;
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();

                // Open File
                Uri fileUri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".provider",
                        file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                
                Intent chooser = Intent.createChooser(intent, "Abrir Documento");
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooser);

            } catch (IOException e) {
                Toast.makeText(context, "Error al guardar archivo", Toast.LENGTH_SHORT).show();
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "saveAndOpenFile: " + e.getMessage(), e);
        }
    }
}
