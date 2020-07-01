package com.example.test;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.nio.channels.CompletionHandler;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private Button transactionBtn;
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button schemaBtn = view.findViewById(R.id.schema);
        transactionBtn = view.findViewById(R.id.start_transaction);
        transactionBtn.setEnabled(false);
        schemaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setupApi(view.getContext(), new CompletionHandler<String, Void>() {
                    @Override
                    public void completed(String result, Void attachment) {
                        if (result != null) {
                            transactionBtn.setEnabled(true);
                        } else {
                            transactionBtn.setEnabled(false);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {

                    }
                });
            }
        });

        transactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.startTransaction(view.getContext(), new CompletionHandler<Bundle, Void>() {
                    @Override
                    public void completed(Bundle result, Void attachment) {
                        if (result != null && getActivity().getSupportFragmentManager() != null) {
                            TransactionFragment fragment = new TransactionFragment(result);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, fragment)
                                    .addToBackStack(MainFragment.class.getSimpleName())
                                    .commit();
                        } else {
                            Toast.makeText(view.getContext(), "Please setup the api again.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {

                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mViewModel.getSchemaId() != null) {
            transactionBtn.setEnabled(true);
        } else {
            transactionBtn.setEnabled(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }
}