package sampleJava.com.kissaki;

import com.kissaki.MessengerWrapper;
import com.kissaki.MessengerProtocol;
import com.kissaki.TagValue;

/**
 * 子どもオブジェクト
 * 
 * @author sassembla
 * 
 */
public class Child implements MessengerProtocol {
	MessengerWrapper messenger;

	/**
	 * コンストラクタ
	 * 
	 * 親の名前(文字列)を入力する
	 * 
	 * @param masterName
	 */
	public Child(String masterName) {
		messenger = new MessengerWrapper(this, "child");
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
		
		
		//parentから、1秒後に起こせと言われた
		if (exec.equals("please wake me up 1Sec later")) {
			//1秒待ちます(この間別のMessagingを行っても問題なく動きます)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			messenger.callParent("WAKE UP!", messenger.tagValue("childId", messenger.getMessengerID()));
		}
	}

}
