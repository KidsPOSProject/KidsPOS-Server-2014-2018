package info.nukoneko.kidspos.util.print;

import info.nukoneko.cuc.kidspos4j.model.ModelItem;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class Printer {

    private final Socket mSocket;
    private final OutputStream mStream;

    public Printer(String ipAddress, int port) throws IOException {
        mSocket = new Socket(ipAddress, port);
        mStream = mSocket.getOutputStream();
    }

    /**
     * printObjectを受け取り、レシートを印刷する（Cutも含む）
     * @param printObject
     */
    public void receipt(PrintObject printObject){
        //初期化命令
        this.write(new byte[]{0x1B, 0x40}); //初期化
        this.write(new byte[]{0x1B, 0x33, 0x28}); //改行量設定
        this.setGravity(Direction.CENTER);
        //タイトル
        this.write(new byte[]{0x1c, 0x70, 0x01, 0x00});

        this.writeTextWithLine(new SimpleDateFormat("yyyy年MM月dd日(E) HH時mm分ss秒").format(new Date())); //日付時刻印字
        this.addNewLine();

        // 店舗名・担当者
        this.setGravity(Direction.LEFT);
        this.writeTextWithLine("店舗名 : " + printObject.getStoreName());
        this.writeTextWithLine("　担当 : " + printObject.getStaffName());
        this.drawLine();

        // 商品
        List<ModelItem> item = printObject.getItems();
        for( int i = 0; i < item.size(); i++) {
            this.writeRow(item.get(i).getName(), item.get(i).getPrice());
        }
        this.drawLine();

        //小計
        this.writeRow("ごうけい",printObject.getTotal());
        this.writeRow("おあずかり",printObject.getDeposit());

        this.writeRow("おつり",printObject.getChange());
        this.drawLine();

        //中央揃え
        this.setGravity(Direction.CENTER);

        // 注釈
        this.addNewLine();
        this.writeTextWithLine("印字保護のため、こちらの面を");
        this.writeTextWithLine("内側に折って保管してください");
        this.addNewLine();

        // バーコード
        this.drawBarcode(printObject.getTransactionId());
        this.cut();
    }

    /**
     * 文字列と金額を受け取り、1行のレコードを印字する
     * @param name 出力レコードの文字列（商品名など）
     * @param price 出力レコードの金額（価格など）
     */
    public void writeRow(String name, Integer price){
        Integer priceOrder;
        if(price == 0) priceOrder = 1;
        else priceOrder = (int)Math.log10(price) + 1;

        this.write(new byte[]{0x1B, 0x24,24,0});
        this.writeText(name);
        this.write(new byte[]{0x1B, 0x24,(byte)(226 - priceOrder * 12) ,1});
        this.writeText(price + "リバー");
        this.addNewLine();
    }

    /**
     * 文字列を印字する
     * @param text
     */
    public void writeText(String text) {
        try {
            write(concat(text.getBytes("SJIS")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void writeTextWithLine(String text) {
        try {
            write(concat(text.getBytes("SJIS")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        write(new byte[]{0x0A});
    }

    /**
     * 改行を行う
     */
    public void addNewLine(){
        write(new byte[]{0x0A});
    }

    /**
     * 文字列を"太字で"印字する
     * @param text
     */
    public void writeTextWithBold(String text) {
        try {
            write(concat(new byte[]{0x1B, 0x45, 0x01}, text.getBytes("SJIS"), new byte[]{0x1B, 0x45, 0x00}));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * CODE39バーコードを印字する
     * @param code バーコードにする文字列
     */
     public void drawBarcode(String code){
        byte[] codeByte = code.getBytes();
        write(new byte[]{0x1D, 0x68, 0x50}); //高さ設定 80(0x50) * 1dot(0.125mm) = 80dot(10mm)
        write(new byte[]{0x1D, 0x67, 0x02}); //モジュール幅設定 3(0x03) * 1dot(0.125mm) = 3dot ＜2から6＞
        write(new byte[]{0x1D, 0x48, 0x00}); //解説文字印字（印字しない）
        write(new byte[]{0x1D, 0x6B, 0x45}); //CODE39指定
        write((byte)codeByte.length); //codeLength
        write(codeByte);
        code = code.substring(1,code.length()-1);
        this.writeTextWithLine(code);
        write(new byte[]{0x0A}); //改行（LF）
    }

    /**
     * QRコードを印字する
     * @param code
     */
    public void drawQR(String code){
        write(new byte[]{0x1D, 0x28, 0x6B, 3, 0, 49, 67, 5});// モジュール幅を5dotに設定
        write(new byte[]{0x1D, 0x28, 0x6B, (byte)(code.length() + 3), 0, 49, 80, 48});
        write(code.getBytes());
        write(new byte[]{0x1D, 0x28, 0x6B, 3, 0, 49, 81, 48});
    }
    /**
     * 水平線を引く
     */
    public void drawLine(){
        this.writeTextWithLine("────────────────────────"); //24文字
    }

    public void cut() {
        // 印刷するときの、下の余白が変なら 3バイト目を変えてみよう!
        write(new byte[]{0x1B, 0x64, (byte) 4});
        write(new byte[]{0x1D, 0x56, 0x30, 0x0});
    }

    /**
     * 印字文字の位置揃え
     * @param dir LEFT,CENTER,RIGHT
     */
    public void setGravity(Direction dir){
        switch (dir){
            case LEFT:
                write(new byte[]{0x1B, 0x61, 0x00});
                break;
            case CENTER:
                write(new byte[]{0x1B, 0x61, 0x01});
                break;
            case RIGHT:
                write(new byte[]{0x1B, 0x61, 0x02});
                break;
        }
    }


    public void close() throws IOException {
        mStream.flush();
        mStream.close();
        mSocket.close();
    }

    public void write(byte[] commands) {
        try {
            mStream.write(commands);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte commands) {
        try {
            mStream.write(commands);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printSample1(){
        write(new byte[]{
                (byte)0x1B,(byte)0x40,(byte)0x1B,(byte)0x74,(byte)0x01,(byte)0x1C,(byte)0x43,(byte)0x01,
                (byte)0x1B,(byte)0x61,(byte)0x01,(byte)0x1C,(byte)0x70,(byte)0x01,(byte)0x30,(byte)0x1B,
                (byte)0x45,(byte)0x01,(byte)0x1B,(byte)0x21,(byte)0x20,(byte)0x30,(byte)0x30,(byte)0x46,
                (byte)0x4B,(byte)0x53,(byte)0x59,(byte)0x53,(byte)0x54,(byte)0x45,(byte)0x4D,(byte)0x3E,
                (byte)0x3E,(byte)0x1B,(byte)0x21,(byte)0x00,(byte)0x1B,(byte)0x45,(byte)0x00,(byte)0x0A,
                (byte)0x1B,(byte)0x33,(byte)0x28,(byte)0x32,(byte)0x30,(byte)0x31,(byte)0x33,(byte)0x94,
                (byte)0x4E,(byte)0x31,(byte)0x30,(byte)0x8C,(byte)0x8E,(byte)0x32,(byte)0x38,(byte)0x93,
                (byte)0xFA,(byte)0x20,(byte)0x31,(byte)0x38,(byte)0x3A,(byte)0x34,(byte)0x31,(byte)0x0A,
                (byte)0x1B,(byte)0x61,(byte)0x00,(byte)0x1B,(byte)0x33,(byte)0x1E,(byte)0x4E,(byte)0x6F,
                (byte)0x2E,(byte)0x39,(byte)0x39,(byte)0x39,(byte)0x2D,(byte)0x58,(byte)0x58,(byte)0x2D,
                (byte)0x39,(byte)0x39,(byte)0x39,(byte)0x39,(byte)0x39,(byte)0x39,(byte)0x39,(byte)0x0A,
                (byte)0x1B,(byte)0x33,(byte)0x28,(byte)0x92,(byte)0x53,(byte)0x93,(byte)0x96,(byte)0x3A,
                (byte)0x97,(byte)0xE9,(byte)0x96,(byte)0xD8,(byte)0x0A,(byte)0x1B,(byte)0x61,(byte)0x01,
                (byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x82,(byte)0xA8,(byte)0x94,(byte)0x83,(byte)0x82,(byte)0xA2,(byte)0x8F,
                (byte)0xE3,(byte)0x82,(byte)0xB0,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x0A,(byte)0x1B,(byte)0x61,(byte)0x00,
                (byte)0x1B,(byte)0x33,(byte)0x1E,(byte)0xDA,(byte)0xBC,(byte)0xB0,(byte)0xC4,(byte)0xCC,
                (byte)0xDF,(byte)0xD8,(byte)0xDD,(byte)0xC0,(byte)0xB0,(byte)0x0A,(byte)0x1B,(byte)0x21,
                (byte)0x20,(byte)0x50,(byte)0x52,(byte)0x50,(byte)0x2D,(byte)0x32,(byte)0x35,(byte)0x30,
                (byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,
                (byte)0x20,(byte)0x20,(byte)0x31,(byte)0x2E,(byte)0x30,(byte)0x30,(byte)0x0A,(byte)0x1B,
                (byte)0x21,(byte)0x00,(byte)0xB7,(byte)0xAC,(byte)0xAF,(byte)0xBC,(byte)0xAD,(byte)0xC4,
                (byte)0xDE,(byte)0xDB,(byte)0xB1,(byte)0xBD,(byte)0x0A,(byte)0x1B,(byte)0x21,(byte)0x20,
                (byte)0x45,(byte)0x57,(byte)0x2D,(byte)0x33,(byte)0x35,(byte)0x30,(byte)0x20,(byte)0x20,
                (byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,
                (byte)0x20,(byte)0x31,(byte)0x2E,(byte)0x30,(byte)0x30,(byte)0x1B,(byte)0x21,(byte)0x00,
                (byte)0x0A,(byte)0x43,(byte)0x43,(byte)0x44,(byte)0xCA,(byte)0xDE,(byte)0xB0,(byte)0xBA,
                (byte)0xB0,(byte)0xC4,(byte)0xDE,(byte)0xD8,(byte)0xB0,(byte)0xC0,(byte)0xDE,(byte)0xB0,
                (byte)0x0A,(byte)0x1B,(byte)0x21,(byte)0x20,(byte)0x54,(byte)0x53,(byte)0x4B,(byte)0x2D,
                (byte)0x65,(byte)0x28,(byte)0x55,(byte)0x53,(byte)0x29,(byte)0x20,(byte)0x20,(byte)0x20,
                (byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x32,(byte)0x2E,(byte)0x30,
                (byte)0x30,(byte)0x1B,(byte)0x21,(byte)0x00,(byte)0x0A,(byte)0xC0,(byte)0xAF,(byte)0xC1,
                (byte)0xCA,(byte)0xDF,(byte)0xC8,(byte)0xD9,(byte)0x50,(byte)0x43,(byte)0x0A,(byte)0x1B,
                (byte)0x21,(byte)0x20,(byte)0x57,(byte)0x61,(byte)0x76,(byte)0x65,(byte)0x50,(byte)0x6F,
                (byte)0x73,(byte)0x37,(byte)0x37,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,
                (byte)0x20,(byte)0x20,(byte)0x20,(byte)0x31,(byte)0x2E,(byte)0x30,(byte)0x30,(byte)0x0A,
                (byte)0x1B,(byte)0x21,(byte)0x00,(byte)0x1B,(byte)0x61,(byte)0x01,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,(byte)0x2D,
                (byte)0x2D,(byte)0x0A,(byte)0x1B,(byte)0x33,(byte)0x50,(byte)0x1B,(byte)0x61,(byte)0x00,
                (byte)0x1B,(byte)0x21,(byte)0x20,(byte)0x54,(byte)0x4F,(byte)0x54,(byte)0x41,(byte)0x4C,
                (byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,
                (byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x35,(byte)0x2E,(byte)0x30,(byte)0x30,
                (byte)0x1B,(byte)0x21,(byte)0x00,(byte)0x0A,(byte)0x1B,(byte)0x61,(byte)0x01,(byte)0x1B,
                (byte)0x33,(byte)0x32,(byte)0x1D,(byte)0x68,(byte)0x50,(byte)0x1D,(byte)0x77,(byte)0x03,
                (byte)0x1D,(byte)0x48,(byte)0x02,(byte)0x1D,(byte)0x66,(byte)0x00,(byte)0x1D,(byte)0x6B,
                (byte)0x45,(byte)0x0C,(byte)0x2A,(byte)0x30,(byte)0x31,(byte)0x30,(byte)0x32,(byte)0x34,
                (byte)0x38,(byte)0x38,(byte)0x32,(byte)0x32,(byte)0x35,(byte)0x2A,(byte)0x0A,(byte)0x1B,
                (byte)0x33,(byte)0x1E,(byte)0x1B,(byte)0x45,(byte)0x01,(byte)0x8A,(byte)0x94,(byte)0x8E,
                (byte)0xAE,(byte)0x89,(byte)0xEF,(byte)0x8E,(byte)0xD0,(byte)0x83,(byte)0x47,(byte)0x83,
                (byte)0x74,(byte)0x83,(byte)0x50,(byte)0x83,(byte)0x43,(byte)0x83,(byte)0x56,(byte)0x83,
                (byte)0x58,(byte)0x83,(byte)0x65,(byte)0x83,(byte)0x80,(byte)0x1B,(byte)0x45,(byte)0x00,
                (byte)0x0A,(byte)0x54,(byte)0x45,(byte)0x4C,(byte)0x3A,(byte)0x30,(byte)0x35,(byte)0x32,
                (byte)0x2D,(byte)0x39,(byte)0x30,(byte)0x39,(byte)0x2D,(byte)0x37,(byte)0x34,(byte)0x36,
                (byte)0x30,(byte)0x0A,(byte)0x81,(byte)0x47,(byte)0x34,(byte)0x36,(byte)0x32,(byte)0x2D,
                (byte)0x30,(byte)0x30,(byte)0x36,(byte)0x33,(byte)0x0A,(byte)0x1B,(byte)0x33,(byte)0x32,
                (byte)0x88,(byte)0xA4,(byte)0x92,(byte)0x6D,(byte)0x8C,(byte)0xA7,(byte)0x96,(byte)0xBC,
                (byte)0x8C,(byte)0xC3,(byte)0x89,(byte)0xAE,(byte)0x8E,(byte)0x73,(byte)0x96,(byte)0x6B,
                (byte)0x8B,(byte)0xE6,(byte)0x84,(byte)0xDB,(byte)0x90,(byte)0x56,(byte)0x92,(byte)0xAC,
                (byte)0x32,(byte)0x31,(byte)0x32,(byte)0x94,(byte)0xD4,(byte)0x92,(byte)0x6E,(byte)0x04,
                (byte)0x1D,(byte)0x28,(byte)0x6B,(byte)0x04,(byte)0x00,(byte)0x31,(byte)0x41,(byte)0x32,
                (byte)0x00,(byte)0x1D,(byte)0x28,(byte)0x6B,(byte)0x03,(byte)0x00,(byte)0x31,(byte)0x43,
                (byte)0x03,(byte)0x1D,(byte)0x28,(byte)0x6B,(byte)0x03,(byte)0x00,(byte)0x31,(byte)0x45,
                (byte)0x31,(byte)0x1D,(byte)0x28,(byte)0x6B,(byte)0x1B,(byte)0x00,(byte)0x31,(byte)0x50,
                (byte)0x30,(byte)0x68,(byte)0x74,(byte)0x74,(byte)0x70,(byte)0x3A,(byte)0x2F,(byte)0x2F,
                (byte)0x77,(byte)0x77,(byte)0x77,(byte)0x2E,(byte)0x66,(byte)0x6B,(byte)0x73,(byte)0x79,
                (byte)0x73,(byte)0x74,(byte)0x65,(byte)0x6D,(byte)0x2E,(byte)0x63,(byte)0x6F,(byte)0x6D,
                (byte)0x2F,(byte)0x1D,(byte)0x28,(byte)0x6B,(byte)0x03,(byte)0x00,(byte)0x31,(byte)0x51,
                (byte)0x30,(byte)0x0A,(byte)0x1D,(byte)0x56,(byte)0x41,(byte)0x32
        });
    }

    private byte[] concat(byte[]... bytes) {
        final ByteBuffer byteBuffer;
        {
            int sumByteLength = 0;
            for (byte[] aByte : bytes) {
                sumByteLength += aByte.length;
            }
            byteBuffer = ByteBuffer.allocate(sumByteLength);
            for (byte[] aByte : bytes) {
                byteBuffer.put(aByte);
            }
        }

        return byteBuffer.array();
    }

    public enum Direction{
        LEFT, CENTER, RIGHT
    }
}