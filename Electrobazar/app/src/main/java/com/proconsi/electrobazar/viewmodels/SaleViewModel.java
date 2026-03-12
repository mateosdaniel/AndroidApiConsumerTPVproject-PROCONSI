package com.proconsi.electrobazar.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.TicketLine;

import java.math.BigDecimal;
import com.proconsi.electrobazar.models.SaleWithTaxRequest;
import com.proconsi.electrobazar.models.SaleWithTaxResponse;
import com.proconsi.electrobazar.repositories.SaleRepository;

import java.util.ArrayList;
import java.util.List;

public class SaleViewModel extends ViewModel {
    private final SaleRepository saleRepository = new SaleRepository();
    private final MutableLiveData<List<TicketLine>> ticketLines = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> totalItems = new MutableLiveData<>(0);
    private final MutableLiveData<BigDecimal> totalAmount = new MutableLiveData<>(BigDecimal.ZERO);
    private final MutableLiveData<Boolean> ticketVisible = new MutableLiveData<>(false);
    private final MutableLiveData<SaleWithTaxResponse> saleResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isProcessing = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<TicketLine>> getTicketLines() {
        return ticketLines;
    }

    public LiveData<Integer> getTotalItems() {
        return totalItems;
    }

    public LiveData<BigDecimal> getTotalAmount() {
        return totalAmount;
    }

    public LiveData<Boolean> isTicketVisible() {
        return ticketVisible;
    }

    public void setTicketVisible(boolean visible) {
        ticketVisible.setValue(visible);
    }

    public void toggleTicket() {
        Boolean current = ticketVisible.getValue();
        ticketVisible.setValue(current == null || !current);
    }

    public LiveData<SaleWithTaxResponse> getSaleResponse() {
        return saleResponse;
    }

    public LiveData<Boolean> getIsProcessing() {
        return isProcessing;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void confirmSale(SaleWithTaxRequest request) {
        if (Boolean.TRUE.equals(isProcessing.getValue())) return;

        isProcessing.setValue(true);
        errorMessage.setValue(null);
        saleResponse.setValue(null);

        saleRepository.createSale(request).observeForever(response -> {
            isProcessing.setValue(false);
            if (response != null) {
                saleResponse.setValue(response);
                clearTicket();
                setTicketVisible(false);
            } else {
                errorMessage.setValue("Error al procesar la venta. Por favor, inténtelo de nuevo.");
            }
        });
    }

    public void addProduct(Product product) {
        List<TicketLine> currentLines = new ArrayList<>(ticketLines.getValue());
        boolean found = false;

        for (TicketLine line : currentLines) {
            if (line.getProduct().getId().equals(product.getId())) {
                line.setQuantity(line.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            currentLines.add(new TicketLine(product, 1));
        }

        updateTicket(currentLines);
    }

    public void removeProduct(Product product) {
        List<TicketLine> currentLines = new ArrayList<>(ticketLines.getValue());
        TicketLine toRemove = null;

        for (TicketLine line : currentLines) {
            if (line.getProduct().getId().equals(product.getId())) {
                line.setQuantity(line.getQuantity() - 1);
                if (line.getQuantity() <= 0) {
                    toRemove = line;
                }
                break;
            }
        }

        if (toRemove != null) {
            currentLines.remove(toRemove);
        }

        updateTicket(currentLines);
    }
    
    public void deleteLine(TicketLine line) {
        List<TicketLine> currentLines = new ArrayList<>(ticketLines.getValue());
        currentLines.removeIf(l -> l.getProduct().getId().equals(line.getProduct().getId()));
        updateTicket(currentLines);
    }

    public void clearTicket() {
        updateTicket(new ArrayList<>());
    }

    private void updateTicket(List<TicketLine> lines) {
        ticketLines.setValue(lines);

        int items = 0;
        BigDecimal amount = BigDecimal.ZERO;

        for (TicketLine line : lines) {
            items += line.getQuantity();
            amount = amount.add(line.getLineTotal());
        }

        totalItems.setValue(items);
        totalAmount.setValue(amount);
    }
}
