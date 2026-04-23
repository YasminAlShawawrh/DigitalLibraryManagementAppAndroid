package com.example.a12122028_1220848_courseproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LibraryInfoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_info, container, false);

        Button btnCall = view.findViewById(R.id.btn_call);
        Button btnMap = view.findViewById(R.id.btn_map);
        Button btnEmail = view.findViewById(R.id.btn_email);

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+97022982000")); // Use actual library number
            startActivity(intent);
        });

        btnMap.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=Birzeit University");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        btnEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:library@birzeit.edu"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Library Inquiry");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Librarian,\n\n");
            startActivity(Intent.createChooser(emailIntent, "Send email via..."));
        });

        return view;
    }
}
