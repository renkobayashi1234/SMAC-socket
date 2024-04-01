package com.example.smacsocket.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.smacsocket.service.AccessLogService;

@Component
public class SocketComponent {
    private Logger logger = LoggerFactory.getLogger(SocketComponent.class);

    private int socketPort=8081;

    @Autowired
    AccessLogService accessLogService;

    @Scheduled(initialDelay = 1000)
    public void socket() throws IOException {
        logger.info("socket start");

        ServerSocket serverSoc = null;
        try {
            // ソケットを作成
            serverSoc = new ServerSocket(socketPort);
        } catch (IOException e) {
            //log.info("ソケットを作成失敗。" + e.toString());
            logger.info("ソケットを作成失敗。" + e.toString());
            e.printStackTrace();
            return;
        } catch (NumberFormatException e) {
            //log.info("ソケットを作成失敗。" + e.toString());
            logger.info("ソケットを作成失敗。" + e.toString());
            e.printStackTrace();
            serverSoc.close();
            return;
        }

        boolean flag = true;
        // クライアントからの接続を待機するaccept()メソッド。
        // accept()は、接続があるまで処理はブロックされる。
        // もし、複数のクライアントからの接続を受け付けるようにするには。
        // スレッドを使う。
        // accept()は接続時に新たなsocketを返す。これを使って通信を行なう。
        Socket socket = null;
        while (flag) {
            //log.info("クライアントからの発報を待ちます。 ");
            logger.info("クライアントからの発報を待ちます。 ");
            try {
                socket = serverSoc.accept();
            } catch (IOException e) {
                //log.info("受信失敗。" + e.toString() + " 【異常】");
                logger.info("受信失敗。" + e.toString() + " 【異常】");
                e.printStackTrace();
                // 通信用ソケットの接続をクローズする。
                // 受信失敗時、返信しなくでも、クライアント側１を受けないので、失敗として処理
                socket.close();
                continue;
            }
            // 接続があれば次の命令に移る。
            //log.info(socket.getInetAddress() + "から発報しました。");
            logger.info(socket.getInetAddress() + "から発報しました。");
            // socketからのデータはInputStreamReaderに送り、さらに
            // BufferedReaderによってバッファリングする。
            BufferedReader reader = null;
            InputStream in = null;
            OutputStream out = null;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // 読み取った発報メッセージを表示する。
                byte[] buff = new byte[1024];
                in = socket.getInputStream();
                in.read(buff);
                //log.info("アラート番号を取得:" + new String(buff, "UTF-8").trim());
                logger.info("アラート番号を取得:" + new String(buff, "UTF-8").trim());

                // 停止指令をもらえば、監視停止。
                if (new String(buff, "UTF-8").trim().equalsIgnoreCase("quit")) {
                    //log.info("監視サーバ停止。");
                    logger.info("監視サーバ停止。");
                    // 通信用ソケットの接続をクローズする。
                    // 入力ストリームをクローズする。
                    if (reader != null) {
                        reader.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    socket.close();
                    break;
                }

                // アラートログテーブルデータ更新処理
                String alertNo =new String(buff, "UTF-8").trim();
                //String iResult=putAlertingInfoService.put(alertNo);
                String iResult= accessLogService.sendPostRequest(alertNo);

                /* 
                String iResult;
                try {
                    iResult = connection(alertNo);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                }
                */
                
                //int iResult=1;
                if (iResult.equals("1")){
                    //log.info("更新処理成功。");
                    logger.info("更新処理成功");
                }else{
                    //log.info("更新処理失敗");
                    logger.info("更新処理失敗");
                }
                byte[] bResult = String.valueOf(iResult).getBytes("UTF-8");
                out = socket.getOutputStream();
                out.write(bResult, 0, 1);

                // 入力ストリームをクローズする。
                if (reader != null) {
                    reader.close();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                // 通信用ソケットの接続をクローズする。
                socket.close();
            } catch (IOException e) {
                // 返信:0 処理失敗
                byte[] bResult = String.valueOf(0).getBytes("UTF-8");
                out = socket.getOutputStream();
                out.write(bResult, 0, 1);
                //log.info("受信処理失敗。" + e.toString());
                logger.info("受信処理失敗。");
                e.printStackTrace();
                // 入力ストリームをクローズする。
                if (reader != null) {
                    reader.close();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                // 通信用ソケットの接続をクローズする。
                socket.close();
            }
        }

        try {
            // ソケットをクロース
            serverSoc.close();
        } catch (IOException e) {
            //log.info("受信ソケットクロース失敗。" + e.toString());
            logger.info("受信ソケットクロース失敗。" + e.toString());
            e.printStackTrace();
            return;
        }
    }
}

