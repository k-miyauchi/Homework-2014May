package rentalShop;


/**
 * @author kbc14a12
 *商品管理用クラス
 */
class ItemMain {
	private Item[] item = new Item[0];
	private Item[] buffer = new Item[0];
	protected String sep = System.getProperty("line.separator"); //改行用

	ItemMain() {
		initInstance(item);
		initInstance(buffer);
	}

	String[] toCSV() {
		String[] csv = new String[item.length];
		for (int i = 0; i < csv.length; i++) {
			csv[i] = item[i].toCSV();
		}
		return csv;
	}

	void fromCSV(String[] csv) {
		for (int i = 0; i < csv.length; i++	) {
			createNewInstance();
			item[i].fromCSV(csv[i]);
		}
	}

	int searchId(String id) {
		int index = -1;
		if (isNum(id)) {
			for (int i = 0; i < item.length; i++) {
				if (id.equals(item[i].getId())) {
					index = i;
					break; //残り探してもしょうがないのでbreak
				}
			}
		}
		return index;
	}
	//削除済みのやつ無視する奴
	int searchIdRejectNonAvailable(String id) {
		int index = -1;
		if (isNum(id)) {
			for (int i = 0; i < item.length; i++) {
				if (id.equals(item[i].getId())) {
					if (item[i].getAvailable()) {
						index = i;
						break; //残り探してもしょうがないのでbreak
					}
				}
			}
		}
		return index;
	}


	String[][] search(String id, String code, String name, String author, String label, String date, String newOld, String type, String available)  {
		int[] result = new int[0];
		int[] buffer = new int[0];
		for (int i = 0; i < item.length; i++) {
			//すべてに対し「空文字列である、または入力された情報が一致している」が真なら通る
			//ようは空欄じゃない奴に対してのand検索
			String avail = (item[i].getAvailable() ? "利用可" : "削除");
			if ((id.equals("") || item[i].getId().equals(id)) && (code.equals("") || item[i].getCode().equals(code)) && (name.equals("") || item[i].getName().equals(name)) && (author.equals("") || item[i].getAuthor().equals(author)) && (label.equals("") || item[i].getLabel().equals(label)) && (date.equals("") || item[i].getDateForSearch().equals(date)) && (newOld.equals("") || item[i].getNewOld().equals(newOld)) && (type.equals("") || item[i].getType().equals(type))  && (available.equals("") || available.equals(avail))) {
				buffer = new int[result.length];
				for (int j = 0; j < result.length; j++) {
					buffer[j] = result[j];
				}
				result = new int[result.length + 1];
				for (int j = 0; j < buffer.length; j++) {
					result[j] = buffer[j];
				}
				result[result.length - 1] = i;				
			}
		}
		//検索が終わったらデータ生成;
		String[][] sArray = new String [result.length][9];
		int index = 0;
		for (int i = 0; i < sArray.length; i++) {
			index = result[i];
			for (int j = 0; j < sArray[i].length; j ++) {
				switch (j) {
				case 0:
					sArray[i][j] = item[index].getId();
					break;
				case 1:
					sArray[i][j] = item[index].getCode();
					break;
				case 2:
					sArray[i][j] = item[index].getName();
					break;
				case 3:
					sArray[i][j] = item[index].getAuthor();
					break;
				case 4:
					sArray[i][j] = item[index].getLabel();
					break;
				case 5:
					sArray[i][j] = item[index].getDate();
					break;
				case 6:
					sArray[i][j] = item[index].getNewOld();
					break;
				case 7:
					sArray[i][j] = item[index].getType();
					break;
				case 8:
					sArray[i][j] = (item[index].getAvailable() ? "利用可" : "利用不可");
					break;
				}
			}
		}
		return sArray;
	}

