package rentalShop;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author kbc14a12
 *従業員管理用クラス
 */
class EmployeeMain {
	private Employee[] employee = new Employee[0];
	private Employee[] buffer = new Employee[0];
	protected String sep = System.getProperty("line.separator"); //改行用

	EmployeeMain() {
		initInstance(employee);
		initInstance(buffer);
	}

	int login(String pass) {
		pass = intoMD5(pass);
		for (int i = 0; i < employee.length; i++) {
			if (pass.equals(employee[i].getPass()) && employee[i].getAvailable()) {
				int grade = employee[i].getGradeInt();
				return grade;
			}
		}
		return -1;
	}

	int searchId(String id) {
		int index = -1;
		if (isNum(id)) {
			for (int i = 0; i < employee.length; i++) {
				if (id.equals(employee[i].getId())) {
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
			for (int i = 0; i < employee.length; i++) {
				if (id.equals(employee[i].getId())) {
					if (employee[i].getAvailable()) {
						index = i;
						break; //残り探してもしょうがないのでbreak
					}
				}
			}
		}
		return index;
	}
	//検索してJTableに表示するのをぶん投げる用
	//投げ返すのはインデックス
	String[][] search(String id, String code, String name, String grade, String sex, String address, String phone, String date, String available) {
		int[] result = new int[0];
		int[] buffer = new int[0];
		for (int i = 0; i < employee.length; i++) {
			//すべてに対し「空文字列である、または入力された情報が一致している」が真なら通る
			//ようは空欄じゃない奴に対してのand検索
			String avail = (employee[i].getAvailable() ? "利用可" : "削除");
			if ((id.equals("") || employee[i].getId().equals(id)) && (code.equals("") || employee[i].getCode().equals(code)) && (name.equals("") || employee[i].getName().equals(name)) && (grade.equals("") || employee[i].getGrade().equals(grade)) && (sex.equals("") || employee[i].getSex().equals(sex))  && (address.equals("") || employee[i].getAddress().equals(address)) && (phone.equals("") || employee[i].getPhone().equals(phone)) && (date.equals("") || employee[i].getDateForSearch().equals(date)) && (available.equals("") || available.equals(avail))) {
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
					sArray[i][j] = employee[index].getId();
					break;
				case 1:
					sArray[i][j] = employee[index].getCode();
					break;
				case 2:
					sArray[i][j] = employee[index].getName();
					break;
				case 3:
					sArray[i][j] = employee[index].getGrade();
					break;
				case 4:
					sArray[i][j] = employee[index].getSex();
					break;
				case 5:
					sArray[i][j] = employee[index].getAddress();
					break;
				case 6:
					sArray[i][j] = employee[index].getPhone();
					break;
				case 7:
					sArray[i][j] = employee[index].getDate();
					break;
				case 8:
					sArray[i][j] = (employee[index].getAvailable() ? "在籍" : "削除");
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
		String[] csv = new String[employee.length];
		for (int i = 0; i < csv.length; i++) {
			csv[i] = employee[i].toCSV();
		}
		return csv;
	}

	void fromCSV(String[] csv) {
		for (int i = 0; i < csv.length; i++	) {
			createNewInstance();
			employee[i].fromCSV(csv[i]);
		}
	}
	//JTable用の商品情報を返すメソッドgetAllemployeeinfo。
	String[][] getAllInfo() {
		String[][] sArray = new String[employee.length][9];
		for (int i = 0; i < sArray.length; i++) {
			for (int j = 0; j < sArray[i].length; j ++) {
				switch (j) {
				case 0:
					sArray[i][j] = employee[i].getId();
					break;
				case 1:
					sArray[i][j] = employee[i].getCode();
					break;
				case 2:
					sArray[i][j] = employee[i].getName();
					break;
				case 3:
					sArray[i][j] = employee[i].getGrade();
					break;
				case 4:
					sArray[i][j] = employee[i].getSex();
					break;
				case 5:
					sArray[i][j] = employee[i].getAddress();
					break;
				case 6:
					sArray[i][j] = employee[i].getPhone();
					break;
				case 7:
					sArray[i][j] = employee[i].getDate();
					break;
				case 8:
					sArray[i][j] = (employee[i].getAvailable() ? "在籍" : "削除");
					break;
				}
			}
		}
		return sArray;
	}

	String[] getAllColumnInfo() {
		String[] sArray = {"ID","社員コード", "名前", "等級", "性別", "住所", "電話番号", "生年月日", "状況"};
		return sArray;
	}

	boolean canRegister(String code, String name, String grade, String sex, String address, String phone, String year, String month, String day, String pass, String passConfirm) {
		//引数に空文字列があったら偽
		if (code.equals("") || name.equals("") || grade.equals("") || sex.equals("") || address.equals("") || phone.equals("") || year.equals("") || month.equals("") || day.equals("") || pass.equals("") || passConfirm.equals(""))
			return false;
		if (!isNum(code) || !isNum(phone))
			return false;
		if (!pass.equals(passConfirm))
			return false;
		for (int i = 0; i < employee.length; i++) {
			if (employee[i].getPass().equals(intoMD5(pass)))
				return false;
		}
		return true;
	}
	String registerErrorMSG(String code, String name, String grade, String sex, String address, String phone, String year, String month, String day, String pass, String passConfirm) {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		if (code.equals("")) {
			msg.append("コードが空欄です");
			msg.append(sep);
		} else if (!isNum(code)) {
			msg.append("コードは数値です");
			msg.append(sep);
		}
		if (name.equals("")) {
			msg.append("名前が空欄です");
			msg.append(sep);
		}
		if (grade.equals("")) {
			msg.append("等級が空欄です");
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
		if (pass.equals("")) {
			msg.append("パスワードが空欄です");
			msg.append(sep);
		} else if (passConfirm.equals("")) {
			msg.append("パスワード確認が空欄です");
			msg.append(sep);
		} else if (!pass.equals(passConfirm)) {
			msg.append("パスワードとパスワード確認が不一致です");
			msg.append(sep);
		}
		for (int i = 0; i < employee.length; i++) {
			if (employee[i].getPass().equals(intoMD5(pass))) {
				msg.append("パスワードは使用されています");
				break;
			}
		}
		return msg.toString();
	}

	//商品登録。employeeの長さを一個増やしてそこに新しい商品の情報を追加するだけ
	void register(String code, String name, String grade, String sex, String address, String phone, String year, String month, String day, String pass) {
		int id;
		if (employee.length > 0) {
			id = Integer.parseInt(employee[employee.length - 1].getId()) + 1;
		} else {
			id = 1;
		}
		createNewInstance();
		int iid = employee.length - 1;
		employee[iid].setId(Integer.toString(id));
		employee[iid].setCode(code);
		employee[iid].setName(name);
		employee[iid].setGrade(grade);
		employee[iid].setSex(sex);
		employee[iid].setAddress(address);
		employee[iid].setPhone(phone);
		employee[iid].setYear(year);
		employee[iid].setMonth(month);
		employee[iid].setDay(day);
		pass = intoMD5(pass);
		employee[iid].setPass(pass);
	}
	String registerMSG(String code, String name, String grade, String sex, String address, String phone, String year, String month, String day, String pass) {
		StringBuffer msg = new StringBuffer("以下の内容で登録を行いました");
		msg.append(sep);
		msg.append("ID:\t");
		msg.append(employee[employee.length - 1].getId());
		msg.append(sep);
		msg.append("コード:\t");
		msg.append(code);
		msg.append(sep);
		msg.append("等級:\t");
		msg.append(grade);
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
		msg.append("パス:\t");
		msg.append("(セキュリティのため非表示)");
		msg.append(sep);
		return msg.toString();
	}
	boolean canModify (String id, String code, String phone, String pass,  String passConfirm) {	//検証が必要なのは「IDが数字か」「指定IDの商品が存在するか」「コードが数字か」「電話番号が数字か」。
		//code(phone)が空文字列でなく、かつ数値でない　or idが数値でない（空文字列もアウト） or 与えられたidがemployeeの範囲外（短絡評価によりidが数値の場合のみ来れるのでtry不要）
		if ((!code.equals("") &&!isNum(code)))
			return false;
		if (searchId(id) == -1)
			return false;
		if (!phone.equals("") && !isNum(phone))	
			return false;
		if (!pass.equals(passConfirm))
			return false;
		for (int i = 0; i < employee.length; i++) {
			if (employee[i].getPass().equals(intoMD5(pass)))
				return false;
		}
		return true;
	}

	void modify(String id, String code, String name, String grade, String sex, String address, String phone, String year, String month, String day, boolean available, String pass){
		int iid = searchId(id);
		employee[iid].setCode(code);
		employee[iid].setName(name);
		employee[iid].setGrade(grade);
		employee[iid].setSex(sex);
		employee[iid].setAddress(address);
		employee[iid].setPhone(phone);
		employee[iid].setYear(year);
		employee[iid].setMonth(month);
		employee[iid].setDay(day);
		employee[iid].setAvailable(available);
		employee[iid].setPass(pass);
	}

	String modifyMSG(String id, String code, String name, String grade, String sex, String address, String phone, String year, String month, String day, boolean available, String pass){
		//速いぞすごいぞStringBufferくん
		StringBuffer msg = new StringBuffer("以下の内容で変更を行いました");
		msg.append(sep);
		msg.append("ID:\t");
		msg.append(id);
		msg.append(sep);
		msg.append("名前");
		msg.append(name);
		msg.append(sep);
		msg.append("コード:\t");
		msg.append(code);
		msg.append(sep);
		msg.append("等級:\t");
		msg.append(grade);
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
		msg.append(sep);
		msg.append("パス:\t");
		msg.append("(セキュリティのため非表示)");
		msg.append(sep);
		return msg.toString();
	}
	String modifyErrorMSG(String id, String code, String phone, String pass, String passConfirm) {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		if (id.equals("")) {
			msg.append("IDが空欄です");
		} else if (!isNum(id)) {
			msg.append("IDは数値です");
			msg.append(sep);
		} else if (searchId(id) == -1) {
			msg.append("指定IDの店員が見つかりません");
			msg.append(sep);
		}

		if (!code.equals("") && !isNum(code)) {
			msg.append("コードは数値です");
			msg.append(sep);
		}
		if (!phone.equals("") && !isNum(phone)) {
			msg.append("電話番号は数値です");
			msg.append(sep);
		}
		if (pass.equals("")) {
			msg.append("パスワードが空欄です");
			msg.append(sep);
		} else if (passConfirm.equals("")) {
			msg.append("パスワード確認が空欄です");
			msg.append(sep);
		} else if (!pass.equals(passConfirm)) {
			msg.append("パスワードとパスワード確認が不一致です");
			msg.append(sep);
		}
		for (int i = 0; i < employee.length; i++) {
			if (employee[i].getPass().equals(intoMD5(pass))) {
				msg.append("パスワードは使用されています");
				break;
			}
		}
		return msg.toString();
	}
	boolean canDelete(String id) {
		if (searchId(id) == -1)
			return false;
		return true;
	}
	//以下はユーティリティメソッド

	//新しくインスタンスを作成する
	//registerの一連の流れの一部
	private void createNewInstance() {
		buffer = new Employee[employee.length];	//employeeと同じだけのbufferを用意する
		initInstance(buffer);			//イニシャライズする
		copyInstance(buffer,employee);		//employeeの内容をbufferに移す
		employee = new Employee[employee.length + 1];	//employeeの長さを1増やして作りなおす
		initInstance(employee);				//イニシャライズする
		copyInstance(employee,buffer);		//bufferからemployeeに内容を戻す
		clearBuffer(); 					//バッファをgcさせる
	}



	//バッファクリア用
	//商品件数が10000とかになるとバッファだけでだいぶメモリを食うはずなので（そしてバッファは純粋に計算にしか使わないので）、配列を宣言しなおしてGCさせておく
	private void clearBuffer() {
		buffer = new Employee[0];
	}

	//インスタンス初期化用
	//配列を作りなおしたら呼ぶこと
	private void initInstance (Employee[] init) {
		for (int i = 0; i < init.length; i++) {
			init[i] = new Employee();
		}
	}

	//配列のコピーに使用する
	//oriの内容をcopに移す
	//短い方の配列の長さ回繰り返す
	//arraycopyやcloneでもいいが、引数が煩雑になりすぎるし、あれらほど高機能なものは要求していない
	//1行ごとが簡潔になるほうが見やすくていいはずだ。
	private void copyInstance (Employee[] cop, Employee ori[]) {	
		int len = (cop.length > ori.length ? ori.length : cop.length);
		for (int i = 0; i < len; i++) {
			cop[i] = ori[i];
		}
	}

	//渡されたStringをバイト型に変換し配列に入れる
	//でそれをMD5に変換し、Stringに変換し、返す。
	private String intoMD5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] data = str.getBytes(); //文字コードからbyteに変換して配列に
			md.update(data); //dataをぶちこむ
			byte[] digestedData = md.digest(); //MD5に変換してbyteに入れる　digestしたらmdはリセットされる仕様らしいので注意
			StringBuilder MD5 = new StringBuilder(); //digestedData入れる奴　Stringに直して入れる奴
			for (int i = 0; i < digestedData.length; i++) {
				MD5.append(String.format("%x", digestedData[i]));
			}
			return MD5.toString();
		} catch (NoSuchAlgorithmException e) {} //ありえないけど入れないと怒られるので
		return "";
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


}