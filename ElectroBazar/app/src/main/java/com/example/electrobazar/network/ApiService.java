package com.example.electrobazar.network;

import com.example.electrobazar.models.CashRegister;
import com.example.electrobazar.models.Category;
import com.example.electrobazar.models.Customer;
import com.example.electrobazar.models.Product;
import com.example.electrobazar.models.Sale;
import com.example.electrobazar.models.Worker;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Workers & Auth
    @POST("api/workers/login")
    Call<Worker> login(@Body Map<String, String> credentials);

    // Products
    @GET("api/products")
    Call<List<Product>> getProducts();

    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") Long id);

    @GET("api/products/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") Long categoryId);

    @GET("api/products/search")
    Call<List<Product>> searchProducts(@Query("name") String name);

    // Categories
    @GET("api/categories")
    Call<List<Category>> getCategories();

    // Sales
    @POST("api/sales")
    Call<Sale> createSale(@Body Sale sale);

    @GET("api/sales/today")
    Call<List<Sale>> getTodaySales();

    // Customers
    @GET("api/customers/search")
    Call<List<Customer>> searchCustomers(@Query("query") String query);

    @POST("api/customers")
    Call<Customer> createCustomer(@Body Customer customer);

    // Cash Register
    @GET("api/cash-registers/today")
    Call<CashRegister> getTodayCashRegister();

    @POST("api/cash-registers/close")
    Call<CashRegister> closeCashRegister(@Query("closingBalance") Double closingBalance, @Query("notes") String notes);
}