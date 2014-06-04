package rentalShop;

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;

import javax.swing.*;

//レンタルDVDショップ管理ソフト「Kosuzu」、検索用ウインドウ「Akyuu」
//取説
//チェーンでない小規模なレンタルビデオ店を想定したプログラムです。田舎のエロビデオ屋みたいな。
//この店では商品一つ一つに固有のIDがつきます。同一種類の商品を複数登録する場合、必要回登録してください。登録ボタン連打すればよいです。
//この店では社員と会員は完全に別の物として扱われます。社員がビデオを借りる場合、会員としての登録を行ってください。
//この店では追加貸出は受け付けません。一部返却も受け付けません。一括レンタル・一括返却のみです。
//この店では上記「一部返却なし」のシステムのため、貸出商品に新作が混ざっていた場合は一律で3日（新作）返却になります。
//この店では申込用紙を預かり、店員がそれを入力して会員登録します。客に渡す会員カードには、裏側にシステムに指示されたIDを書き込みます。カラオケ屋っぽいですね。
//この店では社員情報の閲覧・登録・変更、会員情報の閲覧ができるのは社員と責任者のみです。IDを知る術がないので実質会員の変更と削除もバイトは行えません。
//新旧の変更は手動で行います。そこ組み込みにするといざ新旧変更のタイミングを変えたいというときに面倒なためです。どうせ物理商品のほうにも新旧タグ張り替えとかするんだから、そんときについでにデータも書き換えてください。
//情報変更モード時に空欄をつくると「そこのデータは変更しない」と解釈されます。一覧から該当商品を探していちいち同じものを入力する必要はありません。
//利用不可になった商品、退会した会員などの削除処理は「変更」モードから行えます。データ上には残るので復旧も行えます。
//ソフト起動時にログインを要求されます。社員登録時に用意したパスワードを入力してください。
//csvファイルがないとエラーメッセージが出ますが、初回起動時は正常です。初回起動時は「root」というユーザーが生成されます。
//五分以上画面遷移がなかった場合、画面遷移時に再度のログインを要求されます。なお、ログイン作業も五分以内に行ってください。
//セキュリティのため、「秘密の質問」を設けていません。パスを忘れた際の復旧方法は存在しません。

/**
 * @author kbc14a12
 * RentalShopクラスの真下に位置する最上位クラス。
 * GUI用のViewクラスとフレーム、各種管理クラス、ユーティリティクラスを所持する
 */
public class View {
	//いちおう最大レンタル数も決めておく
	protected final int maxRent = 6;
	//セキュリティクリアランス
	protected int grade  = -1;
	//セッション用
	protected Session session = new Session();
	//IO
	protected FileIO fileIO = new FileIO();
	//各種管理
	protected ItemMain itemMain = new ItemMain();
	protected EmployeeMain employeeMain = new EmployeeMain();
	protected CustomerMain customerMain = new CustomerMain();
	protected RentalMain rentalMain = new RentalMain(maxRent);
	//モード管理
	protected String largeMode = "商品";
	protected String smallMode = "登録";
	protected String subLargeMode = "商品";
	//エリア
	//ボーダーレイアウトの各領域を配置コンポーネントの種類に対応させてグルーピング
	protected LoginArea loginArea = new LoginArea();
	protected NorthArea northArea = new NorthArea();
	protected EastArea eastArea = new EastArea();
	protected CenterArea centerArea = new CenterArea();
	protected SouthArea southArea = new SouthArea();
	protected SubCenterArea subCenterArea = new SubCenterArea();
	protected EastArea subEastArea = new EastArea();
	//フレーム
	//ログイン、メイン（登録とか）、サブ（一覧、検索）
	protected JFrame loginFrame = new JFrame("ログイン");
	protected JFrame mainFrame = new JFrame("Kosuzu");
	protected JFrame subFrame = new JFrame("Akyuu");

