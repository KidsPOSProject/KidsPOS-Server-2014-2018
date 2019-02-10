package info.nukoneko.kidspos.util.print

import java.io.UnsupportedEncodingException
import java.net.Socket
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

private const val printerCharset = "Shift_JIS"

private fun String.toByteArrayWithPrinterCharset(): ByteArray = toByteArray(charset(printerCharset))

class PrintOrder(private val ipAddress: String, private val port: Int) {
    private var commands: ByteArray = ByteArray(0)

    /**
     * printObjectを受け取り、レシートを印刷する（Cutも含む）
     * @param entity
     */
    fun print(entity: ReceiptEntity) {
        //初期化命令
        this.write(byteArrayOf(0x1B, 0x40)) //初期化
        this.write(byteArrayOf(0x1B, 0x33, 0x28)) //改行量設定
        this.setGravity(Direction.CENTER)
        //タイトル
        this.write(byteArrayOf(0x1c, 0x70, 0x01, 0x00))

        this.writeTextWithLine(SimpleDateFormat("yyyy年MM月dd日(E) HH時mm分ss秒").format(Date())) //日付時刻印字
        this.addNewLine()

        // 店舗名・担当者
        this.setGravity(Direction.LEFT)
        this.writeTextWithLine("店舗名 : " + entity.storeName)
        this.writeTextWithLine("　担当 : " + entity.staffName)
        this.drawLine()

        // 商品
        val item = entity.items
        for (i in item.indices) {
            this.writeRow(item[i].name, item[i].price)
        }
        this.drawLine()

        //小計
        this.writeRow("ごうけい", entity.total)
        this.writeRow("おあずかり", entity.deposit)

        this.writeRow("おつり", entity.change)
        this.drawLine()

        //中央揃え
        this.setGravity(Direction.CENTER)

        // 注釈
        this.addNewLine()
        this.writeTextWithLine("印字保護のため、こちらの面を")
        this.writeTextWithLine("内側に折って保管してください")
        this.addNewLine()

        // バーコード
        this.drawBarcode(entity.transactionId)
        this.cut()
        this.send()
    }

    /**
     * 文字列と金額を受け取り、1行のレコードを印字する
     * @param name 出力レコードの文字列（商品名など）
     * @param price 出力レコードの金額（価格など）
     */
    private fun writeRow(name: String, price: Int?) {
        val priceOrder: Int = if (price == 0)
            1
        else
            Math.log10(price!!.toDouble()).toInt() + 1

        this.write(byteArrayOf(0x1B, 0x24, 24, 0))
        this.writeText(name)
        this.write(byteArrayOf(0x1B, 0x24, (226 - priceOrder * 12).toByte(), 1))
        this.writeText(price.toString() + "リバー")
        this.addNewLine()
    }

