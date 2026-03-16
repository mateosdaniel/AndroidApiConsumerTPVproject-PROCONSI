package com.proconsi.electrobazar.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.TicketLine;

import java.math.BigDecimal;
import com.proconsi.electrobazar.models.Customer;
import com.proconsi.electrobazar.models.SaleWithTaxRequest;
import com.proconsi.electrobazar.models.SaleWithTaxResponse;
import com.proconsi.electrobazar.repositories.CustomerRepository;
import com.proconsi.electrobazar.repositories.SaleRepository;

import java.util.ArrayList;
import java.util.List;

public class SaleViewModel extends ViewModel {
    private final SaleRepository saleRepository = new SaleRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final com.proconsi.electrobazar.repositories.HeldSalesRepository heldSalesRepository = new com.proconsi.electrobazar.repositories.HeldSalesRepository();
    private final MutableLiveData<List<TicketLine>> ticketLines = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> totalItems = new MutableLiveData<>(0);
    private final MutableLiveData<BigDecimal> totalAmount = new MutableLiveData<>(BigDecimal.ZERO);
    private final MutableLiveData<Boolean> ticketVisible = new MutableLiveData<>(false);
    private final MutableLiveData<SaleWithTaxResponse> saleResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isProcessing = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Customer> selectedCustomer = new MutableLiveData<>(null);

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

    public LiveData<Customer> getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setCustomer(Customer customer) {
        selectedCustomer.setValue(customer);
        updateTicket(ticketLines.getValue());
    }

    public void clearCustomer() {
        selectedCustomer.setValue(null);
        updateTicket(ticketLines.getValue());
    }

    public LiveData<List<Customer>> searchCustomers(String query) {
        return customerRepository.searchCustomers(query);
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

    /** @return false if the product stock would be exceeded, true if added successfully */
    public boolean addProduct(Product product) {
        int stock = product.getStock() != null ? product.getStock() : 0;
        List<TicketLine> currentLines = new ArrayList<>(ticketLines.getValue());
        boolean found = false;

        for (TicketLine line : currentLines) {
            if (line.getProduct().getId().equals(product.getId())) {
                if (line.getQuantity() >= stock) {
                    // Cannot add more than available stock
                    return false;
                }
                line.setQuantity(line.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            if (stock <= 0) return false;
            currentLines.add(new TicketLine(product, 1));
        }

        updateTicket(currentLines);
        return true;
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

    public void holdSale(String label) {
        if (Boolean.TRUE.equals(isProcessing.getValue())) return;

        List<TicketLine> lines = ticketLines.getValue();
        if (lines == null || lines.isEmpty()) return;

        java.util.List<com.proconsi.electrobazar.models.SuspendedSaleLineRequest> lineRequests = new java.util.ArrayList<>();
        for (TicketLine line : lines) {
            com.proconsi.electrobazar.models.SuspendedSaleLineRequest req = new com.proconsi.electrobazar.models.SuspendedSaleLineRequest();
            req.setProductId(line.getProduct().getId());
            req.setQuantity(line.getQuantity());
            req.setUnitPrice(line.getUnitPrice());
            lineRequests.add(req);
        }

        com.proconsi.electrobazar.models.SuspendRequest request = new com.proconsi.electrobazar.models.SuspendRequest(lineRequests, label);

        isProcessing.setValue(true);
        heldSalesRepository.holdSale(request).observeForever(response -> {
            isProcessing.setValue(false);
            if (response != null) {
                clearTicket();
                setTicketVisible(false);
                errorMessage.setValue("SUCCESS_HOLD"); // Signal success
            } else {
                errorMessage.setValue("Error al poner la venta en espera.");
            }
        });
    }

    public void loadHeldSale(com.proconsi.electrobazar.models.SuspendedSaleResponse heldSale) {
        clearTicket();
        clearCustomer();
        
        List<TicketLine> lines = new ArrayList<>();
        for (com.proconsi.electrobazar.models.SuspendedSaleResponse.SuspendedSaleLineResponse lineResp : heldSale.getLines()) {
            Product p = new Product();
            p.setId(lineResp.getProductId());
            p.setName(lineResp.getProductName());
            p.setPrice(lineResp.getUnitPrice());
            p.setStock(999); // Assume stock is enough for resumed sale, or well handle it in addProduct if we had the full product
            
            TicketLine line = new TicketLine(p, lineResp.getQuantity());
            lines.add(line);
        }
        updateTicket(lines);
        setTicketVisible(true);
    }

    private void updateTicket(List<TicketLine> lines) {
        if (lines == null) lines = new ArrayList<>();
        ticketLines.setValue(lines);

        int items = 0;
        BigDecimal amount = BigDecimal.ZERO;
        Customer customer = selectedCustomer.getValue();
        boolean applyRE = customer != null && Boolean.TRUE.equals(customer.getHasRecargoEquivalencia());

        for (TicketLine line : lines) {
            line.updateTotals(applyRE);
            items += line.getQuantity();
            amount = amount.add(line.getLineTotal());
        }

        totalItems.setValue(items);
        totalAmount.setValue(amount.setScale(2, java.math.RoundingMode.HALF_UP));
    }
}
