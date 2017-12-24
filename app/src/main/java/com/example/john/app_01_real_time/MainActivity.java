package com.example.john.app_01_real_time;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;


import com.github.nkzawa.socketio.client.Socket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MainActivity mainActivity = this;
    private EditText mInputMessageView;
    private Button btnSend;
    private TextView txt;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.37:3000/");
        } catch (URISyntaxException e) {}
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInputMessageView = (EditText)findViewById(R.id.editText);
        btnSend = (Button)findViewById(R.id.btnsend);
        txt = (TextView)findViewById(R.id.textView2);
        btnSend.setOnClickListener(this);
        mSocket.on("response_client_message", onNewMessage);
        mSocket.connect();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    try {
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                    Toast.makeText(mainActivity,"Recibió un mensaje",Toast.LENGTH_LONG).show();
                    txt.setText(message);
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }

    private void attemptSend() {
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mInputMessageView.setText("");
        mSocket.emit("message", "Hola");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnsend:
                attemptSend();
                //Toast.makeText(this,"hola",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
