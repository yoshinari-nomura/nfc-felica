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
package net.kazzz.task;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Locale;

import net.kazzz.felica.lib.R;
import net.kazzz.iso15693.ISO15693Exception;
import net.kazzz.iso15693.ISO15693Tag;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.TagTechnology;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ISO15693 タグデータを書き出すための非同期タスククラスを提供します
 * 
 * @author Kazzz
 * @date 2011/08/05
 * @since Android API Level 10
 *
 */

public class ISO15693WriteTask extends AsyncTask<Void, Void, String> {
    final static String TAG =  ISO15693WriteTask.class.getSimpleName();
    final Activity mActivity;
    final Tag mNfcTag;
    final ProgressDialog mDialog;
    final boolean mUseNDEF;
    
    /**
     * コンストラクタ
     * @param activity アクティビティをセット
     * @param nfcTag NFCTagをセット
     * @param useNDEF NDEFフォーマットを使用する場合はtrueをセット
     */
    public ISO15693WriteTask(Activity activity, Tag nfcTag, boolean useNDEF) {
        mActivity = activity;
        mNfcTag = nfcTag;
        mUseNDEF = useNDEF;
        mDialog = new ProgressDialog(mActivity);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);
    }
    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        mDialog.setMessage("書き込み処理を実行中です...");
        mDialog.show();
    }
    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(Void... params) {
        if ( mUseNDEF ) {
            return writeNDEF();
        } else {
            return writeBare();
        }
    }
    /**
     * NDEFデータで書き込む
     * @return 書き込んだ文字列が戻る
     */
    private String writeNDEF() {
        final EditText editWrite = (EditText) mActivity.findViewById(R.id.edit_write);
        final CharSequence cData = editWrite.getText();
        
        //NDEFRecordを生成
        Charset utfEncoding = Charset.forName("UTF-8");
        Locale locale = Locale.getDefault();
        
        final byte[] langBytes = locale.getLanguage().getBytes(utfEncoding);
        final byte[] textBytes = cData.toString().getBytes(utfEncoding);
        final int utfBit = 0;
        final char status = (char) (utfBit + langBytes.length);
        
        ByteBuffer buff = ByteBuffer.allocate(1 + langBytes.length + textBytes.length);
        final byte[] data =  buff.put((byte) status).put(langBytes).put(textBytes).array();
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN
                , NdefRecord.RTD_TEXT, new byte[0], data);
        try {
            NdefRecord[] records = {record};
            NdefMessage message = new NdefMessage(records);
            
            //一度もNDEFで書き込んでいない場合はNdefFormatable、一度書き込んだ場合はNdefとなる
            //NdefFormatableはNdefに包含されるので、先にNdefかどうかを判断する
            TagTechnology tag = Ndef.get(mNfcTag);
            if ( tag == null ) {
                tag = NdefFormatable.get(mNfcTag);
                tag.connect();
                ((NdefFormatable)tag).format(message);
                tag.close();
            } else {
                tag.connect();
                ((Ndef)tag).writeNdefMessage(message);
                tag.close();
            }
            return cData.toString();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "writeData", e);
            Toast.makeText(mActivity
                    , "書きこみ失敗 : " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }                        
    }
    /**
     * 生データで書き込む
     * @return 書き込んだ文字列が戻る
     */
    private String writeBare() {
        try {
            final EditText editWrite = (EditText) mActivity.findViewById(R.id.edit_write);
            final CharSequence cData = editWrite.getText();
            SparseBooleanArray arry = (SparseBooleanArray)editWrite.getTag();
            
            int firstBlockNumber = 0;
            int numberOfBlocks = 0; 
            boolean setFirstBlockNumber = false;
            for ( int i = 0; i < arry.size(); i++ ) {
                int key = arry.keyAt(i);
                //選択された行 = 選択されたブロック番号
                if ( !setFirstBlockNumber ) {
                    firstBlockNumber = key;
                    setFirstBlockNumber = true;
                }
                numberOfBlocks++;
            }
            //データはUTF-8でエンコード
            Charset utfEncoding = Charset.forName("UTF-8");
            String result = cData.toString();
            byte[] bytes = result.getBytes(utfEncoding);
            
            ISO15693Tag tag = new ISO15693Tag(mNfcTag);
            if ( !tag.writeMultipleBlocks((byte)firstBlockNumber
                    , (byte)numberOfBlocks, bytes).hasError()) {
                return result;
            } else {
                return null; 
            }
        } catch (ISO15693Exception e) {
            e.printStackTrace();
            Log.e(TAG, "writeData", e);
            Toast.makeText(mActivity
                    , "書きこみ失敗 : " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        if ( mDialog != null ) mDialog.dismiss();
        Toast.makeText(mActivity
                , "書きこみ成功 : " + result.toString() , Toast.LENGTH_LONG).show();
        
        //終了して自身を起動 (リフレッシュ)
        mActivity.finish();
        Intent intent = new Intent(mActivity, mActivity.getClass());
        intent.putExtra("nfcTag", mNfcTag);
        mActivity.startActivity(intent);
    }
}
