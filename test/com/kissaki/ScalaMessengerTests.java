package com.kissaki;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.kissaki.Messenger;
import com.kissaki.MessengerProtocol;
import com.kissaki.MessengerWrapper;
import com.kissaki.Result;
import com.kissaki.TagValue;

public class ScalaMessengerTests extends TestCase implements MessengerProtocol {

	String TEST_PARENT = "TEST_PARENT";
	String TEST_MESSENGER = "TEST_MESSENGER";
	String TEST_SOMEONE = "TEST_SOMEONE";

	String TEST_CURRENT_PARENT = "TEST_CURRENT_PARENT";
	String TEST_SAMENAME_CHILD = "TEST_SAMENAME_CHILD";
	String TEST_SAMENAME_PARENTCANDIDATE = "TEST_SAMENAME_PARENTCANDIDATE";
	String TEST_EXEC = "TEST_EXEC";

	String TEST_NOTEXIST_PARENT = "TEST_NOTEXIST_PARENT";
	String TEST_MORE_PARENT = "TEST_MORE_PARENT";

	String TEST_EXEC_CALLMYSELF = "TEST_EXEC_CALLMYSELF";
	String TEST_EXEC_CALLPARENT = "TEST_EXEC_CALLPARENT";
	String TEST_EXEC_CALLCHILD = "TEST_EXEC_CALLCHILD";

	String TEST_EXEC_CALLMYSELF_ASYNC = "TEST_EXEC_CALLMYSELF_ASYNC";
	String TEST_EXEC_CALLPARENT_ASYNC = "TEST_EXEC_CALLPARENT_ASYNC";
	String TEST_EXEC_CALLCHILD_ASYNC = "TEST_EXEC_CALLCHILD_ASYNC";

	String TEST_EXEC_CALLMYSELF_ASYNC_NEST = "TEST_EXEC_CALLMYSELF_ASYNC_NEST";
	String TEST_EXEC_CALLPARENT_ASYNC_NEST = "TEST_EXEC_CALLPARENT_ASYNC_NEST";
	String TEST_EXEC_CALLCHILD_ASYNC_NEST = "TEST_EXEC_CALLCHILD_ASYNC_NEST";

	String TEST_DELAY = "TEST_DELAY";
	String TEST_PARENT_DELAY = "TEST_PARENT_DELAY";
	String TEST_DELAY_AFTER = "TEST_DELAY_AFTER";
	String TEST_DELAY_AWAKE = "TEST_DELAY_AWAKE";
	String TEST_DELAYED_CALLPARENT = "TEST_DELAYED_CALLPARENT";
	int TEST_DELAY_DURATION = 1000;
	
	ArrayList<String> globalLog;
	
	int TEST_TAGVALUE_NUM = 100;

	public String identifier;

	String TEST_SAMPLE = "TEST_SAMPLE";

	// 非同期試験用
	private CountDownLatch lock = new CountDownLatch(1);

	MessengerWrapper messenger;
	ParentObject parent;
	SomeOtherObject someone;
	private TagValue[] receiverResult = null;

	
	
	public void println(String s) {
		System.out.println(s);
	}

	@Before
	public void setUp() {
		int rand = new Random().nextInt();
		identifier = "rand_" + rand;

		println("test identifier	" + identifier+"	/testName	"+this);

		parent = new ParentObject(TEST_PARENT);
		messenger = new MessengerWrapper(this, TEST_MESSENGER);
		someone = new SomeOtherObject(TEST_SOMEONE);

		// initialized added
		assertTrue("messenger not match	" + messenger.getLog().size(),
				1 == messenger.getLog().size());
		assertTrue("parent not match	" + parent.messenger.getLog().size(),
				1 == parent.messenger.getLog().size());

		messenger.inputParent(TEST_PARENT);
	}

	@After
	public void tearDown() {
		messenger.systemDown();
		messenger = null;
		parent = null;
		someone = null;
	}

