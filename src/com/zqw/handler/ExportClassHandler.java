package com.zqw.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.Openable;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.Workbench;

import com.zqw.Activator;
import com.zqw.ExportClassPage;
import com.zqw.FileUtil;

public class ExportClassHandler extends AbstractHandler {
	private List<String> classPaths;
	private List<String> filePaths;
	
	// java编译目录
	private String BIN_PATH;
	private String SRC_PATH;
	//项目名
	private String PROJECT_PATH;
	// 属性页配置的目标相对路径
	private static  String TARGET_PATH;
	// 拷贝class文件的目的地绝对路径
	private static  String DEST_PATH;
	private static boolean IS_OPEN_FILE;
	private static String OPEN_TARGET_PATH;
	// 拷贝文件 绝对路径
	private static  String WEB_SRC_PATH;
	private static  String WEB_DEST_PATH;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ILog log = Activator.getDefault().getLog();
		log.log(new Status(IStatus.OK, Activator.PLUGIN_ID,"PLUGIN_ID is  ExportClassHandler...."));
		
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		IPreferenceStore ps = Activator.getDefault().getPreferenceStore();
		IS_OPEN_FILE = ExportClassPage.isOpenFile(ps);
		String webDestPath = ExportClassPage.webDestPath(ps);
		String classPrePath = ExportClassPage.classPrePath(ps);
		classPaths=new ArrayList<String>();
		filePaths=new ArrayList<String>();
		try {
			IProject project  = getCurrentProject();
			IJavaProject j = JavaCore.create(project);
			PROJECT_PATH = project.getLocation().toOSString();
			DEST_PATH = PROJECT_PATH+File.separator+webDestPath+File.separator+classPrePath;
			OPEN_TARGET_PATH = PROJECT_PATH+File.separator+webDestPath;
			BIN_PATH = PROJECT_PATH+j.getOutputLocation().toOSString().replace(File.separator+project.getName(), "");
			
			WEB_SRC_PATH =PROJECT_PATH+File.separator+ExportClassPage.webSrcPath(ps);
			WEB_DEST_PATH = PROJECT_PATH+File.separator+webDestPath;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sselection = (IStructuredSelection) selection;
			if (!sselection.isEmpty()) {
				Iterator it = sselection.iterator();
				while(it.hasNext()){
					Object object =it.next();
					handlePath(object);
				}
			}
		}
		try {
			// 导出class文件
			if(classPaths.isEmpty() && filePaths.isEmpty()){
				return null;
			}
			if(!classPaths.isEmpty()){
				FileUtil.copyFile(classPaths, BIN_PATH, DEST_PATH);
			}
			if(!filePaths.isEmpty()){
				FileUtil.copyFile(filePaths, WEB_SRC_PATH, WEB_DEST_PATH);
				if(OPEN_TARGET_PATH==null||OPEN_TARGET_PATH.length()==0){
					OPEN_TARGET_PATH = WEB_DEST_PATH;
				}
			}
			
			//跳到指定目录
			if (!IS_OPEN_FILE ||OPEN_TARGET_PATH == null) {
				return null;
			}
			Runtime rt = Runtime.getRuntime();
			rt.exec("cmd /c start " + "file:///" + FileUtil.handleSpace(OPEN_TARGET_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}


//	private String targetFirstDir(){
//		StringBuilder sb = new StringBuilder();
//		String[] dirs = TARGET_PATH.split("\\\\");
//		if(dirs.length>0){
//			sb.append(dirs[0]);
//			if(dirs[0]==null||dirs[0].length()==0){
//				if(dirs.length>1){
//					sb.append(File.separator).append(dirs[1]);		
//				}
//			}
//		}
//		return sb.toString();
//	}
	
	
	public  void handlePath(Object object){
		IPath path = null;
		if(object instanceof Project){// 项目不处理
		} else if (object instanceof IJavaProject) { // 项目不处理
//			path = ((IJavaProject)object).getResource().getLocation();
		} else if (object instanceof IJavaElement) { // 处理class
			if(object instanceof Openable){
				SRC_PATH = ((Openable)object).getPackageFragmentRoot().getResource().getLocation().toOSString();
			}
			path = ((IJavaElement)object).getResource().getLocation();
		}else if(object instanceof IResource){
			path = ((IResource)object).getLocation();
		}
		if (path != null) {
			File objFile = path.toFile();
			dirFiles(objFile);
		}
	}
	
	/**
	 * 递归获取 class的相对路径
	 * @param file
	 */
	private void dirFiles(File file) {
		File[] tempList = null;
		if (!file.isDirectory()) {
			tempList = new File[] { file };
		} else {
			tempList = file.listFiles();
		}
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				String filePath = tempList[i].getPath();
				if (isClass(filePath)) { // 判断是否为.java后缀的文件
					if(!classPaths.contains(filePath)){
						classPaths.add(filePath.replace(SRC_PATH, "").replace(CompilationUnit.SUFFIX_STRING_java, CompilationUnit.SUFFIX_STRING_class));
					}
				}
				if (isFile(filePath)) {// 文件拷贝 包括jsp 和properties
					if(!filePaths.contains(filePath)){
						filePaths.add(filePath.replace(WEB_SRC_PATH, ""));
					}
				}
			}
			if (tempList[i].isDirectory()) {
				dirFiles(tempList[i]);
			}
		}
	}
	
	private boolean isClass(String filePah){
		return filePah.endsWith(".java")?true:false;
	}
	private boolean isFile(String filePah){
		System.out.println("");
		return filePah.startsWith(WEB_SRC_PATH)?true:false;
	}
	public static String getProjectPath() {
		String path = null;
		path = Platform.getLocation().toString();
		return path;
	}
	
	public static IProject getCurrentProject() throws JavaModelException {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		IProject project = null;
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IResource) {
				project = ((IResource) element).getProject();
			} else if (element instanceof PackageFragmentRootContainer) {
				IJavaProject jProject = ((PackageFragmentRootContainer) element).getJavaProject();
				project = jProject.getProject();
			} else if (element instanceof IJavaElement) {
				IJavaProject jProject = ((IJavaElement) element).getJavaProject();
				project = jProject.getProject();
			}
		}
		return project;
	}
	
}