	int[] addNewArray(int add, int[] iarray) {
		int[] buffer = new int[iarray.length];
		for (int i = 0; i < iarray.length; i++) {
			buffer[i] = iarray[i];
		}
		iarray = new int[iarray.length + 1];
		for (int i = 0; i < buffer.length; i++) {
			iarray[i] = buffer[i];
		}
		iarray[iarray.length - 1] = add;
		return iarray;
	}
	//JTable用の商品情報を返すメソッドgetAllIteminfo。
	String[][] getAllInfo() {
		String[][] sArray = new String[item.length][9];
		for (int i = 0; i < sArray.length; i++) {
			for (int j = 0; j < sArray[i].length; j ++) {
				switch (j) {
				case 0:
					sArray[i][j] = item[i].getId();
					break;
				case 1:
					sArray[i][j] = item[i].getCode();
					break;
				case 2:
					sArray[i][j] = item[i].getName();
					break;
				case 3:
					sArray[i][j] = item[i].getAuthor();
					break;
				case 4:
					sArray[i][j] = item[i].getLabel();
					break;
				case 5:
					sArray[i][j] = item[i].getDate();
					break;
				case 6:
					sArray[i][j] = item[i].getNewOld();
					break;
				case 7:
					sArray[i][j] = item[i].getType();
					break;
				case 8:
					sArray[i][j] = (item[i].getAvailable() ? "利用可" : "利用不可");
					break;
				}
			}
		}
		return sArray;
	}
	//JTableのカラム作成用
	String[] getAllColumnInfo() {
		String[] sArray = {"ID", "コード", "商品名", "著作者", "レーベル", "日付", "新旧", "種類", "利用"};
		return sArray;
	}

	boolean canRegister(String code, String name, String author, String label, String year, String month, String day, String newOld, String type) {
		//空文字列があったら偽
		if (code.equals("") || name.equals("") || author.equals("") || label.equals("") || year.equals("") || month.equals("") || day.equals("") || newOld.equals("") || type.equals(""))
			return false;
		if (!isNum(code))
			return false;
		return true;
	}

	//商品登録。itemの長さを一個増やしてそこに新しい商品の情報を追加するだけ
	void register(String code, String name, String author, String label, String year, String month, String day, String newOld, String type) {
		//「新しく追加される商品のID」は「それまでに存在してた奴の中で一番でかい番号+1」
		//んで、「それまでに存在してた奴の中で一番でかい番号」はつねに配列のケツにある
		int id;
		if (item.length > 0) {
			id = Integer.parseInt(item[item.length - 1].getId()) + 1;
		} else {
			id = 1;
		}
		createNewInstance();
		int iid = item.length - 1;
		item[iid].setId(Integer.toString(id));
		item[iid].setCode(code);
		item[iid].setName(name);
		item[iid].setAuthor(author);
		item[iid].setLabel(label);
		item[iid].setYear(year);
		item[iid].setMonth(month);
		item[iid].setDay(day);
		item[iid].setNewOld(newOld);
		item[iid].setType(type);
	}
	String registerMSG(String code, String name, String author, String label, String year, String month, String day, String newOld, String type) {
		StringBuffer msg = new StringBuffer("以下の内容で登録を行いました");
		msg.append(sep);
		msg.append("ID:\t");
		msg.append(item[item.length - 1].getId());
		msg.append(sep);
		msg.append("名前:\t");
		msg.append(name);
		msg.append("コード:\t");
		msg.append(code);
		msg.append(sep);
		msg.append("著作者:\t");
		msg.append(author);
		msg.append(sep);
		msg.append("レーベル:\t");
		msg.append(label);
		msg.append(sep);
		msg.append("年月日:\t");
		msg.append(year);
		msg.append("/");
		msg.append(month);
		msg.append("/");
		msg.append(day);
		msg.append(sep);
		msg.append("新旧:\t");
		msg.append(newOld);
		msg.append(sep);
		msg.append("種類:\t");
		msg.append(type);
		msg.append(sep);
		return msg.toString();
	}

	String registerErrorMSG(String code, String name, String author, String label, String year, String month, String day, String newOld, String type) {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		if (code.equals("")) {
			msg.append("コードが空欄です");
			msg.append(sep);
		}
		if (!isNum(code)) {
			msg.append("コードは数値です");
			msg.append(sep);
		}
		if (name.equals("")) {
			msg.append("名前が空欄です");
			msg.append(sep);
		}
		if (author.equals("")) {
			msg.append("著作者が空欄です");
			msg.append(sep);
		}
		if (label.equals("")) {
			msg.append("レーベルが空欄です");
			msg.append(sep);
		}
		//下2つはありえない仕様のため死に分岐だが、一応
		if (year.equals("") || month.equals("") || day.equals("")) {
			msg.append("年月日が空欄です");
			msg.append(sep);
		}
		if (newOld.equals("")) {
			msg.append("新旧が空欄です");
			msg.append(sep);
		}
		if (type.equals("")) {
			msg.append("新旧が空欄です");
			msg.append(sep);
		}

		return msg.toString();
	}
	boolean canModify (String id, String code) {	//検証が必要なのは「IDが数字か」「指定IDの商品が存在するか」「コードが数字か」。
		//codeが空文字列でなく、かつ数値でない
		if (!code.equals("") &&!isNum(code))
			return false;
		//アイテムがないor欄が空っぽ
		if (searchId(id) == -1)
			return false;
		return true;
	}

