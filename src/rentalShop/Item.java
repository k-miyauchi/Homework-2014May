package rentalShop;
import java.util.*;

class Item {
	private String id = "";
	private String code = "";
	private String name = "";
	private String author = "";
	private String label = "";
	private Calendar date = Calendar.getInstance();
	private String  newOld = "新作";
	private boolean isAvailable = true;
	private String type = "CD";
	//セッターゲッター類
	//セッターについては引数が空文字列かどうかの判断ももたせる（商品情報変更時用）
	void setId (String id) {
		this.id = id;
	}
	void setCode (String code) {
		if (!code.equals(""))
			this.code = code;
	}
	void setName (String name) {
		if (!name.equals(""))
			this.name = name;
	}
	void setAuthor (String author) {
		if (!author.equals(""))
			this.author = author;
	}
	void setLabel (String label) {
		if (!label.equals(""))
			this.label = label;
	}
	void setYear(String year) {
		if (!year.equals(""))
			date.set(Calendar.YEAR, Integer.parseInt(year));
	}
	void setMonth (String month) {
		if (!month.equals(""))
			date.set(Calendar.MONTH, Integer.parseInt(month) - 1);
	}
	void setDay (String day) {
		if (!day.equals(""))
			date.set(Calendar.DATE, Integer.parseInt(day));
	}
	void setNewOld (String newOld) {
		if (!newOld.equals(""))
			this.newOld = newOld;
	}
	void setType (String type) {
		if (!type.equals(""))
			this.type = type;
	}
	void setAvailable (boolean Available) {
			this.isAvailable = Available;
	}
	String getId() {
		return id;
	}
	String getCode() {
		return code;
	}
	String getName() {
		return name;
	}
	String getAuthor() {
		return author;
	}
	String getLabel() {
		return label;
	}
	String getYear() {
		return Integer.toString(date.get(Calendar.YEAR));
	}
	String getMonth() {
		return Integer.toString(date.get(Calendar.MONTH));
	}
	String getDay() {
		return Integer.toString(date.get(Calendar.DATE));
	}
	String getDate() {
		return Integer.toString(date.get(Calendar.YEAR)) + "/" + Integer.toString(date.get(Calendar.MONTH) + 1) + "/" + Integer.toString(date.get(Calendar.DATE));
	}
	String getNewOld() {
		return newOld;
	}
	boolean getAvailable() {
		return isAvailable;
	}
	String getType() {
		return type;
	}
	//StringBufferのほうが速いって聞いたんですよね。。
	//とくにこのメソッドは終了時に大量に呼び出すので速いに越したことはないでしょう。
	String toCSV() {
		StringBuffer csv = new StringBuffer();
		csv.append(id);
		csv.append(",");
		csv.append(code);
		csv.append(",");
		csv.append(name);
		csv.append(",");
		csv.append(author);
		csv.append(",");
		csv.append(label);
		csv.append(",");
		csv.append(getYear());
		csv.append(",");
		csv.append(getMonth());
		csv.append(",");
		csv.append(getDay());
		csv.append(",");
		csv.append(newOld);
		csv.append(",");
		csv.append(type);
		csv.append(",");
		csv.append(isAvailable);
		return csv.toString();
		
	}
	String getDateForSearch() {
		return Integer.toString(date.get(Calendar.YEAR)) + Integer.toString(date.get(Calendar.MONTH) + 1) + Integer.toString(date.get(Calendar.DATE));
	}
	void fromCSV(String csv) {
		String[] sArray;
		
		sArray = csv.split(",");
		
		this.id = sArray[0];
		this.code = sArray[1];
		this.name = sArray[2];
		this.author = sArray[3];
		this.label = sArray[4];
		this.date.set(Integer.parseInt(sArray[5]), Integer.parseInt(sArray[6]), Integer.parseInt(sArray[7]));
		this.newOld = sArray[8];
		this.type = sArray[9];
		this.isAvailable = (sArray[10].equals("true") ? true : false);
	}
}
