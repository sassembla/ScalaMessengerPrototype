package com.kissaki


/**
 * Akka系の実装ではない、ちょっと残念なMessenger
 * 
 * @author sassembla
 */


//import akka.actor._
import scala.actors.Actor._
import scala.actors._
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

//import akka.util.Timeout
//import akka.dispatch.Await
//import akka.pattern.ask

/*
 * システム系のcase class
 */
case class MessengerJoin(actor : MessengerActor)
case class MessengerRemove(actor : MessengerActor)

/*
 * system series
 */
case class InputParent(parentCandidateName : String, child : MessengerActor)
case class ChildInput(child : MessengerActor)
case class ParentAccepted(parent : MessengerActor)
case class ChildAccepted(child : MessengerActor)
case class ChildNotAccepted(child : MessengerActor)
case class ParentChildDone()
case class ParentChildNotDone()

/*
 * call series
 */
case class Call(exec : String, message : Array[TagValue])
case class CallMyself(exec : String, message : Array[TagValue])
case class CallParent(exec : String, message : Array[TagValue])

/*
 * result
 */
case class Done(detail : String)
case class Failure(reason : String)

/*
 * result
 */
case class Result(result : String)

/**
 * メッセンジャー実装
 *
 * Actorのコントロール/receiverの着火を行う
 */
class Messenger (myself : MessengerProtocol, nameInput : String) {

	val self : MessengerProtocol = myself

	/*
	 * シングルトンの生成、centralActorの参照の取得
	 * actorの埋め込み(ActorのLinkみたいなのが出来るといいんだけどね。受け取り側で判断させられる)
	 */
	val centralActorImpl = MessengerCore.centralActor

	val actorImpl = new MessengerActor(self, nameInput)
	actorImpl.start
	actorImpl.log += Log.LOG_TYPE_INITIALIZED.toString
	/*
	 * シングルトンへのActorの追加
	 * actorの埋め込み(ActorのLinkみたいなのが出来るといいんだけどね。)
	 */
	val future = centralActorImpl !! MessengerJoin(actorImpl)
	val result = future()

	/**
	 * このMessengerを閉じる
	 */
	def close {
		val future = centralActorImpl !! MessengerRemove(actorImpl)
		val result = future()

		val future2 = actorImpl !! World.WORLD_MESSAGE_MEMBER_EXIT
		val result2 = future2()
	}

	/**
	 * 系全体のクローズを行う
	 */
	def closeSystem {
		MessengerCore.closeSystem
	}

	def getName : String = actorImpl.name
	def getId : String = actorImpl.id

	def getParentName : String = actorImpl.parentName
	def getParentId : String = actorImpl.parentId

	def hasChild : Boolean = !actorImpl.childList.isEmpty
	def hasParent : Boolean = !actorImpl.parent.isEmpty

	/**
	 * 親の名前入力
	 */
	def inputParent(inputParentName : String) : Result = {
		if (actorImpl.parent.isEmpty) {
			/*
			 * 自前のActorへとインプット
			 */
			val future = centralActorImpl !! InputParent(inputParentName, actorImpl)
			val result = future()

			result match {
				case Done(message) => Result(message)
				case Failure(reason) => Result(reason)
			}
		} else {
			Result(actorImpl.name + " aleady has parent that named " + actorImpl.parent(0).name)
		}

	}

	/**
	 * log
	 */
	def addLog(input : String) = { actorImpl.log += input }
	def getLog : ListBuffer[String] = actorImpl.log
	def getLogAsJava : java.util.List[String] = {
		actorImpl.log.asJava
	}

	def getCentralActorSize : Int = {
		centralActorImpl.actorList.length
	}

	/**
	 * 特定の子へとメッセージを飛ばす
	 */
	def call(targetName : String, exec : String, message : Array[TagValue]) = {
		actorImpl.call(targetName, exec, message)
	}

	/**
	 * 親へとメッセージを飛ばす
	 */
	def callParent(exec : String, message : Array[TagValue]) = {
		actorImpl.callParent(exec, message)
	}

	/**
	 * 自分自身へとメッセージを飛ばす
	 */
	def callMyself(exec : String, message : Array[TagValue]) {
		actorImpl.callMyself(exec, message)
	}
	
	/**
	 * tagの一覧を返す
	 */
	def tags (tagValues:Array[TagValue]) = for (tagValue <- tagValues) yield tagValue.getTag
}

/**
 * 系
 * シングルトン
 */
object MessengerCore {
	val identity = UUID.randomUUID().toString()
	val centralActor = new MessengerCentral
	centralActor.start

	/**
	 * 系を開始する
	 */
	def initSystem = {}

	/**
	 * 系を閉じる
	 */
	def closeSystem {
		/*
		 * actor中継を不可能にする
		 * リスト内のactorを破壊
		 * リストを空に
		 * MessengerCore自体のidを破棄(したいけどシングルトンなのでどうしたものか、、外部から破壊したいのだが。
		 */
		centralActor.actorList.foreach { actor =>
			val result = actor !! World.WORLD_MESSAGE_MEMBER_EXIT
		}
		centralActor.actorList.clear()
		println("everything removed" + centralActor.actorList.size)
	}
}

