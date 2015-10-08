## What's new.('2013.08.01 ) ##
FeliCaTag.readWithoutEncryptionでNullPointerExceptionが発生するバグを修正しました。(by tmshinza)

https://code.google.com/p/nfc-felica/source/detail?r=70


---


## What's new.('2012.01.25 ) ##
![http://cdn-ak.f.st-hatena.com/images/fotolife/K/Kazzz/20120126/20120126192916.png](http://cdn-ak.f.st-hatena.com/images/fotolife/K/Kazzz/20120126/20120126192916.png) ![http://cdn-ak.f.st-hatena.com/images/fotolife/K/Kazzz/20120126/20120126192915.png](http://cdn-ak.f.st-hatena.com/images/fotolife/K/Kazzz/20120126/20120126192915.png)

docomo GALAXY Nexus SC-04D(Android 4.0)での動作を確認しました。

プロジェクトの構成をADT Plugin for Eclipse r14以降のものに変更しました。



---



## What's new.('2011.09.14 ) ##
![http://cdn-ak.f.st-hatena.com/images/fotolife/K/Kazzz/20110805/20110805190514.png](http://cdn-ak.f.st-hatena.com/images/fotolife/K/Kazzz/20110805/20110805190514.png)
以下の変更を行いました

  * Brother社 RL-700S等で印字されるRFIDラベルの規格である、ISO15693(I-CODE SLI)のVICCの読み書きに対応しました


2.3.3系をbranchとして暫く残しますが、今後は保守しない予定です。

**nfc-felica**

  * ISO15693(I-CODE SLI)準拠のコマンドによりrawデータの読み書きが可能になりました。
  * 同様にISO15693(I-CODE SLI)のRFIDラベルに対してNDEFメッセージでの読み書きが可能になりました
  * Fragmentを使用しているため、コンパイル～実行にはサポートライブラリィ"android-support-v4.jar"が必要です。

**nfc-felica-lib**

ISO15693(I-CODE SLI)準拠のコマンドによりrawデータの読み書きに対応したクラスを追加しました。

以下のコマンドに関してのみテストしています。
  * SystemInformation
  * ReadSingleBlock
  * ReadMultipleBlock
  * WriteSingleBlock
  * WriteMultipleBlocl(WriteSingleBlockによるエミュレート)


**thanks @touchRL!**


---




## What's new.('2010.03.04 ) ##

各種バグFIXを行いました。

今回の修正により、2.3.3系をtrunk本流としました。

2.3.2系はbranchとして暫く残しますが、今後保守はしない予定です。

**nfc-felica-lib**
  * CommandPacketクラスでコマンド長が127バイトを超えた際に例外が発生するバグをFIXしました
  * 同クラスにおいてコマンド長が255バイトを超えた場合は例外をスローするように修正しました


**thanks @zaki50!**


---




## What's new.('2010.03.01 ) ##

各種アップデートを行いました。

**nfc-felica**
  * android 2.3.3用のブランチを作成しました。
  * サンプルアプリケーション(NfcFeliCaReader)がandroid 2.3.3 API Level 10に対応しました
  * nfc foreground dispatchを有効にしました
  * それぞれの処理をAsynchTaskにカプセル化しました
  * 処理中にProgressDialogを表示するようにしました


なお、次回のアップデート時には2.3.3ブランチをマージして一本化、以前のバージョンは廃止する予定です。


---



## What's new.('2010.02.25 ) ##

各種アップデートを行いました。

**nfc-felica-lib**
  * android 2.3.3のOTA開始に合わせて、2.3.3用のブランチを作成しました。
  * android.nfc.Tagクラスから@hideが取れてFeliCaはNfcFクラスで直接扱うことができるようになりました。結果リフレクションが不要になったのでNfcWrapperは除去しました。
  * FeliCaTag、FeliCaTagクラス内部のnfcTagフィールドの型をandroid.nfc.Tagクラスに変更しました

リポジトリは以下のURLからダウンロードできます。

https://nfc-felica.googlecode.com/svn/nfc-felica/branches/nfc-felica-lib-2.3.3/

なお、nfc-felicaプロジェクトに関しては変更はありません。依存ライブラリィnfc-felica-libを差し替えることでAndroid2.3.2以前と2.3.3を切り替えることができます。



---



## What's new.('2010.02.20 ) ##

各種アップデートを行いました。

**nfc-felica**

NFCFeliCaReader
  * FeliCa Liteの読みこみ/書き込みに対応しました。
  * FeliCa Liteのデータの書き込みのため、FeliCaLiteWriterアクティビティを追加しました。

**nfc-felica-lib**

  * FeliCa仕様とNfc仕様を分離するためnet.kazzz.nfcパッケージを追加しました
  * FeliCaLibクラスからリフレクションを用いているコードをNfcWrapperに分離しました。※
  * FeliCaLibクラスにMemoryConfigurationBlockクラスを追加しました
  * FeliCaクラスをFeliCaTagクラスに変更しました。
  * FeliCaLiteTagクラスを追加しました。(polling、readWithoutEncryption、writeWithoutEncryptionメソッドを実装)
  * FeliCaTagクラス、FeliCaLiteTagクラスのルートとしてNfcTagクラスを追加しました。
  * net.kazzz.felica.commandパッケージに WriteResponseクラスを追加しました。
  * net.kazzz.utilパッケージを追加、IPredicateインタフェース及びFinder等のユーティリティを追加しました


※なお、今回のアップデートに関しては、以前にコメント頂いたちきん(mokemokechicken)さんのコードを参考にさせて頂きました。
[AndroidのNFC機能でFeliCaの読み書きをする (ゆめ技：ゆめみスタッフブログ)](http://yumewaza.yumemi.co.jp/2011/02/androidnfcfelica.html)

**ちきん(mokemokechicken)さん、ありがとうございました。**


![http://cdn-ak.f.st-hatena.com/images/fotolife/K/Kazzz/20110222/20110222174127.png](http://cdn-ak.f.st-hatena.com/images/fotolife/K/Kazzz/20110222/20110222174127.png)


---



## What's new.('2010.02.07 ) ##

本プロジェクトのコードを"ライブラリィ"として使用するためプロジェクト"nfc-felica-lib"がリポジトリに追加されました。

https://nfc-felica.googlecode.com/svn/nfc-felica/trunk/nfc-felica-lib/

本プロジェクトのコミッタとして[YAMAZAKI Makoto氏](https://twitter.com/#!/zaki50)に参加頂きました。

ありがとうございました!


![http://cdn.f.st-hatena.com/images/fotolife/K/Kazzz/20101108/20110130102209.png](http://cdn.f.st-hatena.com/images/fotolife/K/Kazzz/20101108/20110130102209.png)

## nfc-felicaとは ##

Android 2.3からサポートされたNFC API(一部隠されたAPI)を使ってFeliCaカードにアクセスするためのクラス群を提供します。
FeliCa SDK等のライブラリィを必要とせずにFeliCaカードにアクセスし通信を行うことができます。

Rev5よりSuica/Pasmoのデータとサイバネ駅データベース(StationCode.db)により、使用履歴を日本語でフォーマットできるようになりました。


---





## サンプルアプリケーション(NFCFeliCaReader)の動かし方 ##

プロジェクトはそのままNFCFeliCaReaderという名前のアプリケーションになっています。
アプリケーションをインストール後、SDKのNfcDemo同様にFeliCaカードをNfc機能を搭載したAndroidスマートフォンに翳すことで、インテント"android.nfc.action.TAG\_DISCOVERED"がブロードキャストされますので、これを受信するActivityが起動してカードのデータを読み込みます。

Rev5よりタグ情報を読み込んだ後に「使用履歴」ボタンを押下することで、Suica/Pasmoの使用履歴を見ることができるようになりました。



---




## コードについて ##

FeliCa PICC側にどのようなデータを送っているかは、NFCFeliCaReader.javaを追うことで理解できると思います。(FeliCaコマンドをそのまま送っても駄目な理由も分かります)

処理自体は全て~~FeliCa.java~~FeliCaLib.javaで行っています。~~

Rev5で以下を更新しました。

  * Suica/PasmoのデータをフォーマットするSuicaクラスを追加
  * Suica/Pasmo/IRuCaにおける駅コード/名称データベース(StationCode.db)とそのユーティリティクラスDBUtilを追加
  * ユーティリティをUtilクラスに集約


---




## 注意事項 ##

**一部隠蔽(@hide)されたクラスとAPIを使用しているため、一切の予告なしにAndroid SDKの更新によって機能が使えなくなる可能性があります。自己責任で利用ください**

**本機能はAndroid 2.3を搭載し、NFCチップ(SAMSUNG NXP's PN544 NFC chip)を搭載したAndroidスマートフォン上でのみ動作します。(2011/01/30 現在、確認できているのは Nexus Sのみです)**

現時点(2011/01/30)においては以下のコマンドしか動作確認していません。

  * Polling
  * Request Service
  * Request Response
  * Read Without Encryption
  * Request System Code

**なお、Read/Write等認証(Authentication)が必要なコマンドは個別に契約が必要ですので本ライブラリィでは使えません。**

参考: FeliCa公式仕様

[FeliCaカード ユーザーズマニュアル](http://www.sony.co.jp/Products/felica/business/tech-support/data/fl_usmnl_1.2.pdf)

[FeliCa技術方式の各種コードについて](http://www.sony.co.jp/Products/felica/business/tech-support/data/code_descriptions_1.2.pdf)


---




### サイバネ駅データベース(StationCode.db)について ###

**全てのソースコードは記載通りApache 2.0ライセンスの元に再利用可能ですが、Suicaクラスからアクセスしているサイバネ駅コードデータベース(StationCode.db)の原データに関しては有志の皆様の無償協力のもとで作成されたものであり、対価の発生する配布、商用での利用は作成元のご意向で許可されておりません。(個人利用、無償で利用する分に関してはなんら制限はありません)
仮に本プロジェクトのコードを商用で利用する場合、データベースのデータを分離して再利用して頂くことになりますのでご注意ください。**

駅コード/店舗コード、サイバネ駅コードデータベースは以下のサイトで公開されているExcelデータをインポートして作成しました。

[IC SFCard Fan](http://www014.upp.so-net.ne.jp/SFCardFan/)

謹んで御礼申し上げます。


---






## 謝辞 ##
本プロジェクトとそのコードは以下の方々のご協力無しでは実現しませんでした。この場を借りてお礼申し上げます。


**若さん**([waka21](http://twitter.com/#!/waka21))
> 私がどうしても分からなかった部分を解決してくださいました。感謝しております。


**adamrockerさん**([Gingerbread(Android2.3)でNFC書出しへの道のり - throw Life](http://www.adamrocker.com/blog/313/nfc-write-with-nexuss.html))
> 今や我らのGoogle API Expertでらっしゃいます。Nfcの隠しクラスとそのAPIに関してBlogで詳しくまとめてくださったおかげでこのプロジェクトがあります。


**vvakameさん**([ABC2011WinterのためにもっとNFCについて調べた](http://tech.topgate.co.jp/android/nfc-abc2011w))
> vvakameさんの元気の良さとそのやる気にはいつも励まされます。ABC(Android Bazaar and Conference)のためにまとめた上記リンク先の資料が非常に参考になりました。


Suica/Pasmoのデータフォーマット解析に関しては以下のサイトを全面的に参考にさせて頂きました

[suica - FeliCa Library Wiki - SourceForge.JP](http://sourceforge.jp/projects/felicalib/wiki/suica)


TwitterのAndroidクラスタの方々にはいろいろと刺激されました。このプロジェクトは皆さんのおかげです。



---


※「FeliCa」は、ソニー株式会社が開発した非接触ICカードの技術方式です。

※「FeliCa」は、ソニー株式会社の登録商標です。

※ 本プロジェクトは SONYまたはFeliCa技術を用いるその他企業ともいかなる関係もありません。