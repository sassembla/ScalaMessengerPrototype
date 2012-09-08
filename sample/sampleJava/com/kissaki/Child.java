package sampleJava.com.kissaki;

import com.kissaki.MessengerImplement;
import com.kissaki.MessengerProtocol;
import com.kissaki.TagValue;

/**
 * 子どもオブジェクト
 * 
 * @author sassembla
 * 
 */
public class Child implements MessengerProtocol {
	MessengerImplement messenger;

	/**
	 * コンストラクタ
	 * 
	 * 親の名前(文字列)を入力する
	 * 
	 * @param masterName
	 */
	public Child(String masterName) {
		messenger = new MessengerImplement(this, "child");
		messenger.inputParent(masterName);
	}

	/**
	 * Childのレシーバ
	 */
	@Override
	public void receiver(String exec, TagValue[] tagValues) {

		// parentから呼ばれた
		if (exec.equals("callFromParent")) {
			System.out.println("myname is	" + messenger.getMessengerName()
					+ " and my Id is " + messenger.getMessengerID());
			System.out.println(messenger.getMessengerName()
					+ "received callFromParent");
		}

		// 孫(childにとっての子ども)へとメッセージを経由する
		if (exec.equals("callCousin")) {
			String grand_ma_sName = messenger.getStr("parentName", tagValues);

			messenger.call("cousin", "grand-mother call you, cousin.",
					messenger.tagValue("grand-ma'sName", grand_ma_sName));
		}

		// cousinからのメッセージを親にスルー
		if (exec.equals("forward to grand'ma")) {
			messenger.callParent("to grand'ma from cousin", tagValues);
		}
	}

}