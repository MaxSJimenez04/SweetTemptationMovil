package com.example.sweettemptation.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.PedidoDTO;
import com.example.sweettemptation.dto.ProductoDTO;
import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.PedidoApi;
import com.example.sweettemptation.interfaces.ProductoPedidoApi;
import com.example.sweettemptation.model.Pedido;
import com.example.sweettemptation.network.ApiCliente;
import com.example.sweettemptation.servicios.PedidoService;
import com.example.sweettemptation.servicios.ProductoPedidoService;
import com.example.sweettemptation.utils.UserSession;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.math.BigDecimal;
import java.util.Locale;

public class DetallePedidoBottomSheet extends BottomSheetDialogFragment {

    private ProductoDTO producto;
    private Bitmap imagenBitmap;
    private int cantidad = 1;
    private int idCliente;
    private static PedidoService pedidoService;
    private static ProductoPedidoService productoPedidoService;
    private TextView txtCantidad, txtTotal, txtNombre, txtDescripcion;
    private ImageView imgProducto;
    private Button btnMas, btnMenos, btnAgregar;

    public static DetallePedidoBottomSheet newInstance(ProductoDTO producto, Bitmap imagen) {
        PedidoApi pedidoApi = ApiCliente.getInstance().retrofit().create(PedidoApi.class);
        pedidoService = new PedidoService(pedidoApi);
        ProductoPedidoApi productoPedidoApi = ApiCliente.getInstance().retrofit().create(ProductoPedidoApi.class);
        productoPedidoService = new ProductoPedidoService(productoPedidoApi);
        DetallePedidoBottomSheet fragment = new DetallePedidoBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("producto", producto);
        fragment.imagenBitmap = imagen;
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_detalle_pedido_modal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            producto = (ProductoDTO) getArguments().getSerializable("producto");
        }

        imgProducto = view.findViewById(R.id.imgModalProducto);
        txtNombre = view.findViewById(R.id.txtModalNombre);
        txtDescripcion = view.findViewById(R.id.txtModalDescripcion);
        txtCantidad = view.findViewById(R.id.txtModalCantidad);
        txtTotal = view.findViewById(R.id.txtModalPrecioTotal);
        btnMas = view.findViewById(R.id.btnModalMas);
        btnMenos = view.findViewById(R.id.btnModalMenos);
        btnAgregar = view.findViewById(R.id.btnModalAgregarConfirmar);

        if (producto != null) {
            txtNombre.setText(producto.getNombre());
            txtDescripcion.setText(producto.getDescripcion());

            if (imagenBitmap != null) {
                imgProducto.setImageBitmap(imagenBitmap);
            } else {
                imgProducto.setImageResource(R.drawable.ic_launcher_background);
            }

            actualizarCalculos();
        }

        btnMas.setOnClickListener(v -> {
            if (cantidad < producto.getUnidades()) {
                cantidad++;
                actualizarCalculos();
            } else {
                Toast.makeText(getContext(), "Solo quedan " + producto.getUnidades() + " disponibles", Toast.LENGTH_SHORT).show();
            }
        });

        btnMenos.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                actualizarCalculos();
            }
        });

        btnAgregar.setOnClickListener(v -> {
            idCliente = UserSession.getUserId(requireContext());
            pedidoService.obtenerPedidoActual(idCliente, result -> {
                String mensaje;
                if (result == null) {
                    mensaje = "Pedido no encontrado";
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (result.codigo == 200) {
                    if (result.datos == null) {
                        mensaje = "No se encontró pedido actual";
                        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    } else {
                        PedidoDTO dto = result.datos;
                        Pedido p = new Pedido(dto.getId(), dto.getFechaCompra(), dto.getActual(),
                                dto.getTotal(), dto.getEstado(), dto.getPersonalizado(), dto.getIdCliente());
                        productoPedidoService.crearProducto(producto.getId(), p.getId(), cantidad, respuesta -> {
                            if (respuesta.codigo == 200){
                                String confirmacion = "Agregado: " + cantidad + " " + producto.getNombre();
                                Toast.makeText(getContext(), confirmacion, Toast.LENGTH_SHORT).show();
                                dismiss();
                            }else {
                                Toast.makeText(getContext(), "Hubo un problema: " + respuesta.mensaje, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        });
    }
    private void actualizarCalculos() {
        txtCantidad.setText(String.valueOf(cantidad));
        BigDecimal total = producto.getPrecio().multiply(new BigDecimal(cantidad));
        txtTotal.setText(String.format(Locale.getDefault(), "$%.2f", total));
    }
}
