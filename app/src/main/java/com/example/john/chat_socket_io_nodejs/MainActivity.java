package com.example.john.chat_socket_io_nodejs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MainActivity myActivity = this;
    private EditText mInputMessageView;
    private EditText messageHistory;
    private Button btnSend;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.34:3000");
        } catch (URISyntaxException e) {
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputMessageView = (EditText)findViewById(R.id.editText4);
        messageHistory = (EditText)findViewById(R.id.editText3);
        btnSend = (Button)findViewById(R.id.button3);
        btnSend.setOnClickListener(this);

        mSocket.on("server-response", onNewMessage);
        mSocket.connect();
    }

    private void attemptSend() {
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mInputMessageView.setText("");
        mSocket.emit("client-message", message);
    }

    private  Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            myActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String dato;
                    try {
                        dato = data.getString("dato");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    addMessage(dato);
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("client-message", onNewMessage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button3:
                attemptSend();
                break;
        }
    }

    private void addMessage(String dato){
        String log = messageHistory.getText().toString();
        log += "\n"+dato;
        messageHistory.setText(log);
    }
}
