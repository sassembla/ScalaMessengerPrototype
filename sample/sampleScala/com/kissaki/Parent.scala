package sampleScala.com.kissaki

import com.kissaki.MessengerProtocol
import com.kissaki.TagValue
import com.kissaki.Messenger

/**
 * すべてのオブジェクトの親
 *
 * @author sassembla
 *
 */
class Parent extends MessengerProtocol {
	
	val messenger = new Messenger(this, "parent")

	// 子ども
	val child1 = new Child(messenger.getName)
	val child2 = new Child(messenger.getName)

	// 孫
	val cousin1 = new Cousin()

	// child1,2へとブロードキャスト
	messenger.call("child", "callFromParent", null)

	// child1を介してcousin1の呼び出し
	messenger.call("child", "callCousin",
		messenger.tagValues(new TagValue("parentName", messenger.getName))
		)
		
	/**
	 * parentのレシーバ
	 */
	def receiver(exec : String, tagValues : Array[TagValue]) = {
		exec match {
			case "to grand'ma from cousin" => {
				val message = messenger.get("message", tagValues)
				println("cousinからのmessageは、	\"" + message + "\"	です。")
			}
		}
	}
}


