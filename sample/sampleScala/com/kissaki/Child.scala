package sampleScala.com.kissaki

import com.kissaki.MessengerProtocol
import com.kissaki.TagValue
import com.kissaki.Messenger

/**
 * 子どもオブジェクト
 *
 * @author sassembla
 *
 */
class Child(masterName : String) extends MessengerProtocol {
	/**
	 * コンストラクタ
	 *
	 * 親の名前(文字列)を入力する
	 *
	 * @param masterName
	 */
	val messenger = new Messenger(this, "child")
	messenger.inputParent(masterName)

	/**
	 * Childのレシーバ
	 */
	def receiver(exec : String, tagValues : Array[TagValue]) = {
		exec match {
			// parentから呼ばれた
			case "callFromParent" => {
				println("myname is	" + messenger.getName
					+ " and my Id is " + messenger.getId)
				println(messenger.getName
					+ "received callFromParent")
			}

			// 孫(childにとっての子ども)へとメッセージを経由する
			case "callCousin" => {
				val grand_ma_sName = messenger.get("parentName", tagValues)

				messenger.call("cousin", "grand-mother call you, cousin.",
					messenger.tagValues(new TagValue("grand-ma'sName", grand_ma_sName)))
			}

			// cousinからのメッセージを親にスルー
			case "forward to grand'ma" => {
				messenger.callParent("to grand'ma from cousin", tagValues)
			}
		}

	}

}
