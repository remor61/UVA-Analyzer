package com.example.vitor.uva_analisador;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class ActUva extends AppCompatActivity implements View.OnClickListener {

    Button bt_registro, bt_usuarios, bt_dados, bt_ajuda, bt_voltar, bt_salvar;
    EditText ednome, edidade;
    ImageView ImageBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CarregarTelaPrincipal();
    }

    public void CarregarTelaPrincipal(){
        getSupportActionBar().hide();
        setContentView(R.layout.act_uva);

        bt_registro = (Button) findViewById(R.id.bt_registro);
        bt_usuarios = (Button) findViewById(R.id.bt_usuarios);
        bt_dados = (Button) findViewById(R.id.bt_dados);
        bt_ajuda = (Button) findViewById(R.id.bt_ajuda);

        bt_registro.setOnClickListener(this);
        bt_usuarios.setOnClickListener(this);
        bt_dados.setOnClickListener(this);
        bt_ajuda.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_registro:
                Intent intent = new Intent(this, ActTelaRegistro.class);
                this.startActivity(intent);
                break;

            case R.id.bt_usuarios:
                intent = new Intent(this, ActTelaUsuarios1.class);
                this.startActivity(intent);
                break;

            case R.id.bt_dados:
                intent = new Intent(this, ActDados.class);
                this.startActivity(intent);
                break;

            case R.id.bt_ajuda:
                intent = new Intent(this, ActAjuda.class);
                this.startActivity(intent);
                break;


        }
    }


