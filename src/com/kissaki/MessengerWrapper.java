package com.kissaki;



import java.util.List;



/**
 * Scala実装のラッパー
 * @author sassembla
 *
 */
public class MessengerWrapper {
	final Messenger messengerImpl;
	
	/**
	 * コンストラクタ
	 * @param master
	 * @param messengerName
	 */
	public MessengerWrapper(MessengerProtocol master, String messengerName) {
		messengerImpl = new Messenger(master, messengerName);
	}

	/**
	 * Messengerネットワークからの切断を行う
	 */
	public void close() {
		messengerImpl.close();
	}
	
	
	/**
	 * @param parentName
	 */
	public Result inputParent(String inputParentName) {
		return messengerImpl.inputParent(inputParentName);
	}

	
	public void closeSystem() {
		messengerImpl.closeSystem();
	}
	
	
	
	
	/**
	 * 自分自身の名前を返す
	 * @return
	 */
	public String getMessengerName() {
		return messengerImpl.getName();
	}
	
	/**
	 * 自分自身のUUIDを返す
	 * @return
	 */
	public String getMessengerID() {
		return messengerImpl.getId();
	}

	
	
	/**
	 * 親の名前を取得
	 * @return
	 */
	public String getMessengerParentName() {
		return messengerImpl.getParentName();
	}
	
	/**
	 * 親のIDを取得 
	 */
	public String getMessengerParentId() {
		return messengerImpl.getParentId();
	}
	
	


	public List<String> getLog() {
		return messengerImpl.getLogAsJava();
	}


	public String getLatestLogItem() {
		int size = messengerImpl.getLogAsJava().size();
		return messengerImpl.getLogAsJava().get(size-1);
	}

	
	/**
	 * 自分自身を呼ぶ
	 * @param exec
	 * @param tagValue
	 */
	public void callMyself(String exec, TagValue ... tagValue) {
		messengerImpl.callMyself(exec, tagValue);
	}

	/**
	 * 親を呼ぶ
	 * @param exec
	 * @param tagValue
	 */
	public void callParent(String exec, TagValue ... tagValue) {
		messengerImpl.callParent(exec, tagValue);
	}

	/**
	 * 子を呼ぶ
	 * @param target
	 * @param exec
	 * @param tagValue
	 */
	public void call(String target, String exec, TagValue ... tagValue) {
		messengerImpl.call(target, exec, tagValue);
	}

	/**
	 * 自分自身を非同期に呼ぶ
	 * @param exec
	 * @param tagValue
	 */
	public void callMyselfWithAsync(String exec, TagValue ... tagValue) {
		messengerImpl.callMyselfWithAsync(exec, tagValue);
	}

	/**
	 * 親を非同期に呼ぶ
	 * @param exec
	 * @param tagValue
	 */
	public void callParentWithAsync(String exec, TagValue ... tagValue) {
		messengerImpl.callParentWithAsync(exec, tagValue);
	}

	/**
	 * 子を非同期に呼ぶ
	 * @param target
	 * @param exec
	 * @param tagValue
	 */
	public void callWithAsync(String target, String exec, TagValue ... tagValue) {
		messengerImpl.callWithAsync(target, exec, tagValue);
	}
	
	
	/**
	 * Object,integer,String,boolean . array
	 * @param tag
	 * @param value
	 * @return
	 */
	public TagValue tagValue(String tag, Object value) {
		return new TagValue(tag, value);
	}
	
	public TagValue tagValue(String tag, int value) {
		return new TagValue(tag, value);
	}
	
	public TagValue tagValue(String tag, String value) {
		return new TagValue(tag, value);
	}
	
	public TagValue tagValue(String tag, boolean value) {
		return new TagValue(tag, value);
	}
	
	public TagValue tagValue(String tag, String[] value) {
		return new TagValue(tag, value);
	}
	
	public TagValue tagValue(String tag, int[] value) {
		return new TagValue(tag, value);
	}

	public TagValue tagValue(String tag, boolean[] value) {
		return new TagValue(tag, value);
	}
	
	

	
	/**
	 * タグバリュー 
	 * @param key
	 * @param tagValues
	 * @return
	 */
	public Object get(String key, TagValue [] tagValues) {
		for (TagValue tagValue : tagValues) {
			if (tagValue.getTag().equals(key)) {
				return tagValue.get(key);
			}
		}
		return null;
	}
	
	public String getStr(String key, TagValue [] tagValues) {
		for (TagValue tagValue : tagValues) {
			if (tagValue.getTag().equals(key)) {
				return tagValue.getString(key);
			}
		}
		return null;
	}
	
	public int getInt(String key, TagValue [] tagValues) {
		for (TagValue tagValue : tagValues) {
			if (tagValue.getTag().equals(key)) {
				return tagValue.getInt(key);
			}
		}
		return -1;
	}
	
	public boolean getBool(String key, TagValue [] tagValues) {
		for (TagValue tagValue : tagValues) {
			if (tagValue.getTag().equals(key)) {
				return tagValue.getBool(key);
			}
		}
		return false;
	}
	
	public String[] getStrArray(String key, TagValue [] tagValues) {
		for (TagValue tagValue : tagValues) {
			if (tagValue.getTag().equals(key)) {
				return tagValue.getStrArray(key);
			}
		}
		return null;
	}
	
	public int[] getIntArray(String key, TagValue [] tagValues) {
		for (TagValue tagValue : tagValues) {
			if (tagValue.getTag().equals(key)) {
				return tagValue.getIntArray(key);
			}
		}
		return null;
	}
	
	public boolean[] getBoolArray(String key, TagValue [] tagValues) {
		for (TagValue tagValue : tagValues) {
			if (tagValue.getTag().equals(key)) {
				return tagValue.getBoolArray(key);
			}
		}
		return null;
	}
	

	/**
	 * 確認用にcentralに登録されているActorの数を取得する
	 */
	public int getCentralArray() {
		return messengerImpl.getCentralActorSize();
	}

	
	/**
	 * セントラルの停止
	 */
	public void systemDown() {
		messengerImpl.closeSystem();
	}

	/**
	 * 子どもがいる
	 * @return
	 */
	public boolean hasChild() {
		return messengerImpl.hasChild();
	}
	
	/**
	 * 親がいる
	 * @return 
	 */
	public boolean hasParent() {
		return messengerImpl.hasParent();
	}


	/**
	 * タグの集合を作成する
	 * @param receiverResult
	 */
	public String [] getTags(TagValue[] tagValues) {
		return messengerImpl.tags(tagValues); 
	}

	/**
	 * 子どもの数を得る
	 * @return
	 */
	public int getChildNum() {
		return messengerImpl.getChildNum();
	}

	
	public void println(String print) {
		System.out.println(print);
	}

	
	

	
}