/**
 * 中央Actor
 * 
 * 全参加Actorはここに追加される
 * actor間の中継を行う
 */
class MessengerCentral extends Actor {

	val actorList : ListBuffer[MessengerActor] = ListBuffer()

	def act() = {
		loop {
			react {

				case InputParent(targetName, childActor) => {
					childActor.log += Log.LOG_TYPE_INPUT_TO_PARENTCANDIDATE.toString

					//登録済みのactorの中から、対象を限定してブロードキャストを行う
					actorList.withFilter(_.name.equals(targetName)).foreach { targetCandidate =>
						val future = targetCandidate !! ChildInput(childActor)
						val result = future()

						//この時点でとある親と子のやり取りは完了している

						result match {
							case ParentChildDone() => {
								reply(Done("inputParent succeeded"))
							}
							case ParentChildNotDone() => //同名の親の登録に先を超されている場合
						}
					}

					reply(Failure("targetted parent named:" + targetName + " is not exist. please check parent's name"))
				}

				//system
				case MessengerJoin(actor) => {
					actorList += actor
					reply(Done("joined rep	" + actorList.length))
				}

				case MessengerRemove(actor) => {
					actorList -= actor
					println("actorListから解除	" + actorList.length)
					reply(Done("removed rep	" + actorList.length))
				}

				case m => {
					println("不可解なメッセージ")
					reply(Failure("something wrong,, "+m))
				}
			}
		}
	}
}

/**
 * Messengerの役割を持ったアクター
 * Centralを経由してお互いのポインタを渡し合い、メッセージングを行う。
 */
class MessengerActor(myself : MessengerProtocol, inputtedName : String) extends Actor {
	val log : ListBuffer[String] = ListBuffer()

	val childList : ListBuffer[MessengerActor] = ListBuffer()

	val name = inputtedName
	val id = UUID.randomUUID().toString()

	val parent : ListBuffer[MessengerActor] = ListBuffer()

	def parentName : String = parent(0).name
	def parentId : String = parent(0).id

	def call(targetName : String, exec : String, message : Array[TagValue]) = {
		childList.withFilter(_.name.equals(targetName)).foreach { targetChild =>
			val future = targetChild !! Call(exec, message)
			val result = future()

			result match {
				case Done(_) => {
					log += Log.LOG_TYPE_CALLCHILD.toString
				}
				case Failure(reason) =>
			}
		}
	}

	def callMyself(exec : String, message : Array[TagValue]) = {
		log += Log.LOG_TYPE_CALLMYSELF.toString
		val future = this !! CallMyself(exec, message)
		val result = future()

		result match {
			case Done(_) =>
			case Failure(reason) =>
		}
	}

	def callParent(exec : String, message : Array[TagValue]) = {
		log += Log.LOG_TYPE_CALLPARENT.toString
		val future = parent(0) !! CallParent(exec, message)
		val result = future()

		result match {
			case Done(_) =>
			case Failure(reason) =>
		}
	}

	def act() = {
		loop {
			react {
				//act as parent
				/*
				 * centralを通じた、子ども候補からのインプット
				 * 受け取りの時点で自分の名前をしたMessengerがターゲットなのは確定しているが、
				 * 子ども候補が既に別の同名の親候補から返事を受け取っている可能性もある。
				 */
				case ChildInput(childActor) => {
					//子ども候補からの通信
					log += Log.LOG_TYPE_PARENTCANDIDATE_RECEIVED.toString + childActor
					val future = childActor !! ParentAccepted(this)
					log += Log.LOG_TYPE_PARENTCANDIDATE_ANSWERED.toString + childActor
					val result = future()

					result match {
						case ChildAccepted(currentChildActor) => {
							log += Log.LOG_TYPE_PARENT_CHILD_CONNECTED.toString + currentChildActor

							childList += currentChildActor
							reply(ParentChildDone())
						}
						case ChildNotAccepted(_) => {
							reply(ParentChildNotDone())
						}
					}
				}
				case CallParent(exec, message) => {
					log += Log.LOG_TYPE_CALLED_AS_PARENT.toString
					
					myself.receiver(exec, message)

					reply(Done("parent called"))
				}

				//act as child
				case ParentAccepted(parentActor) => {
					log += Log.LOG_TYPE_CHILD_RECEIVED.toString + parentActor
					if (parent.isEmpty) { //まだ誰も親がいない
						log += Log.LOG_TYPE_PARENT_CHILD_CONNECTED.toString + parentActor
						parent += parentActor
						reply(ChildAccepted(this))
					} else {
						reply(ChildNotAccepted(this))
					}
				}
				case Call(exec, message) => {
					log += Log.LOG_TYPE_CALLED_AS_CHILD.toString
						
					myself.receiver(exec, message)

					reply(Done("child called"))
				}

				//act as myself
				case CallMyself(exec, message) => {
					log += Log.LOG_TYPE_CALLED_MYSELF.toString

					myself.receiver(exec, message)

					reply(Done("myself called"))
				}

				
				/*
				 * messengerとしての駆動を終える
				 */
				case World.WORLD_MESSAGE_MEMBER_EXIT => {
					reply(Done("このMessenger停止完了"))
					exit
				}

				
				case something : String => {
					println("不明物が届いた" + something)
				}
			}
		}
	}
}