	//コア部分
	void main() {
		initialize();
		centerArea.showThePanel();
		subCenterArea.showThePanel();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(300, 550);
		mainFrame.setResizable(false);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.add(eastArea,BorderLayout.EAST);
		mainFrame.add(northArea,BorderLayout.NORTH);
		mainFrame.add(centerArea,BorderLayout.CENTER);
		mainFrame.add(southArea,BorderLayout.SOUTH);
		mainFrame.addWindowListener(new Listener());
		//こっから検索窓
		subFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	//検索窓は消させない
		subFrame.setSize(600, 600);
		subFrame.setResizable(false);
		subFrame.add(subCenterArea,BorderLayout.CENTER);
		subFrame.add(subEastArea, BorderLayout.EAST);
		loginFrame.setSize(250, 200);
		loginFrame.setResizable(false);
		loginFrame.add(loginArea);
		loginFrame.setVisible(true);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/**
	 * イニシャライズ用
	 * 具体的にはFileIOクラスからCSVファイルを読んでitemやemployeeなどを生成する
	 */
	void initialize() {
		String[][] csv = new String[4][];
		csv[0] = fileIO.read("Item.csv");
		itemMain.fromCSV(csv[0]);
		csv[1] = fileIO.read("Employee.csv");
		employeeMain.fromCSV(csv[1]);
		csv[2] = fileIO.read("Customer.csv");
		customerMain.fromCSV(csv[2]);
		csv[3] = fileIO.read("Rental.csv");
		rentalMain.fromCSV(csv[3]);
	}


	/**
	 * @author kbc14a12
	 *メインフレーム中央領域クラス
	 *登録用のフィールドやボックスを表示する
	 */
	protected class CenterArea extends JPanel {
		protected Dimension dim = new Dimension(220,15);
		protected Dimension dimPanel = new Dimension(210,20);
		protected JTextField codeField = new JTextField();
		protected JTextField nameField = new JTextField();
		protected JTextField authorField = new JTextField();
		protected JTextField labelField = new JTextField();
		protected JTextField phoneField = new JTextField();
		protected JTextField addressField = new JTextField();
		protected JTextField idField = new JTextField();
		protected JTextField customerIdField = new JTextField();
		protected JTextField[] itemIdField = new JTextField[maxRent];
		protected JPasswordField passField = new JPasswordField();
		protected JPasswordField passConfirmField = new JPasswordField();
		protected DateBoxes dateBox = new DateBoxes(DateBoxes.FOR_REGISTER);
		protected ComboBoxes newOldBox = new ComboBoxes(ComboBoxes.FOR_NEWOLD, ComboBoxes.FOR_REGISTER);
		protected ComboBoxes gradeBox = new ComboBoxes(ComboBoxes.FOR_GRADE, ComboBoxes.FOR_REGISTER);
		protected ComboBoxes sexBox = new ComboBoxes(ComboBoxes.FOR_SEX, ComboBoxes.FOR_REGISTER);
		protected ComboBoxes availBox = new ComboBoxes(ComboBoxes.FOR_AVAILABLE, ComboBoxes.FOR_MODIFY);
		protected ComboBoxes typeBox = new ComboBoxes(ComboBoxes.FOR_TYPE, ComboBoxes.FOR_REGISTER);
		protected ComboBoxes rentBox = new ComboBoxes(ComboBoxes.FOR_RENT, ComboBoxes.FOR_REGISTER);
		protected JLabel codeLabel = new JLabel("社員コード"); //ITEM操作の時は「商品コード」に直す
		protected JLabel nameLabel = new JLabel("姓名"); //ITEM操作の時は「商品名」に直す
		protected JLabel authorLabel = new JLabel("著作者");
		protected JLabel labelLabel = new JLabel("レーベル");	//最高級にアホなインスタンス名だと思う。
		protected JLabel phoneLabel = new JLabel("電話番号(ハイフン不要)");
		protected JLabel addressLabel = new JLabel("住所");
		protected JLabel dateLabel = new JLabel("生年月日"); //ITEM操作の時は「発売日」に直す
		protected JLabel sexLabel = new JLabel("性別");
		protected JLabel availLabel = new JLabel("状況");
		protected JLabel newOldLabel = new JLabel("新旧");
		protected JLabel typeLabel = new JLabel("種類");
		protected JLabel rentLabel = new JLabel("貸出");
		protected JLabel gradeLabel = new JLabel("等級");
		protected JLabel passLabel = new JLabel("password");
		protected JLabel passConfirmLabel = new JLabel("password確認");
		protected JLabel idLabel = new JLabel("変更対象のID");
		protected JLabel customerLabel = new JLabel("借りる客のID");
		protected JLabel rentItemLabel = new JLabel("貸出商品のID");

		/**
		 * コンストラクタではコンポーネントのサイズを決定する
		 */
		CenterArea() {

			for (int i = 0; i < itemIdField.length ; i++) {
				itemIdField[i] = new JTextField();
				itemIdField[i].setPreferredSize(dim);
			}
			customerIdField.setPreferredSize(dim);
			codeField.setPreferredSize(dim);
			nameField.setPreferredSize(dim);
			authorField.setPreferredSize(dim);
			labelField.setPreferredSize(dim);
			idField.setPreferredSize(dim);
			passField.setPreferredSize(dim);
			passConfirmField.setPreferredSize(dim);
			dateBox.setPreferredSize(dimPanel); //これだけパネルなので独自に設定する
			newOldBox.setPreferredSize(dimPanel);
			availBox.setPreferredSize(dimPanel);
			phoneField.setPreferredSize(dim);
			addressField.setPreferredSize(dim);
			typeBox.setPreferredSize(dimPanel);
		}
		/**
		 * EastAreaとNorthAreaのボタン押したときに呼び出す用
		 * 現在のlargeModeとsmallModeに基づいてコンポーネントを再配置する
		 */
		void showThePanel() {
			removeAll();
			if (largeMode.equals("商品")) {
				if (smallMode.equals("登録")) {
					dateBox = new DateBoxes(DateBoxes.FOR_REGISTER);
					newOldBox = new ComboBoxes(ComboBoxes.FOR_NEWOLD, ComboBoxes.FOR_REGISTER);
					availBox = new ComboBoxes(ComboBoxes.FOR_AVAILABLE, ComboBoxes.FOR_REGISTER);
					typeBox = new ComboBoxes(ComboBoxes.FOR_TYPE, ComboBoxes.FOR_REGISTER);
				} else {
					typeBox = new ComboBoxes(ComboBoxes.FOR_TYPE, ComboBoxes.FOR_MODIFY);
					dateBox = new DateBoxes(DateBoxes.FOR_MODIFY);
					newOldBox = new ComboBoxes(ComboBoxes.FOR_NEWOLD, ComboBoxes.FOR_MODIFY);
					availBox = new ComboBoxes(ComboBoxes.FOR_AVAILABLE, ComboBoxes.FOR_MODIFY);
					add(idLabel);
					add(idField);
				}
				codeLabel = new JLabel("商品コード"); //ITEM操作の時は「商品コード」に直す
				nameLabel = new JLabel("商品名"); //ITEM操作の時は「商品名」に直す
				dateLabel = new JLabel("発売日"); //ITEM操作の時は「発売日」に直す
				add(codeLabel);
				add(codeField);
				add(nameLabel);
				add(nameField);
				add(authorLabel);
				add(authorField);
				add(labelLabel);
				add(labelField);
				add(typeLabel);
				add(typeBox);
				add(dateLabel);
				add(dateBox);
				add(newOldLabel);
				add(newOldBox);
				add(typeLabel);
				add(typeBox);
				add(availLabel);
				add(availBox);
				//使用されるコンポーネントに共通のものが多いのでまとめる
			} else if (largeMode.equals("店員") || largeMode.equals("会員")) {
				if (smallMode.equals("登録")) {
					dateBox = new DateBoxes(DateBoxes.FOR_REGISTER);
					gradeBox =  new ComboBoxes(ComboBoxes.FOR_GRADE, ComboBoxes.FOR_REGISTER);
					sexBox = new ComboBoxes(ComboBoxes.FOR_SEX, ComboBoxes.FOR_REGISTER);
				} else {
					dateBox = new DateBoxes(DateBoxes.FOR_MODIFY);
					gradeBox =  new ComboBoxes(ComboBoxes.FOR_GRADE, ComboBoxes.FOR_MODIFY);
					sexBox = new ComboBoxes(ComboBoxes.FOR_SEX, ComboBoxes.FOR_MODIFY);
					availBox = new ComboBoxes(ComboBoxes.FOR_AVAILABLE, ComboBoxes.FOR_MODIFY);
					add(idLabel);
					add(idField);
				}
				codeLabel = new JLabel("社員コード");
				nameLabel = new JLabel("姓名");
				dateLabel = new JLabel("誕生日");
				if (largeMode == "店員") {
					add(codeLabel);
					add(codeField);
				}
				add(nameLabel);
				add(nameField);
				if (largeMode.equals("店員")) {
					add(gradeLabel);
					add(gradeBox);
				}
				add(sexLabel);
				add(sexBox);
				add(dateLabel);
				add(dateBox);
				add(addressLabel);
				add(addressField);
				add(phoneLabel);
				add(phoneField);
				add(availLabel);
				add(availBox);
				if (largeMode.equals("店員")) {
					add(passLabel);
					add(passField);
					add(passConfirmLabel);
					add(passConfirmField);
				}
			} else if (largeMode.equals("貸出")) {
				if (smallMode.equals("登録")) {
					rentBox = new ComboBoxes(ComboBoxes.FOR_RENT, ComboBoxes.FOR_REGISTER);
				} else {
					add(idLabel);
					add(idField);
					availBox = new ComboBoxes(ComboBoxes.FOR_AVAILABLE, ComboBoxes.FOR_MODIFY);
					rentBox = new ComboBoxes(ComboBoxes.FOR_RENT, ComboBoxes.FOR_MODIFY);
				}
				add(customerLabel);
				add(customerIdField);
				add(rentItemLabel);
				for (int i = 0; i < itemIdField.length; i++) {
					add(itemIdField[i]);
				}
				add(rentLabel);
				add(rentBox);
			}
			//一度消さないと再描画されない
			setVisible(false);
			setVisible(true);
		}
	}


	/**
	 * @author kbc14a12
	 *日付のボックス用クラス。3つ表示するため、パネルから継承
	 *subcenterの検索用エリアにはこれでなくJTextFieldで入力する（収まらないので）
	 */
	protected class DateBoxes extends JPanel	{
		static final boolean FOR_REGISTER = true;
		static final boolean FOR_MODIFY = false; 
		protected JComboBox[] boxForDate = new JComboBox[3];
		DateBoxes(boolean isRegist) {
			for (int i = 0; i < boxForDate.length; i++) {
				boxForDate[i] = new JComboBox();
			}
			final Calendar cal = Calendar.getInstance();
			String[][] date = new String[3][];
			if (isRegist) {
				date[0] = new String[100];
				date[1] = new String[12];
				date[2] = new String[31];
				for (int i = 0; i < date[0].length; i++) { //年のみデフォで今年を表示させたいので逆順でやる
					date[0][i] = Integer.toString(cal.get(Calendar.YEAR) - i);
				}
				for (int i = 1; i < date.length; i++) {
					for (int j = 0; j < date[i].length; j++) {
						date[i][j] = Integer.toString(j + 1);
					}
				}
			} else {
				date[0] = new String[101];
				date[1] = new String[13];
				date[2] = new String[32];
				for (int i = 0; i < date.length; i++) {
					date[i][0] = "";
				}
				for (int i = 1; i < date[0].length; i++) { //年のみデフォで今年を表示させたいので逆順でやる
					date[0][i] = Integer.toString(cal.get(Calendar.YEAR) + 1 - i);
				}
				for (int i = 1; i < date.length; i++) {
					for (int j = 1; j < date[i].length; j++) {
						date[i][j] = Integer.toString(j);
					}
				}
			} 
			//生成された文字配列によってコンボボックスを生成する
			boxForDate[0] = new JComboBox(date[0]);
			boxForDate[1]  = new JComboBox(date[1]);
			boxForDate[2]  = new JComboBox(date[2]);
			boxForDate[0].setPreferredSize(new Dimension(60,18));
			boxForDate[1].setPreferredSize(new Dimension(40,18));
			boxForDate[2].setPreferredSize(new Dimension(40,18));
			add(boxForDate[0]);
			add(boxForDate[1]);
			add(boxForDate[2]);
		}

	}

	/**
	 * @author kbc14a12
	 * コンボボックス用クラス
	 * モード指定用のStataticフィールドを指定してインスタンスを生成する
	 */
	protected class ComboBoxes extends JComboBox {
		static final boolean FOR_REGISTER = true;
		static final boolean FOR_MODIFY = false;
		static final String FOR_SEX = "性別";
		static final String FOR_NEWOLD = "新旧";
		static final String FOR_GRADE = "等級";
		static final String FOR_TYPE = "種類";
		static final String FOR_AVAILABLE = "利用";
		static final String FOR_RENT = "貸出";
		static final boolean FOR_SEARCH_AVAILABLE = true;
		protected final Dimension dimBox = new Dimension(210, 17);
		protected String[] boxData = new String[0];
		/**
		 * @param mode　FOR_SEXからFOR_RENTまで
		 * @param isRegist　登録用か、変更用か
		 */
		ComboBoxes(String mode, boolean isRegist) {
			if (mode.equals("性別")) {
				if (isRegist) {
					boxData = new String[3];
					boxData[0] = "男";
					boxData[1] = "女";
					boxData[2] = "その他";

				} else {
					boxData = new String[4];
					boxData[0] = "";
					boxData[1] = "男";
					boxData[2] = "女";
					boxData[3] = "その他";
				}
			} else if (mode.equals("新旧")) {
				if (isRegist) {
					boxData = new String[2];
					boxData[0] = "新作";
					boxData[1] = "旧作";
				} else {
					boxData = new String[3];
					boxData[0] = "";
					boxData[1] = "新作";
					boxData[2] = "旧作";
				}
			} else if (mode.equals("等級")) {
				if (isRegist) {
					boxData = new String[3];
					boxData[0] = "責任者";
					boxData[1] = "社員";
					boxData[2] = "バイト";
				} else {
					boxData = new String[4];
					boxData[0] = "";
					boxData[1] = "責任者";
					boxData[2] = "社員";
					boxData[3] = "バイト";
				}
			} else if (mode.equals("種類")) {
				if (isRegist) {
					boxData = new String[2];
					boxData[0] = "CD";
					boxData[1] = "DVD";
				} else {
					boxData = new String[3];
					boxData[0] = "";
					boxData[1] = "CD";
					boxData[2] = "DVD";
				}
			} else if (mode.equals("利用")) {
				if (isRegist) {
					boxData = new String[1];
					boxData[0] = "利用状況-利用可";
				} else {
					boxData = new String[2];
					boxData[0] = "利用可";
					boxData[1] = "削除";
				}
			} else if (mode.equals("貸出")) {
				if (isRegist) {
					boxData = new String[1];
					boxData[0] = "貸出状況-貸出";
				} else {
					boxData = new String[2];
					boxData[0] = "貸出中";
					boxData[1] = "削除";
				}
			}
			for (int i = 0; i < boxData.length; i++) {
				addItem(boxData[i]);
			}
			setPreferredSize(dimBox);
		}

		/**
		 * @param search　FOR_SEARCH_AVAILABLE専用
		 */
		ComboBoxes(boolean search) {
			boxData = new String[4];
			boxData[0] = "";
			boxData[1] = "貸出中";
			boxData[2] = "返却";
			boxData[3] = "延滞";
			for (int i = 0; i < boxData.length; i++) {
				addItem(boxData[i]);
			}
			setPreferredSize(dimBox);
		}
	}

	/**
	 * @author kbc14a12
	 *メインフレーム上側エリア
	 *smallModeを変更するためのラジオボタンと、現在のモードを表示するラベル
	 */
	protected class NorthArea extends JPanel{
		protected JRadioButton registerButton = new JRadioButton("登録",true);
		protected JRadioButton modifyButton = new JRadioButton("変更/削除");
		protected ButtonGroup radioGroup = new ButtonGroup();
		protected 	JLabel modeLabel = new JLabel(largeMode + smallMode);
		NorthArea() {
			radioGroup.add(registerButton);
			radioGroup.add(modifyButton);
			registerButton.addItemListener(new Listener());
			modifyButton.addItemListener(new Listener());
			setLayout(new GridLayout(1,4));
			add(registerButton);
			add(modifyButton);
			add(modeLabel);
		}
		/**
		 * モード表示ラベル変更用
		 */
		void redrawLabel() {
			remove(modeLabel);
			modeLabel = new JLabel(largeMode + smallMode);
			add(modeLabel);
			setVisible(false);
			setVisible(true);
		}
	}

	/**
	 * @author kbc14a12
	 *メインフレーム・サブフレーム東側エリア
	 *largeMode,subLargeMode変更用
	 */
	protected class EastArea extends JPanel{
		protected JButton itemButton = new JButton("商品");
		protected JButton employeeButton = new JButton("店員");
		protected JButton customerButton = new JButton("会員");
		protected JButton rentButton = new JButton("貸出");
		EastArea() {
			setLayout(new GridLayout(4,1));
			add(itemButton);
			add(employeeButton);
			add(customerButton);
			add(rentButton);
			itemButton.addActionListener(new Listener());
			employeeButton.addActionListener(new Listener());
			customerButton.addActionListener(new Listener());
			rentButton.addActionListener(new Listener());
		}
	}

	/**
	 * @author kbc14a12
	 *メインフレーム下側エリア
	 *登録・変更の実行ボタン、テキストフィールド等のリセットボタン
	 */
	protected class SouthArea extends JPanel {
		protected JButton executeButton = new JButton("実行");
		protected JButton clearButton = new JButton("クリア");
		SouthArea() {
			add(executeButton);
			add(clearButton);
			executeButton.addActionListener(new Listener());
			clearButton.addActionListener(new Listener());
		}
	}



	/**
	 * @author kbc14a12
	 *サブフレーム中央エリア
	 *中身は検索用フィールドのある北側パネル、一覧表示のJTableを置く南側パネルに二分される
	 */
	protected class SubCenterArea extends JPanel {
		JTable infoTable = new JTable();
		JScrollPane infoScrollPane = new JScrollPane(infoTable);
		protected Dimension dim = new Dimension(150,15);
		protected Dimension dimPanel = new Dimension(140,20);
		protected JTextField codeField = new JTextField();
		protected JTextField nameField = new JTextField();
		protected JTextField authorField = new JTextField();
		protected JTextField labelField = new JTextField();
		protected JTextField phoneField = new JTextField();
		protected JTextField addressField = new JTextField();
		protected JTextField idField = new JTextField();
		protected JTextField customerIdField = new JTextField();
		protected JTextField itemIdField = new JTextField();
		protected JTextField dateField = new JTextField(); //DateBoxesだと収まらないのでテキストフィールド
		protected ComboBoxes newOldBox = new ComboBoxes(ComboBoxes.FOR_NEWOLD, ComboBoxes.FOR_MODIFY);
		protected ComboBoxes gradeBox = new ComboBoxes(ComboBoxes.FOR_GRADE, ComboBoxes.FOR_MODIFY);
		protected ComboBoxes sexBox = new ComboBoxes(ComboBoxes.FOR_SEX, ComboBoxes.FOR_MODIFY);
		protected ComboBoxes availBox = new ComboBoxes(ComboBoxes.FOR_SEARCH_AVAILABLE);
		protected ComboBoxes typeBox = new ComboBoxes(ComboBoxes.FOR_TYPE, ComboBoxes.FOR_MODIFY);
		protected ComboBoxes rentBox = new ComboBoxes(ComboBoxes.FOR_RENT, ComboBoxes.FOR_MODIFY);
		protected JLabel codeLabel = new JLabel("社員コード"); //ITEM操作の時は「商品コード」に直す
		protected JLabel nameLabel = new JLabel("姓名"); //ITEM操作の時は「商品名」に直す
		protected JLabel authorLabel = new JLabel("著作者");
		protected JLabel labelLabel = new JLabel("レーベル");	//最高級にアホなインスタンス名だと思う。
		protected JLabel phoneLabel = new JLabel("電話番号(ハイフン不要)");
		protected JLabel addressLabel = new JLabel("住所");
		protected JLabel dateLabel = new JLabel("生年月日"); //ITEM操作の時は「発売日」に直す
		protected JLabel sexLabel = new JLabel("性別");
		protected JLabel availLabel = new JLabel("状況");
		protected JLabel newOldLabel = new JLabel("新旧");
		protected JLabel typeLabel = new JLabel("種類");
		protected JLabel rentLabel = new JLabel("貸出");
		protected JLabel gradeLabel = new JLabel("等級");
		protected JLabel idLabel = new JLabel("ID");
		protected JLabel customerLabel = new JLabel("借りた客のID");
		protected JLabel rentItemLabel = new JLabel("貸出商品のID");
		protected JPanel northPanel = new JPanel();
		protected JPanel southPanel = new JPanel();
		protected JButton searchButton = new JButton("検索");

		SubCenterArea () {
			itemIdField.setPreferredSize(dim);
			customerIdField.setPreferredSize(dim);
			codeField.setPreferredSize(dim);
			nameField.setPreferredSize(dim);
			authorField.setPreferredSize(dim);
			labelField.setPreferredSize(dim);
			idField.setPreferredSize(dim);
			dateField.setPreferredSize(dim);
			newOldBox.setPreferredSize(dimPanel);
			availBox.setPreferredSize(dimPanel);
			phoneField.setPreferredSize(dim);
			addressField.setPreferredSize(dim);
			typeBox.setPreferredSize(dimPanel);
		}
		/**
		 * 再描画用メソッド　SubEastAreaのボタンを押すと呼び出される
		 * 空欄を作るため、すべてのComboBoxesはMODIFYモードで作ること
		 */
		void showThePanel() {
			northPanel.removeAll();
			southPanel.removeAll();
			removeAll();
			if (subLargeMode.equals("商品")) {
				northPanel.setLayout(new GridLayout(5,4));
				infoTable = new JTable(itemMain.getAllInfo() , itemMain.getAllColumnInfo()); //infoTableはデータ取得の都合上モード分岐内で作成
				newOldBox = new ComboBoxes(ComboBoxes.FOR_NEWOLD, ComboBoxes.FOR_MODIFY);
				typeBox = new ComboBoxes(ComboBoxes.FOR_TYPE, ComboBoxes.FOR_MODIFY);
				codeLabel = new JLabel("商品コード"); //ITEM操作の時は「商品コード」に直す
				nameLabel = new JLabel("商品名"); //ITEM操作の時は「商品名」に直す
				dateLabel = new JLabel("発売日"); //ITEM操作の時は「発売日」に直す
				northPanel.add(idLabel);
				northPanel.add(idField);
				northPanel.add(codeLabel);
				northPanel.add(codeField);
				northPanel.add(nameLabel);
				northPanel.add(nameField);
				northPanel.add(authorLabel);
				northPanel.add(authorField);
				northPanel.add(labelLabel);
				northPanel.add(labelField);
				northPanel.add(typeLabel);
				northPanel.add(typeBox);
				northPanel.add(dateLabel);
				northPanel.add(dateField);
				northPanel.add(newOldLabel);
				northPanel.add(newOldBox);
				northPanel.add(typeLabel);
				northPanel.add(typeBox);
				northPanel.add(availLabel);
				northPanel.add(availBox);
			} else if (subLargeMode.equals("店員")) {
				northPanel.setLayout(new GridLayout(5,4));
				infoTable = new JTable(employeeMain.getAllInfo(), employeeMain.getAllColumnInfo()); //infoTableはデータ取得の都合上モード分岐内で作成
				gradeBox =  new ComboBoxes(ComboBoxes.FOR_GRADE, ComboBoxes.FOR_MODIFY);
				sexBox = new ComboBoxes(ComboBoxes.FOR_SEX, ComboBoxes.FOR_MODIFY);
				codeLabel = new JLabel("社員コード");
				nameLabel = new JLabel("姓名");
				dateLabel = new JLabel("誕生日");
				northPanel.add(idLabel);
				northPanel.add(idField);
				northPanel.add(codeLabel);
				northPanel.add(codeField);
				northPanel.add(nameLabel);
				northPanel.add(nameField);
				northPanel.add(gradeLabel);
				northPanel.add(gradeBox);
				northPanel.add(sexLabel);
				northPanel.add(sexBox);
				northPanel.add(dateLabel);
				northPanel.add(dateField);
				northPanel.add(addressLabel);
				northPanel.add(addressField);
				northPanel.add(phoneLabel);
				northPanel.add(phoneField);
				northPanel.add(availLabel);
				northPanel.add(availBox);
			} else if (subLargeMode.equals("会員")) {
				northPanel.setLayout(new GridLayout(4,4));
				infoTable = new JTable(customerMain.getAllInfo(), customerMain.getAllColumnInfo()); //infoTableはデータ取得の都合上モード分岐内で作成
				gradeBox =  new ComboBoxes(ComboBoxes.FOR_GRADE, ComboBoxes.FOR_MODIFY);
				sexBox = new ComboBoxes(ComboBoxes.FOR_SEX, ComboBoxes.FOR_MODIFY);
				nameLabel = new JLabel("姓名");
				dateLabel = new JLabel("誕生日");
				northPanel.add(idLabel);
				northPanel.add(idField);
				northPanel.add(nameLabel);
				northPanel.add(nameField);
				northPanel.add(sexLabel);
				northPanel.add(sexBox);
				northPanel.add(dateLabel);
				northPanel.add(dateField);
				northPanel.add(addressLabel);
				northPanel.add(addressField);
				northPanel.add(phoneLabel);
				northPanel.add(phoneField);
				northPanel.add(availLabel);
				northPanel.add(availBox);
			} else if (subLargeMode.equals("貸出")) {
				northPanel.setLayout(new GridLayout(3,4));
				infoTable = new JTable(rentalMain.getAllInfo(), rentalMain.getAllColumnInfo());
				rentBox = new ComboBoxes(ComboBoxes.FOR_SEARCH_AVAILABLE);
				northPanel.add(idLabel);
				northPanel.add(idField);
				northPanel.add(customerLabel);
				northPanel.add(customerIdField);
				northPanel.add(rentItemLabel);
				northPanel.add(itemIdField);
				northPanel.add(rentLabel);
				northPanel.add(rentBox);
				northPanel.add(dateLabel);
				northPanel.add(dateField);
			}
			northPanel.add(Box.createRigidArea(new Dimension(10,1)));
			northPanel.add(searchButton);
			searchButton.addActionListener(new Listener());
			northPanel.setPreferredSize(new Dimension(500, 100));
			infoTable.setEnabled(false);
			infoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			infoScrollPane = new JScrollPane(infoTable);
			infoScrollPane.setPreferredSize(new Dimension(500, 400));
			southPanel.setPreferredSize(new Dimension(500,400));
			southPanel.add(infoScrollPane);
			add(northPanel,BorderLayout.NORTH);
			add(southPanel, BorderLayout.SOUTH);
			setVisible(false);
			setVisible(true);
		}
		/**
		 * @param sArray　JTable各セルデータ保管
		 * @param column　JTablej各コラムデータ保管
		 * searchButtonを押した際に呼び出される。渡されたデータに基づいたJTableを表示する。
		 */
		void showSearchResult(String[][] sArray, String[] column) {
			remove(southPanel);
			southPanel.remove(infoScrollPane);
			infoTable = new JTable(sArray, column);
			infoTable.setEnabled(false);
			infoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			infoScrollPane = new JScrollPane(infoTable);
			infoScrollPane.setPreferredSize(new Dimension(500, 400));
			southPanel.setPreferredSize(new Dimension(500,400));
			southPanel.add(infoScrollPane);
			add(northPanel,BorderLayout.NORTH);
			add(southPanel, BorderLayout.SOUTH);
			setVisible(false);
			setVisible(true);
		}
	}

	/**
	 * @author kbc14a12
	 *ログイン窓用エリア
	 *ログイン窓にはこれのみ載せる
	 */
	protected class LoginArea extends JPanel {
		JLabel passLabel = new JLabel("パスワード");
		JPasswordField passField = new JPasswordField();
		JButton loginButton = new JButton("ログイン");
		LoginArea() {
			passField.setPreferredSize(new Dimension(180,20));
			passField.setText("");
			add(passLabel);
			add(passField);
			add(loginButton);
			loginButton.addActionListener(new Listener());
		}
	}

	//リスナー
	//登録ボタン用処理はリスナー外にポイ（長すぎるので）
	/**
	 * @author kbc14a12
	 *リスナークラス。各領域クラスにリスナーを書くとわけが分からなくなるので統一。
	 *登録ボタンを押した時の処理はeditinfoクラスに丸投げ（長くなりすぎる）
	 */
	protected class Listener implements ActionListener, ItemListener, WindowListener {
		public void actionPerformed(ActionEvent e) {
			if (session.confirmSession()) { //時間内の操作か確認する
				//東エリア
				if (e.getSource() == eastArea.itemButton) {
					largeMode = "商品";
					centerArea.showThePanel();
					northArea.redrawLabel();
				} else if (e.getSource() == eastArea.employeeButton) {
					//店員情報の操作は社員以上
					if (grade >= 1) {
						largeMode = "店員";
						northArea.redrawLabel();
						centerArea.showThePanel();
					} else {
						JOptionPane.showMessageDialog(mainFrame,"あなたの社員等級ではその操作は行えません");
					}
				} else if (e.getSource() == eastArea.rentButton) {
					largeMode = "貸出";
					northArea.redrawLabel();
					centerArea.showThePanel();
				} else if (e.getSource() == eastArea.customerButton) {
					largeMode = "会員";
					northArea.redrawLabel();
					centerArea.showThePanel();
					//南エリア
				} else if (e.getSource() == southArea.executeButton) {
					//処理があまりにも縦長なのでクラスとして分離　見やすさ優先ということで
					EditInfo editInfo = new EditInfo();
					editInfo.execute(largeMode + smallMode);
				} else if (e.getSource() == southArea.clearButton) {
					centerArea.codeField.setText("");
					centerArea.nameField.setText("");
					centerArea.authorField.setText("");
					centerArea.labelField.setText("");
					centerArea.phoneField.setText("");
					centerArea.addressField.setText("");
					centerArea.idField.setText("");
					centerArea.customerIdField.setText("");
					for (int i = 0; i < centerArea.itemIdField.length; i++) {
						centerArea.itemIdField[i].setText("");
					}
					centerArea.passField.setText("");
					centerArea.passConfirmField.setText("");
					for (int i = 0; i < centerArea.dateBox.boxForDate.length; i++) {
						centerArea.dateBox.boxForDate[i].setSelectedIndex(0);
					}
					centerArea.newOldBox.setSelectedIndex(0);
					centerArea.gradeBox.setSelectedIndex(0);
					centerArea.sexBox.setSelectedIndex(0);
					centerArea.availBox.setSelectedIndex(0);
					centerArea.typeBox.setSelectedIndex(0);
					centerArea.rentBox.setSelectedIndex(0);
					//サブフレーム
				} else if (e.getSource() == subEastArea.itemButton) {
					subLargeMode = "商品";
					subCenterArea.showThePanel();
				} else if (e.getSource() == subEastArea.employeeButton) {
					if (grade >= 1)	{
						subLargeMode = "店員";
						subCenterArea.showThePanel();
					} else {
						JOptionPane.showMessageDialog(mainFrame,"あなたの社員等級ではその操作は行えません");
					}
				} else if (e.getSource() == subEastArea.customerButton) {
					if (grade >= 1) {
						subLargeMode = "会員";
						subCenterArea.showThePanel();
					} else {
						JOptionPane.showMessageDialog(mainFrame,"あなたの社員等級ではその操作は行えません");
					}
				} else if (e.getSource() == subEastArea.rentButton) {
					subLargeMode = "貸出";
					subCenterArea.showThePanel();
				} else if (e.getSource() == subCenterArea.searchButton) {
					Search search = new Search();
					search.execute();
					//ログインエリア
				} else if (e.getSource() == loginArea.loginButton) {
					char[] tmp = loginArea.passField.getPassword();
					String pass = new String(tmp);
					int i = employeeMain.login(pass); //loginが返してくるのは社員等級を表すint
					if (i >= 0) {
						grade = i;
						loginFrame.setVisible(false);
						subFrame.setVisible(true);
						mainFrame.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(mainFrame, "パスワードが違います。再度入力してください");
					}
				}
			} else { //セッションが切れてたら再ログインさせる
				session.sessionTimeOut();
			}
		}
		public void itemStateChanged(ItemEvent e) {
			if (session.confirmSession()) {
				if (e.getSource() == northArea.registerButton) {
					smallMode = "登録";
					centerArea.showThePanel();
					northArea.redrawLabel();
				} else if (e.getSource() == northArea.modifyButton) {
					smallMode = "変更";
					centerArea.showThePanel();
					northArea.redrawLabel();
				}
			} else {
				session.sessionTimeOut();
			}
		}
		public void windowOpened(WindowEvent e) {
		}
		/* (非 Javadoc)
		 * ウインドウが閉じられるときには情報を記録する
		 */
		public void windowClosing(WindowEvent e) {
			String[][] csv = new String[4][];
			csv[0] = itemMain.toCSV();
			fileIO.write(csv[0], "Item.csv");
			csv[1] = employeeMain.toCSV();
			fileIO.write(csv[1], "Employee.csv");
			csv[2] = customerMain.toCSV();
			fileIO.write(csv[2], "Customer.csv");
			csv[3] = rentalMain.toCSV();
			fileIO.write(csv[3], "Rental.csv");
		}
		//フォーカスあたったらCSVファイルのタイムスタンプ確認して更新されてたら読み直すか確認するとか楽しそう
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
	}


	/**
	 * @author kbc14a12
	 * 検索用クラスsearch
	 * 検索実行ボタンを押すとインスタンスが生成され、コンストラクタからSubCenterAreaのJTableにデータが渡る
	 */
	protected class Search {
		String code = subCenterArea.codeField.getText();
		String name = subCenterArea.nameField.getText();
		String author = subCenterArea.authorField.getText();
		String label = subCenterArea.labelField.getText();
		String phone = subCenterArea.phoneField.getText();
		String address = subCenterArea.addressField.getText();
		String id = subCenterArea.idField.getText();
		String date = subCenterArea.dateField.getText();
		String newOld = (String)subCenterArea.newOldBox.getSelectedItem();
		String grade = (String)subCenterArea.gradeBox.getSelectedItem();
		String sex = (String)subCenterArea.sexBox.getSelectedItem();
		String type = (String)subCenterArea.typeBox.getSelectedItem();
		String customerId = subCenterArea.customerIdField.getText();
		String itemId = subCenterArea.itemIdField.getText();
		String available = (String)subCenterArea.availBox.getSelectedItem();
		String rent = (String)subCenterArea.rentBox.getSelectedItem();
		void execute() {
			if (subLargeMode.equals("商品")) {
				String[][] sArray = itemMain.search(id, code, name, author, label, date, newOld, type, available);
				String[] column = itemMain.getAllColumnInfo();
				subCenterArea.showSearchResult(sArray, column);
			} else if (subLargeMode.equals("店員")) {
				String[][] sArray = employeeMain.search(id, code, name, grade, sex, address, phone, date, available);
				String[] column = employeeMain.getAllColumnInfo();
				subCenterArea.showSearchResult(sArray, column);
			} else if (subLargeMode.equals("会員")) {
				String[][] sArray = customerMain.search(id, name, sex, address, phone, date, available);
				String[] column = customerMain.getAllColumnInfo();
				subCenterArea.showSearchResult(sArray, column);
			} else if (subLargeMode.equals("貸出")) {
				String[][] sArray = rentalMain.search(id, customerId, itemId, date, rent);
				String[] column = rentalMain.getAllColumnInfo();
				subCenterArea.showSearchResult(sArray, column);
			}
		}
	}
	/**
	 * @author kbc14a12
	 *登録・変更ボタン押した時用の処理
	 *CenterAreaクラスの入力内容とlargeMode,smallModeの状況から登録や変更を行う
	 */
	protected class EditInfo {
		String code = centerArea.codeField.getText();
		String name = centerArea.nameField.getText();
		String author = centerArea.authorField.getText();
		String label = centerArea.labelField.getText();
		String phone = centerArea.phoneField.getText();
		String address = centerArea.addressField.getText();
		String id = centerArea.idField.getText();
		String pass;
		String passConfirm;
		String year = (String)centerArea.dateBox.boxForDate[0].getSelectedItem();
		String month = (String)centerArea.dateBox.boxForDate[1].getSelectedItem();
		String day = (String)centerArea.dateBox.boxForDate[2].getSelectedItem();
		String newOld = (String)centerArea.newOldBox.getSelectedItem();
		String grade = (String)centerArea.gradeBox.getSelectedItem();
		String sex = (String)centerArea.sexBox.getSelectedItem();
		String type = (String)centerArea.typeBox.getSelectedItem();
		String customerId = centerArea.customerIdField.getText();
		boolean available;
		boolean isRent;
		String[] itemId = new String[maxRent];
		/**
		 * コンストラクタ
		 * フィールドに、各種コンポーネントから取得したデータを入力
		 */
		EditInfo() {
			code = centerArea.codeField.getText();
			name = centerArea.nameField.getText();
			author = centerArea.authorField.getText();
			label = centerArea.labelField.getText();
			phone = centerArea.phoneField.getText();
			address = centerArea.addressField.getText();
			id = centerArea.idField.getText();
			char[] temp = centerArea.passField.getPassword(); 
			pass = new String(temp);
			temp = centerArea.passConfirmField.getPassword();
			passConfirm = new String(temp);
			customerId = centerArea.customerIdField.getText();
			for (int i = 0; i < itemId.length; i++) {
				itemId[i] = centerArea.itemIdField[i].getText();
			}
			year = (String)centerArea.dateBox.boxForDate[0].getSelectedItem();
			month = (String)centerArea.dateBox.boxForDate[1].getSelectedItem();
			day = (String)centerArea.dateBox.boxForDate[2].getSelectedItem();
			newOld = (String)centerArea.newOldBox.getSelectedItem();
			grade = (String)centerArea.gradeBox.getSelectedItem();
			sex = (String)centerArea.sexBox.getSelectedItem();
			type = (String)centerArea.typeBox.getSelectedItem();
			String a = (String)centerArea.availBox.getSelectedItem();
			available = (!a.equals("削除") ? true : false);
			isRent = (a.equals("貸出中") ? true : false);
		}
		/**
		 * @param str
		 * 登録・変更を実際に行うメソッド
		 * 行えるか確認→いけるなら実際の処理、いけないならエラーメッセージ　と言う流れ
		 */
		void execute(String str){
			try {
				if (str.equals("商品登録")) {
					if (itemMain.canRegister(code,name,author,label,year,month,day, newOld, type)) {
						itemMain.register(code,name,author,label,year,month,day, newOld,type);
						JOptionPane.showMessageDialog(mainFrame, itemMain.registerMSG(code,name,author,label,year,month,day,newOld,type));
					} else {
						JOptionPane.showMessageDialog(mainFrame, itemMain.registerErrorMSG(code,name,author,label,year,month,day, newOld,type));
					}
				} else if (str.equals("商品変更")) {
					if(itemMain.canModify(id,code)) {
						itemMain.modify(id,code,name,author,label,year,month,day,newOld, type, available);
						JOptionPane.showMessageDialog(mainFrame, itemMain.modifyMSG(id,code,name,author,label,year,month,day,newOld, type, available));
					} else {
						JOptionPane.showMessageDialog(mainFrame, itemMain.modifyErrorMSG(id,code));
					}
				} else if (str.equals("店員登録")) {
					if (employeeMain.canRegister(code,name,grade,sex,address,phone,year,month,day,pass,passConfirm)) {
						employeeMain.register(code,name,grade,sex,address,phone,year,month,day,pass);
						JOptionPane.showMessageDialog(mainFrame, employeeMain.registerMSG(code,name,grade,sex,address,phone,year,month,day,pass));
					} else {
						JOptionPane.showMessageDialog(mainFrame, employeeMain.registerErrorMSG(code,name,grade,sex,address,phone,year,month,day,pass,passConfirm));
					}
				} else if (str.equals("店員変更")) {
					if (employeeMain.canModify(id,code,phone, pass, passConfirm)) {
						employeeMain.modify(id,code,name,grade,sex,address,phone,year,month,day,available, pass);
						JOptionPane.showMessageDialog(mainFrame, employeeMain.modifyMSG(id,code,name,grade,sex,address,phone,year,month,day, available, pass));
					} else {
						JOptionPane.showMessageDialog(mainFrame, employeeMain.modifyErrorMSG(id, code, phone, pass, passConfirm));
					}
				} else if (str.equals("会員登録")) {
					if (customerMain.canRegister(name,sex,address,phone,year,month,day)) {
						customerMain.register(name,sex,address,phone,year,month,day);
						JOptionPane.showMessageDialog(mainFrame, customerMain.registerMSG(name,sex,address,phone,year,month,day));
					} else {
						JOptionPane.showMessageDialog(mainFrame, customerMain.registerErrorMSG(name,sex,address,phone,year,month,day));
					}
				} else if (str.equals("会員変更")) {
					if (customerMain.canModify(id,phone)) {
						customerMain.modify(id,name,sex,address,phone,year,month,day, available);
						JOptionPane.showMessageDialog(mainFrame, customerMain.modifyMSG(id,name,sex,address,phone,year,month,day, available));
					} else {
						JOptionPane.showMessageDialog(mainFrame, customerMain.modifyErrorMSG(id,phone));
					}
				} else if (str.equals("貸出登録")) {
					//会員と商品が存在するかのチェック
					boolean tempFlag = true;
					if (customerMain.searchIdRejectNonAvailable(customerId) == -1) { //退会済みの会員は借りれない
						JOptionPane.showMessageDialog(mainFrame, customerMain.notExistErrorMSG());
						tempFlag = false;
					} else {
						for (int i = 0; i < maxRent; i++) {
							if (!itemId[i].equals("") && itemMain.searchIdRejectNonAvailable(itemId[i]) == -1) { //削除済みの商品を借りることも無理
								tempFlag = false;
								JOptionPane.showMessageDialog(mainFrame, itemMain.notExistErrorMSG());
								break;
							}
						}
					}
					//チェックが終わったら内容を確認した上で実処理
					if (tempFlag) {
						if (rentalMain.canRegister(customerId, itemId)) {
							String[] itemNewOld = new String[itemId.length];
							for (int i = 0; i < itemId.length; i++) {
								itemNewOld[i] = itemMain.getNewOld(itemId[i]);
							}
							rentalMain.register(customerId, itemId, itemNewOld);
							JOptionPane.showMessageDialog(mainFrame, rentalMain.registerMSG(customerId, itemId));
						} else {
							JOptionPane.showMessageDialog(mainFrame, rentalMain.registerErrorMSG(customerId, itemId));
						}
					}
				} else if (str.equals("貸出変更")) {
					//会員と商品が(入力されてるなら）存在するかのチェック
					boolean tempFlag = true;
					if (!customerId.equals("") && customerMain.searchIdRejectNonAvailable(customerId) == -1) {
						JOptionPane.showMessageDialog(mainFrame, customerMain.notExistErrorMSG());
						tempFlag = false;
					}
					for (int i = 0; i < maxRent; i++) {
						if (!itemId[i].equals("") && itemMain.searchIdRejectNonAvailable(itemId[i]) == -1) {
							tempFlag = false;
							JOptionPane.showMessageDialog(mainFrame, itemMain.notExistErrorMSG());
							break;
						}
					}
					if (tempFlag) {
						if (rentalMain.canModify(id, customerId, itemId)) {
							rentalMain.modify(id, customerId, itemId, isRent);
							JOptionPane.showMessageDialog(mainFrame, rentalMain.modifyMSG(id,customerId, itemId, isRent));
						} else {
							JOptionPane.showMessageDialog(mainFrame, rentalMain.modifyErrorMSG(id, customerId, itemId));
						}
					}
				}
			} catch (NullPointerException e) {
				JOptionPane.showMessageDialog(mainFrame , "実行エラー");//これくらいならstaticのやつそのまま借りてきたんでいいや
			}
		}
	}
	//擬似セッション管理クラス
	/**
	 * @author kbc14a12
	 *擬似セッション管理クラス
	 *セッションが切れてたら再度ログイン
	 */
	class Session {
		Calendar lastOp = Calendar.getInstance();
		boolean confirmSession() {
			Calendar nowOp = Calendar.getInstance();
			//五分経過してたらセッションタイムアウトとする
			if (nowOp.getTimeInMillis() - lastOp.getTimeInMillis() > 300000) {
				lastOp = Calendar.getInstance();
				return false;
			}
			lastOp = Calendar.getInstance();
			return true;
		}
		void sessionTimeOut() {
			JOptionPane.showMessageDialog(mainFrame, "最後の画面遷移から5分経過しました。ログインしなおしてください");
			mainFrame.setVisible(false);
			subFrame.setVisible(false);
			loginArea.passField.setText("");
			loginFrame.setVisible(true);
		}
	}

	/**
	 * @author kbc14a12
	 *サンプルコードのやつをこのプログラムに合うようにいじくり回したもの
	 */
	class FileIO {
		/**
		 * @param filename　読むべきファイルネーム
		 * @return　拾ってきたcsvデータの入った文字配列
		 */
		String[] read(String filename) {
			String[] csv = new String[0];
			String[] buffer = new String[0];
			try {
				FileReader inFile = new FileReader(filename);
				BufferedReader inBuffer = new BufferedReader(inFile);
				String line;
				//行が存在するなら、配列を一個増やし、その末尾に追加
				while ((line = inBuffer.readLine()) != null) {
					buffer = new String[csv.length];
					for (int i = 0; i < csv.length; i++) {
						buffer[i] = csv[i];
					}
					csv = new String[csv.length + 1];
					for (int i = 0; i < buffer.length; i++) {
						csv[i] = buffer[i];
					}
					csv[csv.length - 1] = line;
				}
				inBuffer.close();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(mainFrame, "ファイル" + filename + "が見つかりません（初回起動の場合、正常です)");
				if (filename.equals("Employee.csv")) { //employeeが存在しない場合、システム側で追加
					employeeMain.register("0","root","責任者","男性","root","00000000000","0000","0","0","root");
					JOptionPane.showMessageDialog(mainFrame, "責任者等級の店員「root」を作成しました。パスワードrootでログインし、その後root店員を削除してください");
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(mainFrame, "ファイル" + filename + "の読み取り中にエラーが発生しました");
			}
			return csv;

		}

		/**
		 * @param csv　記録情報のcsv形式の文字配列
		 * @param filename　ファイル名
		 */
		void write(String[] csv, String filename) {
			try {
				FileWriter outFile = new FileWriter(filename);
				BufferedWriter outBuffer = new BufferedWriter(outFile);
				for (int i = 0; i < csv.length; i++) {
					outBuffer.write(csv[i]);
					outBuffer.newLine();
				}
				outBuffer.flush();
				outBuffer.close();
			} catch(IOException e) {
				JOptionPane.showMessageDialog(mainFrame, "ファイル" + filename + "の書込み中にエラーが発生しました");
			}
		}

	}
}
