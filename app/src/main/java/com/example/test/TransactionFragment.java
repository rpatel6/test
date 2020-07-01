package com.example.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TransactionFragment extends Fragment {

    public enum TransactionKey {
        CONTENT, IMAGE
    }

    private String content;
    private String image;

    public TransactionFragment(Bundle bundle) {
        if (bundle != null) {
            this.content = bundle.getString(TransactionKey.CONTENT.name());
            this.image = bundle.getString(TransactionKey.IMAGE.name());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.transaction_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.content_field);
        textView.setText("Content: " + content);

        ImageView imageView = view.findViewById(R.id.bitmap);
        imageView.setImageBitmap(processImageString());
    }

    private Bitmap processImageString() {
        String[] imageSplit = image.split(",");
        byte[] decodedString = Base64.decode(imageSplit[imageSplit.length - 1], Base64.DEFAULT);
        Bitmap bitmap =  BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return Bitmap.createScaledBitmap(bitmap, 800, 800, false);
    }
}
