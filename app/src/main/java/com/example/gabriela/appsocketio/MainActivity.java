package com.example.gabriela.appsocketio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private EditText mInputMensagem;
    private Button mBtnEnviar;
    private ListView mListaMensagens;
    private ArrayAdapter<String> mAdapter;
    private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputMensagem = (EditText) findViewById(R.id.input_mensagem);
        mBtnEnviar = (Button) findViewById(R.id.btn_enviar);
        mListaMensagens = (ListView) findViewById(R.id.lista_mensagens);

        mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);

        mListaMensagens.setAdapter(mAdapter);

        mBtnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // código para enviar os dados
                if (!mInputMensagem.getText().toString().equals("")) {
                    mSocket.emit("mensagem_servidor", mInputMensagem.getText());
                    mInputMensagem.setText("");
                }
            }
        });

        try {
            mSocket = IO.socket("http://192.168.122.1:8100");
        } catch (URISyntaxException e) {
            this.finish();
            return;
        }

        mSocket.on("mensagem_cliente", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (args.length > 0) {
                    // Note a necessidade de usar o método runOnUiThread pois este código é
                    // executado numa thread separada, então precisamos rodar o código da UI
                    // na thread adequada
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.add((String) args[0]);
                            mAdapter.notifyDataSetChanged();

                            // Apenas faz um scroll para o novo item da lista
                            mListaMensagens.smoothScrollToPosition(mAdapter.getCount() - 1);
                        }
                    });
                }
            }
        });

        mSocket.connect();
    }

    @Override //testar esse trecho aqui é bem importante
    protected void onDestroy() {
        super.onDestroy();

        if (mSocket != null)
            mSocket.disconnect();
    }


}


