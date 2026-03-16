package com.proconsi.electrobazar.network;

import com.proconsi.electrobazar.models.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // --- Worker / Auth ---
    @POST("api/workers/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/workers")
    Call<List<Worker>> getAllWorkers();

    @GET("api/workers/{id}")
    Call<Worker> getWorkerById(@Path("id") Long id);

    @POST("api/workers")
    Call<Worker> createWorker(@Body Worker worker);

    @PUT("api/workers/{id}")
    Call<Worker> updateWorker(@Path("id") Long id, @Body Worker worker);

    @DELETE("api/workers/{id}")
    Call<Void> deleteWorker(@Path("id") Long id);

    // --- Admin ---
    @POST("api/admin/verify-pin")
    Call<Void> verifyAdminPin(@Body Map<String, String> body);

    @GET("api/admin/download/invoice/{id}")
    @Streaming
    Call<ResponseBody> downloadInvoice(@Path("id") Long id);

    @GET("api/admin/download/return/{id}")
    @Streaming
    Call<ResponseBody> downloadReturn(@Path("id") Long id);

    @GET("api/admin/tariffs/{id}/history/pdf")
    @Streaming
    Call<ResponseBody> downloadTariffHistoryPdf(@Path("id") Long id, @Query("date") String date);

    @Multipart
    @POST("api/admin/upload-csv")
    Call<Map<String, Object>> uploadCsv(@Part MultipartBody.Part file);

    @POST("api/admin/tax-rates/{newId}/apply-to-products")
    Call<Void> applyTaxRateToProducts(@Path("newId") Long newId);

    @POST("api/admin/settings")
    Call<Map<String, Object>> saveCompanySettings(@Body CompanySettings settings);

    @DELETE("api/admin/workers/{id}")
    Call<Void> deactivateWorker(@Path("id") Long id);

    // --- Activity Log ---
    @GET("api/activity-log")
    Call<List<ActivityLog>> getActivityLogs();

    @GET("api/activity-log/recent")
    Call<List<ActivityLog>> getRecentActivityLogs();

    // --- Cash Register ---
    @GET("api/cash-registers/open")
    Call<CashRegister> getOpenRegister();

    @GET("api/cash-registers/closed")
    Call<List<CashRegister>> getAllClosedRegisters();

    @GET("api/cash-registers/close-info")
    Call<CashCloseInfoDTO> getCashCloseInfo();

    @GET("api/cash-registers/open-suggestion")
    Call<CashRegisterOpenSuggestion> getOpenSuggestion();

    @POST("api/cash-registers/open")
    Call<CashRegister> openCashRegister(
            @Query("openingBalance") BigDecimal openingBalance,
            @Header("X-Worker-Id") Long workerId);

    @POST("api/cash-registers/close")
    Call<CashRegister> closeCashRegister(
            @Query("closingBalance") BigDecimal closingBalance,
            @Query("notes") String notes,
            @Query("retainedAmount") BigDecimal retainedAmount,
            @Header("X-Worker-Id") Long workerId);

    @GET("api/cash-registers/{id}")
    Call<CashRegister> getCashRegisterById(@Path("id") Long id);

    @GET("api/cash-registers/{id}/ticket")
    @Streaming
    Call<ResponseBody> downloadCashRegisterTicket(@Path("id") Long id);

    // --- Cash Withdrawals ---
    @POST("api/cash-withdrawals")
    Call<CashWithdrawal> createCashMovement(@Body CashWithdrawalRequest body);

    // --- Categories ---
    @GET("api/categories")
    Call<List<Category>> getCategories();

    @GET("api/categories/filter")
    Call<List<Category>> filterCategories(@Query("search") String search);

    @GET("api/categories/{id}")
    Call<Category> getCategoryById(@Path("id") Long id);

    @POST("api/categories")
    Call<Category> createCategory(@Body Category category);

    @PUT("api/categories/{id}")
    Call<Category> updateCategory(@Path("id") Long id, @Body Category category);

    @DELETE("api/categories/{id}")
    Call<Void> deleteCategory(@Path("id") Long id);

    // --- Customers ---
    @GET("api/customers")
    Call<List<Customer>> getCustomers();

    @GET("api/customers/search")
    Call<List<Customer>> searchCustomers(@Query("query") String query);

    @GET("api/customers/{id}")
    Call<Customer> getCustomerById(@Path("id") Long id);

    @POST("api/customers")
    Call<Customer> createCustomer(@Body CustomerRequest request);

    @PUT("api/customers/{id}")
    Call<Customer> updateCustomer(@Path("id") Long id, @Body CustomerRequest request);

    @DELETE("api/customers/{id}")
    Call<Void> deleteCustomer(@Path("id") Long id);

    // --- Dashboard ---
    @GET("api/admin/dashboard/stats")
    Call<DashboardStats> getDashboardStats(@Query("period") String period);

    // --- INE / CPI ---
    @GET("api/ipc/current")
    Call<Map<String, Object>> getCurrentIpc();

    @GET("api/ipc/preview")
    Call<List<Map<String, Object>>> getIpcPreview(@Query("percentage") BigDecimal percentage);

    // --- Products ---
    @GET("api/products")
    Call<List<Product>> getProducts();

    @GET("api/products/search")
    Call<List<Product>> searchProducts(@Query("query") String query);

    @GET("api/products/filter")
    Call<List<Product>> filterProducts(
            @Query("category") Long categoryId,
            @Query("minPrice") BigDecimal minPrice,
            @Query("maxPrice") BigDecimal maxPrice,
            @Query("active") Boolean active);

    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") Long id);

    @POST("api/products")
    Call<Product> createProduct(@Body ProductRequest request);

    @PUT("api/products/{id}")
    Call<Product> updateProduct(@Path("id") Long id, @Body ProductRequest request);

    @DELETE("api/products/{id}")
    Call<Void> deleteProduct(@Path("id") Long id);

    @DELETE("api/products/{id}/hard")
    Call<Void> hardDeleteProduct(@Path("id") Long id);

    @GET("api/products/selection")
    Call<List<ProductSelectionItem>> getProductsForSelection();

    @GET("tpv/api/products/{id}/price")
    Call<PriceResponse> getProductPriceByTariff(@Path("id") Long productId, @Query("tariffId") Long tariffId);

    // --- Product Prices ---
    @POST("api/product-prices/{productId}/schedule")
    Call<ProductPriceResponse> scheduleProductPrice(@Path("productId") Long productId, @Body ProductPriceRequest request);

    @GET("api/product-prices/{productId}")
    Call<ProductPriceResponse> getCurrentProductPrice(@Path("productId") Long productId);

    @GET("api/product-prices/{productId}/history")
    Call<List<ProductPriceResponse>> getProductPriceHistory(@Path("productId") Long productId);

    @POST("api/product-prices/bulk-schedule")
    Call<List<ProductPriceResponse>> bulkSchedulePrices(@Body BulkPriceUpdateRequest request);

    // --- Returns ---
    @GET("api/returns/check")
    Call<ReturnCheckResponse> checkReturn(@Query("query") String query);

    @POST("api/returns")
    Call<SaleReturn> processReturn(@Body ReturnRequest request);

    @GET("api/returns")
    Call<List<SaleReturn>> getReturns(@Query("from") String from, @Query("to") String to);

    @GET("api/returns/{id}")
    Call<SaleReturn> getReturnById(@Path("id") Long id);


    // --- Roles ---
    @GET("api/roles")
    Call<List<Role>> getRoles();

    @GET("api/roles/{id}")
    Call<Role> getRoleById(@Path("id") Long id);

    @POST("api/roles")
    Call<Role> createRole(@Body Role role);

    @PUT("api/roles/{id}")
    Call<Role> updateRole(@Path("id") Long id, @Body Role role);

    @DELETE("api/roles/{id}")
    Call<Void> deleteRole(@Path("id") Long id);

    @GET("api/permissions")
    Call<List<String>> getPermissions();

    // --- Sales ---
    @GET("api/sales")
    Call<List<Sale>> getSales();

    @GET("api/sales/{id}")
    Call<Sale> getSaleById(@Path("id") Long id);

    @GET("api/sales/today")
    Call<List<Sale>> getTodaySales();

    @GET("api/sales/stats/today")
    Call<SaleSummaryResponse> getTodaySalesStats();

    @GET("api/sales/range")
    Call<List<Sale>> getSalesRange(@Query("from") String from, @Query("to") String to);

    @POST("api/sales")
    Call<Sale> createSale(@Body Sale sale, @Header("X-Worker-Id") Long workerId);

    @POST("api/sales/{id}/cancel")
    Call<Void> cancelSale(@Path("id") Long id, @Body Map<String, String> body, @Header("X-Worker-Id") Long workerId);

    @POST("api/sales/with-tax")
    Call<SaleWithTaxResponse> createSaleWithTax(@Body SaleWithTaxRequest request);

    // --- Suspended Sales (Held Sales) ---
    @GET("api/suspended-sales")
    Call<List<SuspendedSaleResponse>> getSuspendedSales();

    @POST("api/suspended-sales")
    Call<SuspendedSaleResponse> suspendSale(@Body SuspendRequest request);

    @POST("api/suspended-sales/{id}/resume")
    Call<SuspendedSaleResponse> resumeSuspendedSale(@Path("id") Long id);

    @POST("api/suspended-sales/{id}/cancel")
    Call<SuspendedSaleResponse> cancelSuspendedSale(@Path("id") Long id);


    // --- Tariffs ---
    @GET("api/tariffs")
    Call<List<Tariff>> getTariffs(@Query("includeInactive") boolean includeInactive);

    @GET("api/tariffs/{id}")
    Call<Tariff> getTariffById(@Path("id") Long id);

    @GET("api/tariffs/customer-counts")
    Call<Map<Long, Long>> getTariffCustomerCounts();

    @POST("api/tariffs")
    Call<Tariff> createTariff(@Body Map<String, Object> body);

    @PUT("api/tariffs/{id}")
    Call<Tariff> updateTariff(@Path("id") Long id, @Body Map<String, Object> body);

    @DELETE("api/tariffs/{id}/deactivate")
    Call<Map<String, String>> deactivateTariff(@Path("id") Long id);

    @POST("api/tariffs/{id}/activate")
    Call<Map<String, String>> activateTariff(@Path("id") Long id);

    // --- Tax Rates ---
    @GET("admin/api/tax-rates")
    Call<List<TaxRate>> getTaxRates();

    @GET("admin/api/tax-rates/active")
    Call<List<TaxRate>> getActiveTaxRates();

    @POST("admin/api/tax-rates")
    Call<TaxRate> createTaxRate(@Body TaxRate taxRate);

    @PUT("admin/api/tax-rates/{id}")
    Call<TaxRate> updateTaxRate(@Path("id") Long id, @Body TaxRate taxRate);

    @DELETE("admin/api/tax-rates/{id}")
    Call<Void> deleteTaxRate(@Path("id") Long id);

    @POST("admin/api/tax-rates/apply-selective")
    Call<Map<String, Object>> applySelectiveTaxRate(@Body ApplySelectiveTaxRateRequest request);
}