    /**
     * 文字列を印字する
     * @param text
     */
    private fun writeText(text: String) {
        try {
            write(text.toByteArrayWithPrinterCharset())
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun writeTextWithLine(text: String) {
        try {
            write(text.toByteArrayWithPrinterCharset())
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        write(byteArrayOf(0x0A))
    }

    /**
     * 改行を行う
     */
    private fun addNewLine() {
        write(byteArrayOf(0x0A))
    }

    /**
     * 文字列を"太字で"印字する
     * @param text
     */
    private fun writeTextWithBold(text: String) {
        try {
            write(concat(byteArrayOf(0x1B, 0x45, 0x01), text.toByteArrayWithPrinterCharset(), byteArrayOf(0x1B, 0x45, 0x00)))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    /**
     * CODE39バーコードを印字する
     * @param code バーコードにする文字列
     */
    private fun drawBarcode(code: String) {
        var code = code
        val codeByte = code.toByteArray()
        write(byteArrayOf(0x1D, 0x68, 0x50)) //高さ設定 80(0x50) * 1dot(0.125mm) = 80dot(10mm)
        write(byteArrayOf(0x1D, 0x67, 0x02)) //モジュール幅設定 3(0x03) * 1dot(0.125mm) = 3dot ＜2から6＞
        write(byteArrayOf(0x1D, 0x48, 0x00)) //解説文字印字（印字しない）
        write(byteArrayOf(0x1D, 0x6B, 0x45)) //CODE39指定
        write(codeByte.size.toByte()) //codeLength
        write(codeByte)
        code = code.substring(1, code.length - 1)
        this.writeTextWithLine(code)
        write(byteArrayOf(0x0A)) //改行（LF）
    }

    /**
     * QRコードを印字する
     * @param code
     */
    private fun drawQR(code: String) {
        write(byteArrayOf(0x1D, 0x28, 0x6B, 3, 0, 49, 67, 5))// モジュール幅を5dotに設定
        write(byteArrayOf(0x1D, 0x28, 0x6B, (code.length + 3).toByte(), 0, 49, 80, 48))
        write(code.toByteArray())
        write(byteArrayOf(0x1D, 0x28, 0x6B, 3, 0, 49, 81, 48))
    }

    /**
     * 水平線を引く
     */
    private fun drawLine() {
        this.writeTextWithLine("────────────────────────") //24文字
    }

    private fun cut() {
        // 印刷するときの、下の余白が変なら 3バイト目を変えてみよう!
        write(byteArrayOf(0x1B, 0x64, 4.toByte()))
        write(byteArrayOf(0x1D, 0x56, 0x30, 0x0))
    }

    /**
     * 印字文字の位置揃え
     * @param dir LEFT,CENTER,RIGHT
     */
    private fun setGravity(dir: Direction) {
        when (dir) {
            PrintOrder.Direction.LEFT -> write(byteArrayOf(0x1B, 0x61, 0x00))
            PrintOrder.Direction.CENTER -> write(byteArrayOf(0x1B, 0x61, 0x01))
            PrintOrder.Direction.RIGHT -> write(byteArrayOf(0x1B, 0x61, 0x02))
        }
    }

    private fun write(commands: ByteArray) {
        appendCommand(commands)
    }

    private fun write(commands: Byte) {
        appendCommand(commands)
    }

    private fun send() {
        Socket(ipAddress, port).getOutputStream().use {
            it.write(commands)
        }
    }

    private fun printSample1() {
        write(byteArrayOf(0x1B.toByte(), 0x40.toByte(), 0x1B.toByte(), 0x74.toByte(), 0x01.toByte(), 0x1C.toByte(), 0x43.toByte(), 0x01.toByte(), 0x1B.toByte(), 0x61.toByte(), 0x01.toByte(), 0x1C.toByte(), 0x70.toByte(), 0x01.toByte(), 0x30.toByte(), 0x1B.toByte(), 0x45.toByte(), 0x01.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x20.toByte(), 0x30.toByte(), 0x30.toByte(), 0x46.toByte(), 0x4B.toByte(), 0x53.toByte(), 0x59.toByte(), 0x53.toByte(), 0x54.toByte(), 0x45.toByte(), 0x4D.toByte(), 0x3E.toByte(), 0x3E.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x00.toByte(), 0x1B.toByte(), 0x45.toByte(), 0x00.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x33.toByte(), 0x28.toByte(), 0x32.toByte(), 0x30.toByte(), 0x31.toByte(), 0x33.toByte(), 0x94.toByte(), 0x4E.toByte(), 0x31.toByte(), 0x30.toByte(), 0x8C.toByte(), 0x8E.toByte(), 0x32.toByte(), 0x38.toByte(), 0x93.toByte(), 0xFA.toByte(), 0x20.toByte(), 0x31.toByte(), 0x38.toByte(), 0x3A.toByte(), 0x34.toByte(), 0x31.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x61.toByte(), 0x00.toByte(), 0x1B.toByte(), 0x33.toByte(), 0x1E.toByte(), 0x4E.toByte(), 0x6F.toByte(), 0x2E.toByte(), 0x39.toByte(), 0x39.toByte(), 0x39.toByte(), 0x2D.toByte(), 0x58.toByte(), 0x58.toByte(), 0x2D.toByte(), 0x39.toByte(), 0x39.toByte(), 0x39.toByte(), 0x39.toByte(), 0x39.toByte(), 0x39.toByte(), 0x39.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x33.toByte(), 0x28.toByte(), 0x92.toByte(), 0x53.toByte(), 0x93.toByte(), 0x96.toByte(), 0x3A.toByte(), 0x97.toByte(), 0xE9.toByte(), 0x96.toByte(), 0xD8.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x61.toByte(), 0x01.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x82.toByte(), 0xA8.toByte(), 0x94.toByte(), 0x83.toByte(), 0x82.toByte(), 0xA2.toByte(), 0x8F.toByte(), 0xE3.toByte(), 0x82.toByte(), 0xB0.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x61.toByte(), 0x00.toByte(), 0x1B.toByte(), 0x33.toByte(), 0x1E.toByte(), 0xDA.toByte(), 0xBC.toByte(), 0xB0.toByte(), 0xC4.toByte(), 0xCC.toByte(), 0xDF.toByte(), 0xD8.toByte(), 0xDD.toByte(), 0xC0.toByte(), 0xB0.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x20.toByte(), 0x50.toByte(), 0x52.toByte(), 0x50.toByte(), 0x2D.toByte(), 0x32.toByte(), 0x35.toByte(), 0x30.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x31.toByte(), 0x2E.toByte(), 0x30.toByte(), 0x30.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x00.toByte(), 0xB7.toByte(), 0xAC.toByte(), 0xAF.toByte(), 0xBC.toByte(), 0xAD.toByte(), 0xC4.toByte(), 0xDE.toByte(), 0xDB.toByte(), 0xB1.toByte(), 0xBD.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x20.toByte(), 0x45.toByte(), 0x57.toByte(), 0x2D.toByte(), 0x33.toByte(), 0x35.toByte(), 0x30.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x31.toByte(), 0x2E.toByte(), 0x30.toByte(), 0x30.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x00.toByte(), 0x0A.toByte(), 0x43.toByte(), 0x43.toByte(), 0x44.toByte(), 0xCA.toByte(), 0xDE.toByte(), 0xB0.toByte(), 0xBA.toByte(), 0xB0.toByte(), 0xC4.toByte(), 0xDE.toByte(), 0xD8.toByte(), 0xB0.toByte(), 0xC0.toByte(), 0xDE.toByte(), 0xB0.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x20.toByte(), 0x54.toByte(), 0x53.toByte(), 0x4B.toByte(), 0x2D.toByte(), 0x65.toByte(), 0x28.toByte(), 0x55.toByte(), 0x53.toByte(), 0x29.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x32.toByte(), 0x2E.toByte(), 0x30.toByte(), 0x30.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x00.toByte(), 0x0A.toByte(), 0xC0.toByte(), 0xAF.toByte(), 0xC1.toByte(), 0xCA.toByte(), 0xDF.toByte(), 0xC8.toByte(), 0xD9.toByte(), 0x50.toByte(), 0x43.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x20.toByte(), 0x57.toByte(), 0x61.toByte(), 0x76.toByte(), 0x65.toByte(), 0x50.toByte(), 0x6F.toByte(), 0x73.toByte(), 0x37.toByte(), 0x37.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x31.toByte(), 0x2E.toByte(), 0x30.toByte(), 0x30.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x00.toByte(), 0x1B.toByte(), 0x61.toByte(), 0x01.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x2D.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x33.toByte(), 0x50.toByte(), 0x1B.toByte(), 0x61.toByte(), 0x00.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x20.toByte(), 0x54.toByte(), 0x4F.toByte(), 0x54.toByte(), 0x41.toByte(), 0x4C.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x20.toByte(), 0x35.toByte(), 0x2E.toByte(), 0x30.toByte(), 0x30.toByte(), 0x1B.toByte(), 0x21.toByte(), 0x00.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x61.toByte(), 0x01.toByte(), 0x1B.toByte(), 0x33.toByte(), 0x32.toByte(), 0x1D.toByte(), 0x68.toByte(), 0x50.toByte(), 0x1D.toByte(), 0x77.toByte(), 0x03.toByte(), 0x1D.toByte(), 0x48.toByte(), 0x02.toByte(), 0x1D.toByte(), 0x66.toByte(), 0x00.toByte(), 0x1D.toByte(), 0x6B.toByte(), 0x45.toByte(), 0x0C.toByte(), 0x2A.toByte(), 0x30.toByte(), 0x31.toByte(), 0x30.toByte(), 0x32.toByte(), 0x34.toByte(), 0x38.toByte(), 0x38.toByte(), 0x32.toByte(), 0x32.toByte(), 0x35.toByte(), 0x2A.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x33.toByte(), 0x1E.toByte(), 0x1B.toByte(), 0x45.toByte(), 0x01.toByte(), 0x8A.toByte(), 0x94.toByte(), 0x8E.toByte(), 0xAE.toByte(), 0x89.toByte(), 0xEF.toByte(), 0x8E.toByte(), 0xD0.toByte(), 0x83.toByte(), 0x47.toByte(), 0x83.toByte(), 0x74.toByte(), 0x83.toByte(), 0x50.toByte(), 0x83.toByte(), 0x43.toByte(), 0x83.toByte(), 0x56.toByte(), 0x83.toByte(), 0x58.toByte(), 0x83.toByte(), 0x65.toByte(), 0x83.toByte(), 0x80.toByte(), 0x1B.toByte(), 0x45.toByte(), 0x00.toByte(), 0x0A.toByte(), 0x54.toByte(), 0x45.toByte(), 0x4C.toByte(), 0x3A.toByte(), 0x30.toByte(), 0x35.toByte(), 0x32.toByte(), 0x2D.toByte(), 0x39.toByte(), 0x30.toByte(), 0x39.toByte(), 0x2D.toByte(), 0x37.toByte(), 0x34.toByte(), 0x36.toByte(), 0x30.toByte(), 0x0A.toByte(), 0x81.toByte(), 0x47.toByte(), 0x34.toByte(), 0x36.toByte(), 0x32.toByte(), 0x2D.toByte(), 0x30.toByte(), 0x30.toByte(), 0x36.toByte(), 0x33.toByte(), 0x0A.toByte(), 0x1B.toByte(), 0x33.toByte(), 0x32.toByte(), 0x88.toByte(), 0xA4.toByte(), 0x92.toByte(), 0x6D.toByte(), 0x8C.toByte(), 0xA7.toByte(), 0x96.toByte(), 0xBC.toByte(), 0x8C.toByte(), 0xC3.toByte(), 0x89.toByte(), 0xAE.toByte(), 0x8E.toByte(), 0x73.toByte(), 0x96.toByte(), 0x6B.toByte(), 0x8B.toByte(), 0xE6.toByte(), 0x84.toByte(), 0xDB.toByte(), 0x90.toByte(), 0x56.toByte(), 0x92.toByte(), 0xAC.toByte(), 0x32.toByte(), 0x31.toByte(), 0x32.toByte(), 0x94.toByte(), 0xD4.toByte(), 0x92.toByte(), 0x6E.toByte(), 0x04.toByte(), 0x1D.toByte(), 0x28.toByte(), 0x6B.toByte(), 0x04.toByte(), 0x00.toByte(), 0x31.toByte(), 0x41.toByte(), 0x32.toByte(), 0x00.toByte(), 0x1D.toByte(), 0x28.toByte(), 0x6B.toByte(), 0x03.toByte(), 0x00.toByte(), 0x31.toByte(), 0x43.toByte(), 0x03.toByte(), 0x1D.toByte(), 0x28.toByte(), 0x6B.toByte(), 0x03.toByte(), 0x00.toByte(), 0x31.toByte(), 0x45.toByte(), 0x31.toByte(), 0x1D.toByte(), 0x28.toByte(), 0x6B.toByte(), 0x1B.toByte(), 0x00.toByte(), 0x31.toByte(), 0x50.toByte(), 0x30.toByte(), 0x68.toByte(), 0x74.toByte(), 0x74.toByte(), 0x70.toByte(), 0x3A.toByte(), 0x2F.toByte(), 0x2F.toByte(), 0x77.toByte(), 0x77.toByte(), 0x77.toByte(), 0x2E.toByte(), 0x66.toByte(), 0x6B.toByte(), 0x73.toByte(), 0x79.toByte(), 0x73.toByte(), 0x74.toByte(), 0x65.toByte(), 0x6D.toByte(), 0x2E.toByte(), 0x63.toByte(), 0x6F.toByte(), 0x6D.toByte(), 0x2F.toByte(), 0x1D.toByte(), 0x28.toByte(), 0x6B.toByte(), 0x03.toByte(), 0x00.toByte(), 0x31.toByte(), 0x51.toByte(), 0x30.toByte(), 0x0A.toByte(), 0x1D.toByte(), 0x56.toByte(), 0x41.toByte(), 0x32.toByte()))
    }

    private fun appendCommand(byte: Byte) {
        appendCommand(byteArrayOf(byte))
    }

    private fun appendCommand(bytes: ByteArray) {
        val currentSize = commands.size
        val result = ByteArray(currentSize + bytes.size)
        System.arraycopy(commands, 0, result, 0, currentSize)
        System.arraycopy(bytes, 0, result, currentSize, bytes.size)
        commands = result
    }

    private fun concat(vararg bytes: ByteArray): ByteArray {
        val byteBuffer: ByteBuffer
        run {
            var sumByteLength = 0
            for (aByte in bytes) {
                sumByteLength += aByte.size
            }
            byteBuffer = ByteBuffer.allocate(sumByteLength)
            for (aByte in bytes) {
                byteBuffer.put(aByte)
            }
        }

        return byteBuffer.array()
    }

    enum class Direction {
        LEFT, CENTER, RIGHT
    }
}