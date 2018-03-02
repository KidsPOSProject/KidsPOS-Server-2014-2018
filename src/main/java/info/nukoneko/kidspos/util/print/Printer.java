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
        this.setGravityCenter();
        this.write(new byte[]{0x1d, 0x21, 0x11}); //文字を縦横2倍

        //タイトル
        this.text("キッズビジネスタウン");
        this.text("いちかわ");
        this.write(new byte[]{0x1d, 0x21, 0x00}); //文字を縦横1倍
        this.text("");
        this.setGravityCenter();
        this.text(new SimpleDateFormat("yyyy年MM月dd日(E) HH時mm分ss秒").format(new Date())); //日付時刻印字

        // 店舗名・担当者
        this.setGravityLeft();
        this.text("店舗名 : " + printObject.getStoreName());
        this.text("　担当 : " + printObject.getStaffName());
        this.drawLine();

        // 商品
        List<ModelItem> item = printObject.getItems();
        for( int i = 0; i < item.size(); i++) {
            this.textPrice(item.get(i).getName(), item.get(i).getPrice());
        }
        this.drawLine();

        //小計
        this.textPrice("ごうけい",printObject.getTotal());
        this.textPrice("おあずかり",printObject.getDeposit());
        this.textPrice("おつり",printObject.getChange());
        this.drawLine();

        //中央揃え
        this.setGravityCenter();

        // 注釈
        this.text("");
        this.text("印字保護のため、こちらの面を");
        this.text("内側に折って保管してください");
        this.text("");

        // バーコード
        this.barcode(printObject.getTransactionId());
        this.text(printObject.getTransactionId());
        this.cut();
    }

    /**
     * 文字列と金額を受け取り、1行のレコードを印字する
     * @param name 出力レコードの文字列（商品名など）
     * @param price 出力レコードの金額（価格など）
     */
    public void textPrice(String name, Integer price){
        Integer priceLength; //priceの文字数
        if(price == 0) priceLength = 1;
        else priceLength = (int)Math.log10(price) + 1;

        Integer marginByte = 38 - name.getBytes().length / 3 * 2 - name.getBytes().length % 3 - priceLength ;
        // 38 = 48（1行の最大半角文字数）- 4（両端の隙間）- 6（"リバー"）
        String marginSt = "";
        for(int sp = 0; sp < marginByte; sp++){
            marginSt = " " + marginSt;
        }
        this.text( "  " + name + marginSt + price + "リバー  ");
    }

    /**
     * 文字列を印字する（改行もする）
     * @param text
     */
    public void text(String text) {
        try {
            write(concat(text.getBytes("SJIS"), new byte[]{0x0A}));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文字列を"太字で"印字する
     * @param text
     */
    public void textWithBold(String text) {
        try {
            write(concat(new byte[]{0x1B, 0x45, 0x01}, text.getBytes("SJIS"), new byte[]{0x1B, 0x45, 0x00}, new byte[]{0x0A}));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * CODE39バーコードを印字する
     * @param code バーコードにする文字列
     */
    public void barcode(String code){
        byte[] codeByte = code.getBytes();
        write(new byte[]{0x1D,0x6B,0x45}); //CODE39指定
        write((byte)codeByte.length); //codeLength
        write(codeByte); //mainCode
        write(new byte[]{0x0A}); //改行（LF）
    }

    /**
     * 水平線を引く
     */
    public void drawLine(){
        this.text("────────────────────────"); //24文字
    }

    public void cut() {
        // 印刷するときの、下の余白が変なら 3バイト目を変えてみよう!
        write(new byte[]{0x1B, 0x64, (byte) 4});
        write(new byte[]{0x1D, 0x56, 0x30, 0x0});
    }

    /**
     * 以降の印字を左揃えにする
     */
    public void setGravityLeft() {
        write(new byte[]{0x1B, 0x61, 0x00});
    }

    /**
     * 以降の印字を中央揃えにする
     */
    public void setGravityCenter() {
        write(new byte[]{0x1B, 0x61, 0x01});
    }

    /**
     * 以降の印字を右揃えにする
     */
    public void setGravityRight() {
        write(new byte[]{0x1B, 0x61, 0x02});
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
}