	void modify(String id, String code, String name, String author, String label, String year, String month, String day, String newOld, String type, boolean available) {
		int iid = searchId(id);
		item[iid].setCode(code);
		item[iid].setAuthor(author);
		item[iid].setLabel(label);
		item[iid].setYear(year);
		item[iid].setMonth(month);
		item[iid].setDay(day);
		item[iid].setNewOld(newOld);
		item[iid].setType(type);
		item[iid].setAvailable(available);
	}

	String modifyMSG(String id, String code, String name, String author, String label, String year, String month, String day, String newOld, String type,boolean available) {
		StringBuffer msg = new StringBuffer("以下の内容で変更を行いました");
		msg.append(sep);
		msg.append("ID:\t");
		msg.append(id);
		msg.append(sep);
		msg.append("商品名:\t");
		msg.append(name);
		msg.append(sep);
		msg.append("コード:\t");
		msg.append(code);
		msg.append(sep);
		msg.append("著作者:\t");
		msg.append(author);
		msg.append(sep);
		msg.append("レーベル:\t");
		msg.append(label);
		msg.append(sep);
		msg.append("年月日:\t");
		msg.append(year);
		msg.append("/");
		msg.append(month);
		msg.append("/");
		msg.append(day);
		msg.append(sep);
		msg.append("新旧:\t");
		msg.append(newOld);
		msg.append(sep);
		msg.append("種類:\t");
		msg.append(type);
		msg.append(sep);
		msg.append("状況:\t" + (available ? "利用可" : "削除"));
		return msg.toString();
	}

	//modify時エラーメッセージ
	String modifyErrorMSG(String id, String code) {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		if (id.equals("")) {
			msg.append("idが空欄です");
		} else if (!isNum(id)) {
			msg.append("IDは数値です");
			msg.append(sep);
		} else if (searchId(id) == -1) {
			msg.append("指定IDの商品が存在しません");
		}
		if (!code.equals("") && !isNum(code)) {
			msg.append("コードは数値です");
			msg.append(sep);
		}
		return msg.toString();
	}


	//貸出時の存在確認に失敗した時用
	//それ以外で呼ばないこと
	String notExistErrorMSG() {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		msg.append("指定された商品IDの商品は存在しません");
		return msg.toString();
	}

	//貸出時用
	//それ以外では使わない
	String getNewOld(String id) {
		String newOld = "";
		if (id.equals("")) {
			newOld = "";
		} else if (searchId(id) > 0) {
			newOld = item[Integer.parseInt(id)].getNewOld();
		}
		return newOld;
	}

	//以下はユーティリティメソッド

	//新しくインスタンスを作成する	//registerの一連の流れの一部
	private void createNewInstance() {
		buffer = new Item[item.length];	//itemと同じだけのbufferを用意する
		initInstance(buffer);			//イニシャライズする
		copyInstance(buffer,item);		//itemの内容をbufferに移す
		item = new Item[item.length + 1];	//itemの長さを1増やして作りなおす
		initInstance(item);				//イニシャライズする
		copyInstance(item,buffer);		//bufferからitemに内容を戻す
		clearBuffer(); 					//バッファをgcさせる
	}

	//バッファクリア用
	//商品件数が10000とかになるとバッファだけでだいぶメモリを食うはずなので（そしてバッファは純粋に計算にしか使わないので）、配列を宣言しなおしてGCさせておく
	private void clearBuffer() {
		buffer = new Item[0];
	}

	//インスタンス初期化用
	//配列を作りなおしたら呼ぶこと
	private void initInstance (Item[] init) {
		for (int i = 0; i < init.length; i++) {
			init[i] = new Item();
		}
	}

	//配列のコピーに使用する
	//oriの内容をcopに移す
	//短い方の配列の長さ回繰り返す
	//arraycopyやcloneでもいいが、引数が煩雑になりすぎるし、あれらほど高機能なものは要求していない
	//1行ごとが簡潔になるほうが見やすくていいはずだ。
	private void copyInstance (Item[] cop, Item ori[]) {	
		int len = (cop.length > ori.length ? ori.length : cop.length);
		for (int i = 0; i < len; i++) {
			cop[i] = ori[i];
		}
	}



	private boolean isNum(String str) {
		long a; //電話番号がintだと受け切れない
		try {
			a = Long.parseLong(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	int getItemNum() {
		return item.length;
	}
}