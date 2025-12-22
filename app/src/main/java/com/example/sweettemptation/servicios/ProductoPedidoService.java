package com.example.sweettemptation.servicios;

import com.example.sweettemptation.interfaces.ApiResult;
import com.example.sweettemptation.interfaces.PedidoApi;
import com.example.sweettemptation.interfaces.ProductoPedidoApi;

public class ProductoPedidoService{
    public interface ResultCallBack<T>{
        void onResult(ApiResult<T> result);
    }

    private final ProductoPedidoApi api;

    public ProductoPedidoService(ProductoPedidoApi api){
        this.api = api;
    }


}
