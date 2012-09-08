package com.kissaki

/**
 * Akka版
 * 0.0.1	12/09/08 23:00:45	2.9.2 Actors版から書き換えただけ
 *
 * @author sassembla
 */

import akka.actor.Actor
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import akka.util.duration._
import akka.util.Timeout
import akka.dispatch.Await
import akka.pattern.ask
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef

/*
 * システム系のcase class
 */
case class MessengerJoin(actor : Messenger)
case class MessengerRemove(actor : Messenger)

/*
 * system series
 */
case class InputParent(parentCandidateName : String, child : Messenger)
case class ChildInput(child : Messenger)
case class ParentAccepted(parent : Messenger)
case class ChildAccepted(child : Messenger)
case class ChildNotAccepted(child : Messenger)
case class ParentChildDone()
case class ParentChildNotDone()

/*
 * call series
 */
case class Call(exec : String, message : Array[TagValue])
case class CallMyself(exec : String, message : Array[TagValue])
case class CallParent(exec : String, message : Array[TagValue])

case class CallWithAsync(exec : String, message : Array[TagValue])
case class CallMyselfWithAsync(exec : String, message : Array[TagValue])
case class CallParentWithAsync(exec : String, message : Array[TagValue])

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
class Messenger(myself : MessengerProtocol, nameInput : String) {
	println("開始")
	
	implicit val timeoutLimit = Timeout(5 seconds)
	
	/**
	 * log
	 */
	def addLog(input : String) = { log += input }
	def getLog : ListBuffer[String] = log
	def getLogAsJava : java.util.List[String] = {
		log.asJava
	}

	def getCentralActorSize : Int = {
		MessengerCore.actorList.length
	}

	val log : ListBuffer[String] = ListBuffer()

	val childList : ListBuffer[Messenger] = ListBuffer()
println("開始2")
	val name = nameInput
	val id = UUID.randomUUID().toString()

	val parent : ListBuffer[Messenger] = ListBuffer()

	def parentName : String = parent(0).name
	def parentId : String = parent(0).id
	

	/*
	 * シングルトンの生成、centralActorの参照の取得
	 * actorの埋め込み(Akkaになったので、どうかなって感じ。)
	 */
	val centralActorImpl = MessengerCore.centralActor

	val system = ActorSystem() 
	val actorImpl = system.actorOf(Props(new MessengerActor(this, myself)), name)

	log += Log.LOG_TYPE_INITIALIZED.toString
	/*
	 * シングルトンへのActorの追加
	 * actorの埋め込み(ActorのLinkみたいなのが出来るといいんだけどね。)
	 */
	
	val result = Await.result(centralActorImpl ? MessengerJoin(this), timeoutLimit.duration)

	/**
	 * このMessengerを閉じる
	 */
	def close {
		val future = Await.result(centralActorImpl ? MessengerRemove(this), timeoutLimit.duration)
//		val future2 = Await.result(actorImpl ? World.WORLD_MESSAGE_MEMBER_EXIT, timeoutLimit.duration)
	}
	
	/**
	 * 系全体のクローズを行う
	 */
	def closeSystem {
		MessengerCore.closeSystem
	}

	
	def getName : String = name
	def getId : String = id

	def getParentName : String = parentName
	def getParentId : String = parentId

	def hasChild : Boolean = !childList.isEmpty
	def hasParent : Boolean = !parent.isEmpty

	/**
	 * 親の名前入力
	 */
	def inputParent(inputParentName : String) : Result = {
		if (parent.isEmpty) {
			/*
			 * 自前のActorへとインプット
			 */
			log += Log.LOG_TYPE_INPUT_TO_PARENTCANDIDATE.toString
			val future = Await.result(centralActorImpl ? InputParent(inputParentName, this), timeoutLimit.duration)
			
			future match {
				case Done(message) => Result(message)
				case Failure(reason) => Result(reason)
			}
		} else {
			Result(name + " aleady has parent that named " + parent(0).name)
		}

	}

	/**
	 * 同期系
	 */
	def call(targetName : String, exec : String, message : Array[TagValue]) = {
		childList.withFilter(_.name.equals(targetName)).foreach { targetChild =>
			log += Log.LOG_TYPE_CALLCHILD.toString
			val future = Await.result(targetChild.actorImpl ? Call(exec, message), timeoutLimit.duration)
			
			future match {
				case Done(_) => 
				case Failure(reason) =>
			}
		}
	}

	def callMyself(exec : String, message : Array[TagValue]) = {
		log += Log.LOG_TYPE_CALLMYSELF.toString
		val future = Await.result(actorImpl ? CallMyself(exec, message), timeoutLimit.duration)

		future match {
			case Done(_) =>
			case Failure(reason) =>
		}
	}

	def callParent(exec : String, message : Array[TagValue]) = {
		log += Log.LOG_TYPE_CALLPARENT.toString
		val future = Await.result(parent(0).actorImpl ? CallParent(exec, message), timeoutLimit.duration)

		future match {
			case Done(_) =>
			case Failure(reason) =>
		}
	}

	/**
	 * 非同期系
	 */
	def callWithAsync(targetName : String, exec : String, message : Array[TagValue]) = {
		childList.withFilter(_.name.equals(targetName)).foreach { targetChild =>
			log += Log.LOG_TYPE_CALLCHILD_ASYNC.toString
			targetChild.actorImpl ! CallWithAsync(exec, message)
		}
	}

	def callMyselfWithAsync(exec : String, message : Array[TagValue]) = {
		log += Log.LOG_TYPE_CALLMYSELF_ASYNC.toString
		actorImpl ! CallMyselfWithAsync(exec, message)
	}

	def callParentWithAsync(exec : String, message : Array[TagValue]) = {
		log += Log.LOG_TYPE_CALLPARENT_ASYNC.toString
		parent(0).actorImpl ! CallParentWithAsync(exec, message)
	}

