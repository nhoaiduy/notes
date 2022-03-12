package vn.edu.huflit.notes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class DeleteDialog extends Dialog {

    public  Context context;
    Button OK_btn, cancel_btn;

    DeleteDialog.Listener listener;

    public DeleteDialog(Context context, DeleteDialog.Listener listener) {
        super(context);
        this.context = context;
        this.listener=listener;
    }

    interface Listener{
        void deleteNote();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_dialog);

        this.OK_btn = (Button) findViewById(R.id.OK_btn);
        this.cancel_btn  = (Button) findViewById(R.id.Cancel_btn);

        this.OK_btn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOKClick();
            }
        });
        this.cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCancelClick();
            }
        });
    }

    // User click "OK" button.
    private void buttonOKClick()  {

        this.dismiss(); // Close Dialog
        if(listener != null){
            listener.deleteNote();
        }

    }

    // User click "Cancel" button.
    private void buttonCancelClick()  {
        this.dismiss();
    }
}
