package com.example.sweettemptation.servicios;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.sweettemptation.utils.Constantes;
import com.sweettemptation.grpc.TicketRequest;
import com.sweettemptation.grpc.TicketResponse;
import com.sweettemptation.grpc.TicketServiceGrpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.okhttp.OkHttpChannelBuilder;


public class TicketGrpcService {
    public interface Callback{
        void onSuccess(Uri rutaArchivo);
        void onError(Throwable error);
    }

    private final Context appContext;
    private final ManagedChannel canal;
    private final TicketServiceGrpc.TicketServiceBlockingStub stub;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TicketGrpcService(Context context, boolean useTls){
        this.appContext = context.getApplicationContext();
        OkHttpChannelBuilder builder = OkHttpChannelBuilder.forAddress(Constantes.URL_GRPC, Constantes.PUERTO_GRPC);
        if (useTls){
            builder.useTransportSecurity();
        }else {
            builder.usePlaintext();
        }

        this.canal = builder.build();
        this.stub = TicketServiceGrpc.newBlockingStub(canal);
    }

    public void descargarTicket(int idPedido, Callback callback) {
        executor.execute(() -> {
            try {
                TicketRequest request = TicketRequest.newBuilder()
                        .setIdPedido(idPedido)
                        .build();

                TicketResponse response = stub
                        .withDeadlineAfter(20, TimeUnit.SECONDS)
                        .generarTicket(request);

                byte[] pdfBytes = response.getPdf().toByteArray();

                String nombreArchivo = response.getFileName();
                if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
                    nombreArchivo = "ticket_pedido" + idPedido + ".pdf";
                }
                nombreArchivo = validarExtension(validarNombreArchivo(nombreArchivo));

                Uri uri = guardarEnDescargas(appContext, nombreArchivo, pdfBytes);
                if (uri == null) throw new RuntimeException("No se pudo guardar el PDF en Descargas");

                callback.onSuccess(uri);

            } catch (StatusRuntimeException grpcEx) {
                callback.onError(grpcEx);
            } catch (Exception ex) {
                callback.onError(ex);
            }
        });
    }

    public void close() {
        try {
            canal.shutdownNow();
        } catch (Exception ignored) {}
        executor.shutdownNow();
    }

    private static Uri guardarEnDescargas(Context context, String displayName, byte[] bytes) throws Exception {
        final String mime = "application/pdf";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, displayName);
            values.put(MediaStore.Downloads.MIME_TYPE, mime);
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            Uri itemUri = resolver.insert(collection, values);
            if (itemUri == null) return null;

            try (OutputStream out = resolver.openOutputStream(itemUri, "w")) {
                if (out == null) throw new RuntimeException("No se pudo abrir OutputStream");
                out.write(bytes);
                out.flush();
            }

            values.clear();
            values.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(itemUri, values, null, null);

            return itemUri;

        } else {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, displayName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
                fos.flush();
            }
            return Uri.fromFile(file);
        }
    }

    private static String validarNombreArchivo(String nombreArchivo) {
        return nombreArchivo.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    private static String validarExtension(String nombreArchivo) {
        String lower = nombreArchivo.toLowerCase();
        return lower.endsWith(".pdf") ? nombreArchivo : (nombreArchivo + ".pdf");
    }
}


