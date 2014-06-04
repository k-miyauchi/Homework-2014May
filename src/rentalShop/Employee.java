package rentalShop;

import java.util.Calendar;

class Employee {
	private String id = "";
	private String name = "";
	private String sex = "";
	private String phone = "";
	private String address = "";
	private Calendar date = Calendar.getInstance();
	private String code = "";
	private String grade = "";
	private String pass = "";
	private boolean isAvailable = true;


	String toCSV() {
		StringBuffer csv = new StringBuffer();
		csv.append(id);
		csv.append(",");
		csv.append(code);
		csv.append(",");
		csv.append(name);
		csv.append(",");
		csv.append(grade);
		csv.append(",");
		csv.append(sex);
		csv.append(",");
		csv.append(phone);
		csv.append(",");
		csv.append(address);
		csv.append(",");
		csv.append(getYear());
		csv.append(",");
		csv.append(getMonth());
		csv.append(",");
		csv.append(getDay());
		csv.append(",");
		csv.append(isAvailable);
		csv.append(",");
		csv.append(pass);
		return csv.toString();
	}
	void fromCSV(String csv) {
		String[] sArray;
		sArray = csv.split(",");
		this.id = sArray[0];
		this.code = sArray[1];
		this.name = sArray[2];
		this.grade = sArray[3];
		this.sex = sArray[4];
		this.phone = sArray[5];
		this.address = sArray[6];
		this.date.set(Integer.parseInt(sArray[7]), Integer.parseInt(sArray[8]), Integer.parseInt(sArray[9]));
		this.isAvailable = (sArray[10].equals("true") ? true : false);
		this.pass = sArray[11];
	}


	//セッターゲッター類
	//セッターについては引数が空文字列かどうかの判断ももたせる（商品情報変更時用）
	void setId (String id) {
		this.id = id;
	}

	void setName (String name) {
		if (!name.equals(""))
			this.name = name;
	}

	void setSex (String sex) {
		if (!sex.equals(""))
			this.sex = sex;
	}
	void setPhone (String phone) {
		if (!phone.equals(""))
			this.phone = phone;
	}
	void setAddress(String address) {
		if (!address.equals(""))
			this.address = address;
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
	void setAvailable (boolean Available) {
		this.isAvailable = Available;
	}

	String getId() {
		return id;
	}

	String getName() {
		return name;
	}

	String getSex() {
		return sex;
	}
	String getPhone() {
		return phone;
	}
	//機能的にはgetDateがあれば十分だが、一応
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
	String getAddress() {
		return address;
	}
	//セッターゲッター類
	//セッターについては引数が空文字列かどうかの判断ももたせる（商品情報変更時用）
	void setCode (String code) {
		if (!code.equals(""))
			this.code = code;
	}

	void setGrade (String grade) {
		if (!code.equals(""))
			this.grade = grade;
	}
	void setPass (String pass) {
		if (!pass.equals(""))
			this.pass = pass;
	}

	String getCode() {
		return code;
	}
	String getGrade() {
		return grade;
	}
	//機能的にはgetDateがあれば十分だが、一応
	String getPass() {
		return pass;
	}
	boolean getAvailable() {
		return isAvailable;
	}
	String getDateForSearch() {
		return Integer.toString(date.get(Calendar.YEAR)) + Integer.toString(date.get(Calendar.MONTH) + 1) + Integer.toString(date.get(Calendar.DATE));
	}
	int getGradeInt() {
		if (grade.equals("責任者"))
			return 2;
		if (grade.equals("社員"))
			return 1;
		if (grade.equals("バイト"))
			return 0;
		return -1;
	}
}
