/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kazzz.felica;

import static net.kazzz.felica.lib.FeliCaLib.*;
import net.kazzz.felica.command.PollingResponse;
import net.kazzz.felica.lib.FeliCaLib;
import net.kazzz.felica.lib.FeliCaLib.CommandPacket;
import net.kazzz.felica.lib.FeliCaLib.CommandResponse;
import net.kazzz.felica.lib.FeliCaLib.IDm;
import net.kazzz.felica.lib.FeliCaLib.PMm;
import android.os.Parcelable;

/**
 * IFeliCaのデフォルト実装クラスを提供します
 * 
 * @author Kazzz
 * @date 2011/01/23
 * @since Android API Level 9
 *
 */

public class FeliCa implements IFeriCa {
     // システムコード
    public static final int SYSTEMCODE_ANY = 0xffff;       // ANY
    public static final int SYSTEMCODE_COMMON = 0xfe00;    // 共通領域
    public static final int SYSTEMCODE_CYBERNE = 0x0003;   // サイバネ領域
    public static final int SYSTEMCODE_EDY = 0xfe00;       // Edy (=共通領域)
    public static final int SYSTEMCODE_SUICA = 0x0003;     // Suica (=サイバネ領域)
    public static final int SYSTEMCODE_PASMO = 0x0003;     // Pasmo (=サイバネ領域)
    
    protected final Parcelable tagService;
    protected IDm idm;
    protected PMm pmm;
    /**
     * コンストラクタ
     * 
     * @param tagService NFCTagサービスへの参照をセット
     */
    public FeliCa(Parcelable tagService) {
        this.tagService =  tagService;
    }
    /* (non-Javadoc)
     * @see net.kazzz.felica.IFeriCa#polling(short)
     */
    @Override
    public void polling(int systemCode) throws FeliCaException {
        if ( this.tagService == null ) {
            throw new FeliCaException("tagService is null. no polling execution");
        }
        CommandPacket polling = 
            new CommandPacket(COMMAND_POLLING
                    , new byte[] {
                      (byte) (systemCode & 0xff) // システムコード
                    , (byte) (systemCode >> 8)
                    , (byte) 0x01              //　システムコードリクエスト
                    , (byte) 0x00});           // タイムスロット}; 
        CommandResponse r = FeliCaLib.execute(this.tagService, polling);
        PollingResponse pr = new PollingResponse(r);
        this.idm = pr.getIDm();
        this.pmm = pr.getPMm();
    }
    /* (non-Javadoc)
     * @see net.kazzz.felica.IFeriCa#getIDm()
     */
    @Override
    public IDm getIDm() throws FeliCaException {
        return this.idm;
    }
    /* (non-Javadoc)
     * @see net.kazzz.felica.IFeriCa#getPMm()
     */
    @Override
    public PMm getPMm() throws FeliCaException {
        return this.pmm;
    }
    /* (non-Javadoc)
     * @see net.kazzz.felica.IFeriCa#readWithoutEncryption(short, byte)
     */
    @Override
    public byte[] readWithoutEncryption(int serviceCode,
            byte addr) throws FeliCaException {
        if ( this.tagService == null ) {
            throw new FeliCaException("tagService is null. no read execution");
        }
        // search read without encryption (利用履歴)
        CommandPacket readWoEncrypt = 
            new CommandPacket(COMMAND_READ_WO_ENCRYPTION, idm
                ,  new byte[]{(byte) 0x01         // サービス数
                    , (byte) (serviceCode & 0xff) // サービスコード (little endian)
                    , (byte) (serviceCode >> 8)
                    , (byte) 0x01                 // 同時読み込みブロック数
                    , (byte) 0x80, (byte) 0x00, (byte) 0x00 });// ブロックリスト
        CommandResponse r = FeliCaLib.execute(this.tagService, readWoEncrypt);
        return r.getBytes();
   }
    /* (non-Javadoc)
     * @see net.kazzz.felica.IFeriCa#writeWWithoutEncryption(short, byte, byte[])
     */
    @Override
    public int writeWithoutEncryption(int serviceCode,
            byte addr, byte[] buff) throws FeliCaException {
        if ( this.tagService == null ) {
            throw new FeliCaException("tagService is null. no read execution");
        }
        // search read without encryption (利用履歴)
        CommandPacket writeWoEncrypt = 
            new CommandPacket(COMMAND_WRITE_WO_ENCRYPTION, idm
                , new byte[]{(byte) 0x01          // Number of Service
                    , (byte) (serviceCode & 0xff) // サービスコード (little endian)
                    , (byte) (serviceCode >> 8)
                    , (byte) buff.length          // 同時書き込みブロック数
                    , (byte) 0x80, (byte) 0x00, (byte) 0x00 });// ブロックリスト
        CommandResponse r = FeliCaLib.execute(this.tagService, writeWoEncrypt);
        return r.getBytes()[9] == 0 ? 0 : -1;
    }
    
}