	/**
	 * レシーバ
	 */
	public void receiver(String exec, TagValue[] tagValues) {
		receiverResult = tagValues;
		assertNotNull("should not null...", receiverResult);

		// myself-nest
		if (exec.equals(TEST_EXEC_CALLMYSELF)) {
			messenger.callMyself(TEST_EXEC_CALLMYSELF + 1);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF + 1)) {
			messenger.callMyself(TEST_EXEC_CALLMYSELF + 2);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF + 2)) {
			messenger.callMyself(TEST_EXEC_CALLMYSELF + 3);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF + 3)) {
			messenger.callMyself(TEST_EXEC_CALLMYSELF + 4);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF + 4)) {
			messenger.callMyself(TEST_EXEC_CALLMYSELF + 5);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF + 5)) {
		}

		// child-parent-nest
		if (exec.equals(TEST_EXEC_CALLCHILD)) {
			messenger.callParent(TEST_EXEC_CALLPARENT + 1);
		}
		if (exec.equals(TEST_EXEC_CALLCHILD + 1)) {
			messenger.callParent(TEST_EXEC_CALLPARENT + 2);
		}
		if (exec.equals(TEST_EXEC_CALLCHILD + 2)) {
			messenger.callParent(TEST_EXEC_CALLPARENT + 3);
		}

		// async-parent
		if (exec.equals(TEST_EXEC_CALLCHILD_ASYNC)) {
			println("TEST_EXEC_CALLCHILD_ASYNC");
		}

		// async-myself-nest
		if (exec.equals(TEST_EXEC_CALLMYSELF_ASYNC_NEST)) {
			messenger.callMyselfWithAsync(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 1);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 1)) {
			messenger.callMyselfWithAsync(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 2);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 2)) {
			messenger.callMyselfWithAsync(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 3);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 3)) {
			messenger.callMyselfWithAsync(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 4);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 4)) {
			messenger.callMyselfWithAsync(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 5);
		}
		if (exec.equals(TEST_EXEC_CALLMYSELF_ASYNC_NEST + 5)) {
		}

		// async-child-parent-nest
		if (exec.equals(TEST_EXEC_CALLCHILD_ASYNC_NEST)) {
			messenger.callParentWithAsync(TEST_EXEC_CALLPARENT_ASYNC_NEST + 1);
		}
		if (exec.equals(TEST_EXEC_CALLCHILD_ASYNC_NEST + 1)) {
			messenger.callParentWithAsync(TEST_EXEC_CALLPARENT_ASYNC_NEST + 2);
		}
		if (exec.equals(TEST_EXEC_CALLCHILD_ASYNC_NEST + 2)) {
			messenger.callParentWithAsync(TEST_EXEC_CALLPARENT_ASYNC_NEST + 3);
		}

		// async-myself-delay
		if (exec.equals(TEST_DELAY)) {
			globalLog.add(TEST_DELAY);
			
			try {
				Thread.sleep(TEST_DELAY_DURATION);
			} catch (InterruptedException e) {
				println("InterruptedException	"+e);
			}
			
			globalLog.add(TEST_DELAY_AWAKE);
			
			messenger.callParent(TEST_DELAYED_CALLPARENT);
		}
		
		if (exec.equals(TEST_DELAY_AFTER)) {
			globalLog.add(TEST_DELAY_AFTER);
		}
	}

	/**
	 * プロトコルに準拠した親
	 * 
	 * @author sassembla
	 */
	class ParentObject implements MessengerProtocol {
		MessengerWrapper messenger;
		TagValue[] receiverResult = null;

		public ParentObject(String name) {
			messenger = new MessengerWrapper(this, name);
		}

		public void receiver(String exec, TagValue[] tagValues) {
			receiverResult = tagValues;
			assertNotNull("should not null...", receiverResult);
			// parent-child
			if (exec.equals(TEST_EXEC_CALLPARENT)) {
				messenger.call(TEST_MESSENGER, TEST_EXEC_CALLCHILD);
			}
			if (exec.equals(TEST_EXEC_CALLPARENT + 1)) {
				messenger.call(TEST_MESSENGER, TEST_EXEC_CALLCHILD + 1);
			}
			if (exec.equals(TEST_EXEC_CALLPARENT + 2)) {
				messenger.call(TEST_MESSENGER, TEST_EXEC_CALLCHILD + 2);
			}
			if (exec.equals(TEST_EXEC_CALLPARENT + 3)) {
				messenger.call(TEST_MESSENGER, TEST_EXEC_CALLCHILD + 3);
			}

			// async-child
			if (exec.equals(TEST_EXEC_CALLPARENT_ASYNC)) {
				println("TEST_EXEC_CALLPARENT_ASYNC");
			}

			// async-parent-child
			if (exec.equals(TEST_EXEC_CALLPARENT_ASYNC_NEST)) {
				messenger.callWithAsync(TEST_MESSENGER,
						TEST_EXEC_CALLCHILD_ASYNC_NEST);
			}
			if (exec.equals(TEST_EXEC_CALLPARENT_ASYNC_NEST + 1)) {
				messenger.callWithAsync(TEST_MESSENGER,
						TEST_EXEC_CALLCHILD_ASYNC_NEST + 1);
			}
			if (exec.equals(TEST_EXEC_CALLPARENT_ASYNC_NEST + 2)) {
				messenger.callWithAsync(TEST_MESSENGER,
						TEST_EXEC_CALLCHILD_ASYNC_NEST + 2);
			}
			if (exec.equals(TEST_EXEC_CALLPARENT_ASYNC_NEST + 3)) {
				messenger.callWithAsync(TEST_MESSENGER,
						TEST_EXEC_CALLCHILD_ASYNC_NEST + 3);
			}
			
			if (exec.equals(TEST_DELAYED_CALLPARENT)) {
				globalLog.add(TEST_DELAYED_CALLPARENT);
			}
			
			if (exec.equals(TEST_PARENT_DELAY)) {
				
				try {
					Thread.sleep(TEST_DELAY_DURATION);
				} catch (InterruptedException e) {
					println("InterruptedException	"+e);
				}
				
				messenger.call(TEST_MESSENGER, TEST_DELAY);
			}
		}
	}

	/**
	 * 他人オブジェクト
	 * 
	 * @author sassembla
	 * 
	 */
	class SomeOtherObject implements MessengerProtocol {
		Messenger messenger;

		public SomeOtherObject(String name) {
			messenger = new Messenger(this, name);
		}

		public void receiver(String exec, TagValue[] tagValues) {

		}
	}

	@Test
	public void testInputParent() {
		String parentName = messenger.getMessengerParentName();
		assertEquals(TEST_PARENT, parentName);

		// 初期化済みの1から関係構築開始、返答受付、関係構築完了のログが増加
		assertTrue("messenger not match	" + messenger.getLog().size(),
				4 == messenger.getLog().size());

		// 初期化済みの1から受信、返答、関係構築完了のログが増加
		assertTrue("parent not match	" + parent.messenger.getLog().size(),
				4 == parent.messenger.getLog().size());
	}

	/**
	 * CentralへのMessengerの登録、削除
	 */
	@Test
	public void testAddToCentral() {
		int centralArrayCount = messenger.getCentralArray();

		MessengerWrapper currentMessenger = new MessengerWrapper(this,
				TEST_SAMPLE);

		// 一人追加すると、centralが持っているリストが少し大きくなる筈
		assertEquals(centralArrayCount + 1, messenger.getCentralArray());
		currentMessenger.close();
	}

	/**
	 * 離脱すると、サイズが小さくなる筈
	 */
	@Test
	public void testRemoveFromCentral() {
		MessengerWrapper currentMessenger = new MessengerWrapper(this,
				TEST_SAMPLE);

		// 足され終わった時点の数値を取得
		int centralArrayCount = messenger.getCentralArray();

		currentMessenger.close();
		// 一人追加すると、centralが持っているリストが少し小さくなる筈
		assertTrue(centralArrayCount - 1 == messenger.getCentralArray());
	}

	@Test
	public void testGetLog() {
		List<String> log = messenger.getLog();

		// 初期化された状態のタグがある筈
		assertTrue(log.contains("LOG_TYPE_INITIALIZED"));
	}

	/**
	 * 値無しコール
	 */
	@Test
	public void testCall() {
		int num = messenger.getLog().size();
		int parentNum = parent.messenger.getLog().size();

		parent.messenger.call(TEST_MESSENGER, TEST_EXEC);

		assertTrue("not match, " + (num + 1) + "	/but	"
				+ messenger.getLog().size(), num + 1 == messenger.getLog()
				.size());
		assertTrue("not match, " + (parentNum + 1) + "	/but	"
				+ parent.messenger.getLog().size(), num + 1 == parent.messenger
				.getLog().size());

		assertTrue("not match log",
				messenger.getLatestLogItem().equals("LOG_TYPE_CALLED_AS_CHILD"));
		assertTrue("not match log",
				parent.messenger.getLatestLogItem()
						.equals("LOG_TYPE_CALLCHILD"));
	}

	@Test
	public void testCallMyself() {
		int num = messenger.getLog().size();

		messenger.callMyself(TEST_EXEC);

		assertTrue("not match, " + (num + 2) + "	/but	"
				+ messenger.getLog().size(), num + 2 == messenger.getLog()
				.size());

		assertTrue("not match log",
				messenger.getLatestLogItem().equals("LOG_TYPE_CALLED_MYSELF"));
	}

	@Test
	public void testCallParent() {
		int num = messenger.getLog().size();
		int parentNum = parent.messenger.getLog().size();

		messenger.callParent(TEST_EXEC);

		assertTrue("not match, " + (num + 1) + "	/but	"
				+ messenger.getLog().size(), num + 1 == messenger.getLog()
				.size());
		assertTrue("not match, " + (parentNum + 1) + "	/but	"
				+ parent.messenger.getLog().size(), num + 1 == parent.messenger
				.getLog().size());

		assertTrue("messenger not match log", messenger.getLatestLogItem()
				.equals("LOG_TYPE_CALLPARENT"));
		assertTrue("parent not match log", parent.messenger.getLatestLogItem()
				.equals("LOG_TYPE_CALLED_AS_PARENT"));
	}

	/**
	 * 値ありのコール
	 */
	@Test
	public void testCallWithParam() {
		parent.messenger.call(TEST_MESSENGER, TEST_EXEC,
				parent.messenger.tagValue("number", TEST_TAGVALUE_NUM));

		// 成功していれば、子ども側の値を取得して、何か入っている
		assertNotNull("should not null...", receiverResult);

		// 値を取得できる
		int int1 = messenger.getInt("number", receiverResult);
		assertTrue(int1 == TEST_TAGVALUE_NUM);
	}

	@Test
	public void testCallMyselfWithParam() {
		messenger.callMyself(TEST_EXEC,
				messenger.tagValue("number", TEST_TAGVALUE_NUM));

		// 成功していれば、子ども側の値を取得して、何か入っている
		assertNotNull("should not null...", receiverResult);

		// 値を取得できる
		int int1 = messenger.getInt("number", receiverResult);
		assertTrue(int1 == TEST_TAGVALUE_NUM);
	}

	@Test
	public void testCallParentWithParam() {
		messenger.callParent(TEST_EXEC,
				messenger.tagValue("number", TEST_TAGVALUE_NUM));

		// 成功していれば、子ども側の値を取得して、何か入っている
		assertNotNull("should not null...", parent.receiverResult);

		// 値を取得できる
		int int1 = messenger.getInt("number", parent.receiverResult);
		assertTrue(int1 == TEST_TAGVALUE_NUM);
	}

	/*
	 * More case
	 */
	/**
	 * 複数の同名の子どもがいるケース
	 */
	@Test
	public void testMultiChild() {
		int childNum = 1000;

		MessengerWrapper currentParent = new MessengerWrapper(this,
				TEST_CURRENT_PARENT);

		ArrayList<MessengerWrapper> currentMessengers = new ArrayList<MessengerWrapper>();

		// 大量の子どもを作成
		for (int i = 0; i < childNum; i++)
			currentMessengers.add(new MessengerWrapper(this,
					TEST_SAMENAME_CHILD));

		// 親登録
		for (MessengerWrapper currentMessenger : currentMessengers) {
			currentMessenger.inputParent(TEST_CURRENT_PARENT);
		}

		int beforeParentLogSize = currentParent.getLog().size();
		int beforeChildLogSizeStandard = currentMessengers.get(0).getLog()
				.size();

		// 親からブロードキャスト
		currentParent.call(TEST_SAMENAME_CHILD, "sample");
		assertTrue(beforeParentLogSize + childNum == currentParent.getLog()
				.size());

		// 子どもの側のログ確認(受信分だけ増えているはず)
		for (MessengerWrapper currentMessenger : currentMessengers) {
			assertTrue(beforeChildLogSizeStandard + 1 == currentMessenger
					.getLog().size());
		}
	}

	/**
	 * 複数の同名の親がいるケース
	 */
	@Test
	public void testMultiSameNameParentCandidate() {
		// 同じ名前の親候補二人
		MessengerWrapper currentMessenger1 = new MessengerWrapper(this,
				TEST_SAMENAME_PARENTCANDIDATE);
		MessengerWrapper currentMessenger2 = new MessengerWrapper(this,
				TEST_SAMENAME_PARENTCANDIDATE);

		MessengerWrapper child = new MessengerWrapper(this,
				"testMultiSameNameParentCandidate");
		child.inputParent(TEST_SAMENAME_PARENTCANDIDATE);

		// 成立していれば、どちらかの親候補は親になれていないはず。
		boolean result1 = currentMessenger1.hasChild();
		boolean result2 = currentMessenger2.hasChild();
		assertTrue((result1 && !result2) || (!result1 && result2));
	}

	/**
	 * 親は最大一人、複数の親を指定しようとするとエラー
	 */
	@Test
	public void testOnlyOneParent() {
		Result result = messenger.inputParent(TEST_MORE_PARENT);

		assertTrue(result.result().equals(
				messenger.getMessengerName() + " aleady has parent that named "
						+ messenger.getMessengerParentName()));
	}

	/**
	 * 存在しない親を指定するとエラー
	 */
	@Test
	public void testNoParentCandidateExistError() {
		MessengerWrapper currentMessenger = new MessengerWrapper(this,
				"currentMessenger");
		Result result = currentMessenger.inputParent(TEST_NOTEXIST_PARENT);
		assertTrue(result.result().equals(
				"targetted parent named:" + TEST_NOTEXIST_PARENT
						+ " is not exist. please check parent's name"));

	}

	/*
	 * タグバリュー
	 * 
	 * 値の取得、送付の確認を行う。 取得
	 */
	/**
	 * タグ取得
	 */
	@Test
	public void testGetTags() {
		messenger.callMyself(TEST_EXEC, messenger.tagValue("tag0", this),
				messenger.tagValue("tag1", "messenger"),
				messenger.tagValue("tag2", 2),
				messenger.tagValue("tag3", true),
				messenger.tagValue("tag4", 1000),
				messenger.tagValue("tag5", "good"));

		// receiverに値が入る
		assertNotNull("should not null...", receiverResult);

		String[] tags = messenger.getTags(receiverResult);
		assertTrue(tags.length == 6);

		ArrayList<String> tagsArray = new ArrayList<String>();
		for (String tag : tags) {
			tagsArray.add(tag);
		}

		assertTrue(tagsArray.contains("tag0"));
		assertTrue(tagsArray.contains("tag1"));
		assertTrue(tagsArray.contains("tag2"));
		assertTrue(tagsArray.contains("tag3"));
		assertTrue(tagsArray.contains("tag4"));
		assertTrue(tagsArray.contains("tag5"));
	}

	/**
	 * タグバリューの型チェック
	 */
	// String
	@Test
	public void testGetStr() {
		messenger.callMyself(TEST_EXEC, messenger.tagValue("key", "value"));
		// receiverに値が入る
		assertNotNull("should not null...", receiverResult);

		TagValue[] tagValues = receiverResult;
		String str = messenger.getStr("key", tagValues);
		assertNotNull(str);

		assertTrue(str.equals("value"));
	}

	// int
	@Test
	public void testGetInt() {
		messenger.callMyself(TEST_EXEC, messenger.tagValue("key", 100));
		// receiverに値が入る
		assertNotNull("should not null...", receiverResult);

		TagValue[] tagValues = receiverResult;
		int num = messenger.getInt("key", tagValues);
		assertTrue(num == 100);
	}

	// bool
	@Test
	public void testGetBool() {
		messenger.callMyself(TEST_EXEC, messenger.tagValue("key", true));
		// receiverに値が入る
		assertNotNull("should not null...", receiverResult);

		TagValue[] tagValues = receiverResult;
		boolean b = messenger.getBool("key", tagValues);
		assertTrue(b);
	}

	// String[]
	@Test
	public void testGetStrArray() {
		String[] a = { "a", "b", "c" };

		messenger.callMyself(TEST_EXEC, messenger.tagValue("key", a));
		// receiverに値が入る
		assertNotNull("should not null...", receiverResult);

		TagValue[] tagValues = receiverResult;
		String[] strArray = messenger.getStrArray("key", tagValues);

		assertTrue(strArray.length == 3);
		assertTrue(strArray[0] == "a");
		assertTrue(strArray[1] == "b");
		assertTrue(strArray[2] == "c");
	}

	// int[]
	@Test
	public void testGetIntArray() {
		int[] a = { 1, 2, 3 };

		messenger.callMyself(TEST_EXEC, messenger.tagValue("key", a));
		// receiverに値が入る
		assertNotNull("should not null...", receiverResult);

		TagValue[] tagValues = receiverResult;
		int[] intArray = messenger.getIntArray("key", tagValues);

		assertTrue(intArray.length == 3);
		assertTrue(intArray[0] == 1);
		assertTrue(intArray[1] == 2);
		assertTrue(intArray[2] == 3);
	}

	// bool[]
	@Test
	public void testGetBoolArray() {
		boolean[] a = { true, false, true };

		messenger.callMyself(TEST_EXEC, messenger.tagValue("key", a));
		// receiverに値が入る
		assertNotNull("should not null...", receiverResult);

		TagValue[] tagValues = receiverResult;
		boolean[] boolArray = messenger.getBoolArray("key", tagValues);

		assertTrue(boolArray.length == 3);
		assertTrue(boolArray[0] == true);
		assertTrue(boolArray[1] == false);
		assertTrue(boolArray[2] == true);
	}

	/*
	 * 複数の型の値
	 */
	@Test
	public void testMultiTypeParam() {
		String str_messenger = "messenger";
		int int_1000 = 1000;

		messenger.callMyself(TEST_EXEC, messenger.tagValue("tag0", this),
				messenger.tagValue("tag1", str_messenger),
				messenger.tagValue("tag2", 2),
				messenger.tagValue("tag3", true),
				messenger.tagValue("tag4", int_1000),
				messenger.tagValue("tag5", "good"));

		// receiverに値が入る
		assertNotNull("should not null...", receiverResult);

		// object same ref
		ScalaMessengerTests theThis = (ScalaMessengerTests) messenger.get(
				"tag0", receiverResult);
		assertTrue(theThis.equals(this));

		// str same value
		String str = messenger.getStr("tag1", receiverResult);
		assertTrue(str.equals("messenger"));

		// str same ref
		String str2 = messenger.getStr("tag1", receiverResult);
		assertTrue(str2 == str_messenger);

		// int same value
		int int1 = messenger.getInt("tag2", receiverResult);
		assertTrue(int1 == 2);

		// bool
		boolean bool = messenger.getBool("tag3", receiverResult);
		assertTrue(bool);

		// int same value2(instancize)
		int int2 = messenger.getInt("tag4", receiverResult);
		assertTrue(int2 == int_1000);

		// str same value(no ref)
		String str3 = messenger.getStr("tag5", receiverResult);
		assertTrue(str3.equals("good"));
	}

	/**
	 * 子どもがcloseするときに親からの解除
	 */
	@Test
	public void testCloseAndRemoveFromParent() {

		int childNum = parent.messenger.getChildNum();

		messenger.close();

		// 一件子どもの数が減っている
		assertTrue(childNum - 1 == parent.messenger.getChildNum());
	}

	/**
	 * 親がcloseするときに子どもからの解除
	 */
	@Test
	public void testCloseAndRemoveFromChild() {
		// まだ親が居る
		assertTrue(messenger.hasParent());

		parent.messenger.close();

		// 子どもは親が居ない
		assertFalse(messenger.hasParent());
	}

	/*
	 * ネストする回数が多いケース myself child parentの3パターン x 3バリエーション x n層レイヤー
	 */
	/**
	 * 自分自身
	 */
	@Test
	public void testMultiBoundCallMyself() {
		int logNum = messenger.getLog().size();

		messenger.callMyself(TEST_EXEC_CALLMYSELF);

		assertTrue("messenger not match	" + messenger.getLog().size(),
				logNum + 12 == messenger.getLog().size());
	}

	/**
	 * 親→自分→親→、、、
	 */
	@Test
	public void testMultiBoundCallParent_CallChild() {
		int logNum = messenger.getLog().size();
		int parentLogNum = parent.messenger.getLog().size();

		messenger.callParent(TEST_EXEC_CALLPARENT);

		assertTrue("messenger not match	" + messenger.getLog().size(),
				logNum + 8 == messenger.getLog().size());
		assertTrue("parent not match	" + parent.messenger.getLog().size(),
				parentLogNum + 8 == messenger.getLog().size());
	}

	/*
	 * 非同期
	 */
	@Test
	public void testCallWithAsync() {
		int logNum = messenger.getLog().size();
		int parentLogNum = parent.messenger.getLog().size();

		parent.messenger.callWithAsync(TEST_MESSENGER,
				TEST_EXEC_CALLCHILD_ASYNC);

		// 非同期ラッチ
		try {
			lock.await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}

		assertTrue("messenger not match	" + messenger.getLog().size(),
				logNum + 1 == messenger.getLog().size());
		assertTrue("parent not match	" + parent.messenger.getLog().size(),
				parentLogNum + 1 == parent.messenger.getLog().size());
	}

	@Test
	public void testCallMyselfWithAsync() {
		int logNum = messenger.getLog().size();
		messenger.callMyselfWithAsync(TEST_EXEC_CALLMYSELF_ASYNC);

		// 非同期ラッチ
		try {
			lock.await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}

		assertTrue("messenger not match	" + (logNum + 2) + " but "
				+ messenger.getLog().size(), logNum + 2 == messenger.getLog()
				.size());
	}

	@Test
	public void testCallParentWithAsync() {
		int logNum = messenger.getLog().size();
		int parentLogNum = parent.messenger.getLog().size();

		messenger.callParentWithAsync(TEST_EXEC_CALLPARENT_ASYNC);

		// 非同期ラッチ
		try {
			lock.await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}

		assertTrue("messenger not match	" + messenger.getLog().size(),
				logNum + 1 == messenger.getLog().size());
		assertTrue("parent not match	" + parent.messenger.getLog().size(),
				parentLogNum + 1 == parent.messenger.getLog().size());
	}

	/*
	 * 非同期系のネストする回数が多いケース myself child parentの3パターン x 3バリエーション x n層レイヤー
	 */
	/**
	 * 自分自身
	 */
	@Test
	public void testMultiBoundCallMyselfWithAsync() {
		int logNum = messenger.getLog().size();

		messenger.callMyselfWithAsync(TEST_EXEC_CALLMYSELF_ASYNC_NEST);
		// 非同期ラッチ
		try {
			lock.await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}

		println("logNum	" + logNum);
		assertTrue("messenger not match	" + messenger.getLog().size(),
				logNum + 12 == messenger.getLog().size());
	}

	/**
	 * 親→自分→親→、、、
	 */
	@Test
	public void testMultiBoundCallParent_CallChildWithAsync() {
		int logNum = messenger.getLog().size();
		int parentLogNum = parent.messenger.getLog().size();

		messenger.callParentWithAsync(TEST_EXEC_CALLPARENT_ASYNC_NEST);
		// 非同期ラッチ
		try {
			lock.await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}

		println("logNum	" + logNum);
		println("parentLogNum	" + parentLogNum);

		assertTrue("messenger not match	" + messenger.getLog().size(),
				logNum + 8 == messenger.getLog().size());
		assertTrue("parent not match	" + parent.messenger.getLog().size(),
				parentLogNum + 8 == parent.messenger.getLog().size());
	}

	
	/**
	 * 遅延実行
	 * 
	 * asyncの変則的な使い方、タイマー実行 値を参照で持つか、値で持つか、内容によってかなり動作に差が出る。
	 */
	@Test
	public void testCallMyselfWithAsyncDelay() {
		globalLog = new ArrayList<String>();
		
		int before = parent.messenger.getLog().size();
		
		// 非同期の先のThreadでsleepすると、非同期で抜けつつ、時間が来たら返事を返す、というような事が出来る。
		messenger.callMyselfWithAsync(TEST_DELAY);
		
		// 非同期ラッチ
		try {
			lock.await(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		
		//親にひとつメッセージが届いている
		assertTrue("parent not match	" + parent.messenger.getLog().size(),
				before + 1 == parent.messenger.getLog().size());
		globalLog = null;
	}
	
	/**
	 * 親から子への非同期遅延 > 子から親への非同期遅延
	 */
	@Test
	public void testCallParentWithAsyncDelay() {
		globalLog = new ArrayList<String>();
		int before = messenger.getLog().size();
		
		// 非同期の先のThreadでsleepすると、非同期で抜けつつ、時間が来たら返事を返す、というような事が出来る。
		messenger.callParentWithAsync(TEST_PARENT_DELAY);
		
		// 非同期ラッチ
		try {
			lock.await(4, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		
		//自分にひとつメッセージが届き、親に送り返している
		assertTrue("messenger not match	" + messenger.getLog().size(),
				before + 3 == messenger.getLog().size());
		globalLog = null;
	}
	
	@Test
	public void testCallWithAsyncDelay() {
		globalLog = new ArrayList<String>();
		
		int before = messenger.getLog().size();
		int beforeParent = parent.messenger.getLog().size();
		
		// 非同期の先のThreadでsleepすると、呼び元のThread != asyncを実行したThreadなため、非同期で抜けつつ、時間が来たら返事を返す、というような事が出来る
		parent.messenger.call(TEST_MESSENGER, TEST_DELAY);
		
		
		// 非同期ラッチ
		try {
			lock.await(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		
		//親 s>r 子 s>r 親	各2つログが増加
		assertTrue("messenger not match	" + messenger.getLog().size(),
				before + 2 == messenger.getLog().size());
		assertTrue(beforeParent+"	parent not match	" + parent.messenger.getLog().size(),
				beforeParent + 2 == parent.messenger.getLog().size());
		globalLog = null;
	}
	
	
	/**
	 * 遅延実行2
	 * 遅延を仕込んだあとで、同じMessengerを使用する
	 */
	@Test
	public void testCallMyselfWithAsyncDelay2() {
		globalLog = new ArrayList<String>();
		
		//非同期で抜ける
		messenger.callMyselfWithAsync(TEST_DELAY);
		
		//メッセージが送られる
		messenger.callMyself(TEST_DELAY_AFTER);
		
		// 非同期ラッチ
		try {
			lock.await(2, TimeUnit.SECONDS);
			
			//このあたりで、TEST_DELAY内部のsleepが解除される
			
			//TEST_DELAYED_CALLPARENT発生
			
			//await終了
		} catch (InterruptedException e) {
		}
		
		//TEST_DELAY_AFTER | TEST_DELAY,	TEST_DELAY_AWAKE,	TEST_DELAYED_CALLPARENTの順でログが発生していないといけない
		
		//どちらが速いかは運
		assertTrue((TEST_DELAY_AFTER.equals(globalLog.get(0)) && TEST_DELAY.equals(globalLog.get(1))) || 
				(TEST_DELAY.equals(globalLog.get(0)) && TEST_DELAY_AFTER.equals(globalLog.get(1))));
		
		assertEquals(TEST_DELAY_AWAKE, globalLog.get(2));
		assertEquals(TEST_DELAYED_CALLPARENT, globalLog.get(3));
		globalLog = null;
	}
	
}
