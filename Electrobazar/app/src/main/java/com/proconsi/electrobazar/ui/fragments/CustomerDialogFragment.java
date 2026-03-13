package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Customer;
import com.proconsi.electrobazar.models.CustomerRequest;
import com.proconsi.electrobazar.models.Tariff;

import java.util.ArrayList;
import java.util.List;

public class CustomerDialogFragment extends DialogFragment {

    private Customer customer;
    private List<Tariff> tariffs = new ArrayList<>();
    private OnCustomerSavedListener listener;

    private EditText nameEdit, taxIdEdit, emailEdit, phoneEdit, addressEdit, cityEdit, postalCodeEdit;
    private RadioGroup typeRadioGroup;
    private Spinner tariffSpinner;
    private SwitchMaterial activeSwitch, reSwitch;
    private View reSection;
    private TextView taxIdLabel;

    public interface OnCustomerSavedListener {
        void onCustomerSaved(CustomerRequest request);
    }

    public static CustomerDialogFragment newInstance(Customer customer, List<Tariff> tariffs, OnCustomerSavedListener listener) {
        CustomerDialogFragment fragment = new CustomerDialogFragment();
        fragment.customer = customer;
        fragment.tariffs = tariffs;
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_customer_form, container, false);

        nameEdit = view.findViewById(R.id.editCustomerName);
        taxIdEdit = view.findViewById(R.id.editCustomerTaxId);
        taxIdLabel = view.findViewById(R.id.customerTaxIdLabel);
        emailEdit = view.findViewById(R.id.editCustomerEmail);
        phoneEdit = view.findViewById(R.id.editCustomerPhone);
        addressEdit = view.findViewById(R.id.editCustomerAddress);
        cityEdit = view.findViewById(R.id.editCustomerCity);
        postalCodeEdit = view.findViewById(R.id.editCustomerPostalCode);
        typeRadioGroup = view.findViewById(R.id.customerTypeRadioGroup);
        tariffSpinner = view.findViewById(R.id.editCustomerTariffSpinner);
        activeSwitch = view.findViewById(R.id.editCustomerActiveSwitch);
        reSwitch = view.findViewById(R.id.editCustomerReSwitch);
        reSection = view.findViewById(R.id.customerReSection);

        setupTariffSpinner();
        setupTypeListeners();

        if (customer != null) {
            ((TextView)view.findViewById(R.id.customerDialogTitle)).setText("Editar Cliente");
            nameEdit.setText(customer.getName());
            taxIdEdit.setText(customer.getTaxId());
            emailEdit.setText(customer.getEmail());
            phoneEdit.setText(customer.getPhone());
            addressEdit.setText(customer.getAddress());
            cityEdit.setText(customer.getCity());
            postalCodeEdit.setText(customer.getPostalCode());
            
            if ("COMPANY".equals(customer.getType())) {
                typeRadioGroup.check(R.id.radioCompany);
                reSection.setVisibility(View.VISIBLE);
            } else {
                typeRadioGroup.check(R.id.radioIndividual);
                reSection.setVisibility(View.GONE);
            }

            activeSwitch.setChecked(Boolean.TRUE.equals(customer.getActive()));
            reSwitch.setChecked(Boolean.TRUE.equals(customer.getHasRecargoEquivalencia()));

            if (customer.getTariff() != null) {
                for (int i = 0; i < tariffs.size(); i++) {
                    if (tariffs.get(i).getId().equals(customer.getTariff().getId())) {
                        tariffSpinner.setSelection(i + 1); // +1 because index 0 is MINORISTA
                        break;
                    }
                }
            }
        }

        view.findViewById(R.id.cancelCustomerBtn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.saveCustomerBtn).setOnClickListener(v -> saveCustomer());

        return view;
    }

    private void setupTariffSpinner() {
        List<String> tariffNames = new ArrayList<>();
        tariffNames.add("-- Sin tarifa asignada (MINORISTA) --");
        for (Tariff t : tariffs) {
            tariffNames.add(t.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tariffNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tariffSpinner.setAdapter(adapter);
    }

    private void setupTypeListeners() {
        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isCompany = checkedId == R.id.radioCompany;
            reSection.setVisibility(isCompany ? View.VISIBLE : View.GONE);
            taxIdLabel.setText(isCompany ? "NIF / CIF *" : "NIF / CIF");
        });
    }

    private void saveCustomer() {
        String name = nameEdit.getText().toString().trim();
        String taxId = taxIdEdit.getText().toString().trim();
        String type = typeRadioGroup.getCheckedRadioButtonId() == R.id.radioCompany ? "COMPANY" : "INDIVIDUAL";
        
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("COMPANY".equals(type) && taxId.isEmpty()) {
            Toast.makeText(getContext(), "El CIF es obligatorio para empresas", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomerRequest request = new CustomerRequest();
        request.setName(name);
        request.setTaxId(taxId);
        request.setEmail(emailEdit.getText().toString().trim());
        request.setPhone(phoneEdit.getText().toString().trim());
        request.setAddress(addressEdit.getText().toString().trim());
        request.setCity(cityEdit.getText().toString().trim());
        request.setPostalCode(postalCodeEdit.getText().toString().trim());
        request.setType(type);
        request.setActive(activeSwitch.isChecked());
        request.setHasRecargoEquivalencia("COMPANY".equals(type) && reSwitch.isChecked());

        int tariffPos = tariffSpinner.getSelectedItemPosition();
        if (tariffPos > 0) {
            request.setTariffId(tariffs.get(tariffPos - 1).getId());
        } else {
            request.setTariffId(null);
        }

        listener.onCustomerSaved(request);
        dismiss();
    }
}
