package sampleJava.com.kissaki;

import com.kissaki.MessengerWrapper;
import com.kissaki.MessengerProtocol;
import com.kissaki.TagValue;

/**
 * すべてのオブジェクトの親
 * 
 * @author sassembla
 * 
 */
public class Parent implements MessengerProtocol {
	MessengerWrapper messenger;

	// 子ども
	Child child1;
	Child child2;

	// 孫
	Cousin cousin1;

	public Parent() {
		messenger = new MessengerWrapper(this, "parent");

		child1 = new Child(messenger.getMessengerName());
		child2 = new Child(messenger.getMessengerName());

		cousin1 = new Cousin();

		// child1,2へとブロードキャスト
		messenger.call("child", "callFromParent");

		// child1を介してcousin1の呼び出し
		messenger.call("child", "callCousin",
				messenger.tagValue("parentName", messenger.getMessengerName()));
	}

	/**
	 * parentのレシーバ
	 */
	@Override
	public void receiver(String exec, TagValue[] tagValues) {
		if (exec.equals("to grand'ma from cousin")) {
			String message = messenger.getStr("message", tagValues);
			System.out.println("cousinからのmessageは、	\"" + message + "\"	です。");
		}
	}
}
