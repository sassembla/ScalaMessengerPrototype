package sampleJava.com.kissaki;

import com.kissaki.MessengerImplement;
import com.kissaki.MessengerProtocol;
import com.kissaki.TagValue;

/**
 * まご
 * @author sassembla
 *
 */
public class Cousin implements MessengerProtocol {
	MessengerImplement messenger;
	
	/**
	 * コンストラクタ
	 * 
	 * childと名付けられたMessengerを親として設定するように動作する。
	 * 親との接点は名前のみ。
	 */
	public Cousin() {
		messenger = new MessengerImplement(this, "cousin");
		messenger.inputParent("child");
	}

	/**
	 * 孫のレシーバ
	 */
	@Override
	public void receiver(String exec, TagValue[] tagValues) {
		//親(child1か2)からのメッセージを受けたら、折り返す
		if (exec.equals("grand-mother call you, cousin.")) {
			System.out.println("あっはいGrand'ma、こちら"+messenger.getMessengerName()+"です、折り返します！");
			messenger.callParent(
					"forward to grand'ma", 
					messenger.tagValue("message", "落ち込んだりもしたけれど、"+messenger.getMessengerName()+"は元気です")
					);
		}
	}
}
