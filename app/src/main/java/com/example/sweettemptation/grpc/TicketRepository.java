package com.example.sweettemptation.grpc;

import android.content.Context;
import android.net.Uri;

public class TicketRepository {

    private TicketGrpcService ticketGrpcService;

    public interface Callback {
        void onSuccess(Uri uri);

        void onSuccess(io.grpc.Uri uri);

        void onError(Throwable error);
    }

    public void init(Context context) {
        if (ticketGrpcService == null) {
            ticketGrpcService = new TicketGrpcService(
                    context.getApplicationContext(),
                    false
            );
        }
    }



    public void descargarTicket(int idPedido, Callback callback) {
        ticketGrpcService.descargarTicket(idPedido, new TicketGrpcService.Callback() {
            @Override
            public void onSuccess(Uri rutaArchivo) {
                callback.onSuccess(rutaArchivo);
            }

            @Override
            public void onError(Throwable error) {
                callback.onError(error);
            }
        });
    }

    public void close() {
        ticketGrpcService.close();
    }
}
