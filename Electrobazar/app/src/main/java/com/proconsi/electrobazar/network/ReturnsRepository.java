package com.proconsi.electrobazar.network;

import com.proconsi.electrobazar.models.ReturnCheckResponse;
import com.proconsi.electrobazar.models.ReturnRequest;
import com.proconsi.electrobazar.models.SaleReturn;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ReturnsRepository {
    private final ApiService apiService;

    public ReturnsRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<ReturnCheckResponse> checkReturn(String query) {
        return apiService.checkReturn(query);
    }

    public Call<SaleReturn> processReturn(ReturnRequest request) {
        return apiService.processReturn(request);
    }

    public Call<List<SaleReturn>> getReturns(String from, String to) {
        return apiService.getReturns(from, to);
    }

    public Call<SaleReturn> getReturnById(Long id) {
        return apiService.getReturnById(id);
    }

    public Call<ResponseBody> downloadReturnPdf(Long id) {
        return apiService.downloadReturn(id);
    }
}
