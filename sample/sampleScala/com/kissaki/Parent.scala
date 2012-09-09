package sampleScala.com.kissaki

import com.kissaki.MessengerImplement
import com.kissaki.MessengerProtocol
import com.kissaki.TagValue

/**
 * すべてのオブジェクトの親
 *
 * @author sassembla
 *
 */
class Parent extends MessengerProtocol {

	// 子ども
	val messenger = new MessengerImplement(this, "parent")

	// 孫
	val child1 = new Child(messenger.getMessengerName())
	val child2 = new Child(messenger.getMessengerName())

	val cousin1 = new Cousin()

	// child1,2へとブロードキャスト
	messenger.call("child", "callFromParent")

	// child1を介してcousin1の呼び出し
	messenger.call("child", "callCousin",
		messenger.tagValue("parentName", messenger.getMessengerName()))

	/**
	 * parentのレシーバ
	 */
	def receiver(exec : String, tagValues : Array[TagValue]) {
		if (exec.equals("to grand'ma from cousin")) {
			val message = messenger.getStr("message", tagValues)
			println("cousinからのmessageは、	\"" + message + "\"	です。")
		}
	}
}
