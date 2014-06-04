package rentalShop;

/**
 * @author kbc14a12
 *従業員管理クラス
 */
class CustomerMain {
	private Customer[] customer = new Customer[0];
	private Customer[] buffer = new Customer[0];
	protected String sep = System.getProperty("line.separator"); //改行用

	CustomerMain() {
		initInstance(customer);
		initInstance(buffer);
	}

	int searchId(String id) {
		int index = -1;
		if (isNum(id)) {
			for (int i = 0; i < customer.length; i++) {
				if (id.equals(customer[i].getId())) {
					index = i;
					break; //残り探してもしょうがないのでbreak

				}
			}
		}
		return index;
	}
	int searchIdRejectNonAvailable(String id) {
		int index = -1;
		if (isNum(id)) {
			for (int i = 0; i < customer.length; i++) {
				if (id.equals(customer[i].getId())) {
					if (customer[i].getAvailable()) {
						index = i;
						break; //残り探してもしょうがないのでbreak
					}
				}
			}
		}
		return index;
	}
	//検索用
	//投げ返すのはインデックス
	int[] serch(String mode, String query) {
		int[] result = new int[0];
		if (mode.equals("id")) {
			for (int i = 0; i < customer.length; i++) {
				if (customer[i].getId().equals(query)) {
					result = addNewArray(i, result);
				}
			}
		}
		return result;
	}

