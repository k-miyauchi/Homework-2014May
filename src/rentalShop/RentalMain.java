package rentalShop;
import java.util.*;

/**
 * @author kbc14a12
 *貸出情報管理クラス
 */
public class RentalMain {
	private Rental[] rental = new Rental[0];
	private Rental[] buffer = new Rental[0];
	protected String sep = System.getProperty("line.separator"); //改行用
	int maxRent = 0;

	RentalMain(int maxRent) {
		initInstance(rental);
		initInstance(buffer);
		this.maxRent = maxRent;
	}

	String[] toCSV() {
		String[] csv = new String[rental.length];
		for (int i = 0; i < csv.length; i++) {
			csv[i] = rental[i].toCSV();
		}
		return csv;
	}

	void fromCSV(String[] csv) {
		for (int i = 0; i < csv.length; i++	) {
			createNewInstance();
			rental[i].fromCSV(csv[i]);
		}
	}

	//変更/削除時用
	//いわゆる「検索」に使う奴ではない
	int searchId(String id) {
		int index = -1;
		if (isNum(id)) {
			for (int i = 0; i < rental.length; i++) {
				if (id.equals(rental[i].getId())) {
					index = i;
					break; //残り探してもしょうがないのでbreak
				}
			}
		}
		return index;
	}
	//削除済みのやつを入れない
	int searchIdRejectNonAvailable(String id) {
		int index = -1;
		if (isNum(id)) {
			for (int i = 0; i < rental.length; i++) {
				if (id.equals(rental[i].getId())) {
					if (id.equals(rental[i].getIsRent())) {
						index = i;
						break; //残り探してもしょうがないのでbreak
					}
				}
			}
		}
		return index;
	}
	String[][] search(String id, String customerId, String itemId, String date, String available) {
		int[] result = new int[0];
		int[] buffer = new int[0];
		for (int i = 0; i < rental.length; i++) {
			//すべてに対し「空文字列である、または入力された情報が一致している」が真なら通る
			//ようは空欄じゃない奴に対してのand検索
			String avail = (rental[i].getIsRent() ? "貸出中" : "返却");
			//延滞を指定した場合、「貸出中でかつ期限切れのもの」も抽出する
			if ((available.equals("延滞") && avail.equals("貸出中") && rental[i].isDelayed()) || (id.equals("") || rental[i].getId().equals(id)) &&  (customerId.equals("") || rental[i].getCustomerId().equals(customerId)) && (date.equals("") || rental[i].getDateForSearch().equals(date)) && (available.equals("") || available.equals(avail))) {
				int length = rental[i].getLength();
				boolean tmp = false;
				for (int j = 0; j < length; j++) {
					if (itemId.equals("") || rental[i].getItemId(j).equals(itemId)) {
						tmp = true;
					}
				}
				if (tmp) {
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
		}
		//検索が終わったらデータ生成;
		String[][] sArray = new String [result.length][5 + maxRent];
		int index = 0;
		for (int i = 0; i< sArray.length; i++) {
			index = result[i];
			sArray[i][0] = rental[index].getId();
			sArray[i][1] = rental[index].getCustomerId();
			sArray[i][2]	 = rental[index].getDate();
			sArray[i][3] = rental[index].getReturnDate();
			if (rental[index].getIsRent()) {
				if (rental[index].isDelayed()) {
					sArray[i][4] = "延滞";
				} else {
					sArray[i][4] = "貸出中";
				}
			} else {
				sArray[i][4] = "返却";
			}
			for (int j = 5; j < sArray[i].length ; j++) {
				//名残
				//別の方法で解決したので不要だが一応残しておこう
				if (null != rental[index].getItemId(j - 5)) {
					sArray[i][j] = rental[index].getItemId(j - 5);
				} else {
					sArray[i][j] = "";
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

	//JTable用の商品情報を返すメソッドgetAllRentalinfo。
	String[][] getAllInfo() {
		String[][] sArray = new String[rental.length][5 + maxRent];
		for (int i = 0; i< sArray.length; i++) {
			sArray[i][0] = rental[i].getId();
			sArray[i][1] = rental[i].getCustomerId();
			sArray[i][2]	 = rental[i].getDate();
			sArray[i][3] = rental[i].getReturnDate();
			if (rental[i].getIsRent()) {
				if (rental[i].isDelayed()) {
					sArray[i][4] = "延滞";
				} else {
					sArray[i][4] = "貸出中";
				}
			} else {
				sArray[i][4] = "返却";
			}
			for (int j = 5; j < sArray[i].length ; j++) {
				//名残
				//別の方法で解決したので不要だが一応残しておこう
				if (null != rental[i].getItemId(j - 5)) {
					sArray[i][j] = rental[i].getItemId(j - 5);
				} else {
					sArray[i][j] = "";
				}
			}
		}
		return sArray;
	}

	String[] getAllColumnInfo() {
		String[] sArray = new String[5 + maxRent];
		sArray[0] = "ID";
		sArray[1] = "会員ID";
		sArray[2] = "貸出日";
		sArray[3] = "返却期限";
		sArray[4] = "状態";
		for (int i = 5; i < sArray.length; i++) {
			sArray[i] = "商品ID"; 
		}
		return sArray;
	}

	boolean canRegister(String customerId, String[] itemId) {
		//顧客IDが数字じゃない、顧客IDがすでに借りてる、商品IDが全て空欄、商品IDの空欄じゃない奴の中に数字じゃないのが混ざってる、商品ID欄の数値にダブりがあるとfalse
		//「指定IDの客・商品がない」はすでに弾いてるしここからでは確認不可能なのでスルー
		if (!isNum(customerId))
			return false;
		if (isAllVoid(itemId))
			return false;
		if (isHeAlreadyRent(customerId)) 
			return false;
		for (int i = 0; i < itemId.length; i++) {
			if (!itemId[i].equals("") && !isNum(itemId[i])) //短絡評価って便利ダナー
				return false;
			if (isItRent(itemId[i]))
				return false;
		}
		if (isDoubleRent(itemId))
			return false;		
		return true;
	}

	//商品登録。rentalの長さを一個増やしてそこに新しい商品の情報を追加するだけ
	void register(String customerId, String[] itemId, String[] itemNewOld) {
		int id;
		if (rental.length > 0) {
			id = Integer.parseInt(rental[rental.length - 1].getId()) + 1;
		} else {
			id = 1;
		}
		createNewInstance();
		int iid = rental.length - 1;
		String y,m,d;
		String yr, mr, dr;
		Calendar cal = Calendar.getInstance(); //貸出日は現在の日付
		Calendar returnCal = Calendar.getInstance(); //返却日算出用
		shiftStrs(itemId);
		shiftStrs(itemNewOld);
		//全部旧作なら七日間
		int itemNum = 0;
		for (int i = 0; i < itemNewOld.length; i++) {
			if (!itemNewOld[i].equals(""))
				itemNum++;
		}
		int tmp = 0;
		for (int i = 0; i < itemNewOld.length; i++) {
			if (itemNewOld[i].equals("旧作"))
				tmp++;
		}
		if (tmp == itemNum) {
			returnCal.add(Calendar.DATE, 7);
		} else {
			returnCal.add(Calendar.DATE, 3);
		}
		y = Integer.toString(cal.get(Calendar.YEAR));
		m = Integer.toString(cal.get(Calendar.MONTH));
		d = Integer.toString(cal.get(Calendar.DATE));
		yr = Integer.toString(returnCal.get(Calendar.YEAR));
		mr = Integer.toString(returnCal.get(Calendar.MONTH));
		dr = Integer.toString(returnCal.get(Calendar.DATE));
		rental[iid].setId(Integer.toString(id));
		rental[iid].setCustomerId(customerId);
		rental[iid].setItemId(itemId);
		rental[iid].setYear(y);
		rental[iid].setMonth(m);
		rental[iid].setDay(d);
		rental[iid].setReturnYear(yr);
		rental[iid].setReturnMonth(mr);
		rental[iid].setReturnDay(dr);
		rental[iid].killFractions();
	}
	String registerMSG(String customerId, String itemId[]) {
		StringBuffer msg = new StringBuffer("以下の内容で登録を行いました");
		int index = rental.length - 1;
		msg.append(sep);
		msg.append("この貸出のID:\t");
		msg.append(rental[index].getId());
		msg.append(sep);
		msg.append("会員ID:\t");
		msg.append(customerId);
		msg.append(sep);
		msg.append("商品ID:\t以下の通り");
		msg.append(sep);
		for (int i = 0; i < itemId.length; i++) {
			msg.append("\t");
			msg.append(itemId[i]);
			msg.append(sep);
		}
		msg.append("日付:\t");
		msg.append(rental[index].getDate());
		msg.append(sep);
		msg.append("返却期限");
		msg.append(rental[index].getReturnDate());
		return msg.toString();
	}

	String registerErrorMSG(String customerId, String itemId[]) {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		if (!isNum(customerId)) {
			msg.append("会員IDは数値です");
			msg.append(sep);
		}
		if (isAllVoid(itemId)) {
			msg.append("商品IDが全て空欄です");
			msg.append(sep);
		}
		for (int i = 0; i < itemId.length; i++) {
			if (!itemId[i].equals("") && !isNum(itemId[i])) { //短絡評価って便利ダナー
				msg.append(i);
				msg.append("番の商品ID欄が数値ではありません");
				msg.append(sep);
			}
			if (isItRent(itemId[i])) {
				msg.append("ID:");
				msg.append(itemId[i]);
				msg.append("はすでに借りられています");
				msg.append(sep);
			}
		}
		if (isHeAlreadyRent(customerId)) {
			msg.append("その会員はすでにレンタルをしています");
			msg.append(sep);
		}
		if (isDoubleRent(itemId)) {
			msg.append("同じIDが複数入力されています");
			msg.append(sep);
		}
		return msg.toString();
	}
	boolean canModify (String id, String customerId, String[] itemId) {
		//顧客IDが数字じゃない、商品IDが全て空欄、商品IDの空欄じゃない奴の中に数字じゃないのが混ざっている　とfalse
		if (searchId(id) == -1)
			return false;
		if (!customerId.equals("") && !isNum(customerId))
			return false;
		for (int i = 0; i < itemId.length; i++) {
			if (!itemId[i].equals("") && !isNum(itemId[i])) //短絡評価って便利ダナー
				return false;
			if (isItRent(itemId[i])) {
				//変更先IDがすでに借りられており、しかもそれが今いじろうとしてる記録のものでない場合、データ上二重にレンタルすることになるので駄目
				boolean flag = false;
				int length = 0;
				int iid = Integer.parseInt(id);
				length = rental[iid].getLength();
				for (int j = 0; j < length; j++) {
					if (itemId[i].equals(rental[i].getItemId(j))) {
						flag = true;
					}
				}
				if (!flag) {
					return false;
				}
			}
			if (isDoubleRent(itemId))
				return false;	
		}
		return true;
	}

	void modify(String id, String customerId, String[] itemId, boolean isRent) {
		int iid = searchId(id);
		shiftStrs(itemId);
		rental[iid].setCustomerId(customerId);
		rental[iid].setItemId(itemId);
		rental[iid].setIsRent(isRent);
	}

	String modifyMSG(String id, String customerId, String itemId[], boolean isRent) {
		StringBuffer msg = new StringBuffer("以下の内容で修正を行いました");
		msg.append(sep);
		msg.append("貸出ID:\t");
		msg.append(id);
		msg.append(sep);
		msg.append("会員ID:\t");
		msg.append(customerId);
		msg.append(sep);
		msg.append("商品ID:\t以下の通り");
		msg.append(sep);
		for (int i = 0; i < itemId.length; i++) {
			msg.append("\t");
			msg.append(itemId[i]);
			msg.append(sep);
		}
		msg.append("状況:\t" + (isRent ? "貸出中" : "返却"));
		return msg.toString();
	}

	String modifyErrorMSG(String id, String customerId, String[] itemId) {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		if (searchId(id) == -1) {
			msg.append("指定のIDは存在しません");
		}
		if (!isNum(id)) {
			msg.append("貸出IDは数値です");
			msg.append(sep);
		}
		if (!customerId.equals("") && !isNum(customerId)) {
			msg.append("会員IDは数値です");
			msg.append(sep);
		}
		for (int i = 0; i < itemId.length; i++) {
			if (!itemId[i].equals("") && !isNum(itemId[i])) { //短絡評価って便利ダナー
				msg.append(i);
				msg.append("番の商品ID欄が数値ではありません");
				msg.append(sep);
			}
			if (isItRent(itemId[i])) {
				msg.append("ID:");
				msg.append(itemId[i]);
				msg.append("はすでに借りられています");
				msg.append(sep);
			}
		}
		if (isHeAlreadyRent(customerId)) {
			msg.append("その会員はすでにレンタルをしています");
			msg.append(sep);
		}
		if (isDoubleRent(itemId)) {
			msg.append("同じIDが複数入力されています");
			msg.append(sep);
		}
		return msg.toString();
	}
	String deleteErrorMSG(String id) {
		StringBuffer msg = new StringBuffer("エラー！");
		msg.append(sep);
		if (!isNum(id))
			msg.append("IDは数値です");
		return msg.toString();
	}

	//以下はユーティリティメソッド

	//客がすでに何か借りているかどうか
	boolean isHeAlreadyRent(String customerId) {
		for (int i = 0; i < rental.length; i++) {
			if (customerId.equals(rental[i].getCustomerId()) && rental[i].getIsRent()) 
				return true;
		}
		return false;
	}
	//商品がすでに借りられているかどうか
	boolean isItRent(String itemId) {
		int length = 0;
		if (!itemId.equals("")) {
			for (int i = 0; i < rental.length; i ++) {
				length = rental[i].getLength();
				for (int j = 0; j < length; j++) {
					if (rental[i].getIsRent() && rental[i].getIsRent() && itemId.equals(rental[i].getItemId(j))) {
						return true;
					}
				}
			}
			return false;
		}
		return false;
	}

	//同じIDのものを二つ以上借りようとしていないか
	boolean isDoubleRent(String[] itemId) {
		for (int i = 0; i < itemId.length - 1; i++) {
			for (int j = i + 1; j < itemId.length; j++) {
				//空文字列でなくてダブってる場合、trueを返す
				if (!itemId[i].equals("") && !itemId[j].equals("") && itemId[i].equals(itemId[j]))
					return true;
			}
		}
		return false;
	}

	//新しくインスタンスを作成する
	//registerの一連の流れの一部	
	private void createNewInstance() {
		buffer = new Rental[rental.length];	//rentalと同じだけのbufferを用意する
		initInstance(buffer);			//イニシャライズする
		copyInstance(buffer,rental);		//rentalの内容をbufferに移す
		rental = new Rental[rental.length + 1];	//rentalの長さを1増やして作りなおす
		initInstance(rental);				//イニシャライズする
		copyInstance(rental,buffer);		//bufferからrentalに内容を戻す
		clearBuffer(); 					//バッファをgcさせる
	}



	//バッファクリア用
	//商品件数が10000とかになるとバッファだけでだいぶメモリを食うはずなので（そしてバッファは純粋に計算にしか使わないので）、配列を宣言しなおしてGCさせておく
	private void clearBuffer() {
		buffer = new Rental[0];
	}

	//インスタンス初期化用
	//配列を作りなおしたら呼ぶこと
	private void initInstance (Rental[] init) {
		for (int i = 0; i < init.length; i++) {
			init[i] = new Rental(maxRent);
		}
	}

	//配列のコピーに使用する
	//oriの内容をcopに移す
	//短い方の配列の長さ回繰り返す
	//arraycopyやcloneでもいいが、引数が煩雑になりすぎるし、あれらほど高機能なものは要求していない
	//1行ごとが簡潔になるほうが見やすくていいはずだ。
	private void copyInstance (Rental[] cop, Rental ori[]) {	
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

	//全部空文字列かどうかチェックする
	private boolean isAllVoid(String str[]) {
		int voidNum = 0;
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals(""))
				voidNum++;
		}
		if (voidNum == str.length)
			return true;
		return false;
	}

	//渡された文字配列を手前に詰めるやつ
	private void shiftStrs(String str[]) {
		int voidIndex;
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals("")) {
				voidIndex = i;
				for (int j = voidIndex; j < str.length; j++) {
					if (!str[j].equals("")) {
						str[voidIndex] = str[j];
						str[j] = "";
						break;	//草葉の陰でダイクストラおじさんが泣いてる 許してやで
					}
				}
			}
		}
	}

	int getRentalNum() {
		return rental.length;
	}
}

