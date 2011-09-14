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
package net.kazzz;

import net.kazzz.AbstractNfcTagFragment.INfcTagListener;
import net.kazzz.felica.FeliCaException;
import net.kazzz.felica.NfcFeliCaTagFragment;
import net.kazzz.felica.lib.FeliCaLib;
import net.kazzz.iso15693.ISO15693TagFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class NFCTagReader extends FragmentActivity implements OnClickListener, INfcTagListener {
    private String TAG = "NFCTagReader";
    private AbstractNfcTagFragment mLastFragment;
    private NfcFeliCaTagFragment mFeliCafragment;
    private ISO15693TagFragment mISO15693Fragment;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //IMEを自動起動しない
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        setContentView(R.layout.main);

        //使用するタグフラグメントを登録
        
        //FeliCa, FeliCaLite
        mFeliCafragment = new NfcFeliCaTagFragment(this);
        mFeliCafragment.addNfcTagListener(this);
        
        //ISO15693
        mISO15693Fragment = new ISO15693TagFragment(this);
        mISO15693Fragment.addNfcTagListener(this);

        //インテントから起動された際の処理
        Intent intent = this.getIntent();
        this.onNewIntent(intent);
    }
    
    public void onClick(final View v) {
        try {
            final int id = v.getId();
            
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);

            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                @Override
                protected void onPreExecute() {
                    switch (id) {
                    case R.id.btn_read:
                        dialog.setMessage("読み込み処理を実行中です...");
                        break;
                    case R.id.btn_write:
                        dialog.setMessage("書き込み画面に移動中です...");
                        break;
                    case R.id.btn_hitory:
                        dialog.setMessage("使用履歴を読み込み中です...");
                        break;
                    }
                    dialog.show();
                }

                @Override
                protected String doInBackground(Void... arg0) {
                    switch (id) {
                    case R.id.btn_read:
                        try {
                            return mLastFragment.dumpTagData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.btn_write:
                        try {
                            Intent intent = 
                                new Intent(NFCTagReader.this, NFCTagWriter.class);
                            intent.putExtra("nfcTag", mLastFragment.getNfcTag());
                            startActivity(intent);
                            return "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.btn_hitory:
                        try {
                            if ( mLastFragment != null && mLastFragment instanceof NfcFeliCaTagFragment) {
                                NfcFeliCaTagFragment nfcf = (NfcFeliCaTagFragment)mLastFragment;
                                return nfcf.dumpFeliCaHistoryData();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                    }
                    return "";
                }

                /* (non-Javadoc)
                 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
                 */
                @Override
                protected void onPostExecute(String result) {
                    dialog.dismiss();
                    TextView tv_tag = (TextView) findViewById(R.id.result_tv);
                    if (result != null && result.length() > 0) tv_tag.setText(result);
                }
            };
            
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        
        if ( mFeliCafragment != null ) {
            mFeliCafragment.onNewIntent(intent);
        }
        if ( mISO15693Fragment != null ) {
            mISO15693Fragment.onNewIntent(intent);
        }
    }
    /* (non-Javadoc)
     * @see net.kazzz.NfcTagFragment.INfcTagListener#onTagDiscovered(android.content.Intent, android.os.Parcelable)
     */
    @Override
    public void onTagDiscovered(Intent intent, Parcelable nfcTag, AbstractNfcTagFragment fragment) {
       //TextView tv_tag = (TextView) findViewById(R.id.result_tv);

       Button btnRead = (Button) findViewById(R.id.btn_read);
       btnRead.setOnClickListener(this);
       
       Button btnHistory = (Button) findViewById(R.id.btn_hitory);
       btnHistory.setOnClickListener(this);
       btnHistory.setEnabled(false);

       Button btnWrite = (Button) findViewById(R.id.btn_write);
       btnWrite.setOnClickListener(this);
       btnWrite.setEnabled(false);

       Button btnInout = (Button) findViewById(R.id.btn_inout);
       btnInout.setOnClickListener(this);
       btnInout.setEnabled(false);

       try {
           
           mLastFragment = fragment;
           
           //フラグメントの判定
           if ( mLastFragment instanceof NfcFeliCaTagFragment ) {
               NfcFeliCaTagFragment nff = (NfcFeliCaTagFragment)mLastFragment;
               boolean isFeliCatLite = nff.isFeliCaLite();
               try {
                   FeliCaLib.IDm idm = 
                       new FeliCaLib.IDm(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));

                   if ( idm == null ) {
                       throw new FeliCaException("Felica IDm を取得できませんでした");
                   }
                   
                   btnHistory.setEnabled(!isFeliCatLite);
                   btnWrite.setEnabled(isFeliCatLite);
                   
                   btnRead.performClick();
               } catch (Exception e) {
                   e.printStackTrace();
                   Log.e(TAG, e.toString());
               }
               btnHistory.setEnabled(!isFeliCatLite);
               btnWrite.setEnabled(isFeliCatLite);    
           } else {
               btnRead.performClick();
               btnHistory.setEnabled(false);
               btnWrite.setEnabled(true);   
           }
           
       } catch ( Exception e ) {
           e.printStackTrace();
           Log.e(TAG, e.toString());
       }
    }
}