	//検索してJTableに表示するのをぶん投げる用
	//投げ返すのはインデックス
	String[][] search(String id, String name, String sex, String address, String phone, String date, String available) {
		int[] result = new int[0];
		int[] buffer = new int[0];
		for (int i = 0; i < customer.length; i++) {
			//すべてに対し「空文字列である、または入力された情報が一致している」が真なら通る
			//ようは空欄じゃない奴に対してのand検索
			String avail = (customer[i].getAvailable() ? "利用可" : "削除");
			if ((id.equals("") || customer[i].getId().equals(id)) &&  (name.equals("") || customer[i].getName().equals(name)) && (sex.equals("") || customer[i].getSex().equals(sex))  && (address.equals("") || customer[i].getAddress().equals(address)) && (phone.equals("") || customer[i].getPhone().equals(phone)) && (date.equals("") || customer[i].getDateForSearch().equals(date))  && (available.equals("") || available.equals(avail))) {
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
		String[][] sArray = new String [result.length][7];
		int index = 0;
		for (int i = 0; i < sArray.length; i++) {
			index = result[i];
			for (int j = 0; j < sArray[i].length; j ++) {
				switch (j) {
				case 0:
					sArray[i][j] = customer[index].getId();
					break;
				case 1:
					sArray[i][j] = customer[index].getName();
					break;
				case 2:
					sArray[i][j] = customer[index].getSex();
					break;
				case 3:
					sArray[i][j] = customer[index].getAddress();
					break;
				case 4:
					sArray[i][j] = customer[index].getPhone();
					break;
				case 5:
					sArray[i][j] = customer[index].getDate();
					break;
				case 6:
					sArray[i][j] = (customer[index].getAvailable() ? "利用可" : "削除");
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

	String[] toCSV() {
		String[] csv = new String[customer.length];
		for (int i = 0; i < csv.length; i++) {
			csv[i] = customer[i].toCSV();
		}
		return csv;
	}

	void fromCSV(String[] csv) {
		for (int i = 0; i < csv.length; i++	) {
			createNewInstance();
			customer[i].fromCSV(csv[i]);
		}
	}

	//JTable用の商品情報を返すメソッドgetAllcustomerinfo。
	String[][] getAllInfo() {
		String[][] sArray = new String[customer.length][7];
		for (int i = 0; i < sArray.length; i++) {
			for (int j = 0; j < sArray[i].length; j ++) {
				switch (j) {
				case 0:
					sArray[i][j] = customer[i].getId();
					break;
				case 1:
					sArray[i][j] = customer[i].getName();
					break;
				case 2:
					sArray[i][j] = customer[i].getSex();
					break;
				case 3:
					sArray[i][j] = customer[i].getAddress();
					break;
				case 4:
					sArray[i][j] = customer[i].getPhone();
					break;
				case 5:
					sArray[i][j] = customer[i].getDate();
					break;
				case 6:
					sArray[i][j] = (customer[i].getAvailable() ? "利用可" : "削除");
					break;
				}
			}
		}
		return sArray;
	}
	String[] getAllColumnInfo() {
		String[] sArray = {"ID","名前", "性別", "住所", "電話番号", "生年月日", "状況"};
		return sArray;
	}

	boolean canRegister(String name, String sex, String address, String phone, String year, String month, String day) {
		//引数に空文字列があったら偽
		if (name.equals("") || sex.equals("") || address.equals("") || phone.equals("") || year.equals("") || month.equals("") || day.equals("")) {
			return false;
		}
		if (!isNum(phone)) {
			return false;
		}
		return true;
	}
	String registerErrorMSG(String name, String sex, String address, String phone, String year, String month, String day) {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		if (name.equals("")) {
			msg.append("名前が空欄です");
			msg.append(sep);
		}
		if (sex.equals("")) {
			msg.append("性別が空欄です");
			msg.append(sep);
		}
		if (address.equals("")) {
			msg.append("住所が空欄です");
			msg.append(sep);
		}
		if (phone.equals("")) {
			msg.append("電話番号が空欄です");
			msg.append(sep);
		} else if (!isNum(phone)) {
			msg.append("電話番号は数値です");
			msg.append(sep);
		}
		if (year.equals("") || month.equals("") || day.equals("")) {
			msg.append("年月日が空欄です");
			msg.append(sep);
		}
		return msg.toString();
	}

	//商品登録。customerの長さを一個増やしてそこに新しい商品の情報を追加するだけ
	void register(String name, String sex, String address, String phone, String year, String month, String day) {
		int id;
		if (customer.length > 0) {
			id = Integer.parseInt(customer[customer.length - 1].getId()) + 1;
		} else {
			id = 1;
		}
		createNewInstance();
		int iid = customer.length - 1;
		customer[iid].setId(Integer.toString(id));
		customer[iid].setName(name);
		customer[iid].setSex(sex);
		customer[iid].setAddress(address);
		customer[iid].setPhone(phone);
		customer[iid].setYear(year);
		customer[iid].setMonth(month);
		customer[iid].setDay(day);
	}
	String registerMSG(String name, String sex, String address, String phone, String year, String month, String day) {
		StringBuffer msg = new StringBuffer("以下の内容で登録を行いました");
		msg.append(sep);
		msg.append("ID:\t");
		msg.append(customer[customer.length - 1].getId());
		msg.append(sep);
		msg.append("性別:\t");
		msg.append(sex);
		msg.append(sep);
		msg.append("住所:\t");
		msg.append(address);
		msg.append(sep);
		msg.append("電話番号:\t");
		msg.append(phone);
		msg.append(sep);
		msg.append("誕生日:\t");
		msg.append(year);
		msg.append("/");
		msg.append(month);
		msg.append("/");
		msg.append(day);
		msg.append(sep);
		return msg.toString();
	}
	boolean canModify (String id, String phone) {	//検証が必要なのは「IDが数字か」「指定IDの商品が存在するか」「コードが数字か」「電話番号が数字か」。
		//code(phone)が空文字列でなく、かつ数値でない　or idが数値でない（空文字列もアウト） or 与えられたidがcustomerの範囲外（短絡評価によりidが数値の場合のみ来れるのでtry不要）
		if (!phone.equals("") && !isNum(phone))
			return false;
		if(searchId(id)	== -1)
			return false;
		return true;
	}

	void modify(String id, String name, String sex, String address, String phone, String year, String month, String day, boolean available){
		int iid = searchId(id);
		customer[iid].setName(name);
		customer[iid].setSex(sex);
		customer[iid].setAddress(address);
		customer[iid].setPhone(phone);
		customer[iid].setYear(year);
		customer[iid].setMonth(month);
		customer[iid].setDay(day);
		customer[iid].setAvailable(available);
	}

	String modifyMSG(String id, String name, String sex, String address, String phone, String year, String month, String day, boolean available){
		//速いぞすごいぞStringBufferくん
		StringBuffer msg = new StringBuffer("以下の内容で変更を行いました");
		msg.append(sep);
		msg.append("ID:\t");
		msg.append(id);
		msg.append(sep);
		msg.append("ID:\t");
		msg.append(customer.length - 1);
		msg.append(sep);
		msg.append("性別:\t");
		msg.append(sex);
		msg.append(sep);
		msg.append("住所:\t");
		msg.append(address);
		msg.append(sep);
		msg.append("電話番号:\t");
		msg.append(phone);
		msg.append(sep);
		msg.append("誕生日:\t");
		msg.append(year);
		msg.append("/");
		msg.append(month);
		msg.append("/");
		msg.append(day);
		msg.append(sep);
		msg.append("状況:\t" + (available ? "利用可" : "削除"));
		return msg.toString();
	}
	String modifyErrorMSG(String id, String phone) {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		if (id.equals("")) {
			msg.append("idが空欄です");
		} else if (!isNum(id)) {
			msg.append("IDは数値です");
			msg.append(sep);
		} else if (searchId(id) == 1) {
			msg.append("指定IDの商品は見つかりませんでした");
		}
		if (!phone.equals("") && !isNum(phone)) {
			msg.append("電話番号は数値です");
			msg.append(sep);
		}
		return msg.toString();
	}
	boolean canDelete(String id) {
		if (searchId(id) == -1)
			return false;
		return true;
	}
	//貸出時の存在確認に失敗した時用
	//それ以外で呼ばないこと
	String notExistErrorMSG() {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		msg.append("指定された会員IDの会員は存在しません");
		return msg.toString();
	}

	//以下はユーティリティメソッド

	//新しくインスタンスを作成する
	//registerの一連の流れの一部
	private void createNewInstance() {
		buffer = new Customer[customer.length];	//customerと同じだけのbufferを用意する
		initInstance(buffer);			//イニシャライズする
		copyInstance(buffer,customer);		//customerの内容をbufferに移す
		customer = new Customer[customer.length + 1];	//customerの長さを1増やして作りなおす
		initInstance(customer);				//イニシャライズする
		copyInstance(customer,buffer);		//bufferからcustomerに内容を戻す
		clearBuffer(); 					//バッファをgcさせる
	}



	//バッファクリア用
	//商品件数が10000とかになるとバッファだけでだいぶメモリを食うはずなので（そしてバッファは純粋に計算にしか使わないので）、配列を宣言しなおしてGCさせておく
	private void clearBuffer() {
		buffer = new Customer[0];
	}

	//インスタンス初期化用
	//配列を作りなおしたら呼ぶこと
	private void initInstance (Customer[] init) {
		for (int i = 0; i < init.length; i++) {
			init[i] = new Customer();
		}
	}

	//配列のコピーに使用する
	//oriの内容をcopに移す
	//短い方の配列の長さ回繰り返す
	//arraycopyやcloneでもいいが、引数が煩雑になりすぎるし、あれらほど高機能なものは要求していない
	//1行ごとが簡潔になるほうが見やすくていいはずだ。
	private void copyInstance (Customer[] cop, Customer ori[]) {	
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

	int getCustomerNum() {
		return customer.length;
	}
}