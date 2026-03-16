package com.proconsi.electrobazar.network;

import com.proconsi.electrobazar.models.CashCloseInfoDTO;
import com.proconsi.electrobazar.models.CashRegister;
import com.proconsi.electrobazar.models.CashRegisterOpenSuggestion;
import com.proconsi.electrobazar.models.CashWithdrawal;
import com.proconsi.electrobazar.models.CashWithdrawalRequest;

import java.math.BigDecimal;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class CashRegisterRepository {
    private final ApiService apiService;

    public CashRegisterRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<CashRegister> getOpenRegister() {
        return apiService.getOpenRegister();
    }

    public Call<List<CashRegister>> getAllClosedRegisters() {
        return apiService.getAllClosedRegisters();
    }

    public Call<CashCloseInfoDTO> getCashCloseInfo() {
        return apiService.getCashCloseInfo();
    }

    public Call<CashRegisterOpenSuggestion> getOpenSuggestion() {
        return apiService.getOpenSuggestion();
    }

    public Call<CashRegister> openCashRegister(BigDecimal openingBalance, Long workerId) {
        return apiService.openCashRegister(openingBalance, workerId);
    }

    public Call<CashRegister> closeCashRegister(BigDecimal closingBalance, String notes, BigDecimal retainedAmount, Long workerId) {
        return apiService.closeCashRegister(closingBalance, notes, retainedAmount, workerId);
    }

    public Call<CashRegister> getCashRegisterById(Long id) {
        return apiService.getCashRegisterById(id);
    }

    public Call<ResponseBody> downloadCashRegisterTicket(Long id) {
        return apiService.downloadCashRegisterTicket(id);
    }

    public Call<CashWithdrawal> createCashMovement(String amount, String reason, String type) {
        return apiService.createCashMovement(new CashWithdrawalRequest(amount, reason, type));
    }
}
