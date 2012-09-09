package sampleScala.com.kissaki

import com.kissaki.MessengerProtocol
import com.kissaki.TagValue
import com.kissaki.Messenger

/**
 * まご
 * @author sassembla
 *
 */
class Cousin extends MessengerProtocol {
	/**
	 * コンストラクタ
	 *
	 * childと名付けられたMessengerを親として設定するように動作する。
	 * 親との接点は名前のみ。
	 */

	val messenger = new Messenger(this, "cousin")
	messenger.inputParent("child")

	/**
	 * 孫のレシーバ
	 */
	def receiver(exec : String, tagValues : Array[TagValue]) {
		//親(child1か2)からのメッセージを受けたら、折り返す
		if (exec.equals("grand-mother call you, cousin.")) {
			println("あっはいGrand'ma、こちら" + messenger.getName + "です、折り返します！")
			messenger.callParent(
				"forward to grand'ma",
				messenger.tagValues(new TagValue("message", "落ち込んだりもしたけれど、" + messenger.getName + "は元気です")))
		}
	}
}