	/**
	 * tagの一覧を返す
	 */
	def tags(tagValues : Array[TagValue]) = for (tagValue <- tagValues) yield tagValue.getTag
}

/**
 * 系
 * シングルトン
 */
object MessengerCore {
	println("MessengerCore到着")
	implicit val timeoutLimit = Timeout(5 seconds)
	
	val identity = UUID.randomUUID().toString()
	val system = ActorSystem("Central")
	val actorList : ListBuffer[Messenger] = ListBuffer()
	
	val centralActor = system.actorOf(Props(new MessengerCentral(actorList)), identity)

	
	
	
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
		actorList.foreach { actor =>
//			val result = Await.result(actor.actorImpl ? World.WORLD_MESSAGE_MEMBER_EXIT, timeoutLimit.duration)
		}
		actorList.clear()
		println("everything removed	" + actorList.size)
//		centralActor ! exit
	}
}

/**
 * 中央Actor
 *
 * 全参加Actorはここに追加される
 * actor間の中継を行う
 */
class MessengerCentral(actorList:ListBuffer[Messenger]) extends Actor {
	implicit val timeoutLimit = Timeout(5 seconds)
	
	def receive = {
		case InputParent(targetName, childActor) => {
			println("InputParent受け取った")
			

//登録済みのactorの中から、対象を限定してブロードキャストを行う
			actorList.withFilter(_.name.equals(targetName)).foreach { targetCandidate =>
				val future = Await.result(targetCandidate.actorImpl ? ChildInput(childActor), timeoutLimit.duration)
				

//この時点でとある親と子のやり取りは完了している

				future match {
					case ParentChildDone() => {
						sender ! Done("inputParent succeeded")
					}
					case ParentChildNotDone() => //同名の親の登録に先を超されている場合
				}
			}

			sender ! Failure("targetted parent named:" + targetName + " is not exist. please check parent's name")
		}

		//system
		case MessengerJoin(actor) => {
			println("MessengerJoin")
			actorList += actor
			sender ! Done("joined	" + actorList.length)
		}

		case MessengerRemove(actor) => {
			actorList -= actor
			println("actorListから解除	" + actorList.length)
			sender ! Done("removed	" + actorList.length)
		}

		case m => {
			println("不可解なメッセージ	"+m)
			//					reply(Failure("something wrong,, "+m))
		}
	}
}

/**
 * Messengerの役割を持ったアクター
 * Centralを経由してお互いのポインタを渡し合い、メッセージングを行う。
 */
class MessengerActor(master : Messenger, myself : MessengerProtocol) extends Actor {
	implicit val timeoutLimit = Timeout(5 seconds)
	
	def receive = {
		//act as parent
		/*
		 * centralを通じた、子ども候補からのインプット
		 * 受け取りの時点で自分の名前をしたMessengerがターゲットなのは確定しているが、
		 * 子ども候補が既に別の同名の親候補から返事を受け取っている可能性もある。
		 */
		case ChildInput(childActor) => {
			println("ChildInput")
			//子ども候補からの通信
			master.log += Log.LOG_TYPE_PARENTCANDIDATE_RECEIVED.toString + childActor
			val future = Await.result(childActor.actorImpl ? ParentAccepted(master), timeoutLimit.duration)
			master.log += Log.LOG_TYPE_PARENTCANDIDATE_ANSWERED.toString + childActor
			
			future match {
				case ChildAccepted(currentChildActor) => {
					master.log += Log.LOG_TYPE_PARENT_CHILD_CONNECTED.toString + currentChildActor

					master.childList += currentChildActor
					sender ! ParentChildDone()
				}
				case ChildNotAccepted(_) => {
					sender ! ParentChildNotDone()
				}
			}
		}
		case CallParent(exec, message) => {
			master.log += Log.LOG_TYPE_CALLED_AS_PARENT.toString

			myself.receiver(exec, message)

			sender ! Done("parent called")
		}

		case CallParentWithAsync(exec, message) => {
			master.log += Log.LOG_TYPE_CALLED_AS_PARENT_ASYNC.toString

			myself.receiver(exec, message)

			//					reply(Done("parent-sync called"))
		}

		//act as child
		case ParentAccepted(parentActor) => {
			master.log += Log.LOG_TYPE_CHILD_RECEIVED.toString + parentActor
			if (master.parent.isEmpty) { //まだ誰も親がいない
				master.log += Log.LOG_TYPE_PARENT_CHILD_CONNECTED.toString + parentActor
				master.parent += parentActor
				sender ! ChildAccepted(master)
			} else {
				sender ! ChildNotAccepted(master)
			}
		}
		case Call(exec, message) => {
			master.log += Log.LOG_TYPE_CALLED_AS_CHILD.toString

			myself.receiver(exec, message)
			
			sender ! Done("child called")
		}

		case CallWithAsync(exec, message) => {
			master.log += Log.LOG_TYPE_CALLED_AS_CHILD_ASYNC.toString

			myself.receiver(exec, message)
		}

		//act as myself
		case CallMyself(exec, message) => {
			master.log += Log.LOG_TYPE_CALLED_MYSELF.toString

			myself.receiver(exec, message)

			sender ! Done("myself called")
		}

		case CallMyselfWithAsync(exec, message) => {
			master.log += Log.LOG_TYPE_CALLED_MYSELF_ASYNC.toString

			myself.receiver(exec, message)
		}

		/*
		 * messengerとしての駆動を終える
		 */
		case World.WORLD_MESSAGE_MEMBER_EXIT => {
			sender ! Done("このMessenger停止完了")
			exit
		}

		case something : String => {
			println("不明物が届いた" + something)
		}
	}
}