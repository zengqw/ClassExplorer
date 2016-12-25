package com.zqw;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ExportClassPage extends PreferencePage implements IWorkbenchPreferencePage {
	//定义属性页键值key
	private static final String KEY_IS_OPEN_FILE="KEY_IS_OPEN_FILE";
	private static final String KEY_CLASS_PRE_PATH="KEY_CLASS_PRE_PATH";
	private static final String KEY_WEB_SRC_PATH="KEY_WEB_SRC_PATH";
	private static final String KEY_WEB_DEST_PATH="KEY_WEB_DEST_PATH";
	// 定义属性默认值
	public static final boolean DEF_IS_OPEN_FILE =true;
	public static final String DEF_CLASS_PRE_PATH ="WEB-INF"+File.separator+"classes"+File.separator;
	public static final String DEF_WEB_SRC_PATH ="web"+File.separator;
	public static final String DEF_WEB_DEST_PATH ="target"+File.separator;
	
	// 属性页 需要设置的信息
	private Text textClassPrePath;
	private Text textWebSrcPath;
	private Text textWebDestPath;
	private Button button;
	
	// 定义一个IPreferenceStore对象
   private IPreferenceStore ps;
	 
	public ExportClassPage() {
		super();
	}

	public ExportClassPage(String title) {
		super(title);
	}

	public ExportClassPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	protected Control createContents(Composite parent) {
		 ps = getPreferenceStore();// 取得一个IPreferenceStore对象
		Composite composite = new Composite(parent, SWT.LEFT);
		 button = new Button(composite, SWT.LEFT|SWT.CHECK); 
		button.setText("导出文件之后是否打开对应文件夹");
		button.setBounds(5,5,400,15);
		button.setSelection(isOpenFile(ps));
		Composite composite2 = new Composite(parent, SWT.LEFT);
		Label label = new Label(composite2, SWT.LEFT);
		label.setText("class目录前缀:");
		label.setBounds(5,15,105,15);
		textClassPrePath = new Text(composite2, SWT.LEFT);
		textClassPrePath.setText(classPrePath(ps));
		textClassPrePath.setBounds(5, 35, 300, 20);
		label = new Label(composite2, SWT.LEFT);
		label.setText("web目录设置");
		label.setBounds(5,55,105,15);
		textWebSrcPath = new Text(composite2, SWT.LEFT);
		textWebSrcPath.setText(webSrcPath(ps));
		textWebSrcPath.setBounds(5, 75, 300, 20);
		label = new Label(composite2, SWT.LEFT);
		label.setText("文件目的地");
		label.setBounds(5,95,300,15);
		textWebDestPath = new Text(composite2, SWT.LEFT);
		textWebDestPath.setText(webDestPath(ps));
		textWebDestPath.setBounds(5, 115, 300, 20);
		
		
//		textTargetPath.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
////				TEMP_TARGET_PATH = textTargetPath.getText();
//			}
//		});
		return parent;
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return super.doGetPreferenceStore();
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/**
	 * 重置属性值
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		button.setSelection(DEF_IS_OPEN_FILE);
		textClassPrePath.setText(DEF_CLASS_PRE_PATH);
		textWebSrcPath.setText(DEF_WEB_SRC_PATH);
		textWebDestPath.setText(DEF_WEB_DEST_PATH);
		super.performDefaults();
	}

	/**
	 * Apply 保存的时候 设置相关连的功能属性静态变量即可!
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		ps.setValue(KEY_IS_OPEN_FILE, String.valueOf(button.getSelection()));
		ps.setValue(KEY_CLASS_PRE_PATH, textClassPrePath.getText());
		ps.setValue(KEY_WEB_SRC_PATH, textWebSrcPath.getText());
		ps.setValue(KEY_WEB_DEST_PATH, textWebDestPath.getText());
		return super.performOk();
	}
	
	/**
	 * 其他地方用到属性的时候统一调用此方法
	 * @param ps
	 * @return
	 */
	public static boolean isOpenFile(IPreferenceStore ps){
		String isOpenFile_  =ps.getString(KEY_IS_OPEN_FILE);
		boolean isOpenFile = (isOpenFile_ == null || isOpenFile_.length() == 0) ? DEF_IS_OPEN_FILE
				: "false".equals(isOpenFile_) ? false : true;
		return isOpenFile;
	}
	public static String classPrePath(IPreferenceStore ps){
		String targetPath_  =ps.getString(KEY_CLASS_PRE_PATH);
		String targetPath = (targetPath_==null||targetPath_.length()==0)?DEF_CLASS_PRE_PATH:targetPath_;
		return targetPath;
	} 
	public static String webSrcPath(IPreferenceStore ps){
		String Path_  =ps.getString(KEY_WEB_SRC_PATH);
		String path = (Path_==null||Path_.length()==0)?DEF_WEB_SRC_PATH:Path_;
		return path;
	} 
	public static String webDestPath(IPreferenceStore ps){
		String Path_  =ps.getString(KEY_WEB_DEST_PATH);
		String path = (Path_==null||Path_.length()==0)?DEF_WEB_DEST_PATH:Path_;
		return path;
	} 
}
