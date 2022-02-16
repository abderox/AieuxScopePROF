package com.example.testchat.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.testchat.R;


public class customizedAlert extends AppCompatDialogFragment {
    private EditText editTextName;
    private EditText editTextNumber;
    private customizedAlertListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.customized_dialog, null);

        builder.setView(view)
                .setTitle("Add contact")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String Name = editTextName.getText().toString();
                        String Number = editTextNumber.getText().toString();
                        listener.applyTexts(Name, Number);
                    }
                });

        editTextName = view.findViewById(R.id.edit_username);
        editTextNumber = view.findViewById(R.id.edit_number);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (customizedAlertListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface customizedAlertListener {
        void applyTexts(String Name, String Number);
    }
}