//    public void tela_registro(){
//        getSupportActionBar().hide();
//        setContentView(R.layout.act_tela_registro);
//        //*******************************************************************************
//        ImageBt = (ImageView)findViewById(R.id.imagemmmmm);
//        ImageBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivity(intent);
//            }
//        });
//        //******************************************************************************
//
//        bt_voltar = (Button) findViewById(R.id.bt_voltar);
//
//        bt_voltar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CarregarTelaPrincipal();
//            }
//        });
//
//        bt_salvar = (Button) findViewById(R.id.bt_salvar);
//
//
//    }
//
//    public void tela_usuarios(){
//
//        getSupportActionBar().hide();
//        setContentView(R.layout.act_tela_usuarios);
//
//
//    }
//


    public static class ConnectionThread extends Thread {

        BluetoothSocket btSocket = null;
        BluetoothServerSocket btServerSocket = null;
        InputStream input = null;
        OutputStream output = null;
        String btDevAddress = null;
        String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
        boolean server;
        boolean running = false;
        boolean isConnected = false;

        /*  Este construtor prepara o dispositivo para atuar como servidor.
         */
        public ConnectionThread() {

            this.server = true;
        }

        /*  Este construtor prepara o dispositivo para atuar como cliente.
            Tem como argumento uma string contendo o endereço MAC do dispositivo
        Bluetooth para o qual deve ser solicitada uma conexão.
         */
        public ConnectionThread(String btDevAddress) {

            this.server = false;
            this.btDevAddress = btDevAddress;
        }

        /*  O método run() contem as instruções que serão efetivamente realizadas
        em uma nova thread.
         */
        public void run() {

            /*  Anuncia que a thread está sendo executada.
                Pega uma referência para o adaptador Bluetooth padrão.
             */
            this.running = true;
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

            /*  Determina que ações executar dependendo se a thread está configurada
            para atuar como servidor ou cliente.
             */
            if(this.server) {

                /*  Servidor.
                 */
                try {

                    /*  Cria um socket de servidor Bluetooth.
                        O socket servidor será usado apenas para iniciar a conexão.
                        Permanece em estado de espera até que algum cliente
                    estabeleça uma conexão.
                     */
                    btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("Super Counter", UUID.fromString(myUUID));
                    btSocket = btServerSocket.accept();

                    /*  Se a conexão foi estabelecida corretamente, o socket
                    servidor pode ser liberado.
                     */
                    if(btSocket != null) {

                        btServerSocket.close();
                    }

                } catch (IOException e) {

                    /*  Caso ocorra alguma exceção, exibe o stack trace para debug.
                        Envia um código para a Activity principal, informando que
                    a conexão falhou.
                     */
                    e.printStackTrace();
                    toMainActivity("---N".getBytes());
                }


            } else {

                /*  Cliente.
                 */
                try {

                    /*  Obtem uma representação do dispositivo Bluetooth com
                    endereço btDevAddress.
                        Cria um socket Bluetooth.
                     */
                    BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDevAddress);
                    btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));

                    /*  Envia ao sistema um comando para cancelar qualquer processo
                    de descoberta em execução.
                     */
                    btAdapter.cancelDiscovery();

                    /*  Solicita uma conexão ao dispositivo cujo endereço é
                    btDevAddress.
                        Permanece em estado de espera até que a conexão seja
                    estabelecida.
                     */
                    if (btSocket != null) {
                        btSocket.connect();
                    }

                } catch (IOException e) {

                    /*  Caso ocorra alguma exceção, exibe o stack trace para debug.
                        Envia um código para a Activity principal, informando que
                    a conexão falhou.
                     */
                    e.printStackTrace();
                    toMainActivity("---N".getBytes());
                }

            }

            /*  Pronto, estamos conectados! Agora, só precisamos gerenciar a conexão.
                ...
             */

            if(btSocket != null) {

                /*  Envia um código para a Activity principal informando que a
                a conexão ocorreu com sucesso.
                 */
                this.isConnected = true;
                toMainActivity("---S".getBytes());

                try {

                    /*  Obtem referências para os fluxos de entrada e saída do
                    socket Bluetooth.
                     */
                    input = btSocket.getInputStream();
                    output = btSocket.getOutputStream();

                    /*  Permanece em estado de espera até que uma mensagem seja
                    recebida.
                        Armazena a mensagem recebida no buffer.
                        Envia a mensagem recebida para a Activity principal, do
                    primeiro ao último byte lido.
                        Esta thread permanecerá em estado de escuta até que
                    a variável running assuma o valor false.
                     */
                    while(running) {

                        /*  Cria um byte array para armazenar temporariamente uma
                        mensagem recebida.
                            O inteiro bytes representará o número de bytes lidos na
                        última transmissão recebida.
                            O inteiro bytesRead representa o número total de bytes
                        lidos antes de uma quebra de linha. A quebra de linha
                        representa o fim da mensagem.
                         */
                        byte[] buffer = new byte[1024];
                        int bytes;
                        int bytesRead = -1;

                        /*  Lê os bytes recebidos e os armazena no buffer até que
                        uma quebra de linha seja identificada. Nesse ponto, assumimos
                        que a mensagem foi transmitida por completo.
                         */
                        do {
                            bytes = input.read(buffer, bytesRead+1, 1);
                            bytesRead+=bytes;
                        } while(buffer[bytesRead] != 'F');

                        /*  A mensagem recebida é enviada para a Activity principal.
                         */
                        toMainActivity(Arrays.copyOfRange(buffer, 0, bytesRead));

                    }

                } catch (IOException e) {

                    /*  Caso ocorra alguma exceção, exibe o stack trace para debug.
                        Envia um código para a Activity principal, informando que
                    a conexão falhou.
                     */
                    e.printStackTrace();
                    toMainActivity("---N".getBytes());
                    this.isConnected = false;
                }
            }

        }

        /*  Utiliza um handler para enviar um byte array à Activity principal.
            O byte array é encapsulado em um Bundle e posteriormente em uma Message
        antes de ser enviado.
         */
        private void toMainActivity(byte[] data) {

            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putByteArray("data", data);
            message.setData(bundle);
            ActDados.handler.sendMessage(message);
        }

        /*  Método utilizado pela Activity principal para transmitir uma mensagem ao
         outro lado da conexão.
            A mensagem deve ser representada por um byte array.
         */
        public void write(byte[] data) {

            if(output != null) {
                try {

                    /*  Transmite a mensagem.
                     */
                    output.write(data);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                /*  Envia à Activity principal um código de erro durante a conexão.
                 */
                toMainActivity("---N".getBytes());
            }
        }

        /*  Método utilizado pela Activity principal para encerrar a conexão
         */
        public void cancel() {

            try {

                running = false;
                this.isConnected = false;
                btServerSocket.close();
                btSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            running = false;
            this.isConnected = false;
        }

        public boolean isConnected() {
            return this.isConnected;
        }

    }
}
